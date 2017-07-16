package com.abhishake;

import java.io.*;

/**
 * Created by abhishakegolyan on 16/7/17.
 */
public class GenerateResultLog {
    String generateResultLogFileName;
    String generateResultLogData;
    boolean generateResultCreatedFirstTime = false;
    String generateResultHeaderData;

    public void writeResultLog(String data){
        final String FILENAME = generateResultLogFileName;
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            File file = new File(FILENAME);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
                generateResultCreatedFirstTime = true;
            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            if (generateResultCreatedFirstTime == true){
                bw.write(generateResultHeaderData);
                bw.newLine();
                generateResultCreatedFirstTime = false;
            }
            bw.write(data);
            bw.newLine();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }

}
