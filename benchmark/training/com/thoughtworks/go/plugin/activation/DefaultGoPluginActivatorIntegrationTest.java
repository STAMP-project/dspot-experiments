/**
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.plugin.activation;


import com.thoughtworks.go.plugin.activation.test.AbstractTestPlugin;
import com.thoughtworks.go.plugin.activation.test.ClassThatExtendsTestExtensionPoint;
import com.thoughtworks.go.plugin.activation.test.ClassWhichUsesSomeClassInJavaxPackage;
import com.thoughtworks.go.plugin.activation.test.ClassWhichUsesSomeClassesInOrgW3CDomPackage;
import com.thoughtworks.go.plugin.activation.test.ClassWhichUsesSomeClassesInOrgXMLSaxPackage;
import com.thoughtworks.go.plugin.activation.test.DummyClassProvidingAnonymousClass;
import com.thoughtworks.go.plugin.activation.test.DummyClassWithLocalInnerClass;
import com.thoughtworks.go.plugin.activation.test.DummyGoPluginWhichThrowsAnExceptionDuringConstruction;
import com.thoughtworks.go.plugin.activation.test.DummyGoPluginWithOneArgConstructorOnly;
import com.thoughtworks.go.plugin.activation.test.DummyTestPlugin;
import com.thoughtworks.go.plugin.activation.test.DummyTestPluginWithNonPublicDefaultConstructor;
import com.thoughtworks.go.plugin.activation.test.NotAGoExtensionAsItDoesNotImplementAnyExtensionPoints;
import com.thoughtworks.go.plugin.activation.test.NotAGoExtensionPoint;
import com.thoughtworks.go.plugin.activation.test.TestGoPluginExtensionInterface;
import com.thoughtworks.go.plugin.activation.test.TestGoPluginExtensionThatImplementsTwoExtensionPoints;
import com.thoughtworks.go.plugin.activation.test.TestPluginOuterClass;
import com.thoughtworks.go.plugin.activation.test.TestPluginThatIsADerivedClass;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.TestGoPluginExtensionPoint;
import com.thoughtworks.go.plugin.infra.FelixGoPluginOSGiFramework;
import com.thoughtworks.go.plugin.infra.plugininfo.DefaultPluginRegistry;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import lib.test.DummyTestPluginInLibDirectory;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import static com.thoughtworks.go.plugin.activation.test.DummyClassProvidingAnonymousClass.DummyInnerClassProvidingAnonymousClass.getAnonymousClass;


public class DefaultGoPluginActivatorIntegrationTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File tmpDir;

    private static final String BUNDLE_DIR_WHICH_HAS_PROPER_ACTIVATOR = "DefaultGoPluginActivatorIntegrationTest.bundleDirWhichHasProperActivator";

    private static final String NO_EXT_ERR_MSG = "No extensions found in this plugin.Please check for @Extension annotations";

    private static final String GO_TEST_DUMMY_SYMBOLIC_NAME = "Go-Test-Dummy-Symbolic-Name";

    private FelixGoPluginOSGiFramework framework;

    private DefaultGoPluginActivatorIntegrationTest.StubOfDefaultPluginRegistry registry;

    @Test
    public void shouldRegisterAClassImplementingGoPluginAsAnOSGiService() throws Exception {
        assertThatPluginWithThisExtensionClassLoadsSuccessfully(DummyTestPlugin.class);
    }

    @Test
    public void shouldNotRegisterAsAnOSGiServiceAClassImplementingGoPluginWithoutAPublicConstructor() throws Exception {
        Bundle bundle = installBundleWithClasses(DummyTestPluginWithNonPublicDefaultConstructor.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotRegisterAsAnOSGiServiceAClassImplementingGoPluginWithOnlyAOneArgConstructor() throws Exception {
        Bundle bundle = installBundleWithClasses(DummyGoPluginWithOneArgConstructorOnly.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        String error = descriptor.getStatus().getMessages().get(0);
        Assert.assertThat(error.contains("DummyGoPluginWithOneArgConstructorOnly"), Matchers.is(true));
        Assert.assertThat(error.contains("Make sure it and all of its parent classes have a default constructor."), Matchers.is(true));
    }

    @Test
    public void shouldNotRegisterAsAnOSGiServiceAnExtensionClassWhichDoesNotImplementAGoExtensionPoint() throws Exception {
        Bundle bundle = installBundleWithClasses(NotAGoExtensionPoint.class, NotAGoExtensionAsItDoesNotImplementAnyExtensionPoints.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotLoadClassesFoundInMETA_INFEvenIfTheyAreProperGoExtensionPoints() throws Exception {
        File bundleWithActivator = createBundleWithActivator(DefaultGoPluginActivatorIntegrationTest.BUNDLE_DIR_WHICH_HAS_PROPER_ACTIVATOR, DummyTestPlugin.class);
        File sourceClassFile = new File(bundleWithActivator, "com/thoughtworks/go/plugin/activation/test/DummyTestPlugin.class");
        File destinationFile = new File(bundleWithActivator, "META-INF/com/thoughtworks/go/plugin/activation/test/");
        FileUtils.moveFileToDirectory(sourceClassFile, destinationFile, true);
        Bundle bundle = installBundleFoundInDirectory(bundleWithActivator);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotFailToRegisterOtherClassesIfAClassCannotBeLoadedBecauseOfWrongPath() throws Exception {
        File bundleWithActivator = createBundleWithActivator(DefaultGoPluginActivatorIntegrationTest.BUNDLE_DIR_WHICH_HAS_PROPER_ACTIVATOR, DummyTestPlugin.class);
        File sourceClassFile = new File(bundleWithActivator, "com/thoughtworks/go/plugin/activation/test/DummyTestPlugin.class");
        File destinationFile = new File(bundleWithActivator, "ABC-DEF/com/thoughtworks/go/plugin/activation/test/");
        FileUtils.copyFileToDirectory(sourceClassFile, destinationFile, true);
        Bundle bundle = installBundleFoundInDirectory(bundleWithActivator);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
    }

    @Test
    public void shouldNotLoadAClassFoundInLibDirectoryEvenIfItIsAProperGoExtensionPoints() throws Exception {
        Bundle bundle = installBundleWithClasses(DummyTestPluginInLibDirectory.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotRegisterAsAnOSGiServiceAClassWhichIsAbstract() throws Exception {
        Bundle bundle = installBundleWithClasses(AbstractTestPlugin.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotRegisterAsAnOSGiServiceAClassWhichIsNotPublic() throws Exception {
        Bundle bundle = installBundleWithClasses(DummyTestPluginWhichIsNotPublic.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotRegisterAsAnOSGiServiceAnInterfaceEvenIfItImplementsAGoExtensionPointInterface() throws Exception {
        Bundle bundle = installBundleWithClasses(TestGoPluginExtensionInterface.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotRegisterAsAnOSGiServiceAClassWhichThrowsExceptionDuringInstantiation() throws Exception {
        Bundle bundle = installBundleWithClasses(DummyTestPlugin.class, DummyGoPluginWhichThrowsAnExceptionDuringConstruction.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        String error = descriptor.getStatus().getMessages().get(0);
        Assert.assertThat(error.contains("DummyGoPluginWhichThrowsAnExceptionDuringConstruction"), Matchers.is(true));
        Assert.assertThat(error.contains("java.lang.RuntimeException: Ouch! I failed!"), Matchers.is(true));
    }

    @Test
    public void shouldRegisterANestedClassImplementingGoPluginAsAnOSGiService() throws Exception {
        if (satisfy()) {
            return;// The class files in this test become too big for a Windows filesystem to handle.

        }
        File bundleWithActivator = createBundleWithActivator(DefaultGoPluginActivatorIntegrationTest.BUNDLE_DIR_WHICH_HAS_PROPER_ACTIVATOR, TestPluginOuterClass.class, TestPluginOuterClass.NestedClass.class, TestPluginOuterClass.InnerClass.class, TestPluginOuterClass.InnerClass.SecondLevelInnerClass.class, TestPluginOuterClass.InnerClass.SecondLevelInnerClass.TestPluginThirdLevelInnerClass.class, TestPluginOuterClass.InnerClass.SecondLevelSiblingInnerClassNoDefaultConstructor.class);
        BundleContext installedBundledContext = bundleContext(installBundleFoundInDirectory(bundleWithActivator));
        ServiceReference<?>[] references = installedBundledContext.getServiceReferences(GoPlugin.class.getName(), null);
        String[] services = toSortedServiceClassNames(installedBundledContext, references);
        Assert.assertEquals(Arrays.toString(services), 4, services.length);
        Assert.assertEquals(TestPluginOuterClass.class.getName(), services[0]);
        Assert.assertEquals(TestPluginOuterClass.InnerClass.class.getName(), services[1]);
        Assert.assertEquals(TestPluginOuterClass.InnerClass.SecondLevelInnerClass.TestPluginThirdLevelInnerClass.class.getName(), services[2]);
        Assert.assertEquals(TestPluginOuterClass.NestedClass.class.getName(), services[3]);
    }

    @Test
    public void shouldRegisterAsAnOSGiServiceADerivedClassWhoseAncestorImplementsAnExtensionPoint() throws Exception {
        BundleContext installedBundledContext = bundleContext(installBundleWithClasses(TestPluginThatIsADerivedClass.class, DummyTestPlugin.class, TestPluginThatIsADerivedClass.class.getSuperclass()));
        ServiceReference<?>[] references = installedBundledContext.getServiceReferences(GoPlugin.class.getName(), null);
        String[] services = toSortedServiceClassNames(installedBundledContext, references);
        Assert.assertEquals(Arrays.toString(services), 2, services.length);
        Assert.assertEquals(DummyTestPlugin.class.getName(), services[0]);
        Assert.assertEquals(TestPluginThatIsADerivedClass.class.getName(), services[1]);
    }

    @Test
    public void shouldRegisterOneInstanceForEachExtensionPointAnExtensionImplements() throws Exception {
        BundleContext installedBundledContext = bundleContext(installBundleWithClasses(TestGoPluginExtensionThatImplementsTwoExtensionPoints.class, DummyTestPlugin.class));
        ServiceReference<?>[] references = installedBundledContext.getServiceReferences(GoPlugin.class.getName(), null);
        String[] services = toSortedServiceClassNames(installedBundledContext, references);
        Assert.assertEquals(Arrays.toString(services), 2, services.length);
        Assert.assertEquals(DummyTestPlugin.class.getName(), services[0]);
        Assert.assertEquals(TestGoPluginExtensionThatImplementsTwoExtensionPoints.class.getName(), services[1]);
        references = installedBundledContext.getServiceReferences(TestGoPluginExtensionPoint.class.getName(), null);
        Assert.assertEquals(1, references.length);
        Assert.assertEquals(TestGoPluginExtensionThatImplementsTwoExtensionPoints.class.getName(), installedBundledContext.getService(references[0]).getClass().getName());
        Object testExtensionImplementation = getImplementationOfType(installedBundledContext, references, TestGoPluginExtensionThatImplementsTwoExtensionPoints.class);
        references = installedBundledContext.getServiceReferences(GoPlugin.class.getName(), null);
        Assert.assertEquals(2, references.length);
        Object testPluginImplementation = getImplementationOfType(installedBundledContext, references, TestGoPluginExtensionThatImplementsTwoExtensionPoints.class);
        Assert.assertSame(testExtensionImplementation, testPluginImplementation);
    }

    @Test
    public void shouldRegisterOneInstanceForEachExtensionPointWhereThePluginClassExtendsABaseClassWhichIsAnExtensionAndImplementsAGoExtensionPoint() throws Exception {
        BundleContext installedBundledContext = bundleContext(installBundleWithClasses(ClassThatExtendsTestExtensionPoint.class, ClassThatExtendsTestExtensionPoint.ClassThatExtendsTwoGoExtensionPoint.class, TestGoPluginExtensionPoint.class));
        ServiceReference<?>[] references = installedBundledContext.getServiceReferences(TestGoPluginExtensionPoint.class.getName(), null);
        Assert.assertEquals(1, references.length);
        Object testExtensionImplementation = getImplementationOfType(installedBundledContext, references, ClassThatExtendsTestExtensionPoint.ClassThatExtendsTwoGoExtensionPoint.class);
        references = installedBundledContext.getServiceReferences(GoPlugin.class.getName(), null);
        Assert.assertEquals(1, references.length);
        Object testPluginImplementation = getImplementationOfType(installedBundledContext, references, ClassThatExtendsTestExtensionPoint.ClassThatExtendsTwoGoExtensionPoint.class);
        Assert.assertSame(testExtensionImplementation, testPluginImplementation);
    }

    @Test
    public void shouldNotRegisterAnAnonymousClassThatImplementsAnExtensionPoint() throws IOException {
        Bundle bundle = installBundleWithClasses(DummyClassProvidingAnonymousClass.getAnonymousClass().getClass());
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotRegisterAnAnonymousClassDefinedWithinAnInnerClassThatImplementsAnExtensionPoint() throws IOException {
        Bundle bundle = installBundleWithClasses(getAnonymousClass().getClass());
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotRegisterLocalInnerClassesThatImplementAnExtensionPoint() throws IOException {
        Bundle bundle = installBundleWithClasses(DummyClassWithLocalInnerClass.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldNotRegisterPublicInnerClassesThatImplementAnExtensionPointInsidePackageLevelClass() throws IOException {
        Bundle bundle = installBundleWithClasses(PackageLevelClassWithPublicInnerClass.class, PackageLevelClassWithPublicInnerClass.DummyInnerClassWithExtension.class);
        Assert.assertThat(bundle.getState(), Matchers.is(Bundle.UNINSTALLED));
        GoPluginDescriptor descriptor = getPlugin(DefaultGoPluginActivatorIntegrationTest.GO_TEST_DUMMY_SYMBOLIC_NAME);
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
        Assert.assertThat(descriptor.getStatus().getMessages().contains(DefaultGoPluginActivatorIntegrationTest.NO_EXT_ERR_MSG), Matchers.is(true));
    }

    @Test
    public void shouldBeAbleToUsePackagesFromJavaxWithinThePluginSinceItHasBeenExportedUsingBootDelegationInTheOSGIFramework() throws Exception {
        assertThatPluginWithThisExtensionClassLoadsSuccessfully(ClassWhichUsesSomeClassInJavaxPackage.class);
    }

    @Test
    public void shouldBeAbleToUsePackagesFromOrgXmlSaxPackageWithinThePluginSinceItHasBeenExportedUsingBootDelegationInTheOSGIFramework() throws Exception {
        assertThatPluginWithThisExtensionClassLoadsSuccessfully(ClassWhichUsesSomeClassesInOrgXMLSaxPackage.class);
    }

    @Test
    public void shouldBeAbleToUsePackagesFromOrgW3cDomPackageWithinThePluginSinceItHasBeenExportedUsingBootDelegationInTheOSGIFramework() throws Exception {
        assertThatPluginWithThisExtensionClassLoadsSuccessfully(ClassWhichUsesSomeClassesInOrgW3CDomPackage.class);
    }

    private class StubOfDefaultPluginRegistry extends DefaultPluginRegistry {
        void fakeRegistrationOfPlugin(GoPluginDescriptor pluginDescriptor) {
            idToDescriptorMap.putIfAbsent(pluginDescriptor.id(), pluginDescriptor);
        }
    }
}

