package water.rapids;


import org.junit.Assert;
import org.junit.Test;
import water.Key;
import water.Keyed;
import water.TestUtil;
import water.fvec.Frame;


public class RBindTest extends TestUtil {
    @Test
    public void testBasic() {
        Frame fr = null;
        String tree = "(rbind 99 a.hex 98)";
        try {
            fr = checkTree(tree);
            Assert.assertEquals(99, fr.vec(0).at(0), Math.ulp(1));// 1st row 99

            Assert.assertEquals(99, fr.vec(3).at(0), Math.ulp(1));// 1st row 99

            Assert.assertEquals(5.1, fr.vec(0).at(1), Math.ulp(1));// 1st row iris

            Assert.assertEquals(0.2, fr.vec(3).at(1), Math.ulp(1));// 1st row iris

            Assert.assertEquals(5.9, fr.vec(0).at(150), Math.ulp(1));// last row iris

            Assert.assertEquals(1.8, fr.vec(3).at(150), Math.ulp(1));// last row iris

            Assert.assertEquals(98, fr.vec(0).at(151), Math.ulp(1));// last row 98

            Assert.assertEquals(98, fr.vec(3).at(151), Math.ulp(1));// last row 98

        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("a.hex"));
        }
    }

    @Test
    public void testZeroArgs() {
        Frame fr = null;
        String tree = "(rbind )";
        try {
            fr = checkTree(tree);
            Assert.assertEquals(0, fr.numRows());
        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("a.hex"));
        }
    }

    @Test
    public void testScalarsOnly() {
        Frame fr = null;
        String tree = "(rbind 99 98)";
        try {
            fr = checkTree(tree);
            Assert.assertEquals(2, fr.numRows());
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(99, fr.vec(0).at(0), Math.ulp(1));// 1st row 99

            Assert.assertEquals(98, fr.vec(0).at(1), Math.ulp(1));// 2nd row 98

        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("a.hex"));
        }
    }

    @Test
    public void testMulti() {
        Frame fr = null;
        String tree = "(rbind 99 a.hex 98 a.hex 97)";
        try {
            fr = checkTree(tree);
            Assert.assertEquals(99, fr.vec(0).at(0), Math.ulp(1));// 1st row 99

            Assert.assertEquals(99, fr.vec(3).at(0), Math.ulp(1));// 1st row 99

            Assert.assertEquals(5.1, fr.vec(0).at(1), Math.ulp(1));// 1st row iris

            Assert.assertEquals(0.2, fr.vec(3).at(1), Math.ulp(1));// 1st row iris

            Assert.assertEquals(5.9, fr.vec(0).at(150), Math.ulp(1));// last row iris

            Assert.assertEquals(1.8, fr.vec(3).at(150), Math.ulp(1));// last row iris

            Assert.assertEquals(98, fr.vec(0).at(151), Math.ulp(1));// last row 98

            Assert.assertEquals(98, fr.vec(3).at(151), Math.ulp(1));// last row 98

            Assert.assertEquals(5.1, fr.vec(0).at(152), Math.ulp(1));// 1st row iris

            Assert.assertEquals(0.2, fr.vec(3).at(152), Math.ulp(1));// 1st row iris

            Assert.assertEquals(5.9, fr.vec(0).at(301), Math.ulp(1));// last row iris

            Assert.assertEquals(1.8, fr.vec(3).at(301), Math.ulp(1));// last row iris

            Assert.assertEquals(97, fr.vec(0).at(302), Math.ulp(1));// last row 98

            Assert.assertEquals(97, fr.vec(3).at(302), Math.ulp(1));// last row 98

        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("a.hex"));
        }
    }
}

