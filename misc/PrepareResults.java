import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Writer;


public class PrepareResults {
	public static void main(String[] args) {
//		try {
//			String infilename = "";
//			BufferedReader in  = new BufferedReader(new FileReader(infilename));
//			String str;
//			String origOutfilename = infilename+"_gnu";
//			String outfilename = origOutfilename;
//			PrintWriter pw = new PrintWriter(outfilename);
//			
//			while ((str=in.readLine())!= null){
//				if (str.length() < 5){
//					outfilename = origOutfilename + str;
//					pw.close();
//					pw = new PrintWriter(outfilename);
//				}
//				
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		//LinkedList
		String outfilename = "/home/mam2/LL20.csv";
		String[] files ={
				"/home/mam2/April6Results/April6_Ring/mineL.csv",
				"/home/mam2/April6Results/April6_Ring/deuceL.csv",
				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineL.csv",
				"/home/mam2/April6Results/April6_TL2/mineL.csv",
				"/home/mam2/April6Results/April6_TL2/deuceL.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceL.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuceL.csv",
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//object
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//multiverse
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//dstm2
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//jvstm
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv"//LF
		};
		int[] start = {
				2,
				2,
				2,
				2,
				2,
				2,
				2,
				2,
				30,
				2,
				30,
				114
		};
		generateFile(outfilename, files, start,  1000);

		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/LL50.csv";
		generateFile(outfilename, files, start,  1000);
		
		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/LL80.csv";
		generateFile(outfilename, files, start,  1000);

		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/LL100.csv";
		generateFile(outfilename, files, start,  1000);
		
		//Skip list
		outfilename = "/home/mam2/SL20.csv";
		files = new String[]{
				"/home/mam2/April6Results/April6_Ring/mineSL.csv",
				"/home/mam2/April6Results/April6_Ring/deuceSL.csv",
				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineSL.csv",
				"/home/mam2/April6Results/April6_TL2/mineSL.csv",
				"/home/mam2/April6Results/April6_TL2/deuceSL.csv",
				"/home/mam2/deuceSL_lsaNoOpt.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuceSL.csv",
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//object
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//multiverse
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//dstm2
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//jvstm
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv"//LF
		};
		start = new int[]{
				2,
				2,
				2,
				2,
				2,
				2,
				2,
				58,
				86,
				58,
				86,
				142
		};
		generateFile(outfilename, files, start,  1000);
		
		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/SL50.csv";
		generateFile(outfilename, files, start,  1000);
		
		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/SL80.csv";
		generateFile(outfilename, files, start,  1000);

		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/SL100.csv";
		generateFile(outfilename, files, start,  1000);

		//RBTree
		outfilename = "/home/mam2/RB20.csv";
		files = new String[]{
				"/home/mam2/April6Results/April6_Ring/mineR.csv",
				"/home/mam2/April6Results/April6_Ring/deuceR.csv",
				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineR.csv",
				"/home/mam2/April6Results/April6_TL2/mineR.csv",
				"/home/mam2/April6Results/April6_TL2/deuceR.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceR.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuceR.csv",
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//object
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//multiverse
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//dstm2
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//jvstm
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv"//LF
		};
		start = new int[]{
				2,
				2,
				2,
				2,
				2,
				2,
				2,
//				58,
//				86,
//				58,
//				86,
//				142
		};
		generateFile(outfilename, files, start,  1000);
		
		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/RB50.csv";
		generateFile(outfilename, files, start,  1000);
		
		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/RB80.csv";
		generateFile(outfilename, files, start,  1000);

		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/RB100.csv";
		generateFile(outfilename, files, start,  1000);

		//Hash
		outfilename = "/home/mam2/HS20.csv";
		files = new String[]{
				"/home/mam2/April6Results/April6_Ring/mineH.csv",
				"/home/mam2/April6Results/April6_Ring/deuceH.csv",
				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineH.csv",
				"/home/mam2/April6Results/April6_TL2/mineH.csv",
				"/home/mam2/April6Results/April6_TL2/deuceH.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceH.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuceH.csv",
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//object
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//multiverse
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//dstm2
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//jvstm
				"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv"//LF
		};
		start = new int[]{
				2,
				2,
				2,
				2,
				2,
				2,
				2,
//				58,
//				86,
//				58,
//				86,
				114
		};
		generateFile(outfilename, files, start,  1000);
		
		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/HS50.csv";
		generateFile(outfilename, files, start,  1000);
		
		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/HS80.csv";
		generateFile(outfilename, files, start,  1000);

		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/HS100.csv";
		generateFile(outfilename, files, start,  1000);
		
		//Bank
		outfilename = "/home/mam2/bank20.csv";
		files = new String[]{
				"/home/mam2/April6Results/April6_Ring/mineB.csv",
				"/home/mam2/April6Results/April6_Ring/deuceB.csv",
				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineB.csv",
				"/home/mam2/April6Results/April6_TL2/mineB.csv",
				"/home/mam2/April6Results/April6_TL2/deuceB.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceB.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuceB.csv",
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//object
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv",//multiverse
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//dstm2
				//"/home/mam2/finalResults isA/oldResult/othersLF_Last/deuceL.csv",//jvstm
//				"/home/mam2/finalResults isA/oldResult/othersLF_Last/mineL.csv"//LF
		};
		start = new int[]{
				2,
				2,
				2,
				2,
				2,
				2,
				2,
//				58,
//				86,
//				58,
//				86,
//				114
		};
		generateFile(outfilename, files, start,  1000);
		
		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/bank50.csv";
		generateFile(outfilename, files, start,  1000);
		
		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/bank80.csv";
		generateFile(outfilename, files, start,  1000);

		for (int i=0;i<start.length;i++)
			start[i]+=7;
		outfilename = "/home/mam2/bank100.csv";
		generateFile(outfilename, files, start,  1000);
		
		//////////////////////////////////////////////////////////////
		//Vac
		outfilename = "/home/mam2/VacLow.csv";
		files = new String[]{
				"/home/mam2/April6Results/April6_Ring/mineV.csv",
				"/home/mam2/April6Results/April6_Ring/deuceV.csv",
				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineV.csv",
				"/home/mam2/April6Results/April6_TL2/mineV.csv",
				"/home/mam2/April6Results/April6_TL2/deuceV.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceV.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuceV.csv",
		};
		start = new int[]{
				1,
				1,
				1,
				1,
				1,
				1,
				1,
		};
		generateFile(outfilename, files, start,  1);
		
		for (int i=0;i<start.length;i++)
			start[i]+=6;
		outfilename = "/home/mam2/VacHi.csv";
		generateFile(outfilename, files, start,  1);

		//Kmeans
		outfilename = "/home/mam2/KMLow.csv";
		files = new String[]{
				"/home/mam2/April6Results/April6_Ring/mineK.csv",
				"/home/mam2/April6Results/April6_Ring/deuceK.csv",
				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineK.csv",
				"/home/mam2/April6Results/April6_TL2/mineK.csv",
				"/home/mam2/April6Results/April6_TL2/deuceK.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceK.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuceK.csv",
		};
		start = new int[]{
				1,
				1,
				1,
				1,
				1,
				1,
				1,
		};
		generateFile(outfilename, files, start,  1);
		
		for (int i=0;i<start.length;i++)
			start[i]+=6;
		outfilename = "/home/mam2/KMHi.csv";
		generateFile(outfilename, files, start,  1);

		
		//Labyrinth3D
		outfilename = "/home/mam2/3D.csv";
		files = new String[]{
				"/home/mam2/April6Results/April6_Ring/mine3D.csv",
				"/home/mam2/April6Results/April6_Ring/deuce3D.csv",
				"/home/mam2/finalResults isA/oldResult/lost48LSA/mine3D.csv",
				"/home/mam2/April6Results/April6_TL2/mine3D.csv",
				"/home/mam2/April6Results/April6_TL2/deuce3D.csv",
				"/home/mam2/finalResults isA/oldResult/lost48LSA/deuce3D.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuce3D.csv",
		};
		start = new int[]{
				1,
				1,
				1,
				1,
				1,
				1,
				1,
		};
		generateFile(outfilename, files, start,  1);
		
		//Introder
		outfilename = "/home/mam2/Int.csv";
		files = new String[]{
				"/home/mam2/April6Results/April6_Ring/mineI.csv",
				"/home/mam2/April6Results/April6_Ring/deuceI.csv",
				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineI.csv",
				"/home/mam2/April6Results/April6_TL2/mineI.csv",
				"/home/mam2/April6Results/April6_TL2/deuceI.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceI.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuceI.csv",
		};
		start = new int[]{
				1,
				1,
				1,
				1,
				1,
				1,
				1,
		};
		generateFile(outfilename, files, start,  1);
		
		//Matrix
		outfilename = "/home/mam2/Mat.csv";
		files = new String[]{
				"/home/mam2/finalResults isA/oldResult/lost48LSA/mineM.csv",
				"/home/mam2/finalResults isA/newRing/mineM.csv",
				//TODO: 48 in Hash and hash array (remove the repeated)
				"/home/mam2/finalResults isA/newRing/mineM.csv",
//				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineM.csv",
				"/home/mam2/finalResults isA/oldResult/tl2/mineM.csv",
				"/home/mam2/finalResults isA/newTL2Hydra/mineM.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceM.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/mineM.csv",
		};
		start = new int[]{
				1,
				1,
				1,
				1,
				1,
				1,
				1,
		};
		generateFile(outfilename, files, start,  1);
		
		//SSCA2
		outfilename = "/home/mam2/SSCA2.csv";
		files = new String[]{
				//TODO: 48
				"/home/mam2/finalResults isA/oldResult/lost48LSA/mineS.csv",
				//TODO: 48 and Array hash
				"/home/mam2/finalResults isA/newRing/mineS.csv",
//				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineS.csv",
				"/home/mam2/finalResults isA/newRing/mineS.csv", //remove this
				//TODO: better repeat
				"/home/mam2/finalResults isA/oldResult/tl2/mineS.csv",
				//TODO: 32 and 48
				"/home/mam2/finalResults isA/newTL2Hydra/mineS.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceS.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/mineS.csv",
		};
		start = new int[]{
				1,
				1,
				1,
				1,
				1,
				1,
				1,
		};
		generateFile(outfilename, files, start,  1);
		
		//Genome
		outfilename = "/home/mam2/Gen.csv";
		files = new String[]{
				"/home/mam2/April6Results/April6_Ring/mineG.csv",
				"/home/mam2/April6Results/April6_Ring/deuceG.csv",
				"/home/mam2/finalResults isA/hashResult/ring_array_hash_lost48/mineG.csv",
				"/home/mam2/April6Results/April6_TL2/mineG.csv",
				"/home/mam2/April6Results/April6_TL2/deuceG.csv",
				"/home/mam2/finalResults isA/newTL2_LSA Deuce/deuceG.csv",
				"/home/mam2/April6Results/April6OrigTL2/deuceG.csv",
		};
		start = new int[]{
				1,
				1,
				1,
				1,
				1,
				1,
				1,
		};
		generateFileN(outfilename, files, start, 4, 1);
		
		///////////////////////////////////////////////////////////////////////
		//Gnuplot scripts
		String[] resFiles = {
			"LL20.csv",
			"LL50.csv",
			"LL80.csv",
			"LL100.csv",
			
			"SL20.csv",
			"SL50.csv",
			"SL80.csv",
			"SL100.csv",
			
			"RB20.csv",
			"RB50.csv",
			"RB80.csv",
			"RB100.csv",

			"HS20.csv",
			"HS50.csv",
			"HS80.csv",
			"HS100.csv",
			
			"bank20.csv",
			"bank50.csv",
			"bank80.csv",
			"bank100.csv",

			"VacLow.csv",
			"VacHi.csv",
			
			"KMLow.csv",
			"KMHi.csv",
			
			
			"Int.csv",
			
			
			"Gen.csv",

			"3D.csv",

			"Mat.csv",
			
			"SSCA2.csv",
		};
		
		String[] titles = {
			"ByteSTM/RingSTM",
			"Non-VM/RingSTM",
			"HydraTM/RingSTM (Array+Hash)",
			"ByteSTM/TL2",
			"Non-VM/TL2",
			"Deuce/LSA",
			"Deuce/TL2",
			"Object Fabric",
			"Multiverse",
			"DSTM2",
			"JVSTM",
			"Lock-Free"
		};
		
		int[] _mask1 = {
			0,
			1,
			0,
			0,
			1,
			0,
			0,
			0,
			0,
			0,
			0,
			1
		};
		int[] _mask1_ = {
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				0
			};
		int[] mask1__ = {
				1,
				1,
				0,
				1,
				1,
				0,
				1,
				1,
				1,
				1,
				1,
				0
			};

		int[] _mask2 = {
				0,
				1,
				0,
				0,
				1,
				0,
				0,
				1,
				0,
				0,
				0,
				0
			};
		int[] _mask2_ = {
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				0,
				0,
				0,
				0,
				0
			};
		int[] mask2__ = {
				1,
				1,
				0,
				1,
				1,
				0,
				1,
				0,
				0,
				0,
				0,
				0
			};
		int[] _mask3 = {
				1,
				1,
				1,
				1,
				1,
				1,
				1,
				0,
				0,
				0,
				0,
				0
			};
		int[] mask3_ = {
				1,
				1,
				0,
				1,
				1,
				0,
				1,
				0,
				0,
				0,
				0,
				0
			};
		int[] mask4 = {
				1,
				0,
				0,
				1,
				1,
				1,
				1,
				0,
				0,
				0,
				0,
				0
			};		
		int[] _mask5 = {
				1,
				1,
				0,
				1,
				1,
				1,
				1,
				0,
				0,
				0,
				0,
				0
			};		
		int[] mask5_ = {
				1,
				1,
				0,
				1,
				1,
				0,
				1,
				0,
				0,
				0,
				0,
				0
			};		

		StringBuffer command = new StringBuffer();
		command.append("set terminal push\n");
		command.append("set xlabel \"Number of threads\"\n");
		command.append("set ylabel \"Time (ms)\"\n");
//		command.append("set ylabel \"Throughput (transaction/sec)\"\n");
//		command.append("set terminal postscript eps enhanced\n");
		command.append("set terminal postscript eps enhanced color\n");
//		command.append("set term postscript enhanced color \n");
		command.append("set logscale x 2\n");
		command.append("set key left top\n");
		command.append("set pointsize 0.01\n");
		command.append("set style line 1 lt 1 lw 3\n");
		command.append("set style line 2 lt 2 lw 3\n");
		command.append("set style line 3 lt 3 lw 3\n");
		command.append("set style line 4 lt 4 lw 3\n");
		command.append("set style line 5 lt 5 lw 3\n");
		command.append("set style line 6 lt 6 lw 3\n");
		command.append("set style line 7 lt 7 lw 3\n");
		command.append("set style line 8 lt 8 lw 3\n");
		//LL, SL
		//lock free
//		genPlots(resFiles, titles, _mask1, command, 0,8,"");
		
//		genPlots(resFiles, titles, mask1_, command, 0,8,"_");
		
		genPlots(resFiles, titles, mask1__, command, 0,8,"__");

		//RBTree
//		genPlots(resFiles, titles, mask3, command, 8,12,"");
		
		genPlots(resFiles, titles, mask3_, command, 8,12,"_");

		//Hash
		
//		genPlots(resFiles, titles, _mask2, command, 12,16,"");
		
//		genPlots(resFiles, titles, mask2_, command, 12,16,"_");
		
		genPlots(resFiles, titles, mask2__, command, 12,16,"__");

		//Bank, ..., Gen
//		genPlots(resFiles, titles, mask3, command, 16,26,"");
		
		genPlots(resFiles, titles, mask3_, command, 16,26,"_");

		//3d
		genPlots(resFiles, titles, mask3_, command, 26,27,"");
		
		//mat & ssca2
//		genPlots(resFiles, titles, mask5, command, 27,29,"");
//		genPlots(resFiles, titles, mask5_, command, 27,29,"_");
		
		
		
		System.out.println(command.toString());
		
		
		
		
//		String[] gnuplot={
//				"set terminal push",
//				"set xlabel \"Number of threads\"",
//				"set ylabel \"Throughput (transaction/sec)\"",
//				"set terminal postscript eps enhanced",
//				"set logscale x 2",
//				"set output \"/home/mam2/teeest.eps\"",
//				"plot \"/home/mam2/Dropbox/Thesis/Figures/LL100.csv\" using 1:2:3 notitle with yerrorbars,\\",
//				 "\"/home/mam2/Dropbox/Thesis/Figures/LL100.csv\" using 1:2 w lp t \"HydraTM-RingSTM (Array)\",\\",

//				"plot \"/home/mam2/Dropbox/Thesis/Figures/LL100.csv\" using 1:2:3 notitle with yerrorbars,\\",
//				 "\"/home/mam2/Dropbox/Thesis/Figures/LL100.csv\" using 1:2 w lp t \"HydraTM-RingSTM (Array)\",\\",
				 
//				"/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:4:5 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:4 w lp t "HydraTM-RingSTM (Hash)",\
//				"/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:6:7 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:6 w lp t "HydraTM-RingSTM (Array+Hash)",\
//				"/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:8:9 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:8 w lp t "HydraTM-TL2 (Arrays)",\
//				"/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:10:11 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:10 w lp t "HydraTM-TL2 (Hash)",\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:12:13 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:12 w lp t "Deuce-LSA",\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:14:15 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:14 w lp t "Deuce-TL2", \
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:16:17 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:16 w lp t "Object Fabric",\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:18:19 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:18 w lp t "Multiverse",\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:20:21 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:20 w lp t "DSTM2", \
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:22:23 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:22 w lp t "JVSTM",\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:24:25 notitle with yerrorbars,\
//				 "/home/mam2/Dropbox/Thesis/Figures/LL100.csv" using 1:24 w lp t "LockFree"
//				set output
//				set terminal pop

//		};
		
	}

	private static void genPlots(String[] resFiles, String[] titles, int[] mask, StringBuffer command, int start, int end, String extra) {
		for (int i=start;i<end;i++){//LL, SL mask1
			command.append("set output \"/home/mam2/");
			command.append(resFiles[i]+extra+".eps\"\n plot ");
			int col =2;
			int o=0;
			for (int j=0;j<mask.length;j++){
				if (mask[j]>0){
					o++;
					command.append("\"/home/mam2/");
					command.append(resFiles[i]+"\" ");
					command.append(" using 1:");
					command.append(col);
					command.append(":");
					command.append(col+1);
					command.append(" notitle with yerrorbars, ");
					command.append("\"/home/mam2/");
					command.append(resFiles[i]+"\" ");
					command.append(" using 1:");
					command.append(col);
//					command.append(" w lp t \"");
//					command.append(" w lp ps .6 t \"");
					command.append(" w l ls ");
					command.append(o);
					command.append(" t \"");
					command.append(titles[j]);
					command.append("\", ");
				}				
				col+=2;
			}
			command.setLength(command.length()-2);
			command.append("\n");
		}
	}
	
	private static void generateFile(String outfilename, String[] files, int[] start, int scale) {
		String[] avgs = new String[6];
		String[] err = new String[6];
		int[] cores = {2, 4,8,16,32,48};
		StringBuffer[] lines = new StringBuffer[6];
		for (int i=0;i<6;i++)
			lines[i] = new StringBuffer(cores[i]+"\t");
		for (int i=0;i<files.length;i++){
			parseResultsFile(files[i], start[i], 6, avgs, err, scale);
			for (int j=0;j<6;j++){
				lines[j].append(avgs[j]);
				lines[j].append('\t');
				lines[j].append(err[j]);
				lines[j].append('\t');
			}
			
		}
		
		try {
			PrintWriter pw = new PrintWriter(outfilename);
			for (int j=0;j<6;j++)
				pw.println(lines[j]);
			pw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void generateFileN(String outfilename, String[] files, int[] start, int N, int scale) {
		String[] avgs = new String[N];
		String[] err = new String[N];
		int[] cores = {2, 4,8,16,32,48};
		StringBuffer[] lines = new StringBuffer[N];
		for (int i=0;i<N;i++)
			lines[i] = new StringBuffer(cores[i]+"\t");
		for (int i=0;i<files.length;i++){
			parseResultsFile(files[i], start[i], N, avgs, err, scale);
			for (int j=0;j<N;j++){
				lines[j].append(avgs[j]);
				lines[j].append('\t');
				lines[j].append(err[j]);
				lines[j].append('\t');
			}
			
		}
		
		try {
			PrintWriter pw = new PrintWriter(outfilename);
			for (int j=0;j<N;j++)
				pw.println(lines[j]);
			pw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void parseResultsFile(String inFile, int lineNumber, int lineCounts, String[] avgs, String[] error, int scale){
		try {
			BufferedReader in  = new BufferedReader(new FileReader(inFile));
			for (int i=1;i<lineNumber;i++){
				in.readLine();
			}
			for (int i=0;i<lineCounts;i++){
				String str=in.readLine();
				String val[] = str.split(",");
				double sumx=0;
				double sumxx=0;
				int n=0;
				for (int j=1;j<val.length;j++){
					int v = Integer.parseInt(val[j])/scale;
					sumx+=v;
					n++;
				}
				sumx = sumx/n;
				for (int j=1;j<val.length;j++){
					int v = Integer.parseInt(val[j])/scale;
					sumxx+=(v - sumx)*(v - sumx);
				}
				double stddev = Math.sqrt(sumxx/(n-1));
				double err = 2.262*(stddev/Math.sqrt(10));
				avgs[i] = Double.toString(sumx);
				error[i] = Double.toString(err);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
