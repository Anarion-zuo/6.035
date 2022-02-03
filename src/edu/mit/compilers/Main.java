package edu.mit.compilers;

import java.io.*;
import edu.mit.compilers.grammar.*;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;

class Main {
  public static void main(String[] args) {
    try {
      CLI.parse(args, new String[0]);
      InputStream inputStream = CLI.infile == null ?
          System.in : new java.io.FileInputStream(CLI.infile);
      PrintStream outputStream = CLI.outfile == null ? System.out : new java.io.PrintStream(new java.io.FileOutputStream(CLI.outfile));
      if (CLI.target == Action.SCAN) {
        DecafScanner scanner =
            new DecafScanner(new DataInputStream(inputStream));
        scanner.setTrace(CLI.debug);
        Token token;
        boolean done = false;
        while (!done) {
          try {
            for (token = scanner.nextToken();
                 !token.isEOF();
                 token = scanner.nextToken()) {
              String text = token.getText();
              outputStream.println(text);
            }
            done = true;
          } catch(Exception e) {
            // print the error
            System.err.println(CLI.infile + " " + e);
          }
        }
        System.out.println("Finished scanning");
      } else if (CLI.target == Action.PARSE ||
                 CLI.target == Action.DEFAULT) {
        DecafScanner scanner =
            new DecafScanner(new DataInputStream(inputStream));
        DecafParser parser = new DecafParser(scanner);
        parser.setTrace(CLI.debug);
        parser.program();
        if(parser.hasError()) {
          System.exit(1);
        }
          System.out.println("Finished parsing");
      }
    } catch(Exception e) {
      // print the error
      System.err.println(CLI.infile+" "+e);
    }
  }
}
