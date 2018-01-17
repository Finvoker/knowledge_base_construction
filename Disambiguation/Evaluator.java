import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Skeleton for an evaluator
 */
public class Evaluator {

	/**
	 * Takes as arguments (1) the gold standard and (2) the output of a program.
	 * Prints to the screen one line with the precision
	 * and one line with the recall.
	 */

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("usage: Evaluator <gold standard> <output> ");
			return;
		}
		File gs = new File(args[0]);
		File res = new File(args[1]);

		SimpleDatabase paires = new SimpleDatabase(gs, res);

		LinkedList matches = new LinkedList();
		LinkedList standards = new LinkedList();

		Iterator gskeyiter = paires.links.keySet().iterator();
		while (gskeyiter.hasNext()){
			String key = gskeyiter.next().toString();
			String match = paires.labels.get(key).toString();
			String standard = paires.links.get(key).toString();
			matches.add(match);
			standards.add(standard);
		}

		int matchcompte = 0;

		//System.out.println("The total number is: "+standards.size());

		for (int i=0;i<matches.size();i++){
			if (matches.get(i).equals(standards.get(i))){
				matchcompte++;
			}
		}

		float prediction = (float)matchcompte/matches.size();
		float recall = (float)matchcompte/standards.size();

		System.out.println("The precision is: "+prediction);
		System.out.println("The recall is: "+recall);

	}

}
