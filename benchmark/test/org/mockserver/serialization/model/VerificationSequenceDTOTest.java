package org.mockserver.serialization.model;


import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationSequence;


public class VerificationSequenceDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        VerificationSequence verification = new VerificationSequence().withRequests(HttpRequest.request("one"), HttpRequest.request("two"), HttpRequest.request("three"));
        // when
        VerificationSequenceDTO verificationSequenceDTO = new VerificationSequenceDTO(verification);
        // then
        MatcherAssert.assertThat(verificationSequenceDTO.getHttpRequests(), Is.is(Arrays.asList(new HttpRequestDTO(HttpRequest.request("one")), new HttpRequestDTO(HttpRequest.request("two")), new HttpRequestDTO(HttpRequest.request("three")))));
    }

    @Test
    public void shouldBuildObject() {
        // given
        VerificationSequence verification = new VerificationSequence().withRequests(HttpRequest.request("one"), HttpRequest.request("two"), HttpRequest.request("three"));
        // when
        VerificationSequence builtVerification = buildObject();
        // then
        MatcherAssert.assertThat(builtVerification.getHttpRequests(), Is.is(Arrays.asList(HttpRequest.request("one"), HttpRequest.request("two"), HttpRequest.request("three"))));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        VerificationSequence verification = new VerificationSequence();
        // when
        VerificationSequenceDTO verificationSequenceDTO = new VerificationSequenceDTO(verification);
        verificationSequenceDTO.setHttpRequests(Arrays.asList(new HttpRequestDTO(HttpRequest.request("one")), new HttpRequestDTO(HttpRequest.request("two")), new HttpRequestDTO(HttpRequest.request("three"))));
        // then
        MatcherAssert.assertThat(verificationSequenceDTO.getHttpRequests(), Is.is(Arrays.asList(new HttpRequestDTO(HttpRequest.request("one")), new HttpRequestDTO(HttpRequest.request("two")), new HttpRequestDTO(HttpRequest.request("three")))));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        VerificationSequenceDTO verificationSequenceDTO = new VerificationSequenceDTO(null);
        // then
        MatcherAssert.assertThat(verificationSequenceDTO.getHttpRequests(), Is.is(Arrays.<HttpRequestDTO>asList()));
    }

    @Test
    public void shouldHandleNullFieldInput() {
        // when
        VerificationSequenceDTO verificationSequenceDTO = new VerificationSequenceDTO(new VerificationSequence());
        // then
        MatcherAssert.assertThat(verificationSequenceDTO.getHttpRequests(), Is.is(Arrays.<HttpRequestDTO>asList()));
    }
}

