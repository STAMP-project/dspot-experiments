/**
 * Copyright (C) 2018 the original author or authors.
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
package org.springframework.cloud.alibaba.sentinel;


import HttpStatus.OK;
import com.alibaba.csp.sentinel.adapter.servlet.config.WebServletConfig;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.log.LogBase;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.alibaba.sentinel.annotation.SentinelRestTemplate;
import org.springframework.cloud.alibaba.sentinel.custom.SentinelAutoConfiguration;
import org.springframework.cloud.alibaba.sentinel.custom.SentinelBeanPostProcessor;
import org.springframework.cloud.alibaba.sentinel.rest.SentinelClientHttpResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


/**
 *
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @author jiashuai.xie
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SentinelAutoConfigurationTests.TestConfig.class }, properties = { "spring.cloud.sentinel.filter.order=123", "spring.cloud.sentinel.filter.urlPatterns=/*,/test", "spring.cloud.sentinel.metric.fileSingleSize=9999", "spring.cloud.sentinel.metric.fileTotalCount=100", "spring.cloud.sentinel.servlet.blockPage=/error", "spring.cloud.sentinel.flow.coldFactor=3", "spring.cloud.sentinel.eager=true", "spring.cloud.sentinel.log.switchPid=true", "spring.cloud.sentinel.transport.dashboard=http://localhost:8080", "spring.cloud.sentinel.transport.port=9999", "spring.cloud.sentinel.transport.clientIp=1.1.1.1", "spring.cloud.sentinel.transport.heartbeatIntervalMs=20000" }, webEnvironment = RANDOM_PORT)
public class SentinelAutoConfigurationTests {
    @Autowired
    private SentinelProperties sentinelProperties;

    @Autowired
    private FilterRegistrationBean filterRegistrationBean;

    @Autowired
    private SentinelBeanPostProcessor sentinelBeanPostProcessor;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate restTemplateWithBlockClass;

    @Autowired
    private RestTemplate restTemplateWithoutBlockClass;

    @Autowired
    private RestTemplate restTemplateWithFallbackClass;

    @LocalServerPort
    private int port;

    private String url = "http://localhost:" + (port);

    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull("FilterRegistrationBean was not created", filterRegistrationBean);
        Assert.assertNotNull("SentinelProperties was not created", sentinelProperties);
        Assert.assertNotNull("SentinelBeanPostProcessor was not created", sentinelBeanPostProcessor);
        checkSentinelLog();
        checkSentinelEager();
        checkSentinelTransport();
        checkSentinelColdFactor();
        checkSentinelMetric();
        checkSentinelFilter();
        checkEndpoint();
    }

    @Test
    public void testFilter() {
        Assert.assertEquals("Sentinel Filter order was wrong", filterRegistrationBean.getOrder(), 123);
        Assert.assertEquals("Sentinel Filter url-pattern was wrong", filterRegistrationBean.getUrlPatterns().size(), 2);
    }

    @Test
    public void testSentinelSystemProperties() {
        Assert.assertEquals("Sentinel log pid was wrong", true, LogBase.isLogNameUsePid());
        Assert.assertEquals("Sentinel transport console server was wrong", "http://localhost:8080", TransportConfig.getConsoleServer());
        Assert.assertEquals("Sentinel transport port was wrong", "9999", TransportConfig.getPort());
        Assert.assertEquals("Sentinel transport heartbeatIntervalMs was wrong", 20000L, TransportConfig.getHeartbeatIntervalMs().longValue());
        Assert.assertEquals("Sentinel transport clientIp was wrong", "1.1.1.1", TransportConfig.getHeartbeatClientIp());
        Assert.assertEquals("Sentinel metric file size was wrong", 9999, SentinelConfig.singleMetricFileSize());
        Assert.assertEquals("Sentinel metric file count was wrong", 100, SentinelConfig.totalMetricFileCount());
        Assert.assertEquals("Sentinel metric file charset was wrong", "UTF-8", SentinelConfig.charset());
        Assert.assertEquals("Sentinel block page was wrong", "/error", WebServletConfig.getBlockPage());
    }

    @Test
    public void testFlowRestTemplate() {
        Assert.assertEquals("RestTemplate interceptors size was wrong", 2, restTemplate.getInterceptors().size());
        Assert.assertEquals("RestTemplateWithBlockClass interceptors size was wrong", 1, restTemplateWithBlockClass.getInterceptors().size());
        ResponseEntity responseEntityBlock = restTemplateWithBlockClass.getForEntity(url, String.class);
        Assert.assertEquals("RestTemplateWithBlockClass Sentinel Block Message was wrong", "Oops", responseEntityBlock.getBody());
        Assert.assertEquals("RestTemplateWithBlockClass Sentinel Block Http Status Code was wrong", OK, responseEntityBlock.getStatusCode());
        ResponseEntity responseEntityRaw = restTemplate.getForEntity(url, String.class);
        Assert.assertEquals("RestTemplate Sentinel Block Message was wrong", "RestTemplate request block by sentinel", responseEntityRaw.getBody());
        Assert.assertEquals("RestTemplate Sentinel Block Http Status Code was wrong", OK, responseEntityRaw.getStatusCode());
    }

    @Test
    public void testNormalRestTemplate() {
        Assert.assertEquals("RestTemplateWithoutBlockClass interceptors size was wrong", 0, restTemplateWithoutBlockClass.getInterceptors().size());
        assertThatExceptionOfType(RestClientException.class).isThrownBy(() -> {
            restTemplateWithoutBlockClass.getForEntity(url, .class);
        });
    }

    @Test
    public void testFallbackRestTemplate() {
        ResponseEntity responseEntity = restTemplateWithFallbackClass.getForEntity(((url) + "/test"), String.class);
        Assert.assertEquals("RestTemplateWithFallbackClass Sentinel Message was wrong", "Oops fallback", responseEntity.getBody());
        Assert.assertEquals("RestTemplateWithFallbackClass Sentinel Http Status Code was wrong", OK, responseEntity.getStatusCode());
    }

    @Configuration
    static class SentinelTestConfiguration {
        @Bean
        @SentinelRestTemplate
        RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(Mockito.mock(ClientHttpRequestInterceptor.class));
            return restTemplate;
        }

        @Bean
        @SentinelRestTemplate(blockHandlerClass = SentinelAutoConfigurationTests.ExceptionUtil.class, blockHandler = "handleException")
        RestTemplate restTemplateWithBlockClass() {
            return new RestTemplate();
        }

        @Bean
        @SentinelRestTemplate(fallbackClass = SentinelAutoConfigurationTests.ExceptionUtil.class, fallback = "fallbackException")
        RestTemplate restTemplateWithFallbackClass() {
            return new RestTemplate();
        }

        @Bean
        RestTemplate restTemplateWithoutBlockClass() {
            return new RestTemplate();
        }
    }

    public static class ExceptionUtil {
        public static SentinelClientHttpResponse handleException(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
            System.out.println(("Oops: " + (ex.getClass().getCanonicalName())));
            return new SentinelClientHttpResponse("Oops");
        }

        public static SentinelClientHttpResponse fallbackException(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
            System.out.println(("Oops: " + (ex.getClass().getCanonicalName())));
            return new SentinelClientHttpResponse("Oops fallback");
        }
    }

    @Configuration
    @EnableAutoConfiguration
    @ImportAutoConfiguration({ SentinelAutoConfiguration.class, SentinelWebAutoConfiguration.class, SentinelAutoConfigurationTests.SentinelTestConfiguration.class })
    public static class TestConfig {}
}

