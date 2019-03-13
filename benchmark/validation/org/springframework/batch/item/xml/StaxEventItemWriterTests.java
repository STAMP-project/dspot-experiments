/**
 * Copyright 2008-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.xml;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.WriterNotOpenException;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;


/**
 * Tests for {@link StaxEventItemWriter}.
 */
public class StaxEventItemWriterTests {
    // object under test
    private StaxEventItemWriter<Object> writer;

    // output file
    private Resource resource;

    private ExecutionContext executionContext;

    // test item for writing to output
    private Object item = new Object() {
        @Override
        public String toString() {
            return (ClassUtils.getShortName(StaxEventItemWriter.class)) + "-testString";
        }
    };

    private StaxEventItemWriterTests.JAXBItem jaxbItem = new StaxEventItemWriterTests.JAXBItem();

    // test item for writing to output with multi byte character
    private Object itemMultiByte = new Object() {
        @Override
        public String toString() {
            return (ClassUtils.getShortName(StaxEventItemWriter.class)) + "-t?stStr?ng";
        }
    };

    private List<? extends Object> items = Collections.singletonList(item);

    private List<? extends Object> itemsMultiByte = Collections.singletonList(itemMultiByte);

    private List<? extends Object> jaxbItems = Collections.singletonList(jaxbItem);

    private static final String TEST_STRING = ("<" + (ClassUtils.getShortName(StaxEventItemWriter.class))) + "-testString/>";

    private static final String TEST_STRING_MULTI_BYTE = ("<" + (ClassUtils.getShortName(StaxEventItemWriter.class))) + "-t?stStr?ng/>";

    private static final String NS_TEST_STRING = ("<ns:" + (ClassUtils.getShortName(StaxEventItemWriter.class))) + "-testString/>";

    private static final String FOO_TEST_STRING = ("<foo:" + (ClassUtils.getShortName(StaxEventItemWriter.class))) + "-testString/>";

    private StaxEventItemWriterTests.SimpleMarshaller marshaller;

    private Jaxb2Marshaller jaxbMarshaller;

    /**
     * Test setting writer name.
     */
    @Test
    public void testSetName() throws Exception {
        writer.setName("test");
        writer.open(executionContext);
        writer.write(items);
        writer.update(executionContext);
        writer.close();
        Assert.assertTrue("execution context keys should be prefixed with writer name", executionContext.containsKey("test.position"));
    }

    @Test(expected = WriterNotOpenException.class)
    public void testAssertWriterIsInitialized() throws Exception {
        StaxEventItemWriter<String> writer = new StaxEventItemWriter();
        writer.write(Collections.singletonList("foo"));
    }

    /**
     * Item is written to the output file only after flush.
     */
    @Test
    public void testWriteAndFlush() throws Exception {
        writer.open(executionContext);
        writer.write(items);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.TEST_STRING));
    }

    @Test
    public void testWriteAndForceFlush() throws Exception {
        writer.setForceSync(true);
        writer.open(executionContext);
        writer.write(items);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.TEST_STRING));
    }

    /**
     * Restart scenario - content is appended to the output file after restart.
     */
    @Test
    public void testRestart() throws Exception {
        writer.open(executionContext);
        // write item
        writer.write(items);
        writer.update(executionContext);
        writer.close();
        // create new writer from saved restart data and continue writing
        writer = createItemWriter();
        writer.open(executionContext);
        writer.write(items);
        writer.write(items);
        writer.close();
        // check the output is concatenation of 'before restart' and 'after
        // restart' writes.
        String outputFile = getOutputFileContent();
        Assert.assertEquals(3, StringUtils.countOccurrencesOf(outputFile, StaxEventItemWriterTests.TEST_STRING));
        Assert.assertEquals((((("<root>" + (StaxEventItemWriterTests.TEST_STRING)) + (StaxEventItemWriterTests.TEST_STRING)) + (StaxEventItemWriterTests.TEST_STRING)) + "</root>"), outputFile.replace(" ", ""));
    }

    @Test
    public void testTransactionalRestart() throws Exception {
        writer.open(executionContext);
        PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
        new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    // write item
                    writer.write(items);
                } catch (Exception e) {
                    throw new UnexpectedInputException("Could not write data", e);
                }
                // get restart data
                writer.update(executionContext);
                return null;
            }
        });
        writer.close();
        // create new writer from saved restart data and continue writing
        writer = createItemWriter();
        writer.open(executionContext);
        new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    writer.write(items);
                } catch (Exception e) {
                    throw new UnexpectedInputException("Could not write data", e);
                }
                // get restart data
                writer.update(executionContext);
                return null;
            }
        });
        writer.close();
        // check the output is concatenation of 'before restart' and 'after
        // restart' writes.
        String outputFile = getOutputFileContent();
        Assert.assertEquals(2, StringUtils.countOccurrencesOf(outputFile, StaxEventItemWriterTests.TEST_STRING));
        Assert.assertTrue(outputFile.contains(((("<root>" + (StaxEventItemWriterTests.TEST_STRING)) + (StaxEventItemWriterTests.TEST_STRING)) + "</root>")));
    }

    // BATCH-1959
    @Test
    public void testTransactionalRestartWithMultiByteCharacterUTF8() throws Exception {
        testTransactionalRestartWithMultiByteCharacter("UTF-8");
    }

    // BATCH-1959
    @Test
    public void testTransactionalRestartWithMultiByteCharacterUTF16BE() throws Exception {
        testTransactionalRestartWithMultiByteCharacter("UTF-16BE");
    }

    @Test
    public void testTransactionalRestartFailOnFirstWrite() throws Exception {
        PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
        writer.open(executionContext);
        try {
            new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    try {
                        writer.write(items);
                    } catch (Exception e) {
                        throw new IllegalStateException("Could not write data", e);
                    }
                    throw new UnexpectedInputException("Could not write data");
                }
            });
        } catch (UnexpectedInputException e) {
            // expected
        }
        writer.close();
        String outputFile = getOutputFileContent();
        Assert.assertEquals("<root></root>", outputFile);
        // create new writer from saved restart data and continue writing
        writer = createItemWriter();
        execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                writer.open(executionContext);
                try {
                    writer.write(items);
                } catch (Exception e) {
                    throw new UnexpectedInputException("Could not write data", e);
                }
                // get restart data
                writer.update(executionContext);
                return null;
            }
        });
        writer.close();
        // check the output is concatenation of 'before restart' and 'after
        // restart' writes.
        outputFile = getOutputFileContent();
        Assert.assertEquals(1, StringUtils.countOccurrencesOf(outputFile, StaxEventItemWriterTests.TEST_STRING));
        Assert.assertTrue(outputFile.contains((("<root>" + (StaxEventItemWriterTests.TEST_STRING)) + "</root>")));
        Assert.assertEquals("<root><StaxEventItemWriter-testString/></root>", outputFile);
    }

    /**
     * Item is written to the output file only after flush.
     */
    @Test
    public void testWriteWithHeader() throws Exception {
        writer.setHeaderCallback(new StaxWriterCallback() {
            @Override
            public void write(XMLEventWriter writer) throws IOException {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                try {
                    writer.add(factory.createStartElement("", "", "header"));
                    writer.add(factory.createEndElement("", "", "header"));
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        writer.open(executionContext);
        writer.write(items);
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains("<header/>"));
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.TEST_STRING));
    }

    /**
     * Count of 'records written so far' is returned as statistics.
     */
    @Test
    public void testStreamContext() throws Exception {
        writer.open(executionContext);
        final int NUMBER_OF_RECORDS = 10;
        Assert.assertFalse(executionContext.containsKey(((ClassUtils.getShortName(StaxEventItemWriter.class)) + ".record.count")));
        for (int i = 1; i <= NUMBER_OF_RECORDS; i++) {
            writer.write(items);
            writer.update(executionContext);
            long writeStatistics = executionContext.getLong(((ClassUtils.getShortName(StaxEventItemWriter.class)) + ".record.count"));
            Assert.assertEquals(i, writeStatistics);
        }
    }

    /**
     * Open method writes the root tag, close method adds corresponding end tag.
     */
    @Test
    public void testOpenAndClose() throws Exception {
        writer.setHeaderCallback(new StaxWriterCallback() {
            @Override
            public void write(XMLEventWriter writer) throws IOException {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                try {
                    writer.add(factory.createStartElement("", "", "header"));
                    writer.add(factory.createEndElement("", "", "header"));
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        writer.setFooterCallback(new StaxWriterCallback() {
            @Override
            public void write(XMLEventWriter writer) throws IOException {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                try {
                    writer.add(factory.createStartElement("", "", "footer"));
                    writer.add(factory.createEndElement("", "", "footer"));
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        writer.setRootTagName("testroot");
        writer.setRootElementAttributes(Collections.<String, String>singletonMap("attribute", "value"));
        writer.open(executionContext);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(content.contains("<testroot attribute=\"value\">"));
        Assert.assertTrue(content.contains("<header/>"));
        Assert.assertTrue(content.contains("<footer/>"));
        Assert.assertTrue(content.endsWith("</testroot>"));
    }

    @Test
    public void testNonExistantResource() throws Exception {
        Resource doesntExist = Mockito.mock(Resource.class);
        Mockito.when(doesntExist.getFile()).thenReturn(File.createTempFile("arbitrary", null));
        Mockito.when(doesntExist.exists()).thenReturn(false);
        writer.setResource(doesntExist);
        try {
            writer.open(executionContext);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertEquals("Output resource must exist", e.getMessage());
        }
    }

    /**
     * Resource is not deleted when items have been written and shouldDeleteIfEmpty flag is set.
     */
    @Test
    public void testDeleteIfEmptyRecordsWritten() throws Exception {
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        writer.write(items);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.TEST_STRING));
    }

    /**
     * Resource is deleted when no items have been written and shouldDeleteIfEmpty flag is set.
     */
    @Test
    public void testDeleteIfEmptyNoRecordsWritten() throws Exception {
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        writer.close();
        Assert.assertFalse(("file should be deleted" + (resource)), resource.getFile().exists());
    }

    /**
     * Resource is deleted when items have not been written and shouldDeleteIfEmpty flag is set.
     */
    @Test
    public void testDeleteIfEmptyNoRecordsWrittenHeaderAndFooter() throws Exception {
        writer.setShouldDeleteIfEmpty(true);
        writer.setHeaderCallback(new StaxWriterCallback() {
            @Override
            public void write(XMLEventWriter writer) throws IOException {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                try {
                    writer.add(factory.createStartElement("", "", "header"));
                    writer.add(factory.createEndElement("", "", "header"));
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        writer.setFooterCallback(new StaxWriterCallback() {
            @Override
            public void write(XMLEventWriter writer) throws IOException {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                try {
                    writer.add(factory.createStartElement("", "", "footer"));
                    writer.add(factory.createEndElement("", "", "footer"));
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        writer.open(executionContext);
        writer.close();
        Assert.assertFalse(("file should be deleted" + (resource)), resource.getFile().exists());
    }

    /**
     * Resource is not deleted when items have been written and shouldDeleteIfEmpty flag is set.
     */
    @Test
    public void testDeleteIfEmptyRecordsWrittenRestart() throws Exception {
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        writer.write(items);
        writer.update(executionContext);
        writer.close();
        writer = createItemWriter();
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.TEST_STRING));
    }

    /**
     * Test that the writer can restart if the previous execution deleted empty file.
     */
    @Test
    public void testDeleteIfEmptyRestartAfterDelete() throws Exception {
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        writer.update(executionContext);
        writer.close();
        Assert.assertFalse(resource.getFile().exists());
        writer = createItemWriter();
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        writer.write(items);
        writer.update(executionContext);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.TEST_STRING));
    }

    /**
     * Resource is not deleted when items have been written and shouldDeleteIfEmpty flag is set (restart after delete).
     */
    @Test
    public void testDeleteIfEmptyNoRecordsWrittenHeaderAndFooterRestartAfterDelete() throws Exception {
        writer.setShouldDeleteIfEmpty(true);
        writer.setHeaderCallback(new StaxWriterCallback() {
            @Override
            public void write(XMLEventWriter writer) throws IOException {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                try {
                    writer.add(factory.createStartElement("", "", "header"));
                    writer.add(factory.createEndElement("", "", "header"));
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        writer.setFooterCallback(new StaxWriterCallback() {
            @Override
            public void write(XMLEventWriter writer) throws IOException {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                try {
                    writer.add(factory.createStartElement("", "", "footer"));
                    writer.add(factory.createEndElement("", "", "footer"));
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        writer.open(executionContext);
        writer.update(executionContext);
        writer.close();
        Assert.assertFalse(("file should be deleted" + (resource)), resource.getFile().exists());
        writer.open(executionContext);
        writer.write(items);
        writer.update(executionContext);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.TEST_STRING));
    }

    /**
     * Item is written to the output file with namespace.
     */
    @Test
    public void testWriteRootTagWithNamespace() throws Exception {
        writer.setRootTagName("{http://www.springframework.org/test}root");
        writer.afterPropertiesSet();
        writer.open(executionContext);
        writer.write(items);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains("<root xmlns=\"http://www.springframework.org/test\">"));
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.TEST_STRING));
        Assert.assertTrue(("Wrong content: " + content), content.contains("</root>"));
    }

    /**
     * Item is written to the output file with namespace and prefix.
     */
    @Test
    public void testWriteRootTagWithNamespaceAndPrefix() throws Exception {
        writer.setRootTagName("{http://www.springframework.org/test}ns:root");
        writer.afterPropertiesSet();
        marshaller.setNamespace(writer.getRootTagNamespace());
        marshaller.setNamespacePrefix(writer.getRootTagNamespacePrefix());
        writer.open(executionContext);
        writer.write(items);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains("<ns:root xmlns:ns=\"http://www.springframework.org/test\">"));
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.NS_TEST_STRING));
        Assert.assertTrue(("Wrong content: " + content), content.contains("</ns:root>"));
        Assert.assertTrue(("Wrong content: " + content), content.contains("<ns:root"));
    }

    /**
     * Item is written to the output file with additional namespaces and prefix.
     */
    @Test
    public void testWriteRootTagWithAdditionalNamespace() throws Exception {
        writer.setRootTagName("{http://www.springframework.org/test}ns:root");
        marshaller.setNamespace("urn:org.test.foo");
        marshaller.setNamespacePrefix("foo");
        writer.setRootElementAttributes(Collections.singletonMap("xmlns:foo", "urn:org.test.foo"));
        writer.afterPropertiesSet();
        writer.open(executionContext);
        writer.write(items);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertTrue(("Wrong content: " + content), content.contains(("<ns:root xmlns:ns=\"http://www.springframework.org/test\" " + "xmlns:foo=\"urn:org.test.foo\">")));
        Assert.assertTrue(("Wrong content: " + content), content.contains(StaxEventItemWriterTests.FOO_TEST_STRING));
        Assert.assertTrue(("Wrong content: " + content), content.contains("</ns:root>"));
        Assert.assertTrue(("Wrong content: " + content), content.contains("<ns:root"));
    }

    /**
     * Namespace prefixes are properly initialized on restart.
     */
    @Test
    public void testRootTagWithNamespaceRestart() throws Exception {
        writer.setMarshaller(jaxbMarshaller);
        writer.setRootTagName("{http://www.springframework.org/test}root");
        writer.afterPropertiesSet();
        writer.open(executionContext);
        writer.write(jaxbItems);
        writer.update(executionContext);
        writer.close();
        writer = createItemWriter();
        writer.setMarshaller(jaxbMarshaller);
        writer.setRootTagName("{http://www.springframework.org/test}root");
        writer.afterPropertiesSet();
        writer.open(executionContext);
        writer.write(jaxbItems);
        writer.update(executionContext);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertEquals(("Wrong content: " + content), "<root xmlns=\"http://www.springframework.org/test\"><item/><item/></root>", content);
    }

    /**
     * Namespace prefixes are properly initialized on restart.
     */
    @Test
    public void testRootTagWithNamespaceAndPrefixRestart() throws Exception {
        writer.setMarshaller(jaxbMarshaller);
        writer.setRootTagName("{http://www.springframework.org/test}ns:root");
        writer.afterPropertiesSet();
        writer.open(executionContext);
        writer.write(jaxbItems);
        writer.update(executionContext);
        writer.close();
        writer = createItemWriter();
        writer.setMarshaller(jaxbMarshaller);
        writer.setRootTagName("{http://www.springframework.org/test}ns:root");
        writer.afterPropertiesSet();
        writer.open(executionContext);
        writer.write(jaxbItems);
        writer.update(executionContext);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertEquals(("Wrong content: " + content), "<ns:root xmlns:ns=\"http://www.springframework.org/test\"><ns:item/><ns:item/></ns:root>", content);
    }

    /**
     * Namespace prefixes are properly initialized on restart.
     */
    @Test
    public void testRootTagWithAdditionalNamespaceRestart() throws Exception {
        writer.setMarshaller(jaxbMarshaller);
        writer.setRootTagName("{urn:org.test.foo}foo:root");
        writer.setRootElementAttributes(Collections.singletonMap("xmlns:ns", "http://www.springframework.org/test"));
        writer.afterPropertiesSet();
        writer.open(executionContext);
        writer.write(jaxbItems);
        writer.update(executionContext);
        writer.close();
        writer = createItemWriter();
        writer.setMarshaller(jaxbMarshaller);
        writer.setRootTagName("{urn:org.test.foo}foo:root");
        writer.setRootElementAttributes(Collections.singletonMap("xmlns:ns", "http://www.springframework.org/test"));
        writer.afterPropertiesSet();
        writer.open(executionContext);
        writer.write(jaxbItems);
        writer.update(executionContext);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertEquals(("Wrong content: " + content), "<foo:root xmlns:foo=\"urn:org.test.foo\" xmlns:ns=\"http://www.springframework.org/test\"><ns:item/><ns:item/></foo:root>", content);
    }

    /**
     * Test with OXM Marshaller that closes the XMLEventWriter.
     */
    // BATCH-2054
    @Test
    public void testMarshallingClosingEventWriter() throws Exception {
        writer.setMarshaller(new StaxEventItemWriterTests.SimpleMarshaller() {
            @Override
            public void marshal(Object graph, Result result) throws IOException, XmlMappingException {
                super.marshal(graph, result);
                try {
                    StaxTestUtils.getXmlEventWriter(result).close();
                } catch (Exception e) {
                    throw new RuntimeException("Exception while writing to output file", e);
                }
            }
        });
        writer.afterPropertiesSet();
        writer.open(executionContext);
        writer.write(items);
        writer.write(items);
    }

    /**
     * Test opening and closing corresponding tags in header- and footer callback.
     */
    @Test
    public void testOpenAndCloseTagsInCallbacks() throws Exception {
        initWriterForSimpleCallbackTests();
        writer.open(executionContext);
        writer.write(items);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertEquals(("Wrong content: " + content), "<ns:testroot xmlns:ns=\"http://www.springframework.org/test\"><ns:group><StaxEventItemWriter-testString/></ns:group></ns:testroot>", content);
    }

    /**
     * Test opening and closing corresponding tags in header- and footer callback (restart).
     */
    @Test
    public void testOpenAndCloseTagsInCallbacksRestart() throws Exception {
        initWriterForSimpleCallbackTests();
        writer.open(executionContext);
        writer.write(items);
        writer.update(executionContext);
        initWriterForSimpleCallbackTests();
        writer.open(executionContext);
        writer.write(items);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertEquals(("Wrong content: " + content), ("<ns:testroot xmlns:ns=\"http://www.springframework.org/test\">" + "<ns:group><StaxEventItemWriter-testString/><StaxEventItemWriter-testString/></ns:group></ns:testroot>"), content);
    }

    /**
     * Test opening and closing corresponding tags in complex header- and footer callback (restart).
     */
    @Test
    public void testOpenAndCloseTagsInComplexCallbacksRestart() throws Exception {
        initWriterForComplexCallbackTests();
        writer.open(executionContext);
        writer.write(items);
        writer.update(executionContext);
        initWriterForComplexCallbackTests();
        writer.open(executionContext);
        writer.write(items);
        writer.close();
        String content = getOutputFileContent();
        Assert.assertEquals(("Wrong content: " + content), ("<ns:testroot xmlns:ns=\"http://www.springframework.org/test\">" + ((("<preHeader>PRE-HEADER</preHeader><ns:group><subGroup><postHeader>POST-HEADER</postHeader>" + "<StaxEventItemWriter-testString/><StaxEventItemWriter-testString/>") + "<preFooter>PRE-FOOTER</preFooter></subGroup></ns:group><postFooter>POST-FOOTER</postFooter>") + "</ns:testroot>")), content);
    }

    /**
     * Writes object's toString representation as XML comment.
     */
    private static class SimpleMarshaller implements Marshaller {
        private String namespacePrefix = "";

        private String namespace = "";

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public void setNamespacePrefix(String namespacePrefix) {
            this.namespacePrefix = namespacePrefix;
        }

        @Override
        public void marshal(Object graph, Result result) throws IOException, XmlMappingException {
            org.springframework.util.Assert.isInstanceOf(Result.class, result);
            try {
                StaxTestUtils.getXmlEventWriter(result).add(XMLEventFactory.newInstance().createStartElement(namespacePrefix, namespace, graph.toString()));
                StaxTestUtils.getXmlEventWriter(result).add(XMLEventFactory.newInstance().createEndElement(namespacePrefix, namespace, graph.toString()));
            } catch (Exception e) {
                throw new RuntimeException("Exception while writing to output file", e);
            }
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return true;
        }
    }

    @XmlRootElement(name = "item", namespace = "http://www.springframework.org/test")
    private static class JAXBItem {}
}

