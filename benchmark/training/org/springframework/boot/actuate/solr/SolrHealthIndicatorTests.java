/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.solr;


import Status.DOWN;
import Status.UP;
import java.io.IOException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Tests for {@link SolrHealthIndicator}
 *
 * @author Andy Wilkinson
 */
public class SolrHealthIndicatorTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void solrIsUp() throws Exception {
        SolrClient solrClient = Mockito.mock(SolrClient.class);
        BDDMockito.given(solrClient.request(ArgumentMatchers.any(CoreAdminRequest.class), ArgumentMatchers.isNull())).willReturn(mockResponse(0));
        SolrHealthIndicator healthIndicator = new SolrHealthIndicator(solrClient);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("status")).isEqualTo(0);
    }

    @Test
    public void solrIsUpAndRequestFailed() throws Exception {
        SolrClient solrClient = Mockito.mock(SolrClient.class);
        BDDMockito.given(solrClient.request(ArgumentMatchers.any(CoreAdminRequest.class), ArgumentMatchers.isNull())).willReturn(mockResponse(400));
        SolrHealthIndicator healthIndicator = new SolrHealthIndicator(solrClient);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails().get("status")).isEqualTo(400);
    }

    @Test
    public void solrIsDown() throws Exception {
        SolrClient solrClient = Mockito.mock(SolrClient.class);
        BDDMockito.given(solrClient.request(ArgumentMatchers.any(CoreAdminRequest.class), ArgumentMatchers.isNull())).willThrow(new IOException("Connection failed"));
        SolrHealthIndicator healthIndicator = new SolrHealthIndicator(solrClient);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(((String) (health.getDetails().get("error")))).contains("Connection failed");
    }
}

