package net.bytebuddy.dynamic.loading;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.test.utility.IntegrationRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;
import static java.util.jar.Attributes.Name.SEALED;


public class PackageDefinitionStrategyTypeSimpleTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    private PackageDefinitionStrategy.Definition definition;

    private URL sealBase;

    @Test
    public void testIsDefined() throws Exception {
        MatcherAssert.assertThat(definition.isDefined(), CoreMatchers.is(true));
    }

    @Test
    public void testSpecificationTitle() throws Exception {
        MatcherAssert.assertThat(definition.getSpecificationTitle(), CoreMatchers.is(PackageDefinitionStrategyTypeSimpleTest.FOO));
    }

    @Test
    public void testSpecificationVersion() throws Exception {
        MatcherAssert.assertThat(definition.getSpecificationVersion(), CoreMatchers.is(PackageDefinitionStrategyTypeSimpleTest.BAR));
    }

    @Test
    public void testSpecificationVendor() throws Exception {
        MatcherAssert.assertThat(definition.getSpecificationVendor(), CoreMatchers.is(PackageDefinitionStrategyTypeSimpleTest.QUX));
    }

    @Test
    public void testImplementationTitle() throws Exception {
        MatcherAssert.assertThat(definition.getImplementationTitle(), CoreMatchers.is(PackageDefinitionStrategyTypeSimpleTest.BAZ));
    }

    @Test
    public void testImplementationVersion() throws Exception {
        MatcherAssert.assertThat(definition.getImplementationVersion(), CoreMatchers.is(((PackageDefinitionStrategyTypeSimpleTest.FOO) + (PackageDefinitionStrategyTypeSimpleTest.BAR))));
    }

    @Test
    public void testImplementationVendor() throws Exception {
        MatcherAssert.assertThat(definition.getImplementationVendor(), CoreMatchers.is(((PackageDefinitionStrategyTypeSimpleTest.QUX) + (PackageDefinitionStrategyTypeSimpleTest.BAZ))));
    }

    @Test
    @IntegrationRule.Enforce
    public void testSealBase() throws Exception {
        MatcherAssert.assertThat(definition.getSealBase(), CoreMatchers.is(sealBase));
    }

    @Test
    public void testSealedNotCompatibleToUnsealed() throws Exception {
        MatcherAssert.assertThat(definition.isCompatibleTo(getClass().getPackage()), CoreMatchers.is(false));
    }

    @Test
    public void testNonSealedIsCompatibleToUnsealed() throws Exception {
        MatcherAssert.assertThat(new PackageDefinitionStrategy.Definition.Simple(PackageDefinitionStrategyTypeSimpleTest.FOO, PackageDefinitionStrategyTypeSimpleTest.BAR, PackageDefinitionStrategyTypeSimpleTest.QUX, PackageDefinitionStrategyTypeSimpleTest.BAZ, ((PackageDefinitionStrategyTypeSimpleTest.FOO) + (PackageDefinitionStrategyTypeSimpleTest.BAR)), ((PackageDefinitionStrategyTypeSimpleTest.QUX) + (PackageDefinitionStrategyTypeSimpleTest.BAZ)), null).isCompatibleTo(getClass().getPackage()), CoreMatchers.is(true));
    }

    @Test
    public void testNonSealedIsCompatibleToSealed() throws Exception {
        File file = File.createTempFile(PackageDefinitionStrategyTypeSimpleTest.FOO, PackageDefinitionStrategyTypeSimpleTest.BAR);
        try {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(SEALED, Boolean.TRUE.toString());
            URL url = new ByteBuddy().subclass(Object.class).name("foo.Bar").make().toJar(file, manifest).toURI().toURL();
            ClassLoader classLoader = new URLClassLoader(new URL[]{ url }, null);
            Package definedPackage = classLoader.loadClass("foo.Bar").getPackage();
            MatcherAssert.assertThat(new PackageDefinitionStrategy.Definition.Simple(PackageDefinitionStrategyTypeSimpleTest.FOO, PackageDefinitionStrategyTypeSimpleTest.BAR, PackageDefinitionStrategyTypeSimpleTest.QUX, PackageDefinitionStrategyTypeSimpleTest.BAZ, ((PackageDefinitionStrategyTypeSimpleTest.FOO) + (PackageDefinitionStrategyTypeSimpleTest.BAR)), ((PackageDefinitionStrategyTypeSimpleTest.QUX) + (PackageDefinitionStrategyTypeSimpleTest.BAZ)), null).isCompatibleTo(definedPackage), CoreMatchers.is(false));
        } finally {
            file.deleteOnExit();
        }
    }

    @Test
    public void testSealedIsCompatibleToSealed() throws Exception {
        File file = File.createTempFile(PackageDefinitionStrategyTypeSimpleTest.FOO, PackageDefinitionStrategyTypeSimpleTest.BAR);
        try {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(SEALED, Boolean.TRUE.toString());
            URL url = new ByteBuddy().subclass(Object.class).name("foo.Bar").make().toJar(file, manifest).toURI().toURL();
            ClassLoader classLoader = new URLClassLoader(new URL[]{ url }, null);
            Package definedPackage = classLoader.loadClass("foo.Bar").getPackage();
            MatcherAssert.assertThat(new PackageDefinitionStrategy.Definition.Simple(PackageDefinitionStrategyTypeSimpleTest.FOO, PackageDefinitionStrategyTypeSimpleTest.BAR, PackageDefinitionStrategyTypeSimpleTest.QUX, PackageDefinitionStrategyTypeSimpleTest.BAZ, ((PackageDefinitionStrategyTypeSimpleTest.FOO) + (PackageDefinitionStrategyTypeSimpleTest.BAR)), ((PackageDefinitionStrategyTypeSimpleTest.QUX) + (PackageDefinitionStrategyTypeSimpleTest.BAZ)), url).isCompatibleTo(definedPackage), CoreMatchers.is(true));
        } finally {
            file.deleteOnExit();
        }
    }
}

