/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.guice;


import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public abstract class JsonConfigTesterBase<T> {
    protected static final String configPrefix = "druid.test.prefix";

    protected Injector injector;

    protected final Class<T> clazz = ((Class<T>) (((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0]));

    protected Map<String, String> propertyValues = new HashMap<>();

    protected int assertions = 0;

    protected Properties testProperties = new Properties();

    private final Module simpleJsonConfigModule = new Module() {
        @Override
        public void configure(Binder binder) {
            binder.bindConstant().annotatedWith(Names.named("serviceName")).to("druid/test");
            binder.bindConstant().annotatedWith(Names.named("servicePort")).to(0);
            binder.bindConstant().annotatedWith(Names.named("tlsServicePort")).to((-1));
            JsonConfigProvider.bind(binder, JsonConfigTesterBase.configPrefix, clazz);
        }
    };

    protected JsonConfigurator configurator;

    protected JsonConfigProvider<T> configProvider;

    @Test
    public final void simpleInjectionTest() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        configProvider.inject(testProperties, configurator);
        validateEntries(configProvider.get().get());
        Assert.assertEquals(propertyValues.size(), assertions);
    }
}

