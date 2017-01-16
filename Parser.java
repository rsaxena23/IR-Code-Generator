import java.util.Vector;

/**
 * implements the main function that gets the file name and calls the scanner and parser 
 */

/**
 * @author Danny Reinheimer
 *
 */
public class Parser {

	
	/**
	 * starting point for the program
	 * @param args The file name to read in and parse
	 */
	public static void main(String[] args) {
		// checks to see if we are given any arguments
		
		boolean code_gen_start=false;
		
		if(args.length < 1) {
			System.out.println("Please provide an input file to process");
			System.exit(0);
		}
		Vector<TokenNames> scannedTokens = new Vector<TokenNames>();
		// run initialize and run the scanner
		Scanner scanner = new Scanner(args[0]);
		scannedTokens = scanner.runScanner();
		// initialize and run the parser
		RecursiveParsing RP = new RecursiveParsing(scannedTokens);
		code_gen_start=RP.parse();
		/*if(code_gen_start)
		{
		   CodeGenTry cgt= new CodeGenTry(args[0]);	
		}*/

	}
	
	
	
	

}
