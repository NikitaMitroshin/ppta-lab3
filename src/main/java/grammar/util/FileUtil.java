package grammar.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<String> readFile(String filename) throws FileNotFoundException {
        List<String> strings = new ArrayList<>();

        File file = new File(filename);

        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
        String s;
        try {
            while ((s = reader.readLine()) != null)
                strings.add(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }
}
