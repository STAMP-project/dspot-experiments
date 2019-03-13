package hex.word2vec;


import Vec.T_NUM;
import Vec.T_STR;
import Word2Vec.NormModel;
import Word2Vec.WordModel;
import Word2VecModel.AggregateMethod.AVERAGE;
import Word2VecModel.AggregateMethod.NONE;
import Word2VecModel.Word2VecParameters;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.fvec.Vec;
import water.parser.BufferedString;
import water.util.ArrayUtils;


public class Word2VecTest extends TestUtil {
    @Test
    public void testW2V_SG_HSM_small() {
        String[] words = new String[220];
        for (int i = 0; i < 200; i += 2) {
            words[i] = "a";
            words[(i + 1)] = "b";
        }
        for (int i = 200; i < 220; i += 2) {
            words[i] = "a";
            words[(i + 1)] = "c";
        }
        Scope.enter();
        try {
            Vec v = Scope.track(svec(words));
            Frame fr = Scope.track(new Frame(Key.<Frame>make(), new String[]{ "Words" }, new Vec[]{ v }));
            DKV.put(fr);
            Word2VecModel.Word2VecParameters p = new Word2VecModel.Word2VecParameters();
            p._train = fr._key;
            p._min_word_freq = 5;
            p._word_model = WordModel.SkipGram;
            p._norm_model = NormModel.HSM;
            p._vec_size = 10;
            p._window_size = 5;
            p._sent_sample_rate = 0.001F;
            p._init_learning_rate = 0.025F;
            p._epochs = 1;
            Word2VecModel w2vm = ((Word2VecModel) (Scope.track_generic(trainModel().get())));
            Map<String, Float> hm = w2vm.findSynonyms("a", 2);
            logResults(hm);
            Assert.assertEquals(new HashSet<>(Arrays.asList("b", "c")), hm.keySet());
            Vec testWordVec = Scope.track(svec("a", "b", "c", "Unseen", null));
            Frame wv = Scope.track(w2vm.transform(testWordVec, NONE));
            Assert.assertEquals(10, wv.numCols());
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 3; j++)
                    Assert.assertFalse(wv.vec(i).isNA(j));
                // known words

                for (int j = 3; j < 5; j++)
                    Assert.assertTrue(wv.vec(i).isNA(j));
                // unseen & missing words

            }
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testW2V_pretrained() {
        String[] words = new String[1000];
        double[] v1 = new double[words.length];
        double[] v2 = new double[words.length];
        for (int i = 0; i < (words.length); i++) {
            words[i] = "word" + i;
            v1[i] = i / ((float) (words.length));
            v2[i] = 1 - (v1[i]);
        }
        Scope.enter();
        Frame pretrained = new TestFrameBuilder().withName("w2v-pretrained").withColNames("Word", "V1", "V2").withVecTypes(T_STR, T_NUM, T_NUM).withDataForCol(0, words).withDataForCol(1, v1).withDataForCol(2, v2).withChunkLayout(100, 100, 20, 80, 100, 100, 100, 100, 100, 100, 100).build();
        Scope.track(pretrained);
        try {
            Word2VecModel.Word2VecParameters p = new Word2VecModel.Word2VecParameters();
            p._vec_size = 2;
            p._pre_trained = pretrained._key;
            Word2VecModel w2vm = ((Word2VecModel) (Scope.track_generic(trainModel().get())));
            for (int i = 0; i < (words.length); i++) {
                float[] wordVector = w2vm.transform(words[i]);
                Assert.assertArrayEquals(("wordvec " + i), new float[]{ ((float) (v1[i])), ((float) (v2[i])) }, wordVector, 1.0E-4F);
            }
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testW2V_toFrame() {
        Random r = new Random();
        String[] words = new String[1000];
        double[] v1 = new double[words.length];
        double[] v2 = new double[words.length];
        for (int i = 0; i < (words.length); i++) {
            words[i] = "word" + i;
            v1[i] = r.nextDouble();
            v2[i] = r.nextDouble();
        }
        try {
            Scope.enter();
            Frame expected = new TestFrameBuilder().withName("w2v").withColNames("Word", "V1", "V2").withVecTypes(T_STR, T_NUM, T_NUM).withDataForCol(0, words).withDataForCol(1, v1).withDataForCol(2, v2).withChunkLayout(100, 900).build();
            Scope.track(expected);
            Word2VecModel.Word2VecParameters p = new Word2VecModel.Word2VecParameters();
            p._vec_size = 2;
            p._pre_trained = expected._key;
            Word2VecModel w2vm = ((Word2VecModel) (Scope.track_generic(trainModel().get())));
            // convert to a Frame
            Frame result = Scope.track(w2vm.toFrame());
            Assert.assertArrayEquals(expected._names, result._names);
            assertStringVecEquals(expected.vec(0), result.vec(0));
            assertVecEquals(expected.vec(1), result.vec(1), 1.0E-4);
            assertVecEquals(expected.vec(2), result.vec(2), 1.0E-4);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testW2V_SG_HSM() {
        Assume.assumeThat("word2vec test enabled", System.getProperty("testW2V"), CoreMatchers.is(CoreMatchers.notNullValue()));// ignored by default

        Frame fr = parse_test_file("bigdata/laptop/text8.gz", "NA", 0, new byte[]{ Vec.T_STR });
        Word2VecModel w2vm = null;
        try {
            Word2VecModel.Word2VecParameters p = new Word2VecModel.Word2VecParameters();
            p._train = fr._key;
            p._min_word_freq = 5;
            p._word_model = WordModel.SkipGram;
            p._norm_model = NormModel.HSM;
            p._vec_size = 100;
            p._window_size = 4;
            p._sent_sample_rate = 0.001F;
            p._init_learning_rate = 0.025F;
            p._epochs = 10;
            w2vm = trainModel().get();
            Map<String, Float> hm = w2vm.findSynonyms("dog", 20);
            logResults(hm);
            Assert.assertTrue((((hm.containsKey("cat")) || (hm.containsKey("dogs"))) || (hm.containsKey("hound"))));
        } finally {
            fr.remove();
            if (w2vm != null)
                w2vm.delete();

        }
    }

    @Test
    public void testTransformAggregate() {
        Scope.enter();
        try {
            Vec v = Scope.track(svec("a", "b"));
            Frame fr = Scope.track(new Frame(Key.<Frame>make(), new String[]{ "Words" }, new Vec[]{ v }));
            DKV.put(fr);
            // build an arbitrary w2v model & overwrite the learned vector with fixed values
            Word2VecModel.Word2VecParameters p = new Word2VecModel.Word2VecParameters();
            p._train = fr._key;
            p._min_word_freq = 0;
            p._epochs = 1;
            p._vec_size = 2;
            Word2VecModel w2vm = ((Word2VecModel) (Scope.track_generic(trainModel().get())));
            w2vm._output._vecs = new float[]{ 1.0F, 0.0F, 0.0F, 1.0F };
            DKV.put(w2vm);
            String[][] chunks = new String[][]{ new String[]{ "a", "b", null, "a", "c", null, "c", null, "a", "a" }, new String[]{ "a", "b", null }, new String[]{ null, null }, new String[]{ "b", "b", "a" }, new String[]{ "b" }// no terminator at the end
            // no terminator at the end
            // no terminator at the end
             };
            long[] layout = new long[chunks.length];
            String[] sentences = new String[0];
            for (int i = 0; i < (chunks.length); i++) {
                sentences = ArrayUtils.append(sentences, chunks[i]);
                layout[i] = chunks[i].length;
            }
            Frame f = new TestFrameBuilder().withName("data").withColNames("Sentences").withVecTypes(T_STR).withDataForCol(0, sentences).withChunkLayout(layout).build();
            Frame result = Scope.track(w2vm.transform(f.vec(0), AVERAGE));
            Vec expectedAs = Scope.track(dvec(0.5, 1.0, Double.NaN, 0.75, Double.NaN, Double.NaN, 0.25));
            Vec expectedBs = Scope.track(dvec(0.5, 0.0, Double.NaN, 0.25, Double.NaN, Double.NaN, 0.75));
            assertVecEquals(expectedAs, result.vec(w2vm._output._vocab.get(new BufferedString("a"))), 1.0E-4);
            assertVecEquals(expectedBs, result.vec(w2vm._output._vocab.get(new BufferedString("b"))), 1.0E-4);
        } finally {
            Scope.exit();
        }
    }
}

