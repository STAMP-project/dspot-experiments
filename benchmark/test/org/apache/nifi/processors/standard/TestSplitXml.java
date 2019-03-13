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


import CoreAttributes.FILENAME;
import SplitXml.REL_ORIGINAL;
import SplitXml.REL_SPLIT;
import SplitXml.SPLIT_DEPTH;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class TestSplitXml {
    SAXParserFactory factory;

    SAXParser saxParser;

    @Test(expected = AssertionError.class)
    public void testDepthOf0() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitXml());
        runner.setProperty(SPLIT_DEPTH, "0");
        runner.enqueue(Paths.get("src/test/resources/TestXml/xml-bundle-1"));
        runner.run();
    }

    @Test
    public void testDepthOf1() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new SplitXml());
        runner.enqueue(Paths.get("src/test/resources/TestXml/xml-bundle-1"), new HashMap<String, String>() {
            {
                put(FILENAME.key(), "test.xml");
            }
        });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile originalFlowFile = runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        originalFlowFile.assertAttributeExists(FRAGMENT_ID.key());
        originalFlowFile.assertAttributeEquals(FRAGMENT_COUNT.key(), "6");
        runner.assertTransferCount(REL_SPLIT, 6);
        parseFlowFiles(runner.getFlowFilesForRelationship(REL_ORIGINAL));
        parseFlowFiles(runner.getFlowFilesForRelationship(REL_SPLIT));
        Arrays.asList(0, 1, 2, 3, 4, 5).forEach(( index) -> {
            final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SPLIT).get(index);
            flowFile.assertAttributeEquals(FRAGMENT_INDEX.key(), Integer.toString(index));
            flowFile.assertAttributeEquals(FRAGMENT_COUNT.key(), "6");
            flowFile.assertAttributeEquals(SEGMENT_ORIGINAL_FILENAME.key(), "test.xml");
        });
    }

    @Test
    public void testDepthOf2() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new SplitXml());
        runner.setProperty(SPLIT_DEPTH, "2");
        runner.enqueue(Paths.get("src/test/resources/TestXml/xml-bundle-1"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "12");
        runner.assertTransferCount(REL_SPLIT, 12);
        parseFlowFiles(runner.getFlowFilesForRelationship(REL_ORIGINAL));
        parseFlowFiles(runner.getFlowFilesForRelationship(REL_SPLIT));
    }

    @Test
    public void testDepthOf3() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new SplitXml());
        runner.setProperty(SPLIT_DEPTH, "3");
        runner.enqueue(Paths.get("src/test/resources/TestXml/xml-bundle-1"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "12");
        runner.assertTransferCount(REL_SPLIT, 12);
        parseFlowFiles(runner.getFlowFilesForRelationship(REL_ORIGINAL));
        parseFlowFiles(runner.getFlowFilesForRelationship(REL_SPLIT));
    }

    @Test
    public void testNamespaceDeclarations() throws Exception {
        // Configure a namespace aware parser to ensure namespace
        // declarations are handled correctly.
        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        saxParser = factory.newSAXParser();
        final TestRunner runner = TestRunners.newTestRunner(new SplitXml());
        runner.setProperty(SPLIT_DEPTH, "3");
        runner.enqueue(Paths.get("src/test/resources/TestXml/namespace.xml"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "2");
        runner.assertTransferCount(REL_SPLIT, 2);
        parseFlowFiles(runner.getFlowFilesForRelationship(REL_ORIGINAL));
        parseFlowFiles(runner.getFlowFilesForRelationship(REL_SPLIT));
        final MockFlowFile split1 = runner.getFlowFilesForRelationship(REL_SPLIT).get(0);
        split1.assertContentEquals(Paths.get("src/test/resources/TestXml/namespaceSplit1.xml"));
        final MockFlowFile split2 = runner.getFlowFilesForRelationship(REL_SPLIT).get(1);
        split2.assertContentEquals(Paths.get("src/test/resources/TestXml/namespaceSplit2.xml"));
    }
}

