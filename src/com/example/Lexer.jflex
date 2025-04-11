package com.example;

import java_cup.runtime.*;

%%

%class Lexer
%cup
%line
%column
%unicode
%{
    // For better error reporting
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

// Define commonly used patterns
Number = [0-9]+
Whitespace = [ \t\r\n\f]

%%

{Number}        { return symbol(sym.NUMBER, Integer.valueOf(yytext())); }
"+"             { return symbol(sym.PLUS); }
"-"             { return symbol(sym.MINUS); }
"*"             { return symbol(sym.TIMES); }
"/"             { return symbol(sym.DIVIDE); }
"("             { return symbol(sym.LPAREN); }
")"             { return symbol(sym.RPAREN); }
{Whitespace}+   { /* ignore whitespace */ }
.               { throw new Error("Illegal character '" + yytext() + "' at line " + (yyline+1) + ", column " + (yycolumn+1)); }
<<EOF>>         { return symbol(sym.EOF); }