import java.io.File;
import java.io.FilenameFilter;


public class FileRenamer {
public static void main(String[] args) {
	File[] files = new File("/home/mam2/exp"/*"/home/mam2/graphs"*/).listFiles(new FilenameFilter() {
		
		public boolean accept(File arg0, String arg1) {
			if (arg1.endsWith(".eps")) return true;
			return false;
		}
	});
	for (File f : files){
//		f.renameTo(new File(f.getAbsolutePath().replace(".csv_", "")));
		System.out.println("epstopdf --outfile="+f.getAbsolutePath().replace(".eps", ".pdf") + " " + f.getAbsolutePath());
	}
}
}
