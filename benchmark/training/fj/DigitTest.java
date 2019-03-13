package fj;


import fj.data.Array;
import fj.data.Option;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class DigitTest {
    @Test
    public void testInteger() {
        for (Integer i : Array.range(0, 10)) {
            Assert.assertThat(Digit.fromLong(i).toLong(), Is.is(i.longValue()));
        }
    }

    @Test
    public void testChar() {
        for (Integer i : Array.range(0, 10)) {
            Character c = Character.forDigit(i, 10);
            Assert.assertThat(Digit.fromChar(c).some().toChar(), Is.is(c));
        }
    }

    @Test
    public void testCharNone() {
        Assert.assertThat(Digit.fromChar('x'), Is.is(Option.none()));
    }
}

