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
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.StringBody;
import org.mockserver.serialization.model.VerificationDTO;
import org.mockserver.validator.jsonschema.JsonSchemaVerificationValidator;
import org.mockserver.verify.Verification;
import org.mockserver.verify.VerificationTimes;


public class VerificationSerializerTest {
    private final HttpRequest request = HttpRequest.request().withMethod("GET").withPath("somepath").withBody(new StringBody("somebody")).withHeaders(new Header("headerName", "headerValue")).withCookies(new Cookie("cookieName", "cookieValue"));

    private final VerificationTimes times = VerificationTimes.atLeast(2);

    private final Verification fullVerification = Verification.verification().withRequest(request).withTimes(times);

    private final VerificationDTO fullVerificationDTO = new VerificationDTO().setHttpRequest(new org.mockserver.serialization.model.HttpRequestDTO(request)).setTimes(new org.mockserver.serialization.model.VerificationTimesDTO(times));

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @Mock
    private JsonSchemaVerificationValidator verificationValidator;

    @InjectMocks
    private VerificationSerializer verificationSerializer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void deserialize() throws IOException {
        // given
        Mockito.when(verificationValidator.isValid(ArgumentMatchers.eq("requestBytes"))).thenReturn("");
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("requestBytes"), ArgumentMatchers.same(VerificationDTO.class))).thenReturn(fullVerificationDTO);
        // when
        Verification verification = verificationSerializer.deserialize("requestBytes");
        // then
        Assert.assertEquals(fullVerification, verification);
    }

    @Test
    public void serialize() throws IOException {
        // given
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        // when
        verificationSerializer.serialize(fullVerification);
        // then
        Mockito.verify(objectMapper).writerWithDefaultPrettyPrinter();
        Mockito.verify(objectWriter).writeValueAsString(fullVerificationDTO);
    }

    @Test
    public void serializeHandlesException() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage((((((((((("Exception while serializing verification to JSON with value {" + (NEW_LINE)) + "  \"httpRequest\" : { },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"atLeast\" : 1") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"));
        // and
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(objectWriter.writeValueAsString(ArgumentMatchers.any(VerificationDTO.class))).thenThrow(new RuntimeException("TEST EXCEPTION"));
        // when
        verificationSerializer.serialize(new Verification());
    }
}

