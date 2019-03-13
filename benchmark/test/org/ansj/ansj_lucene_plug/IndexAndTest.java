package org.ansj.ansj_lucene_plug;


import DicLibrary.DEFAULT;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.lucene6.AnsjAnalyzer.TYPE;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;


public class IndexAndTest {
    @Test
    public void test() throws Exception {
        DicLibrary.put(DEFAULT, "../../library/default.dic");
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new org.ansj.lucene6.AnsjAnalyzer(TYPE.index_ansj));
        Directory directory = null;
        IndexWriter iwriter = null;
        IndexWriterConfig ic = new IndexWriterConfig(analyzer);
        String text = "?????????";
        System.out.println(IndexAnalysis.parse(text));
        // ????????
        directory = new RAMDirectory();
        iwriter = new IndexWriter(directory, ic);
        addContent(iwriter, text);
        iwriter.commit();
        iwriter.close();
        System.out.println("??????");
        Analyzer queryAnalyzer = new org.ansj.lucene6.AnsjAnalyzer(TYPE.index_ansj);
        System.out.println("index ok to search!");
        for (Term t : IndexAnalysis.parse(text)) {
            System.out.println(t.getName());
            search(queryAnalyzer, directory, (("\"" + (t.getName())) + "\""));
        }
    }
}

