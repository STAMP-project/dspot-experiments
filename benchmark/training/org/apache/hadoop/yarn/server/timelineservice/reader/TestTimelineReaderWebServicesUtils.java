/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.timelineservice.reader;


import com.google.common.collect.Sets;
import org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareOp;
import org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineFilterList;
import org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineFilterList.Operator;
import org.junit.Assert;
import org.junit.Test;


public class TestTimelineReaderWebServicesUtils {
    @Test
    public void testMetricFiltersParsing() throws Exception {
        String expr = "(((key11 ne 234 AND key12 gt 23) AND " + "(key13 lt 34 OR key14 ge 567)) OR (key21 lt 24 OR key22 le 45))";
        TimelineFilterList expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(Operator.AND, new TimelineFilterList(Operator.AND, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "key11", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "key12", 23, true)), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "key13", 34, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "key14", 567, true))), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "key21", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "key22", 45, true)));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "abc ene 234";
        expectedList = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "abc ne 234";
        expectedList = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, false));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "abc ne 234 AND def gt 23";
        expectedList = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "def", 23, true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "(abc ne 234 AND def gt 23)";
        expectedList = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "def", 23, true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "abc ne 234 AND def gt 23 OR rst lt 24";
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "def", 23, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "rst", 24, true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "abc ne 234 AND def gt 23 OR rst lt 24 OR xyz le 456";
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "def", 23, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "rst", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "xyz", 456, true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "abc ne 234 AND def gt 23 OR rst lt 24 OR xyz le 456 AND pqr ge 2";
        expectedList = new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "def", 23, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "rst", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "xyz", 456, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "pqr", 2, true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        // Test with unnecessary spaces.
        expr = "  abc ne   234       AND       def           gt 23 OR     rst lt " + "           24     OR xyz     le     456    AND pqr ge 2        ";
        expectedList = new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "def", 23, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "rst", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "xyz", 456, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "pqr", 2, true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "(((key11 ne 234 AND key12 gt 23 OR key13 lt 24 OR key14 le 456 " + ("AND key15 ge 2) AND (key16 lt 34 OR key17 ge 567)) OR (key21 lt 24 " + "OR key22 le 45))");
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(Operator.AND, new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "key11", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "key12", 23, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "key13", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "key14", 456, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "key15", 2, true)), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "key16", 34, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "key17", 567, true))), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "key21", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "key22", 45, true)));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "   (  (     (        key11      ne     234    AND key12    gt   " + (("23    OR    key13    lt    24 OR key14 le 456   AND   key15   ge   2" + "   )   AND ( key16 lt 34 OR key17 ge 567 )    )     OR ") + "(   key21 lt 24 OR key22 le 45 )   )    ");
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(Operator.AND, new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "key11", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "key12", 23, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "key13", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "key14", 456, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "key15", 2, true)), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "key16", 34, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "key17", 567, true))), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "key21", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "key22", 45, true)));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseMetricFilters(expr), expectedList);
        expr = "(((key11 ne 234 AND key12 gt 23 OR key13 lt 24 OR key14 le 456 " + ("AND key15 ge 2) AND (key16 lt 34 OR key17 ge 567)) OR (key21 lt 24 " + "OR key22 le 45)");
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Improper brackers. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        expr = "(((key11 ne 234 AND key12 gt v3 OR key13 lt 24 OR key14 le 456 " + ("AND key15 ge 2) AND (key16 lt 34 OR key17 ge 567)) OR (key21 lt 24 " + "OR key22 le 45))");
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Non Numeric value. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        expr = "(((key11 ne (234 AND key12 gt 3 OR key13 lt 24 OR key14 le 456 " + ("AND key15 ge 2) AND (key16 lt 34 OR key17 ge 567)) OR (key21 lt 24 " + "OR key22 le 45))");
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Unexpected opening bracket. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        expr = "(((k)ey11 ne 234 AND key12 gt 3 OR key13 lt 24 OR key14 le 456 " + ("AND key15 ge 2) AND (key16 lt 34 OR key17 ge 567)) OR (key21 lt 24 " + "OR key22 le 45))");
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Unexpected closing bracket. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        expr = "(((key11 rs 234 AND key12 gt 3 OR key13 lt 24 OR key14 le 456 " + ("AND key15 ge 2) AND (key16 lt 34 OR key17 ge 567)) OR (key21 lt 24 " + "OR key22 le 45))");
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Improper compare op. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        expr = "(((key11 ne 234 PI key12 gt 3 OR key13 lt 24 OR key14 le 456 " + ("AND key15 ge 2) AND (key16 lt 34 OR key17 ge 567)) OR (key21 lt 24 " + "OR key22 le 45))");
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Improper op. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        expr = "(((key11 ne 234 PI key12 gt 3 OR key13 lt 24 OR key14 le 456 " + ("AND key15 ge 2) AND (key16 lt 34 OR key17 ge 567)) OR (key21 lt 24 " + "OR key22 le 45))");
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Improper op. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        expr = "(key11 ne 234 AND key12 gt 3)) OR (key13 lt 24 OR key14 le 456)";
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Unbalanced brackets. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        expr = "(key11 rne 234 AND key12 gt 3) OR (key13 lt 24 OR key14 le 456)";
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Invalid compareop. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        expr = "(key11 ne 234 AND key12 gt 3) OR (key13 lt 24 OR key14 le";
        try {
            TimelineReaderWebServicesUtils.parseMetricFilters(expr);
            Assert.fail("Compareop cant be parsed. Exception should have been thrown.");
        } catch (TimelineParseException e) {
        }
        Assert.assertNull(TimelineReaderWebServicesUtils.parseMetricFilters(null));
        Assert.assertNull(TimelineReaderWebServicesUtils.parseMetricFilters("   "));
    }

    @Test
    public void testConfigFiltersParsing() throws Exception {
        String expr = "(((key11 ne 234 AND key12 eq val12) AND " + ("(key13 ene val13 OR key14 eq 567)) OR (key21 eq val_21 OR key22 eq " + "val.22))");
        TimelineFilterList expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(Operator.AND, new TimelineFilterList(Operator.AND, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "key11", "234", false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "key12", "val12", true)), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "key13", "val13", true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "key14", "567", true))), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "key21", "val_21", true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "key22", "val.22", true)));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseKVFilters(expr, true), expectedList);
        expr = "abc ne 234 AND def eq 23 OR rst ene 24 OR xyz eq 456 AND pqr eq 2";
        expectedList = new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "abc", "234", false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "def", "23", true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "rst", "24", true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "xyz", "456", true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "pqr", "2", true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseKVFilters(expr, true), expectedList);
        // Test with unnecessary spaces.
        expr = "  abc ne   234       AND       def           eq 23 OR     rst ene " + "           24     OR xyz     eq     456    AND pqr eq 2        ";
        expectedList = new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "abc", "234", false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "def", "23", true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "rst", "24", true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "xyz", "456", true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "pqr", "2", true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseKVFilters(expr, true), expectedList);
        expr = "abc gt 234 AND def eq 23 OR rst ene 24 OR xyz eq 456 AND pqr eq 2";
        try {
            TimelineReaderWebServicesUtils.parseKVFilters(expr, true);
            Assert.fail(("Invalid compareop specified for config filters. Should be either" + " eq,ne or ene and exception should have been thrown."));
        } catch (TimelineParseException e) {
        }
    }

    @Test
    public void testInfoFiltersParsing() throws Exception {
        String expr = "(((key11 ne 234 AND key12 eq val12) AND " + ("(key13 ene val13 OR key14 eq 567)) OR (key21 eq val_21 OR key22 eq " + "5.0))");
        TimelineFilterList expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(Operator.AND, new TimelineFilterList(Operator.AND, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "key11", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "key12", "val12", true)), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "key13", "val13", true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "key14", 567, true))), new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "key21", "val_21", true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "key22", 5.0, true)));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseKVFilters(expr, false), expectedList);
        expr = "abc ne 234 AND def eq 23 OR rst ene 24 OR xyz eq 456 AND pqr eq " + "val.1234";
        expectedList = new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "def", 23, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "rst", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "xyz", 456, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "pqr", "val.1234", true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseKVFilters(expr, false), expectedList);
        // Test with unnecessary spaces.
        expr = "  abc ne   234       AND       def           eq 23 OR     rst ene " + "           24     OR xyz     eq     456    AND pqr eq 2        ";
        expectedList = new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "abc", 234, false), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "def", 23, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "rst", 24, true), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "xyz", 456, true)), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "pqr", 2, true));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseKVFilters(expr, false), expectedList);
        expr = "abdeq";
        try {
            TimelineReaderWebServicesUtils.parseKVFilters(expr, false);
            Assert.fail("Expression valuation should throw exception.");
        } catch (TimelineParseException e) {
            // expected: do nothing
        }
        expr = "abc gt 234 AND defeq";
        try {
            TimelineReaderWebServicesUtils.parseKVFilters(expr, false);
            Assert.fail("Expression valuation should throw exception.");
        } catch (TimelineParseException e) {
            // expected: do nothing
        }
        expr = "((key11 ne 234 AND key12 eq val12) AND (key13eq OR key14 eq va14))";
        try {
            TimelineReaderWebServicesUtils.parseKVFilters(expr, false);
            Assert.fail("Expression valuation should throw exception.");
        } catch (TimelineParseException e) {
            // expected: do nothing
        }
    }

    @Test
    public void testEventFiltersParsing() throws Exception {
        String expr = "abc,def";
        TimelineFilterList expectedList = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "abc"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "def"));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseEventFilters(expr), expectedList);
        expr = "(abc,def)";
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseEventFilters(expr), expectedList);
        expr = "(abc,def) OR (rst, uvx)";
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "abc"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "def")), new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "rst"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "uvx")));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseEventFilters(expr), expectedList);
        expr = "!(abc,def,uvc) OR (rst, uvx)";
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "abc"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "def"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "uvc")), new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "rst"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "uvx")));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseEventFilters(expr), expectedList);
        expr = "(((!(abc,def,uvc) OR (rst, uvx)) AND (!(abcdefg) OR !(ghj,tyu)))" + " OR ((bcd,tyu) AND uvb))";
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "abc"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "def"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "uvc")), new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "rst"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "uvx"))), new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "abcdefg")), new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "ghj"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "tyu")))), new TimelineFilterList(new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "bcd"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "tyu")), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "uvb")));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseEventFilters(expr), expectedList);
        expr = "  (  (  (  !  (  abc , def  ,   uvc)   OR   (   rst  ,   uvx )  )" + ("  AND   (  !  (  abcdefg ) OR  !   (  ghj,  tyu)  ) )  OR   (   (" + "   bcd   ,   tyu  )   AND   uvb  )   )");
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseEventFilters(expr), expectedList);
        expr = "(((!(abc,def,uvc) OR (rst, uvx)) AND (!(abcdefg) OR !(ghj,tyu)))" + " OR ((bcd,tyu) AND uvb)";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Improper brackets. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "(((!(abc,def,uvc) (OR (rst, uvx)) AND (!(abcdefg) OR !(ghj,tyu)))" + " OR ((bcd,tyu) AND uvb))";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Unexpected opening bracket. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "(((!(abc,def,uvc) OR) (rst, uvx)) AND (!(abcdefg) OR !(ghj,tyu)))" + " OR ((bcd,tyu) AND uvb))";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Unexpected closing bracket. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "(((!(abc,def,uvc) PI (rst, uvx)) AND (!(abcdefg) OR !(ghj,tyu)))" + " OR ((bcd,tyu) AND uvb))";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Invalid op. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "(((!(abc,def,uvc) !OR (rst, uvx)) AND (!(abcdefg) OR !(ghj,tyu)))" + " OR ((bcd,tyu) AND uvb))";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Unexpected ! char. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "abc,def,uvc) OR (rst, uvx)";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Unexpected closing bracket. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "abc,def,uvc OR )rst, uvx)";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Unexpected closing bracket. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "abc,def,uvc OR ,rst, uvx)";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Unexpected delimiter. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "abc,def,uvc OR !  ";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Unexpected not char. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "(abc,def,uvc)) OR (rst, uvx)";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("Unbalanced brackets. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "(((! ,(abc,def,uvc) OR (rst, uvx)) AND (!(abcdefg) OR !(ghj,tyu" + "))) OR ((bcd,tyu) AND uvb))";
        try {
            TimelineReaderWebServicesUtils.parseEventFilters(expr);
            Assert.fail("( should follow ! char. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        Assert.assertNull(TimelineReaderWebServicesUtils.parseEventFilters(null));
        Assert.assertNull(TimelineReaderWebServicesUtils.parseEventFilters("   "));
    }

    @Test
    public void testRelationFiltersParsing() throws Exception {
        String expr = "type1:entity11,type2:entity21:entity22";
        TimelineFilterList expectedList = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type1", Sets.newHashSet(((Object) ("entity11")))), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type2", Sets.newHashSet(((Object) ("entity21")), "entity22")));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseRelationFilters(expr), expectedList);
        expr = "(type1:entity11,type2:entity21:entity22)";
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseRelationFilters(expr), expectedList);
        expr = "(type1:entity11,type2:entity21:entity22) OR (type3:entity31:" + "entity32:entity33,type1:entity11:entity12)";
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type1", Sets.newHashSet(((Object) ("entity11")))), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type2", Sets.newHashSet(((Object) ("entity21")), "entity22"))), new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type3", Sets.newHashSet(((Object) ("entity31")), "entity32", "entity33")), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type1", Sets.newHashSet(((Object) ("entity11")), "entity12"))));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseRelationFilters(expr), expectedList);
        expr = "!(type1:entity11,type2:entity21:entity22,type5:entity51) OR " + "(type3:entity31:entity32:entity33,type1:entity11:entity12)";
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "type1", Sets.newHashSet(((Object) ("entity11")))), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "type2", Sets.newHashSet(((Object) ("entity21")), "entity22")), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "type5", Sets.newHashSet(((Object) ("entity51"))))), new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type3", Sets.newHashSet(((Object) ("entity31")), "entity32", "entity33")), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type1", Sets.newHashSet(((Object) ("entity11")), "entity12"))));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseRelationFilters(expr), expectedList);
        expr = "(((!(type1:entity11,type2:entity21:entity22,type5:entity51) OR " + (("(type3:entity31:entity32:entity33,type1:entity11:entity12)) AND " + "(!(type11:entity111) OR !(type4:entity43:entity44:entity47:entity49,") + "type7:entity71))) OR ((type2:entity2,type8:entity88) AND t9:e:e1))");
        expectedList = new TimelineFilterList(Operator.OR, new TimelineFilterList(new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "type1", Sets.newHashSet(((Object) ("entity11")))), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "type2", Sets.newHashSet(((Object) ("entity21")), "entity22")), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "type5", Sets.newHashSet(((Object) ("entity51"))))), new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type3", Sets.newHashSet(((Object) ("entity31")), "entity32", "entity33")), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type1", Sets.newHashSet(((Object) ("entity11")), "entity12")))), new TimelineFilterList(Operator.OR, new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "type11", Sets.newHashSet(((Object) ("entity111"))))), new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "type4", Sets.newHashSet(((Object) ("entity43")), "entity44", "entity47", "entity49")), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "type7", Sets.newHashSet(((Object) ("entity71"))))))), new TimelineFilterList(new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type2", Sets.newHashSet(((Object) ("entity2")))), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type8", Sets.newHashSet(((Object) ("entity88"))))), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "t9", Sets.newHashSet(((Object) ("e")), "e1"))));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseRelationFilters(expr), expectedList);
        expr = "   (   (  (   !   (   type1:entity11  ,  type2:entity21:entity22" + (((("  ,  type5:entity51  )   OR  (   type3:entity31:entity32:entity33  " + "     ,   type1:entity11:entity12)) AND (!(  type11:entity111  )  OR ") + "    !   (   type4:entity43:entity44:entity47:entity49 , ") + "type7:entity71  )  )  ) OR  (  (  type2:entity2 , type8:entity88) ") + "AND  t9:e:e1 )    ) ");
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseRelationFilters(expr), expectedList);
        expr = "(((!(type1 : entity11,type2:entity21:entity22,type5:entity51) OR " + (("(type3:entity31:entity32:entity33,type1:entity11:entity12)) AND " + "(!(type11:entity111) OR !(type4:entity43:entity44:entity47:entity49,") + "type7:entity71))) OR ((type2:entity2,type8:entity88) AND t9:e:e1))");
        try {
            TimelineReaderWebServicesUtils.parseRelationFilters(expr);
            Assert.fail(("Space not allowed in relation expression. Exception should have " + "been thrown"));
        } catch (TimelineParseException e) {
        }
    }

    @Test
    public void testDataToRetrieve() throws Exception {
        String expr = "abc,def";
        TimelineFilterList expectedList = new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.EQUAL, "abc"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.EQUAL, "def"));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = "(abc,def)";
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = "   (   abc  ,   def  )   ";
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = "    abc  ,   def   ";
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = "!(abc,def)";
        expectedList = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.NOT_EQUAL, "abc"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.NOT_EQUAL, "def"));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = " !  (   abc  ,  def  )  ";
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = "!(abc)";
        expectedList = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.NOT_EQUAL, "abc"));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = "(abc)";
        expectedList = new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.EQUAL, "abc"));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = "abc";
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = " !  (   abc  ,  def  , xyz)  ";
        expectedList = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.NOT_EQUAL, "abc"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.NOT_EQUAL, "def"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.NOT_EQUAL, "xyz"));
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = "!(abc,def,xyz)";
        TestTimelineReaderWebServicesUtils.verifyFilterList(expr, TimelineReaderWebServicesUtils.parseDataToRetrieve(expr), expectedList);
        expr = "!(abc,def,xyz";
        try {
            TimelineReaderWebServicesUtils.parseDataToRetrieve(expr);
            Assert.fail("No closing bracket. Exception should have been thrown");
        } catch (TimelineParseException e) {
        }
        expr = "!abc,def,xyz";
        try {
            TimelineReaderWebServicesUtils.parseDataToRetrieve(expr);
            Assert.fail(("NOT(!) should be followed by opening bracket. Exception should " + "have been thrown"));
        } catch (TimelineParseException e) {
        }
        expr = "!abc,def,xyz";
        try {
            TimelineReaderWebServicesUtils.parseDataToRetrieve(expr);
            Assert.fail(("NOT(!) should be followed by opening bracket. Exception should " + "have been thrown"));
        } catch (TimelineParseException e) {
        }
        expr = "!   r(  abc,def,xyz)";
        try {
            TimelineReaderWebServicesUtils.parseDataToRetrieve(expr);
            Assert.fail(("NOT(!) should be followed by opening bracket. Exception should " + "have been thrown"));
        } catch (TimelineParseException e) {
        }
        Assert.assertNull(TimelineReaderWebServicesUtils.parseDataToRetrieve(null));
        Assert.assertNull(TimelineReaderWebServicesUtils.parseDataToRetrieve("     "));
        Assert.assertNull(TimelineReaderWebServicesUtils.parseDataToRetrieve("()"));
        Assert.assertNull(TimelineReaderWebServicesUtils.parseDataToRetrieve("!()"));
        Assert.assertNull(TimelineReaderWebServicesUtils.parseDataToRetrieve("(     )"));
        Assert.assertNull(TimelineReaderWebServicesUtils.parseDataToRetrieve("!(   )"));
        Assert.assertNull(TimelineReaderWebServicesUtils.parseDataToRetrieve("!  (   )"));
    }
}

