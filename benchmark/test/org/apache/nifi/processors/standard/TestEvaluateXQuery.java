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


import EvaluateXQuery.DESTINATION;
import EvaluateXQuery.DESTINATION_ATTRIBUTE;
import EvaluateXQuery.DESTINATION_CONTENT;
import EvaluateXQuery.REL_FAILURE;
import EvaluateXQuery.REL_MATCH;
import EvaluateXQuery.REL_NO_MATCH;
import EvaluateXQuery.VALIDATE_DTD;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;

import static EvaluateXQuery.OUTPUT_METHOD_HTML;
import static EvaluateXQuery.OUTPUT_METHOD_TEXT;
import static EvaluateXQuery.OUTPUT_METHOD_XML;


public class TestEvaluateXQuery {
    private static final Path XML_SNIPPET = Paths.get("src/test/resources/TestXml/fruit.xml");

    private static final Path XML_SNIPPET_EMBEDDED_DOCTYPE = Paths.get("src/test/resources/TestXml/xml-snippet-embedded-doctype.xml");

    private static final Path XML_SNIPPET_NONEXISTENT_DOCTYPE = Paths.get("src/test/resources/TestXml/xml-snippet-external-doctype.xml");

    private static final String[] fruitNames = new String[]{ "apple", "apple", "banana", "orange", "blueberry", "raspberry", "none" };

    private static final String[] methods = new String[]{ OUTPUT_METHOD_XML, OUTPUT_METHOD_HTML, OUTPUT_METHOD_TEXT };

    private static final boolean[] booleans = new boolean[]{ true, false };

    @Test
    public void testSetTransformerProperties() throws Exception {
        for (int i = 0; i < (TestEvaluateXQuery.methods.length); i++) {
            for (int j = 0; j < (TestEvaluateXQuery.booleans.length); j++) {
                for (int k = 0; k < (TestEvaluateXQuery.booleans.length); k++) {
                    Properties props = EvaluateXQuery.getTransformerProperties(TestEvaluateXQuery.methods[i], TestEvaluateXQuery.booleans[j], TestEvaluateXQuery.booleans[k]);
                    Assert.assertEquals(3, props.size());
                    Assert.assertEquals(TestEvaluateXQuery.methods[i], props.getProperty(OutputKeys.METHOD));
                    Assert.assertEquals((TestEvaluateXQuery.booleans[j] ? "yes" : "no"), props.getProperty(OutputKeys.INDENT));
                    Assert.assertEquals((TestEvaluateXQuery.booleans[k] ? "yes" : "no"), props.getProperty(OutputKeys.OMIT_XML_DECLARATION));
                }
            }
        }
    }

    @Test
    public void testFormatting() throws Exception {
        List<String> formattedResults;
        final String atomicQuery = "count(//fruit)";
        final String singleElementNodeQuery = "//fruit[1]";
        final String singleTextNodeQuery = "//fruit[1]/name/text()";
        for (int i = 0; i < (TestEvaluateXQuery.methods.length); i++) {
            for (int j = 0; j < (TestEvaluateXQuery.booleans.length); j++) {
                for (int k = 0; k < (TestEvaluateXQuery.booleans.length); k++) {
                    formattedResults = getFormattedResult(TestEvaluateXQuery.XML_SNIPPET, atomicQuery, TestEvaluateXQuery.methods[i], TestEvaluateXQuery.booleans[j], TestEvaluateXQuery.booleans[k]);
                    Assert.assertEquals(1, formattedResults.size());
                    Assert.assertEquals("7", formattedResults.get(0));
                }
            }
        }
        for (int i = 0; i < (TestEvaluateXQuery.methods.length); i++) {
            for (int j = 0; j < (TestEvaluateXQuery.booleans.length); j++) {
                for (int k = 0; k < (TestEvaluateXQuery.booleans.length); k++) {
                    formattedResults = getFormattedResult(TestEvaluateXQuery.XML_SNIPPET, singleTextNodeQuery, TestEvaluateXQuery.methods[i], TestEvaluateXQuery.booleans[j], TestEvaluateXQuery.booleans[k]);
                    Assert.assertEquals(1, formattedResults.size());
                    Assert.assertEquals("apple", formattedResults.get(0));
                }
            }
        }
        {
            formattedResults = getFormattedResult(TestEvaluateXQuery.XML_SNIPPET, singleElementNodeQuery, "xml", false, false);
            Assert.assertEquals(1, formattedResults.size());
            String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\" taste=\"crisp\">\n" + ((("<!-- Apples are my favorite -->\n" + "    <name>apple</name>\n") + "    <color>red</color>\n") + "  </fruit>");
            Assert.assertEquals(spaceTrimmed(expectedXml), spaceTrimmed(formattedResults.get(0)));
        }
        {
            formattedResults = getFormattedResult(TestEvaluateXQuery.XML_SNIPPET, singleElementNodeQuery, "html", false, false);
            Assert.assertEquals(1, formattedResults.size());
            String expectedXml = "<fruit xmlns:ns=\"http://namespace/1\" taste=\"crisp\">\n" + ((("    <!-- Apples are my favorite -->\n" + "    <name>apple</name>\n") + "    <color>red</color>\n") + "  </fruit>");
            Assert.assertEquals(spaceTrimmed(expectedXml), spaceTrimmed(formattedResults.get(0)));
        }
        {
            formattedResults = getFormattedResult(TestEvaluateXQuery.XML_SNIPPET, singleElementNodeQuery, "text", false, false);
            Assert.assertEquals(1, formattedResults.size());
            String expectedXml = "\n    \n" + (("    apple\n" + "    red\n") + "  ");
            Assert.assertEquals(spaceTrimmed(expectedXml), spaceTrimmed(formattedResults.get(0)));
        }
        {
            formattedResults = getFormattedResult(TestEvaluateXQuery.XML_SNIPPET, singleElementNodeQuery, "xml", true, false);
            Assert.assertEquals(1, formattedResults.size());
            String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((("<fruit xmlns:ns=\"http://namespace/1\" taste=\"crisp\">\n" + "    <!-- Apples are my favorite -->\n") + "    <name>apple</name>\n") + "    <color>red</color>\n") + "  </fruit>\n");
            Assert.assertEquals(spaceTrimmed(expectedXml), spaceTrimmed(formattedResults.get(0)));
        }
        {
            formattedResults = getFormattedResult(TestEvaluateXQuery.XML_SNIPPET, singleElementNodeQuery, "xml", true, true);
            Assert.assertEquals(1, formattedResults.size());
            String expectedXml = "<fruit xmlns:ns=\"http://namespace/1\" taste=\"crisp\">\n" + ((("    <!-- Apples are my favorite -->\n" + "    <name>apple</name>\n") + "    <color>red</color>\n") + "  </fruit>\n");
            Assert.assertEquals(spaceTrimmed(expectedXml), spaceTrimmed(formattedResults.get(0)));
        }
    }

    @Test(expected = AssertionError.class)
    public void testBadXQuery() throws Exception {
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "counttttttt(*:fruitbasket/fruit)", Arrays.asList("7"));
    }

    @Test
    public void testXQueries() throws Exception {
        /* count matches */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "count(*:fruitbasket/fruit)", Arrays.asList("7"));
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "count(//fruit)", Arrays.asList("7"));
        /* Using a namespace */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "declare namespace fb = \"http://namespace/1\"; count(fb:fruitbasket/fruit)", Arrays.asList("7"));
        /* determine if node exists */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "boolean(//fruit[1])", Arrays.asList("true"));
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "boolean(//fruit[100])", Arrays.asList("false"));
        /* XML first match */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "//fruit[1]", Arrays.asList("<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\" taste=\"crisp\"><!-- Apples are my favorite --><name>apple</name><color>red</color></fruit>"));
        /* XML last match */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "//fruit[count(//fruit)]", Arrays.asList("<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\"><name>none</name><color/></fruit>"));
        /* XML first match wrapped */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "<wrap>{//fruit[1]}</wrap>", Arrays.asList("<?xml version=\"1.0\" encoding=\"UTF-8\"?><wrap><fruit xmlns:ns=\"http://namespace/1\" taste=\"crisp\"><!-- Apples are my favorite --><name>apple</name><color>red</color></fruit></wrap>"));
        /* XML all matches (multiple results) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "//fruit", Arrays.asList("<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\" taste=\"crisp\"><!-- Apples are my favorite --><name>apple</name><color>red</color></fruit>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\"><name>apple</name><color>green</color></fruit>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\"><name>banana</name><color>yellow</color></fruit>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\" taste=\"sweet\"><name>orange</name><color>orange</color></fruit>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\"><name>blueberry</name><color>blue</color></fruit>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\" taste=\"tart\"><name>raspberry</name><color>red</color></fruit>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruit xmlns:ns=\"http://namespace/1\"><name>none</name><color/></fruit>"));
        /* XML all matches wrapped (one result) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "<wrap>{//fruit}</wrap>", Arrays.asList(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((((((("<wrap>" + "<fruit xmlns:ns=\"http://namespace/1\" taste=\"crisp\"><!-- Apples are my favorite --><name>apple</name><color>red</color></fruit>") + "<fruit xmlns:ns=\"http://namespace/1\"><name>apple</name><color>green</color></fruit>") + "<fruit xmlns:ns=\"http://namespace/1\"><name>banana</name><color>yellow</color></fruit>") + "<fruit xmlns:ns=\"http://namespace/1\" taste=\"sweet\"><name>orange</name><color>orange</color></fruit>") + "<fruit xmlns:ns=\"http://namespace/1\"><name>blueberry</name><color>blue</color></fruit>") + "<fruit xmlns:ns=\"http://namespace/1\" taste=\"tart\"><name>raspberry</name><color>red</color></fruit>") + "<fruit xmlns:ns=\"http://namespace/1\"><name>none</name><color/></fruit>") + "</wrap>"))));
        /* String all matches fruit names */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "for $x in //fruit return $x/name/text()", Arrays.asList(TestEvaluateXQuery.fruitNames));
        /* String first match fruit name (XPath) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "//fruit[1]/name/text()", Arrays.asList("apple"));
        /* String first match fruit color (XPath) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "//fruit[1]/color/text()", Arrays.asList("red"));
        /* String first match fruit name (XQuery) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "for $x in //fruit[1] return string-join(($x/name/text() , $x/color/text()), ' - ')", Arrays.asList("apple - red"));
        /* String first match fruit & color (one result) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "for $x in //fruit[1] return string-join(($x/name/text() , $x/color/text()), ' - ')", Arrays.asList("apple - red"));
        /* String all matches fruit & color (multiple results) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "for $x in //fruit return string-join(($x/name/text() , $x/color/text()), ' - ')", Arrays.asList("apple - red", "apple - green", "banana - yellow", "orange - orange", "blueberry - blue", "raspberry - red", "none"));
        /* String all matches fruit & color (single, newline delimited result) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "string-join((for $y in (for $x in //fruit return string-join(($x/name/text() , $x/color/text()), \' - \')) return $y), \'\n\')", Arrays.asList(("apple - red\n" + ((((("apple - green\n" + "banana - yellow\n") + "orange - orange\n") + "blueberry - blue\n") + "raspberry - red\n") + "none"))));
        /* String all matches fruit & color using "let" (single, newline delimited result) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "string-join((for $y in (for $x in //fruit let $d := string-join(($x/name/text() , $x/color/text()), \' - \')  return $d) return $y), \'\n\')", Arrays.asList(("apple - red\n" + ((((("apple - green\n" + "banana - yellow\n") + "orange - orange\n") + "blueberry - blue\n") + "raspberry - red\n") + "none"))));
        /* String all matches name only, comma delimited (one result) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "string-join((for $x in //fruit return $x/name/text()), ', ')", Arrays.asList("apple, apple, banana, orange, blueberry, raspberry, none"));
        /* String all matches color and name, comma delimited (one result) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "string-join((for $y in (for $x in //fruit return string-join(($x/color/text() , $x/name/text()), ' ')) return $y), ', ')", Arrays.asList("red apple, green apple, yellow banana, orange orange, blue blueberry, red raspberry, none"));
        /* String all matches color and name, comma delimited using let(one result) */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "string-join((for $y in (for $x in //fruit let $d := string-join(($x/color/text() , $x/name/text()), ' ')  return $d) return $y), ', ')", Arrays.asList("red apple, green apple, yellow banana, orange orange, blue blueberry, red raspberry, none"));
        /* Query for attribute */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "string(//fruit[1]/@taste)", Arrays.asList("crisp"));
        /* Query for comment */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "//fruit/comment()", Arrays.asList(" Apples are my favorite "));
        /* Query for processing instruction */
        doXqueryTest(TestEvaluateXQuery.XML_SNIPPET, "//processing-instruction()[name()='xml-stylesheet']", Arrays.asList("type=\"text/xsl\" href=\"foo.xsl\""));
    }

    @Test
    public void testRootPath() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("xquery.result1", "/");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final String attributeString = out.getAttribute("xquery.result1").replaceAll(">\\s+<", "><");
        final String xmlSnippetString = new String(Files.readAllBytes(TestEvaluateXQuery.XML_SNIPPET), "UTF-8").replaceAll(">\\s+<", "><");
        Assert.assertEquals(xmlSnippetString, attributeString);
    }

    @Test
    public void testCheckIfElementExists() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("xquery.result.exist.1", "boolean(/*:fruitbasket/fruit[1])");
        testRunner.setProperty("xquery.result.exist.2", "boolean(/*:fruitbasket/fruit[100])");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out.assertAttributeEquals("xquery.result.exist.1", "true");
        out.assertAttributeEquals("xquery.result.exist.2", "false");
    }

    @Test
    public void testUnmatchedContent() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("xquery.result.exist.2", "/*:fruitbasket/node2");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_NO_MATCH, 1);
        testRunner.getFlowFilesForRelationship(REL_NO_MATCH).get(0).assertContentEquals(TestEvaluateXQuery.XML_SNIPPET);
    }

    @Test
    public void testUnmatchedAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("xquery.result.exist.2", "/*:fruitbasket/node2");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_NO_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_NO_MATCH).get(0);
        out.assertAttributeEquals("xquery.result.exist.2", null);
        testRunner.getFlowFilesForRelationship(REL_NO_MATCH).get(0).assertContentEquals(TestEvaluateXQuery.XML_SNIPPET);
    }

    @Test
    public void testNoXQueryAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_NO_MATCH, 1);
        testRunner.getFlowFilesForRelationship(REL_NO_MATCH).get(0).assertContentEquals(TestEvaluateXQuery.XML_SNIPPET);
    }

    @Test(expected = AssertionError.class)
    public void testNoXQueryContent() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
    }

    @Test
    public void testOneMatchOneUnmatchAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("some.property", "//fruit/name/text()");
        testRunner.setProperty("xquery.result.exist.2", "/*:fruitbasket/node2");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        for (int i = 0; i < (TestEvaluateXQuery.fruitNames.length); i++) {
            final String outXml = out.getAttribute(("some.property." + (((int) (i)) + 1)));
            Assert.assertEquals(TestEvaluateXQuery.fruitNames[i], outXml.trim());
        }
        out.assertAttributeEquals("xquery.result.exist.2", null);
        testRunner.getFlowFilesForRelationship(REL_MATCH).get(0).assertContentEquals(TestEvaluateXQuery.XML_SNIPPET);
    }

    @Test
    public void testMatchedEmptyStringAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("xquery.result.exist.2", "/*:fruitbasket/*[name='none']/color/text()");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_NO_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_NO_MATCH).get(0);
        out.assertAttributeEquals("xquery.result.exist.2", null);
        testRunner.getFlowFilesForRelationship(REL_NO_MATCH).get(0).assertContentEquals(TestEvaluateXQuery.XML_SNIPPET);
    }

    @Test(expected = AssertionError.class)
    public void testMultipleXPathForContent() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("some.property.1", "/*:fruitbasket/fruit[1]");
        testRunner.setProperty("some.property.2", "/*:fruitbasket/fruit[2]");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
    }

    @Test
    public void testWriteStringToAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("xquery.result2", "/*:fruitbasket/fruit[1]/name/text()");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out.assertAttributeEquals("xquery.result2", "apple");
        testRunner.getFlowFilesForRelationship(REL_MATCH).get(0).assertContentEquals(TestEvaluateXQuery.XML_SNIPPET);
    }

    @Test
    public void testWriteStringToContent() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("some.property", "/*:fruitbasket/fruit[1]/name/text()");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.trim().equals("apple"));
    }

    @Test
    public void testWriteXmlToAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("some.property", "/*:fruitbasket/fruit[1]/name");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final String outXml = out.getAttribute("some.property");
        Assert.assertTrue(outXml.contains("<name xmlns:ns=\"http://namespace/1\">apple</name>"));
        testRunner.getFlowFilesForRelationship(REL_MATCH).get(0).assertContentEquals(TestEvaluateXQuery.XML_SNIPPET);
    }

    @Test
    public void testWriteXmlToContent() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("some.property", "/*:fruitbasket/fruit[1]/name");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.contains("<name xmlns:ns=\"http://namespace/1\">apple</name>"));
    }

    @Test
    public void testMatchesMultipleStringContent() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("some.property", "//fruit/name/text()");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 7);
        final List<MockFlowFile> flowFilesForRelMatch = testRunner.getFlowFilesForRelationship(REL_MATCH);
        for (int i = 0; i < (flowFilesForRelMatch.size()); i++) {
            final MockFlowFile out = flowFilesForRelMatch.get(i);
            final byte[] outData = testRunner.getContentAsByteArray(out);
            final String outXml = new String(outData, "UTF-8");
            Assert.assertEquals(TestEvaluateXQuery.fruitNames[i], outXml.trim());
        }
    }

    @Test
    public void testMatchesMultipleStringAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("some.property", "//fruit/name/text()");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        for (int i = 0; i < (TestEvaluateXQuery.fruitNames.length); i++) {
            final String outXml = out.getAttribute(("some.property." + (((int) (i)) + 1)));
            Assert.assertEquals(TestEvaluateXQuery.fruitNames[i], outXml.trim());
        }
        testRunner.getFlowFilesForRelationship(REL_MATCH).get(0).assertContentEquals(TestEvaluateXQuery.XML_SNIPPET);
    }

    @Test
    public void testMatchesMultipleXmlContent() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("some.property", "//fruit/name");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 7);
        final List<MockFlowFile> flowFilesForRelMatch = testRunner.getFlowFilesForRelationship(REL_MATCH);
        for (int i = 0; i < (flowFilesForRelMatch.size()); i++) {
            final MockFlowFile out = flowFilesForRelMatch.get(i);
            final byte[] outData = testRunner.getContentAsByteArray(out);
            final String outXml = new String(outData, "UTF-8");
            String expectedXml = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><name xmlns:ns=\"http://namespace/1\">" + (TestEvaluateXQuery.fruitNames[i])) + "</name>";
            Assert.assertEquals(expectedXml, outXml.trim());
        }
    }

    @Test
    public void testMatchesMultipleXmlAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("some.property", "//fruit/name");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        for (int i = 0; i < (TestEvaluateXQuery.fruitNames.length); i++) {
            final String outXml = out.getAttribute(("some.property." + (((int) (i)) + 1)));
            String expectedXml = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><name xmlns:ns=\"http://namespace/1\">" + (TestEvaluateXQuery.fruitNames[i])) + "</name>";
            Assert.assertEquals(expectedXml, outXml.trim());
        }
        testRunner.getFlowFilesForRelationship(REL_MATCH).get(0).assertContentEquals(TestEvaluateXQuery.XML_SNIPPET);
    }

    @Test
    public void testSuccessForEmbeddedDocTypeValidation() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(VALIDATE_DTD, "true");
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]/value/text()");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET_EMBEDDED_DOCTYPE);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.trim().equals("Hello"));
    }

    @Test
    public void testSuccessForEmbeddedDocTypeValidationDisabled() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(VALIDATE_DTD, "false");
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]/value/text()");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET_EMBEDDED_DOCTYPE);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.trim().equals("Hello"));
    }

    @Test
    public void testFailureForExternalDocTypeWithDocTypeValidationEnabled() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]/value/text()");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET_NONEXISTENT_DOCTYPE);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testSuccessForExternalDocTypeWithDocTypeValidationDisabled() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXQuery());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(VALIDATE_DTD, "false");
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]/value/text()");
        testRunner.enqueue(TestEvaluateXQuery.XML_SNIPPET_NONEXISTENT_DOCTYPE);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.trim().equals("Hello"));
    }
}

