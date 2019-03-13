package net.bytebuddy.dynamic.loading;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.Definition.Undefined.INSTANCE;


public class PackageDefinitionStrategyTypeUndefinedTest {
    @Test
    public void testIsUndefined() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isDefined(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testSpecificationTitleThrowsException() throws Exception {
        INSTANCE.getSpecificationTitle();
    }

    @Test(expected = IllegalStateException.class)
    public void testSpecificationVersionThrowsException() throws Exception {
        INSTANCE.getSpecificationVersion();
    }

    @Test(expected = IllegalStateException.class)
    public void testSpecificationVendorThrowsException() throws Exception {
        INSTANCE.getSpecificationVendor();
    }

    @Test(expected = IllegalStateException.class)
    public void testImplementationTitleThrowsException() throws Exception {
        INSTANCE.getImplementationTitle();
    }

    @Test(expected = IllegalStateException.class)
    public void testImplementationVersionThrowsException() throws Exception {
        INSTANCE.getImplementationVersion();
    }

    @Test(expected = IllegalStateException.class)
    public void testImplementationVendorThrowsException() throws Exception {
        INSTANCE.getImplementationVendor();
    }

    @Test(expected = IllegalStateException.class)
    public void testSealBaseThrowsException() throws Exception {
        INSTANCE.getSealBase();
    }

    @Test(expected = IllegalStateException.class)
    public void testIsCompatibleToThrowsException() throws Exception {
        INSTANCE.isCompatibleTo(getClass().getPackage());
    }
}

