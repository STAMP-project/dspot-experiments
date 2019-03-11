/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller.internal;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.ResourceAlreadyExistsException;
import org.apache.ambari.server.orm.dao.KerberosDescriptorDAO;
import org.apache.ambari.server.orm.entities.KerberosDescriptorEntity;
import org.apache.ambari.server.topology.KerberosDescriptorFactory;
import org.easymock.Capture;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Rule;
import org.junit.Test;


public class KerberosDescriptorResourceProviderTest {
    @Rule
    public final EasyMockRule mocks = new EasyMockRule(this);

    @Mock
    private KerberosDescriptorDAO kerberosDescriptorDAO;

    private final KerberosDescriptorFactory kerberosDescriptorFactory = new KerberosDescriptorFactory();

    @Mock
    private Request request;

    private KerberosDescriptorResourceProvider kerberosDescriptorResourceProvider;

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCreateWithoutDescriptorText() throws Exception {
        // GIVEN
        expect(request.getProperties()).andReturn(KerberosDescriptorResourceProviderTest.descriptorNamed("any name")).anyTimes();
        expect(request.getRequestInfoProperties()).andReturn(ImmutableMap.of()).anyTimes();
        replay(request);
        // WHEN
        kerberosDescriptorResourceProvider.createResources(request);
        // THEN
        // exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCreateWithoutName() throws Exception {
        // GIVEN
        expect(request.getProperties()).andReturn(ImmutableSet.of()).anyTimes();
        replay(request);
        // WHEN
        kerberosDescriptorResourceProvider.createResources(request);
        // THEN
        // exception is thrown
    }

    @Test
    public void acceptsValidRequest() throws Exception {
        // GIVEN
        String name = "some name";
        String text = "any text";
        Capture<KerberosDescriptorEntity> entityCapture = creatingDescriptor(name, text);
        replay(request, kerberosDescriptorDAO);
        // WHEN
        kerberosDescriptorResourceProvider.createResources(request);
        // THEN
        verifyDescriptorCreated(entityCapture, name, text);
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void rejectsDuplicateName() throws Exception {
        String name = "any name";
        descriptorAlreadyExists(name);
        tryingToCreateDescriptor(name, "any text");
        replay(request, kerberosDescriptorDAO);
        kerberosDescriptorResourceProvider.createResources(request);
    }

    @Test
    public void canCreateDescriptorWithDifferentName() throws Exception {
        // GIVEN
        descriptorAlreadyExists("some name");
        String name = "another name";
        String text = "any text";
        Capture<KerberosDescriptorEntity> entityCapture = creatingDescriptor(name, text);
        replay(request, kerberosDescriptorDAO);
        // WHEN
        kerberosDescriptorResourceProvider.createResources(request);
        // THEN
        verifyDescriptorCreated(entityCapture, name, text);
    }
}

