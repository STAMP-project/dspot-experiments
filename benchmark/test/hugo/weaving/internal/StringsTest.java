package hugo.weaving.internal;


import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;


public final class StringsTest {
    @Test
    public void nullValue() {
        Assert.assertEquals("null", Strings.toString(null));
    }

    @Test
    public void string() {
        Assert.assertEquals("\"String\"", Strings.toString("String"));
    }

    @Test
    public void unprintableCharacters() {
        Assert.assertEquals("\"Str\\ning\"", Strings.toString("Str\ning"));
        Assert.assertEquals("\"\\n\\r\\t\\f\\b\\u202C\"", Strings.toString("\n\r\t\f\b\u202c"));
    }

    @Test
    public void objects() {
        Assert.assertEquals("1", Strings.toString(new BigInteger("1")));
    }

    @Test
    public void byteValue() {
        byte primitive = ((byte) (171));
        Assert.assertEquals("0xAB", Strings.toString(primitive));
        Byte boxed = primitive;
        Assert.assertEquals("0xAB", Strings.toString(boxed));
    }

    @Test
    public void byteArrays() {
        byte[] primitive = new byte[]{ ((byte) (171)), ((byte) (188)), ((byte) (205)), ((byte) (222)), ((byte) (239)) };
        Assert.assertEquals("[0xAB, 0xBC, 0xCD, 0xDE, 0xEF]", Strings.toString(primitive));
        Byte[] boxed = new Byte[]{ ((byte) (171)), ((byte) (188)), null, ((byte) (222)), ((byte) (239)) };
        Assert.assertEquals("[0xAB, 0xBC, null, 0xDE, 0xEF]", Strings.toString(boxed));
    }

    @Test
    public void shortArrays() {
        short[] primitive = new short[]{ 1, 2, 3, 4, 5 };
        Assert.assertEquals("[1, 2, 3, 4, 5]", Strings.toString(primitive));
        Short[] boxed = new Short[]{ 1, 2, null, 4, 5 };
        Assert.assertEquals("[1, 2, null, 4, 5]", Strings.toString(boxed));
    }

    @Test
    public void charArrays() {
        char[] primitive = new char[]{ 'a', 'b', 'c', 'd', 'e' };
        Assert.assertEquals("[a, b, c, d, e]", Strings.toString(primitive));
        Character[] boxed = new Character[]{ 'a', 'b', null, 'd', 'e' };
        Assert.assertEquals("[a, b, null, d, e]", Strings.toString(boxed));
    }

    @Test
    public void intArrays() {
        int[] primitive = new int[]{ 1, 2, 3, 4, 5 };
        Assert.assertEquals("[1, 2, 3, 4, 5]", Strings.toString(primitive));
        Integer[] boxed = new Integer[]{ 1, 2, null, 4, 5 };
        Assert.assertEquals("[1, 2, null, 4, 5]", Strings.toString(boxed));
    }

    @Test
    public void longArrays() {
        long[] primitive = new long[]{ 1, 2, 3, 4, 5 };
        Assert.assertEquals("[1, 2, 3, 4, 5]", Strings.toString(primitive));
        Long[] boxed = new Long[]{ 1L, 2L, null, 4L, 5L };
        Assert.assertEquals("[1, 2, null, 4, 5]", Strings.toString(boxed));
    }

    @Test
    public void floatArrays() {
        float[] primitive = new float[]{ 1.1F, 2.2F, 3.3F, 4.4F, 5.5F };
        Assert.assertEquals("[1.1, 2.2, 3.3, 4.4, 5.5]", Strings.toString(primitive));
        Float[] boxed = new Float[]{ 1.1F, 2.2F, null, 4.4F, 5.5F };
        Assert.assertEquals("[1.1, 2.2, null, 4.4, 5.5]", Strings.toString(boxed));
    }

    @Test
    public void doubleArrays() {
        double[] primitive = new double[]{ 1.1, 2.2, 3.3, 4.4, 5.5 };
        Assert.assertEquals("[1.1, 2.2, 3.3, 4.4, 5.5]", Strings.toString(primitive));
        Double[] boxed = new Double[]{ 1.1, 2.2, null, 4.4, 5.5 };
        Assert.assertEquals("[1.1, 2.2, null, 4.4, 5.5]", Strings.toString(boxed));
    }

    @Test
    public void booleanArrays() {
        boolean[] primitive = new boolean[]{ true, false, true, false, true };
        Assert.assertEquals("[true, false, true, false, true]", Strings.toString(primitive));
        Boolean[] boxed = new Boolean[]{ true, false, null, false, true };
        Assert.assertEquals("[true, false, null, false, true]", Strings.toString(boxed));
    }

    @Test
    public void objectArray() {
        Object[] array = new Object[]{ 1, true, "String", 1.1F, null, new BigInteger("1") };
        Assert.assertEquals("[1, true, \"String\", 1.1, null, 1]", Strings.toString(array));
    }

    @Test
    public void deepObjectArray() {
        Object[] array = new Object[]{ 1, true, "String", new Object[]{ 1.1F, "Nested" } };
        Assert.assertEquals("[1, true, \"String\", [1.1, \"Nested\"]]", Strings.toString(array));
    }

    @Test
    public void recursiveObjectArray() {
        Object[] array = new Object[]{ 1, 2, 3, null };
        array[3] = array;
        Assert.assertEquals("[1, 2, 3, [...]]", Strings.toString(array));
    }
}

