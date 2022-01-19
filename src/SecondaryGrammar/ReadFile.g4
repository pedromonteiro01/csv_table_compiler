grammar ReadFile;
// 1st line contains attributes -> header
// nmec, name, nota1, nota2
// val1, val2, val3, val4
@header{
package SecondaryGrammar;
import Files.*;
}

file: line line* EOF;
line: field (SEP field)* '\r' ? '\n';
field: TEXT | STRING| ;
SEP: [ \t]* ',' [ \t]*; // ( ’ ’ | ’ \ t ’ )*
STRING: [ \t]* '"'.*? '"' [ \t]*;
TEXT: ~ [,"\r\n] ~ [,\r \n]*;