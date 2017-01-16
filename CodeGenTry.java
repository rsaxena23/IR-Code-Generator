import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;


public class CodeGenTry {
	
	public static FileWriter fw=null;
	public static int entry_no=0;
	
	
	
	public static String expChecker(String input)
	{
		int i,j;
		String lk_up=null;
		
	//	System.out.println("Checker entry:"+input);
		
		i=input.indexOf('@');
		if(i!=-1)
		{
		String temp=input.substring(i+1);
	//	System.out.println("Temp:"+temp);
		j=temp.indexOf('@');
		
				if(j!=-1)
				lk_up= input.substring(i,i+j+2);
						
				String val= VariableStore.lookUp(lk_up);
				System.out.println("Looking up:"+lk_up);
				VariableStore.printStorage();
				
			/*	if(val!=null)
				{*/
				int del_num= VariableStore.exprLength(lk_up);
				
				if(val==null)
				val="";
				else
				{
					if(del_num==0)
						del_num=val.length();
				}
				String firsthalf= input.substring(0, i);
				String secondhalf= input.substring(i+j+2+(del_num));
				System.out.println("First: "+firsthalf+"  ,Second: "+secondhalf);
				String new_expr = firsthalf  + val + secondhalf;
				
				System.out.println("\n Modified String:"+new_expr+"\n");
				
				return new_expr;
		/*		} */
		}	
		
		return null;
	}
	
	public static void filePrinter(String st)
	{			
		System.out.println("Before mod File:\n " +st+"\n");
		
		if(expChecker(st)!=null)
		st= expChecker(st);
		
		System.out.println("After mod File:\n " +st+"\n");
		
		try{
			
		if(fw==null)
		fw= new FileWriter("Generated_file.c");	
		
		if(entry_no==0)
		fw.write(st);		
	
		else
		fw.append(st);
			
		
		entry_no++;
		
		
		}catch(Exception e)
		{System.out.println("File Problem");}
		
		
	}
	
	public static ArrayList<String> expChecker2(ArrayList<String> input)
	{
		int i;
		
		
		for(i=0;i<input.size();i++)
		{
			System.out.println("Input:"+input.get(i));
			if( input.get(i).charAt(0)=='@'  )
			{
			break;
			}
		}
		if(i!=input.size())
		{
			String val = VariableStore.lookUp(input.get(i));			
			System.out.println("Look up:"+val);
			
			int del_num = VariableStore.exprLength(input.get(i));
			System.out.println(del_num);
			
			for(int m=i;m<=(i+del_num);m++)
				input.remove(i);
			
			if(val!=null)
			input.add(i,val);
			
			
		}
		
		System.out.println("Modified");
		
		for(int k=0;k<input.size();k++)
		{
		   System.out.print(" "+input.get(k));	
		}
		
		return input;
		
	}
	
	
	public static void filePrinter2(ArrayList<String> st)
	{
		int a;
		try{
			
			System.out.println("before mod2");
			for(int f=0;f<st.size();f++)
			System.out.println(st.get(f));
				
			if(fw==null)
			fw= new FileWriter("Generated_file.c");	
		
			st = expChecker2(st);
			
			for(a=0;a<st.size();a++)
			{
		if(entry_no==0)
			fw.write(st.get(a));		
		
			else
			fw.append(st.get(a));
				
			
			entry_no++;
			
			}
			
			}catch(Exception e)
			{System.out.println("File Problem here");}
		
		
	}
	
	public static void stopFilePrint()
	{
		try{
		fw.close();
		}catch(Exception e)
		{}
	}
	

}
