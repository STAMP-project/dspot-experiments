/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.core.groupby;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.datastore.TimeUnit;
import org.kairosdb.core.formatter.FormatterException;


public class TimeGroupByTest {
    @Test
    public void test_getGroupByResults() throws FormatterException {
        TimeGroupBy groupBy = new TimeGroupBy(new org.kairosdb.core.datastore.Duration(2, TimeUnit.DAYS), 14);
        GroupByResult groupByResult = groupBy.getGroupByResult(2);
        MatcherAssert.assertThat(groupByResult.toJson(), Matchers.equalTo("{\"name\":\"time\",\"range_size\":{\"value\":2,\"unit\":\"DAYS\"},\"group_count\":14,\"group\":{\"group_number\":2}}"));
    }

    @Test
    public void test_getGroupId() {
        Map<String, String> tags = new HashMap<String, String>();
        TimeGroupBy groupBy = new TimeGroupBy(new org.kairosdb.core.datastore.Duration(1, TimeUnit.DAYS), 7);
        // Set start time to be Sunday a week ago
        long sunday = dayOfWeek(Calendar.SUNDAY);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(sunday);
        cal.add(Calendar.WEEK_OF_MONTH, (-1));
        groupBy.setStartDate(cal.getTimeInMillis());
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfWeek(Calendar.SUNDAY), 1), tags), Matchers.equalTo(0));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfWeek(Calendar.MONDAY), 1), tags), Matchers.equalTo(1));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfWeek(Calendar.TUESDAY), 1), tags), Matchers.equalTo(2));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfWeek(Calendar.WEDNESDAY), 1), tags), Matchers.equalTo(3));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfWeek(Calendar.THURSDAY), 1), tags), Matchers.equalTo(4));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfWeek(Calendar.FRIDAY), 1), tags), Matchers.equalTo(5));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfWeek(Calendar.SATURDAY), 1), tags), Matchers.equalTo(6));
    }

    @Test
    public void test_getGroupId_Month() {
        Map<String, String> tags = new HashMap<String, String>();
        TimeGroupBy groupBy = new TimeGroupBy(new org.kairosdb.core.datastore.Duration(1, TimeUnit.MONTHS), 24);
        // Set start time to Jan 1, 2010 - 1 am
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2010, Calendar.JANUARY, 1, 1, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        groupBy.setStartDate(cal.getTimeInMillis());
        // 2010
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.JANUARY, 1), 1), tags), Matchers.equalTo(0));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.JANUARY, 31), 1), tags), Matchers.equalTo(0));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.FEBRUARY, 1), 1), tags), Matchers.equalTo(1));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.FEBRUARY, 28), 1), tags), Matchers.equalTo(1));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.MARCH, 1), 1), tags), Matchers.equalTo(2));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.MARCH, 31), 1), tags), Matchers.equalTo(2));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.JULY, 1), 1), tags), Matchers.equalTo(6));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.JULY, 31), 1), tags), Matchers.equalTo(6));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.DECEMBER, 1), 1), tags), Matchers.equalTo(11));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2010, Calendar.DECEMBER, 31), 1), tags), Matchers.equalTo(11));
        // 2011
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2011, Calendar.JANUARY, 31), 1), tags), Matchers.equalTo(12));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2011, Calendar.FEBRUARY, 28), 1), tags), Matchers.equalTo(13));
        // 2012
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2012, Calendar.JANUARY, 31), 1), tags), Matchers.equalTo(0));
        MatcherAssert.assertThat(groupBy.getGroupId(new LongDataPoint(dayOfMonth(2012, Calendar.FEBRUARY, 28), 1), tags), Matchers.equalTo(1));
    }
}

