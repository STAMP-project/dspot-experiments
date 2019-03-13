/**
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.elasticsearch;


import Status.DOWN;
import Status.OUT_OF_SERVICE;
import Status.UP;
import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.config.exception.CouldNotConnectException;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;


/**
 * Tests for {@link ElasticsearchJestHealthIndicator}.
 *
 * @author Stephane Nicoll
 * @author Julian Devia Serna
 * @author Brian Clozel
 */
public class ElasticsearchJestHealthIndicatorTests {
    private final JestClient jestClient = Mockito.mock(JestClient.class);

    private final ElasticsearchJestHealthIndicator healthIndicator = new ElasticsearchJestHealthIndicator(this.jestClient);

    @SuppressWarnings("unchecked")
    @Test
    public void elasticsearchIsUp() throws IOException {
        BDDMockito.given(this.jestClient.execute(ArgumentMatchers.any(Action.class))).willReturn(ElasticsearchJestHealthIndicatorTests.createJestResult(200, true, "green"));
        Health health = this.healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertHealthDetailsWithStatus(health.getDetails(), "green");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void elasticsearchWithYellowStatusIsUp() throws IOException {
        BDDMockito.given(this.jestClient.execute(ArgumentMatchers.any(Action.class))).willReturn(ElasticsearchJestHealthIndicatorTests.createJestResult(200, true, "yellow"));
        Health health = this.healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertHealthDetailsWithStatus(health.getDetails(), "yellow");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void elasticsearchIsDown() throws IOException {
        BDDMockito.given(this.jestClient.execute(ArgumentMatchers.any(Action.class))).willThrow(new CouldNotConnectException("http://localhost:9200", new IOException()));
        Health health = this.healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void elasticsearchIsDownWhenQueryDidNotSucceed() throws IOException {
        BDDMockito.given(this.jestClient.execute(ArgumentMatchers.any(Action.class))).willReturn(ElasticsearchJestHealthIndicatorTests.createJestResult(200, false, ""));
        Health health = this.healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void elasticsearchIsDownByResponseCode() throws IOException {
        BDDMockito.given(this.jestClient.execute(ArgumentMatchers.any(Action.class))).willReturn(ElasticsearchJestHealthIndicatorTests.createJestResult(500, false, ""));
        Health health = this.healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails()).contains(entry("statusCode", 500));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void elasticsearchIsOutOfServiceByStatus() throws IOException {
        BDDMockito.given(this.jestClient.execute(ArgumentMatchers.any(Action.class))).willReturn(ElasticsearchJestHealthIndicatorTests.createJestResult(200, true, "red"));
        Health health = this.healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(OUT_OF_SERVICE);
        assertHealthDetailsWithStatus(health.getDetails(), "red");
    }
}

