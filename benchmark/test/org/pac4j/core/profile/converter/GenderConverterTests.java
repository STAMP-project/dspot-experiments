package org.pac4j.core.profile.converter;


import Gender.FEMALE;
import Gender.MALE;
import Gender.UNSPECIFIED;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the {@link GenderConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class GenderConverterTests {
    private final GenderConverter converter = new GenderConverter();

    private final GenderConverter converterNumber = new GenderConverter("2", "1");

    private final GenderConverter converterChinese = new GenderConverter("?", "?");

    @Test
    public void testNull() {
        Assert.assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        Assert.assertEquals(UNSPECIFIED, this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testMale() {
        Assert.assertEquals(MALE, this.converter.convert("m"));
    }

    @Test
    public void testFemale() {
        Assert.assertEquals(FEMALE, this.converter.convert("f"));
    }

    @Test
    public void testMaleNumber() {
        Assert.assertEquals(MALE, this.converterNumber.convert(2));
    }

    @Test
    public void testFemaleNumber() {
        Assert.assertEquals(FEMALE, this.converterNumber.convert(1));
    }

    @Test
    public void testUnspecified() {
        Assert.assertEquals(UNSPECIFIED, this.converter.convert("unspecified"));
    }

    @Test
    public void testMaleEnum() {
        Assert.assertEquals(MALE, this.converter.convert(MALE.toString()));
    }

    @Test
    public void testFemaleEnum() {
        Assert.assertEquals(FEMALE, this.converter.convert(FEMALE.toString()));
    }

    @Test
    public void testUnspecifiedEnum() {
        Assert.assertEquals(UNSPECIFIED, this.converter.convert(UNSPECIFIED.toString()));
    }

    @Test
    public void testMaleChinese() {
        Assert.assertEquals(MALE, this.converterChinese.convert("?"));
    }

    @Test
    public void testFemaleChinese() {
        Assert.assertEquals(FEMALE, this.converterChinese.convert("?"));
    }

    @Test
    public void testUnspecifiedChinese() {
        Assert.assertEquals(UNSPECIFIED, this.converterChinese.convert("??"));
    }
}

