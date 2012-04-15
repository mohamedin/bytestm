import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;


public class FixLinesStyles {
public static void main(String[] args) {
	
	File[] files = new File("/home/mam2/exp").listFiles(new FilenameFilter() {
		
		public boolean accept(File arg0, String arg1) {
			if (arg1.endsWith(".eps")) return true;
			return false;
		}
	});
	PrintWriter pw = null ;
	BufferedReader in  =null;
	for (File f : files){
		File ft = new File(f.getAbsolutePath().replace(".eps", "M.eps"));
		try{
			in  = new BufferedReader(new FileReader(f));
			
			pw = new PrintWriter(ft);
			
		while (true){

			String str=in.readLine();
			if (str==null) break;
			pw.println(str);
			if (str.equals("% Default Line colors")){
				str=in.readLine();
				pw.println(str);
				str=in.readLine();
				pw.println(str);
				str=in.readLine();
				pw.println(str);
				while (true){
					str=in.readLine();
					if (str.startsWith("/LC0"))
						pw.println("/LC0 {1 0 0} def");
					else{
						pw.println(str);
						break;
					}

					str=in.readLine();
					if (str.startsWith("/LC1"))
						pw.println("/LC1 {0 0.7 0} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LC2"))
						pw.println("/LC2 {0 0 1} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LC3"))
						pw.println("/LC3 {0.7 0 0.7} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LC4"))
						pw.println("/LC4 {0 0.5 1} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LC5"))
						pw.println("/LC5 {1 0.6 0} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LC6"))
						pw.println("/LC6 {0 0 0} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LC7"))
						pw.println("/LC7 {1 0.3 0} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LC8"))
						pw.println("/LC8 {0.4 0.4 0.4} def");
					else{
						pw.println(str);
						break;
					}
				}
			}
			
			if (str.equals("% Default Line Types")){
				str=in.readLine();
				pw.println(str);
				str=in.readLine();
				pw.println(str);
				str=in.readLine();
				pw.println(str);
				while(true){
					str=in.readLine();
					if (str.startsWith("/LT0"))
						pw.println("/LT0 {PL [] LC0 DL} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LT1"))
						pw.println("/LT1 {PL [4 dl1 4 dl2] LC1 DL} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LT2"))
						pw.println("/LT2 {PL [14 dl1 3 dl2] LC2 DL} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LT3"))
						pw.println("/LT3 {PL [1 dl1 3 dl2] LC3 DL} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LT4"))
						pw.println("/LT4 {PL [6 dl1 4 dl2 1 dl1 4 dl2] LC4 DL} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LT5"))
						pw.println("/LT5 {PL [14 dl1 4 dl2 1 dl1 4 dl2 1 dl1 4 dl2 1 dl1 4 dl2] LC5 DL} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LT6"))
						pw.println("/LT6 {PL [2 dl1 2 dl2 2 dl1 6 dl2] LC6 DL} def");
					else{
						pw.println(str);
						break;
					}
	
					str=in.readLine();
					if (str.startsWith("/LT7"))
						pw.println("/LT7 {PL [1 dl1 4 dl2 6 dl1 4 dl2 1 dl1 4 dl2] LC7 DL} def");
					else{
						pw.println(str);
						break;
					}
					
					str=in.readLine();
					if (str.startsWith("/LT8"))
						pw.println("/LT8 {PL [14 dl1 4 dl2 2 dl1 4 dl2] LC8 DL} def");
					else{
						pw.println(str);
						break;
					}
			}
			}
			if (str.equals("% Begin plot #18")){
				str=in.readLine();
				pw.println("3.000 UL");
			}
	}

}catch (Exception e) {
	
}
finally{
	pw.close();
	try {
		in.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	f.delete();
	ft.renameTo(f);
}
}
	}
}
