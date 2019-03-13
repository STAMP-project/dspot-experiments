package org.mockserver.serialization;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.model.HttpRequest;
import org.mockserver.serialization.model.HttpRequestDTO;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpRequestSerializationErrorsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @InjectMocks
    private HttpRequestSerializer httpRequestSerializer;

    @Test
    public void shouldHandleExceptionWhileSerializingObject() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Exception while serializing HttpRequest to JSON with value { }");
        // and
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(objectWriter.writeValueAsString(ArgumentMatchers.any(HttpRequestDTO.class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        httpRequestSerializer.serialize(new HttpRequest());
    }

    @Test
    public void shouldHandleExceptionWhileSerializingArray() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Exception while serializing HttpRequest to JSON with value [{ }]");
        // and
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(objectWriter.writeValueAsString(ArgumentMatchers.any(HttpRequestDTO[].class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        httpRequestSerializer.serialize(new HttpRequest[]{ new HttpRequest() });
    }

    @Test
    public void shouldHandleNullAndEmptyWhileSerializingArray() {
        // when
        Assert.assertEquals("[]", httpRequestSerializer.serialize(new HttpRequest[]{  }));
        Assert.assertEquals("[]", httpRequestSerializer.serialize(((HttpRequest[]) (null))));
    }

    @Test
    public void shouldHandleExceptionWhileDeserializingObject() {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("JsonParseException - Unrecognized token 'requestBytes': was expecting ('true', 'false' or 'null')");
        // when
        httpRequestSerializer.deserialize("requestBytes");
    }

    @Test
    public void shouldHandleExceptionWhileDeserializingArray() {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(("com.fasterxml.jackson.core.JsonParseException: Unrecognized token \'requestBytes\': was expecting (\'true\', \'false\' or \'null\')\n" + " at [Source: (String)\"requestBytes\"; line: 1, column: 25]"));
        // when
        httpRequestSerializer.deserializeArray("requestBytes");
    }

    @Test
    public void shouldValidateInputForArray() {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage((("1 error:" + (NEW_LINE)) + " - a request or request array is required but value was \"\""));
        // when
        Assert.assertArrayEquals(new HttpRequest[]{  }, httpRequestSerializer.deserializeArray(""));
    }
}

