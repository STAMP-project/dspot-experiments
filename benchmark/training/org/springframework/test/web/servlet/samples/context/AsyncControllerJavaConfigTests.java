/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.test.web.servlet.samples.context;


import MediaType.APPLICATION_JSON;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Tests with Java configuration.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy(@ContextConfiguration(classes = AsyncControllerJavaConfigTests.WebConfig.class))
public class AsyncControllerJavaConfigTests {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private CallableProcessingInterceptor callableInterceptor;

    private MockMvc mockMvc;

    // SPR-13615
    @Test
    public void callableInterceptor() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/callable").accept(APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.request().asyncStarted()).andExpect(MockMvcResultMatchers.request().asyncResult(Collections.singletonMap("key", "value"))).andReturn();
        Mockito.verify(this.callableInterceptor).beforeConcurrentHandling(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(this.callableInterceptor).preProcess(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(this.callableInterceptor).postProcess(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(this.callableInterceptor);
        this.mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("{\"key\":\"value\"}"));
        Mockito.verify(this.callableInterceptor).afterCompletion(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(this.callableInterceptor);
    }

    @Configuration
    @EnableWebMvc
    static class WebConfig implements WebMvcConfigurer {
        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.registerCallableInterceptors(callableInterceptor());
        }

        @Bean
        public CallableProcessingInterceptor callableInterceptor() {
            return Mockito.mock(CallableProcessingInterceptor.class);
        }

        @Bean
        public AsyncControllerJavaConfigTests.AsyncController asyncController() {
            return new AsyncControllerJavaConfigTests.AsyncController();
        }
    }

    @RestController
    static class AsyncController {
        @GetMapping("/callable")
        public Callable<Map<String, String>> getCallable() {
            return () -> Collections.singletonMap("key", "value");
        }
    }
}

