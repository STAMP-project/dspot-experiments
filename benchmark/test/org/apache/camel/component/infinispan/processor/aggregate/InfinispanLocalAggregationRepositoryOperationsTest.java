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
package org.apache.camel.component.infinispan.processor.aggregate;


import java.util.Set;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unite test for {@link InfinispanLocalAggregationRepository}
 */
public class InfinispanLocalAggregationRepositoryOperationsTest {
    private static InfinispanLocalAggregationRepository aggregationRepository;

    private CamelContext camelContext = new DefaultCamelContext();

    @Test
    public void testAdd() {
        // Given
        String key = "Add";
        Assert.assertFalse(exists(key));
        Exchange exchange = new org.apache.camel.support.DefaultExchange(camelContext);
        // When
        InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.add(camelContext, key, exchange);
        // Then
        Assert.assertTrue(exists(key));
    }

    @Test
    public void testGetExists() {
        // Given
        String key = "Get_Exists";
        Exchange exchange = new org.apache.camel.support.DefaultExchange(camelContext);
        InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.add(camelContext, key, exchange);
        Assert.assertTrue(exists(key));
        // When
        Exchange exchange2 = InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.get(camelContext, key);
        // Then
        Assert.assertNotNull(exchange2);
        Assert.assertEquals(exchange.getExchangeId(), exchange2.getExchangeId());
    }

    @Test
    public void testGetNotExists() {
        // Given
        String key = "Get_NotExists";
        Assert.assertFalse(exists(key));
        // When
        Exchange exchange2 = InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.get(camelContext, key);
        // Then
        Assert.assertNull(exchange2);
    }

    @Test
    public void testRemoveExists() {
        // Given
        String key = "Remove_Exists";
        Exchange exchange = new org.apache.camel.support.DefaultExchange(camelContext);
        InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.add(camelContext, key, exchange);
        Assert.assertTrue(exists(key));
        // When
        InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.remove(camelContext, key, exchange);
        // Then
        Assert.assertFalse(exists(key));
    }

    @Test
    public void testRemoveNotExists() {
        // Given
        String key = "RemoveNotExists";
        Exchange exchange = new org.apache.camel.support.DefaultExchange(camelContext);
        Assert.assertFalse(exists(key));
        // When
        InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.remove(camelContext, key, exchange);
        // Then
        Assert.assertFalse(exists(key));
    }

    @Test
    public void testGetKeys() {
        // Given
        String[] keys = new String[]{ "GetKeys1", "GetKeys2" };
        addExchanges(keys);
        // When
        Set<String> keySet = InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.getKeys();
        // Then
        for (String key : keys) {
            Assert.assertTrue(keySet.contains(key));
        }
    }

    @Test
    public void testConfirmExist() {
        // Given
        for (int i = 1; i < 4; i++) {
            String key = "Confirm_" + i;
            Exchange exchange = new org.apache.camel.support.DefaultExchange(camelContext);
            exchange.setExchangeId(("Exchange_" + i));
            InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.add(camelContext, key, exchange);
            Assert.assertTrue(exists(key));
        }
        // When
        InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.confirm(camelContext, "Confirm_2");
        // Then
        Assert.assertTrue(exists("Confirm_1"));
        Assert.assertFalse(exists("Confirm_2"));
        Assert.assertTrue(exists("Confirm_3"));
    }

    @Test
    public void testConfirmNotExist() {
        // Given
        String[] keys = new String[3];
        for (int i = 1; i < 4; i++) {
            keys[(i - 1)] = "Confirm" + i;
        }
        addExchanges(keys);
        for (String key : keys) {
            Assert.assertTrue(exists(key));
        }
        // When
        InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.confirm(camelContext, "Exchange-Confirm5");
        // Then
        for (String key : keys) {
            Assert.assertTrue(exists(key));
        }
    }

    @Test
    public void testScan() {
        // Given
        String[] keys = new String[]{ "Scan1", "Scan2" };
        addExchanges(keys);
        // When
        Set<String> exchangeIdSet = InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.scan(camelContext);
        // Then
        for (String key : keys) {
            Assert.assertTrue(exchangeIdSet.contains(key));
        }
    }

    @Test
    public void testRecover() {
        // Given
        String[] keys = new String[]{ "Recover1", "Recover2" };
        addExchanges(keys);
        // When
        Exchange exchange2 = InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.recover(camelContext, "Recover2");
        Exchange exchange3 = InfinispanLocalAggregationRepositoryOperationsTest.aggregationRepository.recover(camelContext, "Recover3");
        // Then
        Assert.assertNotNull(exchange2);
        Assert.assertNull(exchange3);
    }
}

