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
package org.apache.druid.common.aws;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import java.util.Properties;
import java.util.UUID;
import javax.validation.Validation;
import org.apache.druid.guice.JsonConfigProvider;
import org.apache.druid.metadata.DefaultPasswordProvider;
import org.junit.Assert;
import org.junit.Test;


public class AWSCredentialsConfigTest {
    private static final String PROPERTY_PREFIX = UUID.randomUUID().toString();

    private static final String SOME_SECRET = "someSecret";

    private final Properties properties = new Properties();

    @Test
    public void testStringProperty() {
        properties.put(((AWSCredentialsConfigTest.PROPERTY_PREFIX) + ".accessKey"), AWSCredentialsConfigTest.SOME_SECRET);
        properties.put(((AWSCredentialsConfigTest.PROPERTY_PREFIX) + ".secretKey"), AWSCredentialsConfigTest.SOME_SECRET);
        final Injector injector = Guice.createInjector(( binder) -> {
            binder.bindConstant().annotatedWith(Names.named("serviceName")).to("druid/test/redis");
            binder.bindConstant().annotatedWith(Names.named("servicePort")).to(0);
            binder.bindConstant().annotatedWith(Names.named("tlsServicePort")).to((-1));
            binder.bind(.class).toInstance(Validation.buildDefaultValidatorFactory().getValidator());
            binder.bindScope(.class, Scopes.SINGLETON);
            binder.bind(.class).in(.class);
            binder.bind(.class).toInstance(properties);
            JsonConfigProvider.bind(binder, PROPERTY_PREFIX, .class);
        });
        final AWSCredentialsConfig credentialsConfig = injector.getInstance(AWSCredentialsConfig.class);
        Assert.assertEquals(AWSCredentialsConfigTest.SOME_SECRET, credentialsConfig.getAccessKey().getPassword());
        Assert.assertEquals(AWSCredentialsConfigTest.SOME_SECRET, credentialsConfig.getSecretKey().getPassword());
    }

    @Test
    public void testJsonProperty() throws Exception {
        final String someSecret = new ObjectMapper().writeValueAsString(new DefaultPasswordProvider(AWSCredentialsConfigTest.SOME_SECRET));
        properties.put(((AWSCredentialsConfigTest.PROPERTY_PREFIX) + ".accessKey"), someSecret);
        properties.put(((AWSCredentialsConfigTest.PROPERTY_PREFIX) + ".secretKey"), someSecret);
        final Injector injector = Guice.createInjector(( binder) -> {
            binder.bindConstant().annotatedWith(Names.named("serviceName")).to("druid/test/redis");
            binder.bindConstant().annotatedWith(Names.named("servicePort")).to(0);
            binder.bindConstant().annotatedWith(Names.named("tlsServicePort")).to((-1));
            binder.bind(.class).toInstance(Validation.buildDefaultValidatorFactory().getValidator());
            binder.bindScope(.class, Scopes.SINGLETON);
            binder.bind(.class).in(.class);
            binder.bind(.class).toInstance(properties);
            JsonConfigProvider.bind(binder, PROPERTY_PREFIX, .class);
        });
        final AWSCredentialsConfig credentialsConfig = injector.getInstance(AWSCredentialsConfig.class);
        Assert.assertEquals(AWSCredentialsConfigTest.SOME_SECRET, credentialsConfig.getAccessKey().getPassword());
        Assert.assertEquals(AWSCredentialsConfigTest.SOME_SECRET, credentialsConfig.getSecretKey().getPassword());
    }
}

