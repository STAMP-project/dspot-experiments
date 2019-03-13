package org.fnlp.demo;


import org.fnlp.demo.ml.HierClassifierUsage1;
import org.fnlp.demo.ml.HierClassifierUsage2;
import org.fnlp.demo.ml.SequenceLabeling;
import org.fnlp.demo.ml.SimpleClassifier2;
import org.junit.Test;


public class MLTest {
    @Test
    public void test() throws Exception {
        SequenceLabeling.main(null);
        SimpleClassifier2.main(null);
        HierClassifierUsage1.main(null);
        HierClassifierUsage2.main(null);
    }
}

