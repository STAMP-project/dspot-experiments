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
package org.springframework.boot.actuate.autoconfigure.endpoint.web.documentation;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.boot.actuate.cache.CachesEndpoint;
import org.springframework.boot.actuate.cache.CachesEndpointWebExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;


/**
 * Tests for generating documentation describing the {@link CachesEndpoint}
 *
 * @author Stephane Nicoll
 */
public class CachesEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    private static final List<FieldDescriptor> levelFields = Arrays.asList(fieldWithPath("name").description("Cache name."), fieldWithPath("cacheManager").description("Cache manager name."), fieldWithPath("target").description("Fully qualified name of the native cache."));

    private static final List<ParameterDescriptor> requestParameters = Collections.singletonList(parameterWithName("cacheManager").description(("Name of the cacheManager to qualify the cache. May be " + "omitted if the cache name is unique.")).optional());

    @Test
    public void allCaches() throws Exception {
        this.mockMvc.perform(get("/actuator/caches")).andExpect(status().isOk()).andDo(MockMvcRestDocumentation.document("caches/all", responseFields(fieldWithPath("cacheManagers").description("Cache managers keyed by id."), fieldWithPath("cacheManagers.*.caches").description(("Caches in the application context keyed by " + "name."))).andWithPrefix("cacheManagers.*.caches.*.", fieldWithPath("target").description("Fully qualified name of the native cache."))));
    }

    @Test
    public void namedCache() throws Exception {
        this.mockMvc.perform(get("/actuator/caches/cities")).andExpect(status().isOk()).andDo(MockMvcRestDocumentation.document("caches/named", requestParameters(CachesEndpointDocumentationTests.requestParameters), responseFields(CachesEndpointDocumentationTests.levelFields)));
    }

    @Test
    public void evictAllCaches() throws Exception {
        this.mockMvc.perform(delete("/actuator/caches")).andExpect(status().isNoContent()).andDo(MockMvcRestDocumentation.document("caches/evict-all"));
    }

    @Test
    public void evictNamedCache() throws Exception {
        this.mockMvc.perform(delete("/actuator/caches/countries?cacheManager=anotherCacheManager")).andExpect(status().isNoContent()).andDo(MockMvcRestDocumentation.document("caches/evict-named", requestParameters(CachesEndpointDocumentationTests.requestParameters)));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public CachesEndpoint endpoint() {
            Map<String, CacheManager> cacheManagers = new HashMap<>();
            cacheManagers.put("cacheManager", new ConcurrentMapCacheManager("countries", "cities"));
            cacheManagers.put("anotherCacheManager", new ConcurrentMapCacheManager("countries"));
            return new CachesEndpoint(cacheManagers);
        }

        @Bean
        public CachesEndpointWebExtension endpointWebExtension() {
            return new CachesEndpointWebExtension(endpoint());
        }
    }
}

