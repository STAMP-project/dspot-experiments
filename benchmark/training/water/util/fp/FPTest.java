package water.util.fp;


import java.util.Collections;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests set for FP library
 */
public class FPTest {
    @Test
    public void testSome() throws Exception {
        Some<String> sut = new Some("Hello Junit");
        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals("Hello Junit", sut.get());
        Assert.assertEquals("Hello Junit", sut.get());
        Iterator<String> sui1 = sut.iterator();
        Iterator<String> sui2 = sut.iterator();
        Assert.assertTrue(sui1.hasNext());
        Assert.assertTrue(sui1.hasNext());
        Assert.assertTrue(sui2.hasNext());
        Assert.assertTrue(sui2.hasNext());
        Assert.assertEquals("Hello Junit", sui1.next());
        Assert.assertFalse(sui1.hasNext());
        Assert.assertFalse(sui1.hasNext());
        Assert.assertTrue(sui2.hasNext());
        Assert.assertTrue(sui2.hasNext());
        Assert.assertFalse(sut.isEmpty());
        Assert.assertTrue(sut.nonEmpty());
        Assert.assertEquals("Some(3.141592653589793)", Some(Math.PI).toString());
    }

    @Test
    public void testOption() throws Exception {
        Option<String> sut1 = Some("Hello Junit");
        // should not compile
        // sut.get();
        Iterator<String> sui1 = sut1.iterator();
        Assert.assertTrue(sui1.hasNext());
        Assert.assertEquals("Hello Junit", sui1.next());
        Assert.assertFalse(sui1.hasNext());
        Assert.assertFalse(sut1.isEmpty());
        Assert.assertTrue(sut1.nonEmpty());
        Assert.assertTrue(None.isEmpty());
        Assert.assertFalse(None.nonEmpty());
        Assert.assertFalse(None.iterator().hasNext());
        Option<String> sut2 = Option("Hello Junit");
        Assert.assertEquals(sut1, sut2);
        Assert.assertNotEquals(Option("Hello JuniT"), sut1);
        Option<String> sut3 = Option(null);
        Assert.assertEquals(None, sut3);
        Option<Integer> sut4 = sut1.flatMap(new Function<String, Option<Integer>>() {
            public Option<Integer> apply(String s) {
                return Option(((s.length()) - 1));
            }
        });
        Assert.assertEquals(Option(10), sut4);
        Option<?> sute = Option(None);
    }

    @Test
    public void testFlatten() throws Exception {
        Option<String> sut1 = Some("Hello Junit");
        Option<Option<String>> sutopt = Option(sut1);
        Assert.assertEquals(sut1, flatten(sutopt));
        Assert.assertEquals(None, flatten(Option(None)));
        Assert.assertEquals(None, flatten(((Option<Option<Object>>) (None))));
    }

    @Test
    public void testHeadOption() throws Exception {
        Assert.assertEquals(None, headOption(Collections.emptyList()));
        final Option<Double> expected = Some(Math.PI);
        final Option<Double> expected2 = Some(Math.PI);
        Assert.assertTrue(expected.equals(expected2));
        Assert.assertEquals(expected, expected2);
        Assert.assertEquals(expected, headOption(Collections.nCopies(7, Math.PI)));
        Assert.assertEquals(expected, headOption(Collections.nCopies(1, Math.PI)));
        Assert.assertEquals(None, headOption(Collections.nCopies(0, Math.PI)));
    }

    @Test
    public void testHeadOption1() throws Exception {
        Assert.assertEquals(Some(Math.PI), headOption(Collections.nCopies(7, Math.PI).iterator()));
        Assert.assertEquals(Some(Math.PI), headOption(Collections.nCopies(1, Math.PI).iterator()));
        Assert.assertEquals(None, headOption(Collections.nCopies(0, Math.PI).iterator()));
    }
}

