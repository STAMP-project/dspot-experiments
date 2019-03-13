package org.mockserver.serialization;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.serialization.model.VerificationDTO;
import org.mockserver.verify.Verification;


/**
 *
 *
 * @author jamesdbloom
 */
public class VerificationSerializationErrorsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @InjectMocks
    private VerificationSerializer verificationSerializer;

    @Test
    public void shouldHandleExceptionWhileSerializingObject() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage((((((((((("Exception while serializing verification to JSON with value {" + (NEW_LINE)) + "  \"httpRequest\" : { },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"atLeast\" : 1") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"));
        // and
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(objectWriter.writeValueAsString(ArgumentMatchers.any(VerificationDTO.class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        verificationSerializer.serialize(new Verification());
    }

    @Test
    public void shouldHandleExceptionWhileDeserializingObject() throws IOException {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("JsonParseException - Unrecognized token 'requestBytes': was expecting ('true', 'false' or 'null')");
        // when
        verificationSerializer.deserialize("requestBytes");
    }
}

