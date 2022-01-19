package SecondaryGrammar;
import Files.*;
import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class ReadFileMain {
   public static Table ImportFile(String filename) {
      Table t1 = new Table();
      try {
         File f1 = new File(filename);
         FileInputStream fis = new FileInputStream(f1);
         CharStream input = CharStreams.fromStream(fis);
            // create a lexer that feeds off of input CharStream:
            ReadFileLexer lexer = new ReadFileLexer(input);
         CommonTokenStream tokens = new CommonTokenStream(lexer);
         // create a parser that feeds off the tokens buffer:
         ReadFileParser parser = new ReadFileParser(tokens);
         // replace error listener:
         //parser.removeErrorListeners(); // remove ConsoleErrorListener
         //parser.addErrorListener(new ErrorHandlingListener());
         // begin parsing at file rule:
         ParseTree tree = parser.file();
         if (parser.getNumberOfSyntaxErrors() == 0) {
            // print LISP-style tree:
            // System.out.println(tree.toStringTree(parser));
            ReadFile visitor0 = new ReadFile();
            t1= visitor0.visit(tree);
         }
      }
      catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
      catch(RecognitionException e) {
         e.printStackTrace();
         System.exit(1);
      }
      return t1;
   }
   public static Table WriteFile(Table t1) {
      Table outputTable = new Table();
      try {
         String initialString = t1.toString();
         InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
         CharStream input = CharStreams.fromStream(targetStream);
         // create a lexer that feeds off of input CharStream:
         ReadFileLexer lexer = new ReadFileLexer(input);
         CommonTokenStream tokens = new CommonTokenStream(lexer);
         // create a parser that feeds off the tokens buffer:
         ReadFileParser parser = new ReadFileParser(tokens);
         // replace error listener:
         //parser.removeErrorListeners(); // remove ConsoleErrorListener
         //parser.addErrorListener(new ErrorHandlingListener());
         // begin parsing at file rule:
         ParseTree tree = parser.file();
         if (parser.getNumberOfSyntaxErrors() == 0) {
            // print LISP-style tree:
            // System.out.println(tree.toStringTree(parser));
            ReadFile visitor0 = new ReadFile();
           outputTable = visitor0.visit(tree);
         }
      }
      catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
      catch(RecognitionException e) {
         e.printStackTrace();
         System.exit(1);
      }
      return outputTable;
   }
}
