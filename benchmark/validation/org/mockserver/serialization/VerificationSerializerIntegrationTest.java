package org.mockserver.serialization;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpRequest;
import org.mockserver.serialization.model.VerificationDTO;
import org.mockserver.verify.Verification;
import org.mockserver.verify.VerificationTimes;


/**
 *
 *
 * @author jamesdbloom
 */
public class VerificationSerializerIntegrationTest {
    @Test
    public void shouldDeserializeCompleteObject() throws IOException {
        // given
        String requestBytes = ((((((((((((((((("{" + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"method\" : \"GET\",") + (NEW_LINE)) + "    \"path\" : \"somepath\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"atLeast\" : 2,") + (NEW_LINE)) + "    \"atMost\" : 3") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        Verification verification = deserialize(requestBytes);
        // then
        Assert.assertEquals(new VerificationDTO().setHttpRequest(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("somepath"))).setTimes(new org.mockserver.serialization.model.VerificationTimesDTO(VerificationTimes.between(2, 3))).buildObject(), verification);
    }

    @Test
    public void shouldDeserializePartialObject() throws IOException {
        // given
        String requestBytes = ((((("{" + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        Verification verification = deserialize(requestBytes);
        // then
        Assert.assertEquals(new VerificationDTO().setHttpRequest(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request())).buildObject(), verification);
    }

    @Test
    public void shouldSerializeCompleteObject() throws IOException {
        // when
        String jsonExpectation = new VerificationSerializer(new MockServerLogger()).serialize(new VerificationDTO().setHttpRequest(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("somepath"))).setTimes(new org.mockserver.serialization.model.VerificationTimesDTO(VerificationTimes.between(2, 3))).buildObject());
        // then
        Assert.assertEquals((((((((((((((((((("{" + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"method\" : \"GET\",") + (NEW_LINE)) + "    \"path\" : \"somepath\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"atLeast\" : 2,") + (NEW_LINE)) + "    \"atMost\" : 3") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonExpectation);
    }

    @Test
    public void shouldSerializePartialObject() throws IOException {
        // when
        String jsonExpectation = new VerificationSerializer(new MockServerLogger()).serialize(new VerificationDTO().setHttpRequest(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request())).buildObject());
        // then
        Assert.assertEquals((((((((((((("{" + (NEW_LINE)) + "  \"httpRequest\" : { },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"atLeast\" : 1,") + (NEW_LINE)) + "    \"atMost\" : 1") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonExpectation);
    }
}

