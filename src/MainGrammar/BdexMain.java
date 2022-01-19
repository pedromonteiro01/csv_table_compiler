package MainGrammar;

import Files.*;
import java.io.IOException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.stringtemplate.v4.*;


public class BdexMain {
   public static void main(String[] args) {
      try {
         // create a CharStream that reads from standard input:
         CharStream input = CharStreams.fromStream(System.in);
         // create a lexer that feeds off of input CharStream:
         BdexLexer lexer = new BdexLexer(input);
         // create a buffer of tokens pulled from the lexer:
         CommonTokenStream tokens = new CommonTokenStream(lexer);
         // create a parser that feeds off the tokens buffer:
         BdexParser parser = new BdexParser(tokens);
         // replace error listener:
         //parser.removeErrorListeners(); // remove ConsoleErrorListener
         //parser.addErrorListener(new ErrorHandlingListener());
         // begin parsing at main rule:
         ParseTree tree = parser.main();
         if (parser.getNumberOfSyntaxErrors() == 0) {
            // print LISP-style tree:
            // System.out.println(tree.toStringTree(parser));
            Compiler compiler = new Compiler();
            BdexSemanticCheck semanticCheck = new BdexSemanticCheck();
            semanticCheck.visit(tree);
            if (!ErrorHandling.error()){
               String target = "MainGrammar/java";
               if (!compiler.validTarget(target))
                  {
                     System.err.println("ERROR: template group file for target "+target+" not found!");
                     System.exit(2);
                  }
               compiler.setTarget(target);
               ST code = compiler.visit(tree);
               code.add("name", "Output");
               System.out.println(code.render());
            }
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
   }
}
