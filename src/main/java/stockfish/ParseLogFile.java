package stockfish;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.io.Files;

import jline.internal.Log;

/**
 */
public class ParseLogFile {

	/**
	 * Method main.
	 * @param args String[]
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		new ParseLogFile();
	}

	public ParseLogFile() throws NumberFormatException, IOException {
		
		Moves moves = new Moves();
		
		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./resources/log.txt"))));
		String line = "";
		
		while((line = bf.readLine()) != null) {
			String results[] = line.split("\\t");
			String[] lines = results[1].split("\\.");
			Move move = new Move(results[0]);
			for(int i = 0 ; i < lines.length ; i++) {
				if(!lines[i].trim().isEmpty() && !lines[i].trim().startsWith("bestmove") && !lines[i].trim().contains("mate 0")) {
					StringTokenizer st = new StringTokenizer(lines[i].trim(), " ");
					int k=0;
					RowLog depth = new RowLog();
					while(st.hasMoreTokens()) {
						String t = st.nextToken();
						switch(k) {
						case 2 : depth.setDepth(Integer.valueOf(t)); break;
						case 4 : depth.setSeldepth(Integer.valueOf(t)); break;
						case 6 : depth.setMultipv(Integer.valueOf(t)); break;
						case 8 : depth.setScoreType(t); break;
						case 9 : depth.setScoreResult(Integer.valueOf(t)); break;
						}
						k++;
					}
					move.add(depth);
				}
			}
			moves.add(move);
		}
		
		Iterator<Move> it = moves.iterator();
		StringBuilder sb = new StringBuilder("FEN;d19;d20\n");
		while(it.hasNext()) {
			Move move = it.next();
			int score19 = move.getBestScore(19).getScoreResult();
			int score20 = move.getBestScore(20).getScoreResult();
			
			sb.append(move.getMove() + ";" + score19 + ";" + score20 + "\n");	
		}
		
		Files.write(sb, new File("resources/d19vsd20.csv"), Charset.defaultCharset());
	}
}


