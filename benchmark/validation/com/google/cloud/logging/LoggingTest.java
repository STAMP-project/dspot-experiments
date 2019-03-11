/**
 * Copyright 2016 Google LLC
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
package com.google.cloud.logging;


import EntryListOption.OptionType.FILTER;
import EntryListOption.OptionType.ORDER_BY;
import ListOption.OptionType.PAGE_SIZE;
import ListOption.OptionType.PAGE_TOKEN;
import SortingField.TIMESTAMP;
import SortingOrder.ASCENDING;
import SortingOrder.DESCENDING;
import WriteOption.OptionType.LABELS;
import WriteOption.OptionType.LOG_NAME;
import WriteOption.OptionType.RESOURCE;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.Logging.ListOption;
import com.google.common.collect.ImmutableMap;
import com.google.logging.v2.ListLogEntriesRequest;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class LoggingTest {
    private static final int PAGE_SIZE = 42;

    private static final String PAGE_TOKEN = "page token";

    private static final String FILTER = "filter";

    private static final Map<String, String> LABELS = ImmutableMap.of("key", "value");

    private static final String LOG_NAME = "logName";

    private static final MonitoredResource RESOURCE = MonitoredResource.of("global", ImmutableMap.of("project_id", "p"));

    @Test
    public void testListOption() {
        // page token
        ListOption listOption = ListOption.pageToken(LoggingTest.PAGE_TOKEN);
        Assert.assertEquals(LoggingTest.PAGE_TOKEN, listOption.getValue());
        Assert.assertEquals(ListOption.OptionType.PAGE_TOKEN, listOption.getOptionType());
        // page size
        listOption = ListOption.pageSize(LoggingTest.PAGE_SIZE);
        Assert.assertEquals(LoggingTest.PAGE_SIZE, listOption.getValue());
        Assert.assertEquals(ListOption.OptionType.PAGE_SIZE, listOption.getOptionType());
    }

    @Test
    public void testEntryListOption() {
        Logging.EntryListOption listOption = Logging.EntryListOption.pageToken(LoggingTest.PAGE_TOKEN);
        Assert.assertEquals(LoggingTest.PAGE_TOKEN, listOption.getValue());
        Assert.assertEquals(ListOption.OptionType.PAGE_TOKEN, listOption.getOptionType());
        // page size
        listOption = Logging.EntryListOption.pageSize(LoggingTest.PAGE_SIZE);
        Assert.assertEquals(LoggingTest.PAGE_SIZE, listOption.getValue());
        Assert.assertEquals(ListOption.OptionType.PAGE_SIZE, listOption.getOptionType());
        // filter
        listOption = Logging.EntryListOption.filter(LoggingTest.FILTER);
        Assert.assertEquals(LoggingTest.FILTER, listOption.getValue());
        Assert.assertEquals(EntryListOption.OptionType.FILTER, listOption.getOptionType());
        // sort order
        listOption = Logging.EntryListOption.sortOrder(TIMESTAMP, ASCENDING);
        Assert.assertEquals("timestamp asc", listOption.getValue());
        Assert.assertEquals(ORDER_BY, listOption.getOptionType());
        listOption = Logging.EntryListOption.sortOrder(TIMESTAMP, DESCENDING);
        Assert.assertEquals("timestamp desc", listOption.getValue());
        Assert.assertEquals(ORDER_BY, listOption.getOptionType());
        ListLogEntriesRequest request = LoggingImpl.listLogEntriesRequest("some-project-id", LoggingImpl.optionMap(Logging.EntryListOption.pageToken(LoggingTest.PAGE_TOKEN), Logging.EntryListOption.pageSize(LoggingTest.PAGE_SIZE)));
        assertThat(request.getPageToken()).isEqualTo(LoggingTest.PAGE_TOKEN);
        assertThat(request.getPageSize()).isEqualTo(LoggingTest.PAGE_SIZE);
    }

    @Test
    public void testWriteOption() {
        Logging.WriteOption writeOption = Logging.WriteOption.labels(LoggingTest.LABELS);
        Assert.assertEquals(LoggingTest.LABELS, writeOption.getValue());
        Assert.assertEquals(WriteOption.OptionType.LABELS, writeOption.getOptionType());
        writeOption = Logging.WriteOption.logName(LoggingTest.LOG_NAME);
        Assert.assertEquals(LoggingTest.LOG_NAME, writeOption.getValue());
        Assert.assertEquals(WriteOption.OptionType.LOG_NAME, writeOption.getOptionType());
        writeOption = Logging.WriteOption.resource(LoggingTest.RESOURCE);
        Assert.assertEquals(LoggingTest.RESOURCE, writeOption.getValue());
        Assert.assertEquals(WriteOption.OptionType.RESOURCE, writeOption.getOptionType());
    }
}

