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
package org.apache.nifi.processors.standard;


import ValidateXml.ERROR_ATTRIBUTE_KEY;
import ValidateXml.REL_INVALID;
import ValidateXml.REL_VALID;
import ValidateXml.SCHEMA_FILE;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;
import org.xml.sax.SAXException;


public class TestValidateXml {
    @Test
    public void testValid() throws IOException, SAXException {
        final TestRunner runner = TestRunners.newTestRunner(new ValidateXml());
        runner.setProperty(SCHEMA_FILE, "src/test/resources/TestXml/XmlBundle.xsd");
        runner.enqueue(Paths.get("src/test/resources/TestXml/xml-snippet.xml"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_VALID, 1);
    }

    @Test
    public void testInvalid() throws IOException, SAXException {
        final TestRunner runner = TestRunners.newTestRunner(new ValidateXml());
        runner.setProperty(SCHEMA_FILE, "src/test/resources/TestXml/XmlBundle.xsd");
        runner.enqueue("<this>is an invalid</xml>");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INVALID, 1);
        runner.assertAllFlowFilesContainAttribute(REL_INVALID, ERROR_ATTRIBUTE_KEY);
    }

    @Test
    public void testValidEL() throws IOException, SAXException {
        final TestRunner runner = TestRunners.newTestRunner(new ValidateXml());
        runner.setProperty(SCHEMA_FILE, "${my.schema}");
        runner.setVariable("my.schema", "src/test/resources/TestXml/XmlBundle.xsd");
        runner.enqueue(Paths.get("src/test/resources/TestXml/xml-snippet.xml"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_VALID, 1);
    }

    @Test(expected = AssertionError.class)
    public void testInvalidEL() throws IOException, SAXException {
        final TestRunner runner = TestRunners.newTestRunner(new ValidateXml());
        runner.setProperty(SCHEMA_FILE, "${my.schema}");
        runner.enqueue("<this>is an invalid</xml>");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INVALID, 1);
        runner.assertAllFlowFilesContainAttribute(REL_INVALID, ERROR_ATTRIBUTE_KEY);
    }
}

