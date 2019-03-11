/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;


import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKeyUtil;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.ParameterizedMetricKey;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Cl?ment Fournier
 */
public class ParameterizedMetricKeyTest {
    private static final MetricOptions DUMMY_VERSION_1 = MetricOptions.ofOptions(ParameterizedMetricKeyTest.Options.DUMMY1, ParameterizedMetricKeyTest.Options.DUMMY2);

    private static final MetricOptions DUMMY_VERSION_2 = MetricOptions.ofOptions(ParameterizedMetricKeyTest.Options.DUMMY2);

    @Test
    public void testIdentity() {
        for (JavaClassMetricKey key : JavaClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, ParameterizedMetricKeyTest.DUMMY_VERSION_1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, ParameterizedMetricKeyTest.DUMMY_VERSION_1);
            Assert.assertEquals(key1, key2);
            Assert.assertTrue((key1 == key2));
        }
        for (JavaOperationMetricKey key : JavaOperationMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, ParameterizedMetricKeyTest.DUMMY_VERSION_1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, ParameterizedMetricKeyTest.DUMMY_VERSION_1);
            Assert.assertEquals(key1, key2);
            Assert.assertTrue((key1 == key2));
        }
    }

    @Test
    public void testVersioning() {
        for (JavaClassMetricKey key : JavaClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, ParameterizedMetricKeyTest.DUMMY_VERSION_1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, ParameterizedMetricKeyTest.DUMMY_VERSION_2);
            Assert.assertNotEquals(key1, key2);
            Assert.assertFalse((key1 == key2));
        }
        for (JavaOperationMetricKey key : JavaOperationMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, ParameterizedMetricKeyTest.DUMMY_VERSION_1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, ParameterizedMetricKeyTest.DUMMY_VERSION_2);
            Assert.assertNotEquals(key1, key2);
            Assert.assertFalse((key1 == key2));
        }
    }

    @Test
    public void testToString() {
        for (JavaClassMetricKey key : JavaClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, ParameterizedMetricKeyTest.DUMMY_VERSION_1);
            Assert.assertTrue(key1.toString().contains(key1.key.name()));
            Assert.assertTrue(key1.toString().contains(key1.options.toString()));
        }
    }

    @Test
    public void testAdHocMetricKey() {
        MetricKey<ASTAnyTypeDeclaration> adHocKey = MetricKeyUtil.of("metric", null);
        ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(adHocKey, ParameterizedMetricKeyTest.DUMMY_VERSION_1);
        ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(adHocKey, ParameterizedMetricKeyTest.DUMMY_VERSION_1);
        Assert.assertNotNull(key1);
        Assert.assertNotNull(key2);
        Assert.assertTrue((key1 == key2));
        Assert.assertEquals(key1, key2);
        Assert.assertTrue(key1.toString().contains(key1.key.name()));
        Assert.assertTrue(key1.toString().contains(key1.options.toString()));
    }

    private enum Options implements MetricOption {

        DUMMY1,
        DUMMY2;
        @Override
        public String valueName() {
            return null;
        }
    }
}

