package util;

import java.io.IOException;

/**
 * Created by jvdur on 11/01/2016.
 */
public class CmdTools {

    public static void executeSH(String relativePath, String fileName, String arg1, String arg2, String arg3) {


        try {
            ProcessBuilder pb = new ProcessBuilder((relativePath+'/'+fileName), arg1, arg2, arg3);
            Process p = null;
            p = pb.start();

            p.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
