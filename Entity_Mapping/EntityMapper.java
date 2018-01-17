//package lab5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Skeleton class for a program that maps the entities from one KB to the
 * entities of another KB.
 * 
 * @author Xucheng TANG
 *
 */
public class EntityMapper {

    /**
     * Takes as input (1) one knowledge base (2) another knowledge base.
     * 
     * Prints "entity1 TAB entity2 NEWLINE" to the file results.tsv, if the first
     * entity from the first knowledge base is the same as the second entity
     * from the second knowledge base. Output 0 or 1 line per entity1.
     */
    public static void main(String[] args) throws IOException {
        KnowledgeBase kb1 = new KnowledgeBase(new File(args[0]));
        KnowledgeBase kb2 = new KnowledgeBase(new File(args[1]));
        try (Writer out = new OutputStreamWriter(new FileOutputStream("results.tsv"), "UTF-8")) {
            for (String entity1 : kb1.facts.keySet()) {
                String mostLikelyCandidate = null;

                ArrayList table1 = new ArrayList(0);
                for (String relation : kb1.facts.get(entity1).keySet()){
                    //table1.add(relation);
                    table1.add(kb1.facts.get(entity1).get(relation));
                }
                /** get all relations and their values in a array table1 */
                Map<String, Integer> stringsCount = new HashMap<>();

                int i = 0;
                while (i<table1.size()){
//                    String currela = table1.get(i).toString();
//                    i++;
                    String curvalue = table1.get(i).toString();
                    /** parse through each relation and check in kb2*/

                    for (String entity2 :kb2.facts.keySet()){
                        for (String relation : kb2.facts.get(entity2).keySet()){
                            String value2 = kb2.facts.get(entity2).get(relation).toString();
                            if(curvalue.equals(value2)){
                                Integer c = stringsCount.get(entity2);
                                if(c == null) c = new Integer(0);
                                c++;
                                stringsCount.put(entity2,c);
                            }
                        }
                    }
                    /** adding entities into candidates*/
                    i++;
                }

                /** max sequence*/
                Map.Entry<String,Integer> mostRepeated = null;
                for(Map.Entry<String, Integer> e: stringsCount.entrySet())
                {
                    if(mostRepeated == null || mostRepeated.getValue()<e.getValue())
                        mostRepeated = e;
                }
                if(mostRepeated !=null){
                    mostLikelyCandidate = mostRepeated.getKey();
                    //System.out.println(mostRepeated.getKey());
                }

                if (mostLikelyCandidate != null) {
                    out.write(entity1 + "\t" + mostLikelyCandidate + "\n");
                }
            }
        }
    }
}
