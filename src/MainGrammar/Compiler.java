package MainGrammar;
import Files.*;
import java.io.File;
import org.stringtemplate.v4.*;
import org.antlr.v4.runtime.ParserRuleContext;


public class Compiler extends BdexBaseVisitor<ST> {
   
   protected String target = "MainGrammar/java";
   protected STGroup template = null;

   public boolean validTarget(String target) {
      File file = new File(target+".stg");
      return "MainGrammar/java".equalsIgnoreCase(target)  &&
             file.exists() && file.isFile() && file.canRead();
   }

   public void setTarget(String target){
      assert validTarget(target);
      this.target = target;
   }

   @Override public ST visitMain(BdexParser.MainContext ctx) {
      assert validTarget(target);
      

      template = new STGroupFile(target+".stg");

      ST res = template.getInstanceOf("module");
      res.add("stat", visit(ctx.statList()));
      return res;
   }

   @Override public ST visitStatList(BdexParser.StatListContext ctx) {
      ST res = template.getInstanceOf("stats");
      for (BdexParser.StatContext sc : ctx.stat()) {
         res.add("stat", visit(sc));
      }
      return res;
   }

   @Override public ST visitType(BdexParser.TypeContext ctx) {
      ST decl = template.getInstanceOf("decl");
      decl.add("type", ctx.res.name());
      return decl;
   }


   @Override public ST visitDeclaration(BdexParser.DeclarationContext ctx) {
      ST decl = template.getInstanceOf("decl");
      decl = visit(ctx.type());
      decl.add("var",ctx.ID().getText());
      Symbol s = new VarSymbol(ctx.ID().getText(), ctx.type().res);
      return decl;
   }

   @Override public ST visitDeclareAndAssign(BdexParser.DeclareAndAssignContext ctx) {
      ST res = template.getInstanceOf("stats");
      String id = ctx.declaration().ID().getText();
      Symbol s = BdexParser.symbolTable.get(id);
      ST dec = template.getInstanceOf("decl");
      dec.add("stat", visit(ctx.expr()).render());
      s.setVarName(id);
      dec.add("type", s.type().name());
      dec.add("var",s.varName());
      dec.add("value",ctx.expr().varName);

      return dec;
   }

   @Override public ST visitAssign(BdexParser.AssignContext ctx) {
      ST res = template.getInstanceOf("stats");
      ST decl = template.getInstanceOf("assign");
      String id = ctx.ID().getText();
      Symbol s = BdexParser.symbolTable.get(id);
      decl.add("stat", visit(ctx.expr()).render());
      s.setVarName(id);
      decl.add("var",s.varName());
      decl.add("value",ctx.expr().varName);
      res.add("stat",decl.render());
      
      return res;
   }

   @Override public ST visitPrint(BdexParser.PrintContext ctx) {
      
      if(ctx.expr().eType.name() == "Table"){
         ST res = template.getInstanceOf("printTable");
         res.add("stat", visit(ctx.expr()).render());
         res.add("table", ctx.expr().varName);
         return res;
      }
      ST res = template.getInstanceOf("print");
      res.add("stat", visit(ctx.expr()).render());
      res.add("expr", ctx.expr().varName);
      
      return res;
   }

   @Override public ST visitBooleanTypeExpr(BdexParser.BooleanTypeExprContext ctx) {
      ST decl = template.getInstanceOf("decl");
      ctx.varName = newVar();
      decl.add("type", "boolean");
      decl.add("var", ctx.varName);
      decl.add("value", ctx.BOOLEAN().getText());

      return decl;
   }

   @Override public ST visitMultDivExpr(BdexParser.MultDivExprContext ctx) {
      ctx.varName = newVar();
      return binaryExpression(ctx, visit(ctx.e1).render(), visit(ctx.e2).render(), ctx.eType.name(), ctx.varName, ctx.e1.varName, ctx.op.getText(), ctx.e2.varName);
   }

   @Override public ST visitParenthesisExpr(BdexParser.ParenthesisExprContext ctx) {
      ST res = visit(ctx.expr());
      ctx.varName = ctx.expr().varName;
      
      return res;
   }

   @Override public ST visitIntegerTypeExpr(BdexParser.IntegerTypeExprContext ctx) {
      ST decl = template.getInstanceOf("decl");
      ctx.varName = newVar();
      decl.add("type", "int");
      decl.add("var", ctx.varName);
      decl.add("value", ctx.INT().getText());

      return decl;
   }

   @Override public ST visitMaximumExpr(BdexParser.MaximumExprContext ctx) {
      ctx.varName = newVar();
      ST res = template.getInstanceOf("print");
      res.add("stat", visit(ctx.v1).render());     
      res.add("stat", visit(ctx.v2).render());
      res.add("type", ctx.eType);
      res.add("op1", ctx.v1.varName);
      res.add("op2", ctx.v2.varName);
      
      return res;
   }

   @Override public ST visitStringTypeExpr(BdexParser.StringTypeExprContext ctx) {
      ST decl = template.getInstanceOf("decl");
      ctx.varName = newVar();
      decl.add("type", "string");
      decl.add("var", ctx.varName);
      decl.add("value", ctx.STRING().getText());

      return decl;
   }

   @Override public ST visitNullTypeExpr(BdexParser.NullTypeExprContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitVarTypeExpr(BdexParser.VarTypeExprContext ctx) {
      ST decl = template.getInstanceOf("decl");
      String id = ctx.ID().getText();
      ctx.varName = newVar();
      decl.add("type", ctx.eType.name());
      decl.add("var", ctx.varName);
      decl.add("value", BdexParser.symbolTable.get(id).varName());

      return decl;
   }

   @Override public ST visitDoubleTypeExpr(BdexParser.DoubleTypeExprContext ctx) {
      ST decl = template.getInstanceOf("decl");
      ctx.varName = newVar();
      decl.add("type", "double");
      decl.add("var", ctx.varName);
      decl.add("value", ctx.DOUBLE().getText());
      
      return decl;
   }

   @Override public ST visitCompareExpr(BdexParser.CompareExprContext ctx) {
      ctx.varName = newVar();
      return binaryExpression(ctx, visit(ctx.num1).render(), visit(ctx.num2).render(), ctx.eType.name(), ctx.varName, ctx.num1.varName, ctx.op.getText(), ctx.num2.varName);

   }

   
   @Override public ST visitMinimumExpr(BdexParser.MinimumExprContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitAddSubExpr(BdexParser.AddSubExprContext ctx) {
      ctx.varName = newVar();
      return binaryExpression(ctx, visit(ctx.e1).render(), visit(ctx.e2).render(), ctx.eType.name(), ctx.varName, ctx.e1.varName, ctx.op.getText(), ctx.e2.varName);
   }

   @Override public ST visitBooleanExpr(BdexParser.BooleanExprContext ctx) {
      ctx.varName = newVar();
      return binaryExpression(ctx, visit(ctx.bool1).render(), visit(ctx.bool2).render(), ctx.eType.name(), ctx.varName, ctx.bool1.varName, ctx.op.getText(), ctx.bool2.varName);
   }

   @Override public ST visitReadFromFile(BdexParser.ReadFromFileContext ctx) {
      ST res = template.getInstanceOf("readFromFile");
      ST res2 = template.getInstanceOf("decl");
      res.add("filename", ctx.f1.getText());
      res2.add("type", "Table");
      res2.add("var", ctx.t1.getText().replace("\"", ""));
      res2.add("value", res.render());
      return res2;
   }

   @Override public ST visitWriteToFile(BdexParser.WriteToFileContext ctx) {
      ST res = template.getInstanceOf("writeToFile");
      res.add("table", ctx.s1.getText().replace("\"", ""));
      res.add("file", ctx.s2.getText());

      return res;
   }

   @Override public ST visitNewTable(BdexParser.NewTableContext ctx) {
      ST res = template.getInstanceOf("decl");
      TableType type = new TableType();
      res.add("var", ctx.t1.getText().replace("\"", "").replace(" ", ""));
      res.add("type", type.name());
      res.add("value", "new Table("+ctx.t1.getText()+")");

      return res;
   }

   @Override public ST visitAddRow(BdexParser.AddRowContext ctx) {
      ST res = template.getInstanceOf("addRow");
      String varName = newVar();
      res.add("table", ctx.t1.getText().replace("\"", ""));
      res.add("values", ctx.r1.getText());
      res.add("temp", newVar());
      res.add("var", varName);

      return res;
   }

   @Override public ST visitAddCol(BdexParser.AddColContext ctx) {
      ST res = template.getInstanceOf("addCol");
      res.add("column", ctx.c1.getText());
      String type = String.format("\"%s\"", ctx.type().res.name());
      res.add("type", type);
      res.add("table", ctx.t1.getText().replace("\"", ""));

      return res;
   }

   @Override public ST visitRemoveRowFrom(BdexParser.RemoveRowFromContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitRemoveRowByIndex(BdexParser.RemoveRowByIndexContext ctx) {
      ST res = template.getInstanceOf("removeRowByIndex");
      res.add("table", ctx.t1.getText().replace("\"", ""));
      res.add("index", ctx.index.getText());

      return res;
   }

   @Override public ST visitSetCol(BdexParser.SetColContext ctx) {
      return visitChildren(ctx);
   }

   @Override public ST visitAddValCol(BdexParser.AddValColContext ctx) {
      ST res = template.getInstanceOf("addValCol");
      res.add("table", ctx.t1.getText().replace("\"", ""));
      res.add("column", ctx.c1.getText().replace("\"", ""));
      res.add("value", ctx.val.getText());
      return res;
   }

   @Override public ST visitRemoveColFrom(BdexParser.RemoveColFromContext ctx) {
      ST res = template.getInstanceOf("removeCol");
      res.add("column", ctx.c1.getText());
      res.add("table", ctx.t1.getText().replace("\"", ""));

      return res;
   }

   @Override public ST visitForCycle(BdexParser.ForCycleContext ctx){
      ST res = template.getInstanceOf("for");
      res.add("var", ctx.ID().getText());
      res.add("start", ctx.start.getText());
      res.add("end", ctx.start.getText());
      res.add("op", "<");
      res.add("content", visit(ctx.statList()).render());

      return res;

   }

   @Override public ST visitGetColFrom(BdexParser.GetColFromContext ctx) {
      ST res = template.getInstanceOf("decl");
      ST res1 = template.getInstanceOf("getCol");
      res1.add("table", ctx.t1.getText().replace("\"", ""));
      res1.add("column", ctx.c1.getText());
      res.add("type", "column");
      ctx.varName = newVar();
      res.add("var", ctx.varName);
      res.add("value", res1.render());

      return res;
   }

   @Override public ST visitGetRowFrom(BdexParser.GetRowFromContext ctx) {
      ST res = template.getInstanceOf("getRowFromIndex");
      res.add("table", ctx.t1.getText().replace("\"", ""));
      res.add("index", Integer.parseInt(ctx.index.getText()));
      
      return res;
   }

   @Override public ST visitAppendTable(BdexParser.AppendTableContext ctx) {
      ST res = template.getInstanceOf("decl");
      ST res1 = template.getInstanceOf("appendTable");
      res1.add("table1", ctx.t1.getText().replace("\"", ""));
      res1.add("table2", ctx.t2.getText().replace("\"", ""));
      res.add("type", "Table");
      ctx.varName = newVar();
      res.add("var", ctx.varName);
      res.add("value", res1.render());

      return res;
   }

   @Override public ST visitGetRowsNumber(BdexParser.GetRowsNumberContext ctx){ 
      ST res = template.getInstanceOf("decl");
      ST res1 = template.getInstanceOf("getRowNum");
      res1.add("table1", ctx.t1.getText().replace("\"", ""));
      res.add("type", "int");
      ctx.varName = newVar();
      res.add("var", ctx.varName);
      res.add("value", res1.render());

      return res;
   }

   @Override public ST visitGetColsNumber(BdexParser.GetColsNumberContext ctx) {
      ST res = template.getInstanceOf("decl");
      ST res1 = template.getInstanceOf("getColNum");
      res1.add("table1", ctx.t1.getText().replace("\"", ""));
      res.add("type", "int");
      ctx.varName = newVar();
      res.add("var", ctx.varName);
      res.add("value", res1.render());
      
      return res;
   }
   @Override public ST visitFilterBy(BdexParser.FilterByContext ctx){ 
      ST res = template.getInstanceOf("decl");
      ST res1 = template.getInstanceOf("filterBy");
      res1.add("table", ctx.t1.getText().replace("\"", ""));
      String operator = String.format("\"%s\"", ctx.op.getText());
      res1.add("operator", operator);
      res1.add("column",ctx.c1.getText());
      String value = String.format("\"%s\"", ctx.e1.getText());
      res1.add("value",value);
      res.add("type", "Table");
      ctx.varName = newVar();
      res.add("var", ctx.varName);
      res.add("value", res1.render());
      return res;
   }

   @Override public ST visitConditional(BdexParser.ConditionalContext ctx) {
      ST res1 = template.getInstanceOf("ifCondition");
      res1.add("stat", visit(ctx.expr()).render());
      res1.add("bool", ctx.expr().varName);
      res1.add("ifStat", visit(ctx.trueSL).render());
      if (ctx.falseSL != null)
         res1.add("elseStat", visit(ctx.falseSL).render());
      return res1;
   }

   @Override public ST visitInputExpr(BdexParser.InputExprContext ctx) {
      ST res = template.getInstanceOf("input");
      ctx.varName=newVar();
      visit(ctx.type());
      res.add("type", ctx.type().res.name());
      res.add("var", ctx.varName);
      res.add("cmd", ctx.STRING().getText());
      return res;
   }

   protected ST binaryExpression(ParserRuleContext ctx, String e1Stats, String e2Stats, String type, String var, String e1Var, String op, String e2Var) {
      ST res = template.getInstanceOf("binaryExpr");
      res.add("stat", e1Stats);
      res.add("stat", e2Stats);
      res.add("type", type);
      res.add("var", var);
      res.add("e1", e1Var);
      res.add("op", op);
      res.add("e2", e2Var);

      return res;
   }


   private int varCount = 0;
   private String newVar(){
      varCount++;
      return "v"+varCount;
   }
}
