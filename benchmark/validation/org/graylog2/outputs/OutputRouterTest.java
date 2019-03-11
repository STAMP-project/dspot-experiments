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


import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.outputs.MessageOutput;
import org.graylog2.plugin.streams.Output;
import org.graylog2.plugin.streams.Stream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class OutputRouterTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MessageOutput defaultMessageOutput;

    @Mock
    private OutputRegistry outputRegistry;

    @Test
    public void testAlwaysIncludeDefaultOutput() throws Exception {
        final Message message = Mockito.mock(Message.class);
        final OutputRouter outputRouter = new OutputRouter(defaultMessageOutput, outputRegistry);
        final Collection<MessageOutput> messageOutputs = outputRouter.getOutputsForMessage(message);
        Assert.assertEquals(1, messageOutputs.size());
        Assert.assertTrue(messageOutputs.contains(defaultMessageOutput));
    }

    @Test
    public void testGetMessageOutputsForEmptyStream() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final OutputRouter outputRouter = new OutputRouter(defaultMessageOutput, outputRegistry);
        final Collection<MessageOutput> messageOutputs = outputRouter.getMessageOutputsForStream(stream);
        Assert.assertEquals(0, messageOutputs.size());
    }

    @Test
    public void testGetMessageOutputsForSingleStream() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final Output output = Mockito.mock(Output.class);
        final String outputId = "foobar";
        final MessageOutput messageOutput = Mockito.mock(MessageOutput.class);
        final Set<Output> outputSet = ImmutableSet.of(output);
        Mockito.when(stream.getOutputs()).thenReturn(outputSet);
        Mockito.when(output.getId()).thenReturn(outputId);
        Mockito.when(outputRegistry.getOutputForIdAndStream(ArgumentMatchers.eq(outputId), ArgumentMatchers.eq(stream))).thenReturn(messageOutput);
        final OutputRouter outputRouter = new OutputRouter(defaultMessageOutput, outputRegistry);
        final Collection<MessageOutput> messageOutputs = outputRouter.getMessageOutputsForStream(stream);
        Assert.assertEquals(1, messageOutputs.size());
        Assert.assertTrue(messageOutputs.contains(messageOutput));
    }

    @Test
    public void testGetMessageOutputsForStreamWithTwoOutputs() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final Output output1 = Mockito.mock(Output.class);
        final Output output2 = Mockito.mock(Output.class);
        final String output1Id = "foo";
        final String output2Id = "bar";
        final MessageOutput messageOutput1 = Mockito.mock(MessageOutput.class);
        final MessageOutput messageOutput2 = Mockito.mock(MessageOutput.class);
        final Set<Output> outputSet = ImmutableSet.of(output1, output2);
        Mockito.when(stream.getOutputs()).thenReturn(outputSet);
        Mockito.when(output1.getId()).thenReturn(output1Id);
        Mockito.when(output2.getId()).thenReturn(output2Id);
        Mockito.when(outputRegistry.getOutputForIdAndStream(ArgumentMatchers.eq(output1Id), ArgumentMatchers.eq(stream))).thenReturn(messageOutput1);
        Mockito.when(outputRegistry.getOutputForIdAndStream(ArgumentMatchers.eq(output2Id), ArgumentMatchers.eq(stream))).thenReturn(messageOutput2);
        final OutputRouter outputRouter = new OutputRouter(defaultMessageOutput, outputRegistry);
        final Collection<MessageOutput> messageOutputs = outputRouter.getMessageOutputsForStream(stream);
        Assert.assertEquals(2, messageOutputs.size());
        Assert.assertTrue(messageOutputs.contains(messageOutput1));
        Assert.assertTrue(messageOutputs.contains(messageOutput2));
    }

    @Test
    public void testGetOutputFromSingleStreams() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final Message message = Mockito.mock(Message.class);
        Mockito.when(message.getStreams()).thenReturn(ImmutableSet.of(stream));
        final MessageOutput messageOutput = Mockito.mock(MessageOutput.class);
        final Set<MessageOutput> messageOutputList = ImmutableSet.of(messageOutput);
        final OutputRouter outputRouter = Mockito.spy(new OutputRouter(defaultMessageOutput, outputRegistry));
        Mockito.doReturn(messageOutputList).when(outputRouter).getMessageOutputsForStream(ArgumentMatchers.eq(stream));
        // Call to test
        final Collection<MessageOutput> messageOutputs = outputRouter.getOutputsForMessage(message);
        // Verification
        Assert.assertEquals(2, messageOutputs.size());
        Assert.assertTrue(messageOutputs.contains(defaultMessageOutput));
        Assert.assertTrue(messageOutputs.contains(messageOutput));
    }

    @Test
    public void testGetOutputsFromTwoStreams() throws Exception {
        final Stream stream1 = Mockito.mock(Stream.class);
        final Stream stream2 = Mockito.mock(Stream.class);
        final MessageOutput messageOutput1 = Mockito.mock(MessageOutput.class);
        final Set<MessageOutput> messageOutputSet1 = ImmutableSet.of(messageOutput1);
        final MessageOutput messageOutput2 = Mockito.mock(MessageOutput.class);
        final Set<MessageOutput> messageOutputSet2 = ImmutableSet.of(messageOutput2);
        final Message message = Mockito.mock(Message.class);
        Mockito.when(message.getStreams()).thenReturn(ImmutableSet.of(stream1, stream2));
        OutputRouter outputRouter = Mockito.spy(new OutputRouter(defaultMessageOutput, outputRegistry));
        Mockito.doReturn(messageOutputSet1).when(outputRouter).getMessageOutputsForStream(ArgumentMatchers.eq(stream1));
        Mockito.doReturn(messageOutputSet2).when(outputRouter).getMessageOutputsForStream(ArgumentMatchers.eq(stream2));
        final Collection<MessageOutput> result = outputRouter.getOutputsForMessage(message);
        Assert.assertEquals(3, result.size());
        Assert.assertTrue(result.contains(defaultMessageOutput));
        Assert.assertTrue(result.contains(messageOutput1));
        Assert.assertTrue(result.contains(messageOutput2));
    }

    @Test
    public void testGetOutputsWithIdenticalMessageOutputs() throws Exception {
        final Stream stream1 = Mockito.mock(Stream.class);
        final Stream stream2 = Mockito.mock(Stream.class);
        final MessageOutput messageOutput = Mockito.mock(MessageOutput.class);
        final Set<MessageOutput> messageOutputSet = ImmutableSet.of(messageOutput);
        final Message message = Mockito.mock(Message.class);
        Mockito.when(message.getStreams()).thenReturn(ImmutableSet.of(stream1, stream2));
        OutputRouter outputRouter = Mockito.spy(new OutputRouter(defaultMessageOutput, outputRegistry));
        Mockito.doReturn(messageOutputSet).when(outputRouter).getMessageOutputsForStream(ArgumentMatchers.eq(stream1));
        Mockito.doReturn(messageOutputSet).when(outputRouter).getMessageOutputsForStream(ArgumentMatchers.eq(stream2));
        final Collection<MessageOutput> result = outputRouter.getOutputsForMessage(message);
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(defaultMessageOutput));
        Assert.assertTrue(result.contains(messageOutput));
    }
}

