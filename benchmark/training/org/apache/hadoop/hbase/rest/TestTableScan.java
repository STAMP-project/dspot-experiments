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
package org.apache.hadoop.hbase.rest;


import Constants.MIMETYPE_JSON;
import Constants.MIMETYPE_PROTOBUF;
import Constants.MIMETYPE_XML;
import MediaType.APPLICATION_JSON_TYPE;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.ParseFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.rest.model.CellModel;
import org.apache.hadoop.hbase.rest.model.CellSetModel;
import org.apache.hadoop.hbase.rest.model.RowModel;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import static Constants.SCAN_COLUMN;
import static Constants.SCAN_END_ROW;
import static Constants.SCAN_FILTER;
import static Constants.SCAN_LIMIT;
import static Constants.SCAN_REVERSED;
import static Constants.SCAN_START_ROW;


@Category({ RestTests.class, MediumTests.class })
public class TestTableScan {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableScan.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableScan.class);

    private static final TableName TABLE = TableName.valueOf("TestScanResource");

    private static final String CFA = "a";

    private static final String CFB = "b";

    private static final String COLUMN_1 = (TestTableScan.CFA) + ":1";

    private static final String COLUMN_2 = (TestTableScan.CFB) + ":2";

    private static final String COLUMN_EMPTY = (TestTableScan.CFA) + ":";

    private static Client client;

    private static int expectedRows1;

    private static int expectedRows2;

    private static int expectedRows3;

    private static Configuration conf;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    @Test
    public void testSimpleScannerXML() throws IOException, JAXBException, XMLStreamException {
        // Test scanning particular columns
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_LIMIT) + "=10"));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        JAXBContext ctx = JAXBContext.newInstance(CellSetModel.class);
        Unmarshaller ush = ctx.createUnmarshaller();
        CellSetModel model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        int count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(10, count);
        checkRowsNotNull(model);
        // Test with no limit.
        builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(TestTableScan.expectedRows1, count);
        checkRowsNotNull(model);
        // Test with start and end row.
        builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_START_ROW) + "=aaa"));
        builder.append("&");
        builder.append(((SCAN_END_ROW) + "=aay"));
        response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        count = TestScannerResource.countCellSet(model);
        RowModel startRow = model.getRows().get(0);
        Assert.assertEquals("aaa", Bytes.toString(startRow.getKey()));
        RowModel endRow = model.getRows().get(((model.getRows().size()) - 1));
        Assert.assertEquals("aax", Bytes.toString(endRow.getKey()));
        Assert.assertEquals(24, count);
        checkRowsNotNull(model);
        // Test with start row and limit.
        builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_START_ROW) + "=aaa"));
        builder.append("&");
        builder.append(((SCAN_LIMIT) + "=15"));
        response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        startRow = model.getRows().get(0);
        Assert.assertEquals("aaa", Bytes.toString(startRow.getKey()));
        count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(15, count);
        checkRowsNotNull(model);
    }

    @Test
    public void testSimpleScannerJson() throws IOException, JAXBException {
        // Test scanning particular columns with limit.
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_LIMIT) + "=2"));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(CellSetModel.class, APPLICATION_JSON_TYPE);
        CellSetModel model = mapper.readValue(response.getStream(), CellSetModel.class);
        int count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(2, count);
        checkRowsNotNull(model);
        // Test scanning with no limit.
        builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_2)));
        response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        model = mapper.readValue(response.getStream(), CellSetModel.class);
        count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(TestTableScan.expectedRows2, count);
        checkRowsNotNull(model);
        // Test with start row and end row.
        builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_START_ROW) + "=aaa"));
        builder.append("&");
        builder.append(((SCAN_END_ROW) + "=aay"));
        response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        model = mapper.readValue(response.getStream(), CellSetModel.class);
        RowModel startRow = model.getRows().get(0);
        Assert.assertEquals("aaa", Bytes.toString(startRow.getKey()));
        RowModel endRow = model.getRows().get(((model.getRows().size()) - 1));
        Assert.assertEquals("aax", Bytes.toString(endRow.getKey()));
        count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(24, count);
        checkRowsNotNull(model);
    }

    /**
     * An example to scan using listener in unmarshaller for XML.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testScanUsingListenerUnmarshallerXML() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_LIMIT) + "=10"));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        JAXBContext context = JAXBContext.newInstance(TestTableScan.ClientSideCellSetModel.class, RowModel.class, CellModel.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        final TestTableScan.ClientSideCellSetModel.Listener listener = new TestTableScan.ClientSideCellSetModel.Listener() {
            @Override
            public void handleRowModel(TestTableScan.ClientSideCellSetModel helper, RowModel row) {
                Assert.assertTrue(((row.getKey()) != null));
                Assert.assertTrue(((row.getCells().size()) > 0));
            }
        };
        // install the callback on all ClientSideCellSetModel instances
        unmarshaller.setListener(new Unmarshaller.Listener() {
            @Override
            public void beforeUnmarshal(Object target, Object parent) {
                if (target instanceof TestTableScan.ClientSideCellSetModel) {
                    ((TestTableScan.ClientSideCellSetModel) (target)).setCellSetModelListener(listener);
                }
            }

            @Override
            public void afterUnmarshal(Object target, Object parent) {
                if (target instanceof TestTableScan.ClientSideCellSetModel) {
                    ((TestTableScan.ClientSideCellSetModel) (target)).setCellSetModelListener(null);
                }
            }
        });
        // create a new XML parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
        Assert.assertFalse(TestTableScan.ClientSideCellSetModel.listenerInvoked);
        reader.parse(new InputSource(response.getStream()));
        Assert.assertTrue(TestTableScan.ClientSideCellSetModel.listenerInvoked);
    }

    @Test
    public void testStreamingJSON() throws Exception {
        // Test with start row and end row.
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_START_ROW) + "=aaa"));
        builder.append("&");
        builder.append(((SCAN_END_ROW) + "=aay"));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        int count = 0;
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(CellSetModel.class, APPLICATION_JSON_TYPE);
        JsonFactory jfactory = new JsonFactory(mapper);
        JsonParser jParser = jfactory.createJsonParser(response.getStream());
        boolean found = false;
        while ((jParser.nextToken()) != (JsonToken.END_OBJECT)) {
            if (((jParser.getCurrentToken()) == (JsonToken.START_OBJECT)) && found) {
                RowModel row = jParser.readValueAs(RowModel.class);
                Assert.assertNotNull(row.getKey());
                for (int i = 0; i < (row.getCells().size()); i++) {
                    if (count == 0) {
                        Assert.assertEquals("aaa", Bytes.toString(row.getKey()));
                    }
                    if (count == 23) {
                        Assert.assertEquals("aax", Bytes.toString(row.getKey()));
                    }
                    count++;
                }
                jParser.skipChildren();
            } else {
                found = (jParser.getCurrentToken()) == (JsonToken.START_ARRAY);
            }
        } 
        Assert.assertEquals(24, count);
    }

    @Test
    public void testSimpleScannerProtobuf() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_LIMIT) + "=15"));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF, response.getHeader("content-type"));
        int rowCount = readProtobufStream(response.getStream());
        Assert.assertEquals(15, rowCount);
        // Test with start row and end row.
        builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_START_ROW) + "=aaa"));
        builder.append("&");
        builder.append(((SCAN_END_ROW) + "=aay"));
        response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_PROTOBUF);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_PROTOBUF, response.getHeader("content-type"));
        rowCount = readProtobufStream(response.getStream());
        Assert.assertEquals(24, rowCount);
    }

    @Test
    public void testScanningUnknownColumnJson() throws IOException, JAXBException {
        // Test scanning particular columns with limit.
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append(((SCAN_COLUMN) + "=a:test"));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(CellSetModel.class, APPLICATION_JSON_TYPE);
        CellSetModel model = mapper.readValue(response.getStream(), CellSetModel.class);
        int count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(0, count);
    }

    @Test
    public void testSimpleFilter() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_START_ROW) + "=aaa"));
        builder.append("&");
        builder.append(((SCAN_END_ROW) + "=aay"));
        builder.append("&");
        builder.append((((SCAN_FILTER) + "=") + (URLEncoder.encode("PrefixFilter('aab')", "UTF-8"))));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        JAXBContext ctx = JAXBContext.newInstance(CellSetModel.class);
        Unmarshaller ush = ctx.createUnmarshaller();
        CellSetModel model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        int count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(1, count);
        Assert.assertEquals("aab", new String(model.getRows().get(0).getCells().get(0).getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testQualifierAndPrefixFilters() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("/abc*");
        builder.append("?");
        builder.append((((SCAN_FILTER) + "=") + (URLEncoder.encode("QualifierFilter(=,'binary:1')", "UTF-8"))));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        JAXBContext ctx = JAXBContext.newInstance(CellSetModel.class);
        Unmarshaller ush = ctx.createUnmarshaller();
        CellSetModel model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        int count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(1, count);
        Assert.assertEquals("abc", new String(model.getRows().get(0).getCells().get(0).getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testCompoundFilter() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_FILTER) + "=") + (URLEncoder.encode("PrefixFilter('abc') AND QualifierFilter(=,'binary:1')", "UTF-8"))));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        JAXBContext ctx = JAXBContext.newInstance(CellSetModel.class);
        Unmarshaller ush = ctx.createUnmarshaller();
        CellSetModel model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        int count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(1, count);
        Assert.assertEquals("abc", new String(model.getRows().get(0).getCells().get(0).getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testCustomFilter() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("/a*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append((((SCAN_FILTER) + "=") + (URLEncoder.encode("CustomFilter('abc')", "UTF-8"))));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        JAXBContext ctx = JAXBContext.newInstance(CellSetModel.class);
        Unmarshaller ush = ctx.createUnmarshaller();
        CellSetModel model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        int count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(1, count);
        Assert.assertEquals("abc", new String(model.getRows().get(0).getCells().get(0).getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testNegativeCustomFilter() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("/b*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append((((SCAN_FILTER) + "=") + (URLEncoder.encode("CustomFilter('abc')", "UTF-8"))));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        JAXBContext ctx = JAXBContext.newInstance(CellSetModel.class);
        Unmarshaller ush = ctx.createUnmarshaller();
        CellSetModel model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        int count = TestScannerResource.countCellSet(model);
        // Should return no rows as the filters conflict
        Assert.assertEquals(0, count);
    }

    @Test
    public void testReversed() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_START_ROW) + "=aaa"));
        builder.append("&");
        builder.append(((SCAN_END_ROW) + "=aay"));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        JAXBContext ctx = JAXBContext.newInstance(CellSetModel.class);
        Unmarshaller ush = ctx.createUnmarshaller();
        CellSetModel model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        int count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(24, count);
        List<RowModel> rowModels = model.getRows().subList(1, count);
        // reversed
        builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append(((SCAN_START_ROW) + "=aay"));
        builder.append("&");
        builder.append(((SCAN_END_ROW) + "=aaa"));
        builder.append("&");
        builder.append(((SCAN_REVERSED) + "=true"));
        response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        model = ((CellSetModel) (ush.unmarshal(response.getStream())));
        count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(24, count);
        List<RowModel> reversedRowModels = model.getRows().subList(1, count);
        Collections.reverse(reversedRowModels);
        Assert.assertEquals(rowModels.size(), reversedRowModels.size());
        for (int i = 0; i < (rowModels.size()); i++) {
            RowModel rowModel = rowModels.get(i);
            RowModel reversedRowModel = reversedRowModels.get(i);
            Assert.assertEquals(new String(rowModel.getKey(), "UTF-8"), new String(reversedRowModel.getKey(), "UTF-8"));
            Assert.assertEquals(new String(rowModel.getCells().get(0).getValue(), "UTF-8"), new String(reversedRowModel.getCells().get(0).getValue(), "UTF-8"));
        }
    }

    @Test
    public void testColumnWithEmptyQualifier() throws IOException, JAXBException {
        // Test scanning with empty qualifier
        StringBuilder builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_EMPTY)));
        Response response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(CellSetModel.class, APPLICATION_JSON_TYPE);
        CellSetModel model = mapper.readValue(response.getStream(), CellSetModel.class);
        int count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(TestTableScan.expectedRows3, count);
        checkRowsNotNull(model);
        RowModel startRow = model.getRows().get(0);
        Assert.assertEquals("aaa", Bytes.toString(startRow.getKey()));
        Assert.assertEquals(1, startRow.getCells().size());
        // Test scanning with empty qualifier and normal qualifier
        builder = new StringBuilder();
        builder.append("/*");
        builder.append("?");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_1)));
        builder.append("&");
        builder.append((((SCAN_COLUMN) + "=") + (TestTableScan.COLUMN_EMPTY)));
        response = TestTableScan.client.get((("/" + (TestTableScan.TABLE)) + (builder.toString())), MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        mapper = new JacksonJaxbJsonProvider().locateMapper(CellSetModel.class, APPLICATION_JSON_TYPE);
        model = mapper.readValue(response.getStream(), CellSetModel.class);
        count = TestScannerResource.countCellSet(model);
        Assert.assertEquals(((TestTableScan.expectedRows1) + (TestTableScan.expectedRows3)), count);
        checkRowsNotNull(model);
    }

    public static class CustomFilter extends PrefixFilter {
        private byte[] key = null;

        public CustomFilter(byte[] key) {
            super(key);
        }

        @Override
        public boolean filterRowKey(byte[] buffer, int offset, int length) {
            int cmp = Bytes.compareTo(buffer, offset, length, this.key, 0, this.key.length);
            return cmp != 0;
        }

        public static Filter createFilterFromArguments(ArrayList<byte[]> filterArguments) {
            byte[] prefix = ParseFilter.removeQuotesFromByteArray(filterArguments.get(0));
            return new TestTableScan.CustomFilter(prefix);
        }
    }

    /**
     * The Class ClientSideCellSetModel which mimics cell set model, and contains listener to perform
     * user defined operations on the row model.
     */
    @XmlRootElement(name = "CellSet")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ClientSideCellSetModel implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * This list is not a real list; instead it will notify a listener whenever JAXB has
         * unmarshalled the next row.
         */
        @XmlElement(name = "Row")
        private List<RowModel> row;

        static boolean listenerInvoked = false;

        /**
         * Install a listener for row model on this object. If l is null, the listener
         * is removed again.
         */
        public void setCellSetModelListener(final TestTableScan.ClientSideCellSetModel.Listener l) {
            row = (l == null) ? null : new ArrayList<RowModel>() {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean add(RowModel o) {
                    l.handleRowModel(TestTableScan.ClientSideCellSetModel.this, o);
                    TestTableScan.ClientSideCellSetModel.listenerInvoked = true;
                    return false;
                }
            };
        }

        /**
         * This listener is invoked every time a new row model is unmarshalled.
         */
        public static interface Listener {
            void handleRowModel(TestTableScan.ClientSideCellSetModel helper, RowModel rowModel);
        }
    }
}

