package org.pac4j.core.profile.converter;


import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;


/**
 * This class tests the {@link org.pac4j.core.profile.converter.StringConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringConverterTests implements TestsConstants {
    private final StringConverter converter = new StringConverter();

    @Test
    public void testNull() {
        Assert.assertNull(this.converter.convert(null));
    }

    @Test
    public void testListNull() {
        Assert.assertNull(this.converter.convert(new ArrayList()));
    }

    @Test
    public void testNotAString() {
        Assert.assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testString() {
        Assert.assertEquals(TestsConstants.VALUE, this.converter.convert(TestsConstants.VALUE));
    }

    @Test
    public void testListString() {
        Assert.assertEquals(TestsConstants.VALUE, this.converter.convert(Arrays.asList(TestsConstants.VALUE)));
    }
}

