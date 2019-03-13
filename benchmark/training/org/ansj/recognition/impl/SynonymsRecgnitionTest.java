package org.ansj.recognition.impl;


import SynonymsLibrary.DEFAULT;
import org.ansj.domain.Term;
import org.ansj.library.SynonymsLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;


public class SynonymsRecgnitionTest {
    @Test
    public void test() {
        // ??????????
        SynonymsRecgnition synonymsRecgnition = new SynonymsRecgnition();
        String str = "????????,????";
        for (Term term : ToAnalysis.parse("????????")) {
            System.out.println((((term.getName()) + "\t") + (term.getSynonyms())));
        }
        System.out.println("-------------init library------------------");
        for (Term term : ToAnalysis.parse(str).recognition(synonymsRecgnition)) {
            System.out.println((((term.getName()) + "\t") + (term.getSynonyms())));
        }
        System.out.println("---------------insert----------------");
        SynonymsLibrary.insert(DEFAULT, new String[]{ "??", "??" });
        for (Term term : ToAnalysis.parse(str).recognition(synonymsRecgnition)) {
            System.out.println((((term.getName()) + "\t") + (term.getSynonyms())));
        }
        System.out.println("---------------append----------------");
        SynonymsLibrary.append(DEFAULT, new String[]{ "??", "??", "??" });
        for (Term term : ToAnalysis.parse(str).recognition(synonymsRecgnition)) {
            System.out.println((((term.getName()) + "\t") + (term.getSynonyms())));
        }
        System.out.println("---------------remove----------------");
        SynonymsLibrary.remove(DEFAULT, "??");
        for (Term term : ToAnalysis.parse(str).recognition(synonymsRecgnition)) {
            System.out.println((((term.getName()) + "\t") + (term.getSynonyms())));
        }
    }
}

