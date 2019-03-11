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
package org.apache.druid.query.lookup;


import ClassNamesResourceConfig.PROPERTY_CLASSNAMES;
import WebComponent.RESOURCE_CONFIG_CLASS;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class LookupIntrospectionResourceImplTest extends JerseyTest {
    static LookupReferencesManager lookupReferencesManager = EasyMock.createMock(LookupReferencesManager.class);

    @Provider
    public static class MockTodoServiceProvider extends SingletonTypeInjectableProvider<Context, LookupReferencesManager> {
        public MockTodoServiceProvider() {
            super(LookupReferencesManager.class, LookupIntrospectionResourceImplTest.lookupReferencesManager);
        }
    }

    public LookupIntrospectionResourceImplTest() {
        super(new WebAppDescriptor.Builder().initParam(RESOURCE_CONFIG_CLASS, ClassNamesResourceConfig.class.getName()).initParam(PROPERTY_CLASSNAMES, (((((LookupIntrospectionResource.class.getName()) + ';') + (LookupIntrospectionResourceImplTest.MockTodoServiceProvider.class.getName())) + ';') + (LookupIntrospectHandler.class.getName()))).build());
    }

    @Test
    public void testGetKey() {
        WebResource r = resource().path("/druid/v1/lookups/introspect/lookupId1/keys");
        String s = r.get(String.class);
        Assert.assertEquals("[key, key2]", s);
    }

    @Test
    public void testGetValue() {
        WebResource r = resource().path("/druid/v1/lookups/introspect/lookupId1/values");
        String s = r.get(String.class);
        Assert.assertEquals("[value, value2]", s);
    }

    @Test
    public void testGetMap() {
        WebResource r = resource().path("/druid/v1/lookups/introspect/lookupId1/");
        String s = r.get(String.class);
        Assert.assertEquals("{\"key\":\"value\",\"key2\":\"value2\"}", s);
    }
}

