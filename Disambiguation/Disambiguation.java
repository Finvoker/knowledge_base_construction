//package lab1;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * Skeleton class to perform disambiguation
 * 
 * @author Xucheng TANG
 *
 */
public class Disambiguation {

    /**
     * This program takes 3 command line arguments, namely the paths to:
     * - yagoLinks.tsv
     * - yagoLabels.tsv
     * - wikipedia-ambiguous.txt
     * in this order.
     *
     * The program prints statements of the following form into the file
     * results.tsv:
     *    <pageTitle> TAB <yagoEntity> NEWLINE
     * It is OK to skip articles.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("usage: Disambiguation <yagoLinks> <yagoLabels> <wikiText>");
            return;
        }
        File dblinks = new File(args[0]);
        File dblabels = new File(args[1]);
        File wiki = new File(args[2]);

        SimpleDatabase db = new SimpleDatabase(dblinks, dblabels);

        try (Parser parser = new Parser(wiki)) {
            try (Writer out = new OutputStreamWriter(new FileOutputStream("results.tsv"), "UTF-8")) {
                while (parser.hasNext()) {
                    Page nextPage = parser.next();
                    String pageTitle = nextPage.title; // "Clinton_1"
                    String pageContent = nextPage.content; // "Hillary Clinton was..."
                    String pageLabel = nextPage.label(); // "Clinton"
                    String correspondingYagoEntity = "For you to find";

                    Set<String> tlents = db.reverseLabels.get(pageLabel);//change title label to candidate entities

                    LinkedList ctents = new LinkedList();
                    LinkedList ctlabels = new LinkedList();

                    String sentencelist[] = pageContent.split("\\.");
                    for(String sentence :sentencelist){
                        List<String> words = new ArrayList<String>(Arrays.asList(sentence.split(" ")));
                        words.remove("is");
                        words.remove("a");
                        words.remove("and");
                        words.remove("the");
                        words.remove("an");
                        for(int i=0;i<words.size();i++){
                            ctlabels.add(words.get(i));
                            if (i<words.size()-1) {
                                StringBuilder startword = new StringBuilder().append(words.get(i));
                                for (int j = i+1 ; j < words.size(); j++) {
                                    startword.append(words.get(j));
                                    ctlabels.add(startword.toString());
                                }
                            }
                        }
                    }//put all substrings of content into a set ctlabels<>

                    Iterator labeliter = ctlabels.iterator();
                    while (labeliter.hasNext()){
                        String label = (String)labeliter.next();
                        Set<String> subent = db.reverseLabels.get(label);
                        if (subent!=null){
                            Iterator subiter = subent.iterator();
                            while (subiter.hasNext()) {
                                ctents.add((String) subiter.next());
                            }
                        }
                    }//find entities for content labels

                    ArrayList candidates= new ArrayList();
                    if (tlents!=null){
                        for (String candidate: tlents){
                            candidates.add(candidate);
                        }//get the candidates for title entity

                        int counter[]= new int[candidates.size()];
                        for(int i=0; i<candidates.size();i++){
                            int count = 0;
                            String candidate = (String)candidates.get(i);
                            Iterator potential = ctents.iterator();
                            while(potential.hasNext()){
                                String start = potential.next().toString();
                                Set<String> dest = db.links.get(start);
                                if(dest!=null ){
                                    if (dest.contains(candidate))
                                        count++;
                                }

                            }
                            counter[i]=count;
                        }// count votes for each candidate

                        int desti = 0;
                        for(int i=0;i<counter.length;i++){
                            int maxcount = counter[desti];
                            if (counter[i]>maxcount){
                                desti=i;
                            }
                        }
                        correspondingYagoEntity=(String)candidates.get(desti);
                    }else {
                        correspondingYagoEntity= "Not Found";
                    }
                   //change title set into list
                    out.write(pageTitle + "\t" + correspondingYagoEntity + "\n");
                }
            }
        }
    }
}
