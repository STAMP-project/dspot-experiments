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
package org.apache.nifi.controller.serialization;


import ScheduledStateLookup.IDENTITY_LOOKUP;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.nifi.bundle.Bundle;
import org.apache.nifi.controller.DummyScheduledProcessor;
import org.apache.nifi.controller.FlowController;
import org.apache.nifi.controller.ProcessorNode;
import org.apache.nifi.nar.ExtensionDiscoveringManager;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class StandardFlowSerializerTest {
    private static final String RAW_COMMENTS = "<tagName> \"This\" is an \' example with many characters that need to be filtered and escaped \u0002 in it. \u007f \u0086 " + (Character.MIN_SURROGATE);

    private static final String SERIALIZED_COMMENTS = "&lt;tagName&gt; \"This\" is an \' example with many characters that need to be filtered and escaped  in it. &#127; &#134; ";

    private volatile String propsFile = StandardFlowSerializerTest.class.getResource("/standardflowserializertest.nifi.properties").getFile();

    private FlowController controller;

    private Bundle systemBundle;

    private ExtensionDiscoveringManager extensionManager;

    private StandardFlowSerializer serializer;

    @Test
    public void testSerializationEscapingAndFiltering() throws Exception {
        final ProcessorNode dummy = controller.getFlowManager().createProcessor(DummyScheduledProcessor.class.getName(), UUID.randomUUID().toString(), systemBundle.getBundleDetails().getCoordinate());
        dummy.setComments(StandardFlowSerializerTest.RAW_COMMENTS);
        controller.getFlowManager().getRootGroup().addProcessor(dummy);
        // serialize the controller
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final Document doc = serializer.transform(controller, IDENTITY_LOOKUP);
        serializer.serialize(doc, os);
        // verify the results contain the serialized string
        final String serializedFlow = os.toString(StandardCharsets.UTF_8.name());
        Assert.assertTrue(serializedFlow.contains(StandardFlowSerializerTest.SERIALIZED_COMMENTS));
        Assert.assertFalse(serializedFlow.contains(StandardFlowSerializerTest.RAW_COMMENTS));
    }
}

