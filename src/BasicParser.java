import java.util.ArrayList;
import java.util.List;
class Statement implements AST {
	@Override
	public int getValue() {
		if (children.size() == 1) {
			return children.get(0).getValue();
		}
		return 0;
	}

	@Override
	public String getType() {
		return type;
	}

	public List<AST> children = new ArrayList<>();
	public String type = "Statement";
}

class Expression implements AST {
	@Override
	public int getValue() {
		int sum = children.get(0).getValue();
		if (children.size() == 2) {
			sum = BasicParser.calc(children.get(1).getType(), children.get(0));
		}
		else if (children.size() > 2) {
			for (int i = 1; i < children.size(); i = i + 2) {
				sum = BasicParser.calc(children.get(1).getType(), children.get(i + 1), sum);
			}
		}
		return sum;
	}
	@Override
	public String getType() {
		return type;
	}
	public List<AST> children = new ArrayList<>();
	public String type = "Expression";


}

class Term implements AST {
	@Override
	public int getValue() {
		int sum = children.get(0).getValue();
		for (int i = 1; i < children.size(); i += 2) {
			sum = BasicParser.calc(children.get(i).getType(), children.get(i + 1), sum);
		}
		return sum;
	}
	@Override
	public String getType() {
		return type;
	}
	public List<AST> children = new ArrayList<>();
	public String type = "Term";
}

class Factor implements AST {
	@Override
	public int getValue() {
		if (children.size() == 1) {
			return children.get(0).getValue();
		} else if (children.size() == 2) {
			return BasicParser.calc(children.get(1).getType(), children.get(0));
		}
		return 0;
	}
	@Override
	public String getType() {
		return type;
	}
	public List<AST> children = new ArrayList<>();
	public String type = "Factor";

}

class Opt implements AST {
	@Override
	public int getValue() {
		return 0;
	}
	@Override
	public String getType() {
		return type;
	}
	public String type;
	public Opt(String type) {
		this.type = type;
	}

}

class Number implements AST {
	@Override
	public int getValue() {
		return value;
	}
	@Override
	public String getType() {
		return type;
	}
	public String type = "Number";
	private int value;
	public Number(int val) {
		this.value = val;
	}
}



public class BasicParser {
    private Lexer lexer;
    private boolean isLegalStatement = true;
    public List<List<Lexer.Token>> tokenMap;
    private List<Lexer.Token> curTokenList;
    private List<Statement> program = new ArrayList<>();
    private int row = 0, col = 0;
	public Lexer.Token curToken = null;
	public static int calc(String opt, AST a1, int a2) {
		switch (opt) {
			case "PLUS":
				return a1.getValue() + a2;
			case "MINUS":
				return a2 - a1.getValue();
			case "TIMES":
				return a1.getValue() * a2;
			case "DIVIDE":
				return a2 / a1.getValue();

		}
		return 0;
	}
	public static int calc(String opt, AST a1) {
		int val = a1.getValue();
		switch (opt) {
			case "PD":
				return (val + val) / val;
			case "PT":
				return (val + val) * val;
			case "TP":
				return val * val + val;
			case "TM":
				return val * val - val;
			case "DP":
				return val / val + val;
			case "DM":
				return val / val - val;
			case "MD":
				return (val - val) / val;
			case "MT":
				return (val - val) * val;
		}
		return 0;
	}
    private void getToken() {
    	if (row < tokenMap.size()) {
			curTokenList = tokenMap.get(row);
    		if (col < curTokenList.size()) {
    			Lexer.Token ret = curTokenList.get(col++);
    			if (col >= curTokenList.size()) {
    				row++;
    				col = 0;
				}
				curToken = ret;
			}

		} else {
			curToken = null;
		}

	}

	public boolean match(String type) {
		return curToken != null && curToken.type.equals(type);
	}
	public boolean isSpecial() {
		if (curToken == null) return false;
		String type = curToken.type;
		return type.equals("PD")
				|| type.equals("PT")
				|| type.equals("MD")
				|| type.equals("MT")
				|| type.equals("DP")
				|| type.equals("DM")
				|| type.equals("TP")
				|| type.equals("TM");
	}
    public BasicParser(Lexer lexer) {
    	this.lexer = lexer;
    	tokenMap = this.lexer.getTokenMap();
    	program.add(statements());
    }

    public Statement statements() {
    	/*
    	 * statements -> expression ; | expression ; statements
    	 */

    	getToken();
    	Statement statement = new Statement();
    	statement.children.add(expression());
    	
    	if (match("SEMI")) {
    		/*
    		 * look ahead 读取下一个字符，如果下一个字符不是 EOI
    		 * 那就采用右边解析规则
    		 */
    		getToken();
    	}
    	else {
    		/*
    		 *  如果算术表达式不以分号结束，那就是语法错误
    		 */
    		if (isLegalStatement) {
				isLegalStatement = false;
				System.out.println("line: " + (row + 1) + " Missing Token");
				return null;
			}

    	}
    	
    	if (curToken != null) {
    		/*
    		 * 分号后还有字符，继续解析
    		 */
    		program.add(statements());
    	}
    	
    	if (isLegalStatement) {
			System.out.println(statement.getValue());
    	}
    	return statement;
    }
    
    private Expression expression() {
    	/*
    	 * expression -> term expression'
    	 */
    	Expression expression = new Expression();
    	expression.children.add(term());
    	expr_prime(expression.children); //expression'
		return expression;
    }
    
    private void expr_prime(List<AST> children) {
    	/*
    	 * expression' -> PLUS term expression' | '空'
    	 */
    	if (match("PLUS")) {
    		getToken();
    		children.add(new Opt("PLUS"));
    		children.add(term());
    		expr_prime(children);
    	}
    	else if (match("UNKNOWN_SYMBOL")) {
    		isLegalStatement = false;
    		System.out.println("unknow symbol: " + curToken.content);
    		return;
    	}
    	else {
    		/*
    		 * "空" 就是不再解析，直接返回
    		 */
    		return;
    	}
    }
    
    private Term term() {
    	/*
    	 * term -> factor term'
    	 */
    	Term term = new Term();
    	term.children.add(factor());
    	term_prime(term.children); //term
		return term;
    }
    
    private void term_prime(List<AST> children) {
    	/*
    	 * term' -> * factor term' | '空'
    	 */
    	if (match("TIMES")) {
    		getToken();
			children.add(new Opt("TIMES"));
			children.add(factor());
    		term_prime(children);
    	} else if (match("DIVIDE")) {
			getToken();
			children.add(new Opt("DIVIDE"));
			children.add(factor());
			term_prime(children);
		}
    	else {
    		/*
    		 * 如果不是以 * 开头， 那么执行 '空' 
    		 * 也就是不再做进一步解析，直接返回
    		 */
    		return;
    	}
    }
    
    private Factor factor() {
    	/*
    	 * factor -> NUM_OR_ID | LP expression RP
    	 */
    	Factor factor = new Factor();
    	if (match("NUM_OR_ID")) {
    		factor.children.add(new Number(Integer.parseInt(curToken.content)));
    		getToken();
    		if (isSpecial()) {
				factor.children.add(new Opt(curToken.type));
				getToken();
			}
    	}
    	else if (match("LP")){
    		getToken();
    		factor.children.add(expression());
    		if (match("RP")) {
    			getToken();
    		}
    		else {
    			/*
    			 * 有左括号但没有右括号，错误
    			 */
    			isLegalStatement = false;
    			System.out.println("line: " + lexer.yylineno + " Missing )");
    			return null;
    		}
    	}
    	else {
    		/*
    		 * 这里不是数字，解析出错
    		 */
    		isLegalStatement = false;
    		System.out.println("illegal statements");
    		return null;
    	}
    	return factor;
    }
}
