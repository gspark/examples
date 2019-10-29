package com.shrill;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileMapTest {

    private final TreeMap<String, Integer> users = new TreeMap<>();

//    private final static long limit = 200000;
    private final static long limit = 150000;

    private final static String LS = System.getProperty("line.separator");

    private String temp = "temp";

    private boolean isBigFile = false;

    public FileMapTest() {

    }

    public void put(String inFileName, String outFileName) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFileName)));
        String line = null;

        while ((line = reader.readLine()) != null) {
            if (users.size() >= limit || (users.isEmpty() && isBigFile)) {
                line = reader.readLine();
                if (null != line && !line.isEmpty()) {
                    putLargerUser(line, outFileName);
                }
            }
            putUser(line);
        }
    }

    private void putUser(String line) {
        String userNo = line.trim();
        if (users.containsKey(userNo)) {
            users.replace(userNo, users.get(userNo) + 1);
        } else {
//            System.out.println("Users size" + users.size());
            users.put(userNo, 1);
        }
    }

    private void putLargerUser(String line, String outFileName) throws Exception {
        if (users.size() >= limit || (users.isEmpty() && isBigFile)) {
            isBigFile = true;
            outLargeFile(outFileName, line);
            users.clear();
            changeFile(outFileName);

        }
        putUser(line);
    }

    private void changeFile(String outfileName) {
        String outfileName1 = outfileName + "temp";

        File file = new File(outfileName1);
        file.delete();

        file = new File(outfileName + "temp2");
        file.renameTo(new File(outfileName1));
    }

    public void putLarge(String inFileName, String outFileName) throws Exception {
        FileChannel fc = null;

        try {
            fc = FileChannel.open(Paths.get(inFileName));
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            int currentReadPos = 0;
            if (mbb != null) {
                ByteBuffer bb = ByteBuffer.allocate(1024);
                boolean sep = false;
                while (currentReadPos < fc.size()) {
                    byte b = mbb.get();
                    currentReadPos++;
                    if (LS.equals("\r\n") && b == 13) {
                        mbb.get();
                        currentReadPos++;
                        sep = true;
                    } else if (b == 10 || b == 13) {
                        sep = true;
                    } else {
                        bb.put(b);
                        sep = false;
                    }
                    if (sep) {
                        String userNo = new String(bb.array(), Charset.defaultCharset());
                        bb.clear();
                        bb.position(0);

                        //
                        putLargerUser(userNo, outFileName);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private String rightTrim(String s) {
        char[] cs = s.toCharArray();
        int pos = 0;
        for (int i = cs.length - 1; i >= 0; i--) {
            String tostr = String.valueOf(cs[i]);
            if (tostr.trim().length() != 0) {
                pos = i;
                break;
            }
        }
        return s.substring(0, pos + 1);
    }

    public void outSimpleFile(String outFileName) throws IOException {
        FileWriter fw = new FileWriter(outFileName);
        BufferedWriter bw = new BufferedWriter(fw);
        StringBuilder sb = new StringBuilder();
        int ln = 1;
//        System.out.println(users.size());
        for (Map.Entry<String, Integer> entry : users.entrySet()) {
            if (entry.getValue().equals(1)) {
                continue;
            }
            sb
//                .append(ln).append('\t')
                .append(entry.getKey()).append('\t')
                .append(entry.getValue()).append(LS);
//            System.out.println(sb);
            bw.write(sb.toString());
            ln++;
            sb.delete(0, sb.length());
        }
        bw.write(sb.toString());
        bw.close();
        fw.close();
    }

    public void outLargeFile(String outfileName, String line) throws Exception {
        outfileName = outfileName + "temp";
        File file = new File(outfileName);
        if (file.length() > 0) {
            updateUsers(outfileName, line);
        } else {
            if (!file.exists()) {
                file.createNewFile();
            }
            putTempFile(outfileName);
            outLargeFile(outfileName, line);
        }
    }

    private void putTempFile(String outfileName) throws IOException {
        Path path = Paths.get(outfileName);
        StringBuilder sb = new StringBuilder();
        long offet = 0;
        try (FileChannel fileChannel = (FileChannel)Files
            .newByteChannel(path, EnumSet.of(
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING))) {
            for (Map.Entry<String, Integer> entry : users.entrySet()) {
                sb.append(entry.getKey());
                sb.append(",").append(entry.getValue()).append(LS);
                int sizeBy = sb.length();

                ByteBuffer buf = StandardCharsets.UTF_8.encode(sb.toString());
                sb.delete(0, sb.length());

                MappedByteBuffer mappedByteBuffer =
                    fileChannel.map(FileChannel.MapMode.READ_WRITE, offet, sizeBy);
                mappedByteBuffer.put(buf);
                buf.position(0);

                offet += sizeBy;
            }
        }
    }

    public void updateUsers(String outFileName, String line) throws Exception {
        String userNo = line.trim();

        Path path = Paths.get(outFileName);
        try (FileChannel fc = (FileChannel)Files
            .newByteChannel(path, EnumSet.of(
                StandardOpenOption.READ,
                StandardOpenOption.WRITE))) {

            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            int currentReadPos = 0;
            if (mbb != null) {
                ByteBuffer bb = ByteBuffer.allocate(1024);
                boolean sep = false;
                while (currentReadPos < fc.size()) {
                    byte b = mbb.get();
                    currentReadPos++;
                    if (LS.equals("\r\n") && b == 13) {
                        mbb.get();
                        currentReadPos++;
                        sep = true;
                    } else if (b == 10 || b == 13) {
                        sep = true;
                    } else {
                        bb.put(b);
                        sep = false;
                    }
                    if (sep) {
                        String userS = new String(bb.array(), Charset.defaultCharset());
                        String[] user = userS.trim().split(",");
                        String userNo0 = user[0];
                        int count = Integer.parseInt(user[1]);

                        File file = new File(outFileName + "temp2");
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        long offset = 0;
                        Path path2 = Paths.get(outFileName + "temp2");

                        try (FileChannel fileChannel = (FileChannel)Files
                            .newByteChannel(path, EnumSet.of(
                                StandardOpenOption.READ,
                                StandardOpenOption.WRITE,
                                StandardOpenOption.TRUNCATE_EXISTING))) {

                            int compare = userNo0.compareTo(userNo);
                            if (compare == 0) {
                                count++;
                                offset = offset + putTemp2(fileChannel, userNo0, count, offset);
                            } else if (compare < 0) {
                                offset = offset + putTemp2(fileChannel, userNo0, 1, offset);
                                offset = offset + putTemp2(fileChannel, userNo, 1, offset);
                            } else {
                                offset = offset + putTemp2(fileChannel, userNo, 1, offset);
                                offset = offset + putTemp2(fileChannel, userNo0, 1, offset);
                            }
                        }
                    }
                }
            }
        }
    }

    private int putTemp2(FileChannel fileChannel, String userNo, int count, long offset) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(userNo);
        sb.append(",").append(count).append(LS);
        int sizeBy = sb.length();
        ByteBuffer buf = StandardCharsets.UTF_8.encode(sb.toString());
        sb.delete(0, sb.length());

        MappedByteBuffer mappedByteBuffer =
            fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, sizeBy);
        mappedByteBuffer.put(buf);
        return sizeBy;
    }

    /**
     * @param fileName
     * @return 单位 M
     */
    public static double getFileSize(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            double bytes = file.length();
            double kilobytes = (bytes / 1024);
            double megabytes = (kilobytes / 1024);
            return megabytes;
        }
        return 0;
    }

    /**
     * B方法追加文件：使用FileWriter
     */
    public static void appendMethodB(String intfileName, String outfileName) throws IOException {
        Path inPath = Paths.get(intfileName);
        CharBuffer charBuffer = null;
        long sizeBy = 0;
        try (FileChannel fileChannel = FileChannel.open(inPath)) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            if (mappedByteBuffer != null) {
                charBuffer = StandardCharsets.UTF_8.decode(mappedByteBuffer);
                sizeBy = fileChannel.size();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final int loop = 100000;
        ByteBuffer buf = StandardCharsets.UTF_8.encode(charBuffer);
        Path path = Paths.get(outfileName);
        try (FileChannel fileChannel = (FileChannel)Files
            .newByteChannel(path, EnumSet.of(
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING))) {
            for (int i = 0; i < loop; ++i) {
                MappedByteBuffer mappedByteBuffer =
                    fileChannel.map(FileChannel.MapMode.READ_WRITE, i * sizeBy, sizeBy);
                mappedByteBuffer.put(buf);
                buf.position(0);
            }
        }
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ) {
            int number = random.nextInt(base.length());
            if (i == 0 && base.charAt(number) == '0') {
                i = 0;
                continue;
            }
            sb.append(base.charAt(number));
            ++i;
        }
        return sb.toString();
    }

    public static void appendMethodC(String outfileName) throws IOException {

        final String ln = System.getProperty("line.separator");
        final int loop = 1000000;

        Path path = Paths.get(outfileName);
        try (FileChannel fileChannel = (FileChannel)Files
            .newByteChannel(path, EnumSet.of(
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING))) {
            for (int i = 0; i < loop; ++i) {
                String userNo = getRandomString(20);
                userNo = userNo + ln;
                ByteBuffer buf = StandardCharsets.UTF_8.encode(userNo);
                MappedByteBuffer mappedByteBuffer =
                    fileChannel.map(FileChannel.MapMode.READ_WRITE, i * userNo.length(), userNo.length());
                mappedByteBuffer.put(buf);
                buf.position(0);
            }
        }
    }

    public static long getFileLine(String infileName) throws IOException {
        File test = new File(infileName);
        long fileLength = test.length();
        LineNumberReader rf = null;
        long lines = 0;
        try {
            rf = new LineNumberReader(new FileReader(test));
            if (rf != null) {
                rf.skip(fileLength);
                lines = rf.getLineNumber();
                rf.close();
            }
        } catch (IOException e) {
            if (rf != null) {
                try {
                    rf.close();
                } catch (IOException ee) {
                }
            }
        }
        return lines;
    }

    public static void main(String[] args) throws Exception {
        long starttime = System.currentTimeMillis();

//        appendMethodB(args[0], "z:\\TEMP\\customer222.txt");
        doFile(args);
//        appendMethodC("z:\\TEMP\\customer333.txt");
        long endtime = System.currentTimeMillis();

        System.out.println("total time = " + (endtime - starttime));
    }

    private static void doFile(String[] args)  {
        try {
            long fileLine = getFileLine(args[0]);
            FileMapTest fileMapTest = new FileMapTest();
            if (fileLine <= limit) {
                fileMapTest.put(args[0], args[1]);
                fileMapTest.outSimpleFile(args[1]);
            } else {
                fileMapTest.putLarge(args[0], args[1]);
                fileMapTest.outSimpleFile(args[1]);
            }
        } catch (Exception ee) {
            File test = new File(args[1]);
            if (!test.exists()) {
                try {
                    test.createNewFile();
                } catch (IOException e) {

                }
            }
        }
    }
}
