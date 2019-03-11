package net.bytebuddy.dynamic.loading;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import net.bytebuddy.test.utility.IntegrationRule;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static java.util.jar.Attributes.Name.IMPLEMENTATION_TITLE;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_VENDOR;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_VERSION;
import static java.util.jar.Attributes.Name.MANIFEST_VERSION;
import static java.util.jar.Attributes.Name.SEALED;
import static java.util.jar.Attributes.Name.SPECIFICATION_TITLE;
import static java.util.jar.Attributes.Name.SPECIFICATION_VENDOR;
import static java.util.jar.Attributes.Name.SPECIFICATION_VERSION;
import static net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.ManifestReading.SealBaseLocator.NonSealing.INSTANCE;


public class PackageTypeStrategyManifestReadingTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public MethodRule integrationRule = new IntegrationRule();

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private PackageDefinitionStrategy.ManifestReading.SealBaseLocator sealBaseLocator;

    @Mock
    private ClassLoader classLoader;

    private URL url;

    @Test
    public void testNoManifest() throws Exception {
        PackageDefinitionStrategy packageDefinitionStrategy = new PackageDefinitionStrategy.ManifestReading(sealBaseLocator);
        PackageDefinitionStrategy.Definition definition = packageDefinitionStrategy.define(classLoader, PackageTypeStrategyManifestReadingTest.FOO, PackageTypeStrategyManifestReadingTest.BAR);
        MatcherAssert.assertThat(definition.isDefined(), CoreMatchers.is(true));
        MatcherAssert.assertThat(definition.getImplementationTitle(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getImplementationVersion(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getImplementationVendor(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getSpecificationTitle(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getSpecificationVersion(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getSpecificationVendor(), CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(definition.getSealBase(), CoreMatchers.nullValue(URL.class));
        MatcherAssert.assertThat(definition.isCompatibleTo(getClass().getPackage()), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(sealBaseLocator);
    }

    @Test
    public void testManifestMainAttributesNotSealed() throws Exception {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(SPECIFICATION_TITLE, PackageTypeStrategyManifestReadingTest.FOO);
        manifest.getMainAttributes().put(SPECIFICATION_VERSION, PackageTypeStrategyManifestReadingTest.BAR);
        manifest.getMainAttributes().put(SPECIFICATION_VENDOR, PackageTypeStrategyManifestReadingTest.QUX);
        manifest.getMainAttributes().put(IMPLEMENTATION_TITLE, PackageTypeStrategyManifestReadingTest.BAZ);
        manifest.getMainAttributes().put(IMPLEMENTATION_VERSION, ((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.BAR)));
        manifest.getMainAttributes().put(IMPLEMENTATION_VENDOR, ((PackageTypeStrategyManifestReadingTest.QUX) + (PackageTypeStrategyManifestReadingTest.BAZ)));
        manifest.getMainAttributes().put(SEALED, Boolean.FALSE.toString());
        Mockito.when(classLoader.getResourceAsStream(JarFile.MANIFEST_NAME)).then(new Answer<InputStream>() {
            public InputStream answer(InvocationOnMock invocationOnMock) throws Throwable {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                manifest.write(outputStream);
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
        });
        PackageDefinitionStrategy packageDefinitionStrategy = new PackageDefinitionStrategy.ManifestReading(sealBaseLocator);
        PackageDefinitionStrategy.Definition definition = packageDefinitionStrategy.define(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)), (((((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".") + (PackageTypeStrategyManifestReadingTest.QUX)));
        MatcherAssert.assertThat(definition.isDefined(), CoreMatchers.is(true));
        MatcherAssert.assertThat(definition.getSpecificationTitle(), CoreMatchers.is(PackageTypeStrategyManifestReadingTest.FOO));
        MatcherAssert.assertThat(definition.getSpecificationVersion(), CoreMatchers.is(PackageTypeStrategyManifestReadingTest.BAR));
        MatcherAssert.assertThat(definition.getSpecificationVendor(), CoreMatchers.is(PackageTypeStrategyManifestReadingTest.QUX));
        MatcherAssert.assertThat(definition.getImplementationTitle(), CoreMatchers.is(PackageTypeStrategyManifestReadingTest.BAZ));
        MatcherAssert.assertThat(definition.getImplementationVersion(), CoreMatchers.is(((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.BAR))));
        MatcherAssert.assertThat(definition.getImplementationVendor(), CoreMatchers.is(((PackageTypeStrategyManifestReadingTest.QUX) + (PackageTypeStrategyManifestReadingTest.BAZ))));
        MatcherAssert.assertThat(definition.getSealBase(), CoreMatchers.nullValue(URL.class));
        MatcherAssert.assertThat(definition.isCompatibleTo(getClass().getPackage()), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(sealBaseLocator);
    }

    @Test
    public void testManifestPackageAttributesNotSealed() throws Exception {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(SPECIFICATION_TITLE, ((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.QUX)));
        manifest.getMainAttributes().put(SPECIFICATION_VERSION, ((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.QUX)));
        manifest.getMainAttributes().put(SPECIFICATION_VENDOR, ((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.QUX)));
        manifest.getMainAttributes().put(IMPLEMENTATION_TITLE, ((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.QUX)));
        manifest.getMainAttributes().put(IMPLEMENTATION_VERSION, ((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.QUX)));
        manifest.getMainAttributes().put(IMPLEMENTATION_VENDOR, ((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.QUX)));
        manifest.getMainAttributes().put(SEALED, ((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.QUX)));
        manifest.getEntries().put(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/"), new Attributes());
        manifest.getAttributes(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/")).put(SPECIFICATION_TITLE, PackageTypeStrategyManifestReadingTest.FOO);
        manifest.getAttributes(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/")).put(SPECIFICATION_VERSION, PackageTypeStrategyManifestReadingTest.BAR);
        manifest.getAttributes(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/")).put(SPECIFICATION_VENDOR, PackageTypeStrategyManifestReadingTest.QUX);
        manifest.getAttributes(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/")).put(IMPLEMENTATION_TITLE, PackageTypeStrategyManifestReadingTest.BAZ);
        manifest.getAttributes(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/")).put(IMPLEMENTATION_VERSION, ((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.BAR)));
        manifest.getAttributes(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/")).put(IMPLEMENTATION_VENDOR, ((PackageTypeStrategyManifestReadingTest.QUX) + (PackageTypeStrategyManifestReadingTest.BAZ)));
        manifest.getAttributes(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/")).put(SEALED, Boolean.FALSE.toString());
        Mockito.when(classLoader.getResourceAsStream(JarFile.MANIFEST_NAME)).then(new Answer<InputStream>() {
            public InputStream answer(InvocationOnMock invocationOnMock) throws Throwable {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                manifest.write(outputStream);
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
        });
        PackageDefinitionStrategy packageDefinitionStrategy = new PackageDefinitionStrategy.ManifestReading(sealBaseLocator);
        PackageDefinitionStrategy.Definition definition = packageDefinitionStrategy.define(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)), (((((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".") + (PackageTypeStrategyManifestReadingTest.QUX)));
        MatcherAssert.assertThat(definition.isDefined(), CoreMatchers.is(true));
        MatcherAssert.assertThat(definition.getSpecificationTitle(), CoreMatchers.is(PackageTypeStrategyManifestReadingTest.FOO));
        MatcherAssert.assertThat(definition.getSpecificationVersion(), CoreMatchers.is(PackageTypeStrategyManifestReadingTest.BAR));
        MatcherAssert.assertThat(definition.getSpecificationVendor(), CoreMatchers.is(PackageTypeStrategyManifestReadingTest.QUX));
        MatcherAssert.assertThat(definition.getImplementationTitle(), CoreMatchers.is(PackageTypeStrategyManifestReadingTest.BAZ));
        MatcherAssert.assertThat(definition.getImplementationVersion(), CoreMatchers.is(((PackageTypeStrategyManifestReadingTest.FOO) + (PackageTypeStrategyManifestReadingTest.BAR))));
        MatcherAssert.assertThat(definition.getImplementationVendor(), CoreMatchers.is(((PackageTypeStrategyManifestReadingTest.QUX) + (PackageTypeStrategyManifestReadingTest.BAZ))));
        MatcherAssert.assertThat(definition.getSealBase(), CoreMatchers.nullValue(URL.class));
        MatcherAssert.assertThat(definition.isCompatibleTo(getClass().getPackage()), CoreMatchers.is(true));
        Mockito.verifyZeroInteractions(sealBaseLocator);
    }

    @Test
    @IntegrationRule.Enforce
    public void testManifestMainAttributesSealed() throws Exception {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(SEALED, Boolean.TRUE.toString());
        Mockito.when(classLoader.getResourceAsStream(JarFile.MANIFEST_NAME)).then(new Answer<InputStream>() {
            public InputStream answer(InvocationOnMock invocationOnMock) throws Throwable {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                manifest.write(outputStream);
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
        });
        Mockito.when(sealBaseLocator.findSealBase(classLoader, (((((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".") + (PackageTypeStrategyManifestReadingTest.QUX)))).thenReturn(url);
        PackageDefinitionStrategy packageDefinitionStrategy = new PackageDefinitionStrategy.ManifestReading(sealBaseLocator);
        PackageDefinitionStrategy.Definition definition = packageDefinitionStrategy.define(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)), (((((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".") + (PackageTypeStrategyManifestReadingTest.QUX)));
        MatcherAssert.assertThat(definition.isDefined(), CoreMatchers.is(true));
        MatcherAssert.assertThat(definition.getSealBase(), CoreMatchers.is(url));
        Mockito.verify(sealBaseLocator).findSealBase(classLoader, (((((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".") + (PackageTypeStrategyManifestReadingTest.QUX)));
        Mockito.verifyNoMoreInteractions(sealBaseLocator);
    }

    @Test
    @IntegrationRule.Enforce
    public void testManifestPackageAttributesSealed() throws Exception {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
        manifest.getEntries().put(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/"), new Attributes());
        manifest.getAttributes(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + "/")).put(SEALED, Boolean.TRUE.toString());
        Mockito.when(classLoader.getResourceAsStream(JarFile.MANIFEST_NAME)).then(new Answer<InputStream>() {
            public InputStream answer(InvocationOnMock invocationOnMock) throws Throwable {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                manifest.write(outputStream);
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
        });
        Mockito.when(sealBaseLocator.findSealBase(classLoader, (((((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".") + (PackageTypeStrategyManifestReadingTest.QUX)))).thenReturn(url);
        PackageDefinitionStrategy packageDefinitionStrategy = new PackageDefinitionStrategy.ManifestReading(sealBaseLocator);
        PackageDefinitionStrategy.Definition definition = packageDefinitionStrategy.define(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)), (((((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".") + (PackageTypeStrategyManifestReadingTest.QUX)));
        MatcherAssert.assertThat(definition.isDefined(), CoreMatchers.is(true));
        MatcherAssert.assertThat(definition.getSealBase(), CoreMatchers.is(url));
        Mockito.verify(sealBaseLocator).findSealBase(classLoader, (((((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".") + (PackageTypeStrategyManifestReadingTest.QUX)));
        Mockito.verifyNoMoreInteractions(sealBaseLocator);
    }

    @Test
    public void testSealBaseLocatorNonSealing() throws Exception {
        MatcherAssert.assertThat(INSTANCE.findSealBase(classLoader, PackageTypeStrategyManifestReadingTest.FOO), CoreMatchers.nullValue(URL.class));
    }

    @Test
    @IntegrationRule.Enforce
    public void testSealBaseLocatorForFixedValue() throws Exception {
        MatcherAssert.assertThat(new PackageDefinitionStrategy.ManifestReading.SealBaseLocator.ForFixedValue(url).findSealBase(classLoader, PackageTypeStrategyManifestReadingTest.FOO), CoreMatchers.is(url));
    }

    @Test
    @IntegrationRule.Enforce
    public void testSealBaseLocatorForTypeResourceUrlUnknownUrl() throws Exception {
        Mockito.when(sealBaseLocator.findSealBase(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)))).thenReturn(url);
        MatcherAssert.assertThat(new PackageDefinitionStrategy.ManifestReading.SealBaseLocator.ForTypeResourceUrl(sealBaseLocator).findSealBase(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR))), CoreMatchers.is(url));
        Mockito.verify(sealBaseLocator).findSealBase(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR)));
        Mockito.verifyNoMoreInteractions(sealBaseLocator);
    }

    @Test
    @IntegrationRule.Enforce
    public void testSealBaseLocatorForTypeResourceUrlFileUrl() throws Exception {
        URL url = new URL("file:/foo");
        Mockito.when(classLoader.getResource(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".class"))).thenReturn(url);
        MatcherAssert.assertThat(new PackageDefinitionStrategy.ManifestReading.SealBaseLocator.ForTypeResourceUrl(sealBaseLocator).findSealBase(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR))), CoreMatchers.is(url));
        Mockito.verifyZeroInteractions(sealBaseLocator);
    }

    @Test
    @IntegrationRule.Enforce
    public void testSealBaseLocatorForTypeResourceUrlJarUrl() throws Exception {
        URL url = new URL("jar:file:/foo.jar!/bar");
        Mockito.when(classLoader.getResource(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".class"))).thenReturn(url);
        MatcherAssert.assertThat(new PackageDefinitionStrategy.ManifestReading.SealBaseLocator.ForTypeResourceUrl(sealBaseLocator).findSealBase(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR))), CoreMatchers.is(new URL("file:/foo.jar")));
        Mockito.verifyZeroInteractions(sealBaseLocator);
    }

    @Test
    @IntegrationRule.Enforce
    @JavaVersionRule.Enforce(9)
    public void testSealBaseLocatorForTypeResourceUrlJavaRuntimeImageUrl() throws Exception {
        URL url = new URL("jrt:/foo/bar");
        Mockito.when(classLoader.getResource(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".class"))).thenReturn(url);
        MatcherAssert.assertThat(new PackageDefinitionStrategy.ManifestReading.SealBaseLocator.ForTypeResourceUrl(sealBaseLocator).findSealBase(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR))), CoreMatchers.is(new URL("jrt:/foo")));
        Mockito.verifyZeroInteractions(sealBaseLocator);
    }

    @Test
    @IntegrationRule.Enforce
    @JavaVersionRule.Enforce(9)
    public void testSealBaseLocatorForTypeResourceUrlJavaRuntimeImageUrlRawModule() throws Exception {
        URL url = new URL("jrt:/foo");
        Mockito.when(classLoader.getResource(((((PackageTypeStrategyManifestReadingTest.FOO) + "/") + (PackageTypeStrategyManifestReadingTest.BAR)) + ".class"))).thenReturn(url);
        MatcherAssert.assertThat(new PackageDefinitionStrategy.ManifestReading.SealBaseLocator.ForTypeResourceUrl(sealBaseLocator).findSealBase(classLoader, (((PackageTypeStrategyManifestReadingTest.FOO) + ".") + (PackageTypeStrategyManifestReadingTest.BAR))), CoreMatchers.is(new URL("jrt:/foo")));
        Mockito.verifyZeroInteractions(sealBaseLocator);
    }
}

