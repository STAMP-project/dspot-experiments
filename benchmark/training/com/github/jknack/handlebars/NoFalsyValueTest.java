package com.github.jknack.handlebars;


import Handlebars.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit test for {@link Utils#isEmpty(Object)}
 *
 * @author edgar.espina
 */
@RunWith(Parameterized.class)
public class NoFalsyValueTest {
    /**
     * The value under testing.
     */
    private Object value;

    public NoFalsyValueTest(final Object value) {
        this.value = value;
    }

    @Test
    public void noFalsy() {
        Assert.assertEquals(false, Utils.isEmpty(value));
    }
}

