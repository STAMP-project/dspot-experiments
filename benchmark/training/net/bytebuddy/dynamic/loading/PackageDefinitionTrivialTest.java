package net.bytebuddy.dynamic.loading;


import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.Trivial.INSTANCE;


public class PackageDefinitionTrivialTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testPackageNotDefined() throws Exception {
        PackageDefinitionStrategy.Definition definition = INSTANCE.define(getClass().getClassLoader(), PackageDefinitionTrivialTest.FOO, PackageDefinitionTrivialTest.BAR);
        MatcherAssert.assertThat(definition.isDefined(), CoreMatchers.is(true));
        MatcherAssert.assertThat(definition.getImplementationTitle(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getImplementationVersion(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getImplementationVendor(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getSpecificationTitle(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getSpecificationVersion(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getSpecificationVendor(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getSealBase(), CoreMatchers.nullValue(URL.class));
        MatcherAssert.assertThat(definition.isCompatibleTo(getClass().getPackage()), CoreMatchers.is(true));
    }
}

