//package lab4;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Takes as argument a Max-Sat-file or a folder of Max-Sat-files, 
 * writes a KB to the corresponding output file(s).
 * Does not take longer than 5 minutes PER PROBLEM.
 */
public class MaxSat {

    /** Start time*/
    public static long startTime;

    /** TRUE if we have to stop*/
    public static boolean haveToStop() {
        return (System.currentTimeMillis() - startTime > 5 * 60 * 1000);
    }


    public static void main(String[] args) throws IOException {
        File argument = new File(args[0]);
        for (File file : argument.isDirectory() ? argument.listFiles() : new File[] { argument }) {
            startTime = System.currentTimeMillis();
            List<Clause> rules = Clause.readFrom(file);
            Set<Atom> bestKB = new HashSet<>();

            // magic goes here
            
            try (Writer out = Files.newBufferedWriter(Paths.get(file.getName().replaceAll("\\.[a-z]+$", ".res")),
                    Charset.forName("UTF-8"))) {
                for (Atom var : bestKB)
                    if (var.isPositive()) out.write(var + "\n");
            }
        }
    }
}
