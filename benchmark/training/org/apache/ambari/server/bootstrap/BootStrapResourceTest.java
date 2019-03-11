/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.bootstrap;


import BSStat.ERROR;
import MediaType.APPLICATION_JSON;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import junit.framework.Assert;
import org.apache.ambari.server.api.rest.BootStrapResource;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing bootstrap API.
 */
public class BootStrapResourceTest extends JerseyTest {
    static String PACKAGE_NAME = "org.apache.ambari.server.api.rest";

    private static final Logger LOG = LoggerFactory.getLogger(BootStrapResourceTest.class);

    Injector injector;

    BootStrapImpl bsImpl;

    public BootStrapResourceTest() {
        super(new WebAppDescriptor.Builder(BootStrapResourceTest.PACKAGE_NAME).servletClass(ServletContainer.class).build());
    }

    public class MockModule extends AbstractModule {
        @Override
        protected void configure() {
            BootStrapImpl bsImpl = Mockito.mock(BootStrapImpl.class);
            Mockito.when(bsImpl.getStatus(0)).thenReturn(generateDummyBSStatus());
            Mockito.when(bsImpl.runBootStrap(ArgumentMatchers.any(SshHostInfo.class))).thenReturn(generateBSResponse());
            bind(BootStrapImpl.class).toInstance(bsImpl);
            requestStaticInjection(BootStrapResource.class);
        }
    }

    @Test
    public void bootStrapGet() throws UniformInterfaceException, JSONException {
        WebResource webResource = resource();
        BootStrapStatus status = webResource.path("/bootstrap/0").type(APPLICATION_JSON).get(BootStrapStatus.class);
        BootStrapResourceTest.LOG.info(((("GET Response from the API " + (status.getLog())) + " ") + (status.getStatus())));
        Assert.assertEquals(status.getStatus(), ERROR);
    }

    @Test
    public void bootStrapPost() throws UniformInterfaceException, JSONException {
        WebResource webResource = resource();
        JSONObject object = webResource.path("/bootstrap").type(APPLICATION_JSON).post(JSONObject.class, createDummySshInfo());
        Assert.assertEquals("OK", object.get("status"));
    }
}

