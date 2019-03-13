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
package org.apache.camel.processor;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Header;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SagaTest extends ContextTestSupport {
    private SagaTest.OrderManagerService orderManagerService;

    private SagaTest.CreditService creditService;

    @Test
    public void testCreditExhausted() throws Exception {
        // total credit is 100
        buy(20, false, false);
        buy(70, false, false);
        buy(20, false, true);// fail

        buy(5, false, false);
        await().until(() -> orderManagerService.getOrders().size(), Matchers.equalTo(3));
        await().until(() -> creditService.getCredit(), Matchers.equalTo(5));
    }

    @Test
    public void testTotalCompensation() throws Exception {
        // total credit is 100
        for (int i = 0; i < 10; i++) {
            if ((i % 2) == 0) {
                buy(10, false, false);
            } else {
                buy(10, true, true);
            }
        }
        await().until(() -> orderManagerService.getOrders().size(), Matchers.equalTo(5));
        await().until(() -> creditService.getCredit(), Matchers.equalTo(50));
    }

    public static class OrderManagerService {
        private Set<String> orders = new HashSet<>();

        public synchronized void newOrder(String id) {
            orders.add(id);
        }

        public synchronized void cancelOrder(String id) {
            orders.remove(id);
        }

        public synchronized Set<String> getOrders() {
            return new TreeSet<>(orders);
        }
    }

    public static class CreditService {
        private int totalCredit;

        private Map<String, Integer> reservations = new HashMap<>();

        public CreditService(int totalCredit) {
            this.totalCredit = totalCredit;
        }

        public synchronized void reserveCredit(String id, @Header("amount")
        int amount) {
            int credit = getCredit();
            if (amount > credit) {
                throw new IllegalStateException("Insufficient credit");
            }
            reservations.put(id, amount);
        }

        public synchronized void refundCredit(String id) {
            reservations.remove(id);
        }

        public synchronized int getCredit() {
            return (totalCredit) - (reservations.values().stream().reduce(0, ( a, b) -> a + b));
        }
    }
}

