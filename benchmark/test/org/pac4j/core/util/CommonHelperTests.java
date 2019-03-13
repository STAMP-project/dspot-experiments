package org.pac4j.core.util;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;


/**
 * This class tests the {@link CommonHelper} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CommonHelperTests {
    private static final String URL_WITHOUT_PARAMETER = "http://host/app";

    private static final String URL_WITH_PARAMETER = "http://host/app?param=value";

    private static final String NAME = "name";

    private static final String VALUE = "va+l+ue";

    private static final String ENCODED_VALUE = "va%2Bl%2Bue";

    private static final Class<?> CLAZZ = String.class;

    private static final String CLASS_NAME = String.class.getSimpleName();

    @Test
    public void testIsNotBlankNull() {
        Assert.assertFalse(CommonHelper.isNotBlank(null));
    }

    @Test
    public void testIsNotBlankEmply() {
        Assert.assertFalse(CommonHelper.isNotBlank(""));
    }

    @Test
    public void testIsNotBlankBlank() {
        Assert.assertFalse(CommonHelper.isNotBlank("     "));
    }

    @Test
    public void testIsNotBlankNotBlank() {
        Assert.assertTrue(CommonHelper.isNotBlank(CommonHelperTests.NAME));
    }

    @Test
    public void testAssertNotBlankBlank() {
        try {
            CommonHelper.assertNotBlank(CommonHelperTests.NAME, "");
            Assert.fail("must throw an ClientException");
        } catch (final TechnicalException e) {
            Assert.assertEquals(((CommonHelperTests.NAME) + " cannot be blank"), e.getMessage());
        }
    }

    @Test
    public void testAssertNotBlankNotBlank() {
        CommonHelper.assertNotBlank(CommonHelperTests.NAME, CommonHelperTests.VALUE);
    }

    @Test
    public void testAssertNotNullNull() {
        try {
            CommonHelper.assertNotNull(CommonHelperTests.NAME, null);
            Assert.fail("must throw an ClientException");
        } catch (final TechnicalException e) {
            Assert.assertEquals(((CommonHelperTests.NAME) + " cannot be null"), e.getMessage());
        }
    }

    @Test
    public void testAssertNotNullNotNull() {
        CommonHelper.assertNotNull(CommonHelperTests.NAME, CommonHelperTests.VALUE);
    }

    @Test
    public void testAddParameterNullUrl() {
        Assert.assertNull(CommonHelper.addParameter(null, CommonHelperTests.NAME, CommonHelperTests.VALUE));
    }

    @Test
    public void testAddParameterNullName() {
        Assert.assertEquals(CommonHelperTests.URL_WITH_PARAMETER, CommonHelper.addParameter(CommonHelperTests.URL_WITH_PARAMETER, null, CommonHelperTests.VALUE));
    }

    @Test
    public void testAddParameterNullValue() {
        Assert.assertEquals(((((CommonHelperTests.URL_WITH_PARAMETER) + "&") + (CommonHelperTests.NAME)) + "="), CommonHelper.addParameter(CommonHelperTests.URL_WITH_PARAMETER, CommonHelperTests.NAME, null));
    }

    @Test
    public void testAddParameterWithParameter() {
        Assert.assertEquals((((((CommonHelperTests.URL_WITH_PARAMETER) + "&") + (CommonHelperTests.NAME)) + "=") + (CommonHelperTests.ENCODED_VALUE)), CommonHelper.addParameter(CommonHelperTests.URL_WITH_PARAMETER, CommonHelperTests.NAME, CommonHelperTests.VALUE));
    }

    @Test
    public void testAddParameterWithoutParameter() {
        Assert.assertEquals((((((CommonHelperTests.URL_WITHOUT_PARAMETER) + "?") + (CommonHelperTests.NAME)) + "=") + (CommonHelperTests.ENCODED_VALUE)), CommonHelper.addParameter(CommonHelperTests.URL_WITHOUT_PARAMETER, CommonHelperTests.NAME, CommonHelperTests.VALUE));
    }

    @Test
    public void testToNiceStringNoParameter() {
        Assert.assertEquals((("#" + (CommonHelperTests.CLASS_NAME)) + "# |"), CommonHelper.toNiceString(CommonHelperTests.CLAZZ));
    }

    @Test
    public void testToNiceStringWithParameter() {
        Assert.assertEquals((((((("#" + (CommonHelperTests.CLASS_NAME)) + "# | ") + (CommonHelperTests.NAME)) + ": ") + (CommonHelperTests.VALUE)) + " |"), CommonHelper.toNiceString(CommonHelperTests.CLAZZ, CommonHelperTests.NAME, CommonHelperTests.VALUE));
    }

    @Test
    public void testToNiceStringWithParameters() {
        Assert.assertEquals((((((((((("#" + (CommonHelperTests.CLASS_NAME)) + "# | ") + (CommonHelperTests.NAME)) + ": ") + (CommonHelperTests.VALUE)) + " | ") + (CommonHelperTests.NAME)) + ": ") + (CommonHelperTests.VALUE)) + " |"), CommonHelper.toNiceString(CommonHelperTests.CLAZZ, CommonHelperTests.NAME, CommonHelperTests.VALUE, CommonHelperTests.NAME, CommonHelperTests.VALUE));
    }

    @Test
    public void testAreEqualsOk() {
        Assert.assertTrue(CommonHelper.areEquals(null, null));
        Assert.assertTrue(CommonHelper.areEquals(CommonHelperTests.VALUE, CommonHelperTests.VALUE));
    }

    @Test
    public void testAreEqualsIgnoreCaseAndTrimOk() {
        Assert.assertTrue(CommonHelper.areEqualsIgnoreCaseAndTrim(null, null));
        Assert.assertTrue(CommonHelper.areEqualsIgnoreCaseAndTrim((" " + (CommonHelperTests.VALUE.toUpperCase())), ((CommonHelperTests.VALUE) + "                ")));
    }

    @Test
    public void testAreEqualsFails() {
        Assert.assertFalse(CommonHelper.areEquals(CommonHelperTests.VALUE, null));
        Assert.assertFalse(CommonHelper.areEquals(null, CommonHelperTests.VALUE));
        Assert.assertFalse(CommonHelper.areEquals(CommonHelperTests.NAME, CommonHelperTests.VALUE));
    }

    @Test
    public void testAreEqualsIgnoreCaseAndTrimFails() {
        Assert.assertFalse(CommonHelper.areEqualsIgnoreCaseAndTrim(CommonHelperTests.VALUE, null));
        Assert.assertFalse(CommonHelper.areEqualsIgnoreCaseAndTrim(CommonHelperTests.NAME, CommonHelperTests.VALUE));
    }

    @Test(expected = TechnicalException.class)
    public void testAssertNotBlank_null() {
        String var = null;
        CommonHelper.assertNotBlank("var", var);
    }

    @Test(expected = TechnicalException.class)
    public void testAssertNotBlank_empty() {
        String var = " ";
        CommonHelper.assertNotBlank("var", var);
    }

    @Test
    public void testAssertNotBlank_notBlank() {
        String var = "contents";
        CommonHelper.assertNotBlank("var", var);
    }

    @Test(expected = TechnicalException.class)
    public void testAssertNotNull_null() {
        String var = null;
        CommonHelper.assertNotNull("var", var);
    }

    @Test
    public void testAssertNotNull_notBlank() {
        String var = "contents";
        CommonHelper.assertNotNull("var", var);
    }

    @Test
    public void testAssertNull_null() {
        CommonHelper.assertNull("var", null);
    }

    @Test(expected = TechnicalException.class)
    public void testAssertNull_notNull() {
        CommonHelper.assertNull("var", "notnull");
    }

    @Test
    public void testRandomStringNChars() {
        Arrays.asList(0, 10, 31, 32, 33, 39).forEach(( i) -> testRandomString(i));
    }

    @Test
    public void testSubstringAfter() {
        Assert.assertEquals("after", CommonHelper.substringAfter("before###after", "###"));
    }

    @Test
    public void testSubstringBefore() {
        Assert.assertEquals("before", CommonHelper.substringBefore("before###after", "###"));
    }

    @Test
    public void testSubstringBetween() {
        Assert.assertEquals("bet", CommonHelper.substringBetween("123startbet##456", "start", "##"));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(CommonHelper.isEmpty(null));
        Assert.assertTrue(CommonHelper.isEmpty(new ArrayList()));
        Assert.assertFalse(CommonHelper.isEmpty(Arrays.asList(new String[]{ CommonHelperTests.VALUE })));
    }

    @Test
    public void testIsNotEmpty() {
        Assert.assertFalse(CommonHelper.isNotEmpty(null));
        Assert.assertFalse(CommonHelper.isNotEmpty(new ArrayList()));
        Assert.assertTrue(CommonHelper.isNotEmpty(Arrays.asList(new String[]{ CommonHelperTests.VALUE })));
    }

    @Test
    public void testGetConstructorOK() throws Exception {
        Constructor constructor = CommonHelper.getConstructor(CommonProfile.class.getName());
        final CommonProfile profile = ((CommonProfile) (constructor.newInstance()));
        Assert.assertNotNull(profile);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testGetConstructorMissingClass() throws Exception {
        CommonHelper.getConstructor("this.class.does.not.Exist");
    }
}

