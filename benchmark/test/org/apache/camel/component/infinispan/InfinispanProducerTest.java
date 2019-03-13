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
package org.apache.camel.component.infinispan;


import InfinispanConstants.DEFAULT_VALUE;
import InfinispanConstants.KEY;
import InfinispanConstants.LIFESPAN_TIME;
import InfinispanConstants.LIFESPAN_TIME_UNIT;
import InfinispanConstants.MAP;
import InfinispanConstants.MAX_IDLE_TIME;
import InfinispanConstants.MAX_IDLE_TIME_UNIT;
import InfinispanConstants.OLD_VALUE;
import InfinispanConstants.OPERATION;
import InfinispanConstants.VALUE;
import InfinispanOperation.CLEAR;
import InfinispanOperation.CLEARASYNC;
import InfinispanOperation.COMPUTE;
import InfinispanOperation.CONTAINSKEY;
import InfinispanOperation.CONTAINSVALUE;
import InfinispanOperation.GET;
import InfinispanOperation.PUT;
import InfinispanOperation.PUTALL;
import InfinispanOperation.PUTIFABSENT;
import InfinispanOperation.REMOVE;
import InfinispanOperation.REMOVEASYNC;
import InfinispanOperation.REPLACE;
import InfinispanOperation.SIZE;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.infinispan.util.Condition;
import org.apache.camel.component.infinispan.util.Wait;
import org.infinispan.stats.Stats;
import org.junit.Test;


public class InfinispanProducerTest extends InfinispanTestSupport {
    private static final String COMMAND_VALUE = "commandValue";

    private static final String COMMAND_KEY = "commandKey1";

    private static final long LIFESPAN_TIME = 100;

    private static final long LIFESPAN_FOR_MAX_IDLE = -1;

    private static final long MAX_IDLE_TIME = 200;

    @BindToRegistry("mappingFunction")
    BiFunction<String, String, String> comp = ( k, v) -> v + "replay";

    @Test
    public void keyAndValueArePublishedWithDefaultOperation() throws Exception {
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
            }
        });
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertEquals(InfinispanTestSupport.VALUE_ONE, value.toString());
    }

    @Test
    public void cacheSizeTest() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        currentCache().put(InfinispanTestSupport.KEY_TWO, InfinispanTestSupport.VALUE_TWO);
        Exchange exchange = template.request("direct:size", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, SIZE);
            }
        });
        Integer cacheSize = exchange.getIn().getBody(Integer.class);
        assertEquals(cacheSize, new Integer(2));
    }

    @Test
    public void publishKeyAndValueByExplicitlySpecifyingTheOperation() throws Exception {
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(OPERATION, PUT);
            }
        });
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertEquals(InfinispanTestSupport.VALUE_ONE, value.toString());
    }

    @Test
    public void publishKeyAndValueAsync() throws Exception {
        final Exchange exchange = template.send("direct:putasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
            }
        });
        Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                CompletableFuture<Object> resultPutAsync = exchange.getIn().getBody(CompletableFuture.class);
                Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
                return (resultPutAsync.isDone()) && (value.toString().equals(InfinispanTestSupport.VALUE_ONE));
            }
        }, 5000);
    }

    @Test
    public void publishKeyAndValueAsyncWithLifespan() throws Exception {
        final Exchange exchange = template.send("direct:putasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_TIME));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                CompletableFuture<Object> resultPutAsync = exchange.getIn().getBody(CompletableFuture.class);
                Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
                return (resultPutAsync.isDone()) && (value.equals(InfinispanTestSupport.VALUE_ONE));
            }
        }, 1000);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void publishKeyAndValueAsyncWithLifespanAndMaxIdle() throws Exception {
        final Exchange exchange = template.send("direct:putasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_FOR_MAX_IDLE));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(InfinispanConstants.MAX_IDLE_TIME, new Long(InfinispanProducerTest.MAX_IDLE_TIME));
                exchange.getIn().setHeader(MAX_IDLE_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                CompletableFuture<Object> resultPutAsync = exchange.getIn().getBody(CompletableFuture.class);
                return (resultPutAsync.isDone()) && (currentCache().get(InfinispanTestSupport.KEY_ONE).toString().equals(InfinispanTestSupport.VALUE_ONE));
            }
        }, 1000);
        Thread.sleep(300);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void publishMapNormal() throws Exception {
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Map<String, String> map = new HashMap<>();
                map.put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
                map.put(InfinispanTestSupport.KEY_TWO, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(MAP, map);
                exchange.getIn().setHeader(OPERATION, PUTALL);
            }
        });
        assertEquals(2, currentCache().size());
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertEquals(InfinispanTestSupport.VALUE_ONE, value.toString());
        value = currentCache().get(InfinispanTestSupport.KEY_TWO);
        assertEquals(InfinispanTestSupport.VALUE_TWO, value.toString());
    }

    @Test
    public void publishMapWithLifespan() throws Exception {
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Map<String, String> map = new HashMap<>();
                map.put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
                map.put(InfinispanTestSupport.KEY_TWO, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(MAP, map);
                exchange.getIn().setHeader(OPERATION, PUTALL);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_TIME));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        assertEquals(2, currentCache().size());
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertEquals(InfinispanTestSupport.VALUE_ONE, value.toString());
        value = currentCache().get(InfinispanTestSupport.KEY_TWO);
        assertEquals(InfinispanTestSupport.VALUE_TWO, value.toString());
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void publishMapWithLifespanAndMaxIdleTime() throws Exception {
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Map<String, String> map = new HashMap<>();
                map.put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
                map.put(InfinispanTestSupport.KEY_TWO, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(MAP, map);
                exchange.getIn().setHeader(OPERATION, PUTALL);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_FOR_MAX_IDLE));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(InfinispanConstants.MAX_IDLE_TIME, new Long(InfinispanProducerTest.MAX_IDLE_TIME));
                exchange.getIn().setHeader(MAX_IDLE_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        assertEquals(2, currentCache().size());
        Thread.sleep(300);
        waitForNullValue(InfinispanTestSupport.KEY_TWO);
    }

    @Test
    public void publishMapNormalAsync() throws Exception {
        template.send("direct:putallasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Map<String, String> map = new HashMap<>();
                map.put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
                map.put(InfinispanTestSupport.KEY_TWO, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(MAP, map);
            }
        });
        Thread.sleep(100);
        assertEquals(2, currentCache().size());
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertEquals(InfinispanTestSupport.VALUE_ONE, value.toString());
        value = currentCache().get(InfinispanTestSupport.KEY_TWO);
        assertEquals(InfinispanTestSupport.VALUE_TWO, value.toString());
    }

    @Test
    public void publishMapWithLifespanAsync() throws Exception {
        template.send("direct:putallasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Map<String, String> map = new HashMap<>();
                map.put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
                map.put(InfinispanTestSupport.KEY_TWO, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(MAP, map);
                exchange.getIn().setHeader(OPERATION, PUTALL);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_TIME));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                Object valueOne = currentCache().get(InfinispanTestSupport.KEY_ONE);
                Object valueTwo = currentCache().get(InfinispanTestSupport.KEY_TWO);
                return ((valueOne.equals(InfinispanTestSupport.VALUE_ONE)) && (valueTwo.equals(InfinispanTestSupport.VALUE_TWO))) && ((currentCache().size()) == 2);
            }
        }, 100);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void publishMapWithLifespanAndMaxIdleTimeAsync() throws Exception {
        template.send("direct:putallasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Map<String, String> map = new HashMap<>();
                map.put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
                map.put(InfinispanTestSupport.KEY_TWO, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(MAP, map);
                exchange.getIn().setHeader(OPERATION, PUTALL);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_FOR_MAX_IDLE));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(InfinispanConstants.MAX_IDLE_TIME, new Long(InfinispanProducerTest.MAX_IDLE_TIME));
                exchange.getIn().setHeader(MAX_IDLE_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                return (currentCache().size()) == 2;
            }
        }, 100);
        Thread.sleep(300);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
        waitForNullValue(InfinispanTestSupport.KEY_TWO);
    }

    @Test
    public void putIfAbsentAlreadyExists() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        template.send("direct:putifabsent", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OPERATION, PUTIFABSENT);
            }
        });
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertEquals(InfinispanTestSupport.VALUE_ONE, value.toString());
        assertEquals(1, currentCache().size());
    }

    @Test
    public void putIfAbsentNotExists() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        template.send("direct:putifabsent", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_TWO);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OPERATION, PUTIFABSENT);
            }
        });
        Object value = currentCache().get(InfinispanTestSupport.KEY_TWO);
        assertEquals(InfinispanTestSupport.VALUE_TWO, value.toString());
        assertEquals(2, currentCache().size());
    }

    @Test
    public void putIfAbsentKeyAndValueAsync() throws Exception {
        final Exchange exchange = template.send("direct:putifabsentasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
            }
        });
        Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                CompletableFuture<Object> resultPutAsync = exchange.getIn().getBody(CompletableFuture.class);
                return (resultPutAsync.isDone()) && (currentCache().get(InfinispanTestSupport.KEY_ONE).equals(InfinispanTestSupport.VALUE_ONE));
            }
        }, 2000);
    }

    @Test
    public void putIfAbsentKeyAndValueAsyncWithLifespan() throws Exception {
        final Exchange exchange = template.send("direct:putifabsentasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_TIME));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                CompletableFuture<Object> resultPutAsync = exchange.getIn().getBody(CompletableFuture.class);
                return (resultPutAsync.isDone()) && (currentCache().get(InfinispanTestSupport.KEY_ONE).equals(InfinispanTestSupport.VALUE_ONE));
            }
        }, 100);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void putIfAbsentKeyAndValueAsyncWithLifespanAndMaxIdle() throws Exception {
        final Exchange exchange = template.send("direct:putifabsentasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_FOR_MAX_IDLE));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(InfinispanConstants.MAX_IDLE_TIME, new Long(InfinispanProducerTest.MAX_IDLE_TIME));
                exchange.getIn().setHeader(MAX_IDLE_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                CompletableFuture<Object> resultPutAsync = exchange.getIn().getBody(CompletableFuture.class);
                return (resultPutAsync.isDone()) && (currentCache().get(InfinispanTestSupport.KEY_ONE).equals(InfinispanTestSupport.VALUE_ONE));
            }
        }, 500);
        Thread.sleep(300);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void notContainsKeyTest() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:containskey", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_TWO);
                exchange.getIn().setHeader(OPERATION, CONTAINSKEY);
            }
        });
        Boolean cacheContainsKey = exchange.getIn().getBody(Boolean.class);
        assertFalse(cacheContainsKey);
    }

    @Test
    public void containsKeyTest() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:containskey", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(OPERATION, CONTAINSKEY);
            }
        });
        Boolean cacheContainsKey = exchange.getIn().getBody(Boolean.class);
        assertTrue(cacheContainsKey);
    }

    @Test
    public void notContainsValueTest() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:containsvalue", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OPERATION, CONTAINSVALUE);
            }
        });
        Boolean cacheContainsValue = exchange.getIn().getBody(Boolean.class);
        assertFalse(cacheContainsValue);
    }

    @Test
    public void containsValueTest() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:containsvalue", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(OPERATION, CONTAINSVALUE);
            }
        });
        Boolean cacheContainsValue = exchange.getIn().getBody(Boolean.class);
        assertTrue(cacheContainsValue);
    }

    @Test
    public void publishKeyAndValueWithLifespan() throws Exception {
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_TIME));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(OPERATION, PUT);
            }
        });
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertEquals(InfinispanTestSupport.VALUE_ONE, value.toString());
        Exchange exchange;
        exchange = template.send("direct:get", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
            }
        });
        String resultGet = exchange.getIn().getBody(String.class);
        assertEquals(InfinispanTestSupport.VALUE_ONE, resultGet);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void getOrDefault() throws Exception {
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(OPERATION, PUT);
            }
        });
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertEquals(InfinispanTestSupport.VALUE_ONE, value.toString());
        Exchange exchange;
        exchange = template.send("direct:getOrDefault", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(DEFAULT_VALUE, "defaultTest");
            }
        });
        String resultGet = exchange.getIn().getBody(String.class);
        assertEquals(InfinispanTestSupport.VALUE_ONE, resultGet);
        exchange = template.send("direct:getOrDefault", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_TWO);
                exchange.getIn().setHeader(DEFAULT_VALUE, "defaultTest");
            }
        });
        resultGet = exchange.getIn().getBody(String.class);
        assertEquals("defaultTest", resultGet);
    }

    @Test
    public void putOperationReturnsThePreviousValue() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, "existing value");
        Exchange exchange = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(OPERATION, PUT);
            }
        });
        String result = exchange.getIn().getBody(String.class);
        assertEquals("existing value", result);
    }

    @Test
    public void computeOperation() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, "existing value");
        Exchange exchange = template.request("direct:compute", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(OPERATION, COMPUTE);
            }
        });
        String result = exchange.getIn().getBody(String.class);
        assertEquals("existing valuereplay", result);
    }

    @Test
    public void retrievesAValueByKey() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(OPERATION, GET);
            }
        });
        assertEquals(InfinispanTestSupport.VALUE_ONE, exchange.getIn().getBody(String.class));
    }

    @Test
    public void replaceAValueByKey() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replace", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OPERATION, REPLACE);
            }
        });
        assertEquals(InfinispanTestSupport.VALUE_ONE, exchange.getIn().getBody(String.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
    }

    @Test
    public void replaceAValueByKeyWithLifespan() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replace", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_TIME));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(OPERATION, REPLACE);
            }
        });
        assertEquals(InfinispanTestSupport.VALUE_ONE, exchange.getIn().getBody(String.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void replaceAValueByKeyWithLifespanAndMaxIdleTime() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replace", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_FOR_MAX_IDLE));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(InfinispanConstants.MAX_IDLE_TIME, new Long(InfinispanProducerTest.MAX_IDLE_TIME));
                exchange.getIn().setHeader(MAX_IDLE_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(OPERATION, REPLACE);
            }
        });
        assertEquals(InfinispanTestSupport.VALUE_ONE, exchange.getIn().getBody(String.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
        Thread.sleep(300);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void replaceAValueByKeyWithOldValue() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replace", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OLD_VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(OPERATION, REPLACE);
            }
        });
        assertTrue(exchange.getIn().getBody(Boolean.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
    }

    @Test
    public void replaceAValueByKeyWithLifespanWithOldValue() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replace", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OLD_VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_TIME));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(OPERATION, REPLACE);
            }
        });
        assertTrue(exchange.getIn().getBody(Boolean.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void replaceAValueByKeyWithLifespanAndMaxIdleTimeWithOldValue() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replace", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OLD_VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_FOR_MAX_IDLE));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(InfinispanConstants.MAX_IDLE_TIME, new Long(InfinispanProducerTest.MAX_IDLE_TIME));
                exchange.getIn().setHeader(MAX_IDLE_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(OPERATION, REPLACE);
            }
        });
        assertTrue(exchange.getIn().getBody(Boolean.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
        Thread.sleep(300);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void replaceAValueByKeyAsync() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replaceasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
            }
        });
        assertEquals(InfinispanTestSupport.VALUE_ONE, exchange.getIn().getBody(String.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
    }

    @Test
    public void replaceAValueByKeyWithLifespanAsync() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replaceasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_TIME));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        assertEquals(exchange.getIn().getBody(String.class), InfinispanTestSupport.VALUE_ONE);
        assertEquals(currentCache().get(InfinispanTestSupport.KEY_ONE), InfinispanTestSupport.VALUE_TWO);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void replaceAValueByKeyWithLifespanAndMaxIdleTimeAsync() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replaceasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_FOR_MAX_IDLE));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(InfinispanConstants.MAX_IDLE_TIME, new Long(InfinispanProducerTest.MAX_IDLE_TIME));
                exchange.getIn().setHeader(MAX_IDLE_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        assertEquals(InfinispanTestSupport.VALUE_ONE, exchange.getIn().getBody(String.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
        Thread.sleep(300);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void replaceAValueByKeyAsyncWithOldValue() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replaceasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(OLD_VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
            }
        });
        assertTrue(exchange.getIn().getBody(Boolean.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
    }

    @Test
    public void replaceAValueByKeyWithLifespanAsyncWithOldValue() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replaceasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OLD_VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_TIME));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        assertTrue(exchange.getIn().getBody(Boolean.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void replaceAValueByKeyWithLifespanAndMaxIdleTimeAsyncWithOldValue() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:replaceasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OLD_VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.LIFESPAN_TIME, new Long(InfinispanProducerTest.LIFESPAN_FOR_MAX_IDLE));
                exchange.getIn().setHeader(LIFESPAN_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
                exchange.getIn().setHeader(InfinispanConstants.MAX_IDLE_TIME, new Long(InfinispanProducerTest.MAX_IDLE_TIME));
                exchange.getIn().setHeader(MAX_IDLE_TIME_UNIT, TimeUnit.MILLISECONDS.toString());
            }
        });
        assertTrue(exchange.getIn().getBody(Boolean.class));
        assertEquals(InfinispanTestSupport.VALUE_TWO, currentCache().get(InfinispanTestSupport.KEY_ONE));
        Thread.sleep(300);
        waitForNullValue(InfinispanTestSupport.KEY_ONE);
    }

    @Test
    public void deletesExistingValueByKey() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(OPERATION, REMOVE);
            }
        });
        assertEquals(InfinispanTestSupport.VALUE_ONE, exchange.getIn().getBody(String.class));
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertNull(value);
    }

    @Test
    public void deletesExistingValueByKeyAsync() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:removeasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(OPERATION, REMOVEASYNC);
            }
        });
        Thread.sleep(100);
        CompletableFuture<Object> fut = exchange.getIn().getBody(CompletableFuture.class);
        assertTrue(fut.isDone());
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertNull(value);
    }

    @Test
    public void deletesExistingValueByKeyWithValue() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(OPERATION, REMOVE);
            }
        });
        assertTrue(exchange.getIn().getBody(Boolean.class));
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertNull(value);
    }

    @Test
    public void deletesExistingValueByKeyAsyncWithValue() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        Exchange exchange = template.request("direct:removeasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(OPERATION, REMOVEASYNC);
            }
        });
        Thread.sleep(100);
        CompletableFuture<Object> fut = exchange.getIn().getBody(CompletableFuture.class);
        assertTrue(fut.isDone());
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertNull(value);
    }

    @Test
    public void clearsAllValues() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        assertFalse(currentCache().isEmpty());
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, CLEAR);
            }
        });
        assertTrue(currentCache().isEmpty());
    }

    @Test
    public void testUriCommandOption() throws Exception {
        template.send("direct:put", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanProducerTest.COMMAND_KEY);
                exchange.getIn().setHeader(VALUE, InfinispanProducerTest.COMMAND_VALUE);
            }
        });
        String result = ((String) (currentCache().get(InfinispanProducerTest.COMMAND_KEY)));
        assertEquals(InfinispanProducerTest.COMMAND_VALUE, result);
        Exchange exchange;
        exchange = template.send("direct:get", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanProducerTest.COMMAND_KEY);
            }
        });
        String resultGet = exchange.getIn().getBody(String.class);
        assertEquals(InfinispanProducerTest.COMMAND_VALUE, resultGet);
        exchange = template.send("direct:remove", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanProducerTest.COMMAND_KEY);
            }
        });
        String resultRemove = exchange.getIn().getBody(String.class);
        assertEquals(InfinispanProducerTest.COMMAND_VALUE, resultRemove);
        assertNull(currentCache().get(InfinispanProducerTest.COMMAND_KEY));
        assertTrue(currentCache().isEmpty());
        currentCache().put(InfinispanProducerTest.COMMAND_KEY, InfinispanProducerTest.COMMAND_VALUE);
        currentCache().put("keyTest", "valueTest");
        template.send("direct:clear", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertTrue(currentCache().isEmpty());
    }

    @Test
    public void testDeprecatedUriOption() throws Exception {
        template.send("direct:put-deprecated-option", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanProducerTest.COMMAND_KEY);
                exchange.getIn().setHeader(VALUE, InfinispanProducerTest.COMMAND_VALUE);
            }
        });
        String result = ((String) (currentCache().get(InfinispanProducerTest.COMMAND_KEY)));
        assertEquals(InfinispanProducerTest.COMMAND_VALUE, result);
        assertEquals(InfinispanProducerTest.COMMAND_VALUE, currentCache().get(InfinispanProducerTest.COMMAND_KEY));
    }

    @Test
    public void testDeprecatedUriCommand() throws Exception {
        template.send("direct:put-deprecated-command", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanProducerTest.COMMAND_KEY);
                exchange.getIn().setHeader(VALUE, InfinispanProducerTest.COMMAND_VALUE);
            }
        });
        String result = ((String) (currentCache().get(InfinispanProducerTest.COMMAND_KEY)));
        assertEquals(InfinispanProducerTest.COMMAND_VALUE, result);
        assertEquals(InfinispanProducerTest.COMMAND_VALUE, currentCache().get(InfinispanProducerTest.COMMAND_KEY));
    }

    @Test
    public void clearAsyncTest() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        currentCache().put(InfinispanTestSupport.KEY_TWO, InfinispanTestSupport.VALUE_TWO);
        Exchange exchange = template.request("direct:clearasync", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, CLEARASYNC);
            }
        });
        Thread.sleep(100);
        CompletableFuture<Object> fut = exchange.getIn().getBody(CompletableFuture.class);
        assertTrue(fut.isDone());
        assertTrue(currentCache().isEmpty());
    }

    @Test
    public void statsOperation() throws Exception {
        getAdvancedCache().getStats().setStatisticsEnabled(true);
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_ONE);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_ONE);
                exchange.getIn().setHeader(OPERATION, PUT);
            }
        });
        Object value = currentCache().get(InfinispanTestSupport.KEY_ONE);
        assertEquals(InfinispanTestSupport.VALUE_ONE, value.toString());
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, InfinispanTestSupport.KEY_TWO);
                exchange.getIn().setHeader(VALUE, InfinispanTestSupport.VALUE_TWO);
                exchange.getIn().setHeader(OPERATION, PUT);
            }
        });
        value = currentCache().get(InfinispanTestSupport.KEY_TWO);
        assertEquals(InfinispanTestSupport.VALUE_TWO, value.toString());
        Exchange exchange;
        exchange = template.send("direct:stats", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
            }
        });
        Stats resultStats = exchange.getIn().getBody(Stats.class);
        assertEquals(2L, resultStats.getTotalNumberOfEntries());
    }
}

