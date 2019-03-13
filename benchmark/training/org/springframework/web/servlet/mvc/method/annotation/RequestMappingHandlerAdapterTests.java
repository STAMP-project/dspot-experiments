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


import DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE;
import HttpStatus.OK;
import MediaType.APPLICATION_JSON_VALUE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;


/**
 * Unit tests for {@link RequestMappingHandlerAdapter}.
 *
 * @author Rossen Stoyanchev
 * @see ServletAnnotationControllerHandlerMethodTests
 * @see HandlerMethodAnnotationDetectionTests
 * @see RequestMappingHandlerAdapterIntegrationTests
 */
public class RequestMappingHandlerAdapterTests {
    private static int RESOLVER_COUNT;

    private static int INIT_BINDER_RESOLVER_COUNT;

    private static int HANDLER_COUNT;

    private RequestMappingHandlerAdapter handlerAdapter;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private StaticWebApplicationContext webAppContext;

    @Test
    public void cacheControlWithoutSessionAttributes() throws Exception {
        HandlerMethod handlerMethod = handlerMethod(new RequestMappingHandlerAdapterTests.SimpleController(), "handle");
        this.handlerAdapter.setCacheSeconds(100);
        this.handlerAdapter.afterPropertiesSet();
        this.handlerAdapter.handle(this.request, this.response, handlerMethod);
        Assert.assertTrue(response.getHeader("Cache-Control").contains("max-age"));
    }

    @Test
    public void cacheControlWithSessionAttributes() throws Exception {
        RequestMappingHandlerAdapterTests.SessionAttributeController handler = new RequestMappingHandlerAdapterTests.SessionAttributeController();
        this.handlerAdapter.setCacheSeconds(100);
        this.handlerAdapter.afterPropertiesSet();
        this.handlerAdapter.handle(this.request, this.response, handlerMethod(handler, "handle"));
        Assert.assertEquals("no-store", this.response.getHeader("Cache-Control"));
    }

    @Test
    public void setAlwaysUseRedirectAttributes() throws Exception {
        HandlerMethodArgumentResolver redirectAttributesResolver = new RedirectAttributesMethodArgumentResolver();
        HandlerMethodArgumentResolver modelResolver = new ModelMethodProcessor();
        HandlerMethodReturnValueHandler viewHandler = new ViewNameMethodReturnValueHandler();
        this.handlerAdapter.setArgumentResolvers(Arrays.asList(redirectAttributesResolver, modelResolver));
        this.handlerAdapter.setReturnValueHandlers(Collections.singletonList(viewHandler));
        this.handlerAdapter.setIgnoreDefaultModelOnRedirect(true);
        this.handlerAdapter.afterPropertiesSet();
        this.request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
        HandlerMethod handlerMethod = handlerMethod(new RequestMappingHandlerAdapterTests.RedirectAttributeController(), "handle", Model.class);
        ModelAndView mav = this.handlerAdapter.handle(request, response, handlerMethod);
        Assert.assertTrue("Without RedirectAttributes arg, model should be empty", mav.getModel().isEmpty());
    }

    @Test
    public void setCustomArgumentResolvers() throws Exception {
        HandlerMethodArgumentResolver resolver = new ServletRequestMethodArgumentResolver();
        this.handlerAdapter.setCustomArgumentResolvers(Collections.singletonList(resolver));
        this.handlerAdapter.afterPropertiesSet();
        Assert.assertTrue(this.handlerAdapter.getArgumentResolvers().contains(resolver));
        assertMethodProcessorCount(((RequestMappingHandlerAdapterTests.RESOLVER_COUNT) + 1), ((RequestMappingHandlerAdapterTests.INIT_BINDER_RESOLVER_COUNT) + 1), RequestMappingHandlerAdapterTests.HANDLER_COUNT);
    }

    @Test
    public void setArgumentResolvers() throws Exception {
        HandlerMethodArgumentResolver resolver = new ServletRequestMethodArgumentResolver();
        this.handlerAdapter.setArgumentResolvers(Collections.singletonList(resolver));
        this.handlerAdapter.afterPropertiesSet();
        assertMethodProcessorCount(1, RequestMappingHandlerAdapterTests.INIT_BINDER_RESOLVER_COUNT, RequestMappingHandlerAdapterTests.HANDLER_COUNT);
    }

    @Test
    public void setInitBinderArgumentResolvers() throws Exception {
        HandlerMethodArgumentResolver resolver = new ServletRequestMethodArgumentResolver();
        this.handlerAdapter.setInitBinderArgumentResolvers(Collections.singletonList(resolver));
        this.handlerAdapter.afterPropertiesSet();
        assertMethodProcessorCount(RequestMappingHandlerAdapterTests.RESOLVER_COUNT, 1, RequestMappingHandlerAdapterTests.HANDLER_COUNT);
    }

    @Test
    public void setCustomReturnValueHandlers() {
        HandlerMethodReturnValueHandler handler = new ViewNameMethodReturnValueHandler();
        this.handlerAdapter.setCustomReturnValueHandlers(Collections.singletonList(handler));
        this.handlerAdapter.afterPropertiesSet();
        Assert.assertTrue(this.handlerAdapter.getReturnValueHandlers().contains(handler));
        assertMethodProcessorCount(RequestMappingHandlerAdapterTests.RESOLVER_COUNT, RequestMappingHandlerAdapterTests.INIT_BINDER_RESOLVER_COUNT, ((RequestMappingHandlerAdapterTests.HANDLER_COUNT) + 1));
    }

    @Test
    public void setReturnValueHandlers() {
        HandlerMethodReturnValueHandler handler = new ModelMethodProcessor();
        this.handlerAdapter.setReturnValueHandlers(Collections.singletonList(handler));
        this.handlerAdapter.afterPropertiesSet();
        assertMethodProcessorCount(RequestMappingHandlerAdapterTests.RESOLVER_COUNT, RequestMappingHandlerAdapterTests.INIT_BINDER_RESOLVER_COUNT, 1);
    }

    @Test
    public void modelAttributeAdvice() throws Exception {
        this.webAppContext.registerSingleton("maa", RequestMappingHandlerAdapterTests.ModelAttributeAdvice.class);
        this.webAppContext.refresh();
        HandlerMethod handlerMethod = handlerMethod(new RequestMappingHandlerAdapterTests.SimpleController(), "handle");
        this.handlerAdapter.afterPropertiesSet();
        ModelAndView mav = this.handlerAdapter.handle(this.request, this.response, handlerMethod);
        Assert.assertEquals("lAttr1", mav.getModel().get("attr1"));
        Assert.assertEquals("gAttr2", mav.getModel().get("attr2"));
    }

    @Test
    public void modelAttributeAdviceInParentContext() throws Exception {
        StaticWebApplicationContext parent = new StaticWebApplicationContext();
        parent.registerSingleton("maa", RequestMappingHandlerAdapterTests.ModelAttributeAdvice.class);
        parent.refresh();
        this.webAppContext.setParent(parent);
        this.webAppContext.refresh();
        HandlerMethod handlerMethod = handlerMethod(new RequestMappingHandlerAdapterTests.SimpleController(), "handle");
        this.handlerAdapter.afterPropertiesSet();
        ModelAndView mav = this.handlerAdapter.handle(this.request, this.response, handlerMethod);
        Assert.assertEquals("lAttr1", mav.getModel().get("attr1"));
        Assert.assertEquals("gAttr2", mav.getModel().get("attr2"));
    }

    @Test
    public void modelAttributePackageNameAdvice() throws Exception {
        this.webAppContext.registerSingleton("mapa", RequestMappingHandlerAdapterTests.ModelAttributePackageAdvice.class);
        this.webAppContext.registerSingleton("manupa", RequestMappingHandlerAdapterTests.ModelAttributeNotUsedPackageAdvice.class);
        this.webAppContext.refresh();
        HandlerMethod handlerMethod = handlerMethod(new RequestMappingHandlerAdapterTests.SimpleController(), "handle");
        this.handlerAdapter.afterPropertiesSet();
        ModelAndView mav = this.handlerAdapter.handle(this.request, this.response, handlerMethod);
        Assert.assertEquals("lAttr1", mav.getModel().get("attr1"));
        Assert.assertEquals("gAttr2", mav.getModel().get("attr2"));
        Assert.assertEquals(null, mav.getModel().get("attr3"));
    }

    // SPR-10859
    @Test
    public void responseBodyAdvice() throws Exception {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        this.handlerAdapter.setMessageConverters(converters);
        this.webAppContext.registerSingleton("rba", RequestMappingHandlerAdapterTests.ResponseCodeSuppressingAdvice.class);
        this.webAppContext.refresh();
        this.request.addHeader("Accept", APPLICATION_JSON_VALUE);
        this.request.setParameter("c", "callback");
        HandlerMethod handlerMethod = handlerMethod(new RequestMappingHandlerAdapterTests.SimpleController(), "handleBadRequest");
        this.handlerAdapter.afterPropertiesSet();
        this.handlerAdapter.handle(this.request, this.response, handlerMethod);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("{\"status\":400,\"message\":\"body\"}", this.response.getContentAsString());
    }

    @SuppressWarnings("unused")
    private static class SimpleController {
        @ModelAttribute
        public void addAttributes(Model model) {
            model.addAttribute("attr1", "lAttr1");
        }

        public String handle() {
            return null;
        }

        public ResponseEntity<Map<String, String>> handleWithResponseEntity() {
            return new ResponseEntity(Collections.singletonMap("foo", "bar"), HttpStatus.OK);
        }

        public ResponseEntity<String> handleBadRequest() {
            return new ResponseEntity("body", HttpStatus.BAD_REQUEST);
        }
    }

    @SessionAttributes("attr1")
    private static class SessionAttributeController {
        @SuppressWarnings("unused")
        public void handle() {
        }
    }

    @SuppressWarnings("unused")
    private static class RedirectAttributeController {
        public String handle(Model model) {
            model.addAttribute("someAttr", "someAttrValue");
            return "redirect:/path";
        }
    }

    @ControllerAdvice
    private static class ModelAttributeAdvice {
        @SuppressWarnings("unused")
        @ModelAttribute
        public void addAttributes(Model model) {
            model.addAttribute("attr1", "gAttr1");
            model.addAttribute("attr2", "gAttr2");
        }
    }

    @ControllerAdvice({ "org.springframework.web.servlet.mvc.method.annotation", "java.lang" })
    private static class ModelAttributePackageAdvice {
        @SuppressWarnings("unused")
        @ModelAttribute
        public void addAttributes(Model model) {
            model.addAttribute("attr2", "gAttr2");
        }
    }

    @ControllerAdvice("java.lang")
    private static class ModelAttributeNotUsedPackageAdvice {
        @SuppressWarnings("unused")
        @ModelAttribute
        public void addAttributes(Model model) {
            model.addAttribute("attr3", "gAttr3");
        }
    }

    @ControllerAdvice
    private static class ResponseCodeSuppressingAdvice extends AbstractMappingJacksonResponseBodyAdvice {
        @Override
        protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType, MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
            int status = getServletResponse().getStatus();
            response.setStatusCode(OK);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", status);
            map.put("message", bodyContainer.getValue());
            bodyContainer.setValue(map);
        }
    }
}

