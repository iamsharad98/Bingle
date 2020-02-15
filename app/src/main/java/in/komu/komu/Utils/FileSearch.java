package in.komu.komu.Utils;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */

    public static ArrayList<String> getDirectPathArray(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);

        File[] listFile = file.listFiles();
        for(int i= 0; i<listFile.length; i++){
            if (listFile[i].isDirectory()){
                pathArray.add(listFile[i].getAbsolutePath());
            }
        }
        return  pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePathArray(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);

        File[] listFile = file.listFiles();
        for(int i= 0; i<listFile.length; i++){
            if (listFile[i].isFile()){
                pathArray.add(listFile[i].getAbsolutePath());
            }
        }
        return  pathArray;
    }
}
