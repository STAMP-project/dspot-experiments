/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign.mock;


import feign.Body;
import feign.FeignException;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MockClientSequentialTest {
    interface GitHub {
        @Headers({ "Name: {owner}" })
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<MockClientSequentialTest.Contributor> contributors(@Param("owner")
        String owner, @Param("repo")
        String repo);

        @RequestLine("GET /repos/{owner}/{repo}/contributors?client_id={client_id}")
        List<MockClientSequentialTest.Contributor> contributors(@Param("client_id")
        String clientId, @Param("owner")
        String owner, @Param("repo")
        String repo);

        @RequestLine("PATCH /repos/{owner}/{repo}/contributors")
        List<MockClientSequentialTest.Contributor> patchContributors(@Param("owner")
        String owner, @Param("repo")
        String repo);

        @RequestLine("POST /repos/{owner}/{repo}/contributors")
        @Body("%7B\"login\":\"{login}\",\"type\":\"{type}\"%7D")
        MockClientSequentialTest.Contributor create(@Param("owner")
        String owner, @Param("repo")
        String repo, @Param("login")
        String login, @Param("type")
        String type);
    }

    static class Contributor {
        String login;

        int contributions;
    }

    class AssertionDecoder implements Decoder {
        private final Decoder delegate;

        public AssertionDecoder(Decoder delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object decode(Response response, Type type) throws FeignException, DecodeException, IOException {
            MatcherAssert.assertThat(response.request(), Matchers.notNullValue());
            return delegate.decode(response, type);
        }
    }

    private MockClientSequentialTest.GitHub githubSequential;

    private MockClient mockClientSequential;

    @Test
    public void sequentialRequests() throws Exception {
        githubSequential.contributors("netflix", "feign");
        try {
            githubSequential.contributors("55", "netflix", "feign");
            Assert.fail();
        } catch (FeignException e) {
            MatcherAssert.assertThat(e.status(), Matchers.equalTo(HttpsURLConnection.HTTP_NOT_FOUND));
        }
        try {
            githubSequential.contributors("7 7", "netflix", "feign");
            Assert.fail();
        } catch (FeignException e) {
            MatcherAssert.assertThat(e.status(), Matchers.equalTo(HttpsURLConnection.HTTP_INTERNAL_ERROR));
        }
        githubSequential.contributors("netflix", "feign");
        mockClientSequential.verifyStatus();
    }

    @Test
    public void sequentialRequestsCalledTooLess() throws Exception {
        githubSequential.contributors("netflix", "feign");
        try {
            mockClientSequential.verifyStatus();
            Assert.fail();
        } catch (VerificationAssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.startsWith("More executions"));
        }
    }

    @Test
    public void sequentialRequestsCalledTooMany() throws Exception {
        sequentialRequests();
        try {
            githubSequential.contributors("netflix", "feign");
            Assert.fail();
        } catch (VerificationAssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("excessive"));
        }
    }

    @Test
    public void sequentialRequestsInWrongOrder() throws Exception {
        try {
            githubSequential.contributors("7 7", "netflix", "feign");
            Assert.fail();
        } catch (VerificationAssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.startsWith("Expected: \nRequest ["));
        }
    }
}

