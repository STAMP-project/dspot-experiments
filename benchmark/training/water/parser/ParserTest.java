package water.parser;


import Vec.T_CAT;
import Vec.T_NUM;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.api.schemas3.ParseSetupV3;
import water.util.Log;

import static ParseSetup.GUESS_HEADER;
import static Vec.T_NUM;
import static Vec.T_STR;


public class ParserTest extends TestUtil {
    private final double NaN = Double.NaN;

    private final char[] SEPARATORS = new char[]{ ',', ' ' };

    @Test
    public void testBasic() {
        String[] data = new String[]{ "1|2|3\n1|2|3", "4|5|6", "4|5.2|", "asdf|qwer|1", "1.1", "1.1|2.1|3.4" };
        double[][] exp = new double[][]{ TestUtil.ard(1.0, 2.0, 3.0), TestUtil.ard(1.0, 2.0, 3.0), TestUtil.ard(4.0, 5.0, 6.0), TestUtil.ard(4.0, 5.2, NaN), TestUtil.ard(NaN, NaN, 1.0), TestUtil.ard(1.1, NaN, NaN), TestUtil.ard(1.1, 2.1, 3.4) };
        for (char separator : SEPARATORS) {
            String[] dataset = ParserTest.getDataForSeparator(separator, data);
            StringBuilder sb1 = new StringBuilder();
            for (String ds : dataset)
                sb1.append(ds).append("\n");

            Key k1 = ParserTest.makeByteVec(sb1.toString());
            Key r1 = Key.make("r1");
            ParseDataset.parse(r1, k1);
            ParserTest.testParsed(r1, exp);
            StringBuilder sb2 = new StringBuilder();
            for (String ds : dataset)
                sb2.append(ds).append("\r\n");

            Key k2 = ParserTest.makeByteVec(sb2.toString());
            Key r2 = Key.make("r2");
            ParseDataset.parse(r2, k2);
            ParserTest.testParsed(r2, exp);
        }
    }

    @Test
    public void testMajorityVote() {
        String[] data = new String[]{ "a 0\n", "a 0\n", "a 0\n", "a 0\n", "a 0\n", "a 0\n", "a 0\n", "a 0\n", "a 0\n", "a 0\n", "a 0\n", "1 0\n", "2 0\n", "3 0\n" };
        String[] dataset = ParserTest.getDataForSeparator(' ', data);
        StringBuilder sb1 = new StringBuilder();
        for (String ds : dataset)
            sb1.append(ds).append("\n");

        Key k1 = ParserTest.makeByteVec(sb1.toString());
        Key r1 = Key.make("r1");
        Frame fr = ParseDataset.parse(r1, k1);
        Assert.assertTrue(fr.vec(0).get_type_str().equals("Enum"));
        fr.delete();
    }

    @Test
    public void testChunkBoundaries() {
        String[] data = new String[]{ "1|2|3\n1|2|3\n", "1|2|3\n1|2", "|3\n1|1|1\n", "2|2|2\n2|3|", "4\n3|3|3\n", "3|4|5\n5", ".5|2|3\n5.", "5|2|3\n55e-", "1|2.0|3.0\n55e", "-1|2.0|3.0\n55", "e-1|2.0|3.0\n" };
        double[][] exp = new double[][]{ TestUtil.ard(1.0, 2.0, 3.0), TestUtil.ard(1.0, 2.0, 3.0), TestUtil.ard(1.0, 2.0, 3.0), TestUtil.ard(1.0, 2.0, 3.0), TestUtil.ard(1.0, 1.0, 1.0), TestUtil.ard(2.0, 2.0, 2.0), TestUtil.ard(2.0, 3.0, 4.0), TestUtil.ard(3.0, 3.0, 3.0), TestUtil.ard(3.0, 4.0, 5.0), TestUtil.ard(5.5, 2.0, 3.0), TestUtil.ard(5.5, 2.0, 3.0), TestUtil.ard(5.5, 2.0, 3.0), TestUtil.ard(5.5, 2.0, 3.0), TestUtil.ard(5.5, 2.0, 3.0) };
        for (char separator : SEPARATORS) {
            String[] dataset = ParserTest.getDataForSeparator(separator, data);
            Key k = ParserTest.makeByteVec(dataset);
            Key r3 = Key.make();
            ParseDataset.parse(r3, k);
            ParserTest.testParsed(r3, exp);
        }
    }

    @Test
    public void testChunkBoundariesMixedLineEndings() {
        String[] data = new String[]{ "1|2|3\n4|5|6\n7|8|9", "\r\n10|11|12\n13|14|15", "\n16|17|18\r", "\n19|20|21\n", "22|23|24\n25|26|27\r\n", "28|29|30" };
        double[][] exp = new double[][]{ TestUtil.ard(1, 2, 3), TestUtil.ard(4, 5, 6), TestUtil.ard(7, 8, 9), TestUtil.ard(10, 11, 12), TestUtil.ard(13, 14, 15), TestUtil.ard(16, 17, 18), TestUtil.ard(19, 20, 21), TestUtil.ard(22, 23, 24), TestUtil.ard(25, 26, 27), TestUtil.ard(28, 29, 30) };
        for (char separator : SEPARATORS) {
            String[] dataset = ParserTest.getDataForSeparator(separator, data);
            Key k = ParserTest.makeByteVec(dataset);
            Key r4 = Key.make();
            ParseDataset.parse(r4, k);
            ParserTest.testParsed(r4, exp);
        }
    }

    @Test
    public void testNondecimalColumns() {
        String[] data = new String[]{ "1| 2|one\n" + (((((((("3| 4|two\n" + "5| 6|three\n") + "7| 8|one\n") + "9| 10|two\n") + "11|12|three\n") + "13|14|one\n") + "15|16|\"two\"\n") + "17|18|\" four\"\n") + "19|20| three\n") };
        double[][] expDouble = new double[][]{ TestUtil.ard(1, 2, 1)// preserve order
        , TestUtil.ard(3, 4, 3), TestUtil.ard(5, 6, 2), TestUtil.ard(7, 8, 1), TestUtil.ard(9, 10, 3), TestUtil.ard(11, 12, 2), TestUtil.ard(13, 14, 1), TestUtil.ard(15, 16, 3), TestUtil.ard(17, 18, 0), TestUtil.ard(19, 20, 2) };
        for (char separator : SEPARATORS) {
            String[] dataset = ParserTest.getDataForSeparator(separator, data);
            Key key = ParserTest.makeByteVec(dataset);
            Key r = Key.make();
            ParseDataset.parse(r, key);
            Frame fr = DKV.get(r).get();
            String[] cd = fr.vecs()[2].domain();
            Assert.assertEquals(" four", cd[0]);
            Assert.assertEquals("one", cd[1]);
            Assert.assertEquals("three", cd[2]);
            Assert.assertEquals("two", cd[3]);
            ParserTest.testParsed(r, expDouble);
        }
    }

    @Test
    public void testSingleEntryDatasets() {
        String[] numericDataset = new String[]{ "10.9533122476" };
        Key k1 = ParserTest.makeByteVec(numericDataset);
        Key r1 = Key.make();
        ParseDataset.parse(r1, k1);
        Frame fr1 = DKV.get(r1).get();
        Assert.assertTrue(fr1.vecs()[0].isNumeric());
        Assert.assertTrue(((fr1.numCols()) == 1));
        Assert.assertTrue(((fr1.numRows()) == 1));
        fr1.delete();
        String[] dateDataset = new String[]{ "3-Jan-06" };
        Key k2 = ParserTest.makeByteVec(dateDataset);
        Key r2 = Key.make();
        ParseDataset.parse(r2, k2);
        Frame fr2 = DKV.get(r2).get();
        Assert.assertTrue(fr2.vecs()[0].isTime());
        Assert.assertTrue(((fr2.numCols()) == 1));
        Assert.assertTrue(((fr2.numRows()) == 1));
        fr2.delete();
        String[] UUIDDataset = new String[]{ "9ff4ed3a-6b00-4130-9aca-2ed897305fd1" };
        Key k3 = ParserTest.makeByteVec(UUIDDataset);
        Key r3 = Key.make();
        ParseDataset.parse(r3, k3);
        Frame fr3 = DKV.get(r3).get();
        Assert.assertTrue(((fr3.numCols()) == 1));
        Assert.assertTrue(((fr3.numRows()) == 1));
        Assert.assertTrue(fr3.vecs()[0].isUUID());
        fr3.delete();
        String[] categoricalDataset = new String[]{ "Foo-bar" };
        Key k4 = ParserTest.makeByteVec(categoricalDataset);
        Key r4 = Key.make();
        ParseDataset.parse(r4, k4);
        Frame fr4 = DKV.get(r4).get();
        Assert.assertTrue(((fr4.numCols()) == 1));
        Assert.assertTrue(((fr4.numRows()) == 1));
        Assert.assertTrue(fr4.vecs()[0].isCategorical());
        String[] dom = fr4.vecs()[0].domain();
        Assert.assertTrue(((dom.length) == 1));
        Assert.assertEquals("Foo-bar", dom[0]);
        fr4.delete();
    }

    @Test
    public void testNumberFormats() {
        String[] data = new String[]{ "+.6e102|+.7e102|+.8e102\n.6e102|.7e102|.8e102\n" };
        double[][] expDouble = new double[][]{ TestUtil.ard((+6.0E101), 7.0E101, 8.0E101)// preserve order
        , TestUtil.ard((+6.0E101), (+7.0E101), (+8.0E101)) };
        for (char separator : SEPARATORS) {
            String[] dataset = ParserTest.getDataForSeparator(separator, data);
            Key key = ParserTest.makeByteVec(dataset);
            Key r = Key.make();
            ParseDataset.parse(r, key);
            ParserTest.testParsed(r, expDouble);
        }
    }

    @Test
    public void testMajoritySep() {
        String data = "a,b,c,d,e,f,g,h,i,j,k,space 1,l,space 2,m,space 3,n,o,p,q,r,s,t,u,v,w,x,y,z\n"// 26+3 cols, exactly 3 spaces
         + "1,2,3,4,5,6,7,8,9,0,1,catag 1,2,catag 2,3,catag 3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9\n";// a few extra cols, exactly 3 spaces

        Key k1 = ParserTest.makeByteVec(data);
        Key r1 = Key.make("r1");
        Frame fr = ParseDataset.parse(r1, k1);
        Assert.assertTrue(((fr.numCols()) == (26 + 3)));
        fr.delete();
    }

    @Test
    public void testMultipleNondecimalColumns() {
        String[] data = new String[]{ "foo| 2|one\n" + ((((("bar| 4|two\n" + "foo| 6|three\n") + "bar| 8|one\n") + "bar|ten|two\n") + "bar| 12|three\n") + "foobar|14|one\n") };
        double[][] expDouble = new double[][]{ TestUtil.ard(1, 2, 0)// preserve order
        , TestUtil.ard(0, 4, 2), TestUtil.ard(1, 6, 1), TestUtil.ard(0, 8, 0), TestUtil.ard(0, NaN, 2), TestUtil.ard(0, 12, 1), TestUtil.ard(2, 14, 0) };
        for (char separator : SEPARATORS) {
            String[] dataset = ParserTest.getDataForSeparator(separator, data);
            Key key = ParserTest.makeByteVec(dataset);
            Key r = Key.make();
            ParseDataset.parse(r, key);
            Frame fr = DKV.get(r).get();
            String[] cd = fr.vecs()[2].domain();
            Assert.assertEquals("one", cd[0]);
            Assert.assertEquals("three", cd[1]);
            Assert.assertEquals("two", cd[2]);
            cd = fr.vecs()[0].domain();
            Assert.assertEquals("bar", cd[0]);
            Assert.assertEquals("foo", cd[1]);
            Assert.assertEquals("foobar", cd[2]);
            ParserTest.testParsed(r, expDouble);
        }
    }

    // Test if the empty column is correctly handled.
    // NOTE: this test makes sense only for comma separated columns
    @Test
    public void testEmptyColumnValues() {
        String[] data = new String[]{ "1,2,3,foo\n" + (((((((("4,5,6,bar\n" + "7,,8,\n") + ",9,10\n") + "11,,,\n") + "0,0,0,z\n") + "0,0,0,z\n") + "0,0,0,z\n") + "0,0,0,z\n") + "0,0,0,z\n") };
        double[][] expDouble = new double[][]{ TestUtil.ard(1, 2, 3, 1), TestUtil.ard(4, 5, 6, 0), TestUtil.ard(7, NaN, 8, NaN), TestUtil.ard(NaN, 9, 10, NaN), TestUtil.ard(11, NaN, NaN, NaN), TestUtil.ard(0, 0, 0, 2), TestUtil.ard(0, 0, 0, 2), TestUtil.ard(0, 0, 0, 2), TestUtil.ard(0, 0, 0, 2), TestUtil.ard(0, 0, 0, 2) };
        final char separator = ',';
        String[] dataset = ParserTest.getDataForSeparator(separator, data);
        Key key = ParserTest.makeByteVec(dataset);
        Key r = Key.make();
        ParseDataset.parse(r, key);
        Frame fr = DKV.get(r).get();
        String[] cd = fr.vecs()[3].domain();
        Assert.assertEquals("bar", cd[0]);
        Assert.assertEquals("foo", cd[1]);
        ParserTest.testParsed(r, expDouble);
    }

    @Test
    public void testBasicSpaceAsSeparator() {
        String[] data = new String[]{ " 1|2|3", " 4 | 5 | 6", "4|5.2 ", "asdf|qwer|1", "1.1", "1.1|2.1|3.4" };
        double[][] exp = new double[][]{ TestUtil.ard(1.0, 2.0, 3.0), TestUtil.ard(4.0, 5.0, 6.0), TestUtil.ard(4.0, 5.2, NaN), TestUtil.ard(NaN, NaN, 1.0), TestUtil.ard(1.1, NaN, NaN), TestUtil.ard(1.1, 2.1, 3.4) };
        for (char separator : SEPARATORS) {
            String[] dataset = ParserTest.getDataForSeparator(separator, data);
            StringBuilder sb = new StringBuilder();
            for (String ds : dataset)
                sb.append(ds).append("\n");

            Key k = ParserTest.makeByteVec(sb.toString());
            Key r5 = Key.make();
            ParseDataset.parse(r5, k);
            ParserTest.testParsed(r5, exp);
        }
    }

    @Test
    public void testTimeParse() {
        Frame fr = TestUtil.parse_test_file("smalldata/junit/bestbuy_train_10k.csv.gz");
        fr.delete();
    }

    // TODO Update, originally tested categorical to string conversion
    // TODO now just tests missing values among strings
    @Test
    public void testStrings() {
        Frame fr = null;
        try {
            fr = TestUtil.parse_test_file("smalldata/junit/string_test.csv");
            // check dimensions
            int nlines = ((int) (fr.numRows()));
            Assert.assertEquals(65005, nlines);
            Assert.assertEquals(7, fr.numCols());
            // check column types
            Vec[] vecs = fr.vecs();
            Assert.assertTrue(vecs[0].isString());
            Assert.assertTrue(vecs[1].isString());
            Assert.assertTrue(vecs[2].isString());
            Assert.assertTrue(vecs[3].isString());
            Assert.assertTrue(vecs[4].isString());
            Assert.assertTrue(vecs[5].isString());
            Assert.assertTrue(vecs[6].isString());
            // checks column counts - expects MAX_CATEGORICAL_COUNT == 65000
            // Categorical registration is racy so actual categorical limit can exceed MAX by a few values
            Assert.assertTrue((65003 <= (vecs[0].nzCnt())));// ColV2 A lacks starting values

            Assert.assertTrue((65002 <= (vecs[1].nzCnt())));// ColV2 B has random missing values & dble quotes

            Assert.assertTrue((65005 <= (vecs[2].nzCnt())));// ColV2 C has all values & single quotes

            Assert.assertTrue((65002 <= (vecs[3].nzCnt())));// ColV2 D missing vals just prior to Categorical limit

            Assert.assertTrue((65003 <= (vecs[4].nzCnt())));// ColV2 E missing vals just after Categorical limit hit

            // Assert.assertTrue(65000 <= vecs[5].domain().length); //ColV2 F cardinality just at Categorical limit
            Assert.assertTrue((65003 <= (vecs[6].nzCnt())));// ColV2 G missing final values

            // spot check value parsing
            BufferedString str = new BufferedString();
            Assert.assertEquals("A2", vecs[0].atStr(str, 2).toString());
            Assert.assertEquals("B7", vecs[1].atStr(str, 7).toString());
            Assert.assertEquals("'C65001'", vecs[2].atStr(str, 65001).toString());
            Assert.assertEquals("E65004", vecs[4].atStr(str, 65004).toString());
            Assert.assertNull(vecs[6].atStr(str, 65004));
            fr.delete();
        } finally {
            if (fr != null)
                fr.delete();

        }
    }

    @Test
    public void testMixedSeps() {
        double[][] exp = new double[][]{ TestUtil.ard(NaN, 1, 1), TestUtil.ard(NaN, 2, NaN), TestUtil.ard(3, NaN, 3), TestUtil.ard(4, NaN, NaN), TestUtil.ard(NaN, NaN, NaN), TestUtil.ard(NaN, NaN, NaN), TestUtil.ard(NaN, NaN, 6) };
        Frame fr = TestUtil.parse_test_file("smalldata/junit/is_NA.csv");
        ParserTest.testParsed(fr._key, exp, 25);
    }

    @Test
    public void testMultifileSVMLight() {
        String[] dataset0 = new String[]{ // " 1 2:.2 5:.5 9:.9\n",
        // "-1 7:.7 8:.8 9:.9\n",
        // "+1 1:.1 5:.5 6:.6\n"
        "1 2:.2 5:.5 9:.9\n-1 1:.1 4:.4 8:.8\n", "1 2:.2 5:.5 9:.9\n1 3:.3 6:.6\n", "-1 7:.7 8:.8 9:.9\n1 20:2.\n", "+1 1:.1 5:.5 6:.6 10:1\n1 19:1.9\n", "1 2:.2 5:.5 9:.9\n-1 1:.1 4:.4 8:.8\n", "1 2:.2 5:.5 9:.9\n1 3:.3 6:.6\n", "-1 7:.7 8:.8 9:.9\n1 20:2.\n", "+1 1:.1 5:.5 6:.6 10:1\n1 19:1.9\n", "1 2:.2 5:.5 9:.9\n-1 1:.1 4:.4 8:.8\n", "1 2:.2 5:.5 9:.9\n1 3:.3 6:.6\n", "-1 7:.7 8:.8 9:.9\n1 20:2.\n", "+1 1:.1 5:.5 6:.6 10:1\n1 19:1.9\n" };
        String[] dataset1 = new String[]{ // " 1 2:.2 5:.5 9:.9\n",
        // "-1 7:.7 8:.8 9:.9\n",
        // "+1 1:.1 5:.5 6:.6\n"
        "1 2:.2 5:.5 9:.9\n-1 1:.1 4:.4 8:.8\n", "1 2:.2 5:.5 9:.9\n1 3:.3 6:.6\n", "-1 7:.7 8:.8 9:.9\n1 20:2.\n", "+1 1:.1 5:.5 6:.6 10:1\n1 19:1.9\n", "1 2:.2 5:.5 9:.9\n-1 1:.1 4:.4 8:.8\n", "1 2:.2 5:.5 9:.9\n1 3:.3 6:.6\n", "-1 7:.7 8:.8 9:.9\n1 20:2.\n", "+1 1:.1 5:.5 6:.6 10:1\n1 19:1.9\n", "1 2:.2 5:.5 9:.9\n-1 1:.1 4:.4 8:.8\n", "1 2:.2 5:.5 9:.9\n1 3:.3 6:.6\n", "-1 7:.7 8:.8 9:.9\n1 20:2.\n", "+1 1:.1 5:.5 6:.6 10:1\n1 19:1.9 21:1\n" };
        double[][] exp = new double[][]{ // ard( 1., .0, .2, .0, .0, .5, .0, .0, .0, .9),
        // ard(-1., .0, .0, .0, .0, .0, .0, .7, .8, .9),
        // ard( 1., .1, .0, .0, .0, .5, .6, .0, .0, .0),
        TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.1, 0.0, 0.0, 0.4, 0.0, 0.0, 0.0, 0.8, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.6, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 0.8, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 2.0, 0.0), TestUtil.ard(1.0, 0.1, 0.0, 0.0, 0.0, 0.5, 0.6, 0.0, 0.0, 0.0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.9, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.1, 0.0, 0.0, 0.4, 0.0, 0.0, 0.0, 0.8, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.6, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 0.8, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 2.0, 0.0), TestUtil.ard(1.0, 0.1, 0.0, 0.0, 0.0, 0.5, 0.6, 0.0, 0.0, 0.0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.9, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.1, 0.0, 0.0, 0.4, 0.0, 0.0, 0.0, 0.8, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.6, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 0.8, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 2.0, 0.0), TestUtil.ard(1.0, 0.1, 0.0, 0.0, 0.0, 0.5, 0.6, 0.0, 0.0, 0.0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.9, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.1, 0.0, 0.0, 0.4, 0.0, 0.0, 0.0, 0.8, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.6, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 0.8, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 2.0, 0.0), TestUtil.ard(1.0, 0.1, 0.0, 0.0, 0.0, 0.5, 0.6, 0.0, 0.0, 0.0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.9, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.1, 0.0, 0.0, 0.4, 0.0, 0.0, 0.0, 0.8, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.6, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 0.8, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 2.0, 0.0), TestUtil.ard(1.0, 0.1, 0.0, 0.0, 0.0, 0.5, 0.6, 0.0, 0.0, 0.0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.9, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.1, 0.0, 0.0, 0.4, 0.0, 0.0, 0.0, 0.8, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.6, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard((-1.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 0.8, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 2.0, 0.0), TestUtil.ard(1.0, 0.1, 0.0, 0.0, 0.0, 0.5, 0.6, 0.0, 0.0, 0.0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.9, 0.0, 1.0) };
        StringBuilder sb = new StringBuilder();
        for (String ds : dataset0)
            sb.append(ds).append("\n");

        Key k = ParserTest.makeByteVec(sb.toString());
        Key r1 = Key.make("r1");
        sb = new StringBuilder();
        for (String ds : dataset1)
            sb.append(ds).append("\n");

        Key k2 = ParserTest.makeByteVec(sb.toString());
        ParseDataset.parse(r1, k, k2);
        ParserTest.testParsed(r1, exp);
    }

    @Test
    public void testSVMLight() {
        String[] dataset = new String[]{ // " 1 2:.2 5:.5 9:.9\n",
        // "-1 7:.7 8:.8 9:.9\n",
        // "+1 1:.1 5:.5 6:.6\n"
        "1 2:.2 5:.5 9:.9\n-1 1:.1 4:.4 8:.8\n", "1 2:.2 5:.5 9:.9\n1 3:.3 6:.6\n", "-1 7:.7 8:.8 9:.9\n1 20:2.\n", "+1 1:.1 5:.5 6:.6 10:1\n1 19:1.9\n", "1 2:.2 5:.5 9:.9\n-1 1:.1 4:.4 8:.8\n", "1 2:.2 5:.5 9:.9\n1 3:.3 6:.6\n", "-1 7:.7 8:.8 9:.9\n1 20:2.\n", "+1 1:.1 5:.5 6:.6 10:1\n1 19:1.9\n", "1 2:.2 5:.5 9:.9\n-1 1:.1 4:.4 8:.8\n", "1 2:.2 5:.5 9:.9\n1 3:.3 6:.6\n", "-1 7:.7 8:.8 9:.9\n1 20:2.\n", "+1 1:.1 5:.5 6:.6 10:1\n1 19:1.9\n" };
        double[][] exp = new double[][]{ // ard( 1., .0, .2, .0, .0, .5, .0, .0, .0, .9),
        // ard(-1., .0, .0, .0, .0, .0, .0, .7, .8, .9),
        // ard( 1., .1, .0, .0, .0, .5, .6, .0, .0, .0),
        TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard((-1.0), 0.1, 0.0, 0.0, 0.4, 0.0, 0.0, 0.0, 0.8, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.6, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard((-1.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 0.8, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 2.0), TestUtil.ard(1.0, 0.1, 0.0, 0.0, 0.0, 0.5, 0.6, 0.0, 0.0, 0.0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.9, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard((-1.0), 0.1, 0.0, 0.0, 0.4, 0.0, 0.0, 0.0, 0.8, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.6, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard((-1.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 0.8, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 2.0), TestUtil.ard(1.0, 0.1, 0.0, 0.0, 0.0, 0.5, 0.6, 0.0, 0.0, 0.0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.9, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard((-1.0), 0.1, 0.0, 0.0, 0.4, 0.0, 0.0, 0.0, 0.8, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.2, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.6, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard((-1.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 0.8, 0.9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 2.0), TestUtil.ard(1.0, 0.1, 0.0, 0.0, 0.0, 0.5, 0.6, 0.0, 0.0, 0.0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0), TestUtil.ard(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.9, 0.0) };
        StringBuilder sb = new StringBuilder();
        for (String ds : dataset)
            sb.append(ds).append("\n");

        Key k = ParserTest.makeByteVec(sb.toString());
        Key r1 = Key.make("r1");
        ParseDataset.parse(r1, k);
        ParserTest.testParsed(r1, exp);
    }

    // Mix of NA's, very large & very small, ^A Hive-style seperator, comments, labels
    @Test
    public void testParseMix() {
        double[][] exp = new double[][]{ TestUtil.ard(0, 0.5, 1, 0), TestUtil.ard(3, NaN, 4, 1), TestUtil.ard(6, NaN, 8, 0), TestUtil.ard(0.6, 0.7, 0.8, 1), TestUtil.ard((+0.6), (+0.7), (+0.8), 0), TestUtil.ard((-0.6), (-0.7), (-0.8), 1), TestUtil.ard(0.6, 0.7, 0.8, 0), TestUtil.ard((+0.6), (+0.7), (+0.8), 1), TestUtil.ard((-0.6), (-0.7), (-0.8), 0), TestUtil.ard((+0.6), (+0.7), (+0.8), 1), TestUtil.ard((-0.6), (-0.7), (-0.8), 0), TestUtil.ard(0.6, 0.7, 0.8, 1), TestUtil.ard((+0.6), (+0.7), (+0.8), 0), TestUtil.ard((-0.6), (-0.7), (-0.8), 1), TestUtil.ard((+0.6), (+0.7), (+0.8), 0), TestUtil.ard((-0.6), (-0.7), (-0.8), 1), TestUtil.ard(0.6, 0.7, 0.8, 0), TestUtil.ard((+0.6), (+0.7), (+0.8), 1), TestUtil.ard((-0.6), (-0.7), (-0.8), 0), TestUtil.ard((+0.06), (+0.07), (+0.08), 1), TestUtil.ard((-0.06), (-0.07), (-0.08), 0), TestUtil.ard(0.06, 0.07, 0.08, 1), TestUtil.ard((+0.06), (+0.07), (+0.08), 0), TestUtil.ard((-0.06), (-0.07), (-0.08), 1), TestUtil.ard((+6.0), (+7.0), (+8.0), 0), TestUtil.ard((-6.0), (-7.0), (-8.0), 1), TestUtil.ard(6.0, 7.0, 8.0, 0), TestUtil.ard((+6.0), (+7.0), (+8.0), 1), TestUtil.ard((-6.0), (-7.0), (-8.0), 0), TestUtil.ard((+6.0E101), (+7.0E101), (+8.0E101), 1), TestUtil.ard((-6.0E101), (-7.0E101), (-8.0E101), 0), TestUtil.ard(6.0E101, 7.0E101, 8.0E101, 1), TestUtil.ard((+6.0E101), (+7.0E101), (+8.0E101), 0), TestUtil.ard((-6.0E101), (-7.0E101), (-8.0E101), 1) };
        Frame fr = TestUtil.parse_test_file("smalldata/junit/test_parse_mix.csv");
        ParserTest.testParsed(fr._key, exp);
    }

    // Test of parsing numbers with many digits
    @Test
    public void testParseManyDigits1() {
        String pows10 = "1\n" + (((((((((((((((((((((("10\n" + "100\n") + "1000\n") + "10000\n") + "100000\n") + "1000000\n") + "10000000\n") + "100000000\n") + "1000000000\n") + "10000000000\n") + "100000000000\n") + "1000000000000\n") + "10000000000000\n") + "100000000000000\n") + "1000000000000000\n") + "10000000000000000\n") + "100000000000000000\n") + "1000000000000000000\n") + "10000000000000000000\n") + "100000000000000000000\n") + "1000000000000000000000\n") + "10000000000000000000000\n") + "100000000000000000000000\n");
        double[][] pows10_exp = new double[][]{ TestUtil.ard(1.0), TestUtil.ard(10.0), TestUtil.ard(100.0), TestUtil.ard(1000.0), TestUtil.ard(10000.0), TestUtil.ard(100000.0), TestUtil.ard(1000000.0), TestUtil.ard(1.0E7), TestUtil.ard(1.0E8), TestUtil.ard(1.0E9), TestUtil.ard(1.0E10), TestUtil.ard(1.0E11), TestUtil.ard(1.0E12), TestUtil.ard(1.0E13), TestUtil.ard(1.0E14), TestUtil.ard(1.0E15), TestUtil.ard(1.0E16), TestUtil.ard(1.0E17), TestUtil.ard(1.0E18), TestUtil.ard(1.0E19), TestUtil.ard(1.0E20), TestUtil.ard(1.0E21), TestUtil.ard(1.0E22), TestUtil.ard(9.999999999999999E22) };
        Key k = ParserTest.makeByteVec(pows10);
        Key r1 = Key.make("r1");
        ParseDataset.parse(r1, k);
        ParserTest.testParsed(r1, pows10_exp);
    }

    // Test of parsing numbers with many digits
    @Test
    public void testParseManyDigits2() {
        String pows10 = "9\n" + (((((((((((((((((((((("99\n" + "999\n") + "9999\n") + "99999\n") + "999999\n") + "9999999\n") + "99999999\n") + "999999999\n") + "9999999999\n") + "99999999999\n") + "999999999999\n") + "9999999999999\n") + "99999999999999\n") + "999999999999999\n") + "9999999999999999\n") + "99999999999999999\n") + "999999999999999999\n") + "9999999999999999999\n") + "99999999999999999999\n") + "999999999999999999999\n") + "9999999999999999999999\n") + "99999999999999999999999\n") + "999999999999999999999999\n");
        double[][] pows10_exp = new double[][]{ TestUtil.ard(9L), TestUtil.ard(99L), TestUtil.ard(999L), TestUtil.ard(9999L), TestUtil.ard(99999L), TestUtil.ard(999999L), TestUtil.ard(9999999L), TestUtil.ard(99999999L), TestUtil.ard(999999999L), TestUtil.ard(9999999999L), TestUtil.ard(99999999999L), TestUtil.ard(999999999999L), TestUtil.ard(9999999999999L), TestUtil.ard(99999999999999L), TestUtil.ard(999999999999999L), TestUtil.ard(9999999999999999L), TestUtil.ard(99999999999999999L), TestUtil.ard(999999999999999999L), TestUtil.ard(1.0E19), TestUtil.ard(1.0E20), TestUtil.ard(1.0E21), TestUtil.ard(1.0E22), TestUtil.ard(9.999999999999999E22), TestUtil.ard(1.0E24) };
        Key k = ParserTest.makeByteVec(pows10);
        Key r1 = Key.make("r1");
        ParseDataset.parse(r1, k);
        ParserTest.testParsed(r1, pows10_exp);
    }

    // Test of parsing numbers with many digits
    @Test
    public void testParseManyDigits3() {
        String pows10 = "0.00000000000001\n" + (("1000001\n" + "2000001\n") + "");
        double[][] pows10_exp = new double[][]{ TestUtil.ard(1.0E-14), TestUtil.ard(1000001L), TestUtil.ard(2000001L) };
        Key k = ParserTest.makeByteVec(pows10);
        Key r1 = Key.make("r1");
        ParseDataset.parse(r1, k);
        ParserTest.testParsed(r1, pows10_exp);
    }

    // Test of parsing numbers with many digits
    @Test
    public void testParseManyDigits4() {
        String pows10 = "3\n" + (("1e-18\n" + "1e-34\n") + "");
        double[][] pows10_exp = new double[][]{ TestUtil.ard(3), TestUtil.ard(1.0E-18), TestUtil.ard(1.0E-34) };
        Key k = ParserTest.makeByteVec(pows10);
        Key r1 = Key.make("r1");
        ParseDataset.parse(r1, k);
        ParserTest.testParsed(r1, pows10_exp);
    }

    // Comparing a column with just 1399008600149269883L and 1399008600149269880L
    // (differ by 3 in the lowest digit), converting to a double or scaling up
    // and down by 10 and converting inverts the size:
    // ((double)1399008600149269883L) < ((double)139900860014926988L*10.0)
    // i.e., the larger long converts to the smaller double.
    @Test
    public void testParseManyDigits5() {
        String pows10 = "1399008600149269883\n" + ("1399008600149269880\n" + "");
        double[][] pows10_exp = new double[][]{ TestUtil.ard(1399008600149269883L), TestUtil.ard(1399008600149269880L) };
        Key k = ParserTest.makeByteVec(pows10);
        Key r1 = Key.make("r1");
        ParseDataset.parse(r1, k);
        ParserTest.testParsed(r1, pows10_exp);
    }

    @Test
    public void testParseAll() {
        String[] files = new String[]{ "smalldata/./airlines/allyears2k_headers.zip", "smalldata/./covtype/covtype.20k.data", "smalldata/./iris/iris.csv", "smalldata/./iris/iris_wheader.csv", "smalldata/./junit/benign.xls", "smalldata/./junit/bestbuy_train_10k.csv.gz", "smalldata/./junit/cars.csv", "smalldata/./junit/iris.csv", "smalldata/./junit/iris.csv.gz", "smalldata/./junit/iris.csv.zip", "smalldata/./junit/iris.xls", "smalldata/./junit/is_NA.csv", "smalldata/./junit/one-line-dataset-0.csv", "smalldata/./junit/one-line-dataset-1dos.csv", "smalldata/./junit/one-line-dataset-1unix.csv", "smalldata/./junit/one-line-dataset-2dos.csv", "smalldata/./junit/one-line-dataset-2unix.csv", "smalldata/./junit/parse_folder/prostate_0.csv", "smalldata/./junit/parse_folder/prostate_1.csv", "smalldata/./junit/parse_folder/prostate_2.csv", "smalldata/./junit/parse_folder/prostate_3.csv", "smalldata/./junit/parse_folder/prostate_4.csv", "smalldata/./junit/parse_folder/prostate_5.csv", "smalldata/./junit/parse_folder/prostate_6.csv", "smalldata/./junit/parse_folder/prostate_7.csv", "smalldata/./junit/parse_folder/prostate_8.csv", "smalldata/./junit/parse_folder/prostate_9.csv", "smalldata/./junit/parse_folder_gold.csv", "smalldata/./junit/pros.xls", "smalldata/./junit/syn_2659x1049.csv.gz", "smalldata/./junit/test_parse_mix.csv", "smalldata/./junit/test_quote.csv", "smalldata/./junit/test_time.csv", "smalldata/./junit/test_uuid.csv", "smalldata/./junit/time.csv", "smalldata/./junit/two-lines-dataset.csv", "smalldata/./junit/ven-11.csv", "smalldata/./logreg/prostate.csv" };
        for (String f : files) {
            for (boolean delete_on_done : // false
            new boolean[]{ true }// false
            ) {
                for (int check_header : // ParseSetup.HAS_HEADER
                new int[]{ GUESS_HEADER }// ParseSetup.HAS_HEADER
                ) {
                    try {
                        Log.info(("Trying to parse " + f));
                        NFSFileVec nfs = TestUtil.makeNfsFileVec(f);
                        Frame fr = /* single quote */
                        ParseDataset.parse(Key.make(), new Key[]{ nfs._key }, delete_on_done, true, check_header);
                        fr.delete();
                    } catch (Throwable t) {
                        throw Log.throwErr(t);
                    }
                }
            }
        }
    }

    @Test
    public void testPubDev2897() {
        Frame f = TestUtil.parse_test_file("smalldata/jira/pubdev_2897.csv");
        try {
            Assert.assertEquals("Frame rows", 5, f.numRows());
            Assert.assertEquals("Frame columns", 3, f.numCols());
            Vec v0 = f.vec(0);
            Assert.assertEquals("1. Column type", T_NUM, v0.get_type());
            Vec v1 = f.vec(1);
            Assert.assertEquals("2. Column type", T_NUM, v1.get_type());
            Vec v2 = f.vec(2);
            Assert.assertEquals("3. Column type", T_CAT, v2.get_type());
            Assert.assertArrayEquals("3. Column domain", TestUtil.ar("A", "B"), v2.domain());
            int domainLen = v2.domain().length;
            // Verify values in columns
            for (int i = 0; i < (f.numRows()); i++) {
                if (v2.isNA(i))
                    continue;

                long value = v2.at8(i);
                Assert.assertTrue("Vector value should reference a string inside domain", ((value >= 0) && (value < domainLen)));
            }
        } finally {
            f.delete();
        }
    }

    @Test
    public void testParserRespectsSpecifiedColNum() {
        Vec fv = TestUtil.makeNfsFileVec("smalldata/jira/runit_pubdev_3590_unexpected_column.csv");
        Key fkey = Key.make("data4cols");
        try {
            Key[] keys = new Key[]{ fv._key };
            ParseSetup guessedSetup = ParseSetup.guessSetup(keys, false, 1);
            Assert.assertEquals(guessedSetup._number_columns, 2);
            Assert.assertEquals(0, guessedSetup.errs().length);
            guessedSetup._column_names = new String[]{ "c1", "c2", "c3", "c4" };
            guessedSetup._column_types = new byte[]{ T_NUM, T_STR, T_NUM, T_STR };
            guessedSetup._number_columns = 4;
            Frame f = ParseDataset.parse(fkey, keys, true, guessedSetup);
            Assert.assertEquals(4, f.numCols());
            // last two columns have values only on line 4, rest of the values are NAs
            Assert.assertEquals(5, f.vec(2).at8(3));
            Assert.assertEquals(5, f.vec(2).naCnt());
            Assert.assertEquals("e", String.valueOf(f.vec(3).atStr(new BufferedString(), 3)));
            Assert.assertEquals(5, f.vec(3).naCnt());
        } finally {
            fkey.remove();
            fv.remove();
        }
    }

    /**
     * PUBDEV-3401: Multi-file parse fails when streamParseZip produces less chunks than expected
     * This test makes sure that streamParseZip will create expected number of output chunks even if
     * the chunk size is perfectly aligned with the underlying buffers and rollover to the next input
     * chunk happens in a call to get number of available bytes (is.available()).
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testStreamParsePubDev3401() throws IOException {
        final int chunkSize = 64 * 1024;
        ParseSetupV3 ps = new ParseSetupV3();
        ps.parse_type = "CSV";
        ps.chunk_size = chunkSize;
        // mock Parser
        final List<Integer> pcCalls = new LinkedList<>();
        Parser p = new Parser(new ParseSetup(ps), null) {
            @Override
            protected ParseWriter parseChunk(int cidx, ParseReader din, ParseWriter dout) {
                pcCalls.add(cidx);
                byte[] data = din.getChunkData(cidx);
                assert cidx <= 3;
                assert ((data != null) && ((data.length) == chunkSize)) || (cidx == 3);
                return null;
            }
        };
        // mock source InputStream, only used for "back-channel" chunk index signalling
        final Queue<Integer> cidxs = new LinkedList<>(Arrays.asList(1, 2, 3, 4));
        InputStream bvs = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                assert b == null;
                return cidxs.remove();
            }
        };
        // mock StreamParseWriter
        ParserTest.MockStreamParseWriter w = new ParserTest.MockStreamParseWriter(chunkSize);
        // Vec made of 3 chunks
        Key k = ParserTest.makeByteVec(RandomStringUtils.randomAscii(chunkSize), RandomStringUtils.randomAscii(chunkSize), RandomStringUtils.randomAscii(chunkSize));
        try {
            ByteVec v = DKV.getGet(k);
            p.streamParseZip(v.openStream(null), w, bvs);
            Assert.assertEquals("Expected calls to parseChunk()", Arrays.asList(0, 1, 2, 3), pcCalls);
            Assert.assertEquals("Expected calls to nextChunk()", Arrays.asList(0, 1, 2), w._nchks);
        } finally {
            k.remove();
        }
    }

    private static class MockStreamParseWriter extends FVecParseWriter {
        final List<Integer> _nchks;

        MockStreamParseWriter(int chunkSize) {
            super(null, 0, null, null, chunkSize, new AppendableVec[0]);
            _nchks = new LinkedList<>();
        }

        MockStreamParseWriter(ParserTest.MockStreamParseWriter prev) {
            super(null, ((prev._cidx) + 1), null, null, prev._chunkSize, new AppendableVec[0]);
            _nchks = prev._nchks;
        }

        @Override
        public FVecParseWriter nextChunk() {
            _nchks.add(_cidx);
            return new ParserTest.MockStreamParseWriter(this);
        }

        @Override
        public FVecParseWriter reduce(StreamParseWriter sdout) {
            return this;
        }

        @Override
        public FVecParseWriter close(Futures fs) {
            return this;
        }
    }
}

