package edu.mit.compilers.tools;

import java.security.InvalidAlgorithmParameterException;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;

public class Logger {
    HashSet<String> modes = new HashSet<>(List.of(
            "RegularParse"
    ));
    String curMode = "";
    private Logger() {}

    private static Logger instance = new Logger();

    public static Logger getInstance() {
        return instance;
    }

    public void printf(String mode, String fmt, Object ...args) {
        if (mode.equals(curMode)) {
            System.out.printf(fmt, args);
            System.out.println("");
        }
    }

    public void setMode(String mode) throws InvalidAlgorithmParameterException {
        if (!modes.contains(mode)) {
            return;
        }
        curMode = mode;
    }
}
