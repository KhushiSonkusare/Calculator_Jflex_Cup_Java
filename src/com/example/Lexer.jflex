package com.example;

import java_cup.runtime.*;

%%

%class Lexer
%cup
%line
%column

%%

[0-9]+          { return new Symbol(sym.NUMBER, Integer.parseInt(yytext())); }
"+"             { return new Symbol(sym.PLUS); }
"-"             { return new Symbol(sym.MINUS); }
"*"             { return new Symbol(sym.TIMES); }
"/"             { return new Symbol(sym.DIVIDE); }
"("             { return new Symbol(sym.LPAREN); }
")"             { return new Symbol(sym.RPAREN); }
[ \t\r\n]+      { /* ignore whitespace */ }
.               { System.err.println("Illegal character: " + yytext()); }
<<EOF>>         { return new Symbol(sym.EOF); }