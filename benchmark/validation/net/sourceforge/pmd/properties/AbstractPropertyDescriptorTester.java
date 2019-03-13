/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.properties;


import PropertyDescriptorField.DEFAULT_VALUE;
import PropertyDescriptorField.DELIMITER;
import PropertyDescriptorField.DESCRIPTION;
import PropertyDescriptorField.NAME;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base functionality for all concrete subclasses that evaluate type-specific property descriptors. Checks for error
 * conditions during construction, error value detection, serialization, etc.
 *
 * @author Brian Remedios
 */
public abstract class AbstractPropertyDescriptorTester<T> {
    public static final String PUNCTUATION_CHARS = "!@#$%^&*()_-+=[]{}\\|;:\'\",.<>/?`~";

    public static final String WHITESPACE_CHARS = " \t\n";

    public static final String DIGIT_CHARS = "0123456789";

    public static final String ALPHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmniopqrstuvwxyz";

    public static final String ALPHA_NUMERIC_CHARS = (AbstractPropertyDescriptorTester.DIGIT_CHARS) + (AbstractPropertyDescriptorTester.ALPHA_CHARS);

    public static final String ALL_CHARS = ((AbstractPropertyDescriptorTester.PUNCTUATION_CHARS) + (AbstractPropertyDescriptorTester.WHITESPACE_CHARS)) + (AbstractPropertyDescriptorTester.ALPHA_NUMERIC_CHARS);

    private static final int MULTI_VALUE_COUNT = 10;

    private static final Random RANDOM = new Random();

    protected final String typeName;

    public AbstractPropertyDescriptorTester(String typeName) {
        this.typeName = typeName;
    }

    @Test
    public void testFactorySingleValue() {
        PropertyDescriptor<T> prop = getSingleFactory().build(getPropertyDescriptorValues());
        T originalValue = createValue();
        T value = prop.valueFrom((originalValue instanceof Class ? ((Class) (originalValue)).getName() : String.valueOf(originalValue)));
        T value2 = prop.valueFrom(prop.asDelimitedString(value));
        if (Pattern.class.equals(prop.type())) {
            // Pattern.equals uses object identity...
            // we're forced to do that to make it compare the string values of the pattern
            Assert.assertEquals(String.valueOf(value), String.valueOf(value2));
        } else {
            Assert.assertEquals(value, value2);
        }
    }

    @Test
    public void testFactoryMultiValueDefaultDelimiter() {
        PropertyDescriptorExternalBuilder<List<T>> multiFactory = getMultiFactory();
        PropertyDescriptor<List<T>> prop = multiFactory.build(getPropertyDescriptorValues());
        List<T> originalValue = createMultipleValues(AbstractPropertyDescriptorTester.MULTI_VALUE_COUNT);
        String asDelimitedString = prop.asDelimitedString(originalValue);
        List<T> value2 = prop.valueFrom(asDelimitedString);
        Assert.assertEquals(originalValue, value2);
    }

    @Test
    public void testFactoryMultiValueCustomDelimiter() {
        PropertyDescriptorExternalBuilder<List<T>> multiFactory = getMultiFactory();
        Map<PropertyDescriptorField, String> valuesById = getPropertyDescriptorValues();
        String customDelimiter = "?";
        Assert.assertFalse(AbstractPropertyDescriptorTester.ALL_CHARS.contains(customDelimiter));
        valuesById.put(DELIMITER, customDelimiter);
        PropertyDescriptor<List<T>> prop = multiFactory.build(valuesById);
        List<T> originalValue = createMultipleValues(AbstractPropertyDescriptorTester.MULTI_VALUE_COUNT);
        String asDelimitedString = prop.asDelimitedString(originalValue);
        List<T> value2 = prop.valueFrom(asDelimitedString);
        Assert.assertEquals(originalValue.toString(), value2.toString());
        Assert.assertEquals(originalValue, value2);
    }

    @Test
    public void testConstructors() {
        PropertyDescriptor<T> desc = createProperty();
        Assert.assertNotNull(desc);
        try {
            createBadProperty();
        } catch (Exception ex) {
            return;// caught ok

        }
        Assert.fail("uncaught constructor exception");
    }

    @Test
    public void testAsDelimitedString() {
        List<T> testValue = createMultipleValues(AbstractPropertyDescriptorTester.MULTI_VALUE_COUNT);
        PropertyDescriptor<List<T>> pmdProp = createMultiProperty();
        String storeValue = pmdProp.asDelimitedString(testValue);
        List<T> returnedValue = pmdProp.valueFrom(storeValue);
        Assert.assertEquals(returnedValue, testValue);
    }

    @Test
    public void testValueFrom() {
        T testValue = createValue();
        PropertyDescriptor<T> pmdProp = createProperty();
        String storeValue = pmdProp.asDelimitedString(testValue);
        T returnedValue = pmdProp.valueFrom(storeValue);
        if (Pattern.class.equals(pmdProp.type())) {
            // Pattern.equals uses object identity...
            // we're forced to do that to make it compare the string values of the pattern
            Assert.assertEquals(String.valueOf(returnedValue), String.valueOf(testValue));
        } else {
            Assert.assertEquals(returnedValue, testValue);
        }
    }

    @Test
    public void testErrorForCorrectSingle() {
        T testValue = createValue();
        PropertyDescriptor<T> pmdProp = createProperty();// plain vanilla

        // property & valid test value
        String errorMsg = pmdProp.errorFor(testValue);
        Assert.assertNull(errorMsg, errorMsg);
    }

    @Test
    public void testErrorForCorrectMulti() {
        List<T> testMultiValues = createMultipleValues(AbstractPropertyDescriptorTester.MULTI_VALUE_COUNT);// multi-value property, all

        // valid test values
        PropertyDescriptor<List<T>> multiProperty = createMultiProperty();
        String errorMsg = multiProperty.errorFor(testMultiValues);
        Assert.assertNull(errorMsg, errorMsg);
    }

    @Test
    public void testErrorForBadSingle() {
        T testValue = createBadValue();
        PropertyDescriptor<T> pmdProp = createProperty();// plain vanilla

        // property & valid test value
        String errorMsg = pmdProp.errorFor(testValue);
        Assert.assertNotNull(("uncaught bad value: " + testValue), errorMsg);
    }

    @Test
    public void testErrorForBadMulti() {
        List<T> testMultiValues = createMultipleBadValues(AbstractPropertyDescriptorTester.MULTI_VALUE_COUNT);// multi-value property, all

        // valid test values
        PropertyDescriptor<List<T>> multiProperty = createMultiProperty();
        String errorMsg = multiProperty.errorFor(testMultiValues);
        Assert.assertNotNull(("uncaught bad value in: " + testMultiValues), errorMsg);
    }

    @Test
    public void testIsMultiValue() {
        Assert.assertFalse(createProperty().isMultiValue());
    }

    @Test
    public void testIsMultiValueMulti() {
        Assert.assertTrue(createMultiProperty().isMultiValue());
    }

    @Test
    public void testAddAttributes() {
        Map<PropertyDescriptorField, String> atts = createProperty().attributeValuesById();
        Assert.assertTrue(atts.containsKey(NAME));
        Assert.assertTrue(atts.containsKey(DESCRIPTION));
        Assert.assertTrue(atts.containsKey(DEFAULT_VALUE));
    }

    @Test
    public void testAddAttributesMulti() {
        Map<PropertyDescriptorField, String> multiAtts = createMultiProperty().attributeValuesById();
        Assert.assertTrue(multiAtts.containsKey(DELIMITER));
        Assert.assertTrue(multiAtts.containsKey(NAME));
        Assert.assertTrue(multiAtts.containsKey(DESCRIPTION));
        Assert.assertTrue(multiAtts.containsKey(DEFAULT_VALUE));
    }

    @Test
    public void testType() {
        Assert.assertNotNull(createProperty().type());
    }

    @Test
    public void testTypeMulti() {
        Assert.assertNotNull(createMultiProperty().type());
    }
}

