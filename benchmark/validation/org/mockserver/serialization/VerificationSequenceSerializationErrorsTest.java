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
import org.mockserver.serialization.model.VerificationSequenceDTO;
import org.mockserver.verify.VerificationSequence;


/**
 *
 *
 * @author jamesdbloom
 */
public class VerificationSequenceSerializationErrorsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @InjectMocks
    private VerificationSequenceSerializer verificationSequenceSerializer;

    @Test
    public void shouldHandleExceptionWhileSerializingObject() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Exception while serializing verificationSequence to JSON with value { }");
        // and
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(objectWriter.writeValueAsString(ArgumentMatchers.any(VerificationSequenceDTO.class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        verificationSequenceSerializer.serialize(new VerificationSequence());
    }

    @Test
    public void shouldHandleExceptionWhileDeserializingObject() throws IOException {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("JsonParseException - Unrecognized token 'requestBytes': was expecting ('true', 'false' or 'null')");
        // when
        verificationSequenceSerializer.deserialize("requestBytes");
    }
}

