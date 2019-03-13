/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import org.junit.Test;


public class NodesPartitioningTest {
    @Test
    public void test2Partitions() {
        String drl = (((((((ruleA(1)) + (ruleB(2))) + (ruleC(2))) + (ruleD(1))) + (ruleD(2))) + (ruleC(1))) + (ruleA(2))) + (ruleB(1));
        checkDrl(drl);
    }

    @Test
    public void testPartitioningWithSharedNodes() {
        StringBuilder sb = new StringBuilder(400);
        for (int i = 1; i < 4; i++) {
            sb.append(getRule(i));
        }
        for (int i = 1; i < 4; i++) {
            sb.append(getNotRule(i));
        }
        checkDrl(sb.toString());
    }

    public static class Account {
        private final int number;

        private final int uuid;

        private final NodesPartitioningTest.Customer owner;

        public Account(int number, int uuid, NodesPartitioningTest.Customer owner) {
            this.number = number;
            this.uuid = uuid;
            this.owner = owner;
        }

        public int getNumber() {
            return number;
        }

        public int getUuid() {
            return uuid;
        }

        public NodesPartitioningTest.Customer getOwner() {
            return owner;
        }
    }

    public static class Customer {
        private final int uuid;

        public Customer(int uuid) {
            this.uuid = uuid;
        }

        public int getUuid() {
            return uuid;
        }
    }

    @Test
    public void testChangePartitionOfAlphaSourceOfAlpha() {
        // DROOLS-1487
        String drl = ((((((((((((((((("import " + (NodesPartitioningTest.Account.class.getCanonicalName())) + ";\n") + "import ") + (NodesPartitioningTest.Customer.class.getCanonicalName())) + ";\n") + "rule \"customerDoesNotHaveSpecifiedAccount_2\"\n") + "when\n") + "    $account : Account (number == 1, uuid == \"customerDoesNotHaveSpecifiedAccount\")\n") + "    Customer (uuid == \"customerDoesNotHaveSpecifiedAccount\")\n") + "then\n") + "end\n") + "\n") + "rule \"customerDoesNotHaveSpecifiedAccount_1\"\n") + "when\n") + "    $account : Account (number == 2, uuid == \"customerDoesNotHaveSpecifiedAccount\")\n") + "    Customer (uuid == \"customerDoesNotHaveSpecifiedAccount\")\n") + "then\n") + "end";
        checkDrl(drl);
    }
}

