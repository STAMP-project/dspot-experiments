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


import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.internal.api.LoggingService;
import com.thoughtworks.go.plugin.internal.api.PluginHealthService;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;


public class DefaultGoPluginActivatorTest {
    private static final String CONSTRUCTOR_FAIL_MSG = "Ouch! Failed construction";

    private static final String PLUGIN_ID = "plugin-id";

    private static final String NO_EXT_ERR_MSG = "No extensions found in this plugin.Please check for @Extension annotations";

    private DefaultGoPluginActivator activator;

    @Captor
    private ArgumentCaptor<List<String>> errorMessageCaptor;

    @Mock
    private BundleContext context;

    @Mock
    private Bundle bundle;

    @Mock
    private ServiceReference<PluginHealthService> pluginHealthServiceReference;

    @Mock
    private PluginHealthService pluginHealthService;

    @Mock
    private ServiceReference<LoggingService> loggingServiceReference;

    @Mock
    private LoggingService loggingService;

    private Enumeration<URL> emptyListOfClassesInBundle = new Hashtable<URL, String>().keys();

    @Test
    public void shouldReportAClassLoadErrorToThePluginHealthService() throws Exception {
        setupClassesInBundle("SomeClass.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.anyString())).thenThrow(new ClassNotFoundException("Ouch! Failed"));
        activator.start(context);
        verifyErrorsReported("Class [SomeClass] could not be loaded. Message: [Ouch! Failed].", DefaultGoPluginActivatorTest.NO_EXT_ERR_MSG);
    }

    @Test
    public void shouldReportMultipleClassLoadErrorsToThePluginHealthService() throws Exception {
        setupClassesInBundle("SomeClass.class", "SomeOtherClass.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.anyString())).thenThrow(new ClassNotFoundException("Ouch! Failed"));
        activator.start(context);
        verifyErrorsReported("Class [SomeClass] could not be loaded. Message: [Ouch! Failed].", "Class [SomeOtherClass] could not be loaded. Message: [Ouch! Failed].", DefaultGoPluginActivatorTest.NO_EXT_ERR_MSG);
    }

    @Test
    public void shouldReportAClassWhichIsAnnotatedAsAnExtensionIfItIsNotPublic() throws Exception {
        setupClassesInBundle("NonPublicGoExtensionClass.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("NonPublicGoExtensionClass"))).thenReturn(((Class) (NonPublicGoExtensionClass.class)));
        activator.start(context);
        verifyErrorsReported("Class [NonPublicGoExtensionClass] is annotated with @Extension but is not public.", DefaultGoPluginActivatorTest.NO_EXT_ERR_MSG);
    }

    @Test
    public void shouldNotReportAClassWhichIsNotAnnotatedAsAnExtensionEvenIfItIsNotPublic() throws Exception {
        setupClassesInBundle("NonPublicClassWhichIsNotAGoExtension.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("NonPublicClassWhichIsNotAGoExtension"))).thenReturn(((Class) (NonPublicClassWhichIsNotAGoExtension.class)));
        activator.start(context);
        verifyErrorsReported(DefaultGoPluginActivatorTest.NO_EXT_ERR_MSG);
    }

    @Test
    public void shouldReportAClassWhichIsAnnotatedAsAnExtensionIfItIsAbstract() throws Exception {
        setupClassesInBundle("PublicAbstractGoExtensionClass.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("PublicAbstractGoExtensionClass"))).thenReturn(((Class) (DefaultGoPluginActivatorTest.PublicAbstractGoExtensionClass.class)));
        activator.start(context);
        verifyErrorsReported("Class [PublicAbstractGoExtensionClass] is annotated with @Extension but is abstract.", DefaultGoPluginActivatorTest.NO_EXT_ERR_MSG);
    }

    @Test
    public void shouldReportAClassWhichIsAnnotatedAsAnExtensionIfItIsNotInstantiable() throws Exception {
        setupClassesInBundle("PublicGoExtensionClassWhichDoesNotHaveADefaultConstructor.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("PublicGoExtensionClassWhichDoesNotHaveADefaultConstructor"))).thenReturn(((Class) (DefaultGoPluginActivatorTest.PublicGoExtensionClassWhichDoesNotHaveADefaultConstructor.class)));
        activator.start(context);
        verifyErrorsReported(("Class [PublicGoExtensionClassWhichDoesNotHaveADefaultConstructor] is annotated with @Extension but cannot be constructed. " + "Make sure it and all of its parent classes have a default constructor."), DefaultGoPluginActivatorTest.NO_EXT_ERR_MSG);
    }

    @Test
    public void shouldReportAClassWhichIsAnnotatedAsAnExtensionIfItFailsDuringConstruction() throws Exception {
        setupClassesInBundle("PublicGoExtensionClassWhichThrowsAnExceptionInItsConstructor.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("PublicGoExtensionClassWhichThrowsAnExceptionInItsConstructor"))).thenReturn(((Class) (DefaultGoPluginActivatorTest.PublicGoExtensionClassWhichThrowsAnExceptionInItsConstructor.class)));
        activator.start(context);
        verifyErrorsReported(String.format("Class [PublicGoExtensionClassWhichThrowsAnExceptionInItsConstructor] is annotated with @Extension but cannot be constructed. Reason: java.lang.RuntimeException: %s.", DefaultGoPluginActivatorTest.CONSTRUCTOR_FAIL_MSG), DefaultGoPluginActivatorTest.NO_EXT_ERR_MSG);
    }

    @Test
    public void shouldSetupTheLoggerWithTheLoggingServiceAndPluginId() throws Exception {
        setupClassesInBundle();
        activator.start(context);
        Logger logger = Logger.getLoggerFor(DefaultGoPluginActivatorTest.class);
        logger.info("INFO");
        Mockito.verify(loggingService).info(DefaultGoPluginActivatorTest.PLUGIN_ID, DefaultGoPluginActivatorTest.class.getName(), "INFO");
    }

    @Test
    public void loggerShouldBeAvailableToBeUsedInStaticBlocksAndConstructorAndLoadUnloadMethodsOfPluginExtensionClasses() throws Exception {
        setupClassesInBundle("PublicGoExtensionClassWhichLogsInAStaticBlock.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("PublicGoExtensionClassWhichLogsInAStaticBlock"))).thenReturn(((Class) (PublicGoExtensionClassWhichLogsInAStaticBlock.class)));
        activator.start(context);
        activator.stop(context);
        Mockito.verify(loggingService).info(DefaultGoPluginActivatorTest.PLUGIN_ID, PublicGoExtensionClassWhichLogsInAStaticBlock.class.getName(), "HELLO from static block in PublicGoExtensionClassWhichLogsInAStaticBlock");
        Mockito.verify(loggingService).info(DefaultGoPluginActivatorTest.PLUGIN_ID, PublicGoExtensionClassWhichLogsInAStaticBlock.class.getName(), "HELLO from constructor in PublicGoExtensionClassWhichLogsInAStaticBlock");
        Mockito.verify(loggingService).info(DefaultGoPluginActivatorTest.PLUGIN_ID, PublicGoExtensionClassWhichLogsInAStaticBlock.class.getName(), "HELLO from load in PublicGoExtensionClassWhichLogsInAStaticBlock");
        Mockito.verify(loggingService).info(DefaultGoPluginActivatorTest.PLUGIN_ID, PublicGoExtensionClassWhichLogsInAStaticBlock.class.getName(), "HELLO from unload in PublicGoExtensionClassWhichLogsInAStaticBlock");
    }

    @Test
    public void shouldInvokeMethodWithLoadUnloadAnnotationAtPluginStart() throws Exception {
        setupClassesInBundle("GoExtensionWithLoadUnloadAnnotation.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("GoExtensionWithLoadUnloadAnnotation"))).thenReturn(((Class) (GoExtensionWithLoadUnloadAnnotation.class)));
        activator.start(context);
        Assert.assertThat(GoExtensionWithLoadUnloadAnnotation.loadInvoked, Matchers.is(1));
        activator.stop(context);
        Assert.assertThat(GoExtensionWithLoadUnloadAnnotation.unLoadInvoked, Matchers.is(1));
    }

    @Test
    public void shouldNotInvokeMethodWithLoadUnloadAnnotationAtPluginStartIfTheClassIsNotAnExtension() throws Exception {
        assertDidNotInvokeLoadUnload(NonExtensionWithLoadUnloadAnnotation.class);
    }

    @Test
    public void shouldNotInvokeStaticMethodWithLoadAnnotationAtPluginStart() throws Exception {
        assertDidNotInvokeLoadUnload(GoExtensionWithStaticLoadAnnotationMethod.class);
    }

    @Test
    public void shouldNotInvokeNonPublicMethodWithLoadAnnotationAtPluginStart() throws Exception {
        assertDidNotInvokeLoadUnload(GoExtensionWithNonPublicLoadUnloadAnnotation.class);
    }

    @Test
    public void shouldNotInvokePublicMethodWithLoadAnnotationHavingArgumentsAtPluginStart() throws Exception {
        assertDidNotInvokeLoadUnload(GoExtensionWithPublicLoadUnloadAnnotationWithArguments.class);
    }

    @Test
    public void shouldNotInvokeInheritedPublicMethodWithLoadAnnotationAtPluginStart() throws Exception {
        assertDidNotInvokeLoadUnload(GoExtensionWithInheritedPublicLoadUnloadAnnotationMethod.class);
    }

    @Test
    public void shouldGenerateExceptionWhenThereAreMoreThanOneLoadAnnotationsAtPluginStart() throws Exception {
        String expectedErrorMessageWithMethodsWithIncreasingOrder = "Class [GoExtensionWithMultipleLoadUnloadAnnotation] is annotated with @Extension will not be registered. " + (("Reason: java.lang.RuntimeException: More than one method with @Load annotation not allowed. " + "Methods Found: [public void com.thoughtworks.go.plugin.activation.GoExtensionWithMultipleLoadUnloadAnnotation.setupData1(com.thoughtworks.go.plugin.api.info.PluginContext), ") + "public void com.thoughtworks.go.plugin.activation.GoExtensionWithMultipleLoadUnloadAnnotation.setupData2(com.thoughtworks.go.plugin.api.info.PluginContext)].");
        String expectedErrorMessageWithMethodsWithDecreasingOrder = "Class [GoExtensionWithMultipleLoadUnloadAnnotation] is annotated with @Extension will not be registered. " + (("Reason: java.lang.RuntimeException: More than one method with @Load annotation not allowed. " + "Methods Found: [public void com.thoughtworks.go.plugin.activation.GoExtensionWithMultipleLoadUnloadAnnotation.setupData2(com.thoughtworks.go.plugin.api.info.PluginContext), ") + "public void com.thoughtworks.go.plugin.activation.GoExtensionWithMultipleLoadUnloadAnnotation.setupData1(com.thoughtworks.go.plugin.api.info.PluginContext)].");
        setupClassesInBundle("GoExtensionWithMultipleLoadUnloadAnnotation.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("GoExtensionWithMultipleLoadUnloadAnnotation"))).thenReturn(((Class) (GoExtensionWithMultipleLoadUnloadAnnotation.class)));
        activator.start(context);
        Assert.assertThat(activator.hasErrors(), Matchers.is(true));
        verifyThatOneOfTheErrorMessagesIsPresent(expectedErrorMessageWithMethodsWithIncreasingOrder, expectedErrorMessageWithMethodsWithDecreasingOrder);
        activator.stop(context);
        verifyThatOneOfTheErrorMessagesIsPresent(expectedErrorMessageWithMethodsWithIncreasingOrder, expectedErrorMessageWithMethodsWithDecreasingOrder);
    }

    @Test
    public void shouldHandleExceptionGeneratedByLoadMethodAtPluginStart() throws Exception {
        setupClassesInBundle("GoExtensionWithLoadAnnotationMethodThrowingException.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("GoExtensionWithLoadAnnotationMethodThrowingException"))).thenReturn(((Class) (GoExtensionWithLoadAnnotationMethodThrowingException.class)));
        activator.start(context);
        Assert.assertThat(activator.hasErrors(), Matchers.is(true));
        verifyErrorsReported(("Class [GoExtensionWithLoadAnnotationMethodThrowingException] is annotated with @Extension but cannot be registered. " + "Reason: java.io.IOException: Load Dummy Checked Exception."));
    }

    @Test
    public void shouldHandleExceptionGeneratedByUnLoadMethodAtPluginStop() throws Exception {
        setupClassesInBundle("GoExtensionWithUnloadAnnotationMethodThrowingException.class");
        Mockito.when(bundle.loadClass(ArgumentMatchers.contains("GoExtensionWithUnloadAnnotationMethodThrowingException"))).thenReturn(((Class) (GoExtensionWithUnloadAnnotationMethodThrowingException.class)));
        activator.start(context);
        Assert.assertThat(activator.hasErrors(), Matchers.is(false));
        activator.stop(context);
        Assert.assertThat(activator.hasErrors(), Matchers.is(true));
        verifyErrorsReported(("Invocation of unload method [public int com.thoughtworks.go.plugin.activation.GoExtensionWithUnloadAnnotationMethodThrowingException" + ((".throwExceptionAgain(com.thoughtworks.go.plugin.api.info.PluginContext) " + "throws java.io.IOException]. ") + "Reason: java.io.IOException: Unload Dummy Checked Exception.")));
    }

    @Test
    public void shouldRegisterServiceWithBothPluginIDAndExtensionTypeAsProperties() throws Exception {
        setupClassesInBundle("PublicGoExtensionClassWhichWillLoadSuccessfullyAndProvideAValidIdentifier.class");
        Mockito.when(bundle.loadClass("PublicGoExtensionClassWhichWillLoadSuccessfullyAndProvideAValidIdentifier")).thenReturn(((Class) (PublicGoExtensionClassWhichWillLoadSuccessfullyAndProvideAValidIdentifier.class)));
        Hashtable<String, String> expectedPropertiesUponRegistration = new Hashtable<>();
        expectedPropertiesUponRegistration.put(Constants.BUNDLE_SYMBOLICNAME, DefaultGoPluginActivatorTest.PLUGIN_ID);
        expectedPropertiesUponRegistration.put(Constants.BUNDLE_CATEGORY, "test-extension");
        activator.start(context);
        Assert.assertThat(activator.hasErrors(), Matchers.is(false));
        Mockito.verify(context).registerService(ArgumentMatchers.eq(GoPlugin.class), ArgumentMatchers.any(GoPlugin.class), ArgumentMatchers.eq(expectedPropertiesUponRegistration));
    }

    @Test
    public void shouldFailToRegisterServiceWhenExtensionTypeCannotBeSuccessfullyRetrieved() throws Exception {
        setupClassesInBundle("PublicGoExtensionClassWhichWillLoadSuccessfullyButThrowWhenAskedForPluginIdentifier.class");
        Mockito.when(bundle.loadClass("PublicGoExtensionClassWhichWillLoadSuccessfullyButThrowWhenAskedForPluginIdentifier")).thenReturn(((Class) (PublicGoExtensionClassWhichWillLoadSuccessfullyButThrowWhenAskedForPluginIdentifier.class)));
        activator.start(context);
        Assert.assertThat(activator.hasErrors(), Matchers.is(true));
        verifyErrorReportedContains("Unable to find extension type from plugin identifier in class com.thoughtworks.go.plugin.activation.PublicGoExtensionClassWhichWillLoadSuccessfullyButThrowWhenAskedForPluginIdentifier");
        Mockito.verify(context, Mockito.times(0)).registerService(ArgumentMatchers.eq(GoPlugin.class), ArgumentMatchers.any(GoPlugin.class), ArgumentMatchers.any());
    }

    @Extension
    public abstract class PublicAbstractGoExtensionClass {}

    @Extension
    public class PublicGoExtensionClassWhichDoesNotHaveADefaultConstructor implements GoPlugin {
        public PublicGoExtensionClassWhichDoesNotHaveADefaultConstructor(int x) {
        }

        @Override
        public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        }

        @Override
        public GoPluginApiResponse handle(GoPluginApiRequest requestMessage) throws UnhandledRequestTypeException {
            return null;
        }

        @Override
        public GoPluginIdentifier pluginIdentifier() {
            return null;
        }
    }

    @Extension
    public class PublicGoExtensionClassWhichThrowsAnExceptionInItsConstructor implements GoPlugin {
        public PublicGoExtensionClassWhichThrowsAnExceptionInItsConstructor() {
            throw new RuntimeException(DefaultGoPluginActivatorTest.CONSTRUCTOR_FAIL_MSG);
        }

        @Override
        public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        }

        @Override
        public GoPluginApiResponse handle(GoPluginApiRequest requestMessage) throws UnhandledRequestTypeException {
            return null;
        }

        @Override
        public GoPluginIdentifier pluginIdentifier() {
            return null;
        }
    }
}

