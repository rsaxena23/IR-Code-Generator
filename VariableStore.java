import java.util.ArrayList;

public class VariableStore {

	
	public static ArrayList<ArrayList> storage= new ArrayList<ArrayList>();
	public static ArrayList<ArrayList> exp_storage= new ArrayList<ArrayList>();
	public static ArrayList<ArrayList> func_var_count= new ArrayList<ArrayList>();
	public static int var_counter_local=0,var_counter_global=0,arr_length=1;
	public static String func_name="NULL";
	
	
	public static void add(String name,boolean array)
	{
		int no_var;		
		
		for(no_var=0;no_var<arr_length;no_var++)
		{
			ArrayList <String> al = new ArrayList<String>();
			
			if(array)
			al.add(name+"["+no_var+"]");
			
			else
				al.add(name);
			
		if(!(func_name==null))		
		{
			al.add("local["+var_counter_local+"]");
			var_counter_local++;
		}
		else
		{
		al.add("global["+var_counter_global+"]");
		var_counter_global++;
		}	
		al.add(func_name);
		
		storage.add(al);
		}
		
		arr_length=1;
		
	}
	public static String lookUp(String name)
	{
		int i;
		String temp;
		
		for(i=0;i<storage.size();i++)
		{
			temp= storage.get(i).get(0).toString();			
			
			if(temp.equals(name.trim()))
				return storage.get(i).get(1).toString();			
			else if (temp.equals(name.trim()) && storage.get(i).get(2).toString().equals("NULL") )
				return storage.get(i).get(1).toString();
		}
		
		return null;
	}
	public static void printStorage()
	{
		System.out.println("Variables:");
		for(int i=0;i<storage.size();i++)
		{
			System.out.println(storage.get(i).get(0) + "  " + storage.get(i).get(1) + "  " + storage.get(i).get(2));
		}
		
	}
	
	public static int exprLength(String name)
	{
		int i;
		String temp;
		
		if(name==null)
		return 0;
			
		for(i=0;i<storage.size();i++)
		{
			temp= storage.get(i).get(0).toString();			
			
			if(temp.equals(name.trim()))
				return Integer.parseInt( storage.get(i).get(2).toString() );			
		}
		
		return 0;
	}
	

	public static void flush_local()
	{
		int a;
		for(a=0;a<storage.size();a++)
		{
			if(!(storage.get(a).get(2).toString().equals("NULL")))
				break;
		}
		if(a!=storage.size())
		{
			for(int m=a;m<storage.size();m++)
			storage.remove(a);
			
			
		}
	}
}
