package net.bytebuddy.dynamic.loading;


import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.Definition.Trivial.INSTANCE;


public class PackageDefinitionStrategyTypeTrivialTest {
    @Test
    public void testIsDefined() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isDefined(), CoreMatchers.is(true));
    }

    @Test
    public void testSpecificationTitle() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getSpecificationTitle(), CoreMatchers.nullValue(String.class));
    }

    @Test
    public void testSpecificationVersion() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getSpecificationVersion(), CoreMatchers.nullValue(String.class));
    }

    @Test
    public void testSpecificationVendor() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getSpecificationVendor(), CoreMatchers.nullValue(String.class));
    }

    @Test
    public void testImplementationTitle() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getImplementationTitle(), CoreMatchers.nullValue(String.class));
    }

    @Test
    public void testImplementationVersion() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getImplementationVersion(), CoreMatchers.nullValue(String.class));
    }

    @Test
    public void testImplementationVendor() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getImplementationVendor(), CoreMatchers.nullValue(String.class));
    }

    @Test
    public void testSealBase() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getSealBase(), CoreMatchers.nullValue(URL.class));
    }

    @Test
    public void testIsCompatibleTo() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isCompatibleTo(getClass().getPackage()), CoreMatchers.is(true));
    }
}

