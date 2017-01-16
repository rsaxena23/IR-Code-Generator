import java.util.Vector;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Implements the recursive decent parser 
 */

/**
 * @author Danny Reinheimer
 *
 */
public class RecursiveParsing {
	
	
	FileWriter generated_file;
	public static int counter = -1;
	String func_name=null;
	private static int numVariables;  // Keeps track of the number of variables 
	private static int numFunctions;  // Keeps track of the number of functions
	private static int numStatements; // Keeps track of the number of statements
	private static Vector<TokenNames> inputTokens; // Stores the set of input tokens 
	private static TokenNames currentToken;  // shows what the current token removed from the stack was for debug purposes 
	ArrayList <String>stack= new ArrayList<String>();
	String label[]=new String[2];
	public boolean variable_decl=false;
	public boolean assign=false;
	public boolean lock_ex=false;
	public boolean is_array=false;
	public boolean dontprint=false;
	public boolean func_cl=false;
	public boolean ifstm=false;
	//public String Normal_line="";
	ArrayList<String> Normal_line= new ArrayList<String>();
	public String Expression_line="",Additional_line="";
	public static int expr_count=0;
	public static String expr_notation="@";
	public static int arr_start=-1;
	public static String label_const= "g";
	public static int label_count=0;
	//Constants
	
	
	
	/**
	 * Constructor initializes the fields and get the list of input tokens
	 * @param inputTokens1
	 */
	public RecursiveParsing(Vector<TokenNames> inputTokens1) {
		numFunctions = 0;
		numVariables = 0;
		numStatements = 0;
		inputTokens = inputTokens1;
		currentToken = TokenNames.None;
		
		try{
		generated_file = new FileWriter("tp_file.c");
		}catch(IOException e)
		{
			System.out.println("Some crappy error");
		}
		
	}
	
	/**
	 * initialized the parsing and prints out the results when finished
	 */
	public boolean parse() {
		program();
		if(inputTokens.firstElement() == TokenNames.eof) {
			System.out.println("Pass variable " + numVariables + " function " + numFunctions + " statement " + numStatements);
			CodeGenTry.stopFilePrint();			
			
			
			return true;
		}
		else {
			System.out.println("error");
			return false;
		}
	}
	
	/**
	 * <program> --> <type name> ID <data decls> <func list> | empty
	 * @return A boolean indicating pass or error 
	 */
	private boolean program() {
		// check if we are at the eof
		//CodeGenTry.filePrinter2("Works?");
		if(inputTokens.firstElement() == TokenNames.eof) {
			return true;
		}
		else if(type_name()) {
			if(inputTokens.firstElement() == TokenNames.ID) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( )); // get the ID token
				func_name = Scanner.tokenValues.get(counter).getValue().toString();
				if(data_decls() && func_list()) {
					//check to see if the remaining token is eof is so this is a legal syntax
					if(inputTokens.firstElement() == TokenNames.eof) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * <func list> --> empty | left_parenthesis <parameter list> right_parenthesis <func Z> <func list Z> 
	 * @return A boolean indicating if the rule passed or failed 
	 */
	private boolean func_list() {
		if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(parameter_list()) {
				if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					if(func_Z()) {
						return func_list_Z();
					}
					return false;
				}
				return false;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * <func Z> --> semicolon | left_brace <data decls Z> <statements> right_brace 
	 * @return A boolean indicating if the rule passed or failed 
	 */
	private boolean func_Z() {
		// checks if the next token is a semicolon
		if(inputTokens.firstElement() == TokenNames.semicolon) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( )); // remove the token from the stack
			return true;
		}
		
		if(inputTokens.firstElement() == TokenNames.left_brace) {
			currentToken = inputTokens.remove(0); counter++;
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			
			ArrayList<String> temp= new ArrayList<String>();
			temp.add(VariableStore.func_name);
			temp.add(VariableStore.var_counter_local+"");
			
			VariableStore.func_var_count.add(temp);
			
			if(!VariableStore.func_name.equals("NULL"))
				VariableStore.flush_local();
			
			VariableStore.func_name=func_name;
			VariableStore.var_counter_local=0;
			
			
			
			if(data_decls_Z()) {
				if(statements()) {
					if(inputTokens.firstElement() == TokenNames.right_brace) {
						currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
						// Count the number of function definitions
						CodeGenTry.filePrinter2(Normal_line);
						numFunctions += 1;
						return true;
					}
					return false;
				}
				return false;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * <func list Z> --> empty | <type name> ID left_parenthesis <parameter list> right_parenthesis <func Z> <func list Z>
	 * @return a boolean 
	 */
	private boolean func_list_Z() {
		if(type_name()) {
			if(inputTokens.firstElement() == TokenNames.ID) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					if(parameter_list()) {
						if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
							currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
							if(func_Z()) {
								return func_list_Z();
							}
						}						
					}					
				}				
			}
			return false;
		}
		// return true for the empty rule
		return true;		
	}
	
	/**
	 * <type name> --> int | void | binary | decimal 
	 * @return A boolean indicating if the rule passed or failed
	 */
	private boolean type_name() {
		if(inputTokens.firstElement() == TokenNames.Int || inputTokens.firstElement() == TokenNames.Void 
				|| inputTokens.firstElement() == TokenNames.binary || inputTokens.firstElement() == TokenNames.decimal) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(Scanner.tokenValues.get(counter).getValue().toString() + " ");
			variable_decl=true;
			return true;
		}
		return false;
	}
	
	/**
	 * <parameter list> --> empty | void <parameter list Z> | <non-empty list> 
	 * @return a boolean
	 */
	private boolean parameter_list() {
		// void <parameter list Z>
		if(inputTokens.firstElement() == TokenNames.Void) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			return parameter_list_Z();
		}
		// <non-empty list>
		else if(non_empty_list()) {
			return true;
		}
		// empty
		return true;
	}
	
	/**
	 * <parameter list Z> --> empty | ID <non-empty list prime>
	 * @return a boolean
	 */
	private boolean parameter_list_Z() {
		if(inputTokens.firstElement() == TokenNames.ID) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			return non_empty_list_prime();
		}
		return true;
	}
	
	/**
	 * <non-empty list> --> int ID <non-empty list prime> | binary ID <non-empty list prime> | 
	 * decimal ID <non-empty list prime>
	 * @return a boolean
	 */
	private boolean non_empty_list() {
		// check for int, binary, decimal
		if(inputTokens.firstElement() == TokenNames.Int || inputTokens.firstElement() == TokenNames.binary || 
				inputTokens.firstElement() == TokenNames.decimal) {
			currentToken = inputTokens.remove(0); counter++;
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( )+" ");
			if(inputTokens.firstElement() == TokenNames.ID) {				
				currentToken = inputTokens.remove(0); counter++;
				Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString());
				VariableStore.add( Scanner.tokenValues.get(counter).getValue().toString(), false);
				return non_empty_list_prime();
			}
		}
		return false;
	}
	
	/**
	 * <non-empty list prime> --> comma <type name> ID <non-empty list prime> | empty
	 * @return a boolean
	 */
	private boolean non_empty_list_prime() {
		if(inputTokens.firstElement() == TokenNames.comma) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(type_name()) {
				if(inputTokens.firstElement() == TokenNames.ID) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					return non_empty_list_prime();
				}
				return false;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * <data decls> --> empty | <id list Z> semicolon <program> | <id list prime> semicolon <program>
	 * @return a boolean
	 */
	private boolean data_decls() {
		if(id_list_Z()) {
			if(inputTokens.firstElement() == TokenNames.semicolon) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				// count variable 
				numVariables += 1;
				return program(); //data_decls_Z();
			}
			return false;
		}
		if(id_list_prime()) {
			if(inputTokens.firstElement() == TokenNames.semicolon) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				// since we consume the first id before we get here count this as a variable
				numVariables += 1;
				return program(); //data_decls_Z();
			}
			//return false;
		}
		return true;
	}
	
	/**
	 * <data decls Z> --> empty | int <id list> semicolon <data decls Z> | 
	 * 				     void <id list> semicolon <data decls Z> | 
	 * 			         binary <id list> semicolon <data decls Z> | decimal <id list> semicolon <data decls Z> 
	 * @return A boolean indicating if the rule passed or failed
	 */
	private boolean data_decls_Z() {
		if(type_name()) {
			if(id_list()) {
				if(inputTokens.firstElement() == TokenNames.semicolon) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					variable_decl=false;
					return data_decls_Z();
				}
				return false;
			}
			return false;
		}
		VariableStore.printStorage();
		return true;
	}
	
	/**
	 * <id list> --> <id> <id list prime>
	 * @return a boolean
	 */
	private boolean id_list() {
		if(id()) {
			return id_list_prime();
		}
		return false;
	}
	
	/**
	 * <id list Z> --> left_bracket <expression> right_bracket <id list prime>
	 * @return a boolean indicating if the rule passed or failed
	 */
	private boolean id_list_Z() {
		if(inputTokens.firstElement() == TokenNames.left_bracket) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(expression()) {
				if(inputTokens.firstElement() == TokenNames.right_bracket) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					return id_list_prime();
				}
			}
		}
		return false;
	}
	
	/**
	 * <id list prime> --> comma <id> <id list prime> | empty
	 * @return a boolean indicating if the rule passed or failed
	 */
	private boolean id_list_prime() {
		if(inputTokens.firstElement() == TokenNames.comma) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			//VariableStore.add();
			if(id()) {
				return id_list_prime();
			}
			return false;
		}
		return true;
	}
	
	/**
	 * <id> --> ID <id Z>
	 * @return a boolean
	 */
	private boolean id() {
		if(inputTokens.firstElement() == TokenNames.ID) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			/* if(variable_decl)
			{
			VariableStore.add(Scanner.tokenValues.get(counter).getValue().toString(),false);			
			}  */
			//stack.add(Scanner.tokenValues.get(counter).getValue().toString());
			return id_Z();
		}
		return false;
	}
	
	/**
	 * <id Z> --> left_bracket <expression> right_bracket | empty
	 * @return a boolean
	 */
	private boolean id_Z() {
		if(inputTokens.firstElement() == TokenNames.left_bracket) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			
			if(expression()) {
				if(variable_decl)
				{		
				
				try{
					//System.out.println("Arr Length:"+Scanner.tokenValues.get(counter).getValue().toString());
				VariableStore.arr_length= Integer.parseInt(Scanner.tokenValues.get(counter).getValue().toString());
				}catch(Exception e)
				{
					System.out.println("Array Length Problem");
				}
				VariableStore.add(Scanner.tokenValues.get(counter-2).getValue().toString(),true);
				
				}
				if(inputTokens.firstElement() == TokenNames.right_bracket) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					// count the number of variables 
					numVariables += 1;
					return true;
				}
				return false;
			}
			return false;
		}
		
		if(variable_decl)
		{
		VariableStore.add(Scanner.tokenValues.get(counter).getValue().toString(),false);			
		}
		
		// count the number of variables 
		numVariables += 1;
		return true;
	}
	
	/**
	 * <block statements> --> left_brace <statements> right_brace 
	 * @return a boolean
	 */
	private boolean block_statements() {
		if(inputTokens.firstElement() == TokenNames.left_brace) {
			currentToken = inputTokens.remove(0); counter++;
			if(!ifstm)
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			else
				Normal_line.add(label[0]+": ");
			if(statements()) {
				if(inputTokens.firstElement() == TokenNames.right_brace) {
					currentToken = inputTokens.remove(0); counter++;
					//Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					
					if(!ifstm)
						Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
						else
							Normal_line.add(label[1]+":");
					
					ifstm=false;
					CodeGenTry.filePrinter2(Normal_line);
					emptyNormalLine();
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * <statements> --> empty | <statement> <statements> 
	 * @return a boolean
	 */
	private boolean statements() {
		if(statement()) {
			numStatements += 1;			
			return statements();
		}
		return true;
	}
	
	/**
	 * <statement> --> ID <statement Z> | <if statement> | <while statement> | 
	 *	<return statement> | <break statement> | <continue statement> | 
	 *	read left_parenthesis  ID right_parenthesis semicolon | 
	 *  write left_parenthesis <expression> right_parenthesis semicolon | 
	 *  print left_parenthesis  STRING right_parenthesis semicolon 
	 * @return a boolean indicating if the rule passed or failed 
	 */
	private boolean statement() {
		CodeGenTry.filePrinter(Expression_line);
		//if(Expression_line.equals("\n"))
		
		CodeGenTry.filePrinter2(Normal_line);
		
		/*else
		{
			
			if(!stack.isEmpty())
			CodeGenTry.filePrinter2(Additional_line+" "+stack.get(0)+";");
			
			Additional_line = "";
		}*/
		while(Normal_line.size()!=0)
			Normal_line.remove(0);
		
		
		System.out.println("print here");
		Normal_line.add("\n ");
		if(inputTokens.firstElement() == TokenNames.ID) {
			currentToken = inputTokens.remove(0); counter++;
			Normal_line.add( " "+VariableStore.lookUp(Scanner.tokenValues.get(counter).getValue().toString()) );
		
		if(VariableStore.lookUp(Scanner.tokenValues.get(counter).getValue().toString())!=null)
		{Additional_line="\n" + VariableStore.lookUp(Scanner.tokenValues.get(counter).getValue().toString()) + "=";							}
			//stack.add(currentToken.toString());
			return statement_Z();
		}
		if(if_statement()) {
			return true;
		}
		if(while_statement()) {
			return true;
		}
		if(return_statement()) {
			return true;
		}
		if(break_statement()) {
			return true;
		}
		if(continue_statement()) {
			return true;
		}
		if(inputTokens.firstElement() == TokenNames.read) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				stack.add(Scanner.tokenValues.get(counter).getValue().toString( ));
			if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				stack.add(Scanner.tokenValues.get(counter).getValue().toString( ));
				if(inputTokens.firstElement() == TokenNames.ID) {
					currentToken = inputTokens.remove(0); counter++;
					Normal_line.add(  VariableStore.lookUp(Scanner.tokenValues.get(counter).getValue().toString( )));
					stack.add(VariableStore.lookUp(Scanner.tokenValues.get(counter).getValue().toString( )));
					if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
						currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
						stack.add(Scanner.tokenValues.get(counter).getValue().toString( ));
						
						if(inputTokens.firstElement() == TokenNames.semicolon) {
							currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
							//stack.add(Scanner.tokenValues.get(counter).getValue().toString());
							resolveExp();
							emptyStack();
							return true;
						}
					}
				}
			}
			return false;
		}
		
		// write left_parenthesis <expression> right_parenthesis semicolon
		if(inputTokens.firstElement() == TokenNames.write) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				if(expression()) {
					if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
						currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
						if(inputTokens.firstElement() == TokenNames.semicolon) {
							currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
							return true;
						}
					}
				}
			}
			return false;
		}
		
		// print left_parenthesis  STRING right_parenthesis semicolon
		if(inputTokens.firstElement() == TokenNames.print) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				if(inputTokens.firstElement() == TokenNames.STRING) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
						currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
						if(inputTokens.firstElement() == TokenNames.semicolon) {
							currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
							return true;
						}
					}
				}
			}
			return false;
		}
		return false;
	}
	
	/**
	 * <statement Z> --> <assignment Z> | <func call>
	 * @return a boolean indicating if the rule passed or failed
	 */
	private boolean statement_Z() {
		if(assignment_Z()) {
			return true;
		}
		else if(func_call()) {
			return true;
		}
		return false;
	}
	
	/**
	 * <assignment Z> --> equal_sign <expression> semicolon | 
	 * left_bracket <expression> right_bracket equal_sign <expression> semicolon
	 * @return a boolean
	 */
	private boolean assignment_Z() {
		if(inputTokens.firstElement() == TokenNames.equal_sign) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			assign=true;
			
			
			if(expression()) {
				if(inputTokens.firstElement() == TokenNames.semicolon) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					stack.add(Scanner.tokenValues.get(counter).getValue().toString());
					printStack();
					resolveExp();
					emptyStack();
					//expr_notation="@";
					assign=false;
					lock_ex=false;
					System.out.println("Resolved"+expr_count); 
			//		calcExp();
					return true;
				}
			}
			return false;
		}
		if(inputTokens.firstElement() == TokenNames.left_bracket) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(expression()) {
				if(inputTokens.firstElement() == TokenNames.right_bracket) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					if(inputTokens.firstElement() == TokenNames.equal_sign) {
						currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
						if(expression()) {
							if(inputTokens.firstElement() == TokenNames.semicolon) {
								currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
								return true;
							}
						}
					}
				}
			}
			return false;
		}
		return false;
	}
	
	/**
	 * <func call> --> left_parenthesis <expr list> right_parenthesis semicolon 
	 * @return a boolean
	 */
	private boolean func_call() {
		if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
			currentToken = inputTokens.remove(0); counter++;
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			stack.add(Scanner.tokenValues.get(counter).getValue().toString( ));
			if(expr_list()) {
				if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					stack.add(Scanner.tokenValues.get(counter).getValue().toString( ));		
					if(inputTokens.firstElement() == TokenNames.semicolon) {
						currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
						func_cl=true;
						resolveExp();
						CodeGenTry.filePrinter(Expression_line);
						emptyStack();
						return true;
					}
				}
			}
		}		
		return false;
	}
	
	/**
	 * <expr list> --> empty | <non-empty expr list> 
	 * @return a boolean
	 */
	private boolean expr_list() {
		if(non_empty_expr_list()) {
			return true;
		}
		return true;
	}
	
	/**
	 * <non-empty expr list> --> <expression> <non-empty expr list prime>
	 * @return a boolean
	 */
	private boolean non_empty_expr_list() {
		
		assign=true;
		if(expression()) {
			
			resolveExp();
			emptyStack();
			CodeGenTry.expChecker(Expression_line);
			CodeGenTry.expChecker2(Normal_line);
			emptyNormalLine();
			assign=false;
			lock_ex=false;
			
			return non_empty_expr_list_prime();
		}
		return false;
	}
	
	/**
	 * <non-empty expr list prime> --> comma <expression> <non-empty expr list prime> | empty
	 * @return a boolean
	 */
	private boolean non_empty_expr_list_prime() {
		if(inputTokens.firstElement() == TokenNames.comma) {
			currentToken = inputTokens.remove(0); counter++;
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
		assign=true;
			if(expression()) {
				resolveExp();
				emptyStack();
				CodeGenTry.expChecker(Expression_line);
				CodeGenTry.expChecker2(Normal_line);
				emptyNormalLine();
				return non_empty_expr_list_prime();
			}
			return false;
		}
		assign=false;
		lock_ex=false;
		return true;
	}
	
	/**
	 * <if statement> --> if left_parenthesis <condition expression> right_parenthesis <block statements> 
	 * @return a boolean
	 */
	private boolean if_statement() {
		if(inputTokens.firstElement() == TokenNames.If) {
			ifstm=true;
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				if(condition_expression()) {
					if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
						currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
						
						//System.out.println("Before block statements");
						printStack();
						resolveExp();
						emptyStack();
						
						assign=false;
						lock_ex=false;
						setLabel();
						Normal_line.add("\ngoto "+label[0]+";");
						Normal_line.add("\ngoto "+label[1]+";");
						return block_statements();
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * <condition expression> -->  <condition> <condition expression Z>
	 * @return a boolean
	 */
	private boolean condition_expression() {
		if(condition()) {
			assign=true;
			printStack();
			resolveExp();
			emptyStack();
			
			System.out.println("Condition exp print");
			CodeGenTry.filePrinter(Expression_line);
			CodeGenTry.filePrinter2(Normal_line);
			emptyNormalLine();
			
			dontprint=true;
			
			return condition_expression_Z();
		}
		return false;
	}
	
	/**
	 * <condition expression Z> --> <condition op> <condition> | empty
	 * @return a boolean
	 */
	private boolean condition_expression_Z() {
		if(condition_op()) {
			return condition();
		}
		return true;
	}
	
	/**
	 * <condition op> --> double_end_sign | double_or_sign 
	 * @return a boolean
	 */
	private boolean condition_op() {
		if(inputTokens.firstElement() == TokenNames.double_and_sign || inputTokens.firstElement() == TokenNames.double_or_sign) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(Scanner.tokenValues.get(counter).getValue() + " ");
			return true;
		}
		return false;
	}
	
	/**
	 * <condition> --> <expression> <comparison op> <expression> 
	 * @return a boolean
	 */
	private boolean condition() {
		assign=true;
		if(expression()) {
			System.out.println("Condition Stack:");
			printStack();
			resolveExp();
			emptyStack();
			
			System.out.println("Condition print:");
			CodeGenTry.filePrinter(Expression_line);
			CodeGenTry.filePrinter2(Normal_line);
			emptyNormalLine();
			assign=false;
			lock_ex=false;
			if(comparison_op()) {
				return expression();
			}
		}
		return false;
	}
	
	/**
	 * <comparison op> --> == | != | > | >= | < | <=
	 * @return a boolean
	 */
	private boolean comparison_op() {
		assign=false;
		lock_ex=false;
		if(inputTokens.firstElement() == TokenNames.doubleEqualSign || inputTokens.firstElement() == TokenNames.notEqualSign ||
				inputTokens.firstElement() == TokenNames.greaterThenSign || inputTokens.firstElement() == TokenNames.greaterThenOrEqualSign ||
				inputTokens.firstElement() == TokenNames.lessThenSign || inputTokens.firstElement() == TokenNames.lessThenOrEqualSign) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			return true;
		}
		return false;
	}
	
	/**
	 * <while statement> --> while left_parenthesis <condition expression> right_parenthesis <block statements> 
	 * @return
	 */
	private boolean while_statement() {
		if(inputTokens.firstElement() == TokenNames.While) {
			currentToken = inputTokens.remove(0); counter++;
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				if(condition_expression()){
					if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
						currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
						return block_statements();
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * <return statement> --> return <return statement Z>
	 * @return a boolean
	 */
	private boolean return_statement() {
		if(inputTokens.firstElement() == TokenNames.Return) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( )+" ");
			return return_statement_Z();
		}
		return false;
	}
	
	/**
	 * <return statement Z> --> <expression> semicolon | semicolon 
	 * @return a boolean
	 */
	private boolean return_statement_Z() {
		if(expression()) {
			if(inputTokens.firstElement() == TokenNames.semicolon) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				return true;
			}
			return false;
		}
		if(inputTokens.firstElement() == TokenNames.semicolon) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			return true;
		}
		return false;
	}
	
	/**
	 * <break statement> ---> break semicolon
	 * @return a boolean
	 */
	private boolean break_statement() {
		if(inputTokens.firstElement() == TokenNames.Break) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(inputTokens.firstElement() == TokenNames.semicolon) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <continue statement> ---> continue semicolon
	 * @return a boolean
	 */
	private boolean continue_statement() {
		if(inputTokens.firstElement() == TokenNames.Continue) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(inputTokens.firstElement() == TokenNames.semicolon) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <expression> --> <term> <expression prime>
	 * @return a boolean
	 */
	private boolean expression() {
		Expression_line="";
	//	if(expr_notation.equals("@"))
		if(!lock_ex)
		{
			System.out.println("Lock occured");
		expr_notation="@"+expr_count+"@";
		Normal_line.add(expr_notation);
		expr_count++;
		if(assign)
			lock_ex=true;
		}  
		if(term()) {
			return expression_prime();
		}
		return false;
	}
	
	/**
	 * <expression prime> --> <addop> <term> <expression prime> | empty
	 * @return
	 */
	private boolean expression_prime() {
		if(addop()) {
			if(term()) {
				return expression_prime();
			}
			return false;
		}
		return true;
	}
	
	/**
	 * <addop> --> plus_sign | minus_sign 
	 * @return a boolean
	 */
	private boolean addop() {
		if(inputTokens.firstElement() == TokenNames.plus_sign || inputTokens.firstElement() == TokenNames.minus_sign) {
			currentToken = inputTokens.remove(0); counter++;
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			stack.add(Scanner.tokenValues.get(counter).getValue().toString());
			return true;
		}
		return false;
	}
	
	/**
	 * <term> --> <factor> <term prime>
	 * @return a boolean
	 */
	private boolean term() {
		if(factor()) {
			return term_prime();
		}
		return false;
	}
	
	/**
	 * <term prime> --> <mulop> <factor> <term prime> | empty
	 * @return
	 */
	private boolean term_prime() {
		if(mulop()) {
			if(factor()) {
				return term_prime();
			}
			return false;
		}
		return true;
	}
	
	/**
	 * <mulop> --> star_sign | forward_slash 
	 * @return a boolean
	 */
	private boolean mulop() {
		if(inputTokens.firstElement() == TokenNames.star_sign || inputTokens.firstElement() == TokenNames.forward_slash) {
			currentToken = inputTokens.remove(0); counter++;
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			stack.add(Scanner.tokenValues.get(counter).getValue().toString());
			return true;
		}
		return false;
	}
	
	/**
	 * <factor> --> ID <factor Z> | NUMBER | minus_sign NUMBER | left_parenthesis <expression>right_parenthesis 
	 * @return
	 */
	private boolean factor() {
		
		if(inputTokens.firstElement() == TokenNames.ID) {
			currentToken = inputTokens.remove(0); counter++;
			//Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			
			//stack.add(Scanner.tokenValues.get(counter).getValue().toString());
						
			//stack.add(currentToken.toString());
			return factor_Z();
		}
		// NUMBER
		if(inputTokens.firstElement() == TokenNames.NUMBER) {
			currentToken = inputTokens.remove(0); counter++;
			//Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			
			if(!variable_decl && !is_array)
			{
			stack.add(Scanner.tokenValues.get(counter).getValue().toString());
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			}
			//stack.add(currentToken.toString());
			return true;
		}
		
		// minus_sign NUMBER
		if(inputTokens.firstElement() == TokenNames.minus_sign) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			
			if(!variable_decl)
			stack.add(Scanner.tokenValues.get(counter).getValue().toString());
			
			if(inputTokens.firstElement() == TokenNames.NUMBER) {
				currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
				
				if(!variable_decl)
				stack.add(Scanner.tokenValues.get(counter).getValue().toString());
				
				return true;
			}
			return false;
		}
		
		// left_parenthesis <expression>right_parenthesis
		if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
			currentToken = inputTokens.remove(0); counter++;
			Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			stack.add(Scanner.tokenValues.get(counter).getValue().toString());
			if(expression()) {
				if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
					currentToken = inputTokens.remove(0); counter++;
					Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					stack.add(Scanner.tokenValues.get(counter).getValue().toString());
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	/**
	 * <factor Z> --> left_bracket <expression> right_bracket | left_parenthesis <expr list> right_parenthesis | empty
	 * @return
	 */
	private boolean factor_Z() {
		// left_bracket <expression> right_bracket
		if(inputTokens.firstElement() == TokenNames.left_bracket) {
			is_array=true;
			arr_start=counter;
			currentToken = inputTokens.remove(0); counter++;
			//Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(expression()) {				
				if(inputTokens.firstElement() == TokenNames.right_bracket) {
						
					
					currentToken = inputTokens.remove(0); counter++;
					
					//Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					
					String temp="";
					for(int h=arr_start;h<=counter;h++)
						temp=temp +  Scanner.tokenValues.get(h).getValue().toString();
						
						stack.add(temp);
						Normal_line.add(temp);
					is_array=false;
					return true;
				}
			}
			return false;
		}
		// left_parenthesis <expr list> right_parenthesis
		if(inputTokens.firstElement() == TokenNames.left_parenthesis) {
			currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
			if(expr_list()) {
				if(inputTokens.firstElement() == TokenNames.right_parenthesis) {
					currentToken = inputTokens.remove(0); counter++;Normal_line.add(  Scanner.tokenValues.get(counter).getValue().toString( ));
					return true;
				}
			}
			return false;
		}
		// empty
		if(!is_array)
		{
			stack.add(Scanner.tokenValues.get(counter).getValue().toString());
			Normal_line.add(Scanner.tokenValues.get(counter).getValue().toString());
		}
		return true;
	}
	
	private void printStack()
	{
		int i;
		System.out.println("");
		System.out.println("Stack: ");
		for(i=0;i<stack.size();i++)
		{
			System.out.print(stack.get(i) + " ");
		}	
		
		System.out.println("");
	}
	
	public void emptyStack()
	{
		for(int i=0;i<stack.size();i++)
			stack.remove(0);
	}
	
	public void resolveExp()
	{ 
		int i,a;
		ArrayList<Integer> bracket_pos= new ArrayList<Integer>();				
		int exp_size=stack.size()-1;
		try
		{
		
		if(stack.size()>0)
		{
		if(stack.size()<=2  && !(VariableStore.lookUp( stack.get(0))==null) )
		{			
			ArrayList<String> temp = new ArrayList<String>();
			
			temp.add(expr_notation);
			temp.add(VariableStore.lookUp( stack.get(0)));
			
			/*if(VariableStore.lookUp( stack.get(0))==null)
			temp.add(exp_size+"");
			
			else */
				temp.add((exp_size+1)+"");
			
			System.out.println("Expr set"+expr_notation+"    len:"+exp_size+"  :"+VariableStore.lookUp( stack.get(0)));
			
			VariableStore.storage.add(temp);
		}
		else
		{
		
		for(i=0;i<stack.size();i++)
		{
			//System.out.println("value:"+stack.get(i));
		    if(stack.get(i).toString().equals("("))
		    {
		    	bracket_pos.add(i);
		    }
		    else if(stack.get(i).toString().equals(")"))
		    {	
		    	int temp_loc =  bracket_pos.get( bracket_pos.size() -1 );
		    	List<String> temp =  stack.subList(temp_loc+1, i);
		    	temp.add(";");
		    	
		    	temp= calcExp(temp);
		 /*   	System.out.println("Here");	    	
		    
		    	for(int j=0;j<stack.size();j++)
		    	{
		    		System.out.print(stack.get(j)+" ");
		   	}*/ 		    	
		    	
		    	
		    	
		    	stack.remove(temp_loc + 2);
		    	stack.remove(temp_loc); 	
		    			    	
		    	System.out.println("Editted");
		    	printStack();
		    	i=bracket_pos.remove(bracket_pos.size()-1);
		    }
		}
		}
		
		if(stack.size()>1)
		{
			calcExp(stack);
		}
		
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(expr_notation);
		temp.add(stack.get(0));
		temp.add(exp_size+"");
		
		System.out.println("Expr set"+expr_notation+"    len:"+exp_size);
		
		VariableStore.storage.add(temp);
		
		}
		
		
		
	}catch(Exception e)
	{
		CodeGenTry.stopFilePrint();
	}
	}
	
	public List<String> calcExp(List<String> stack)
	{		
		int i;	
		
		while(stack.size()>2)
		{
			for(i=0;i<stack.size();i++)
			{
				//System.out.println("Entered");
				if(stack.get(i).toString().equals("*") || stack.get(i).toString().equals("/"))
				{
					//System.out.println("Found Mul");
					try{											
							System.out.println(" Multiply ");
							String ans =execOp(stack.get(i-1),stack.get(i),stack.get(i+1));
							
							for(int a=0;a<3;a++)
								stack.remove(i-1);
								
								if((i-1)<(stack.size() ))
								stack.add(i-1,ans);
								
								else
								stack.add(ans);								
							break;						
						
					}catch(Exception e)
					{
						System.out.println(" Operand missing ");
						System.exit(0);
					}
					
					
				}
				if(stack.get(i).toString().equals("+") || stack.get(i).toString().equals("-"))
				{
					//System.out.println("Found add");
					try{
						if(! ( stack.get(i+2).toString().equals("*") || stack.get(i+2).toString().equals("/") ) )
						{
							//System.out.println(" add:"+stack.get(i+2));
				/*			System.out.println("1st Stack Add_Temp:");
							for(int b=0;b<stack.size();b++)
							{
								System.out.print(stack.get(b) + " ");
							}*/
							
							String ans =execOp(stack.get(i-1),stack.get(i),stack.get(i+1));
														
							for(int a=0;a<3;a++)
								stack.remove(i-1);
								
								if((i-1)<(stack.size() ))
								stack.add(i-1,ans);
								
								else
								stack.add(ans);
							
								/*System.out.println("2nd Stack Add_Temp:"+(i-1)+"  "+ ans);
								for(int b=0;b<stack.size();b++)
								{
									System.out.print(stack.get(b) + " ");
								}  */
								
							break;
						}
						
					}catch(Exception e)
					{
						System.out.println(" Operand missing ");
						System.exit(0);
					}
					
				}
			}
		}
		
		//System.out.println("out of local exp");
		if(stack.size()>1)
		stack.remove(1);
		
		return stack;
	}
	
	public String execOp(String var1,String operation,String var2)	
	{
		System.out.println("");
		VariableStore.printStorage();
		System.out.println("");
		
		if( VariableStore.lookUp(var1)!=null)
		var1 = VariableStore.lookUp(var1);
		
		if( VariableStore.lookUp(var2)!=null)
		var2 = VariableStore.lookUp(var2);
		
		System.out.println(var1+" "+operation+" "+var2);
		
		String new_var= "local["+(++VariableStore.var_counter_local)+"]";
		Expression_line+="\n"+new_var+"="+var1+" "+operation+" "+var2+";";
		
		return new_var;
	}
	
	public void emptyNormalLine()
	{
		while(Normal_line.size()!=0)
			Normal_line.remove(0);
	}
	
	public void setLabel()
	{
		label[0]=label_const+(label_count++);
		label[1]=label_const+(label_count);
		
		
	}
	

}
