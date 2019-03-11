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


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.nifi.distributed.cache.client.Deserializer;
import org.apache.nifi.distributed.cache.client.DistributedMapCacheClient;
import org.apache.nifi.distributed.cache.client.Serializer;
import org.apache.nifi.distributed.cache.client.exception.DeserializationException;
import org.apache.nifi.distributed.cache.client.exception.SerializationException;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class TestHBase_2_ClientMapCacheService {
    private KerberosProperties kerberosPropsWithFile;

    private KerberosProperties kerberosPropsWithoutFile;

    private Serializer<String> stringSerializer = new TestHBase_2_ClientMapCacheService.StringSerializer();

    private Deserializer<String> stringDeserializer = new TestHBase_2_ClientMapCacheService.StringDeserializer();

    private final String tableName = "nifi";

    private final String columnFamily = "family1";

    private final String columnQualifier = "qualifier1";

    @Test
    public void testPut() throws IOException, InitializationException {
        final String row = "row1";
        final String content = "content1";
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final MockHBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        final DistributedMapCacheClient cacheService = configureHBaseCacheService(runner, hBaseClientService);
        runner.assertValid(cacheService);
        // try to put a single cell
        final DistributedMapCacheClient hBaseCacheService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CACHE_SERVICE).asControllerService(DistributedMapCacheClient.class);
        hBaseCacheService.put(row, content, stringSerializer, stringSerializer);
        // verify only one call to put was made
        ArgumentCaptor<Put> capture = ArgumentCaptor.forClass(Put.class);
        Mockito.verify(table, Mockito.times(1)).put(capture.capture());
        verifyPut(row, columnFamily, columnQualifier, content, capture.getValue());
    }

    @Test
    public void testGet() throws IOException, InitializationException {
        final String row = "row1";
        final String content = "content1";
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final MockHBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        final DistributedMapCacheClient cacheService = configureHBaseCacheService(runner, hBaseClientService);
        runner.assertValid(cacheService);
        final DistributedMapCacheClient hBaseCacheService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CACHE_SERVICE).asControllerService(DistributedMapCacheClient.class);
        hBaseCacheService.put(row, content, stringSerializer, stringSerializer);
        final String result = hBaseCacheService.get(row, stringSerializer, stringDeserializer);
        Assert.assertEquals(content, result);
    }

    @Test
    public void testContainsKey() throws IOException, InitializationException {
        final String row = "row1";
        final String content = "content1";
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final MockHBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        final DistributedMapCacheClient cacheService = configureHBaseCacheService(runner, hBaseClientService);
        runner.assertValid(cacheService);
        final DistributedMapCacheClient hBaseCacheService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CACHE_SERVICE).asControllerService(DistributedMapCacheClient.class);
        Assert.assertFalse(hBaseCacheService.containsKey(row, stringSerializer));
        hBaseCacheService.put(row, content, stringSerializer, stringSerializer);
        Assert.assertTrue(hBaseCacheService.containsKey(row, stringSerializer));
    }

    @Test
    public void testPutIfAbsent() throws IOException, InitializationException {
        final String row = "row1";
        final String content = "content1";
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final MockHBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        final DistributedMapCacheClient cacheService = configureHBaseCacheService(runner, hBaseClientService);
        runner.assertValid(cacheService);
        final DistributedMapCacheClient hBaseCacheService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CACHE_SERVICE).asControllerService(DistributedMapCacheClient.class);
        Assert.assertTrue(hBaseCacheService.putIfAbsent(row, content, stringSerializer, stringSerializer));
        // verify only one call to put was made
        ArgumentCaptor<Put> capture = ArgumentCaptor.forClass(Put.class);
        Mockito.verify(table, Mockito.times(1)).put(capture.capture());
        verifyPut(row, columnFamily, columnQualifier, content, capture.getValue());
        Assert.assertFalse(hBaseCacheService.putIfAbsent(row, content, stringSerializer, stringSerializer));
        Mockito.verify(table, Mockito.times(1)).put(capture.capture());
    }

    @Test
    public void testGetAndPutIfAbsent() throws IOException, InitializationException {
        final String row = "row1";
        final String content = "content1";
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        // Mock an HBase Table so we can verify the put operations later
        final Table table = Mockito.mock(Table.class);
        Mockito.when(table.getName()).thenReturn(TableName.valueOf(tableName));
        // create the controller service and link it to the test processor
        final MockHBaseClientService service = configureHBaseClientService(runner, table);
        runner.assertValid(service);
        final HBaseClientService hBaseClientService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CLIENT_SERVICE).asControllerService(HBaseClientService.class);
        final DistributedMapCacheClient cacheService = configureHBaseCacheService(runner, hBaseClientService);
        runner.assertValid(cacheService);
        final DistributedMapCacheClient hBaseCacheService = runner.getProcessContext().getProperty(TestProcessor.HBASE_CACHE_SERVICE).asControllerService(DistributedMapCacheClient.class);
        Assert.assertNull(hBaseCacheService.getAndPutIfAbsent(row, content, stringSerializer, stringSerializer, stringDeserializer));
        // verify only one call to put was made
        ArgumentCaptor<Put> capture = ArgumentCaptor.forClass(Put.class);
        Mockito.verify(table, Mockito.times(1)).put(capture.capture());
        verifyPut(row, columnFamily, columnQualifier, content, capture.getValue());
        final String result = hBaseCacheService.getAndPutIfAbsent(row, content, stringSerializer, stringSerializer, stringDeserializer);
        Mockito.verify(table, Mockito.times(1)).put(capture.capture());
        Assert.assertEquals(result, content);
    }

    private static class StringSerializer implements Serializer<String> {
        @Override
        public void serialize(final String value, final OutputStream out) throws IOException, SerializationException {
            out.write(value.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static class StringDeserializer implements Deserializer<String> {
        @Override
        public String deserialize(byte[] input) throws IOException, DeserializationException {
            return new String(input);
        }
    }
}

