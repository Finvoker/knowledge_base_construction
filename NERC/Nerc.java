//package lab2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.*;

/** Skeleton for NERC task.
 * 
 * @author Xucheng TANG
 *
 */
public class Nerc {

    /** Labels that we will attach to the words*/
    public enum Class {
        ARTIFACT, EVENT, GEO, NATURAL, ORGANIZATION, PERSON, TIME, OTHER
    }

    /** Determines the class for the word at position 0 in the window*/
    public static Class findClass(Window window) {

        String target = window.getWordAt(0);
        String targettag = window.getTagAt(0);
        String targetsen = window.getSentenceNumberAt(0);
        String pretag = window.getTagAt(-1);
        String presen = window.getSentenceNumberAt(-1);
        String latertag = window.getTagAt(1);
        String latersen = window.getSentenceNumberAt(-1);

        switch (targettag){
            case "NNP":
                if (pretag.equals("DT")|pretag.equals("NNP")&&(latertag.equals("NNP")||pretag.equals("NNS")))
                    return (Class.ORGANIZATION);
                if (pretag.equals("IN")||!presen.equals(targetsen))
                    if (latertag!=("',")||latertag!=("."))
                        return (Class.GEO);
                    else
                        return (Class.TIME);
                if(pretag.equals("VBP"))
                    return (Class.ARTIFACT);
                if(pretag.equals("CD"))
                    return (Class.EVENT);
                if(pretag.equals("LRB")||latertag.equals("RRB"))
                    return (Class.NATURAL);
                if(pretag.equals("CC")||latertag.equals("CC"))
                    return (Class.PERSON);
                if(Pattern.matches("[A-Za-z]+.+",target))
                    return (Class.OTHER);
            case "CD":
                if(Pattern.matches("[0-9]{4}",target))
                    return (Class.TIME);
                else
                    if (!Pattern.matches("[0-9]*.*[0-9]*",target))
                        return (Class.TIME);
            case "JJ":
                if(pretag.equals("JJ"))
                    return (Class.GEO);
            case "RBR":
                if(latertag.equals("IN"))
                    return (Class.TIME);
            case "IN":
                if(pretag.equals("RBR"))
                    return (Class.TIME);
            default:
                return (Class.OTHER);
        }
    }

    /** Takes as arguments:
     * (1) a testing file with sentences
     * (2) optionally: a training file with labeled sentences
     * 
     *  Writes to the file result.tsv lines of the form
     *     X-WORD \t CLASS
     *  where X is a sentence number, WORD is a word, and CLASS is a class.
     */
    public static void main(String[] args) throws IOException {
        //args = new String[] { "/Users/fabian/Data/ner-test.tsv", "/Users/fabian/Data/ner-train.tsv" };

        // EXPERIMENTAL: If you wish, you can train a KNN classifier here
        // on the file args[1].
        // KNN<Nerc.Class> knn = new KNN<>(5);
        // knn.addTrainingExample(Nerc.Class.ARTIFACT, 1, 2, 3);

        try (BufferedWriter out = Files.newBufferedWriter(Paths.get("result.tsv"))) {
            try (BufferedReader in = Files.newBufferedReader(Paths.get(args[0]))) {
                String line;
                Window window = new Window(5);
                while (null != (line = in.readLine())) {
                    window.add(line);
                    if (window.getWordAt(-window.width) == null) continue;
                    Class c = findClass(window);
                    if (c != null && c != Class.OTHER)
                        out.write(window.getSentenceNumberAt(0) + "-" + window.getWordAt(0) + "\t" + c + "\n");
                }
            }
        }
    }
}
