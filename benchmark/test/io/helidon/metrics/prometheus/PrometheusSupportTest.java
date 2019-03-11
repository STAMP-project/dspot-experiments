/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.metrics.prometheus;


import Http.Header.CONTENT_TYPE;
import Http.Status.OK_200;
import io.helidon.webserver.Routing;
import io.helidon.webserver.testsupport.TestResponse;
import io.prometheus.client.Counter;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;


public class PrometheusSupportTest {
    private Routing routing;

    private Counter alpha;

    private Counter beta;

    @Test
    public void simpleCall() throws Exception {
        TestResponse response = doTestRequest(null);
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE).orElse(null), StringStartsWith.startsWith("text/plain"));
        String body = response.asString().get(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(body, CoreMatchers.containsString("# HELP beta"));
        MatcherAssert.assertThat(body, CoreMatchers.containsString("# TYPE beta counter"));
        MatcherAssert.assertThat(body, CoreMatchers.containsString("beta 3.0"));
        MatcherAssert.assertThat(body, CoreMatchers.containsString("# TYPE alpha counter"));
        MatcherAssert.assertThat(body, CoreMatchers.containsString("# HELP alpha Alpha help with \\\\ and \\n."));
        MatcherAssert.assertThat(body, CoreMatchers.containsString("alpha{method=\"bar\",} 6.0"));
        MatcherAssert.assertThat(body, CoreMatchers.containsString("alpha{method=\"\\\"foo\\\" \\\\ \\n\",} 5.0"));
    }

    @Test
    public void doubleCall() throws Exception {
        TestResponse response = doTestRequest(null);
        MatcherAssert.assertThat(response.headers().first(CONTENT_TYPE).orElse(null), StringStartsWith.startsWith("text/plain"));
        String body = response.asString().get(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(body, CoreMatchers.containsString("alpha{method=\"bar\",} 6.0"));
        MatcherAssert.assertThat(body, IsNot.not(CoreMatchers.containsString("alpha{method=\"baz\"")));
        alpha.labels("baz").inc();
        response = doTestRequest(null);
        body = response.asString().get(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(body, CoreMatchers.containsString("alpha{method=\"baz\",} 1.0"));
    }

    @Test
    public void filter() throws Exception {
        TestResponse response = doTestRequest("alpha");
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(OK_200));
        String body = response.asString().get(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(body, IsNot.not(CoreMatchers.containsString("# TYPE beta")));
        MatcherAssert.assertThat(body, IsNot.not(CoreMatchers.containsString("beta 3.0")));
        MatcherAssert.assertThat(body, CoreMatchers.containsString("# TYPE alpha counter"));
        MatcherAssert.assertThat(body, CoreMatchers.containsString("alpha{method=\"bar\",} 6.0"));
    }
}

