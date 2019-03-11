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


import com.google.common.collect.Iterables;
import java.util.Set;
import org.graylog2.database.NotFoundException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.outputs.MessageOutput;
import org.graylog2.plugin.outputs.MessageOutputConfigurationException;
import org.graylog2.plugin.streams.Output;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.streams.OutputService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class OutputRegistryTest {
    private static final long FAULT_COUNT_THRESHOLD = 5;

    private static final long FAULT_PENALTY_SECONDS = 30;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MessageOutput messageOutput;

    @Mock
    private MessageOutputFactory messageOutputFactory;

    @Mock
    private Output output;

    @Mock
    private OutputService outputService;

    @Test
    public void testMessageOutputsIncludesDefault() {
        OutputRegistry registry = new OutputRegistry(messageOutput, null, null, null, null, OutputRegistryTest.FAULT_COUNT_THRESHOLD, OutputRegistryTest.FAULT_PENALTY_SECONDS);
        Set<MessageOutput> outputs = registry.getMessageOutputs();
        Assert.assertSame("we should only have the default MessageOutput", Iterables.getOnlyElement(outputs, null), messageOutput);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionForUnknownOutputType() throws MessageOutputConfigurationException {
        OutputRegistry registry = new OutputRegistry(null, null, messageOutputFactory, null, null, OutputRegistryTest.FAULT_COUNT_THRESHOLD, OutputRegistryTest.FAULT_PENALTY_SECONDS);
        registry.launchOutput(output, null);
    }

    @Test
    public void testLaunchNewOutput() throws Exception {
        final String outputId = "foobar";
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(messageOutputFactory.fromStreamOutput(ArgumentMatchers.eq(output), ArgumentMatchers.eq(stream), ArgumentMatchers.any(Configuration.class))).thenReturn(messageOutput);
        Mockito.when(outputService.load(ArgumentMatchers.eq(outputId))).thenReturn(output);
        final OutputRegistry outputRegistry = new OutputRegistry(null, outputService, messageOutputFactory, null, null, OutputRegistryTest.FAULT_COUNT_THRESHOLD, OutputRegistryTest.FAULT_PENALTY_SECONDS);
        Assert.assertEquals(0, outputRegistry.getRunningMessageOutputs().size());
        MessageOutput result = outputRegistry.getOutputForIdAndStream(outputId, stream);
        Assert.assertSame(result, messageOutput);
        Assert.assertNotNull(outputRegistry.getRunningMessageOutputs());
        Assert.assertEquals(1, outputRegistry.getRunningMessageOutputs().size());
    }

    @Test
    public void testNonExistingInput() throws Exception {
        final String outputId = "foobar";
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(outputService.load(ArgumentMatchers.eq(outputId))).thenThrow(NotFoundException.class);
        final OutputRegistry outputRegistry = new OutputRegistry(null, outputService, null, null, null, OutputRegistryTest.FAULT_COUNT_THRESHOLD, OutputRegistryTest.FAULT_PENALTY_SECONDS);
        MessageOutput messageOutput = outputRegistry.getOutputForIdAndStream(outputId, stream);
        Assert.assertNull(messageOutput);
        Assert.assertEquals(0, outputRegistry.getRunningMessageOutputs().size());
    }

    @Test
    public void testInvalidOutputConfiguration() throws Exception {
        final String outputId = "foobar";
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(messageOutputFactory.fromStreamOutput(ArgumentMatchers.eq(output), ArgumentMatchers.any(Stream.class), ArgumentMatchers.any(Configuration.class))).thenThrow(new MessageOutputConfigurationException());
        Mockito.when(outputService.load(ArgumentMatchers.eq(outputId))).thenReturn(output);
        final OutputRegistry outputRegistry = new OutputRegistry(null, outputService, messageOutputFactory, null, null, OutputRegistryTest.FAULT_COUNT_THRESHOLD, OutputRegistryTest.FAULT_PENALTY_SECONDS);
        Assert.assertEquals(0, outputRegistry.getRunningMessageOutputs().size());
        MessageOutput result = outputRegistry.getOutputForIdAndStream(outputId, stream);
        Assert.assertNull(result);
        Assert.assertEquals(0, outputRegistry.getRunningMessageOutputs().size());
    }
}

