package com.github.jknack.handlebars;


import Handlebars.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit test for {@link Utils}
 *
 * @author edgar.espina
 */
@RunWith(Parameterized.class)
public class FalsyValueTest {
    /**
     * The value under testing.
     */
    private Object value;

    public FalsyValueTest(final Object value) {
        this.value = value;
    }

    @Test
    public void falsy() {
        Assert.assertEquals(true, Utils.isEmpty(value));
    }
}

