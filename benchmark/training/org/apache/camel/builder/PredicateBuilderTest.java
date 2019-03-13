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
package org.apache.camel.builder;


import java.util.Arrays;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.TestSupport;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


public class PredicateBuilderTest extends TestSupport {
    protected Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());

    @Test
    public void testRegexPredicates() throws Exception {
        assertMatches(TestSupport.header("location").regex("[a-zA-Z]+,London,UK"));
        assertDoesNotMatch(TestSupport.header("location").regex("[a-zA-Z]+,Westminster,[a-zA-Z]+"));
    }

    @Test
    public void testPredicates() throws Exception {
        assertMatches(TestSupport.header("name").isEqualTo(Builder.constant("James")));
        assertMatches(PredicateBuilder.not(TestSupport.header("name").isEqualTo(Builder.constant("Claus"))));
        assertMatches(TestSupport.header("size").isEqualTo(10));
        assertMatches(TestSupport.header("size").isEqualTo("10"));
    }

    @Test
    public void testFailingPredicates() throws Exception {
        assertDoesNotMatch(TestSupport.header("name").isEqualTo(Builder.constant("Hiram")));
        assertDoesNotMatch(TestSupport.header("size").isGreaterThan(Builder.constant(100)));
        assertDoesNotMatch(PredicateBuilder.not(TestSupport.header("size").isLessThan(Builder.constant(100))));
    }

    @Test
    public void testCompoundOrPredicates() throws Exception {
        Predicate p1 = TestSupport.header("name").isEqualTo(Builder.constant("Hiram"));
        Predicate p2 = TestSupport.header("size").isGreaterThanOrEqualTo(Builder.constant(10));
        Predicate or = PredicateBuilder.or(p1, p2);
        assertMatches(or);
    }

    @Test
    public void testCompoundAndPredicates() throws Exception {
        Predicate p1 = TestSupport.header("name").isEqualTo(Builder.constant("James"));
        Predicate p2 = TestSupport.header("size").isGreaterThanOrEqualTo(Builder.constant(10));
        Predicate and = PredicateBuilder.and(p1, p2);
        assertMatches(and);
    }

    @Test
    public void testCompoundAndPredicatesVarargs() throws Exception {
        Predicate p1 = TestSupport.header("name").isEqualTo(Builder.constant("James"));
        Predicate p2 = TestSupport.header("size").isGreaterThanOrEqualTo(Builder.constant(10));
        Predicate p3 = TestSupport.header("location").contains(Builder.constant("London"));
        Predicate and = PredicateBuilder.and(p1, p2, p3);
        assertMatches(and);
    }

    @Test
    public void testOrSignatures() throws Exception {
        Predicate p1 = TestSupport.header("name").isEqualTo(Builder.constant("does-not-apply"));
        Predicate p2 = TestSupport.header("size").isGreaterThanOrEqualTo(Builder.constant(10));
        Predicate p3 = TestSupport.header("location").contains(Builder.constant("does-not-apply"));
        // check method signature with two parameters
        assertMatches(PredicateBuilder.or(p1, p2));
        assertMatches(PredicateBuilder.or(p2, p3));
        // check method signature with varargs
        assertMatches(PredicateBuilder.in(p1, p2, p3));
        assertMatches(PredicateBuilder.or(p1, p2, p3));
        // maybe a list of predicates?
        assertMatches(PredicateBuilder.in(Arrays.asList(p1, p2, p3)));
        assertMatches(PredicateBuilder.or(Arrays.asList(p1, p2, p3)));
    }

    @Test
    public void testCompoundAndOrPredicates() throws Exception {
        Predicate p1 = TestSupport.header("name").isEqualTo(Builder.constant("Hiram"));
        Predicate p2 = TestSupport.header("size").isGreaterThan(Builder.constant(100));
        Predicate p3 = TestSupport.header("location").contains("London");
        Predicate and = PredicateBuilder.and(p1, p2);
        Predicate andor = PredicateBuilder.or(and, p3);
        assertMatches(andor);
    }

    @Test
    public void testPredicateIn() throws Exception {
        assertMatches(PredicateBuilder.in(TestSupport.header("name").isEqualTo("Hiram"), TestSupport.header("name").isEqualTo("James")));
    }

    @Test
    public void testValueIn() throws Exception {
        assertMatches(TestSupport.header("name").in("Hiram", "Jonathan", "James", "Claus"));
    }

    @Test
    public void testEmptyHeaderValueIn() throws Exception {
        // there is no header with xxx
        assertDoesNotMatch(TestSupport.header("xxx").in("Hiram", "Jonathan", "James", "Claus"));
    }

    @Test
    public void testStartsWith() throws Exception {
        assertMatches(TestSupport.header("name").startsWith("J"));
        assertMatches(TestSupport.header("name").startsWith("James"));
        assertDoesNotMatch(TestSupport.header("name").startsWith("C"));
        assertMatches(TestSupport.header("size").startsWith("1"));
        assertMatches(TestSupport.header("size").startsWith("10"));
        assertDoesNotMatch(TestSupport.header("size").startsWith("99"));
        assertDoesNotMatch(TestSupport.header("size").startsWith("9"));
        assertMatches(TestSupport.header("size").startsWith(1));
        assertMatches(TestSupport.header("size").startsWith(10));
        assertDoesNotMatch(TestSupport.header("size").startsWith(99));
        assertDoesNotMatch(TestSupport.header("size").startsWith(9));
    }

    @Test
    public void testEndsWith() throws Exception {
        assertMatches(TestSupport.header("name").endsWith("mes"));
        assertMatches(TestSupport.header("name").endsWith("James"));
        assertDoesNotMatch(TestSupport.header("name").endsWith("world"));
        assertMatches(TestSupport.header("size").endsWith("0"));
        assertMatches(TestSupport.header("size").endsWith("10"));
        assertDoesNotMatch(TestSupport.header("size").endsWith("99"));
        assertDoesNotMatch(TestSupport.header("size").endsWith("9"));
        assertMatches(TestSupport.header("size").endsWith(0));
        assertMatches(TestSupport.header("size").endsWith(10));
        assertDoesNotMatch(TestSupport.header("size").endsWith(99));
        assertDoesNotMatch(TestSupport.header("size").endsWith(9));
    }

    @Test
    public void testNot() throws Exception {
        assertMatches(TestSupport.body().not().isInstanceOf(Integer.class));
        assertMatches(TestSupport.header("name").not().isEqualTo("Claus"));
        assertMatches(TestSupport.header("size").not().isLessThan(7));
        try {
            assertMatches(TestSupport.header("name").not().isEqualTo("James"));
            Assert.fail("Should fail");
        } catch (AssertionError e) {
            // expected
        }
    }
}

