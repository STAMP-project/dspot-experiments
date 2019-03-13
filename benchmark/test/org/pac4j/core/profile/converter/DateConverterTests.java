package org.pac4j.core.profile.converter;


import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the {@link DateConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class DateConverterTests {
    private static final String FORMAT = "yyyy.MM.dd";

    private final DateConverter converter = new DateConverter(DateConverterTests.FORMAT);

    private static final String GOOD_DATE = "2012.01.01";

    private static final String BAD_DATE = "2012/01/01";

    @Test
    public void testNull() {
        Assert.assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        Assert.assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testGoodDate() {
        final Date d = this.converter.convert(DateConverterTests.GOOD_DATE);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateConverterTests.FORMAT);
        Assert.assertEquals(DateConverterTests.GOOD_DATE, simpleDateFormat.format(d));
    }

    @Test
    public void testBadDate() {
        Assert.assertNull(this.converter.convert(DateConverterTests.BAD_DATE));
    }
}

