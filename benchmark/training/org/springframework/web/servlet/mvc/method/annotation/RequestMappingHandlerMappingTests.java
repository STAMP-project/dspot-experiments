/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.servlet.mvc.method.annotation;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_VALUE;
import RequestMethod.DELETE;
import RequestMethod.GET;
import RequestMethod.PATCH;
import RequestMethod.POST;
import RequestMethod.PUT;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;


/**
 * Tests for {@link RequestMappingHandlerMapping}.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 */
public class RequestMappingHandlerMappingTests {
    private final StaticWebApplicationContext wac = new StaticWebApplicationContext();

    private final RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();

    {
        this.handlerMapping.setApplicationContext(wac);
    }

    @Test
    public void useRegisteredSuffixPatternMatch() {
        Assert.assertTrue(this.handlerMapping.useSuffixPatternMatch());
        Assert.assertFalse(this.handlerMapping.useRegisteredSuffixPatternMatch());
        Map<String, MediaType> fileExtensions = Collections.singletonMap("json", APPLICATION_JSON);
        PathExtensionContentNegotiationStrategy strategy = new PathExtensionContentNegotiationStrategy(fileExtensions);
        ContentNegotiationManager manager = new ContentNegotiationManager(strategy);
        this.handlerMapping.setContentNegotiationManager(manager);
        this.handlerMapping.setUseRegisteredSuffixPatternMatch(true);
        this.handlerMapping.afterPropertiesSet();
        Assert.assertTrue(this.handlerMapping.useSuffixPatternMatch());
        Assert.assertTrue(this.handlerMapping.useRegisteredSuffixPatternMatch());
        Assert.assertEquals(Arrays.asList("json"), this.handlerMapping.getFileExtensions());
    }

    @Test
    public void useRegisteredSuffixPatternMatchInitialization() {
        Map<String, MediaType> fileExtensions = Collections.singletonMap("json", APPLICATION_JSON);
        PathExtensionContentNegotiationStrategy strategy = new PathExtensionContentNegotiationStrategy(fileExtensions);
        ContentNegotiationManager manager = new ContentNegotiationManager(strategy);
        final Set<String> extensions = new HashSet<>();
        RequestMappingHandlerMapping hm = new RequestMappingHandlerMapping() {
            @Override
            protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
                extensions.addAll(getFileExtensions());
                return super.getMappingForMethod(method, handlerType);
            }
        };
        wac.registerSingleton("testController", RequestMappingHandlerMappingTests.ComposedAnnotationController.class);
        wac.refresh();
        hm.setContentNegotiationManager(manager);
        hm.setUseRegisteredSuffixPatternMatch(true);
        hm.setApplicationContext(wac);
        hm.afterPropertiesSet();
        Assert.assertEquals(Collections.singleton("json"), extensions);
    }

    @Test
    public void useSuffixPatternMatch() {
        Assert.assertTrue(this.handlerMapping.useSuffixPatternMatch());
        this.handlerMapping.setUseSuffixPatternMatch(false);
        Assert.assertFalse(this.handlerMapping.useSuffixPatternMatch());
        this.handlerMapping.setUseRegisteredSuffixPatternMatch(false);
        Assert.assertFalse("'false' registeredSuffixPatternMatch shouldn't impact suffixPatternMatch", this.handlerMapping.useSuffixPatternMatch());
        this.handlerMapping.setUseRegisteredSuffixPatternMatch(true);
        Assert.assertTrue("'true' registeredSuffixPatternMatch should enable suffixPatternMatch", this.handlerMapping.useSuffixPatternMatch());
    }

    @Test
    public void resolveEmbeddedValuesInPatterns() {
        this.handlerMapping.setEmbeddedValueResolver(( value) -> "/${pattern}/bar".equals(value) ? "/foo/bar" : value);
        String[] patterns = new String[]{ "/foo", "/${pattern}/bar" };
        String[] result = this.handlerMapping.resolveEmbeddedValuesInPatterns(patterns);
        Assert.assertArrayEquals(new String[]{ "/foo", "/foo/bar" }, result);
    }

    @Test
    public void pathPrefix() throws NoSuchMethodException {
        this.handlerMapping.setEmbeddedValueResolver(( value) -> "/${prefix}".equals(value) ? "/api" : value);
        this.handlerMapping.setPathPrefixes(Collections.singletonMap("/${prefix}", HandlerTypePredicate.forAnnotation(RestController.class)));
        Method method = RequestMappingHandlerMappingTests.UserController.class.getMethod("getUser");
        RequestMappingInfo info = this.handlerMapping.getMappingForMethod(method, RequestMappingHandlerMappingTests.UserController.class);
        Assert.assertNotNull(info);
        Assert.assertEquals(Collections.singleton("/api/user/{id}"), info.getPatternsCondition().getPatterns());
    }

    @Test
    public void resolveRequestMappingViaComposedAnnotation() throws Exception {
        RequestMappingInfo info = assertComposedAnnotationMapping("postJson", "/postJson", POST);
        Assert.assertEquals(APPLICATION_JSON_VALUE, info.getConsumesCondition().getConsumableMediaTypes().iterator().next().toString());
        Assert.assertEquals(APPLICATION_JSON_VALUE, info.getProducesCondition().getProducibleMediaTypes().iterator().next().toString());
    }

    // SPR-14988
    @Test
    public void getMappingOverridesConsumesFromTypeLevelAnnotation() throws Exception {
        RequestMappingInfo requestMappingInfo = assertComposedAnnotationMapping(GET);
        Assert.assertArrayEquals(new MediaType[]{ MediaType.ALL }, new java.util.ArrayList(requestMappingInfo.getConsumesCondition().getConsumableMediaTypes()).toArray());
    }

    @Test
    public void getMapping() throws Exception {
        assertComposedAnnotationMapping(GET);
    }

    @Test
    public void postMapping() throws Exception {
        assertComposedAnnotationMapping(POST);
    }

    @Test
    public void putMapping() throws Exception {
        assertComposedAnnotationMapping(PUT);
    }

    @Test
    public void deleteMapping() throws Exception {
        assertComposedAnnotationMapping(DELETE);
    }

    @Test
    public void patchMapping() throws Exception {
        assertComposedAnnotationMapping(PATCH);
    }

    @Controller
    @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    static class ComposedAnnotationController {
        @RequestMapping
        public void handle() {
        }

        @RequestMappingHandlerMappingTests.PostJson("/postJson")
        public void postJson() {
        }

        @GetMapping(value = "/get", consumes = MediaType.ALL_VALUE)
        public void get() {
        }

        @PostMapping("/post")
        public void post() {
        }

        @PutMapping("/put")
        public void put() {
        }

        @DeleteMapping("/delete")
        public void delete() {
        }

        @PatchMapping("/patch")
        public void patch() {
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface PostJson {
        @AliasFor(annotation = RequestMapping.class, attribute = "path")
        String[] value() default {  };
    }

    @RestController
    @RequestMapping("/user")
    static class UserController {
        @GetMapping("/{id}")
        public Principal getUser() {
            return Mockito.mock(Principal.class);
        }
    }
}

