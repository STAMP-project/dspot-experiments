/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.web.servlet.config.annotation;


import java.util.Arrays;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.cors.CorsConfiguration;


/**
 * Test fixture with a {@link CorsRegistry}.
 *
 * @author Sebastien Deleuze
 */
public class CorsRegistryTests {
    private CorsRegistry registry;

    @Test
    public void noMapping() {
        Assert.assertTrue(this.registry.getCorsConfigurations().isEmpty());
    }

    @Test
    public void multipleMappings() {
        this.registry.addMapping("/foo");
        this.registry.addMapping("/bar");
        Assert.assertEquals(2, this.registry.getCorsConfigurations().size());
    }

    @Test
    public void customizedMapping() {
        this.registry.addMapping("/foo").allowedOrigins("http://domain2.com", "http://domain2.com").allowedMethods("DELETE").allowCredentials(false).allowedHeaders("header1", "header2").exposedHeaders("header3", "header4").maxAge(3600);
        Map<String, CorsConfiguration> configs = this.registry.getCorsConfigurations();
        Assert.assertEquals(1, configs.size());
        CorsConfiguration config = configs.get("/foo");
        Assert.assertEquals(Arrays.asList("http://domain2.com", "http://domain2.com"), config.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("DELETE"), config.getAllowedMethods());
        Assert.assertEquals(Arrays.asList("header1", "header2"), config.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList("header3", "header4"), config.getExposedHeaders());
        Assert.assertEquals(false, config.getAllowCredentials());
        Assert.assertEquals(Long.valueOf(3600), config.getMaxAge());
    }
}

