package client.model.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BSException extends Exception {
    public static String getStackTraceAsString(final Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        return sw.toString();
    }
}
