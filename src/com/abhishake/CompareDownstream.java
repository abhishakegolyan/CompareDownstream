package com.abhishake;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CompareDownstream {

    public static void main(String[] args) throws IOException {
        Integer failCount = -1;
        ReadConfigProperties configProperties = new ReadConfigProperties();

        CompareLargeFile compareLargeFile = new CompareLargeFile();
        compareLargeFile.failcountmax = configProperties.getFailcountmax();
        compareLargeFile.readbufferlines = configProperties.getReadbufferlines();

        GenerateResultLog compareIndividualFileResultLog = new GenerateResultLog();

        String csvFile = configProperties.getCompareSoucefilenames();
        String line = "";
        String cvsSplitBy = ",";

        BufferedReader br = new BufferedReader(new FileReader(csvFile));

        line = br.readLine();

        while ((line = br.readLine()) != null) {

            // use comma as separator
            String[] compareConfig = line.split(cvsSplitBy);

            compareLargeFile.fileLocation1 = compareConfig[0];
            compareLargeFile.fileLocation2 = compareConfig[1];

            compareIndividualFileResultLog.generateResultHeaderData = "Compare File";
            compareIndividualFileResultLog.generateResultLogFileName = compareConfig[2];
            compareIndividualFileResultLog.generateResultLogData = String.valueOf(new Timestamp(System.currentTimeMillis()));
            compareIndividualFileResultLog.writeResultLog(compareIndividualFileResultLog.generateResultLogData);

            compareLargeFile.generateResultLogFileName = compareIndividualFileResultLog.generateResultLogFileName;
            failCount = compareLargeFile.whenStreamingThroughAFile_thenCorrect();

            compareIndividualFileResultLog.writeResultLog("FailCount : " + failCount);

            GenerateResultLog generateAllFileSummaryResultLog = new GenerateResultLog();
            generateAllFileSummaryResultLog.generateResultHeaderData = "File1,File2,FailCount,logfilename,timestamp";
            generateAllFileSummaryResultLog.generateResultLogFileName = configProperties.getComparelogfilename();
            generateAllFileSummaryResultLog.generateResultLogData = compareLargeFile.fileLocation1
                    + "," + compareLargeFile.fileLocation2
                    + "," + failCount
                    + "," + compareIndividualFileResultLog.generateResultLogFileName
                    + "," + String.valueOf(new Timestamp(System.currentTimeMillis()));

            generateAllFileSummaryResultLog.writeResultLog(generateAllFileSummaryResultLog.generateResultLogData);
        }
    }


    public static class CompareLargeFile {

        String fileLocation1;
        String fileLocation2;
        int failcountmax; // Story comparison if maximum failcount is reached
        int readbufferlines; // Number of linesSet1 to read
        boolean endofFileflag;

        String generateResultLogFileName;
        GenerateResultLog compareIndividualFileResultLog = new GenerateResultLog();


        public final int whenStreamingThroughAFile_thenCorrect() throws IOException {

            int failcount = 0;

            logMemory();

            compareIndividualFileResultLog.generateResultLogFileName = generateResultLogFileName;
            compareIndividualFileResultLog.writeResultLog("------------Start-----------------");

            //////////////
            File filePath1 = new File(fileLocation1);
            File filePath2 = new File(fileLocation2);

            BufferedReader br1 = new BufferedReader(new FileReader(filePath1));
            BufferedReader br2 = new BufferedReader(new FileReader(filePath2));

            List<String> linesSet1;
            List<String> linesSet2;
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            compareIndividualFileResultLog.writeResultLog(String.valueOf(timestamp));
            double bytes = filePath1.length();
            double kilobytes = (bytes / 1024);
            double megabytes = (kilobytes / 1024);
            double gigabytes = (megabytes / 1024);
            double terabytes = (gigabytes / 1024);


            compareIndividualFileResultLog.writeResultLog("------------File 1 Info-----------------");
            compareIndividualFileResultLog.writeResultLog("Fil-1 Size :" + kilobytes + "kb/" + megabytes + "mb");
            compareIndividualFileResultLog.writeResultLog(System.lineSeparator());

            compareIndividualFileResultLog.writeResultLog("------------File 2 Info-----------------");
            bytes = filePath2.length();
            kilobytes = (bytes / 1024);
            megabytes = (kilobytes / 1024);
            compareIndividualFileResultLog.writeResultLog("Fil-2 Size :" + kilobytes + "kb/" + megabytes + "mb");
            compareIndividualFileResultLog.writeResultLog(System.lineSeparator());

//        double petabytes = (terabytes / 1024);
//        double exabytes = (petabytes / 1024);
//        double zettabytes = (exabytes / 1024);
//        double yottabytes = (zettabytes / 1024);

//            System.out.println("bytes : " + bytes);
//            System.out.println("kilobytes : " + kilobytes);
//            System.out.println("megabytes : " + megabytes);
//            System.out.println("gigabytes : " + gigabytes);
//            System.out.println("terabytes : " + terabytes);
//        System.out.println("petabytes : " + petabytes);
//        System.out.println("exabytes : " + exabytes);
//        System.out.println("zettabytes : " + zettabytes);
//        System.out.println("yottabytes : " + yottabytes);
            long linenumber = 0;
            if (filePath1.hashCode() == filePath2.hashCode()) {
                compareIndividualFileResultLog.writeResultLog(String.valueOf(filePath1.hashCode()));
            }
            do {
                if (failcount == failcountmax) {
                    compareIndividualFileResultLog.writeResultLog("Max Fail count is reached terminating comparison");
                    break;
                }
                ++linenumber;
                linesSet1 = readNLines(br1, readbufferlines);
                boolean file1endofFileflag1 = endofFileflag;
                linesSet2 = readNLines(br2, readbufferlines);
                boolean file1endofFileflag2 = endofFileflag;
                if (file1endofFileflag1 == file1endofFileflag2 && file1endofFileflag1 == true) {
                    compareIndividualFileResultLog.writeResultLog("-------------------File comparision completed--------------");
                    break;
                }

                if (!method2(linesSet1, linesSet2)) {
                    ++failcount;
                    compareIndividualFileResultLog.writeResultLog("Record Miss-Match at line : " + linenumber);
                    for (int i = 0; i < readbufferlines; i++) {
                        compareIndividualFileResultLog.writeResultLog("File1:" + linesSet1.get(i));
                        compareIndividualFileResultLog.writeResultLog("File2:" + linesSet2.get(i));
                    }

                }
                if (file1endofFileflag1 == true || file1endofFileflag2 == true) {
                    compareIndividualFileResultLog.writeResultLog("-------------------File comparision Stopped as one of the file reached End of file--------------");
                    break;
                }

            } while (!linesSet1.isEmpty() || !linesSet2.isEmpty());


            timestamp = new Timestamp(System.currentTimeMillis());
            compareIndividualFileResultLog.writeResultLog(String.valueOf(timestamp));
            compareIndividualFileResultLog.writeResultLog("------------completed----------------- ");
            compareIndividualFileResultLog.writeResultLog("Record Count:" + linenumber);
            //logMemory();
            return failcount;
        }

        public boolean method2(List<String> linesSet1, List<String> linesSet2) {
            // processing...
            if (linesSet1.hashCode() == linesSet2.hashCode()) {
//        if (linesSet1.get(0).toString().equals(linesSet2.get(0).toString())) {
                return true;
            } else {
                return false;
            }
        }

        private List<String> readNLines(BufferedReader reader, int numberOfLines) throws IOException {
            List<String> lines = new ArrayList<>(numberOfLines);
            String line;
            String s = reader.readLine();
            endofFileflag = false;
            if (s == null) {
                s = "End of File Reached";
                endofFileflag = true;
            }
            while (lines.size() < numberOfLines && ((line = s) != null)) {
                lines.add(line);
            }

            return lines;
        }

        private final void logMemory() {
            compareIndividualFileResultLog.generateResultLogFileName = generateResultLogFileName;
            compareIndividualFileResultLog.writeResultLog("Max Memory: {} Mb " + Runtime.getRuntime().maxMemory() / 1048576);
            compareIndividualFileResultLog.writeResultLog("Total Memory: {} Mb " + Runtime.getRuntime().totalMemory() / 1048576);
            compareIndividualFileResultLog.writeResultLog("Free Memory: {} Mb " + Runtime.getRuntime().freeMemory() / 1048576);
        }

    }

}
