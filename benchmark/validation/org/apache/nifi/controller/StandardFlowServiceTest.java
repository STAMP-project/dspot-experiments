/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller;


import ScheduledStateLookup.IDENTITY_LOOKUP;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.admin.service.AuditService;
import org.apache.nifi.authorization.Authorizer;
import org.apache.nifi.cluster.protocol.StandardDataFlow;
import org.apache.nifi.controller.repository.FlowFileEventRepository;
import org.apache.nifi.controller.serialization.FlowSerializationException;
import org.apache.nifi.controller.serialization.StandardFlowSerializer;
import org.apache.nifi.encrypt.StringEncryptor;
import org.apache.nifi.nar.ExtensionManager;
import org.apache.nifi.registry.VariableRegistry;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.web.revision.RevisionManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 *
 */
@Ignore
public class StandardFlowServiceTest {
    private StandardFlowService flowService;

    private FlowController flowController;

    private NiFiProperties properties;

    private FlowFileEventRepository mockFlowFileEventRepository;

    private Authorizer authorizer;

    private AuditService mockAuditService;

    private StringEncryptor mockEncryptor;

    private RevisionManager revisionManager;

    private VariableRegistry variableRegistry;

    private ExtensionManager extensionManager;

    @Test
    public void testLoadWithFlow() throws IOException {
        byte[] flowBytes = IOUtils.toByteArray(StandardFlowServiceTest.class.getResourceAsStream("/conf/all-flow.xml"));
        flowService.load(new StandardDataFlow(flowBytes, null, null, new HashSet()));
        StandardFlowSerializer serializer = new StandardFlowSerializer(mockEncryptor);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Document doc = serializer.transform(flowController, IDENTITY_LOOKUP);
        serializer.serialize(doc, baos);
        String expectedFlow = new String(flowBytes).trim();
        String actualFlow = new String(baos.toByteArray()).trim();
        Assert.assertEquals(expectedFlow, actualFlow);
    }

    @Test(expected = FlowSerializationException.class)
    public void testLoadWithCorruptFlow() throws IOException {
        byte[] flowBytes = IOUtils.toByteArray(StandardFlowServiceTest.class.getResourceAsStream("/conf/all-flow-corrupt.xml"));
        flowService.load(new StandardDataFlow(flowBytes, null, null, new HashSet()));
    }

    @Test
    public void testLoadExistingFlow() throws IOException {
        byte[] flowBytes = IOUtils.toByteArray(StandardFlowServiceTest.class.getResourceAsStream("/conf/all-flow.xml"));
        flowService.load(new StandardDataFlow(flowBytes, null, null, new HashSet()));
        flowBytes = IOUtils.toByteArray(StandardFlowServiceTest.class.getResourceAsStream("/conf/all-flow-inheritable.xml"));
        flowService.load(new StandardDataFlow(flowBytes, null, null, new HashSet()));
        StandardFlowSerializer serializer = new StandardFlowSerializer(mockEncryptor);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Document doc = serializer.transform(flowController, IDENTITY_LOOKUP);
        serializer.serialize(doc, baos);
        String expectedFlow = new String(flowBytes).trim();
        String actualFlow = new String(baos.toByteArray()).trim();
        Assert.assertEquals(expectedFlow, actualFlow);
    }

    @Test
    public void testLoadExistingFlowWithUninheritableFlow() throws IOException {
        byte[] originalBytes = IOUtils.toByteArray(StandardFlowServiceTest.class.getResourceAsStream("/conf/all-flow.xml"));
        flowService.load(new StandardDataFlow(originalBytes, null, null, new HashSet()));
        try {
            byte[] updatedBytes = IOUtils.toByteArray(StandardFlowServiceTest.class.getResourceAsStream("/conf/all-flow-uninheritable.xml"));
            flowService.load(new StandardDataFlow(updatedBytes, null, null, new HashSet()));
            Assert.fail(("should have thrown " + (UninheritableFlowException.class)));
        } catch (UninheritableFlowException ufe) {
            StandardFlowSerializer serializer = new StandardFlowSerializer(mockEncryptor);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final Document doc = serializer.transform(flowController, IDENTITY_LOOKUP);
            serializer.serialize(doc, baos);
            String expectedFlow = new String(originalBytes).trim();
            String actualFlow = new String(baos.toByteArray()).trim();
            Assert.assertEquals(expectedFlow, actualFlow);
        }
    }

    @Test
    public void testLoadExistingFlowWithCorruptFlow() throws IOException {
        byte[] originalBytes = IOUtils.toByteArray(StandardFlowServiceTest.class.getResourceAsStream("/conf/all-flow.xml"));
        flowService.load(new StandardDataFlow(originalBytes, null, null, new HashSet()));
        try {
            byte[] updatedBytes = IOUtils.toByteArray(StandardFlowServiceTest.class.getResourceAsStream("/conf/all-flow-corrupt.xml"));
            flowService.load(new StandardDataFlow(updatedBytes, null, null, new HashSet()));
            Assert.fail(("should have thrown " + (FlowSerializationException.class)));
        } catch (FlowSerializationException ufe) {
            StandardFlowSerializer serializer = new StandardFlowSerializer(mockEncryptor);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final Document doc = serializer.transform(flowController, IDENTITY_LOOKUP);
            serializer.serialize(doc, baos);
            String expectedFlow = new String(originalBytes).trim();
            String actualFlow = new String(baos.toByteArray()).trim();
            Assert.assertEquals(expectedFlow, actualFlow);
        }
    }
}

