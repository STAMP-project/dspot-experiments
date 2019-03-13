/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.spring;


import HttpStatus.OK;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.PathMapping;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.logging.LoggingService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * This uses {@link ArmeriaAutoConfiguration} for integration tests.
 * application-autoConfTest.yml will be loaded with minimal settings to make it work.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArmeriaAutoConfigurationWithoutMeterTest.NoMeterTestConfiguration.class)
@ActiveProfiles({ "local", "autoConfTest" })
public class ArmeriaAutoConfigurationWithoutMeterTest {
    @SpringBootApplication
    public static class NoMeterTestConfiguration {
        @Bean
        public HttpServiceRegistrationBean okService() {
            return new HttpServiceRegistrationBean().setServiceName("okService").setService(new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                    return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, "ok");
                }
            }).setPathMapping(PathMapping.ofExact("/ok")).setDecorators(LoggingService.newDecorator());
        }
    }

    @Rule
    public TestRule globalTimeout = new DisableOnDebug(new Timeout(10, TimeUnit.SECONDS));

    @Inject
    private Server server;

    @Test
    public void testHttpServiceRegistrationBean() throws Exception {
        final HttpClient client = HttpClient.of(newUrl("h1c"));
        final HttpResponse response = client.get("/ok");
        final AggregatedHttpMessage msg = response.aggregate().get();
        assertThat(msg.status()).isEqualTo(OK);
        assertThat(msg.contentUtf8()).isEqualTo("ok");
    }
}

