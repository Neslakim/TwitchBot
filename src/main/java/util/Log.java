package util;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log {
    String className;

    public Log(String className) {
        this.className = className;
    }

    public Log() {
    }

    static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public String getTime() {
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public void i(String str) {
        String s = String.format("[%s] [INFO   ] %s", getTime(), str);
        System.out.println(s);
        appendToFile(s);
    }

    public void d(String str) {
        String s = String.format("[%s] [DEBUG  ] %s", getTime(), str);
        System.out.println(s);
        appendToFile(s);
    }

    public void e(String str) {
        String s = String.format("[%s] [ERROR  ] %s", getTime(), str);
        System.out.println(s);
        appendToFile(s);
    }

    public void v(String str) {
        String s = String.format("[%s] [VERBOSE] %s", getTime(), str);
        System.out.println(s);
        appendToFile(s);
    }

    public void w(String str) {
        String s = String.format("[%s] [WARNING] %s", getTime(), str);
        System.out.println();
        appendToFile(s);
    }

    public void appendToFile(String s) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("log/%s.log", className), true))) {
            bw.write(s);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}