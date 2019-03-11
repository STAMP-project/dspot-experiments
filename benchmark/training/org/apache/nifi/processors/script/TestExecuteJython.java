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
package org.apache.nifi.processors.script;


import ExecuteScript.REL_SUCCESS;
import ScriptingComponentUtils.SCRIPT_BODY;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.nifi.util.MockFlowFile;
import org.junit.Test;


/**
 * Unit tests for ExecuteScript with Jython.
 */
public class TestExecuteJython extends BaseScriptTest {
    /**
     * Tests a Jython script that has provides the body of an onTrigger() function.
     *
     * @throws Exception
     * 		Any error encountered while testing
     */
    @Test
    public void testReadFlowFileContentAndStoreInFlowFileAttributeWithScriptBody() throws Exception {
        runner.setValidateExpressionUsage(false);
        runner.setProperty(scriptingComponent.getScriptingComponentHelper().SCRIPT_ENGINE, "python");
        runner.setProperty(SCRIPT_BODY, ("from org.apache.nifi.processors.script import ExecuteScript\n" + (("flowFile = session.get()\n" + "flowFile = session.putAttribute(flowFile, \"from-content\", \"test content\")\n") + "session.transfer(flowFile, ExecuteScript.REL_SUCCESS)")));
        runner.assertValid();
        runner.enqueue("test content".getBytes(StandardCharsets.UTF_8));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship(REL_SUCCESS);
        result.get(0).assertAttributeEquals("from-content", "test content");
    }

    /**
     * Tests a script that does not transfer or remove the original flow file, thereby causing an error during commit.
     *
     * @throws Exception
     * 		Any error encountered while testing. Expecting
     */
    @Test(expected = AssertionError.class)
    public void testScriptNoTransfer() throws Exception {
        runner.setValidateExpressionUsage(false);
        runner.setProperty(scriptingComponent.getScriptingComponentHelper().SCRIPT_ENGINE, "python");
        runner.setProperty(SCRIPT_BODY, "flowFile = session.putAttribute(flowFile, \"from-content\", \"test content\")\n");
        runner.assertValid();
        runner.enqueue("test content".getBytes(StandardCharsets.UTF_8));
        runner.run();
    }
}

