package org.apache.commons.io.input;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class AmplUnixLineEndingInputStreamTest {
    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_add7_add87_literalMutationString592() throws Exception {
        String o_inTheMiddleOfTheLine_add7__1 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
        String o_inTheMiddleOfTheLine_add7_add87__4 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add87__4);
        String o_inTheMiddleOfTheLine_add7__2 = roundtrip("a\ry\nbc");
        Assert.assertEquals("ay\nbc\n", o_inTheMiddleOfTheLine_add7__2);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add87__4);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_add7_add87_literalMutationString554() throws Exception {
        String o_inTheMiddleOfTheLine_add7__1 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
        String o_inTheMiddleOfTheLine_add7_add87__4 = roundtrip("a\rwbc");
        Assert.assertEquals("awbc\n", o_inTheMiddleOfTheLine_add7_add87__4);
        String o_inTheMiddleOfTheLine_add7__2 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__2);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
        Assert.assertEquals("awbc\n", o_inTheMiddleOfTheLine_add7_add87__4);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_add7_add87_literalMutationString511() throws Exception {
        String o_inTheMiddleOfTheLine_add7__1 = roundtrip("a\rbc");
        Assert.assertEquals("abc\n", o_inTheMiddleOfTheLine_add7__1);
        String o_inTheMiddleOfTheLine_add7_add87__4 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add87__4);
        String o_inTheMiddleOfTheLine_add7__2 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__2);
        Assert.assertEquals("abc\n", o_inTheMiddleOfTheLine_add7__1);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add87__4);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_add7_add86_literalMutationString479() throws Exception {
        String o_inTheMiddleOfTheLine_add7_add86__1 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add86__1);
        String o_inTheMiddleOfTheLine_add7__1 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
        String o_inTheMiddleOfTheLine_add7__2 = roundtrip("a\rNbc");
        Assert.assertEquals("aNbc\n", o_inTheMiddleOfTheLine_add7__2);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add86__1);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_add7_add87_literalMutationString525() throws Exception {
        String o_inTheMiddleOfTheLine_add7__1 = roundtrip("a\r[\nbc");
        Assert.assertEquals("a[\nbc\n", o_inTheMiddleOfTheLine_add7__1);
        String o_inTheMiddleOfTheLine_add7_add87__4 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add87__4);
        String o_inTheMiddleOfTheLine_add7__2 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__2);
        Assert.assertEquals("a[\nbc\n", o_inTheMiddleOfTheLine_add7__1);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add87__4);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_add7_literalMutationString82() throws Exception {
        String o_inTheMiddleOfTheLine_add7__1 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
        String o_inTheMiddleOfTheLine_add7__2 = roundtrip("a\r1bc");
        Assert.assertEquals("a1bc\n", o_inTheMiddleOfTheLine_add7__2);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_literalMutationString4_add75_literalMutationString584() throws Exception {
        String o_inTheMiddleOfTheLine_literalMutationString4_add75__1 = roundtrip("a}\r-\nbc");
        Assert.assertEquals("a}-\nbc\n", o_inTheMiddleOfTheLine_literalMutationString4_add75__1);
        String o_inTheMiddleOfTheLine_literalMutationString4__1 = roundtrip("a}\r\nbc");
        Assert.assertEquals("a}\nbc\n", o_inTheMiddleOfTheLine_literalMutationString4__1);
        Assert.assertEquals("a}-\nbc\n", o_inTheMiddleOfTheLine_literalMutationString4_add75__1);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_literalMutationString4_add75_literalMutationString588() throws Exception {
        String o_inTheMiddleOfTheLine_literalMutationString4_add75__1 = roundtrip("a}\rbc");
        Assert.assertEquals("a}bc\n", o_inTheMiddleOfTheLine_literalMutationString4_add75__1);
        String o_inTheMiddleOfTheLine_literalMutationString4__1 = roundtrip("a}\r\nbc");
        Assert.assertEquals("a}\nbc\n", o_inTheMiddleOfTheLine_literalMutationString4__1);
        Assert.assertEquals("a}bc\n", o_inTheMiddleOfTheLine_literalMutationString4_add75__1);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_add7_add86_literalMutationString440() throws Exception {
        String o_inTheMiddleOfTheLine_add7_add86__1 = roundtrip("a\rbc");
        Assert.assertEquals("abc\n", o_inTheMiddleOfTheLine_add7_add86__1);
        String o_inTheMiddleOfTheLine_add7__1 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
        String o_inTheMiddleOfTheLine_add7__2 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__2);
        Assert.assertEquals("abc\n", o_inTheMiddleOfTheLine_add7_add86__1);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__1);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_add7_add86_literalMutationString463() throws Exception {
        String o_inTheMiddleOfTheLine_add7_add86__1 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add86__1);
        String o_inTheMiddleOfTheLine_add7__1 = roundtrip("a\r/bc");
        Assert.assertEquals("a/bc\n", o_inTheMiddleOfTheLine_add7__1);
        String o_inTheMiddleOfTheLine_add7__2 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__2);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add86__1);
        Assert.assertEquals("a/bc\n", o_inTheMiddleOfTheLine_add7__1);
    }

    @Test(timeout = 10000)
    public void inTheMiddleOfTheLine_add7_add86_literalMutationString459() throws Exception {
        String o_inTheMiddleOfTheLine_add7_add86__1 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add86__1);
        String o_inTheMiddleOfTheLine_add7__1 = roundtrip("a\rbc");
        Assert.assertEquals("abc\n", o_inTheMiddleOfTheLine_add7__1);
        String o_inTheMiddleOfTheLine_add7__2 = roundtrip("a\r\nbc");
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7__2);
        Assert.assertEquals("a\nbc\n", o_inTheMiddleOfTheLine_add7_add86__1);
        Assert.assertEquals("abc\n", o_inTheMiddleOfTheLine_add7__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6250() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\nFt\nbc");
        Assert.assertEquals("a\nFt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nFt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6259() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("");
        Assert.assertEquals("\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_add5933_add6018_literalMutationString6600() throws Exception {
        String o_multipleBlankLines_add5933__1 = roundtrip("a\r\n\rpbc");
        Assert.assertEquals("a\npbc\n", o_multipleBlankLines_add5933__1);
        String o_multipleBlankLines_add5933_add6018__4 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6018__4);
        String o_multipleBlankLines_add5933__2 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__2);
        Assert.assertEquals("a\npbc\n", o_multipleBlankLines_add5933__1);
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6018__4);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6260() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5930_add6008_literalMutationString6652() throws Exception {
        String o_multipleBlankLines_literalMutationString5930_add6008__1 = roundtrip("a\r\ng\nbc");
        Assert.assertEquals("a\ng\nbc\n", o_multipleBlankLines_literalMutationString5930_add6008__1);
        String o_multipleBlankLines_literalMutationString5930__1 = roundtrip("a\rg\nbc");
        Assert.assertEquals("ag\nbc\n", o_multipleBlankLines_literalMutationString5930__1);
        Assert.assertEquals("a\ng\nbc\n", o_multipleBlankLines_literalMutationString5930_add6008__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6268() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("aZ\r\n\rt\nbc");
        Assert.assertEquals("aZ\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6265() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("A&wJD0X/");
        Assert.assertEquals("A&wJD0X/\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6263() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt8bc");
        Assert.assertEquals("a\nt8bc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6264() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("application/atom+xml");
        Assert.assertEquals("application/atom+xml\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_add5978_literalMutationString6349() throws Exception {
        String o_multipleBlankLines_literalMutationString5929_add5978__1 = roundtrip("a \r\n\rbc");
        Assert.assertEquals("a \nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929__1);
        Assert.assertEquals("a \nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_add5978_literalMutationString6352() throws Exception {
        String o_multipleBlankLines_literalMutationString5929_add5978__1 = roundtrip("application/atom+xml");
        Assert.assertEquals("application/atom+xml\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929__1);
        Assert.assertEquals("application/atom+xml\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_add5933_add6018_literalMutationString6594() throws Exception {
        String o_multipleBlankLines_add5933__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_add5933__1);
        String o_multipleBlankLines_add5933_add6018__4 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6018__4);
        String o_multipleBlankLines_add5933__2 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__2);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_add5933__1);
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6018__4);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932() throws Exception {
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_add5978_literalMutationString6398() throws Exception {
        String o_multipleBlankLines_literalMutationString5929_add5978__1 = roundtrip("a\r\nibc");
        Assert.assertEquals("a\nibc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929__1);
        Assert.assertEquals("a\nibc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_literalMutationString5969() throws Exception {
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\rt\nbc");
        Assert.assertEquals("a\rt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6239() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("7,e!<J!z");
        Assert.assertEquals("7,e!<J!z\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("7,e!<J!z\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6236() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\nT\rt\nbc");
        Assert.assertEquals("a\nTt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nTt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6237() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("");
        Assert.assertEquals("\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_literalMutationString5962() throws Exception {
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nDbc");
        Assert.assertEquals("a\nt\nDbc\n", o_multipleBlankLines_literalMutationString5932__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_literalMutationString5965() throws Exception {
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt)bc");
        Assert.assertEquals("a\nt)bc\n", o_multipleBlankLines_literalMutationString5932__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929() throws Exception {
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_add5933_literalMutationString6015() throws Exception {
        String o_multipleBlankLines_add5933__1 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__1);
        String o_multipleBlankLines_add5933__2 = roundtrip("a\r\n\r4bc");
        Assert.assertEquals("a\n4bc\n", o_multipleBlankLines_add5933__2);
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_add5978_add6485() throws Exception {
        String o_multipleBlankLines_literalMutationString5929_add5978__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        String o_multipleBlankLines_literalMutationString5929_add5978_add6485__4 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978_add6485__4);
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929__1);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978_add6485__4);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_add5933_literalMutationString6016() throws Exception {
        String o_multipleBlankLines_add5933__1 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__1);
        String o_multipleBlankLines_add5933__2 = roundtrip("a\r\r\nbc");
        Assert.assertEquals("a\r\nbc\n", o_multipleBlankLines_add5933__2);
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6241() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_literalMutationString6242() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("application/atom+xml");
        Assert.assertEquals("application/atom+xml\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("application/atom+xml\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_add5933_add6017_literalMutationString6520() throws Exception {
        String o_multipleBlankLines_add5933_add6017__1 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6017__1);
        String o_multipleBlankLines_add5933__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_add5933__1);
        String o_multipleBlankLines_add5933__2 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__2);
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6017__1);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_add5933__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_add5978() throws Exception {
        String o_multipleBlankLines_literalMutationString5929_add5978__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929__1);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_add5933_add6017_literalMutationString6535() throws Exception {
        String o_multipleBlankLines_add5933_add6017__1 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6017__1);
        String o_multipleBlankLines_add5933__1 = roundtrip("a\r\n\rWbc");
        Assert.assertEquals("a\nWbc\n", o_multipleBlankLines_add5933__1);
        String o_multipleBlankLines_add5933__2 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__2);
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6017__1);
        Assert.assertEquals("a\nWbc\n", o_multipleBlankLines_add5933__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_literalMutationString5966() throws Exception {
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\rE\n\rbc");
        Assert.assertEquals("aE\nbc\n", o_multipleBlankLines_literalMutationString5929__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_add5933_literalMutationString5992() throws Exception {
        String o_multipleBlankLines_add5933__1 = roundtrip("a\r\n\r6\nbc");
        Assert.assertEquals("a\n6\nbc\n", o_multipleBlankLines_add5933__1);
        String o_multipleBlankLines_add5933__2 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__2);
        Assert.assertEquals("a\n6\nbc\n", o_multipleBlankLines_add5933__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_literalMutationString5970() throws Exception {
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("aK\n\rbc");
        Assert.assertEquals("aK\nbc\n", o_multipleBlankLines_literalMutationString5929__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_add6279() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932_add5975_add6279__4 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975_add6279__4);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975_add6279__4);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5932_add5975_add6275() throws Exception {
        String o_multipleBlankLines_literalMutationString5932_add5975_add6275__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975_add6275__1);
        String o_multipleBlankLines_literalMutationString5932_add5975__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
        String o_multipleBlankLines_literalMutationString5932__1 = roundtrip("a\r\n\rt\nbc");
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975_add6275__1);
        Assert.assertEquals("a\nt\nbc\n", o_multipleBlankLines_literalMutationString5932_add5975__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_add5978_add6432() throws Exception {
        String o_multipleBlankLines_literalMutationString5929_add5978_add6432__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978_add6432__1);
        String o_multipleBlankLines_literalMutationString5929_add5978__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929__1);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978_add6432__1);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_add5978_literalMutationString6419() throws Exception {
        String o_multipleBlankLines_literalMutationString5929_add5978__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\r\nxbc");
        Assert.assertEquals("a\nxbc\n", o_multipleBlankLines_literalMutationString5929__1);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_add5978_literalMutationString6416() throws Exception {
        String o_multipleBlankLines_literalMutationString5929_add5978__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("application/atom+xml");
        Assert.assertEquals("application/atom+xml\n", o_multipleBlankLines_literalMutationString5929__1);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_literalMutationString5929_add5978_literalMutationString6421() throws Exception {
        String o_multipleBlankLines_literalMutationString5929_add5978__1 = roundtrip("a\r\n\rbc");
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
        String o_multipleBlankLines_literalMutationString5929__1 = roundtrip("a\r\n*\rbc");
        Assert.assertEquals("a\n*bc\n", o_multipleBlankLines_literalMutationString5929__1);
        Assert.assertEquals("a\nbc\n", o_multipleBlankLines_literalMutationString5929_add5978__1);
    }

    @Test(timeout = 10000)
    public void multipleBlankLines_add5933_add6018_literalMutationString6627() throws Exception {
        String o_multipleBlankLines_add5933__1 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__1);
        String o_multipleBlankLines_add5933_add6018__4 = roundtrip("a\r\n\r\nbc");
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6018__4);
        String o_multipleBlankLines_add5933__2 = roundtrip("a\rs\r\nbc");
        Assert.assertEquals("as\nbc\n", o_multipleBlankLines_add5933__2);
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933__1);
        Assert.assertEquals("a\n\nbc\n", o_multipleBlankLines_add5933_add6018__4);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7679() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\r<\r\n");
        Assert.assertEquals("a<\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7675() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("N_Y#I");
        Assert.assertEquals("N_Y#I\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("N_Y#I\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7677() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("aT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_add7697() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410_add7697__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410_add7697__1);
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410_add7697__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_add7698() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366_add7410_add7698__4 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410_add7698__4);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410_add7698__4);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7666() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("aR\rT\r\n");
        Assert.assertEquals("aRT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aRT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7671() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("application/atom+xml");
        Assert.assertEquals("application/atom+xml\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("application/atom+xml\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7673() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("Q\rT\r\n");
        Assert.assertEquals("QT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("QT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_add7367_add7452_literalMutationString7849() throws Exception {
        String o_twoLinesAtEnd_add7367__1 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__1);
        String o_twoLinesAtEnd_add7367_add7452__4 = roundtrip("a\r8\n\r\n");
        Assert.assertEquals("a8\n\n", o_twoLinesAtEnd_add7367_add7452__4);
        String o_twoLinesAtEnd_add7367__2 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__2);
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__1);
        Assert.assertEquals("a8\n\n", o_twoLinesAtEnd_add7367_add7452__4);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7685() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("application/atom+xml");
        Assert.assertEquals("application/atom+xml\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7686() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\r_T\r\n");
        Assert.assertEquals("a_T\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7687() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("R@z<e");
        Assert.assertEquals("R@z<e\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410_literalMutationString7688() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7361_add7446_literalMutationString8220() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7361_add7446__1 = roundtrip("a\r\n\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_literalMutationString7361_add7446__1);
        String o_twoLinesAtEnd_literalMutationString7361__1 = roundtrip("a\rY\n\n");
        Assert.assertEquals("aY\n\n", o_twoLinesAtEnd_literalMutationString7361__1);
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_literalMutationString7361_add7446__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_literalMutationString7402() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("F\rT\r\n");
        Assert.assertEquals("FT\n", o_twoLinesAtEnd_literalMutationString7366__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_add7367_add7451_literalMutationString7754() throws Exception {
        String o_twoLinesAtEnd_add7367_add7451__1 = roundtrip("a\rU\n\r\n");
        Assert.assertEquals("aU\n\n", o_twoLinesAtEnd_add7367_add7451__1);
        String o_twoLinesAtEnd_add7367__1 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__1);
        String o_twoLinesAtEnd_add7367__2 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__2);
        Assert.assertEquals("aU\n\n", o_twoLinesAtEnd_add7367_add7451__1);
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7363_literalMutationString7408() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7363__1 = roundtrip("a\r!H\r\n");
        Assert.assertEquals("a!H\n", o_twoLinesAtEnd_literalMutationString7363__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_add7367_add7451_literalMutationString7766() throws Exception {
        String o_twoLinesAtEnd_add7367_add7451__1 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367_add7451__1);
        String o_twoLinesAtEnd_add7367__1 = roundtrip("a\rd\n\r\n");
        Assert.assertEquals("ad\n\n", o_twoLinesAtEnd_add7367__1);
        String o_twoLinesAtEnd_add7367__2 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__2);
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367_add7451__1);
        Assert.assertEquals("ad\n\n", o_twoLinesAtEnd_add7367__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_add7367_add7451_literalMutationString7768() throws Exception {
        String o_twoLinesAtEnd_add7367_add7451__1 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367_add7451__1);
        String o_twoLinesAtEnd_add7367__1 = roundtrip("a\r\r\n");
        Assert.assertEquals("a\r\n", o_twoLinesAtEnd_add7367__1);
        String o_twoLinesAtEnd_add7367__2 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__2);
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367_add7451__1);
        Assert.assertEquals("a\r\n", o_twoLinesAtEnd_add7367__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7363_add7412_literalMutationString7847() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7363_add7412__1 = roundtrip("a\r!H\r\n");
        Assert.assertEquals("a!H\n", o_twoLinesAtEnd_literalMutationString7363_add7412__1);
        String o_twoLinesAtEnd_literalMutationString7363__1 = roundtrip("a\r\nH\r\n");
        Assert.assertEquals("a\nH\n", o_twoLinesAtEnd_literalMutationString7363__1);
        Assert.assertEquals("a!H\n", o_twoLinesAtEnd_literalMutationString7363_add7412__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_add7410() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366_add7410__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366_add7410__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_add7367_add7451_literalMutationString7783() throws Exception {
        String o_twoLinesAtEnd_add7367_add7451__1 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367_add7451__1);
        String o_twoLinesAtEnd_add7367__1 = roundtrip("a\r\n\r\n");
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__1);
        String o_twoLinesAtEnd_add7367__2 = roundtrip("a\rh\r\n");
        Assert.assertEquals("ah\n", o_twoLinesAtEnd_add7367__2);
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367_add7451__1);
        Assert.assertEquals("a\n\n", o_twoLinesAtEnd_add7367__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rT\r\n");
        Assert.assertEquals("aT\n", o_twoLinesAtEnd_literalMutationString7366__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_literalMutationString7394() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\rMT\r\n");
        Assert.assertEquals("aMT\n", o_twoLinesAtEnd_literalMutationString7366__1);
    }

    @Test(timeout = 10000)
    public void twoLinesAtEnd_literalMutationString7366_literalMutationString7396() throws Exception {
        String o_twoLinesAtEnd_literalMutationString7366__1 = roundtrip("a\r\r\n");
        Assert.assertEquals("a\r\n", o_twoLinesAtEnd_literalMutationString7366__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9365() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_literalMutationString8840() throws Exception {
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rc", true);
        Assert.assertEquals("ac\n", o_malformed_literalMutationBoolean8801__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796_add8889() throws Exception {
        String o_malformed_literalMutationString8796_add8889__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889__1);
        String o_malformed_literalMutationString8796__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796__1);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9360() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a?bc", false);
        Assert.assertEquals("a?bc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("a?bc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9363() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rubc", false);
        Assert.assertEquals("aubc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("aubc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationBoolean9382() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a\r6bc", true);
        Assert.assertEquals("a6bc\n", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("a6bc\n", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationBoolean9352() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc\n", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8799_add8888_literalMutationString9632() throws Exception {
        String o_malformed_literalMutationString8799_add8888__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_literalMutationString8799_add8888__1);
        String o_malformed_literalMutationString8799__1 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_literalMutationString8799__1);
        Assert.assertEquals("application/atom+xml", o_malformed_literalMutationString8799_add8888__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9368() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("ac", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_literalMutationString8833() throws Exception {
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\r3bc", true);
        Assert.assertEquals("a3bc\n", o_malformed_literalMutationBoolean8801__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9374() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("&rnF", false);
        Assert.assertEquals("&rnF", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("&rnF", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_add9389() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798_add8854_add9389__4 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854_add9389__4);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854_add9389__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationBoolean9520() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8905() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("abbc", false);
        Assert.assertEquals("abbc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9385() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("4ljZ", false);
        Assert.assertEquals("4ljZ", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8799_add8888() throws Exception {
        String o_malformed_literalMutationString8799_add8888__1 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_literalMutationString8799_add8888__1);
        String o_malformed_literalMutationString8799__1 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_literalMutationString8799__1);
        Assert.assertEquals("ac", o_malformed_literalMutationString8799_add8888__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9592() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\r bc", true);
        Assert.assertEquals("a bc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("a bc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9590() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("d\rbc", true);
        Assert.assertEquals("dbc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("dbc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_literalMutationBoolean8849() throws Exception {
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_literalMutationBoolean8801__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8799_literalMutationBoolean8882() throws Exception {
        String o_malformed_literalMutationString8799__1 = roundtrip("a\rc", true);
        Assert.assertEquals("ac\n", o_malformed_literalMutationString8799__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_add9414() throws Exception {
        String o_malformed_add8802_add8907_add9414__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907_add9414__1);
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907_add9414__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8901() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8902() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8903() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("Pl|N", false);
        Assert.assertEquals("Pl|N", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_add9418() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802_add8907_add9418__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907_add9418__4);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8907_add9418__4);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8904() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("aO\rbc", false);
        Assert.assertEquals("aObc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_add9419() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8907_add9419__7 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907_add9419__7);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8907_add9419__7);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9398() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("|\rbc", false);
        Assert.assertEquals("|bc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9394() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9585() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rc", true);
        Assert.assertEquals("ac\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("ac\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9396() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\r@bc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9390() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9581() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("application/atom+xml", true);
        Assert.assertEquals("application/atom+xml\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("application/atom+xml\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796_add8889_literalMutationString9848() throws Exception {
        String o_malformed_literalMutationString8796_add8889__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_literalMutationString8796_add8889__1);
        String o_malformed_literalMutationString8796__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796__1);
        Assert.assertEquals("application/atom+xml", o_malformed_literalMutationString8796_add8889__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796_add8889_literalMutationString9843() throws Exception {
        String o_malformed_literalMutationString8796_add8889__1 = roundtrip("`\r}bc", false);
        Assert.assertEquals("`}bc", o_malformed_literalMutationString8796_add8889__1);
        String o_malformed_literalMutationString8796__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796__1);
        Assert.assertEquals("`}bc", o_malformed_literalMutationString8796_add8889__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationBoolean9548() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9609() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a!\rbc", true);
        Assert.assertEquals("a!bc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9607() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("azbc", true);
        Assert.assertEquals("azbc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationBoolean9614() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9605() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rc", true);
        Assert.assertEquals("ac\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9603() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("application/atom+xml", true);
        Assert.assertEquals("application/atom+xml\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9602() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("", true);
        Assert.assertEquals("\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationBoolean9357() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a\r6bc", true);
        Assert.assertEquals("a6bc\n", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("a6bc\n", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9539() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("VC>o", false);
        Assert.assertEquals("VC>o", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9536() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("abc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9537() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9533() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("ah\rbc", false);
        Assert.assertEquals("ahbc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationBoolean9380() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc\n", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9508() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("abc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_literalMutationBoolean8850() throws Exception {
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", true);
        Assert.assertEquals("a6bc\n", o_malformed_literalMutationString8798__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9504() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rZbc", false);
        Assert.assertEquals("aZbc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("aZbc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8799_add8888_literalMutationString9655() throws Exception {
        String o_malformed_literalMutationString8799_add8888__1 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_literalMutationString8799_add8888__1);
        String o_malformed_literalMutationString8799__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_literalMutationString8799__1);
        Assert.assertEquals("ac", o_malformed_literalMutationString8799_add8888__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9513() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9510() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("@=7T", false);
        Assert.assertEquals("@=7T", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("@=7T", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_add9617() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855_add9617__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855_add9617__1);
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855_add9617__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796_literalMutationBoolean8887() throws Exception {
        String o_malformed_literalMutationString8796__1 = roundtrip("`\rbc", true);
        Assert.assertEquals("`bc\n", o_malformed_literalMutationString8796__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796_add8889_literalMutationString9880() throws Exception {
        String o_malformed_literalMutationString8796_add8889__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889__1);
        String o_malformed_literalMutationString8796__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_literalMutationString8796__1);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796_add8889_add9901() throws Exception {
        String o_malformed_literalMutationString8796_add8889_add9901__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889_add9901__1);
        String o_malformed_literalMutationString8796_add8889__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889__1);
        String o_malformed_literalMutationString8796__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796__1);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889_add9901__1);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796_add8889_add9904() throws Exception {
        String o_malformed_literalMutationString8796_add8889__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889__1);
        String o_malformed_literalMutationString8796_add8889_add9904__4 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889_add9904__4);
        String o_malformed_literalMutationString8796__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796__1);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889__1);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796_add8889_add9904__4);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_add9620() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801_add8855_add9620__5 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855_add9620__5);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855_add9620__5);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796() throws Exception {
        String o_malformed_literalMutationString8796__1 = roundtrip("`\rbc", false);
        Assert.assertEquals("`bc", o_malformed_literalMutationString8796__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9449() throws Exception {
        String o_malformed_add8802__1 = roundtrip("K\rbc", false);
        Assert.assertEquals("Kbc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("Kbc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8799() throws Exception {
        String o_malformed_literalMutationString8799__1 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_literalMutationString8799__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798() throws Exception {
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationBoolean9494() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationBoolean8906() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_add9562() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908_add9562__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908_add9562__4);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908_add9562__4);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796_literalMutationString8876() throws Exception {
        String o_malformed_literalMutationString8796__1 = roundtrip("`Z\rbc", false);
        Assert.assertEquals("`Zbc", o_malformed_literalMutationString8796__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9452() throws Exception {
        String o_malformed_add8802__1 = roundtrip("aP\rbc", false);
        Assert.assertEquals("aPbc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("aPbc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8796_literalMutationString8879() throws Exception {
        String o_malformed_literalMutationString8796__1 = roundtrip("`\rc", false);
        Assert.assertEquals("`c", o_malformed_literalMutationString8796__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_add9560() throws Exception {
        String o_malformed_add8802_add8908_add9560__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908_add9560__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802_add8908_add9560__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_add9565() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802_add8908_add9565__7 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908_add9565__7);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        Assert.assertEquals("abc", o_malformed_add8802_add8908_add9565__7);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationString9364() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("ay6bc", false);
        Assert.assertEquals("ay6bc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed() throws Exception {
        String o_malformed__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_literalMutationString8835() throws Exception {
        String o_malformed_literalMutationString8798__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_literalMutationString8798__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationString9367() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_literalMutationString8830() throws Exception {
        String o_malformed_literalMutationString8798__1 = roundtrip("a\rF6bc", false);
        Assert.assertEquals("aF6bc", o_malformed_literalMutationString8798__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9542() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("/\rbc", false);
        Assert.assertEquals("/bc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationString9370() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801() throws Exception {
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_literalMutationString8846() throws Exception {
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r;bc", false);
        Assert.assertEquals("a;bc", o_malformed_literalMutationString8798__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationString9376() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("a@\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9612() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("{NoC", true);
        Assert.assertEquals("{NoC\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9577() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("", true);
        Assert.assertEquals("\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationString9578() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("#PrJ", true);
        Assert.assertEquals("#PrJ\n", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("#PrJ\n", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationString9349() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("|P&{i", false);
        Assert.assertEquals("|P&{i", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("|P&{i", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationString9348() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("application/atom+xml", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationString9345() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("ad\r6bc", false);
        Assert.assertEquals("ad6bc", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("ad6bc", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationBoolean8899() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc\n", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9489() throws Exception {
        String o_malformed_add8802__1 = roundtrip("Q_&)", false);
        Assert.assertEquals("Q_&)", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("Q_&)", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9485() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("ac", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9481() throws Exception {
        String o_malformed_add8802__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802__1);
        Assert.assertEquals("abc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9331() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\r(bc", false);
        Assert.assertEquals("a(bc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("a(bc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_literalMutationString9354() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a@6bc", false);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8908_literalMutationString9499() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802_add8908__4 = roundtrip(" \rbc", false);
        Assert.assertEquals(" bc", o_malformed_add8802_add8908__4);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        Assert.assertEquals(" bc", o_malformed_add8802_add8908__4);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8894() throws Exception {
        String o_malformed_add8802__1 = roundtrip("uXY;", false);
        Assert.assertEquals("uXY;", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("uXY;", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8895() throws Exception {
        String o_malformed_add8802__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8896() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a6\rbc", false);
        Assert.assertEquals("a6bc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("a6bc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8897() throws Exception {
        String o_malformed_add8802__1 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("ac", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_literalMutationString8898() throws Exception {
        String o_malformed_add8802__1 = roundtrip("t\rbc", false);
        Assert.assertEquals("tbc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("tbc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9339() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("f\rbc", false);
        Assert.assertEquals("fbc", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("fbc", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9337() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("application/atom+xml", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9343() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rc", false);
        Assert.assertEquals("ac", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("ac", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854_add9386() throws Exception {
        String o_malformed_literalMutationString8798_add8854_add9386__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854_add9386__1);
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854_add9386__1);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationBoolean9402() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("abc\n", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationBoolean8801_add8855_literalMutationBoolean9596() throws Exception {
        String o_malformed_literalMutationBoolean8801_add8855__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_literalMutationBoolean8801_add8855__1);
        String o_malformed_literalMutationBoolean8801__1 = roundtrip("a\rbc", true);
        Assert.assertEquals("abc\n", o_malformed_literalMutationBoolean8801__1);
        Assert.assertEquals("abc", o_malformed_literalMutationBoolean8801_add8855__1);
    }

    @Test(timeout = 10000)
    public void malformed_add8802_add8907_literalMutationString9347() throws Exception {
        String o_malformed_add8802_add8907__1 = roundtrip("G+fP", false);
        Assert.assertEquals("G+fP", o_malformed_add8802_add8907__1);
        String o_malformed_add8802__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__1);
        String o_malformed_add8802__2 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed_add8802__2);
        Assert.assertEquals("G+fP", o_malformed_add8802_add8907__1);
        Assert.assertEquals("abc", o_malformed_add8802__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8799_literalMutationString8875() throws Exception {
        String o_malformed_literalMutationString8799__1 = roundtrip("a!\rc", false);
        Assert.assertEquals("a!c", o_malformed_literalMutationString8799__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8799_literalMutationString8871() throws Exception {
        String o_malformed_literalMutationString8799__1 = roundtrip("e\rc", false);
        Assert.assertEquals("ec", o_malformed_literalMutationString8799__1);
    }

    @Test(timeout = 10000)
    public void malformed_literalMutationString8798_add8854() throws Exception {
        String o_malformed_literalMutationString8798_add8854__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
        String o_malformed_literalMutationString8798__1 = roundtrip("a\r6bc", false);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798__1);
        Assert.assertEquals("a6bc", o_malformed_literalMutationString8798_add8854__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_add1557_literalMutationString4213() throws Exception {
        String o_retainLineFeed_literalMutationString1438_add1557__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_literalMutationString1525() throws Exception {
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1442_literalMutationString1585() throws Exception {
        String o_retainLineFeed_literalMutationString1442__1 = roundtrip("a\r-\n\r\n", false);
        Assert.assertEquals("a-\n\n", o_retainLineFeed_literalMutationString1442__1);
        String o_retainLineFeed_literalMutationString1442__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1442__2);
        Assert.assertEquals("a-\n\n", o_retainLineFeed_literalMutationString1442__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1436_add1696_literalMutationString3692() throws Exception {
        String o_retainLineFeed_literalMutationString1436_add1696__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1436_add1696__1);
        String o_retainLineFeed_literalMutationString1436__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
        String o_retainLineFeed_literalMutationString1436__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1436__2);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1436_add1696__1);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationBoolean1444_literalMutationString1500() throws Exception {
        String o_retainLineFeed_literalMutationBoolean1444__1 = roundtrip("a\rW\r\n", true);
        Assert.assertEquals("aW\n", o_retainLineFeed_literalMutationBoolean1444__1);
        String o_retainLineFeed_literalMutationBoolean1444__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationBoolean1444__3);
        Assert.assertEquals("aW\n", o_retainLineFeed_literalMutationBoolean1444__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_add1557_literalMutationString4203() throws Exception {
        String o_retainLineFeed_literalMutationString1438_add1557__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1438_add1557__1);
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1438_add1557__1);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_literalMutationString1542() throws Exception {
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6;\n\r\n", false);
        Assert.assertEquals("a6;\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6;\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_literalMutationString1551() throws Exception {
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_literalMutationString1552() throws Exception {
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("M", false);
        Assert.assertEquals("M", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationBoolean1440_literalMutationString1627() throws Exception {
        String o_retainLineFeed_literalMutationBoolean1440__1 = roundtrip("a\r\r\n", true);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationBoolean1440__1);
        String o_retainLineFeed_literalMutationBoolean1440__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationBoolean1440__3);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationBoolean1440__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_literalMutationString1506() throws Exception {
        String o_retainLineFeed_add1445__1 = roundtrip("a\r1\r\n", false);
        Assert.assertEquals("a1\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a1\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_literalMutationString1539() throws Exception {
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6]\r\n", false);
        Assert.assertEquals("a6]\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6]\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_literalMutationString1562() throws Exception {
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445__2 = roundtrip("a\rd\r\n", false);
        Assert.assertEquals("ad\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("ad\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_add1642_literalMutationString4722() throws Exception {
        String o_retainLineFeed_add1445_add1642__1 = roundtrip("a\r.\n\r\n", false);
        Assert.assertEquals("a.\n\n", o_retainLineFeed_add1445_add1642__1);
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a.\n\n", o_retainLineFeed_add1445_add1642__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_add1642_literalMutationString4842() throws Exception {
        String o_retainLineFeed_add1445_add1642__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445_add1642__1);
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r2\r\n", false);
        Assert.assertEquals("a2\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445_add1642__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("a2\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_add1642_literalMutationString4759() throws Exception {
        String o_retainLineFeed_add1445_add1642__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445_add1642__1);
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445_add1642__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1442_add1607_literalMutationString2365() throws Exception {
        String o_retainLineFeed_literalMutationString1442__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1442__1);
        String o_retainLineFeed_literalMutationString1442_add1607__4 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1442_add1607__4);
        String o_retainLineFeed_literalMutationString1442__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1442__2);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1442__1);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1442_add1607__4);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1446_literalMutationString1651() throws Exception {
        String o_retainLineFeed_add1446__1 = roundtrip("a\rL\n\r\n", false);
        Assert.assertEquals("aL\n\n", o_retainLineFeed_add1446__1);
        String o_retainLineFeed_add1446__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1446__2);
        String o_retainLineFeed_add1446__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1446__3);
        Assert.assertEquals("aL\n\n", o_retainLineFeed_add1446__1);
        Assert.assertEquals("a", o_retainLineFeed_add1446__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1446_literalMutationString1650() throws Exception {
        String o_retainLineFeed_add1446__1 = roundtrip("a\rU\r\n", false);
        Assert.assertEquals("aU\n", o_retainLineFeed_add1446__1);
        String o_retainLineFeed_add1446__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1446__2);
        String o_retainLineFeed_add1446__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1446__3);
        Assert.assertEquals("aU\n", o_retainLineFeed_add1446__1);
        Assert.assertEquals("a", o_retainLineFeed_add1446__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1436_add1696_literalMutationString3717() throws Exception {
        String o_retainLineFeed_literalMutationString1436_add1696__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436_add1696__1);
        String o_retainLineFeed_literalMutationString1436__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
        String o_retainLineFeed_literalMutationString1436__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1436__2);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436_add1696__1);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1441_failAssert50_literalMutationString1614() throws Exception {
        try {
            String o_retainLineFeed_literalMutationString1441_failAssert50_literalMutationString1614__3 = roundtrip("a\r&\r\n", false);
            Assert.assertEquals("a&\n", o_retainLineFeed_literalMutationString1441_failAssert50_literalMutationString1614__3);
            roundtrip("", false);
            org.junit.Assert.fail("retainLineFeed_literalMutationString1441 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1436_add1697() throws Exception {
        String o_retainLineFeed_literalMutationString1436__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
        String o_retainLineFeed_literalMutationString1436_add1697__4 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1436_add1697__4);
        String o_retainLineFeed_literalMutationString1436__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1436__2);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1436_add1697__4);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1436_add1696() throws Exception {
        String o_retainLineFeed_literalMutationString1436_add1696__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436_add1696__1);
        String o_retainLineFeed_literalMutationString1436__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
        String o_retainLineFeed_literalMutationString1436__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1436__2);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436_add1696__1);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1442_add1604_literalMutationString2704() throws Exception {
        String o_retainLineFeed_literalMutationString1442_add1604__1 = roundtrip("a\r+\r\n", false);
        Assert.assertEquals("a+\n", o_retainLineFeed_literalMutationString1442_add1604__1);
        String o_retainLineFeed_literalMutationString1442__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_literalMutationString1442__1);
        String o_retainLineFeed_literalMutationString1442__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1442__2);
        Assert.assertEquals("a+\n", o_retainLineFeed_literalMutationString1442_add1604__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_literalMutationString1442__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1436_add1696_literalMutationString3707() throws Exception {
        String o_retainLineFeed_literalMutationString1436_add1696__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436_add1696__1);
        String o_retainLineFeed_literalMutationString1436__1 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1436__1);
        String o_retainLineFeed_literalMutationString1436__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1436__2);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436_add1696__1);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1436__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1442_add1604_literalMutationString2723() throws Exception {
        String o_retainLineFeed_literalMutationString1442_add1604__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_literalMutationString1442_add1604__1);
        String o_retainLineFeed_literalMutationString1442__1 = roundtrip("a\r1\n\r\n", false);
        Assert.assertEquals("a1\n\n", o_retainLineFeed_literalMutationString1442__1);
        String o_retainLineFeed_literalMutationString1442__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1442__2);
        Assert.assertEquals("a\n\n", o_retainLineFeed_literalMutationString1442_add1604__1);
        Assert.assertEquals("a1\n\n", o_retainLineFeed_literalMutationString1442__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_add1559() throws Exception {
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438_add1559__4 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438_add1559__4);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438_add1559__4);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_add1557() throws Exception {
        String o_retainLineFeed_literalMutationString1438_add1557__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1436_literalMutationString1660() throws Exception {
        String o_retainLineFeed_literalMutationString1436__1 = roundtrip("aq\r\r\n", false);
        Assert.assertEquals("aq\r\n", o_retainLineFeed_literalMutationString1436__1);
        String o_retainLineFeed_literalMutationString1436__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1436__2);
        Assert.assertEquals("aq\r\n", o_retainLineFeed_literalMutationString1436__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1436() throws Exception {
        String o_retainLineFeed_literalMutationString1436__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
        String o_retainLineFeed_literalMutationString1436__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1436__2);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438() throws Exception {
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_literalMutationBoolean1554() throws Exception {
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", true);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_add1642_literalMutationString4717() throws Exception {
        String o_retainLineFeed_add1445_add1642__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445_add1642__1);
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445_add1642__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeednull1448_failAssert52_literalMutationString1550() throws Exception {
        try {
            String o_retainLineFeednull1448_failAssert52_literalMutationString1550__3 = roundtrip("a\rA\r\n", false);
            Assert.assertEquals("aA\n", o_retainLineFeednull1448_failAssert52_literalMutationString1550__3);
            roundtrip(null, false);
            org.junit.Assert.fail("retainLineFeednull1448 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_literalMutationBoolean1545() throws Exception {
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", true);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1436_literalMutationString1683() throws Exception {
        String o_retainLineFeed_literalMutationString1436__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
        String o_retainLineFeed_literalMutationString1436__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1436__2);
        Assert.assertEquals("a\r\n", o_retainLineFeed_literalMutationString1436__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_add1557_add4263() throws Exception {
        String o_retainLineFeed_literalMutationString1438_add1557_add4263__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557_add4263__1);
        String o_retainLineFeed_literalMutationString1438_add1557__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557_add4263__1);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_add1557_add4265() throws Exception {
        String o_retainLineFeed_literalMutationString1438_add1557__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        String o_retainLineFeed_literalMutationString1438_add1557_add4265__4 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557_add4265__4);
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557_add4265__4);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_literalMutationString1438_add1557_literalMutationString4254() throws Exception {
        String o_retainLineFeed_literalMutationString1438_add1557__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        String o_retainLineFeed_literalMutationString1438__1 = roundtrip("a\r6\n\r\n", false);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
        String o_retainLineFeed_literalMutationString1438__2 = roundtrip("application/atom+xml", false);
        Assert.assertEquals("application/atom+xml", o_retainLineFeed_literalMutationString1438__2);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438_add1557__1);
        Assert.assertEquals("a6\n\n", o_retainLineFeed_literalMutationString1438__1);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_add1643_literalMutationString4549() throws Exception {
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445_add1643__4 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445_add1643__4);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445_add1643__4);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_add1643_literalMutationString4563() throws Exception {
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445_add1643__4 = roundtrip("a\rF\r\n", false);
        Assert.assertEquals("aF\n", o_retainLineFeed_add1445_add1643__4);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("aF\n", o_retainLineFeed_add1445_add1643__4);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_literalMutationString1493() throws Exception {
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\r\n", false);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a\r\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__2);
    }

    @Test(timeout = 10000)
    public void retainLineFeed_add1445_add1643_literalMutationString4571() throws Exception {
        String o_retainLineFeed_add1445__1 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        String o_retainLineFeed_add1445_add1643__4 = roundtrip("a\r\n\r\n", false);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445_add1643__4);
        String o_retainLineFeed_add1445__2 = roundtrip("a\r]\n\r\n", false);
        Assert.assertEquals("a]\n\n", o_retainLineFeed_add1445__2);
        String o_retainLineFeed_add1445__3 = roundtrip("a", false);
        Assert.assertEquals("a", o_retainLineFeed_add1445__3);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445__1);
        Assert.assertEquals("a\n\n", o_retainLineFeed_add1445_add1643__4);
        Assert.assertEquals("a]\n\n", o_retainLineFeed_add1445__2);
    }

    private String roundtrip(String msg) throws IOException {
        return roundtrip(msg, true);
    }

    private String roundtrip(String msg, boolean ensure) throws IOException {
        ByteArrayInputStream baos = new ByteArrayInputStream(msg.getBytes("UTF-8"));
        UnixLineEndingInputStream lf = new UnixLineEndingInputStream(baos, ensure);
        byte[] buf = new byte[100];
        final int read = lf.read(buf);
        lf.close();
        return new String(buf, 0, read, "UTF-8");
    }
}

