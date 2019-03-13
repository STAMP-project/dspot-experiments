package org.mockserver.serialization.model;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.Verification;
import org.mockserver.verify.VerificationTimes;


public class VerificationDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        HttpRequest request = HttpRequest.request();
        VerificationTimes times = VerificationTimes.atLeast(1);
        Verification verification = Verification.verification().withRequest(request).withTimes(times);
        // when
        VerificationDTO verificationDTO = new VerificationDTO(verification);
        // then
        MatcherAssert.assertThat(verificationDTO.getHttpRequest(), Is.is(new HttpRequestDTO(request)));
        MatcherAssert.assertThat(verificationDTO.getTimes(), Is.is(new VerificationTimesDTO(times)));
    }

    @Test
    public void shouldBuildObject() {
        // given
        HttpRequest request = HttpRequest.request();
        VerificationTimes times = VerificationTimes.atLeast(1);
        Verification verification = Verification.verification().withRequest(request).withTimes(times);
        // when
        Verification builtVerification = buildObject();
        // then
        MatcherAssert.assertThat(builtVerification.getHttpRequest(), Is.is(request));
        MatcherAssert.assertThat(builtVerification.getTimes(), Is.is(times));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        HttpRequestDTO request = new HttpRequestDTO(HttpRequest.request());
        VerificationTimesDTO times = new VerificationTimesDTO(VerificationTimes.atLeast(1));
        Verification verification = Verification.verification();
        // when
        VerificationDTO verificationDTO = new VerificationDTO(verification);
        verificationDTO.setHttpRequest(request);
        verificationDTO.setTimes(times);
        // then
        MatcherAssert.assertThat(verificationDTO.getHttpRequest(), Is.is(request));
        MatcherAssert.assertThat(verificationDTO.getTimes(), Is.is(times));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        VerificationDTO verificationDTO = new VerificationDTO(null);
        // then
        MatcherAssert.assertThat(verificationDTO.getHttpRequest(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(verificationDTO.getTimes(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldHandleNullFieldInput() {
        // when
        VerificationDTO verificationDTO = new VerificationDTO(new Verification());
        // then
        MatcherAssert.assertThat(verificationDTO.getHttpRequest(), Is.is(new HttpRequestDTO(HttpRequest.request())));
        MatcherAssert.assertThat(verificationDTO.getTimes(), Is.is(new VerificationTimesDTO(VerificationTimes.atLeast(1))));
    }
}

