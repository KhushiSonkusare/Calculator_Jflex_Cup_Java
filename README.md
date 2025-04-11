# 📐 Java Calculator using JFlex and CUP

A simple arithmetic expression calculator implemented in **Java**, using **JFlex** for lexical analysis and **CUP** for syntax parsing. This calculator can evaluate expressions like `2 + 3 * (4 - 1)` via command-line or web-based interface.


---

## 🚀 Features

- Supports basic arithmetic:
  - Addition `+`
  - Subtraction `-`
  - Multiplication `*`
  - Division `/`
  - Parentheses `()` for grouping
    
- Error handling for:
  - Division by zero
  - Invalid syntax

- Can be used via:
  - ✅ Command-line (CLI)
  - 🌐 Web server (REST API + frontend)

---

## ⚙️ How It Works

### Lexer (JFlex)

Defined in `Lexer.flex`, it breaks input into tokens like `NUMBER`, `PLUS`, `MINUS`, etc.

```java
[0-9]+  { return new Symbol(sym.NUMBER, Integer.parseInt(yytext())); }
"+"     { return new Symbol(sym.PLUS); }
...
```
### Parser (CUP)

Defined in Parser.cup, it uses grammar rules and operator precedence to parse expressions:

```java
expression ::= expression PLUS term
             | expression MINUS term
             | term
...
```
---

## 📦 Compilation Steps

``` bash
java -cp "build;lib/*" com.example.WebCalculatorServer
```
Then open http://localhost:8080 in your browser.

### Sample Inputs and Outputs

```plaintext
Enter expression (or 'exit' to quit): 2 + 3 * 4
Result: 14

Enter expression (or 'exit' to quit): (2 + 3) * 4
Result: 20

Enter expression (or 'exit' to quit): 10 / 2 - 1
Result: 4

Enter expression (or 'exit' to quit): ((2 + 3) * (4 - 1))
Result: 15

Enter expression (or 'exit' to quit): 5 / 0
Error: / by zero

Enter expression (or 'exit' to quit): 2 + a
Error: Illegal character: a

Enter expression (or 'exit' to quit): exit
```
