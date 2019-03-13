package org.kie.pmml.pmml_4_2.model;


import ExternalBeanDefinition.DEFAULT_BEAN_PKG;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.pmml.pmml_4_2.model.ExternalBeanRef.ModelUsage;


public class ExternalBeanRefTest {
    private static final String pkgName = "org.drools.scorecard.example";

    private static final String clsName = "Attribute";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testValidExternalRef() {
        ExternalBeanRef ref;
        try {
            ref = new ExternalBeanRef("attribute", "org.drools.scorecard.example.Attribute", ModelUsage.MINING);
            Assert.assertNotNull(ref);
            Assert.assertEquals(ExternalBeanRefTest.pkgName, ref.getBeanPackageName());
            Assert.assertEquals(ExternalBeanRefTest.clsName, ref.getBeanName());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testExternalRefMissingPackage() {
        ExternalBeanRef ref;
        try {
            ref = new ExternalBeanRef("attribute", "Attribute", ModelUsage.MINING);
            Assert.assertNotNull(ref);
            Assert.assertEquals(DEFAULT_BEAN_PKG, ref.getBeanPackageName());
            Assert.assertEquals(ExternalBeanRefTest.clsName, ref.getBeanName());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testExternalRefEmptyName() throws Exception {
        ExternalBeanRef ref;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unable to construct ExternalBeanRef.");
        ref = new ExternalBeanRef("attribute", "", ModelUsage.MINING);
        Assert.fail("Expected an Exception due to empty bean information string");
    }
}

