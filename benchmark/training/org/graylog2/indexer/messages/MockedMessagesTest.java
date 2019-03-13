/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.indexer.messages;


import BulkResult.BulkResultItem;
import DateTimeZone.UTC;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.searchbox.client.JestClient;
import io.searchbox.core.BulkResult;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.graylog2.indexer.IndexSet;
import org.graylog2.plugin.Message;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static java.util.AbstractMap.SimpleEntry.<init>;


public class MockedMessagesTest {
    public static class MockedBulkResult extends BulkResult {
        MockedBulkResult() {
            super(((ObjectMapper) (null)));
        }

        class MockedBulkResultItem extends BulkResult.BulkResultItem {
            MockedBulkResultItem(String operation, String index, String type, String id, int status, String error, Integer version, String errorType, String errorReason) {
                super(operation, index, type, id, status, error, version, errorType, errorReason);
            }
        }

        MockedMessagesTest.MockedBulkResult.MockedBulkResultItem createResultItem(String operation, String index, String type, String id, int status, String error, Integer version, String errorType, String errorReason) {
            return new MockedMessagesTest.MockedBulkResult.MockedBulkResultItem(operation, index, type, id, status, error, version, errorType, errorReason);
        }
    }

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private JestClient jestClient;

    private Messages messages;

    @Test
    public void bulkIndexingShouldNotDoAnythingForEmptyList() throws Exception {
        final List<String> result = messages.bulkIndex(Collections.emptyList());
        assertThat(result).isNotNull().isEmpty();
        Mockito.verify(jestClient, Mockito.never()).execute(ArgumentMatchers.any());
    }

    @Test
    public void bulkIndexingShouldRetry() throws Exception {
        final BulkResult jestResult = Mockito.mock(BulkResult.class);
        Mockito.when(jestResult.isSucceeded()).thenReturn(true);
        Mockito.when(jestResult.getFailedItems()).thenReturn(Collections.emptyList());
        Mockito.when(jestClient.execute(ArgumentMatchers.any())).thenThrow(new IOException("Boom!")).thenReturn(jestResult);
        final List<Map.Entry<IndexSet, Message>> messageList = ImmutableList.of(new AbstractMap.SimpleEntry(Mockito.mock(IndexSet.class), Mockito.mock(Message.class)));
        final List<String> result = messages.bulkIndex(messageList);
        assertThat(result).isNotNull().isEmpty();
        Mockito.verify(jestClient, Mockito.times(2)).execute(ArgumentMatchers.any());
    }

    @Test
    public void bulkIndexingShouldNotRetryForIndexMappingErrors() throws Exception {
        final String messageId = "BOOMID";
        final BulkResult jestResult = Mockito.mock(BulkResult.class);
        final BulkResult.BulkResultItem bulkResultItem = new MockedMessagesTest.MockedBulkResult().createResultItem("index", "someindex", "message", messageId, 400, "{\"type\":\"mapper_parsing_exception\",\"reason\":\"failed to parse [http_response_code]\",\"caused_by\":{\"type\":\"number_format_exception\",\"reason\":\"For input string: \\\"FOOBAR\\\"\"}}", null, "mapper_parsing_exception", "failed to parse [http_response_code]");
        Mockito.when(jestResult.isSucceeded()).thenReturn(false);
        Mockito.when(jestResult.getFailedItems()).thenReturn(ImmutableList.of(bulkResultItem));
        Mockito.when(jestClient.execute(ArgumentMatchers.any())).thenReturn(jestResult).thenThrow(new IllegalStateException("JestResult#execute should not be called twice."));
        final Message mockedMessage = Mockito.mock(Message.class);
        Mockito.when(mockedMessage.getId()).thenReturn(messageId);
        Mockito.when(mockedMessage.getTimestamp()).thenReturn(DateTime.now(UTC));
        final List<Map.Entry<IndexSet, Message>> messageList = ImmutableList.of(new AbstractMap.SimpleEntry(Mockito.mock(IndexSet.class), mockedMessage));
        final List<String> result = messages.bulkIndex(messageList);
        assertThat(result).hasSize(1);
        Mockito.verify(jestClient, Mockito.times(1)).execute(ArgumentMatchers.any());
    }
}

