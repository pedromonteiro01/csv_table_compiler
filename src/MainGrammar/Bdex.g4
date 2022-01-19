grammar Bdex;

@header{
package MainGrammar;
import Files.*;
import java.util.*;
import SecondaryGrammar.Table;

}

@parser::members {
static protected Map<String,Symbol> symbolTable = new HashMap<>();
static protected Map<String,Table> tableNames = new HashMap<>();
}

main: statList EOF;

statList: (stat)* 
;

stat: declaration
    | assignment
    | print
    | tableOps
    | conditional 
    | forCycle
;

type returns[Type res]:
      'int'        {$res = new IntegerType();} 
    | 'double'     {$res = new DoubleType();}
    | 'Table'      {$res = new TableType();} 
    | 'boolean'    {$res = new BooleanType();} 
    | 'string'     {$res = new StringType();} 
    | 'column'     {$res = new ColumnType();} 
;

// Variable declaration
declaration: type ID;  

// Variable assignment
assignment: declaration '=' expr    #DeclareAndAssign
          | ID '=' expr             #Assign
;

print: 'print' '(' expr ')';

expr returns[String varName, Type eType]: 
             sign=('+'|'-') e=expr                                                       #UnaryTypeExpr
        |    e1=expr op=('*'|'/') e2=expr                                                #MultDivExpr
        |    e1=expr op=('+'|'-') e2=expr                                                #AddSubExpr
        |    '(' e=expr ')'                                                              #ParenthesisExpr
        |    'max' '(' v1=expr ',' v2=expr ')'                                           #MaximumExpr
        |    'min' '(' v1=expr ',' v2=expr ')'                                           #MinimumExpr
        |    num1=expr op=('=='|'!='|'<'|'>'|'<='|'>=') num2=expr                        #CompareExpr
        |    bool1=expr op=('||' | '&&') bool2=expr                                      #BooleanExpr
        |    'input''('  type ':' STRING ')'                                             #InputExpr
        |    'get' 'col' c1=STRING 'from' t1=STRING                                      #GetColFrom
        |    'get' 'row' index=INT 'from' t1=STRING                                      #GetRowFrom
        |    'append' 'table' t1=STRING 'to' t2=STRING                                   #AppendTable
        |    'get' 'row' 'num' t1=STRING                                                 #GetRowsNumber
        |    'get' 'col' 'num' t1=STRING                                                 #GetColsNumber
        |    'filter' t1=STRING 'where' c1=STRING op=('=='|'!='|'<'|'>') e1=expr         #FilterBy
        |    BOOLEAN                                                                     #BooleanTypeExpr
        |    STRING                                                                      #StringTypeExpr 
        |    INT                                                                         #IntegerTypeExpr
        |    DOUBLE                                                                      #DoubleTypeExpr
        |    ID                                                                          #VarTypeExpr
        |    'null'                                                                      #NullTypeExpr
;

tableOps:
             'read' 'table' t1=STRING 'from' f1=STRING                                   #ReadFromFile
        |    'write' s1=STRING 'to' s2=STRING                                            #WriteToFile
        |    'new' 'table' t1=STRING                                                     #NewTable
        |    'remove' 'row' 'where' e1=expr 'from' t1=STRING                             #RemoveRowFrom
        |    'remove' 'row' 'index' index=(INT|ID) 'from' t1=STRING                      #RemoveRowByIndex
        |    'add' 'row' r1=STRING 'to' t1=STRING                                        #AddRow
        |    'add' 'col' c1=STRING 'to' t1=STRING 'type' type                            #AddCol
        |    'set' 'col' c1=STRING 'from' t1=STRING 'to' e1=expr                         #SetCol
        |    'add' 'val' val=STRING 'to' 'col' c1=STRING 'in' t1=STRING                  #AddValCol
        |    'remove' 'col' c1=STRING 'from' t1=STRING                                   #RemoveColFrom
;

conditional: 'if' '(' expr ')' '{' trueSL=statList '}' ('else' '{' falseSL=statList '}')?;
forCycle: 'for' '(' ID '=' start=INT ':' end=INT ')' '{' statList '}';


STRING: ('"' (~'"')* '"');
INT: [0-9]+;
DOUBLE: [0-9]+'.'[0-9]+;
BOOLEAN: ('true' | 'false');
ID: [a-zA-Z0-9_]+;
WS: [ \t\n\r]+ -> skip;
COMMENT: '//' .*? '\n' -> skip;
MULTILINE: '/*' .*? '*/' -> skip;