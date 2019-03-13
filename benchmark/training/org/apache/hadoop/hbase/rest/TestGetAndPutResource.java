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


import Constants.MIMETYPE_BINARY;
import Constants.MIMETYPE_JSON;
import Constants.MIMETYPE_PROTOBUF;
import Constants.MIMETYPE_XML;
import HConstants.UTF8_ENCODING;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.hadoop.hbase.CompatibilityFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.rest.model.CellModel;
import org.apache.hadoop.hbase.rest.model.CellSetModel;
import org.apache.hadoop.hbase.rest.model.RowModel;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.Header;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RestTests.class, MediumTests.class })
public class TestGetAndPutResource extends RowResourceBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestGetAndPutResource.class);

    private static final MetricsAssertHelper METRICS_ASSERT = CompatibilityFactory.getInstance(MetricsAssertHelper.class);

    @Test
    public void testForbidden() throws IOException, JAXBException {
        RowResourceBase.conf.set("hbase.rest.readonly", "true");
        Response response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(403, response.getCode());
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(403, response.getCode());
        response = RowResourceBase.checkAndPutValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(403, response.getCode());
        response = RowResourceBase.checkAndPutValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(403, response.getCode());
        response = RowResourceBase.deleteValue(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(403, response.getCode());
        response = RowResourceBase.checkAndDeletePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(403, response.getCode());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(403, response.getCode());
        RowResourceBase.conf.set("hbase.rest.readonly", "false");
        response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.checkAndPutValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.checkAndPutValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2, RowResourceBase.VALUE_3);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.deleteValue(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testSingleCellGetPutXML() throws IOException, JAXBException {
        Response response = RowResourceBase.getValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2);
        response = RowResourceBase.checkAndPutValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2, RowResourceBase.VALUE_3);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        response = RowResourceBase.checkAndDeleteXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testSingleCellGetPutPB() throws IOException, JAXBException {
        Response response = RowResourceBase.getValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2);
        response = RowResourceBase.checkAndPutValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2, RowResourceBase.VALUE_3);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        response = RowResourceBase.checkAndPutValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3, RowResourceBase.VALUE_4);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_4);
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testMultipleCellCheckPutPB() throws IOException, JAXBException {
        Response response = RowResourceBase.getValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // Add 2 Columns to setup the test
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        HashMap<String, String> otherCells = new HashMap<>();
        otherCells.put(RowResourceBase.COLUMN_2, RowResourceBase.VALUE_3);
        // On Success update both the cells
        response = RowResourceBase.checkAndPutValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1, RowResourceBase.VALUE_3, otherCells);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_3);
        // On Failure, we dont update any cells
        response = RowResourceBase.checkAndPutValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1, RowResourceBase.VALUE_4, otherCells);
        Assert.assertEquals(304, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_3);
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testMultipleCellCheckPutXML() throws IOException, JAXBException {
        Response response = RowResourceBase.getValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // Add 2 Columns to setup the test
        response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        HashMap<String, String> otherCells = new HashMap<>();
        otherCells.put(RowResourceBase.COLUMN_2, RowResourceBase.VALUE_3);
        // On Success update both the cells
        response = RowResourceBase.checkAndPutValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1, RowResourceBase.VALUE_3, otherCells);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_3);
        // On Failure, we dont update any cells
        response = RowResourceBase.checkAndPutValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1, RowResourceBase.VALUE_4, otherCells);
        Assert.assertEquals(304, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_3);
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testMultipleCellCheckDeletePB() throws IOException, JAXBException {
        Response response = RowResourceBase.getValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // Add 3 Columns to setup the test
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_3, RowResourceBase.VALUE_3);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_3, RowResourceBase.VALUE_3);
        // Deletes the following columns based on Column1 check
        HashMap<String, String> cellsToDelete = new HashMap<>();
        cellsToDelete.put(RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);// Value does not matter

        cellsToDelete.put(RowResourceBase.COLUMN_3, RowResourceBase.VALUE_3);// Value does not matter

        // On Success update both the cells
        response = RowResourceBase.checkAndDeletePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1, cellsToDelete);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        response = RowResourceBase.getValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2);
        Assert.assertEquals(404, response.getCode());
        response = RowResourceBase.getValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_3);
        Assert.assertEquals(404, response.getCode());
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        response = RowResourceBase.putValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_3, RowResourceBase.VALUE_3);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_3, RowResourceBase.VALUE_3);
        // On Failure, we dont update any cells
        response = RowResourceBase.checkAndDeletePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3, cellsToDelete);
        Assert.assertEquals(304, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_3, RowResourceBase.VALUE_3);
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testSingleCellGetPutBinary() throws IOException {
        final String path = (((("/" + (RowResourceBase.TABLE)) + "/") + (RowResourceBase.ROW_3)) + "/") + (RowResourceBase.COLUMN_1);
        final byte[] body = Bytes.toBytes(RowResourceBase.VALUE_3);
        Response response = RowResourceBase.client.put(path, MIMETYPE_BINARY, body);
        Assert.assertEquals(200, response.getCode());
        Thread.yield();
        response = RowResourceBase.client.get(path, MIMETYPE_BINARY);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_BINARY, response.getHeader("content-type"));
        Assert.assertTrue(Bytes.equals(response.getBody(), body));
        boolean foundTimestampHeader = false;
        for (Header header : response.getHeaders()) {
            if (header.getName().equals("X-Timestamp")) {
                foundTimestampHeader = true;
                break;
            }
        }
        Assert.assertTrue(foundTimestampHeader);
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_3);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testSingleCellGetJSON() throws IOException, JAXBException {
        final String path = (((("/" + (RowResourceBase.TABLE)) + "/") + (RowResourceBase.ROW_4)) + "/") + (RowResourceBase.COLUMN_1);
        Response response = RowResourceBase.client.put(path, MIMETYPE_BINARY, Bytes.toBytes(RowResourceBase.VALUE_4));
        Assert.assertEquals(200, response.getCode());
        Thread.yield();
        response = RowResourceBase.client.get(path, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_4);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testLatestCellGetJSON() throws IOException, JAXBException {
        final String path = (((("/" + (RowResourceBase.TABLE)) + "/") + (RowResourceBase.ROW_4)) + "/") + (RowResourceBase.COLUMN_1);
        CellSetModel cellSetModel = new CellSetModel();
        RowModel rowModel = new RowModel(RowResourceBase.ROW_4);
        CellModel cellOne = new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), 1L, Bytes.toBytes(RowResourceBase.VALUE_1));
        CellModel cellTwo = new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), 2L, Bytes.toBytes(RowResourceBase.VALUE_2));
        rowModel.addCell(cellOne);
        rowModel.addCell(cellTwo);
        cellSetModel.addRow(rowModel);
        String jsonString = RowResourceBase.jsonMapper.writeValueAsString(cellSetModel);
        Response response = RowResourceBase.client.put(path, MIMETYPE_JSON, Bytes.toBytes(jsonString));
        Assert.assertEquals(200, response.getCode());
        Thread.yield();
        response = RowResourceBase.client.get(path, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        CellSetModel cellSet = RowResourceBase.jsonMapper.readValue(response.getBody(), CellSetModel.class);
        Assert.assertTrue(((cellSet.getRows().size()) == 1));
        Assert.assertTrue(((cellSet.getRows().get(0).getCells().size()) == 1));
        CellModel cell = cellSet.getRows().get(0).getCells().get(0);
        Assert.assertEquals(RowResourceBase.VALUE_2, Bytes.toString(cell.getValue()));
        Assert.assertEquals(2L, cell.getTimestamp());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_4);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testURLEncodedKey() throws IOException, JAXBException {
        String urlKey = "http://example.com/foo";
        StringBuilder path = new StringBuilder();
        path.append('/');
        path.append(RowResourceBase.TABLE);
        path.append('/');
        path.append(URLEncoder.encode(urlKey, UTF8_ENCODING));
        path.append('/');
        path.append(RowResourceBase.COLUMN_1);
        Response response;
        response = RowResourceBase.putValueXML(path.toString(), RowResourceBase.TABLE, urlKey, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(path.toString(), RowResourceBase.TABLE, urlKey, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
    }

    @Test
    public void testNoSuchCF() throws IOException, JAXBException {
        final String goodPath = ((((("/" + (RowResourceBase.TABLE)) + "/") + (RowResourceBase.ROW_1)) + "/") + (RowResourceBase.CFA)) + ":";
        final String badPath = (((("/" + (RowResourceBase.TABLE)) + "/") + (RowResourceBase.ROW_1)) + "/") + "BAD";
        Response response = RowResourceBase.client.post(goodPath, MIMETYPE_BINARY, Bytes.toBytes(RowResourceBase.VALUE_1));
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(200, RowResourceBase.client.get(goodPath, MIMETYPE_BINARY).getCode());
        Assert.assertEquals(404, RowResourceBase.client.get(badPath, MIMETYPE_BINARY).getCode());
        Assert.assertEquals(200, RowResourceBase.client.get(goodPath, MIMETYPE_BINARY).getCode());
    }

    @Test
    public void testMultiCellGetPutXML() throws IOException, JAXBException {
        String path = ("/" + (RowResourceBase.TABLE)) + "/fakerow";// deliberate nonexistent row

        CellSetModel cellSetModel = new CellSetModel();
        RowModel rowModel = new RowModel(RowResourceBase.ROW_1);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_1)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_2)));
        cellSetModel.addRow(rowModel);
        rowModel = new RowModel(RowResourceBase.ROW_2);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_3)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_4)));
        cellSetModel.addRow(rowModel);
        StringWriter writer = new StringWriter();
        RowResourceBase.xmlMarshaller.marshal(cellSetModel, writer);
        Response response = RowResourceBase.client.put(path, MIMETYPE_XML, Bytes.toBytes(writer.toString()));
        Thread.yield();
        // make sure the fake row was not actually created
        response = RowResourceBase.client.get(path, MIMETYPE_XML);
        Assert.assertEquals(404, response.getCode());
        // check that all of the values were created
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_2, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_2, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_4);
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_2);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testMultiCellGetPutPB() throws IOException {
        String path = ("/" + (RowResourceBase.TABLE)) + "/fakerow";// deliberate nonexistent row

        CellSetModel cellSetModel = new CellSetModel();
        RowModel rowModel = new RowModel(RowResourceBase.ROW_1);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_1)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_2)));
        cellSetModel.addRow(rowModel);
        rowModel = new RowModel(RowResourceBase.ROW_2);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_3)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_4)));
        cellSetModel.addRow(rowModel);
        Response response = RowResourceBase.client.put(path, MIMETYPE_PROTOBUF, cellSetModel.createProtobufOutput());
        Thread.yield();
        // make sure the fake row was not actually created
        response = RowResourceBase.client.get(path, MIMETYPE_PROTOBUF);
        Assert.assertEquals(404, response.getCode());
        // check that all of the values were created
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_2, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_2, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_4);
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_2);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testStartEndRowGetPutXML() throws IOException, JAXBException {
        String[] rows = new String[]{ RowResourceBase.ROW_1, RowResourceBase.ROW_2, RowResourceBase.ROW_3 };
        String[] values = new String[]{ RowResourceBase.VALUE_1, RowResourceBase.VALUE_2, RowResourceBase.VALUE_3 };
        Response response = null;
        for (int i = 0; i < (rows.length); i++) {
            response = RowResourceBase.putValueXML(RowResourceBase.TABLE, rows[i], RowResourceBase.COLUMN_1, values[i]);
            Assert.assertEquals(200, response.getCode());
            RowResourceBase.checkValueXML(RowResourceBase.TABLE, rows[i], RowResourceBase.COLUMN_1, values[i]);
        }
        response = RowResourceBase.getValueXML(RowResourceBase.TABLE, rows[0], rows[2], RowResourceBase.COLUMN_1);
        Assert.assertEquals(200, response.getCode());
        CellSetModel cellSet = ((CellSetModel) (RowResourceBase.xmlUnmarshaller.unmarshal(new ByteArrayInputStream(response.getBody()))));
        Assert.assertEquals(2, cellSet.getRows().size());
        for (int i = 0; i < ((cellSet.getRows().size()) - 1); i++) {
            RowModel rowModel = cellSet.getRows().get(i);
            for (CellModel cell : rowModel.getCells()) {
                Assert.assertEquals(RowResourceBase.COLUMN_1, Bytes.toString(cell.getColumn()));
                Assert.assertEquals(values[i], Bytes.toString(cell.getValue()));
            }
        }
        for (String row : rows) {
            response = RowResourceBase.deleteRow(RowResourceBase.TABLE, row);
            Assert.assertEquals(200, response.getCode());
        }
    }

    @Test
    public void testInvalidCheckParam() throws IOException, JAXBException {
        CellSetModel cellSetModel = new CellSetModel();
        RowModel rowModel = new RowModel(RowResourceBase.ROW_1);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_1)));
        cellSetModel.addRow(rowModel);
        StringWriter writer = new StringWriter();
        RowResourceBase.xmlMarshaller.marshal(cellSetModel, writer);
        final String path = ((((("/" + (RowResourceBase.TABLE)) + "/") + (RowResourceBase.ROW_1)) + "/") + (RowResourceBase.COLUMN_1)) + "?check=blah";
        Response response = RowResourceBase.client.put(path, MIMETYPE_XML, Bytes.toBytes(writer.toString()));
        Assert.assertEquals(400, response.getCode());
    }

    @Test
    public void testInvalidColumnPut() throws IOException, JAXBException {
        String dummyColumn = "doesnot:exist";
        CellSetModel cellSetModel = new CellSetModel();
        RowModel rowModel = new RowModel(RowResourceBase.ROW_1);
        rowModel.addCell(new CellModel(Bytes.toBytes(dummyColumn), Bytes.toBytes(RowResourceBase.VALUE_1)));
        cellSetModel.addRow(rowModel);
        StringWriter writer = new StringWriter();
        RowResourceBase.xmlMarshaller.marshal(cellSetModel, writer);
        final String path = (((("/" + (RowResourceBase.TABLE)) + "/") + (RowResourceBase.ROW_1)) + "/") + dummyColumn;
        Response response = RowResourceBase.client.put(path, MIMETYPE_XML, Bytes.toBytes(writer.toString()));
        Assert.assertEquals(404, response.getCode());
    }

    @Test
    public void testMultiCellGetJson() throws IOException, JAXBException {
        String path = ("/" + (RowResourceBase.TABLE)) + "/fakerow";// deliberate nonexistent row

        CellSetModel cellSetModel = new CellSetModel();
        RowModel rowModel = new RowModel(RowResourceBase.ROW_1);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_1)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_2)));
        cellSetModel.addRow(rowModel);
        rowModel = new RowModel(RowResourceBase.ROW_2);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_3)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_4)));
        cellSetModel.addRow(rowModel);
        String jsonString = RowResourceBase.jsonMapper.writeValueAsString(cellSetModel);
        Response response = RowResourceBase.client.put(path, MIMETYPE_JSON, Bytes.toBytes(jsonString));
        Thread.yield();
        // make sure the fake row was not actually created
        response = RowResourceBase.client.get(path, MIMETYPE_JSON);
        Assert.assertEquals(404, response.getCode());
        // check that all of the values were created
        RowResourceBase.checkValueJSON(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        RowResourceBase.checkValueJSON(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        RowResourceBase.checkValueJSON(RowResourceBase.TABLE, RowResourceBase.ROW_2, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_3);
        RowResourceBase.checkValueJSON(RowResourceBase.TABLE, RowResourceBase.ROW_2, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_4);
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_2);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testMetrics() throws IOException, JAXBException {
        final String path = (((("/" + (RowResourceBase.TABLE)) + "/") + (RowResourceBase.ROW_4)) + "/") + (RowResourceBase.COLUMN_1);
        Response response = RowResourceBase.client.put(path, MIMETYPE_BINARY, Bytes.toBytes(RowResourceBase.VALUE_4));
        Assert.assertEquals(200, response.getCode());
        Thread.yield();
        response = RowResourceBase.client.get(path, MIMETYPE_JSON);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_JSON, response.getHeader("content-type"));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_4);
        Assert.assertEquals(200, response.getCode());
        UserProvider userProvider = UserProvider.instantiate(RowResourceBase.conf);
        TestGetAndPutResource.METRICS_ASSERT.assertCounterGt("requests", 2L, RESTServlet.getInstance(RowResourceBase.conf, userProvider).getMetrics().getSource());
        TestGetAndPutResource.METRICS_ASSERT.assertCounterGt("successfulGet", 0L, RESTServlet.getInstance(RowResourceBase.conf, userProvider).getMetrics().getSource());
        TestGetAndPutResource.METRICS_ASSERT.assertCounterGt("successfulPut", 0L, RESTServlet.getInstance(RowResourceBase.conf, userProvider).getMetrics().getSource());
        TestGetAndPutResource.METRICS_ASSERT.assertCounterGt("successfulDelete", 0L, RESTServlet.getInstance(RowResourceBase.conf, userProvider).getMetrics().getSource());
    }

    @Test
    public void testMultiColumnGetXML() throws Exception {
        String path = ("/" + (RowResourceBase.TABLE)) + "/fakerow";
        CellSetModel cellSetModel = new CellSetModel();
        RowModel rowModel = new RowModel(RowResourceBase.ROW_1);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_1)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_2)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_3), Bytes.toBytes(RowResourceBase.VALUE_2)));
        cellSetModel.addRow(rowModel);
        StringWriter writer = new StringWriter();
        RowResourceBase.xmlMarshaller.marshal(cellSetModel, writer);
        Response response = RowResourceBase.client.put(path, MIMETYPE_XML, Bytes.toBytes(writer.toString()));
        Thread.yield();
        // make sure the fake row was not actually created
        response = RowResourceBase.client.get(path, MIMETYPE_XML);
        Assert.assertEquals(404, response.getCode());
        // Try getting all the column values at once.
        path = (((((((("/" + (RowResourceBase.TABLE)) + "/") + (RowResourceBase.ROW_1)) + "/") + (RowResourceBase.COLUMN_1)) + ",") + (RowResourceBase.COLUMN_2)) + ",") + (RowResourceBase.COLUMN_3);
        response = RowResourceBase.client.get(path, MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        CellSetModel cellSet = ((CellSetModel) (RowResourceBase.xmlUnmarshaller.unmarshal(new ByteArrayInputStream(response.getBody()))));
        Assert.assertTrue(((cellSet.getRows().size()) == 1));
        Assert.assertTrue(((cellSet.getRows().get(0).getCells().size()) == 3));
        List<CellModel> cells = cellSet.getRows().get(0).getCells();
        Assert.assertTrue(containsCellModel(cells, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1));
        Assert.assertTrue(containsCellModel(cells, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2));
        Assert.assertTrue(containsCellModel(cells, RowResourceBase.COLUMN_3, RowResourceBase.VALUE_2));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testSuffixGlobbingXMLWithNewScanner() throws IOException, JAXBException {
        String path = ("/" + (RowResourceBase.TABLE)) + "/fakerow";// deliberate nonexistent row

        CellSetModel cellSetModel = new CellSetModel();
        RowModel rowModel = new RowModel(RowResourceBase.ROW_1);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_1)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_2)));
        cellSetModel.addRow(rowModel);
        rowModel = new RowModel(RowResourceBase.ROW_2);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_3)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_4)));
        cellSetModel.addRow(rowModel);
        StringWriter writer = new StringWriter();
        RowResourceBase.xmlMarshaller.marshal(cellSetModel, writer);
        Response response = RowResourceBase.client.put(path, MIMETYPE_XML, Bytes.toBytes(writer.toString()));
        Thread.yield();
        // make sure the fake row was not actually created
        response = RowResourceBase.client.get(path, MIMETYPE_XML);
        Assert.assertEquals(404, response.getCode());
        // check that all of the values were created
        StringBuilder query = new StringBuilder();
        query.append('/');
        query.append(RowResourceBase.TABLE);
        query.append('/');
        query.append("testrow*");
        response = RowResourceBase.client.get(query.toString(), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        CellSetModel cellSet = ((CellSetModel) (RowResourceBase.xmlUnmarshaller.unmarshal(new ByteArrayInputStream(response.getBody()))));
        Assert.assertTrue(((cellSet.getRows().size()) == 2));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_2);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testSuffixGlobbingXML() throws IOException, JAXBException {
        String path = ("/" + (RowResourceBase.TABLE)) + "/fakerow";// deliberate nonexistent row

        CellSetModel cellSetModel = new CellSetModel();
        RowModel rowModel = new RowModel(RowResourceBase.ROW_1);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_1)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_2)));
        cellSetModel.addRow(rowModel);
        rowModel = new RowModel(RowResourceBase.ROW_2);
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_1), Bytes.toBytes(RowResourceBase.VALUE_3)));
        rowModel.addCell(new CellModel(Bytes.toBytes(RowResourceBase.COLUMN_2), Bytes.toBytes(RowResourceBase.VALUE_4)));
        cellSetModel.addRow(rowModel);
        StringWriter writer = new StringWriter();
        RowResourceBase.xmlMarshaller.marshal(cellSetModel, writer);
        Response response = RowResourceBase.client.put(path, MIMETYPE_XML, Bytes.toBytes(writer.toString()));
        Thread.yield();
        // make sure the fake row was not actually created
        response = RowResourceBase.client.get(path, MIMETYPE_XML);
        Assert.assertEquals(404, response.getCode());
        // check that all of the values were created
        StringBuilder query = new StringBuilder();
        query.append('/');
        query.append(RowResourceBase.TABLE);
        query.append('/');
        query.append("testrow*");
        query.append('/');
        query.append(RowResourceBase.COLUMN_1);
        response = RowResourceBase.client.get(query.toString(), MIMETYPE_XML);
        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals(MIMETYPE_XML, response.getHeader("content-type"));
        CellSetModel cellSet = ((CellSetModel) (RowResourceBase.xmlUnmarshaller.unmarshal(new ByteArrayInputStream(response.getBody()))));
        List<RowModel> rows = cellSet.getRows();
        Assert.assertTrue(((rows.size()) == 2));
        for (RowModel row : rows) {
            Assert.assertTrue(((row.getCells().size()) == 1));
            Assert.assertEquals(RowResourceBase.COLUMN_1, Bytes.toString(row.getCells().get(0).getColumn()));
        }
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_2);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testAppendXML() throws IOException, JAXBException {
        Response response = RowResourceBase.getValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // append cell
        response = RowResourceBase.appendValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        response = RowResourceBase.appendValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, ((RowResourceBase.VALUE_1) + (RowResourceBase.VALUE_2)));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testAppendPB() throws IOException, JAXBException {
        Response response = RowResourceBase.getValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // append cell
        response = RowResourceBase.appendValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        response = RowResourceBase.appendValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, ((RowResourceBase.VALUE_1) + (RowResourceBase.VALUE_2)));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testAppendJSON() throws IOException, JAXBException {
        Response response = RowResourceBase.getValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // append cell
        response = RowResourceBase.appendValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.putValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        response = RowResourceBase.appendValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.putValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, ((RowResourceBase.VALUE_1) + (RowResourceBase.VALUE_2)));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testIncrementXML() throws IOException, JAXBException {
        Response response = RowResourceBase.getValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // append single cell
        response = RowResourceBase.incrementValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_5);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkIncrementValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, Long.parseLong(RowResourceBase.VALUE_5));
        response = RowResourceBase.incrementValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_6);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkIncrementValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, ((Long.parseLong(RowResourceBase.VALUE_5)) + (Long.parseLong(RowResourceBase.VALUE_6))));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testIncrementPB() throws IOException, JAXBException {
        Response response = RowResourceBase.getValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // append cell
        response = RowResourceBase.incrementValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_5);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkIncrementValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, Long.parseLong(RowResourceBase.VALUE_5));
        response = RowResourceBase.incrementValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_6);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkIncrementValuePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, ((Long.parseLong(RowResourceBase.VALUE_5)) + (Long.parseLong(RowResourceBase.VALUE_6))));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testIncrementJSON() throws IOException, JAXBException {
        Response response = RowResourceBase.getValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // append cell
        response = RowResourceBase.incrementValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_5);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkIncrementValueJSON(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, Long.parseLong(RowResourceBase.VALUE_5));
        response = RowResourceBase.incrementValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_6);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkIncrementValueJSON(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, ((Long.parseLong(RowResourceBase.VALUE_5)) + (Long.parseLong(RowResourceBase.VALUE_6))));
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
    }
}

