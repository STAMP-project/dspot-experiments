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


import feign.hystrix.FallbackFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.alibaba.sentinel.feign.SentinelFeignAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SentinelFeignTests.TestConfig.class }, properties = { "feign.sentinel.enabled=true" })
public class SentinelFeignTests {
    @Autowired
    private SentinelFeignTests.EchoService echoService;

    @Autowired
    private SentinelFeignTests.FooService fooService;

    @Autowired
    private SentinelFeignTests.BarService barService;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull("EchoService was not created", echoService);
        Assert.assertNotNull("FooService was not created", fooService);
    }

    @Test
    public void testFeignClient() {
        Assert.assertEquals("Sentinel Feign Client fallback success", "echo fallback", echoService.echo("test"));
        Assert.assertEquals("Sentinel Feign Client fallbackFactory success", "foo fallback", fooService.echo("test"));
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> {
            barService.bar();
        });
        Assert.assertNotEquals("ToString method invoke was not in SentinelInvocationHandler", echoService.toString(), fooService.toString());
        Assert.assertNotEquals("HashCode method invoke was not in SentinelInvocationHandler", echoService.hashCode(), fooService.hashCode());
        Assert.assertFalse("Equals method invoke was not in SentinelInvocationHandler", echoService.equals(fooService));
    }

    @Configuration
    @EnableAutoConfiguration
    @ImportAutoConfiguration({ SentinelFeignAutoConfiguration.class })
    @EnableFeignClients
    public static class TestConfig {
        @Bean
        public SentinelFeignTests.EchoServiceFallback echoServiceFallback() {
            return new SentinelFeignTests.EchoServiceFallback();
        }

        @Bean
        public SentinelFeignTests.CustomFallbackFactory customFallbackFactory() {
            return new SentinelFeignTests.CustomFallbackFactory();
        }
    }

    @FeignClient(value = "test-service", fallback = SentinelFeignTests.EchoServiceFallback.class)
    public interface EchoService {
        @RequestMapping(path = "echo/{str}")
        String echo(@RequestParam("str")
        String param);
    }

    @FeignClient(value = "foo-service", fallbackFactory = SentinelFeignTests.CustomFallbackFactory.class)
    public interface FooService {
        @RequestMapping(path = "echo/{str}")
        String echo(@RequestParam("str")
        String param);
    }

    @FeignClient("bar-service")
    public interface BarService {
        @RequestMapping(path = "bar")
        String bar();
    }

    public static class EchoServiceFallback implements SentinelFeignTests.EchoService {
        @Override
        public String echo(@RequestParam("str")
        String param) {
            return "echo fallback";
        }
    }

    public static class FooServiceFallback implements SentinelFeignTests.FooService {
        @Override
        public String echo(@RequestParam("str")
        String param) {
            return "foo fallback";
        }
    }

    public static class CustomFallbackFactory implements FallbackFactory<SentinelFeignTests.FooService> {
        private SentinelFeignTests.FooService fooService = new SentinelFeignTests.FooServiceFallback();

        @Override
        public SentinelFeignTests.FooService create(Throwable throwable) {
            return fooService;
        }
    }
}

