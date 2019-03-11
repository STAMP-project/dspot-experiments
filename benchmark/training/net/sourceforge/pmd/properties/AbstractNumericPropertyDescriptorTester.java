/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.properties;


import PropertyDescriptorField.MAX;
import PropertyDescriptorField.MIN;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Cl?ment Fournier
 */
public abstract class AbstractNumericPropertyDescriptorTester<T> extends AbstractPropertyDescriptorTester<T> {
    public AbstractNumericPropertyDescriptorTester(String typeName) {
        super(typeName);
    }

    @Test
    public void testLowerUpperLimit() {
        Assert.assertNotNull(((NumericPropertyDescriptor<T>) (createProperty())).lowerLimit());
        Assert.assertNotNull(((NumericPropertyDescriptor<T>) (createProperty())).upperLimit());
        Assert.assertNotNull(((NumericPropertyDescriptor<T>) (createMultiProperty())).lowerLimit());
        Assert.assertNotNull(((NumericPropertyDescriptor<T>) (createMultiProperty())).upperLimit());
    }

    @Test(expected = RuntimeException.class)
    public void testMissingMinThreshold() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(MIN);
        getSingleFactory().build(attributes);
    }

    @Test(expected = RuntimeException.class)
    public void testMissingMaxThreshold() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(MAX);
        getSingleFactory().build(attributes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadDefaultValue() {
        singleBuilder().defaultValue(createBadValue()).build();
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testMultiBadDefaultValue() {
        multiBuilder().defaultValues(createValue(), createBadValue()).build();
    }
}

