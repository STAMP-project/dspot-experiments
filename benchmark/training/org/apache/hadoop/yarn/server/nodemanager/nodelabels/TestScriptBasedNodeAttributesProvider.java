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
package org.apache.hadoop.yarn.server.nodemanager.nodelabels;


import NodeAttributeType.STRING;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.records.NodeAttribute;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for script based node attributes provider.
 */
public class TestScriptBasedNodeAttributesProvider {
    private static File testRootDir = new File("target", ((TestScriptBasedNodeAttributesProvider.class.getName()) + "-localDir")).getAbsoluteFile();

    private final File nodeAttributeScript = new File(TestScriptBasedNodeAttributesProvider.testRootDir, Shell.appendScriptExtension("attributeScript"));

    private ScriptBasedNodeAttributesProvider nodeAttributesProvider;

    @Test
    public void testNodeAttributeScriptProvider() throws IOException, InterruptedException {
        String simpleScript = "echo NODE_ATTRIBUTE:host,STRING,host1234\n " + ("echo NODE_ATTRIBUTE:os,STRING,redhat_6_3\n " + "echo NODE_ATTRIBUTE:ip,STRING,10.0.0.1");
        writeNodeAttributeScriptFile(simpleScript, true);
        nodeAttributesProvider.init(getConfForNodeAttributeScript());
        nodeAttributesProvider.start();
        try {
            GenericTestUtils.waitFor(() -> (nodeAttributesProvider.getDescriptors().size()) == 3, 500, 3000);
        } catch (TimeoutException e) {
            Assert.fail(("Expecting node attributes size is 3, but got " + (nodeAttributesProvider.getDescriptors().size())));
        }
        Iterator<NodeAttribute> it = nodeAttributesProvider.getDescriptors().iterator();
        while (it.hasNext()) {
            NodeAttribute att = it.next();
            switch (att.getAttributeKey().getAttributeName()) {
                case "host" :
                    Assert.assertEquals(STRING, att.getAttributeType());
                    Assert.assertEquals("host1234", att.getAttributeValue());
                    break;
                case "os" :
                    Assert.assertEquals(STRING, att.getAttributeType());
                    Assert.assertEquals("redhat_6_3", att.getAttributeValue());
                    break;
                case "ip" :
                    Assert.assertEquals(STRING, att.getAttributeType());
                    Assert.assertEquals("10.0.0.1", att.getAttributeValue());
                    break;
                default :
                    Assert.fail(("Unexpected attribute name " + (att.getAttributeKey().getAttributeName())));
                    break;
            }
        } 
    }

    @Test
    public void testInvalidScriptOutput() throws IOException, InterruptedException {
        // Script output doesn't have correct prefix.
        String scriptContent = "echo host,STRING,host1234";
        writeNodeAttributeScriptFile(scriptContent, true);
        nodeAttributesProvider.init(getConfForNodeAttributeScript());
        nodeAttributesProvider.start();
        try {
            GenericTestUtils.waitFor(() -> (nodeAttributesProvider.getDescriptors().size()) == 1, 500, 3000);
            Assert.fail(("This test should timeout because the provide is unable" + " to parse any attributes from the script output."));
        } catch (TimeoutException e) {
            Assert.assertEquals(0, nodeAttributesProvider.getDescriptors().size());
        }
    }

    @Test
    public void testMalformedScriptOutput() throws Exception {
        // Script output has correct prefix but each line is malformed.
        String scriptContent = "echo NODE_ATTRIBUTE:host,STRING,host1234,a_extra_column";
        writeNodeAttributeScriptFile(scriptContent, true);
        nodeAttributesProvider.init(getConfForNodeAttributeScript());
        nodeAttributesProvider.start();
        // There should be no attributes found, and we should
        // see Malformed output warnings in the log
        try {
            GenericTestUtils.waitFor(() -> (nodeAttributesProvider.getDescriptors().size()) == 1, 500, 3000);
            Assert.fail(("This test should timeout because the provide is unable" + " to parse any attributes from the script output."));
        } catch (TimeoutException e) {
            Assert.assertEquals(0, nodeAttributesProvider.getDescriptors().size());
        }
    }

    @Test
    public void testFetchInterval() throws Exception {
        // The script returns the pid (as an attribute) each time runs this script
        String simpleScript = "echo NODE_ATTRIBUTE:pid,STRING,$$";
        writeNodeAttributeScriptFile(simpleScript, true);
        nodeAttributesProvider.init(getConfForNodeAttributeScript());
        nodeAttributesProvider.start();
        // Wait for at most 3 seconds until we get at least 1
        // different attribute value.
        Set<String> resultSet = new HashSet<>();
        GenericTestUtils.waitFor(() -> {
            Set<NodeAttribute> attributes = nodeAttributesProvider.getDescriptors();
            if (attributes != null) {
                Assert.assertEquals(1, attributes.size());
                resultSet.add(attributes.iterator().next().getAttributeValue());
                return (resultSet.size()) > 1;
            } else {
                return false;
            }
        }, 500, 3000);
    }

    @Test
    public void testNodeAttributesValidation() throws Exception {
        // Script output contains ambiguous node attributes
        String scriptContent = "echo NODE_ATTRIBUTE:host,STRING,host1234\n " + ("echo NODE_ATTRIBUTE:host,STRING,host2345\n " + "echo NODE_ATTRIBUTE:ip,STRING,10.0.0.1");
        writeNodeAttributeScriptFile(scriptContent, true);
        nodeAttributesProvider.init(getConfForNodeAttributeScript());
        nodeAttributesProvider.start();
        // There should be no attributes found, and we should
        // see Malformed output warnings in the log
        try {
            GenericTestUtils.waitFor(() -> (nodeAttributesProvider.getDescriptors().size()) == 3, 500, 3000);
            Assert.fail(("This test should timeout because the provide is unable" + " to parse any attributes from the script output."));
        } catch (TimeoutException e) {
            Assert.assertEquals(0, nodeAttributesProvider.getDescriptors().size());
        }
    }
}

