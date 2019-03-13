/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.core.xml;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultClassResolver;
import org.apache.camel.impl.DefaultPackageScanClassResolver;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spi.ExecutorServiceManager;
import org.apache.camel.spi.Injector;
import org.apache.camel.spi.ManagementNameStrategy;
import org.apache.camel.spi.RuntimeEndpointRegistry;
import org.apache.camel.support.ObjectHelper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;


public class AbstractCamelContextFactoryBeanTest {
    // any properties (abstract methods in AbstractCamelContextFactoryBean that
    // return String and receive no arguments) that do not support property
    // placeholders
    Set<String> propertiesThatAreNotPlaceholdered = Collections.singleton("{{getErrorHandlerRef}}");

    TypeConverter typeConverter = new org.apache.camel.impl.converter.DefaultTypeConverter(new DefaultPackageScanClassResolver(), new Injector() {
        @Override
        public <T> T newInstance(Class<T> type) {
            return ObjectHelper.newInstance(type);
        }

        @Override
        public boolean supportsAutoWiring() {
            return false;
        }
    }, new org.apache.camel.impl.DefaultFactoryFinder(new DefaultClassResolver(), "META-INF/services/org/apache/camel/"), false);

    // properties that should return value that can be converted to boolean
    Set<String> valuesThatReturnBoolean = new HashSet<>(Arrays.asList("{{getStreamCache}}", "{{getTrace}}", "{{getMessageHistory}}", "{{getLogMask}}", "{{getLogExhaustedMessageBody}}", "{{getHandleFault}}", "{{getAutoStartup}}", "{{getUseMDCLogging}}", "{{getUseDataType}}", "{{getUseBreadcrumb}}", "{{getAllowUseOriginalMessage}}"));

    // properties that should return value that can be converted to long
    Set<String> valuesThatReturnLong = new HashSet<>(Arrays.asList("{{getDelayer}}"));

    public AbstractCamelContextFactoryBeanTest() throws Exception {
        start();
    }

    @Test
    public void shouldSupportPropertyPlaceholdersOnAllProperties() throws Exception {
        final Set<Invocation> invocations = new LinkedHashSet<>();
        final ModelCamelContext context = Mockito.mock(ModelCamelContext.class, Mockito.withSettings().invocationListeners(( i) -> invocations.add(((Invocation) (i.getInvocation())))));
        // program the property resolution in context mock
        Mockito.when(context.resolvePropertyPlaceholders(ArgumentMatchers.anyString())).thenAnswer(( invocation) -> {
            final String placeholder = invocation.getArgument(0);
            // we receive the argument and check if the method should return a
            // value that can be converted to boolean
            if ((valuesThatReturnBoolean.contains(placeholder)) || (placeholder.endsWith("Enabled}}"))) {
                return "true";
            }
            // or long
            if (valuesThatReturnLong.contains(placeholder)) {
                return "1";
            }
            // else is just plain string
            return "string";
        });
        Mockito.when(context.getTypeConverter()).thenReturn(typeConverter);
        Mockito.when(context.getRuntimeEndpointRegistry()).thenReturn(Mockito.mock(RuntimeEndpointRegistry.class));
        Mockito.when(context.getManagementNameStrategy()).thenReturn(Mockito.mock(ManagementNameStrategy.class));
        Mockito.when(context.getExecutorServiceManager()).thenReturn(Mockito.mock(ExecutorServiceManager.class));
        @SuppressWarnings("unchecked")
        final AbstractCamelContextFactoryBean<ModelCamelContext> factory = Mockito.mock(AbstractCamelContextFactoryBean.class);
        Mockito.when(factory.getContext()).thenReturn(context);
        Mockito.doCallRealMethod().when(factory).initCamelContext(context);
        final Set<String> expectedPropertiesToBeResolved = propertiesToBeResolved(factory);
        // method under test
        factory.initCamelContext(context);
        // we want to capture the arguments initCamelContext tried to resolve
        // and check if it tried to resolve all placeholders we expected
        final ArgumentCaptor<String> capturedPlaceholders = ArgumentCaptor.forClass(String.class);
        Mockito.verify(context, Mockito.atLeastOnce()).resolvePropertyPlaceholders(capturedPlaceholders.capture());
        // removes any properties that are not using property placeholders
        expectedPropertiesToBeResolved.removeAll(propertiesThatAreNotPlaceholdered);
        assertThat(capturedPlaceholders.getAllValues()).as(("The expectation is that all abstract getter methods that return Strings should support property " + ("placeholders, and that for those will delegate to CamelContext::resolvePropertyPlaceholders, " + "we captured all placeholders that tried to resolve and found differences"))).containsAll(expectedPropertiesToBeResolved);
    }
}

