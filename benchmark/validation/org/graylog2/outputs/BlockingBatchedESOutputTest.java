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
package org.graylog2.outputs;


import com.codahale.metrics.MetricRegistry;
import java.util.List;
import java.util.Map;
import org.graylog2.Configuration;
import org.graylog2.indexer.IndexSet;
import org.graylog2.indexer.messages.Messages;
import org.graylog2.plugin.Message;
import org.graylog2.shared.journal.NoopJournal;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class BlockingBatchedESOutputTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private MetricRegistry metricRegistry;

    private NoopJournal journal;

    private Configuration config;

    @Mock
    private Messages messages;

    @Test
    public void write() throws Exception {
        final BlockingBatchedESOutput output = new BlockingBatchedESOutput(metricRegistry, messages, config, journal);
        final List<Map.Entry<IndexSet, Message>> messageList = buildMessages(config.getOutputBatchSize());
        for (Map.Entry<IndexSet, Message> entry : messageList) {
            output.writeMessageEntry(entry);
        }
        Mockito.verify(messages, Mockito.times(1)).bulkIndex(ArgumentMatchers.eq(messageList));
    }

    @Test
    public void forceFlushIfTimedOut() throws Exception {
        final BlockingBatchedESOutput output = new BlockingBatchedESOutput(metricRegistry, messages, config, journal);
        final List<Map.Entry<IndexSet, Message>> messageList = buildMessages(((config.getOutputBatchSize()) - 1));
        for (Map.Entry<IndexSet, Message> entry : messageList) {
            output.writeMessageEntry(entry);
        }
        // Should flush the buffer even though the batch size is not reached yet
        output.forceFlushIfTimedout();
        Mockito.verify(messages, Mockito.times(1)).bulkIndex(ArgumentMatchers.eq(messageList));
    }
}

