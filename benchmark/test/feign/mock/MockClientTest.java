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


import HttpMethod.POST;
import feign.Body;
import feign.FeignException;
import feign.Param;
import feign.Request;
import feign.RequestLine;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MockClientTest {
    interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<MockClientTest.Contributor> contributors(@Param("owner")
        String owner, @Param("repo")
        String repo);

        @RequestLine("GET /repos/{owner}/{repo}/contributors?client_id={client_id}")
        List<MockClientTest.Contributor> contributors(@Param("client_id")
        String clientId, @Param("owner")
        String owner, @Param("repo")
        String repo);

        @RequestLine("PATCH /repos/{owner}/{repo}/contributors")
        List<MockClientTest.Contributor> patchContributors(@Param("owner")
        String owner, @Param("repo")
        String repo);

        @RequestLine("POST /repos/{owner}/{repo}/contributors")
        @Body("%7B\"login\":\"{login}\",\"type\":\"{type}\"%7D")
        MockClientTest.Contributor create(@Param("owner")
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

    private MockClientTest.GitHub github;

    private MockClient mockClient;

    @Test
    public void hitMock() {
        List<MockClientTest.Contributor> contributors = github.contributors("netflix", "feign");
        MatcherAssert.assertThat(contributors, Matchers.hasSize(30));
        mockClient.verifyStatus();
    }

    @Test
    public void missMock() {
        try {
            github.contributors("velo", "feign-mock");
            Assert.fail();
        } catch (FeignException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("404"));
        }
    }

    @Test
    public void missHttpMethod() {
        try {
            github.patchContributors("netflix", "feign");
            Assert.fail();
        } catch (FeignException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("404"));
        }
    }

    @Test
    public void paramsEncoding() {
        List<MockClientTest.Contributor> contributors = github.contributors("7 7", "netflix", "feign");
        MatcherAssert.assertThat(contributors, Matchers.hasSize(30));
        mockClient.verifyStatus();
    }

    @Test
    public void verifyInvocation() {
        MockClientTest.Contributor contribution = github.create("netflix", "feign", "velo_at_github", "preposterous hacker");
        // making sure it received a proper response
        MatcherAssert.assertThat(contribution, Matchers.notNullValue());
        MatcherAssert.assertThat(contribution.login, Matchers.equalTo("velo"));
        MatcherAssert.assertThat(contribution.contributions, Matchers.equalTo(0));
        List<Request> results = mockClient.verifyTimes(POST, "/repos/netflix/feign/contributors", 1);
        MatcherAssert.assertThat(results, Matchers.hasSize(1));
        byte[] body = mockClient.verifyOne(POST, "/repos/netflix/feign/contributors").body();
        MatcherAssert.assertThat(body, Matchers.notNullValue());
        String message = new String(body);
        MatcherAssert.assertThat(message, Matchers.containsString("velo_at_github"));
        MatcherAssert.assertThat(message, Matchers.containsString("preposterous hacker"));
        mockClient.verifyStatus();
    }

    @Test
    public void verifyNone() {
        github.create("netflix", "feign", "velo_at_github", "preposterous hacker");
        mockClient.verifyTimes(POST, "/repos/netflix/feign/contributors", 1);
        try {
            mockClient.verifyTimes(POST, "/repos/netflix/feign/contributors", 0);
            Assert.fail();
        } catch (VerificationAssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Do not wanted"));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("POST"));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("/repos/netflix/feign/contributors"));
        }
        try {
            mockClient.verifyTimes(POST, "/repos/netflix/feign/contributors", 3);
            Assert.fail();
        } catch (VerificationAssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Wanted"));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("POST"));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("/repos/netflix/feign/contributors"));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("'3'"));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("'1'"));
        }
    }

    @Test
    public void verifyNotInvoked() {
        mockClient.verifyNever(POST, "/repos/netflix/feign/contributors");
        List<Request> results = mockClient.verifyTimes(POST, "/repos/netflix/feign/contributors", 0);
        MatcherAssert.assertThat(results, Matchers.hasSize(0));
        try {
            mockClient.verifyOne(POST, "/repos/netflix/feign/contributors");
            Assert.fail();
        } catch (VerificationAssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Wanted"));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("POST"));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("/repos/netflix/feign/contributors"));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("never invoked"));
        }
    }

    @Test
    public void verifyNegative() {
        try {
            mockClient.verifyTimes(POST, "/repos/netflix/feign/contributors", (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("non negative"));
        }
    }

    @Test
    public void verifyMultipleRequests() {
        mockClient.verifyNever(POST, "/repos/netflix/feign/contributors");
        github.create("netflix", "feign", "velo_at_github", "preposterous hacker");
        Request result = mockClient.verifyOne(POST, "/repos/netflix/feign/contributors");
        MatcherAssert.assertThat(result, Matchers.notNullValue());
        github.create("netflix", "feign", "velo_at_github", "preposterous hacker");
        List<Request> results = mockClient.verifyTimes(POST, "/repos/netflix/feign/contributors", 2);
        MatcherAssert.assertThat(results, Matchers.hasSize(2));
        github.create("netflix", "feign", "velo_at_github", "preposterous hacker");
        results = mockClient.verifyTimes(POST, "/repos/netflix/feign/contributors", 3);
        MatcherAssert.assertThat(results, Matchers.hasSize(3));
        mockClient.verifyStatus();
    }

    @Test
    public void resetRequests() {
        mockClient.verifyNever(POST, "/repos/netflix/feign/contributors");
        github.create("netflix", "feign", "velo_at_github", "preposterous hacker");
        Request result = mockClient.verifyOne(POST, "/repos/netflix/feign/contributors");
        MatcherAssert.assertThat(result, Matchers.notNullValue());
        mockClient.resetRequests();
        mockClient.verifyNever(POST, "/repos/netflix/feign/contributors");
    }
}

