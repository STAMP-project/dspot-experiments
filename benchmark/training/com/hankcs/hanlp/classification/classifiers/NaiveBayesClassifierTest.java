package com.hankcs.hanlp.classification.classifiers;


import com.hankcs.demo.DemoTextClassification;
import com.hankcs.hanlp.classification.models.NaiveBayesModel;
import com.hankcs.hanlp.corpus.io.IOUtil;
import java.util.Map;
import junit.framework.TestCase;


public class NaiveBayesClassifierTest extends TestCase {
    private static final String MODEL_PATH = "data/test/classification.ser";

    private Map<String, String[]> trainingDataSet;

    public void testTrain() throws Exception {
        loadDataSet();
        NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();
        long start = System.currentTimeMillis();
        System.out.println("????...");
        naiveBayesClassifier.train(trainingDataSet);
        System.out.printf("\u8bad\u7ec3\u8017\u65f6\uff1a%d ms\n", ((System.currentTimeMillis()) - start));
        // ?????
        IOUtil.saveObjectTo(naiveBayesClassifier.getNaiveBayesModel(), NaiveBayesClassifierTest.MODEL_PATH);
    }

    public void testPredictAndAccuracy() throws Exception {
        // ????
        NaiveBayesModel model = ((NaiveBayesModel) (IOUtil.readObjectFrom(NaiveBayesClassifierTest.MODEL_PATH)));
        if (model == null) {
            testTrain();
            model = ((NaiveBayesModel) (IOUtil.readObjectFrom(NaiveBayesClassifierTest.MODEL_PATH)));
        }
        NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier(model);
        // ??????
        String path = (DemoTextClassification.CORPUS_FOLDER) + "/??/0004.txt";
        String text = IOUtil.readTxt(path);
        String label = naiveBayesClassifier.classify(text);
        String title = text.split("\\n")[0].replaceAll("\\s", "");
        System.out.printf("\u300a%s\u300b \u5c5e\u4e8e\u5206\u7c7b \u3010%s\u3011\n", title, label);
        text = "2016???????????????8000??";
        title = text;
        label = naiveBayesClassifier.classify(text);
        System.out.printf("\u300a%s\u300b \u5c5e\u4e8e\u5206\u7c7b \u3010%s\u3011\n", title, label);
        text = "??2016?????????????????";
        title = text;
        label = naiveBayesClassifier.classify(text);
        System.out.printf("\u300a%s\u300b \u5c5e\u4e8e\u5206\u7c7b \u3010%s\u3011\n", title, label);
        // ???????????????
        int totalDocuments = 0;
        int rightDocuments = 0;
        loadDataSet();
        long start = System.currentTimeMillis();
        System.out.println("????...");
        for (Map.Entry<String, String[]> entry : trainingDataSet.entrySet()) {
            String category = entry.getKey();
            String[] documents = entry.getValue();
            totalDocuments += documents.length;
            for (String document : documents) {
                if (category.equals(naiveBayesClassifier.classify(document)))
                    ++rightDocuments;

            }
        }
        System.out.printf("\u51c6\u786e\u7387 %d / %d = %.2f%%\n\u901f\u5ea6 %.2f \u6587\u6863/\u79d2", rightDocuments, totalDocuments, ((rightDocuments / ((double) (totalDocuments))) * 100.0), ((totalDocuments / ((double) ((System.currentTimeMillis()) - start))) * 1000.0));
    }

    public void testPredict() throws Exception {
        // ????
        NaiveBayesModel model = ((NaiveBayesModel) (IOUtil.readObjectFrom(NaiveBayesClassifierTest.MODEL_PATH)));
        if (model == null) {
            testTrain();
            model = ((NaiveBayesModel) (IOUtil.readObjectFrom(NaiveBayesClassifierTest.MODEL_PATH)));
        }
        NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier(model);
        Map<String, Double> pMap = naiveBayesClassifier.predict("??2016?????????????????");
        for (Map.Entry<String, Double> entry : pMap.entrySet()) {
            System.out.println(entry);
        }
    }
}

