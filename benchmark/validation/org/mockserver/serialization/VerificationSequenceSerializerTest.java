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
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.StringBody;
import org.mockserver.serialization.model.VerificationSequenceDTO;
import org.mockserver.validator.jsonschema.JsonSchemaVerificationSequenceValidator;
import org.mockserver.verify.VerificationSequence;


public class VerificationSequenceSerializerTest {
    private final HttpRequest requestOne = HttpRequest.request().withMethod("GET").withPath("some_path_one").withBody(new StringBody("some_body_one")).withHeaders(new Header("header_name_two", "header_value_two"));

    private final HttpRequest requestTwo = HttpRequest.request().withMethod("GET").withPath("some_path_two").withBody(new StringBody("some_body_two")).withHeaders(new Header("header_name_one", "header_value_one"));

    private final VerificationSequence fullVerificationSequence = new VerificationSequence().withRequests(requestOne);

    private final VerificationSequenceDTO fullVerificationSequenceDTO = new VerificationSequenceDTO(fullVerificationSequence);

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @Mock
    private JsonSchemaVerificationSequenceValidator verificationSequenceValidator;

    @InjectMocks
    private VerificationSequenceSerializer verificationSequenceSerializer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void deserialize() throws IOException {
        // given
        Mockito.when(verificationSequenceValidator.isValid(ArgumentMatchers.eq("requestBytes"))).thenReturn("");
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("requestBytes"), ArgumentMatchers.same(VerificationSequenceDTO.class))).thenReturn(fullVerificationSequenceDTO);
        // when
        VerificationSequence verification = verificationSequenceSerializer.deserialize("requestBytes");
        // then
        Assert.assertEquals(fullVerificationSequence, verification);
    }

    @Test
    public void serialize() throws IOException {
        // given
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        // when
        verificationSequenceSerializer.serialize(fullVerificationSequence);
        // then
        Mockito.verify(objectMapper).writerWithDefaultPrettyPrinter();
        Mockito.verify(objectWriter).writeValueAsString(fullVerificationSequenceDTO);
    }

    @Test
    public void serializeHandlesException() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Exception while serializing verificationSequence to JSON with value { }");
        // and
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(objectWriter.writeValueAsString(ArgumentMatchers.any(VerificationSequenceDTO.class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        verificationSequenceSerializer.serialize(new VerificationSequence());
    }
}

