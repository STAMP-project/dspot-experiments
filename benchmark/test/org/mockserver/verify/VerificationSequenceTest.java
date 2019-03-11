package org.mockserver.verify;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.model.HttpRequest;


public class VerificationSequenceTest {
    @Test
    public void shouldReturnValuesSetInSetter() {
        // when
        VerificationSequence verification = new VerificationSequence().withRequests(HttpRequest.request("one"), HttpRequest.request("two"), HttpRequest.request("three"));
        // then
        MatcherAssert.assertThat(verification.getHttpRequests(), CoreMatchers.is(Arrays.asList(HttpRequest.request("one"), HttpRequest.request("two"), HttpRequest.request("three"))));
    }
}

