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
package org.springframework.web.cors;


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.POST;
import HttpMethod.PUT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link CorsConfiguration}.
 *
 * @author Sebastien Deleuze
 * @author Sam Brannen
 */
public class CorsConfigurationTests {
    @Test
    public void setNullValues() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(null);
        Assert.assertNull(config.getAllowedOrigins());
        config.setAllowedHeaders(null);
        Assert.assertNull(config.getAllowedHeaders());
        config.setAllowedMethods(null);
        Assert.assertNull(config.getAllowedMethods());
        config.setExposedHeaders(null);
        Assert.assertNull(config.getExposedHeaders());
        config.setAllowCredentials(null);
        Assert.assertNull(config.getAllowCredentials());
        config.setMaxAge(null);
        Assert.assertNull(config.getMaxAge());
    }

    @Test
    public void setValues() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        Assert.assertEquals(Arrays.asList("*"), config.getAllowedOrigins());
        config.addAllowedHeader("*");
        Assert.assertEquals(Arrays.asList("*"), config.getAllowedHeaders());
        config.addAllowedMethod("*");
        Assert.assertEquals(Arrays.asList("*"), config.getAllowedMethods());
        config.addExposedHeader("header1");
        config.addExposedHeader("header2");
        Assert.assertEquals(Arrays.asList("header1", "header2"), config.getExposedHeaders());
        config.setAllowCredentials(true);
        Assert.assertTrue(config.getAllowCredentials());
        config.setMaxAge(123L);
        Assert.assertEquals(new Long(123), config.getMaxAge());
    }

    @Test(expected = IllegalArgumentException.class)
    public void asteriskWildCardOnAddExposedHeader() {
        CorsConfiguration config = new CorsConfiguration();
        config.addExposedHeader("*");
    }

    @Test(expected = IllegalArgumentException.class)
    public void asteriskWildCardOnSetExposedHeaders() {
        CorsConfiguration config = new CorsConfiguration();
        config.setExposedHeaders(Arrays.asList("*"));
    }

    @Test
    public void combineWithNull() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.combine(null);
        Assert.assertEquals(Arrays.asList("*"), config.getAllowedOrigins());
    }

    @Test
    public void combineWithNullProperties() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("header1");
        config.addExposedHeader("header3");
        config.addAllowedMethod(GET.name());
        config.setMaxAge(123L);
        config.setAllowCredentials(true);
        CorsConfiguration other = new CorsConfiguration();
        config = config.combine(other);
        Assert.assertEquals(Arrays.asList("*"), config.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("header1"), config.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList("header3"), config.getExposedHeaders());
        Assert.assertEquals(Arrays.asList(GET.name()), config.getAllowedMethods());
        Assert.assertEquals(new Long(123), config.getMaxAge());
        Assert.assertTrue(config.getAllowCredentials());
    }

    // SPR-15772
    @Test
    public void combineWithDefaultPermitValues() {
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        CorsConfiguration other = new CorsConfiguration();
        other.addAllowedOrigin("http://domain.com");
        other.addAllowedHeader("header1");
        other.addAllowedMethod(PUT.name());
        CorsConfiguration combinedConfig = config.combine(other);
        Assert.assertEquals(Arrays.asList("http://domain.com"), combinedConfig.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("header1"), combinedConfig.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList(PUT.name()), combinedConfig.getAllowedMethods());
        combinedConfig = other.combine(config);
        Assert.assertEquals(Arrays.asList("http://domain.com"), combinedConfig.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("header1"), combinedConfig.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList(PUT.name()), combinedConfig.getAllowedMethods());
        combinedConfig = config.combine(new CorsConfiguration());
        Assert.assertEquals(Arrays.asList("*"), config.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("*"), config.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList(GET.name(), HEAD.name(), POST.name()), combinedConfig.getAllowedMethods());
        combinedConfig = new CorsConfiguration().combine(config);
        Assert.assertEquals(Arrays.asList("*"), config.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("*"), config.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList(GET.name(), HEAD.name(), POST.name()), combinedConfig.getAllowedMethods());
    }

    @Test
    public void combineWithAsteriskWildCard() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        CorsConfiguration other = new CorsConfiguration();
        other.addAllowedOrigin("http://domain.com");
        other.addAllowedHeader("header1");
        other.addExposedHeader("header2");
        other.addAllowedMethod(PUT.name());
        CorsConfiguration combinedConfig = config.combine(other);
        Assert.assertEquals(Arrays.asList("*"), combinedConfig.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("*"), combinedConfig.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList("*"), combinedConfig.getAllowedMethods());
        combinedConfig = other.combine(config);
        Assert.assertEquals(Arrays.asList("*"), combinedConfig.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("*"), combinedConfig.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList("*"), combinedConfig.getAllowedMethods());
    }

    // SPR-14792
    @Test
    public void combineWithDuplicatedElements() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://domain1.com");
        config.addAllowedOrigin("http://domain2.com");
        config.addAllowedHeader("header1");
        config.addAllowedHeader("header2");
        config.addExposedHeader("header3");
        config.addExposedHeader("header4");
        config.addAllowedMethod(GET.name());
        config.addAllowedMethod(PUT.name());
        CorsConfiguration other = new CorsConfiguration();
        other.addAllowedOrigin("http://domain1.com");
        other.addAllowedHeader("header1");
        other.addExposedHeader("header3");
        other.addAllowedMethod(GET.name());
        CorsConfiguration combinedConfig = config.combine(other);
        Assert.assertEquals(Arrays.asList("http://domain1.com", "http://domain2.com"), combinedConfig.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("header1", "header2"), combinedConfig.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList("header3", "header4"), combinedConfig.getExposedHeaders());
        Assert.assertEquals(Arrays.asList(GET.name(), PUT.name()), combinedConfig.getAllowedMethods());
    }

    @Test
    public void combine() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://domain1.com");
        config.addAllowedHeader("header1");
        config.addExposedHeader("header3");
        config.addAllowedMethod(GET.name());
        config.setMaxAge(123L);
        config.setAllowCredentials(true);
        CorsConfiguration other = new CorsConfiguration();
        other.addAllowedOrigin("http://domain2.com");
        other.addAllowedHeader("header2");
        other.addExposedHeader("header4");
        other.addAllowedMethod(PUT.name());
        other.setMaxAge(456L);
        other.setAllowCredentials(false);
        config = config.combine(other);
        Assert.assertEquals(Arrays.asList("http://domain1.com", "http://domain2.com"), config.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("header1", "header2"), config.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList("header3", "header4"), config.getExposedHeaders());
        Assert.assertEquals(Arrays.asList(GET.name(), PUT.name()), config.getAllowedMethods());
        Assert.assertEquals(new Long(456), config.getMaxAge());
        Assert.assertFalse(config.getAllowCredentials());
    }

    @Test
    public void checkOriginAllowed() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        Assert.assertEquals("*", config.checkOrigin("http://domain.com"));
        config.setAllowCredentials(true);
        Assert.assertEquals("http://domain.com", config.checkOrigin("http://domain.com"));
        config.setAllowedOrigins(Arrays.asList("http://domain.com"));
        Assert.assertEquals("http://domain.com", config.checkOrigin("http://domain.com"));
        config.setAllowCredentials(false);
        Assert.assertEquals("http://domain.com", config.checkOrigin("http://domain.com"));
    }

    @Test
    public void checkOriginNotAllowed() {
        CorsConfiguration config = new CorsConfiguration();
        Assert.assertNull(config.checkOrigin(null));
        Assert.assertNull(config.checkOrigin("http://domain.com"));
        config.addAllowedOrigin("*");
        Assert.assertNull(config.checkOrigin(null));
        config.setAllowedOrigins(Arrays.asList("http://domain1.com"));
        Assert.assertNull(config.checkOrigin("http://domain2.com"));
        config.setAllowedOrigins(new ArrayList());
        Assert.assertNull(config.checkOrigin("http://domain.com"));
    }

    @Test
    public void checkMethodAllowed() {
        CorsConfiguration config = new CorsConfiguration();
        Assert.assertEquals(Arrays.asList(GET, HEAD), config.checkHttpMethod(GET));
        config.addAllowedMethod("GET");
        Assert.assertEquals(Arrays.asList(GET), config.checkHttpMethod(GET));
        config.addAllowedMethod("POST");
        Assert.assertEquals(Arrays.asList(GET, POST), config.checkHttpMethod(GET));
        Assert.assertEquals(Arrays.asList(GET, POST), config.checkHttpMethod(POST));
    }

    @Test
    public void checkMethodNotAllowed() {
        CorsConfiguration config = new CorsConfiguration();
        Assert.assertNull(config.checkHttpMethod(null));
        Assert.assertNull(config.checkHttpMethod(DELETE));
        config.setAllowedMethods(new ArrayList());
        Assert.assertNull(config.checkHttpMethod(POST));
    }

    @Test
    public void checkHeadersAllowed() {
        CorsConfiguration config = new CorsConfiguration();
        Assert.assertEquals(Collections.emptyList(), config.checkHeaders(Collections.emptyList()));
        config.addAllowedHeader("header1");
        config.addAllowedHeader("header2");
        Assert.assertEquals(Arrays.asList("header1"), config.checkHeaders(Arrays.asList("header1")));
        Assert.assertEquals(Arrays.asList("header1", "header2"), config.checkHeaders(Arrays.asList("header1", "header2")));
        Assert.assertEquals(Arrays.asList("header1", "header2"), config.checkHeaders(Arrays.asList("header1", "header2", "header3")));
    }

    @Test
    public void checkHeadersNotAllowed() {
        CorsConfiguration config = new CorsConfiguration();
        Assert.assertNull(config.checkHeaders(null));
        Assert.assertNull(config.checkHeaders(Arrays.asList("header1")));
        config.setAllowedHeaders(Collections.emptyList());
        Assert.assertNull(config.checkHeaders(Arrays.asList("header1")));
        config.addAllowedHeader("header2");
        config.addAllowedHeader("header3");
        Assert.assertNull(config.checkHeaders(Arrays.asList("header1")));
    }

    // SPR-15772
    @Test
    public void changePermitDefaultValues() {
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedOrigin("http://domain.com");
        config.addAllowedHeader("header1");
        config.addAllowedMethod("PATCH");
        Assert.assertEquals(Arrays.asList("*", "http://domain.com"), config.getAllowedOrigins());
        Assert.assertEquals(Arrays.asList("*", "header1"), config.getAllowedHeaders());
        Assert.assertEquals(Arrays.asList("GET", "HEAD", "POST", "PATCH"), config.getAllowedMethods());
    }
}

