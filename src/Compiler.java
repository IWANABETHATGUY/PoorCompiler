
public class Compiler {

	public static void main(String[] args) {
		Lexer lexer = new Lexer();
		BasicParser parser = new BasicParser(lexer);

	}
}
