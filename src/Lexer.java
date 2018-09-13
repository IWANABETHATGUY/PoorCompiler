import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Lexer {
    class Token {
        String type;
        String content;
        public Token(String type, String content) {
            this.type = type;
            this.content = content;
        }
    }
    public static final int  EOI = 0;
    public static final int  SEMI = 1;
    public static final int  PLUS = 2;
    public static final int  TIMES = 3;
    public static final int  LP = 4;
    public static final int  RP = 5;
    public static final int  NUM_OR_ID = 6;
    public static final int  UNKNOWN_SYMBOL = 7;
    public static final int PD = 8;
    public static final int PT = 9;
    public static final int MD = 10;
    public static final int MT = 11;
    public static final int DP = 12;
    public static final int DM = 13;
    public static final int TP = 14;
    public static final int TM = 15;
    public static final int MINUS = 16;
    public static final int DIVIDE = 17;

    private int lookAhead = -1;
    
    public String yytext = "";
    public int yyleng = 0;
    public int yylineno = 0;
    private String input_buffer = "";
    private String current = "";
    
    private boolean isAlnum(char c) {
        return Character.isDigit(c);
    }
    public boolean isSkip(char ch) {
        return ch == '\n' || ch == '\t' || ch == ' ';
    }
    private int lex() {
    
    	while (true) {
    		
    		while (current == "") {
    		    Scanner s = new Scanner(System.in);
    		    while (true) {
    		    	String line = s.nextLine();
    		    	if (line.equals("end")) {
    		    		break;
    		    	}
    		    	input_buffer += line;
    		    }
    		    s.close();
    		    
    		    if (input_buffer.length() == 0) {
    		    	current = "";
    		    	return EOI;
    		    }
    		    
    		    current = input_buffer;
    		    ++yylineno;
    		    current.trim();
    		}//while (current == "")
    		
    		if (current.isEmpty()) {
    			return EOI;
    		}

            for (int i = 0; i < current.length(); i++) {

                yyleng = 0;
                yytext = current.substring(0, 1);
                switch (current.charAt(i)) {
                    case ';': current = current.substring(1); return SEMI;
                    case '(': current = current.substring(1);return LP;
                    case ')': current = current.substring(1);return RP;

                    case '\n':
                    case '\t':
                    case ' ': current = current.substring(1); i--; break;

                    case '+':
                        if (current.length() > 2 && current.charAt(i + 1) == '*' && (isSkip(current.charAt(i + 2)) || current.charAt(i + 2) == ';')) {
                            yytext = current.substring(0, i + 2);
                            current = current.substring(2);
                            return PT;
                        } else if (current.length() > 2 && current.charAt(i + 1) == '/' && (isSkip(current.charAt(i + 2)) || current.charAt(i + 2) == ';')) {
                            yytext = current.substring(0, i + 2);
                            current = current.substring(2);
                            return PD;
                        } else {
                            current = current.substring(1);
                            return PLUS;
                        }
                    case '*':
                        if (current.length() > 2 && current.charAt(i + 1) == '+' && (isSkip(current.charAt(i + 2)) || current.charAt(i + 2) == ';')) {
                            yytext = current.substring(0, i + 2);
                            current = current.substring(2);
                            return TP;
                        } else if (current.length() > 2 && current.charAt(i + 1) == '-' && (isSkip(current.charAt(i + 2)) || current.charAt(i + 2) == ';')) {
                            yytext = current.substring(0, i + 2);
                            current = current.substring(2);
                            return TM;
                        } else {
                            current = current.substring(1);
                            return TIMES;
                        }
                    case '-':
                        if (current.length() > 2 && current.charAt(i + 1) == '*' && (isSkip(current.charAt(i + 2)) || current.charAt(i + 2) == ';')) {
                            yytext = current.substring(0, i + 2);
                            current = current.substring(2);
                            return MT;
                        } else if (current.length() > 2 && current.charAt(i + 1) == '/' && (isSkip(current.charAt(i + 2)) || current.charAt(i + 2) == ';')) {
                            yytext = current.substring(0, i + 2);
                            current = current.substring(2);
                            return MD;
                        } else {
                            current = current.substring(1);
                            return MINUS;
                        }
                    case '/':
                        if (current.length() > 2 && current.charAt(i + 1) == '+' && (isSkip(current.charAt(i + 2)) || current.charAt(i + 2) == ';')) {
                            yytext = current.substring(0, i + 2);
                            current = current.substring(2);
                            return DP;
                        } else if (current.length() > 2 && current.charAt(i + 1) == '-' && (isSkip(current.charAt(i + 2)) || current.charAt(i + 2) == ';')) {
                            yytext = current.substring(0, i + 2);
                            current = current.substring(2);
                            return DM;
                        } else {
                            current = current.substring(1);
                            return DIVIDE;
                        }
                    default:
                        if (isAlnum(current.charAt(i)) == false) {
                            current = current.substring(1);
                            return UNKNOWN_SYMBOL;
                        }
                        else {

                            while (i < current.length() && isAlnum(current.charAt(i))) {
                                i++;
                                yyleng++;
                            } // while (isAlnum(current.charAt(i)))

                            yytext = current.substring(0, yyleng);
                            current = current.substring(yyleng);
                            i = -1;
                            return NUM_OR_ID;
                        }

                } //switch (current.charAt(i))
            }//  for (int i = 0; i < current.length(); i++)
    		
    	}//while (true)	
    }//lex()
    
    public boolean match(int token) {
    	if (lookAhead == -1) {
    		lookAhead = lex();
    	}
    	
    	return token == lookAhead;
    }
    
    public void advance() {
    	lookAhead = lex();
    }
    
    public  List<List<Token>> getTokenMap() {
        List<List<Token>> tokenMap = new ArrayList<>();
    	while (!match(EOI)) {
            List<Token> tokens = new ArrayList<>();
    	    while (true) {
    	        tokens.add(new Token(token(), yytext));
                if (yytext.equals(";")) {
                    break;
                }
    	        advance();

            }
    		tokenMap.add(tokens);
    	    advance();
    	}
    	return tokenMap;
    }

    private String token() {
        String token = "";
        switch (lookAhead) {
            case EOI:
                token = "EOI";
                break;
            case PLUS:
                token = "PLUS";
                break;
            case DIVIDE:
                token = "DIVIDE";
                break;
            case MINUS:
                token = "MINUS";
                break;
            case TIMES:
                token = "TIMES";
                break;
            case NUM_OR_ID:
                token = "NUM_OR_ID";
                break;
            case SEMI:
                token = "SEMI";
                break;
            case LP:
                token = "LP";
                break;
            case RP:
                token = "RP";
                break;
            case PD:
                token = "PD";
                break;
            case PT:
                token = "PT";
                break;
            case MD :
                token = "MD";
                break;
            case MT :
                token = "MT";
                break;
            case DP :
                token = "DP";
                break;
            case DM :
                token = "DM";
                break;
            case TP :
                token = "TP";
                break;
            case TM :
                token = "TM";
                break;
            default:
                token = "UNKNOWN_SYMBOL";
                break;

        }


        return token;
    }
}
