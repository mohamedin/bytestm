import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;


public class CheckCiteDuplicates {
public static void main(String[] args) {
	try {
		BufferedReader in  = new BufferedReader(new FileReader("/home/mam2/Dropbox/Thesis/BibTex/all.bib"));
		String str;
		HashSet<String> ref = new HashSet<String>();
		while ((str=in.readLine())!=null){
			if (str.toLowerCase().contains("title") && !str.toLowerCase().contains("booktitle") ){
				if (!ref.add(str.toLowerCase().replaceAll("[^A-Za-z0-9 ]", "")))
					System.out.println(str);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}

}
}
