/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.config;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ConfigurationParameterResolverFactoryTest {
    private Configuration configuration;

    private ConfigurationParameterResolverFactory testSubject;

    private Parameter[] parameters;

    private Method method;

    private CommandBus commandBus;

    @Test
    public void testReturnsNullOnUnavailableParameter() {
        Assert.assertNull(testSubject.createInstance(method, parameters, 0));
        Mockito.verify(configuration).getComponent(String.class);
    }

    @Test
    public void testConfigurationContainsRequestedParameter() {
        ParameterResolver<?> actual = testSubject.createInstance(method, parameters, 1);
        Assert.assertNotNull(actual);
        Assert.assertSame(commandBus, actual.resolveParameterValue(new org.axonframework.messaging.GenericMessage("test")));
        Mockito.verify(configuration).getComponent(CommandBus.class);
    }
}

