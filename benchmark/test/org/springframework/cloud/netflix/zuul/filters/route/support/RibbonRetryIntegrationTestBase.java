/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.zuul.filters.route.support;


import HttpMethod.GET;
import HttpMethod.POST;
import HttpStatus.GATEWAY_TIMEOUT;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancedRetryFactory;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancedRetryPolicy;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerContext;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.test.NoSecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 *
 * @author Ryan Baxter
 */
public abstract class RibbonRetryIntegrationTestBase {
    private final Log LOG = LogFactory.getLog(RibbonRetryIntegrationTestBase.class);

    @Value("${local.server.port}")
    protected int port;

    @Test
    public void retryable() {
        String uri = "/retryable/everyothererror";
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void retryableFourOFour() {
        String uri = "/retryable/404everyothererror";
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void postRetryOK() {
        String uri = "/retryable/posteveryothererror";
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), POST, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void getRetryable() {
        String uri = "/getretryable/everyothererror";
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void postNotRetryable() {
        String uri = "/getretryable/posteveryothererror";
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), POST, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void disableRetry() {
        String uri = "/disableretry/everyothererror";
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        LOG.info(("Response Body: " + (result.getBody())));
        assertThat(result.getStatusCode()).isEqualTo(GATEWAY_TIMEOUT);
    }

    @Test
    public void globalRetryDisabled() {
        String uri = "/globalretrydisabled/everyothererror";
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + uri), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        LOG.info(("Response Body: " + (result.getBody())));
        assertThat(result.getStatusCode()).isEqualTo(GATEWAY_TIMEOUT);
    }

    // Don't use @SpringBootApplication because we don't want to component scan
    @Configuration
    @EnableAutoConfiguration
    @RestController
    @EnableZuulProxy
    @RibbonClients({ @RibbonClient(name = "retryable", configuration = RibbonRetryIntegrationTestBase.RibbonClientConfiguration.class), @RibbonClient(name = "disableretry", configuration = RibbonRetryIntegrationTestBase.RibbonClientConfiguration.class), @RibbonClient(name = "globalretrydisabled", configuration = RibbonRetryIntegrationTestBase.RibbonClientConfiguration.class), @RibbonClient(name = "getretryable", configuration = RibbonRetryIntegrationTestBase.RibbonClientConfiguration.class) })
    @Import(NoSecurityConfiguration.class)
    public static class RetryableTestConfig {
        private final Log LOG = LogFactory.getLog(RibbonRetryIntegrationTestBase.RetryableTestConfig.class);

        private boolean error = true;

        @RequestMapping("/resetError")
        public void resetError() {
            error = true;
        }

        @RequestMapping("/everyothererror")
        public ResponseEntity<String> timeout() {
            boolean shouldError = error;
            error = !(error);
            try {
                if (shouldError) {
                    Thread.sleep(80000);
                }
            } catch (InterruptedException e) {
                LOG.info(e);
                Thread.currentThread().interrupt();
            }
            return new ResponseEntity<String>("no error", HttpStatus.OK);
        }

        @RequestMapping(path = "/posteveryothererror", method = RequestMethod.POST)
        public ResponseEntity<String> postTimeout() {
            return timeout();
        }

        @RequestMapping("/404everyothererror")
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ResponseEntity<String> fourOFourError() {
            boolean shouldError = error;
            error = !(error);
            if (shouldError) {
                return new ResponseEntity<String>("not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<String>("no error", HttpStatus.OK);
        }
    }

    @Configuration
    public static class RibbonClientConfiguration {
        @Value("${local.server.port}")
        private int port;

        @Bean
        public ServerList<Server> ribbonServerList() {
            return new org.springframework.cloud.netflix.ribbon.StaticServerList(new Server("localhost", this.port));
        }
    }

    @Configuration
    public static class FourOFourRetryableRibbonConfiguration extends RibbonRetryIntegrationTestBase.RibbonClientConfiguration {
        @Bean
        public LoadBalancedRetryFactory loadBalancedRetryPolicyFactory(SpringClientFactory factory) {
            return new RibbonRetryIntegrationTestBase.FourOFourRetryableRibbonConfiguration.MyRibbonRetryFactory(factory);
        }

        public static class MyRibbonRetryFactory extends RibbonLoadBalancedRetryFactory {
            private SpringClientFactory factory;

            public MyRibbonRetryFactory(SpringClientFactory clientFactory) {
                super(clientFactory);
                this.factory = clientFactory;
            }

            @Override
            public LoadBalancedRetryPolicy createRetryPolicy(String serviceId, ServiceInstanceChooser loadBalanceChooser) {
                RibbonLoadBalancerContext lbContext = this.factory.getLoadBalancerContext(serviceId);
                return new RibbonRetryIntegrationTestBase.FourOFourRetryableRibbonConfiguration.MyRibbonRetryFactory.MyLoadBalancedRetryPolicy(serviceId, lbContext, loadBalanceChooser);
            }

            class MyLoadBalancedRetryPolicy extends RibbonLoadBalancedRetryPolicy {
                MyLoadBalancedRetryPolicy(String serviceId, RibbonLoadBalancerContext context, ServiceInstanceChooser loadBalanceChooser) {
                    super(serviceId, context, loadBalanceChooser);
                }

                @Override
                public boolean retryableStatusCode(int statusCode) {
                    if (statusCode == (NOT_FOUND.value())) {
                        return true;
                    }
                    return super.retryableStatusCode(statusCode);
                }
            }
        }
    }
}

