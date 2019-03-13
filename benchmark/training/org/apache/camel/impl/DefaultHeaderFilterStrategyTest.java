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
package org.apache.camel.impl;


import java.util.HashSet;
import java.util.Set;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultHeaderFilterStrategy;
import org.junit.Assert;
import org.junit.Test;


public class DefaultHeaderFilterStrategyTest extends ContextTestSupport {
    @Test
    public void testSimpleDefaultHeaderFilterStrategy() {
        DefaultHeaderFilterStrategy comp = new DefaultHeaderFilterStrategy();
        comp.setAllowNullValues(true);
        Assert.assertEquals(true, comp.isAllowNullValues());
        comp.setLowerCase(true);
        Assert.assertEquals(true, comp.isLowerCase());
        comp.setCaseInsensitive(true);
        Assert.assertEquals(true, comp.isCaseInsensitive());
        comp.setFilterOnMatch(false);
        Assert.assertEquals(false, comp.isFilterOnMatch());
    }

    @Test
    public void testInFilterDefaultHeaderFilterStrategy() {
        DefaultHeaderFilterStrategy comp = new DefaultHeaderFilterStrategy();
        Set<String> set = new HashSet<>();
        set.add("foo");
        comp.setInFilter(set);
        Assert.assertEquals(set, comp.getInFilter());
    }

    @Test
    public void testInFilterDoFilterDefaultHeaderFilterStrategy() {
        DefaultHeaderFilterStrategy comp = new DefaultHeaderFilterStrategy();
        Set<String> set = new HashSet<>();
        set.add("foo");
        comp.setInFilter(set);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setHeader("bar", 123);
        exchange.getIn().setHeader("foo", "cheese");
        Assert.assertFalse(comp.applyFilterToExternalHeaders("bar", 123, exchange));
        Assert.assertTrue(comp.applyFilterToExternalHeaders("foo", "cheese", exchange));
    }

    @Test
    public void testOutFilterDefaultHeaderFilterStrategy() {
        DefaultHeaderFilterStrategy comp = new DefaultHeaderFilterStrategy();
        Set<String> set = new HashSet<>();
        set.add("foo");
        comp.setOutFilter(set);
        Assert.assertEquals(set, comp.getOutFilter());
    }

    @Test
    public void testOutFilterDoFilterDefaultHeaderFilterStrategy() {
        DefaultHeaderFilterStrategy comp = new DefaultHeaderFilterStrategy();
        Set<String> set = new HashSet<>();
        set.add("foo");
        comp.setOutFilter(set);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setHeader("bar", 123);
        exchange.getIn().setHeader("foo", "cheese");
        Assert.assertFalse(comp.applyFilterToCamelHeaders("bar", 123, exchange));
        Assert.assertTrue(comp.applyFilterToCamelHeaders("foo", "cheese", exchange));
    }

    @Test
    public void testOutReverseFilterDefaultHeaderFilterStrategy() {
        DefaultHeaderFilterStrategy comp = new DefaultHeaderFilterStrategy();
        comp.setFilterOnMatch(false);
        Set<String> set = new HashSet<>();
        set.add("foo");
        comp.setOutFilter(set);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setHeader("bar", 123);
        exchange.getIn().setHeader("foo", "cheese");
        Assert.assertTrue(comp.applyFilterToCamelHeaders("bar", 123, exchange));
        Assert.assertFalse(comp.applyFilterToCamelHeaders("foo", "cheese", exchange));
    }

    @Test
    public void testCaseInsensitiveHeaderNameDoFilterDefaultHeaderFilterStrategy() {
        DefaultHeaderFilterStrategy comp = new DefaultHeaderFilterStrategy();
        comp.setCaseInsensitive(true);
        Set<String> set = new HashSet<>();
        set.add("Content-Type");
        comp.setOutFilter(set);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setHeader("content-type", "application/xml");
        exchange.getIn().setHeader("Content-Type", "application/json");
        Assert.assertTrue(comp.applyFilterToCamelHeaders("content-type", "application/xml", exchange));
        Assert.assertTrue(comp.applyFilterToCamelHeaders("Content-Type", "application/json", exchange));
    }
}

