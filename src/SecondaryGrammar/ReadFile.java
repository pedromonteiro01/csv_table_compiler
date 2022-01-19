package SecondaryGrammar;
import Files.*;
import java.util.List;

public class ReadFile extends ReadFileBaseVisitor<Table> {
   private int firstLine = 0; // se o valor for 0 significa que está na primeira linha -> header
   private Table table = new Table();

   @Override public Table visitFile(ReadFileParser.FileContext ctx) {
    Table t1 = new Table();
    for (ReadFileParser.LineContext sc : ctx.line()) {
             t1 = visit(sc);
          }
    
    return t1; 
   }
    
   @Override public Table visitLine(ReadFileParser.LineContext ctx) {
      if (firstLine == 0) {
         for (int i = 0; i < ctx.field().size(); i++) {
             table.addColumn(ctx.field(i).getText(), "string");
         }
     }
     else {
         List<String> temp = table.getColumnNames();
         for (int i = 0; i < ctx.field().size(); i++) {
             table.addToColumn(temp.get(i), ctx.field(i).getText().replace('"', ' ').trim());
             //System.out.print(temp.get(i).replace('"', ' ')+": "+ctx.field(i).getText().replace('"', ' ').trim()+" ");
         }
         
     }
     firstLine ++;
     return table;
   }
}
