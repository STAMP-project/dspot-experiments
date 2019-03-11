package org.mockserver.serialization;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.model.PortBinding;


/**
 *
 *
 * @author jamesdbloom
 */
public class PortBindingSerializerTest {
    private final PortBinding fullPortBinding = new PortBinding().setPorts(Arrays.asList(1, 2, 3, 4, 5));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @InjectMocks
    private PortBindingSerializer portBindingSerializer;

    @Test
    public void deserialize() throws IOException {
        // given
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("requestBytes"), ArgumentMatchers.same(PortBinding.class))).thenReturn(fullPortBinding);
        // when
        PortBinding portBinding = portBindingSerializer.deserialize("requestBytes");
        // then
        Assert.assertEquals(fullPortBinding, portBinding);
    }

    @Test
    public void deserializeHandleException() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Exception while parsing PortBinding for [requestBytes]");
        // and
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("requestBytes"), ArgumentMatchers.same(PortBinding.class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        portBindingSerializer.deserialize("requestBytes");
    }

    @Test
    public void serialize() throws IOException {
        // given
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        // when
        portBindingSerializer.serialize(fullPortBinding);
        // then
        Mockito.verify(objectMapper).writerWithDefaultPrettyPrinter();
        Mockito.verify(objectWriter).writeValueAsString(fullPortBinding);
    }

    @Test
    public void serializeObjectHandlesException() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Exception while serializing portBinding to JSON with value { }");
        // and
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(objectWriter.writeValueAsString(ArgumentMatchers.any(PortBinding.class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        portBindingSerializer.serialize(PortBinding.portBinding());
    }
}

