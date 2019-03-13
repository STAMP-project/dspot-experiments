/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.properties;


import PropertyDescriptorField.LEGAL_PACKAGES;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author Cl?ment Fournier
 */
public abstract class AbstractPackagedPropertyDescriptorTester<T> extends AbstractPropertyDescriptorTester<T> {
    /* default */
    AbstractPackagedPropertyDescriptorTester(String typeName) {
        super(typeName);
    }

    @Test
    public void testMissingPackageNames() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(LEGAL_PACKAGES);
        getMultiFactory().build(attributes);// no exception, null is ok

        getSingleFactory().build(attributes);
    }
}

