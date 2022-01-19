package MainGrammar;
import Files.*;
import SecondaryGrammar.Table;

import static java.lang.System.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

public class BdexSemanticCheck extends BdexBaseVisitor<Boolean> {

   private final DoubleType doubleType = new DoubleType();
   private final IntegerType integerType = new IntegerType();
   private final BooleanType booleanType = new BooleanType();
   private final StringType stringType = new StringType();
   private final ColumnType columnType = new ColumnType();
   private final TableType tableType = new TableType();

   @Override
   public Boolean visitDeclaration(BdexParser.DeclarationContext ctx) {
      Boolean res = true;
      TerminalNode t = ctx.ID();
      String id = t.getText();

      if (BdexParser.symbolTable.containsKey(id)) {
         ErrorHandling.printError(ctx, "Variable \"" + id + "\" already declared!");
         res = false;
      } else
         BdexParser.symbolTable.put(id, new VarSymbol(id, ctx.type().res));

      return res;
   }

   @Override
   public Boolean visitDeclareAndAssign(BdexParser.DeclareAndAssignContext ctx) {
      Boolean res = visit(ctx.declaration());
      String id = ctx.declaration().ID().getText();
      Boolean res2 = visit(ctx.expr());
      if (res) {
         Symbol symb = BdexParser.symbolTable.get(id);
         if (res2) {
            if (!ctx.expr().eType.conformsTo(symb.type())) {
               ErrorHandling.printError(ctx, "Expression type "+ ctx.expr().eType.name() +" does not conform to variable \"" + id + "\" type!");
               res2 = false;
            } else
               symb.setValueDefined();
         }
      }

      return res && res2;
   }

   @Override
   public Boolean visitAssign(BdexParser.AssignContext ctx) {
      Boolean res = visit(ctx.expr());
      String id = ctx.ID().getText();
      if (res) {
         if (!BdexParser.symbolTable.containsKey(id)) {
            ErrorHandling.printError(ctx, "Variable \"" + id + "\" does not exist!");
            res = false;
         } else {
            Symbol sym = BdexParser.symbolTable.get(id);
            if (!ctx.expr().eType.conformsTo(sym.type())) {
               ErrorHandling.printError(ctx, "Expression type does not conform to variable \"" + id + "\" type!");
               res = false;
            } else
               sym.setValueDefined();
         }
      }

      return res;
   }

   @Override
   public Boolean visitBooleanTypeExpr(BdexParser.BooleanTypeExprContext ctx) {
      ctx.eType = booleanType;
      return true;
   }

   @Override
   public Boolean visitMultDivExpr(BdexParser.MultDivExprContext ctx) {
      Boolean res = visit(ctx.e1) && checkNumType(ctx, ctx.e1.eType) &&
      visit(ctx.e2) && checkNumType(ctx, ctx.e2.eType);
      if (res) {
         ctx.eType = fetchType(ctx.e1.eType, ctx.e2.eType);
      }
      return res;
   }

   @Override
   public Boolean visitUnaryTypeExpr(BdexParser.UnaryTypeExprContext ctx) {
      Boolean res = visit(ctx.e) && checkNumType(ctx, ctx.e.eType);
      if (res)
         ctx.eType = ctx.e.eType;
      return res;
   }

   @Override
   public Boolean visitParenthesisExpr(BdexParser.ParenthesisExprContext ctx) {
      Boolean res = visit(ctx.e);
      if (res)
         ctx.eType = ctx.e.eType;
      return res;
   }
    @Override
   public Boolean visitPrint(BdexParser.PrintContext ctx) {
      Boolean res = visit(ctx.expr());
      return res;
   }

   @Override
   public Boolean visitIntegerTypeExpr(BdexParser.IntegerTypeExprContext ctx) {
      ctx.eType = integerType;
      return true;
   }

   @Override
   public Boolean visitMaximumExpr(BdexParser.MaximumExprContext ctx) {
      // Boolean res1 = visit(ctx.v1);
      // Boolean res2 = visit(ctx.v2);
      // if(res1)
      //    ctx.eType = ctx.v1.eType;
      
      
      return visitChildren(ctx);
   }

   @Override
   public Boolean visitStringTypeExpr(BdexParser.StringTypeExprContext ctx) {
      ctx.eType = stringType;
      return true;
   }

   @Override
   public Boolean visitNullTypeExpr(BdexParser.NullTypeExprContext ctx) {
      return visitChildren(ctx);
   }

   @Override
   public Boolean visitVarTypeExpr(BdexParser.VarTypeExprContext ctx) {
      Boolean res = true;
      String id = ctx.ID().getText();
      if (!BdexParser.symbolTable.containsKey(id))
      {
         ErrorHandling.printError(ctx, "Variable \""+id+"\" does not exist!");
         res = false;
      }
      else
      {
         Symbol sym = BdexParser.symbolTable.get(id);
         if (!sym.valueDefined())
         {
            ErrorHandling.printError(ctx, "Variable \""+id+"\" not defined!");
            res = false;
         }
         else
            ctx.eType = sym.type();
      }
      return res;
   }

   @Override
   public Boolean visitDoubleTypeExpr(BdexParser.DoubleTypeExprContext ctx) {
      ctx.eType = doubleType;
      return true;   
   }

   @Override
   public Boolean visitCompareExpr(BdexParser.CompareExprContext ctx) {
      Boolean res = visit(ctx.num1) && visit(ctx.num2);
      if (res)
      {
         if (fetchType(ctx.num1.eType, ctx.num2.eType) == null)
         {
            ErrorHandling.printError(ctx, "Comparison operator applied to invalid operands!");
            res = false;
         }
         else
            ctx.eType = booleanType;
      }
      return res;
   }

   @Override
   public Boolean visitMinimumExpr(BdexParser.MinimumExprContext ctx) {
      return visitChildren(ctx);
   }

   @Override
   public Boolean visitAddSubExpr(BdexParser.AddSubExprContext ctx) {
      Boolean res = visit(ctx.e1) && checkNumType(ctx, ctx.e1.eType) && visit(ctx.e2) && checkNumType(ctx, ctx.e2.eType);
      if (res)
         ctx.eType = fetchType(ctx.e1.eType, ctx.e2.eType);
      return res;
   }

   @Override
   public Boolean visitBooleanExpr(BdexParser.BooleanExprContext ctx) {
      Boolean res = visit(ctx.bool1) && visit(ctx.bool2);
      if(res){
         res = ctx.bool1.eType.conformsTo(ctx.bool2.eType);
         if(res){
            ctx.eType = fetchType(ctx.bool1.eType, ctx.bool2.eType);
         }else{
            ErrorHandling.printError("When performing a boolean expression, both sides must produce a boolean!");
         }
      }
      return res;
   }
   @Override
   public Boolean visitForCycle(BdexParser.ForCycleContext ctx){
      Boolean res = true;
      if(res){
         res = Integer.parseInt(ctx.start.getText()) < Integer.parseInt(ctx.end.getText());
         if(!res){
            ErrorHandling.printError("Starting value must be lower than ending value!");
         }
         else{
            res = visit(ctx.statList());
         }
      }
      
      return res;
   }

   @Override
   public Boolean visitReadFromFile(BdexParser.ReadFromFileContext ctx) {
      Boolean res = BdexParser.tableNames.containsKey(ctx.t1.getText()); // check if table exists
      if(!res){
          try{
             File file1 = new File(ctx.f1.getText().replace("\"",""));
             FileInputStream fis = new FileInputStream(file1);
             String text1 = ctx.t1.getText().replace("\"","");
             BdexParser.symbolTable.put(text1, new VarSymbol(text1, tableType));
             BdexParser.symbolTable.get(text1).setValueDefined();
             BdexParser.symbolTable.get(text1).setVarName(text1);
             return res;
          }
          catch(FileNotFoundException e){
             ErrorHandling.printError("File does not exist! Please try with the correct path!");
          }
     }else{
        ErrorHandling.printError("There's already an existing table with that name!");
     }

    return res;
   }

   @Override
   public Boolean visitWriteToFile(BdexParser.WriteToFileContext ctx) {
      Boolean res=BdexParser.tableNames.containsKey(ctx.s1.getText());
      String tname = ctx.s1.getText();
      if(!res){
         ErrorHandling.printError("Table "+tname+" does not exist!");
      }
      
      return visitChildren(ctx);
   }

   @Override
   public Boolean visitNewTable(BdexParser.NewTableContext ctx) {
      Boolean res = true;
      if(ctx.t1.getText() != "" || ctx.t1.getText() != null){
         if(BdexParser.tableNames.containsKey(ctx.t1.getText())){
            ErrorHandling.printError("Table "+ctx.t1.getText()+" already exists!");
            res = false;
         }
         else {
            Table t1 = new Table(ctx.STRING().getText());
            String text1 = ctx.t1.getText().replace("\"","");
            BdexParser.tableNames.put(ctx.t1.getText(),t1);
            BdexParser.symbolTable.put(text1, new VarSymbol(text1, tableType));
            BdexParser.symbolTable.get(text1).setValueDefined();
            BdexParser.symbolTable.get(text1).setVarName(text1);

         }
      }
      return res;
   }

   @Override
   public Boolean visitAddRow(BdexParser.AddRowContext ctx) {
      Boolean res=BdexParser.tableNames.containsKey(ctx.t1.getText());
      if(res){
         Table t1 = BdexParser.tableNames.get(ctx.t1.getText());
         if(t1.getColumnNames().size()==0)
            ErrorHandling.printError("Table "+ctx.t1.getText()+" does not have any columns!");

         int size = ctx.r1.getText().split(",").length;
         res = t1.getColumnNames().size() >= size;
         if(!res){
            ErrorHandling.printError("Table "+ctx.t1.getText()+" does not have that many columns!");
         }
      }
      else{
         ErrorHandling.printError("Table "+ctx.t1.getText()+" does not exist!");
      }
      return res;
   }

   @Override
   public Boolean visitAddCol(BdexParser.AddColContext ctx) {
      Boolean res = BdexParser.tableNames.containsKey(ctx.t1.getText());
      String tName = ctx.t1.getText();
      String cName = ctx.c1.getText();
      if(res){
         Table t1 = BdexParser.tableNames.get(tName);
         if(!t1.getColumnNames().contains(cName)){
            t1.addColumn(cName, ctx.type().res.name());
         }
         else{
            res=false;
            ErrorHandling.printError("Column "+ ctx.c1.getText() +" already exists in that table!");
         }
      }else{
         ErrorHandling.printError("Table "+ctx.t1.getText()+" does not exist!");
      }
      return res;
   }

   @Override
   public Boolean visitRemoveRowFrom(BdexParser.RemoveRowFromContext ctx) {
      Boolean res=BdexParser.tableNames.containsKey(ctx.t1.getText());
      if(res){
         res= visit(ctx.e1);
      }
      else{
         ErrorHandling.printError("Table "+ctx.t1.getText()+" does not exist!");
      }
      return res;
   }

   @Override
   public Boolean visitRemoveRowByIndex(BdexParser.RemoveRowByIndexContext ctx) {
      Boolean res=BdexParser.tableNames.containsKey(ctx.t1.getText());
      if(res){
         Table t1 = BdexParser.tableNames.get(ctx.t1.getText());
         res = t1.getTableSize()>=Integer.parseInt(ctx.index.getText());
         System.out.println(t1.getTableSize());
         if(!res){
            ErrorHandling.printError("Table "+ctx.t1.getText()+" does not have such index!");
         }
      }
      else{
         ErrorHandling.printError("Table "+ctx.t1.getText()+" does not exist!");
      }
      
      return res;
   }

   @Override
   public Boolean visitSetCol(BdexParser.SetColContext ctx) {
      //Boolean res = visit(ctx.t1)
      return visitChildren(ctx);
   }

   @Override
   public Boolean visitAddValCol(BdexParser.AddValColContext ctx) {
      Boolean res = BdexParser.tableNames.containsKey(ctx.t1.getText());
      if(res){
         Table t1 = BdexParser.tableNames.get(ctx.t1.getText());
         res = t1.getColumnNames().contains(ctx.c1.getText());
         if(!res){
            ErrorHandling.printError("Column "+ ctx.c1.getText() +" does not exist in that table!");
         }
      }
      else{
         ErrorHandling.printError("Table "+ctx.t1.getText()+" does not exist!");
      }
      return res;
   }

   @Override
   public Boolean visitRemoveColFrom(BdexParser.RemoveColFromContext ctx) {
      Boolean res = BdexParser.tableNames.containsKey(ctx.t1.getText());
      if(res){
         Table t1 = BdexParser.tableNames.get(ctx.t1.getText());
         res = t1.getColumnNames().contains(ctx.c1.getText());
         if(!res){
            ErrorHandling.printError("Column "+ ctx.c1.getText() +" does not exist in that table!");
         }else{
            t1.removeColumn(ctx.c1.getText());
         }
      }
      else{
         ErrorHandling.printError("Table "+ctx.t1.getText()+" does not exist!");
      }
      return res;
   }

   @Override
   public Boolean visitGetColFrom(BdexParser.GetColFromContext ctx) {
      Boolean res = BdexParser.tableNames.containsKey(ctx.t1.getText());
      if(res){
         Table t1 = BdexParser.tableNames.get(ctx.t1.getText());
         res = t1.getColumnNames().contains(ctx.c1.getText());
         ctx.eType=columnType;
         if(!res){
            ErrorHandling.printError("Column "+ ctx.c1.getText() +" does not exist in that table!");
         }
      }
      else{
         ErrorHandling.printError("Table "+ctx.t1.getText()+" does not exist!");
      }
      return res;
   }

   @Override
   public Boolean visitGetRowFrom(BdexParser.GetRowFromContext ctx) {
      Boolean res=ctx.t1.getText()!="";
      if(res){     
      res=BdexParser.tableNames.containsKey(ctx.t1.getText());
      if(res){
         Table t1 = BdexParser.tableNames.get(ctx.t1.getText());
         res = t1.getTableSize()>=Integer.parseInt(ctx.index.getText());
         if(!res){
            ErrorHandling.printError("Table "+ctx.t1.getText()+" does not have such index!");
         }
      }
      else{
         ErrorHandling.printError("Table "+ctx.t1.getText()+" does not exist!");
      }
   }else{
      ErrorHandling.printError("Table cannot have empty name!");
   }
      return res;
 }

   @Override
   public Boolean visitAppendTable(BdexParser.AppendTableContext ctx) {
      Boolean res = BdexParser.tableNames.containsKey(ctx.t1.getText());
      if(res){
         res = BdexParser.tableNames.containsKey(ctx.t2.getText());
         if(res){
            ctx.eType=tableType;
            Table t1 = BdexParser.tableNames.get(ctx.t1.getText());
            Table t2 = BdexParser.tableNames.get(ctx.t2.getText());
            res = t1.getTableSize()>0 && t2.getTableSize()>0;
            if(!res){
               ErrorHandling.printError("Tables are empty (or one of them)!");
            }
            
         }else{
            ErrorHandling.printError("Second table does not exist!");
         }
      }
      else{
         ErrorHandling.printError("First table does not exist!");
      }
      return res;
   }

   @Override
   public Boolean visitFilterBy(BdexParser.FilterByContext ctx) {
      Boolean res = BdexParser.tableNames.containsKey(ctx.t1.getText());
      if(res){
         res = visit(ctx.e1);
         ctx.eType = tableType;
      }else{
         ErrorHandling.printError("Table "+ctx.t1.getText()+" does not exist!");
      }
      return res;
   }

   @Override public Boolean visitGetRowsNumber(BdexParser.GetRowsNumberContext ctx){ 
      Boolean res = true;
      ctx.eType=integerType;
      return res;
   }

   @Override 
   public Boolean visitGetColsNumber(BdexParser.GetColsNumberContext ctx) {
      Boolean res = true;
      ctx.eType=integerType;
      return res;
   }

   @Override
   public Boolean visitConditional(BdexParser.ConditionalContext ctx) {
      Boolean res = visit(ctx.expr());
      visit(ctx.trueSL);
      if (ctx.falseSL != null)
         visit(ctx.falseSL);
      if (res)
      {
         if (!"boolean".equals(ctx.expr().eType.name()))
         {
            ErrorHandling.printError(ctx, "Boolean expression required in conditional instruction!");
            res = false;
         }
      }
      return res;
   }
   
   @Override
   public Boolean visitInputExpr(BdexParser.InputExprContext ctx) {
      ctx.eType = ctx.type().res;
      return true;
   }

   private Boolean checkNumType(ParserRuleContext ctx, Type t) {
      Boolean res = true;
      if (!t.isNumeric()) {
         ErrorHandling.printError(ctx, "Numeric operator applied to a non-numeric operand!");
         res = false;
      }
      return res;
   }

   private Type fetchType(Type t1, Type t2)
   {
      Type res = null;
      if (t1.isNumeric() && t2.isNumeric())
      {
         if ("double".equals(t1.name()))
            res = t1;
         else if ("double".equals(t2.name()))
            res = t2;
         else
            res = t1;
      }
      else if ("boolean".equals(t1.name()) && "boolean".equals(t2.name()))
         res = t1;
      else if ("String".equals(t1.name()) && "string".equals(t2.name()));
      return res;
   }

}
