/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.hbase;


import HBaseAttribute.HBASE_FAMILY;
import HBaseAttribute.HBASE_QUALIFIER;
import HBaseAttribute.HBASE_ROW_ID;
import HBaseAttribute.HBASE_VALUE;
import HBaseConstants.DELETE;
import HBaseConstants.FROM_ROW;
import HBaseConstants.GET;
import HBaseConstants.OPERATION;
import HBaseConstants.PUT;
import HBaseConstants.STOP_ROW;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.util.IOHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.junit.Test;


public class HBaseProducerTest extends CamelHBaseTestSupport {
    @Test
    public void testPut() throws Exception {
        if (CamelHBaseTestSupport.systemReady) {
            Map<String, Object> headers = new HashMap<>();
            headers.put(HBASE_ROW_ID.asHeader(), key[0]);
            headers.put(HBASE_FAMILY.asHeader(), family[0]);
            headers.put(HBASE_QUALIFIER.asHeader(), column[0][0]);
            headers.put(HBASE_VALUE.asHeader(), body[0][0][0]);
            headers.put(OPERATION, PUT);
            template.sendBodyAndHeaders("direct:start", null, headers);
            Configuration configuration = CamelHBaseTestSupport.hbaseUtil.getHBaseAdmin().getConfiguration();
            Connection connection = ConnectionFactory.createConnection(configuration);
            Table table = connection.getTable(TableName.valueOf(CamelHBaseTestSupport.PERSON_TABLE.getBytes()));
            Get get = new Get(key[0].getBytes());
            get.addColumn(family[0].getBytes(), column[0][0].getBytes());
            Result result = table.get(get);
            byte[] resultValue = result.value();
            assertArrayEquals(body[0][0][0].getBytes(), resultValue);
            IOHelper.close(table);
        }
    }

    @Test
    public void testPutAndGet() throws Exception {
        testPut();
        if (CamelHBaseTestSupport.systemReady) {
            Exchange resp = template.request("direct:start", new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(HBASE_ROW_ID.asHeader(), key[0]);
                    exchange.getIn().setHeader(HBASE_FAMILY.asHeader(), family[0]);
                    exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader(), column[0][0]);
                    exchange.getIn().setHeader(OPERATION, GET);
                }
            });
            assertEquals(body[0][0][0], resp.getOut().getHeader(HBASE_VALUE.asHeader()));
        }
    }

    @Test
    public void testPutAndGetWithModel() throws Exception {
        if (CamelHBaseTestSupport.systemReady) {
            Map<String, Object> headers = new HashMap<>();
            headers.put(OPERATION, PUT);
            int index = 1;
            for (int row = 0; row < (key.length); row++) {
                for (int fam = 0; fam < (family.length); fam++) {
                    for (int col = 0; col < (column[fam].length); col++) {
                        headers.put(HBASE_ROW_ID.asHeader(index), key[row]);
                        headers.put(HBASE_FAMILY.asHeader(index), family[fam]);
                        headers.put(HBASE_QUALIFIER.asHeader(index), column[fam][col]);
                        headers.put(HBASE_VALUE.asHeader((index++)), body[row][fam][col]);
                    }
                }
            }
            template.sendBodyAndHeaders("direct:start", null, headers);
            Exchange resp = template.request("direct:start-with-model", new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(HBASE_ROW_ID.asHeader(), key[0]);
                    exchange.getIn().setHeader(OPERATION, GET);
                }
            });
            assertEquals(body[0][0][1], resp.getOut().getHeader(HBASE_VALUE.asHeader()));
            assertEquals(body[0][1][2], resp.getOut().getHeader(HBASE_VALUE.asHeader(2)));
        }
    }

    @Test
    public void testPutMultiRows() throws Exception {
        if (CamelHBaseTestSupport.systemReady) {
            Map<String, Object> headers = new HashMap<>();
            headers.put(OPERATION, PUT);
            for (int row = 0; row < (key.length); row++) {
                headers.put(HBASE_ROW_ID.asHeader((row + 1)), key[row]);
                headers.put(HBASE_FAMILY.asHeader((row + 1)), family[0]);
                headers.put(HBASE_QUALIFIER.asHeader((row + 1)), column[0][0]);
                headers.put(HBASE_VALUE.asHeader((row + 1)), body[row][0][0]);
            }
            template.sendBodyAndHeaders("direct:start", null, headers);
            Configuration configuration = CamelHBaseTestSupport.hbaseUtil.getHBaseAdmin().getConfiguration();
            Connection conn = ConnectionFactory.createConnection(configuration);
            Table bar = conn.getTable(TableName.valueOf(CamelHBaseTestSupport.PERSON_TABLE));
            // Check row 1
            for (int row = 0; row < (key.length); row++) {
                Get get = new Get(key[row].getBytes());
                get.addColumn(family[0].getBytes(), column[0][0].getBytes());
                Result result = bar.get(get);
                byte[] resultValue = result.value();
                assertArrayEquals(body[row][0][0].getBytes(), resultValue);
            }
            IOHelper.close(bar);
        }
    }

    @Test
    public void testPutAndGetMultiRows() throws Exception {
        testPutMultiRows();
        if (CamelHBaseTestSupport.systemReady) {
            Exchange resp = template.request("direct:start", new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(OPERATION, GET);
                    for (int row = 0; row < (key.length); row++) {
                        exchange.getIn().setHeader(HBASE_ROW_ID.asHeader((row + 1)), key[row]);
                        exchange.getIn().setHeader(HBASE_FAMILY.asHeader((row + 1)), family[0]);
                        exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader((row + 1)), column[0][0]);
                    }
                }
            });
            for (int row = 0; row < (key.length); row++) {
                assertEquals(body[row][0][0], resp.getOut().getHeader(HBASE_VALUE.asHeader((row + 1))));
            }
        }
    }

    @Test
    public void testPutMultiColumns() throws Exception {
        if (CamelHBaseTestSupport.systemReady) {
            Map<String, Object> headers = new HashMap<>();
            headers.put(OPERATION, PUT);
            for (int col = 0; col < (column[0].length); col++) {
                headers.put(HBASE_ROW_ID.asHeader((col + 1)), key[0]);
                headers.put(HBASE_FAMILY.asHeader((col + 1)), family[0]);
                headers.put(HBASE_QUALIFIER.asHeader((col + 1)), column[0][col]);
                headers.put(HBASE_VALUE.asHeader((col + 1)), body[0][col][0]);
            }
            template.sendBodyAndHeaders("direct:start", null, headers);
            Configuration configuration = CamelHBaseTestSupport.hbaseUtil.getHBaseAdmin().getConfiguration();
            Connection connection = ConnectionFactory.createConnection(configuration);
            Table bar = connection.getTable(TableName.valueOf(CamelHBaseTestSupport.PERSON_TABLE.getBytes()));
            for (int col = 0; col < (column[0].length); col++) {
                Get get = new Get(key[0].getBytes());
                get.addColumn(family[0].getBytes(), column[0][col].getBytes());
                Result result = bar.get(get);
                byte[] resultValue = result.value();
                assertArrayEquals(body[0][col][0].getBytes(), resultValue);
            }
            IOHelper.close(bar);
        }
    }

    @Test
    public void testPutAndGetMultiColumns() throws Exception {
        testPutMultiColumns();
        if (CamelHBaseTestSupport.systemReady) {
            Exchange resp = template.request("direct:start", new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(OPERATION, GET);
                    for (int col = 0; col < (column[0].length); col++) {
                        exchange.getIn().setHeader(HBASE_ROW_ID.asHeader((col + 1)), key[0]);
                        exchange.getIn().setHeader(HBASE_FAMILY.asHeader((col + 1)), family[0]);
                        exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader((col + 1)), column[0][col]);
                    }
                }
            });
            for (int col = 0; col < (column[0].length); col++) {
                assertEquals(body[0][col][0], resp.getOut().getHeader(HBASE_VALUE.asHeader((col + 1))));
            }
        }
    }

    @Test
    public void testPutAndGetAndDeleteMultiRows() throws Exception {
        testPutMultiRows();
        if (CamelHBaseTestSupport.systemReady) {
            Map<String, Object> headers = new HashMap<>();
            headers.put(OPERATION, DELETE);
            headers.put(HBASE_ROW_ID.asHeader(), key[0]);
            template.sendBodyAndHeaders("direct:start", null, headers);
            Exchange resp = template.request("direct:start", new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(OPERATION, GET);
                    exchange.getIn().setHeader(HBASE_ROW_ID.asHeader(), key[0]);
                    exchange.getIn().setHeader(HBASE_FAMILY.asHeader(), family[0]);
                    exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader(), column[0][0]);
                    exchange.getIn().setHeader(HBASE_ROW_ID.asHeader(2), key[1]);
                    exchange.getIn().setHeader(HBASE_FAMILY.asHeader(2), family[0]);
                    exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader(2), column[0][0]);
                }
            });
            assertEquals(null, resp.getOut().getHeader(HBASE_VALUE.asHeader()));
            assertEquals(body[1][0][0], resp.getOut().getHeader(HBASE_VALUE.asHeader(2)));
        }
    }

    @Test
    public void testPutMultiRowsAndMaxScan() throws Exception {
        testPutMultiRows();
        if (CamelHBaseTestSupport.systemReady) {
            Exchange resp = template.request("direct:maxScan", new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(HBASE_FAMILY.asHeader(), family[0]);
                    exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader(), column[0][0]);
                }
            });
            Object result1 = resp.getOut().getHeader(HBASE_VALUE.asHeader(1));
            Object result2 = resp.getOut().getHeader(HBASE_VALUE.asHeader(2));
            // as we use maxResults=2 we only get 2 results back
            Object result3 = resp.getOut().getHeader(HBASE_VALUE.asHeader(3));
            assertNull("Should only get 2 results back", result3);
            List<?> bodies = Arrays.asList(body[0][0][0], body[1][0][0]);
            assertTrue(((bodies.contains(result1)) && (bodies.contains(result2))));
        }
    }

    @Test
    public void testPutMultiRowsAndScan() throws Exception {
        testPutMultiRows();
        if (CamelHBaseTestSupport.systemReady) {
            Exchange resp = template.request("direct:scan", new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(HBASE_FAMILY.asHeader(), family[0]);
                    exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader(), column[0][0]);
                }
            });
            Object result1 = resp.getOut().getHeader(HBASE_VALUE.asHeader(1));
            Object result2 = resp.getOut().getHeader(HBASE_VALUE.asHeader(2));
            Object result3 = resp.getOut().getHeader(HBASE_VALUE.asHeader(3));
            List<?> bodies = Arrays.asList(body[0][0][0], body[1][0][0], body[2][0][0]);
            assertTrue((((bodies.contains(result1)) && (bodies.contains(result2))) && (bodies.contains(result3))));
        }
    }

    @Test
    public void testPutMultiRowsAndScanWithStop() throws Exception {
        testPutMultiRows();
        if (CamelHBaseTestSupport.systemReady) {
            Exchange resp = template.request("direct:scan", new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(HBASE_FAMILY.asHeader(), family[0]);
                    exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader(), column[0][0]);
                    exchange.getIn().setHeader(FROM_ROW, key[0]);
                    exchange.getIn().setHeader(STOP_ROW, key[1]);
                }
            });
            Object result1 = resp.getOut().getHeader(HBASE_VALUE.asHeader(1));
            Object result2 = resp.getOut().getHeader(HBASE_VALUE.asHeader(2));
            Object result3 = resp.getOut().getHeader(HBASE_VALUE.asHeader(3));
            List<?> bodies = Arrays.asList(body[0][0][0], body[1][0][0], body[2][0][0]);
            assertTrue((((bodies.contains(result1)) && (!(bodies.contains(result2)))) && (!(bodies.contains(result3)))));
        }
    }

    @Test
    public void testPutAndScan() throws Exception {
        if (CamelHBaseTestSupport.systemReady) {
            Map<String, Object> headers = new HashMap<>();
            headers.put(OPERATION, PUT);
            headers.put(HBASE_ROW_ID.asHeader(), "1");
            headers.put(HBASE_FAMILY.asHeader(), "info");
            headers.put(HBASE_QUALIFIER.asHeader(), "id");
            headers.put(HBASE_VALUE.asHeader(), "3");
            template.sendBodyAndHeaders("direct:start", null, headers);
            Configuration configuration = CamelHBaseTestSupport.hbaseUtil.getHBaseAdmin().getConfiguration();
            Connection conn = ConnectionFactory.createConnection(configuration);
            Table bar = conn.getTable(TableName.valueOf(CamelHBaseTestSupport.PERSON_TABLE));
            Get get = new Get("1".getBytes());
            get.addColumn("info".getBytes(), "id".getBytes());
            Result result = bar.get(get);
            assertArrayEquals("3".getBytes(), result.value());
            IOHelper.close(bar);
            Exchange resp = template.request("direct:scan", new Processor() {
                public void process(Exchange exchange) throws Exception {
                    exchange.getIn().setHeader(HBASE_FAMILY.asHeader(), "info");
                    exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader(), "id");
                }
            });
            assertEquals("1", resp.getOut().getHeader(HBASE_ROW_ID.asHeader()));
            assertEquals("info", resp.getOut().getHeader(HBASE_FAMILY.asHeader()));
            assertEquals("id", resp.getOut().getHeader(HBASE_QUALIFIER.asHeader()));
            assertEquals("3", resp.getOut().getHeader(HBASE_VALUE.asHeader()));
        }
    }
}

