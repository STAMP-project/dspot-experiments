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
package org.apache.nifi.hbase;


import HBase_1_1_2_ClientService.HADOOP_CONF_FILES;
import HBase_1_1_2_ClientService.ZOOKEEPER_CLIENT_PORT;
import HBase_1_1_2_ClientService.ZOOKEEPER_QUORUM;
import HBase_1_1_2_ClientService.ZOOKEEPER_ZNODE_PARENT;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.hbase.put.PutColumn;
import org.apache.nifi.hbase.put.PutFlowFile;
import org.apache.nifi.hbase.scan.ResultCell;
import org.apache.nifi.hbase.scan.ResultHandler;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class TestHBase_1_1_2_ClientService {
    static final String COL_FAM = "nifi1";

    private KerberosProperties kerberosPropsWithFile;

    private KerberosProperties kerberosPropsWithoutFile;

    @Test
    public void testCustomValidate() throws IOException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final String tableName = "nifi";
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // no conf file or zk properties so should be invalid
        MockHBaseClientService service = new MockHBaseClientService(table, TestHBase_1_1_2_ClientService.COL_FAM, kerberosPropsWithFile);
        runner.addControllerService("hbaseClientService", service);
        runner.assertNotValid(service);
        runner.removeControllerService(service);
        runner.setVariable("hadoop-conf-files", "src/test/resources/hbase-site.xml");
        runner.setVariable("zk-quorum", "localhost");
        runner.setVariable("zk-client-port", "2181");
        runner.setVariable("zk-znode", "/hbase");
        // conf file with no zk properties should be valid
        service = new MockHBaseClientService(table, TestHBase_1_1_2_ClientService.COL_FAM, kerberosPropsWithFile);
        runner.addControllerService("hbaseClientService", service);
        runner.setProperty(service, HADOOP_CONF_FILES, "${hadoop-conf-files}");
        runner.enableControllerService(service);
        runner.assertValid(service);
        runner.removeControllerService(service);
        // only quorum and no conf file should be invalid
        service = new MockHBaseClientService(table, TestHBase_1_1_2_ClientService.COL_FAM, kerberosPropsWithFile);
        runner.addControllerService("hbaseClientService", service);
        runner.setProperty(service, ZOOKEEPER_QUORUM, "${zk-quorum}");
        runner.assertNotValid(service);
        runner.removeControllerService(service);
        // quorum and port, no znode, no conf file, should be invalid
        service = new MockHBaseClientService(table, TestHBase_1_1_2_ClientService.COL_FAM, kerberosPropsWithFile);
        runner.addControllerService("hbaseClientService", service);
        runner.setProperty(service, ZOOKEEPER_QUORUM, "${zk-quorum}");
        runner.setProperty(service, ZOOKEEPER_CLIENT_PORT, "${zk-client-port}");
        runner.assertNotValid(service);
        runner.removeControllerService(service);
        // quorum, port, and znode, no conf file, should be valid
        service = new MockHBaseClientService(table, TestHBase_1_1_2_ClientService.COL_FAM, kerberosPropsWithFile);
        runner.addControllerService("hbaseClientService", service);
        runner.setProperty(service, ZOOKEEPER_QUORUM, "${zk-quorum}");
        runner.setProperty(service, ZOOKEEPER_CLIENT_PORT, "${zk-client-port}");
        runner.setProperty(service, ZOOKEEPER_ZNODE_PARENT, "${zk-znode}");
        runner.enableControllerService(service);
        runner.assertValid(service);
        runner.removeControllerService(service);
        // quorum and port with conf file should be valid
        service = new MockHBaseClientService(table, TestHBase_1_1_2_ClientService.COL_FAM, kerberosPropsWithFile);
        runner.addControllerService("hbaseClientService", service);
        runner.setProperty(service, HADOOP_CONF_FILES, "src/test/resources/hbase-site.xml");
        runner.setProperty(service, ZOOKEEPER_QUORUM, "localhost");
        runner.setProperty(service, ZOOKEEPER_CLIENT_PORT, "2181");
        runner.enableControllerService(service);
        runner.assertValid(service);
        runner.removeControllerService(service);
        // Kerberos - principal with non-set keytab and only hbase-site-security - valid because we need core-site-security to turn on security
        service = new MockHBaseClientService(table, TestHBase_1_1_2_ClientService.COL_FAM, kerberosPropsWithFile);
        runner.addControllerService("hbaseClientService", service);
        runner.setProperty(service, HADOOP_CONF_FILES, "src/test/resources/hbase-site-security.xml");
        runner.setProperty(service, kerberosPropsWithFile.getKerberosPrincipal(), "test@REALM");
        runner.enableControllerService(service);
        runner.assertValid(service);
        // Kerberos - principal with non-set keytab and both config files
        runner.disableControllerService(service);
        runner.setProperty(service, HADOOP_CONF_FILES, "src/test/resources/hbase-site-security.xml, src/test/resources/core-site-security.xml");
        runner.assertNotValid(service);
        // Kerberos - add valid options
        runner.setProperty(service, kerberosPropsWithFile.getKerberosKeytab(), "src/test/resources/fake.keytab");
        runner.setProperty(service, kerberosPropsWithFile.getKerberosPrincipal(), "test@REALM");
        runner.enableControllerService(service);
        runner.assertValid(service);
        // Kerberos - add invalid non-existent keytab file
        runner.disableControllerService(service);
        runner.setProperty(service, kerberosPropsWithFile.getKerberosKeytab(), "src/test/resources/missing.keytab");
        runner.assertNotValid(service);
        // Kerberos - add invalid principal
        runner.setProperty(service, kerberosPropsWithFile.getKerberosKeytab(), "src/test/resources/fake.keytab");
        runner.setProperty(service, kerberosPropsWithFile.getKerberosPrincipal(), "");
        runner.assertNotValid(service);
        // Kerberos - valid props but the KerberosProperties has a null Kerberos config file so be invalid
        service = new MockHBaseClientService(table, TestHBase_1_1_2_ClientService.COL_FAM, kerberosPropsWithoutFile);
        runner.addControllerService("hbaseClientService", service);
        runner.setProperty(service, HADOOP_CONF_FILES, "src/test/resources/hbase-site-security.xml, src/test/resources/core-site-security.xml");
        runner.setProperty(service, kerberosPropsWithoutFile.getKerberosKeytab(), "src/test/resources/fake.keytab");
        runner.setProperty(service, kerberosPropsWithoutFile.getKerberosPrincipal(), "test@REALM");
        runner.assertNotValid(service);
    }

    @Test
    public void testSinglePut() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row = "row1";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final String content = "content1";
        final Collection<PutColumn> columns = Collections.singletonList(new PutColumn(columnFamily.getBytes(StandardCharsets.UTF_8), columnQualifier.getBytes(StandardCharsets.UTF_8), content.getBytes(StandardCharsets.UTF_8)));
        final PutFlowFile putFlowFile = new PutFlowFile(tableName, row.getBytes(StandardCharsets.UTF_8), columns, null);
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final HBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        // try to put a single cell
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        hBaseClientService.put(tableName, Arrays.asList(putFlowFile));
        // verify only one call to put was made
        ArgumentCaptor<List> capture = ArgumentCaptor.forClass(List.class);
        Mockito.verify(table, Mockito.times(1)).put(capture.capture());
        // verify only one put was in the list of puts
        final List<Put> puts = capture.getValue();
        Assert.assertEquals(1, puts.size());
        verifyPut(row, columnFamily, columnQualifier, content, puts.get(0));
    }

    @Test
    public void testMultiplePutsSameRow() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row = "row1";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final String content1 = "content1";
        final String content2 = "content2";
        final Collection<PutColumn> columns1 = Collections.singletonList(new PutColumn(columnFamily.getBytes(StandardCharsets.UTF_8), columnQualifier.getBytes(StandardCharsets.UTF_8), content1.getBytes(StandardCharsets.UTF_8)));
        final PutFlowFile putFlowFile1 = new PutFlowFile(tableName, row.getBytes(StandardCharsets.UTF_8), columns1, null);
        final Collection<PutColumn> columns2 = Collections.singletonList(new PutColumn(columnFamily.getBytes(StandardCharsets.UTF_8), columnQualifier.getBytes(StandardCharsets.UTF_8), content2.getBytes(StandardCharsets.UTF_8)));
        final PutFlowFile putFlowFile2 = new PutFlowFile(tableName, row.getBytes(StandardCharsets.UTF_8), columns2, null);
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final HBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        // try to put a multiple cells for the same row
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        hBaseClientService.put(tableName, Arrays.asList(putFlowFile1, putFlowFile2));
        // verify put was only called once
        ArgumentCaptor<List> capture = ArgumentCaptor.forClass(List.class);
        Mockito.verify(table, Mockito.times(1)).put(capture.capture());
        // verify there was only one put in the list of puts
        final List<Put> puts = capture.getValue();
        Assert.assertEquals(1, puts.size());
        // verify two cells were added to this one put operation
        final NavigableMap<byte[], List<Cell>> familyCells = puts.get(0).getFamilyCellMap();
        Map.Entry<byte[], List<Cell>> entry = familyCells.firstEntry();
        Assert.assertEquals(2, entry.getValue().size());
    }

    @Test
    public void testMultiplePutsDifferentRow() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row1 = "row1";
        final String row2 = "row2";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final String content1 = "content1";
        final String content2 = "content2";
        final Collection<PutColumn> columns1 = Collections.singletonList(new PutColumn(columnFamily.getBytes(StandardCharsets.UTF_8), columnQualifier.getBytes(StandardCharsets.UTF_8), content1.getBytes(StandardCharsets.UTF_8)));
        final PutFlowFile putFlowFile1 = new PutFlowFile(tableName, row1.getBytes(StandardCharsets.UTF_8), columns1, null);
        final Collection<PutColumn> columns2 = Collections.singletonList(new PutColumn(columnFamily.getBytes(StandardCharsets.UTF_8), columnQualifier.getBytes(StandardCharsets.UTF_8), content2.getBytes(StandardCharsets.UTF_8)));
        final PutFlowFile putFlowFile2 = new PutFlowFile(tableName, row2.getBytes(StandardCharsets.UTF_8), columns2, null);
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final HBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        // try to put a multiple cells with different rows
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        hBaseClientService.put(tableName, Arrays.asList(putFlowFile1, putFlowFile2));
        // verify put was only called once
        ArgumentCaptor<List> capture = ArgumentCaptor.forClass(List.class);
        Mockito.verify(table, Mockito.times(1)).put(capture.capture());
        // verify there were two puts in the list
        final List<Put> puts = capture.getValue();
        Assert.assertEquals(2, puts.size());
    }

    @Test
    public void testScan() throws IOException, InitializationException {
        final String tableName = "nifi";
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final MockHBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        // stage some results in the mock service...
        final long now = System.currentTimeMillis();
        final Map<String, String> cells = new HashMap<>();
        cells.put("greeting", "hello");
        cells.put("name", "nifi");
        service.addResult("row0", cells, (now - 2));
        service.addResult("row1", cells, (now - 1));
        service.addResult("row2", cells, (now - 1));
        service.addResult("row3", cells, now);
        // perform a scan and verify the four rows were returned
        final TestHBase_1_1_2_ClientService.CollectingResultHandler handler = new TestHBase_1_1_2_ClientService.CollectingResultHandler();
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        hBaseClientService.scan(tableName, new ArrayList<org.apache.nifi.hbase.scan.Column>(), null, now, handler);
        Assert.assertEquals(4, handler.results.size());
        // get row0 using the row id and verify it has 2 cells
        final ResultCell[] results = handler.results.get("row0");
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.length);
        verifyResultCell(results[0], TestHBase_1_1_2_ClientService.COL_FAM, "greeting", "hello");
        verifyResultCell(results[1], TestHBase_1_1_2_ClientService.COL_FAM, "name", "nifi");
    }

    @Test
    public void testScanWithValidFilter() throws IOException, InitializationException {
        final String tableName = "nifi";
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final MockHBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        // perform a scan and verify the four rows were returned
        final TestHBase_1_1_2_ClientService.CollectingResultHandler handler = new TestHBase_1_1_2_ClientService.CollectingResultHandler();
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        // make sure we parse the filter expression without throwing an exception
        final String filter = "PrefixFilter ('Row') AND PageFilter (1) AND FirstKeyOnlyFilter ()";
        hBaseClientService.scan(tableName, new ArrayList<org.apache.nifi.hbase.scan.Column>(), filter, System.currentTimeMillis(), handler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScanWithInvalidFilter() throws IOException, InitializationException {
        final String tableName = "nifi";
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final MockHBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        // perform a scan and verify the four rows were returned
        final TestHBase_1_1_2_ClientService.CollectingResultHandler handler = new TestHBase_1_1_2_ClientService.CollectingResultHandler();
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        // this should throw IllegalArgumentException
        final String filter = "this is not a filter";
        hBaseClientService.scan(tableName, new ArrayList<org.apache.nifi.hbase.scan.Column>(), filter, System.currentTimeMillis(), handler);
    }

    // handler that saves results for verification
    private static final class CollectingResultHandler implements ResultHandler {
        Map<String, ResultCell[]> results = new LinkedHashMap<>();

        @Override
        public void handle(byte[] row, ResultCell[] resultCells) {
            final String rowStr = new String(row, StandardCharsets.UTF_8);
            results.put(rowStr, resultCells);
        }
    }
}

