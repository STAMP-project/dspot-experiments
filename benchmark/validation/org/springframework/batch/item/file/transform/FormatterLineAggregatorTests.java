/**
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.file.transform;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link FormatterLineAggregator}
 *
 * @author Dave Syer
 */
public class FormatterLineAggregatorTests {
    // object under test
    private FormatterLineAggregator<String[]> aggregator;

    private FieldExtractor<String[]> defaultFieldExtractor = new FieldExtractor<String[]>() {
        @Override
        public Object[] extract(String[] item) {
            return item;
        }
    };

    /**
     * If no ranges are specified, IllegalArgumentException is thrown
     */
    @Test
    public void testAggregateNullRecordDescriptor() {
        String[] args = new String[]{ "does not matter what is here" };
        try {
            aggregator.aggregate(args);
            Assert.fail("should not work with no format specified");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Text length exceeds the length of the column.
     */
    @Test
    public void testAggregateInvalidInputLength() {
        String[] args = new String[]{ "Oversize" };
        aggregator.setMaximumLength(3);
        aggregator.setFormat("%3s");
        try {
            aggregator.aggregate(args);
            Assert.fail("Invalid text length, exception should have been thrown");
        } catch (IllegalStateException expected) {
            // expected
        }
    }

    /**
     * Test aggregation
     */
    @Test
    public void testAggregate() {
        String[] args = new String[]{ "Matchsize", "Smallsize" };
        aggregator.setFormat("%9s%9s");
        String result = aggregator.aggregate(args);
        Assert.assertEquals("MatchsizeSmallsize", result);
    }

    /**
     * Test aggregation with last range unbound
     */
    @Test
    public void testAggregateWithLastRangeUnbound() {
        String[] args = new String[]{ "Matchsize", "Smallsize" };
        aggregator.setFormat("%-12s%s");
        String result = aggregator.aggregate(args);
        Assert.assertEquals("Matchsize   Smallsize", result);
    }

    /**
     * Test aggregation with right alignment
     */
    @Test
    public void testAggregateFormattedRight() {
        String[] args = new String[]{ "Matchsize", "Smallsize" };
        aggregator.setFormat("%13s%10s");
        String result = aggregator.aggregate(args);
        Assert.assertEquals(23, result.length());
        Assert.assertEquals("    Matchsize Smallsize", result);
    }

    /**
     * Test aggregation with center alignment
     */
    @Test
    public void testAggregateFormattedCenter() {
        String[] args = new String[]{ "Matchsize", "Smallsize" };
        aggregator.setFormat("%13s%12s");
        aggregator.setMinimumLength(25);
        aggregator.setMaximumLength(25);
        aggregator.setFieldExtractor(new FieldExtractor<String[]>() {
            private int[] widths = new int[]{ 13, 12 };

            @Override
            public Object[] extract(String[] item) {
                String[] strings = new String[item.length];
                for (int i = 0; i < (strings.length); i++) {
                    strings[i] = item[i];
                    if ((item[i].length()) < (widths[i])) {
                        StringBuilder buffer = new StringBuilder(strings[i]);
                        for (int j = 0; j < ((((widths[i]) - (item[i].length())) + 1) / 2); j++) {
                            buffer.append(" ");
                        }
                        strings[i] = buffer.toString();
                    }
                }
                return strings;
            }
        });
        String result = aggregator.aggregate(args);
        Assert.assertEquals("  Matchsize   Smallsize  ", result);
    }

    /**
     * Test aggregation with left alignment
     */
    @Test
    public void testAggregateWithCustomPadding() {
        String[] args = new String[]{ "Matchsize", "Smallsize" };
        aggregator.setFormat("%13s%11s");
        aggregator.setMinimumLength(24);
        aggregator.setMaximumLength(24);
        aggregator.setFieldExtractor(new FieldExtractor<String[]>() {
            private int[] widths = new int[]{ 13, 11 };

            @Override
            public Object[] extract(String[] item) {
                String[] strings = new String[item.length];
                for (int i = 0; i < (strings.length); i++) {
                    strings[i] = item[i];
                    if ((item[i].length()) < (widths[i])) {
                        StringBuilder buffer = new StringBuilder(strings[i]);
                        for (int j = 0; j < ((widths[i]) - (item[i].length())); j++) {
                            buffer.append(".");
                        }
                        strings[i] = buffer.toString();
                    }
                }
                return strings;
            }
        });
        String result = aggregator.aggregate(args);
        Assert.assertEquals("Matchsize....Smallsize..", result);
    }

    /**
     * Test aggregation with left alignment
     */
    @Test
    public void testAggregateFormattedLeft() {
        String[] args = new String[]{ "Matchsize", "Smallsize" };
        aggregator.setFormat("%-13s%-11s");
        String result = aggregator.aggregate(args);
        Assert.assertEquals("Matchsize    Smallsize  ", result);
    }

    /**
     * If one of the passed arguments is null, string filled with spaces should
     * be returned
     */
    @Test
    public void testAggregateNullArgument() {
        String[] args = new String[]{ "foo", null, "bar" };
        aggregator.setFormat("%3s%3s%3s");
        Assert.assertEquals("foo   bar", aggregator.aggregate(args));
    }
}

