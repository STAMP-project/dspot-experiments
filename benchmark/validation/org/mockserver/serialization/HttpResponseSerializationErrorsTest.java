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
import org.mockserver.model.HttpResponse;
import org.mockserver.serialization.model.HttpResponseDTO;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpResponseSerializationErrorsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @InjectMocks
    private HttpResponseSerializer httpResponseSerializer;

    @Test
    public void shouldHandleExceptionWhileSerializingObject() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Exception while serializing httpResponse to JSON with value { }");
        // and
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(objectWriter.writeValueAsString(ArgumentMatchers.any(HttpResponseDTO.class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        httpResponseSerializer.serialize(new HttpResponse());
    }

    @Test
    public void shouldHandleExceptionWhileSerializingArray() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Exception while serializing HttpResponse to JSON with value [{ }]");
        // and
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(objectWriter.writeValueAsString(ArgumentMatchers.any(HttpResponseDTO[].class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        httpResponseSerializer.serialize(new HttpResponse[]{ new HttpResponse() });
    }

    @Test
    public void shouldHandleNullAndEmptyWhileSerializingArray() {
        // when
        Assert.assertEquals("[]", httpResponseSerializer.serialize(new HttpResponse[]{  }));
        Assert.assertEquals("[]", httpResponseSerializer.serialize(((HttpResponse[]) (null))));
    }

    @Test
    public void shouldHandleExceptionWhileDeserializingObject() {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("JsonParseException - Unrecognized token 'responseBytes': was expecting ('true', 'false' or 'null')");
        // when
        httpResponseSerializer.deserialize("responseBytes");
    }

    @Test
    public void shouldHandleExceptionWhileDeserializingArray() {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(("com.fasterxml.jackson.core.JsonParseException: Unrecognized token \'responseBytes\': was expecting (\'true\', \'false\' or \'null\')\n" + " at [Source: (String)\"responseBytes\"; line: 1, column: 27]"));
        // when
        httpResponseSerializer.deserializeArray("responseBytes");
    }

    @Test
    public void shouldValidateInputForArray() {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage((("1 error:" + (NEW_LINE)) + " - a response or response array is required but value was \"\""));
        // when
        Assert.assertArrayEquals(new HttpResponse[]{  }, httpResponseSerializer.deserializeArray(""));
    }
}

