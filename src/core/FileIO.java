package core;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FileIO {
    public static final int MAX_LENGTH = Integer.MAX_VALUE;

    public static class FileTooBigException extends Exception {
        public FileTooBigException() {
            super();
        }

        public FileTooBigException(String message) {
            super(message);
        }

        public FileTooBigException(String message, Throwable cause) {
            super(message, cause);
        }

        public FileTooBigException(Throwable cause) {
            super(cause);
        }
    }

    public static String read(String filePath) throws IOException, FileTooBigException {
        return read(filePath, StandardCharsets.UTF_8);
    }

    public static String read(String filePath, Charset charset) throws IOException, FileTooBigException {
        File file = new File(filePath);
        long fileLength = file.length();
        if (fileLength >= MAX_LENGTH) {
            throw new FileTooBigException(file + " is bigger than MAX_LENGTH(" + MAX_LENGTH + "):" + fileLength);
        }
        byte[] bufByte = new byte[(int) fileLength];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bufByte);
        fis.close();
        return new String(bufByte, charset);
    }

    public static void write(String filePath, String content) throws Exception {
        write(filePath, content, StandardCharsets.UTF_8, false);
    }

    public static void write(String filePath, String content, boolean append) throws Exception {
        write(filePath, content, StandardCharsets.UTF_8, append);
    }

    public static void write(String filePath, String content, Charset charset, boolean append) throws Exception {
        if (!append) {
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = content.getBytes(charset);
            fos.write(buf);
            fos.flush();
            fos.close();
        } else {
            write(filePath, read(filePath, charset) + content, charset, false);
        }
    }

    /**
     * ?????????Url???????????????
     *
     * @param urlStr
     * @param saveName
     * @param savePath
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, String saveName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //??????????????????3???
        conn.setConnectTimeout(10 * 1000);
        //?????????????????????????????????403??????
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        conn.setRequestProperty("token", UUID.randomUUID().toString());
        //?????????
        DataInputStream dataInputStream = new DataInputStream(conn.getInputStream());
        //?????????????????????
        // ??????????????????
        File saveDir = new File(savePath.replaceAll("\\?", " "));
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File file = new File(saveDir + (saveDir.getPath().equals("")?"":File.separator) + saveName);
        FileOutputStream fos = new FileOutputStream(file);

        byte[] data = new byte[1024];
        int len = 0;
        while ((len = dataInputStream.read(data, 0, data.length)) != -1) {
            fos.write(data, 0, len);
            fos.flush();
        }
        fos.close();
        dataInputStream.close();
    }
}
