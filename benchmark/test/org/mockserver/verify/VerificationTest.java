package org.mockserver.verify;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.model.HttpRequest;


/**
 *
 *
 * @author jamesdbloom
 */
public class VerificationTest {
    @Test
    public void shouldReturnValuesSetInSetter() {
        // when
        HttpRequest request = HttpRequest.request();
        VerificationTimes times = VerificationTimes.atLeast(2);
        Verification verification = Verification.verification().withRequest(request).withTimes(times);
        // then
        MatcherAssert.assertThat(verification.getHttpRequest(), CoreMatchers.sameInstance(request));
        MatcherAssert.assertThat(verification.getTimes(), CoreMatchers.sameInstance(times));
    }
}

