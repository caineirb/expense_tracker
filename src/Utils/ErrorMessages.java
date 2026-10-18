package Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ErrorMessages {
    public static void printError(Class<?> c, Throwable throwable, String additionalMsg) {
        // Prevent NullPointerException if class or throwable is missing
        if (c == null || throwable == null) {
            return;
        }

        Logger logger = Logger.getLogger(c.getName());

        String Msg = (additionalMsg != null) ? additionalMsg.trim() + " "
                : "An application error occurred on `" + c.getName() + "`: ";
        logger.log(Level.SEVERE, Msg, throwable);

        String traceString = getStackTrace(throwable);
        System.err.println(traceString);
    }

    public static void printError(Class<?> c, Throwable throwable) {
        ErrorMessages.printError(c, throwable, null);
    }

    public static String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);

        return stringWriter.toString();
    }
}
