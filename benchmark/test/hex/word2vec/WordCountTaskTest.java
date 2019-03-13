package hex.word2vec;


import Vec.T_STR;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.fvec.Vec;
import water.parser.BufferedString;
import water.util.IcedLong;


public class WordCountTaskTest extends TestUtil {
    @Test
    public void testWordCount() {
        String[] strData = new String[10000];
        for (int i = 0; i < (strData.length); i++) {
            int b = i % 10;
            if (b < 3)
                strData[i] = "A";
            else
                if (b < 5)
                    strData[i] = "B";
                else
                    strData[i] = "C";


        }
        Frame fr = new TestFrameBuilder().withName("data").withColNames("Str").withVecTypes(T_STR).withDataForCol(0, strData).withChunkLayout(100, 900, 5000, 4000).build();
        try {
            Map<BufferedString, IcedLong> counts = new WordCountTask().doAll(fr.vec(0))._counts;
            Assert.assertEquals(3, counts.size());
            Assert.assertEquals(3000L, counts.get(new BufferedString("A"))._val);
            Assert.assertEquals(2000L, counts.get(new BufferedString("B"))._val);
            Assert.assertEquals(5000L, counts.get(new BufferedString("C"))._val);
            System.out.println(counts);
        } finally {
            fr.remove();
        }
    }

    @Test
    public void testWordCountText8() {
        String fName = "bigdata/laptop/text8.gz";
        Assume.assumeThat("text8 data available", locateFile(fName), CoreMatchers.is(CoreMatchers.notNullValue()));// only run if text8 is present

        Frame fr = parse_test_file(fName, "NA", 0, new byte[]{ Vec.T_STR });
        try {
            Map<BufferedString, IcedLong> counts = new WordCountTask().doAll(fr.vec(0))._counts;
            Assert.assertEquals(253854, counts.size());
            Assert.assertEquals(303L, counts.get(new BufferedString("anarchism"))._val);
            Assert.assertEquals(316376L, counts.get(new BufferedString("to"))._val);
            Assert.assertNotNull(counts);
        } finally {
            fr.remove();
        }
    }
}

