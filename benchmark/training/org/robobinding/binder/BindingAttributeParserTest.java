package org.robobinding.binder;


import BindingAttributeParser.ROBOBINDING_NAMESPACE;
import android.util.AttributeSet;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
public class BindingAttributeParserTest {
    private BindingAttributeParser bindingAttributeParser = new BindingAttributeParser();

    @Test
    public void givenAttributeSetWithNoBindingAttributes_whenLoading_thenBindingMapShouldBeEmpty() {
        Map<String, String> bindingMap = loadBindingMapFromAttributeSet(MockAttributeSet.withNoBindingAttributes());
        Assert.assertTrue(bindingMap.isEmpty());
    }

    @Test
    public void givenAttributeSetWithXBindingAttributes_whenLoading_thenBindingMapShouldContainXEntries() {
        int numberOfBindingAttributes = anyNumber();
        int numberOfNonBindingAttributes = anyNumber();
        Map<String, String> bindingMap = loadBindingMapFromAttributeSet(MockAttributeSet.withAttributes(numberOfBindingAttributes, numberOfNonBindingAttributes));
        Assert.assertThat(bindingMap.size(), CoreMatchers.equalTo(numberOfBindingAttributes));
    }

    @Test
    public void givenAttributeSetWithBindingAttributes_whenLoading_thenBindingMapKeysShouldMapToCorrectValues() {
        int numberOfBindingAttributes = anyNumber();
        int numberOfNonBindingAttributes = anyNumber();
        AttributeSet attributeSetWithAttributes = MockAttributeSet.withAttributes(numberOfBindingAttributes, numberOfNonBindingAttributes);
        Map<String, String> bindingMap = loadBindingMapFromAttributeSet(attributeSetWithAttributes);
        for (String attribute : bindingMap.keySet()) {
            Assert.assertThat(bindingMap.get(attribute), CoreMatchers.equalTo(attributeSetWithAttributes.getAttributeValue(ROBOBINDING_NAMESPACE, attribute)));
        }
    }
}

