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
package org.apache.commons.lang3.builder;


import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class MultilineRecursiveToStringStyleTest {
    private final String BR = System.lineSeparator();

    @Test
    public void simpleObject() {
        final MultilineRecursiveToStringStyleTest.Transaction tx = new MultilineRecursiveToStringStyleTest.Transaction("2014.10.15", 100);
        final String expected = (((((((getClassPrefix(tx)) + "[") + (BR)) + "  amount=100.0,") + (BR)) + "  date=2014.10.15") + (BR)) + "]";
        Assertions.assertEquals(expected, toString(tx));
    }

    @Test
    public void nestedElements() {
        final MultilineRecursiveToStringStyleTest.Customer customer = new MultilineRecursiveToStringStyleTest.Customer("Douglas Adams");
        final MultilineRecursiveToStringStyleTest.Bank bank = new MultilineRecursiveToStringStyleTest.Bank("ASF Bank");
        customer.bank = bank;
        final String exp = (((((((((((((((getClassPrefix(customer)) + "[") + (BR)) + "  name=Douglas Adams,") + (BR)) + "  bank=") + (getClassPrefix(bank))) + "[") + (BR)) + "    name=ASF Bank") + (BR)) + "  ],") + (BR)) + "  accounts=<null>") + (BR)) + "]";
        Assertions.assertEquals(exp, toString(customer));
    }

    @Test
    public void nestedAndArray() {
        final MultilineRecursiveToStringStyleTest.Account acc = new MultilineRecursiveToStringStyleTest.Account();
        final MultilineRecursiveToStringStyleTest.Transaction tx1 = new MultilineRecursiveToStringStyleTest.Transaction("2014.10.14", 100);
        final MultilineRecursiveToStringStyleTest.Transaction tx2 = new MultilineRecursiveToStringStyleTest.Transaction("2014.10.15", 50);
        acc.transactions.add(tx1);
        acc.transactions.add(tx2);
        final String expected = (((((((((((((((((((((((((((((((getClassPrefix(acc)) + "[") + (BR)) + "  owner=<null>,") + (BR)) + "  transactions=") + (getClassPrefix(acc.transactions))) + "{") + (BR)) + "    ") + (getClassPrefix(tx1))) + "[") + (BR)) + "      amount=100.0,") + (BR)) + "      date=2014.10.14") + (BR)) + "    ],") + (BR)) + "    ") + (getClassPrefix(tx2))) + "[") + (BR)) + "      amount=50.0,") + (BR)) + "      date=2014.10.15") + (BR)) + "    ]") + (BR)) + "  }") + (BR)) + "]";
        Assertions.assertEquals(expected, toString(acc));
    }

    @Test
    public void noArray() {
        final MultilineRecursiveToStringStyleTest.WithArrays wa = new MultilineRecursiveToStringStyleTest.WithArrays();
        final String exp = (((((((((((((((getClassPrefix(wa)) + "[") + (BR)) + "  boolArray=<null>,") + (BR)) + "  charArray=<null>,") + (BR)) + "  intArray=<null>,") + (BR)) + "  doubleArray=<null>,") + (BR)) + "  longArray=<null>,") + (BR)) + "  stringArray=<null>") + (BR)) + "]";
        Assertions.assertEquals(exp, toString(wa));
    }

    @Test
    public void boolArray() {
        final MultilineRecursiveToStringStyleTest.WithArrays wa = new MultilineRecursiveToStringStyleTest.WithArrays();
        wa.boolArray = new boolean[]{ true, false, true };
        final String exp = (((((((((((((((((((((((getClassPrefix(wa)) + "[") + (BR)) + "  boolArray={") + (BR)) + "    true,") + (BR)) + "    false,") + (BR)) + "    true") + (BR)) + "  },") + (BR)) + "  charArray=<null>,") + (BR)) + "  intArray=<null>,") + (BR)) + "  doubleArray=<null>,") + (BR)) + "  longArray=<null>,") + (BR)) + "  stringArray=<null>") + (BR)) + "]";
        Assertions.assertEquals(exp, toString(wa));
    }

    @Test
    public void charArray() {
        final MultilineRecursiveToStringStyleTest.WithArrays wa = new MultilineRecursiveToStringStyleTest.WithArrays();
        wa.charArray = new char[]{ 'a', 'A' };
        final String exp = (((((((((((((((((((((getClassPrefix(wa)) + "[") + (BR)) + "  boolArray=<null>,") + (BR)) + "  charArray={") + (BR)) + "    a,") + (BR)) + "    A") + (BR)) + "  },") + (BR)) + "  intArray=<null>,") + (BR)) + "  doubleArray=<null>,") + (BR)) + "  longArray=<null>,") + (BR)) + "  stringArray=<null>") + (BR)) + "]";
        Assertions.assertEquals(exp, toString(wa));
    }

    @Test
    public void intArray() {
        final MultilineRecursiveToStringStyleTest.WithArrays wa = new MultilineRecursiveToStringStyleTest.WithArrays();
        wa.intArray = new int[]{ 1, 2 };
        final String exp = (((((((((((((((((((((getClassPrefix(wa)) + "[") + (BR)) + "  boolArray=<null>,") + (BR)) + "  charArray=<null>,") + (BR)) + "  intArray={") + (BR)) + "    1,") + (BR)) + "    2") + (BR)) + "  },") + (BR)) + "  doubleArray=<null>,") + (BR)) + "  longArray=<null>,") + (BR)) + "  stringArray=<null>") + (BR)) + "]";
        Assertions.assertEquals(exp, toString(wa));
    }

    @Test
    public void doubleArray() {
        final MultilineRecursiveToStringStyleTest.WithArrays wa = new MultilineRecursiveToStringStyleTest.WithArrays();
        wa.doubleArray = new double[]{ 1, 2 };
        final String exp = (((((((((((((((((((((getClassPrefix(wa)) + "[") + (BR)) + "  boolArray=<null>,") + (BR)) + "  charArray=<null>,") + (BR)) + "  intArray=<null>,") + (BR)) + "  doubleArray={") + (BR)) + "    1.0,") + (BR)) + "    2.0") + (BR)) + "  },") + (BR)) + "  longArray=<null>,") + (BR)) + "  stringArray=<null>") + (BR)) + "]";
        Assertions.assertEquals(exp, toString(wa));
    }

    @Test
    public void longArray() {
        final MultilineRecursiveToStringStyleTest.WithArrays wa = new MultilineRecursiveToStringStyleTest.WithArrays();
        wa.longArray = new long[]{ 1L, 2L };
        final String exp = (((((((((((((((((((((getClassPrefix(wa)) + "[") + (BR)) + "  boolArray=<null>,") + (BR)) + "  charArray=<null>,") + (BR)) + "  intArray=<null>,") + (BR)) + "  doubleArray=<null>,") + (BR)) + "  longArray={") + (BR)) + "    1,") + (BR)) + "    2") + (BR)) + "  },") + (BR)) + "  stringArray=<null>") + (BR)) + "]";
        Assertions.assertEquals(exp, toString(wa));
    }

    @Test
    public void stringArray() {
        final MultilineRecursiveToStringStyleTest.WithArrays wa = new MultilineRecursiveToStringStyleTest.WithArrays();
        wa.stringArray = new String[]{ "a", "A" };
        final String exp = (((((((((((((((((((((getClassPrefix(wa)) + "[") + (BR)) + "  boolArray=<null>,") + (BR)) + "  charArray=<null>,") + (BR)) + "  intArray=<null>,") + (BR)) + "  doubleArray=<null>,") + (BR)) + "  longArray=<null>,") + (BR)) + "  stringArray={") + (BR)) + "    a,") + (BR)) + "    A") + (BR)) + "  }") + (BR)) + "]";
        Assertions.assertEquals(exp, toString(wa));
    }

    @Test
    public void testLANG1319() {
        final String[] stringArray = new String[]{ "1", "2" };
        final String exp = (((((((((((getClassPrefix(stringArray)) + "[") + (BR)) + "  {") + (BR)) + "    1,") + (BR)) + "    2") + (BR)) + "  }") + (BR)) + "]";
        Assertions.assertEquals(exp, toString(stringArray));
    }

    static class WithArrays {
        boolean[] boolArray;

        char[] charArray;

        int[] intArray;

        double[] doubleArray;

        long[] longArray;

        String[] stringArray;
    }

    static class Bank {
        String name;

        Bank(final String name) {
            this.name = name;
        }
    }

    static class Customer {
        String name;

        MultilineRecursiveToStringStyleTest.Bank bank;

        List<MultilineRecursiveToStringStyleTest.Account> accounts;

        Customer(final String name) {
            this.name = name;
        }
    }

    static class Account {
        MultilineRecursiveToStringStyleTest.Customer owner;

        List<MultilineRecursiveToStringStyleTest.Transaction> transactions = new ArrayList<>();

        public double getBalance() {
            double balance = 0;
            for (final MultilineRecursiveToStringStyleTest.Transaction tx : transactions) {
                balance += tx.amount;
            }
            return balance;
        }
    }

    static class Transaction {
        double amount;

        String date;

        Transaction(final String datum, final double betrag) {
            this.date = datum;
            this.amount = betrag;
        }
    }
}

