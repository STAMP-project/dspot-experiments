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
package org.springframework.web.servlet.handler;


import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class SimpleUrlHandlerMappingTests {
    @Test
    @SuppressWarnings("resource")
    public void handlerBeanNotFound() {
        MockServletContext sc = new MockServletContext("");
        XmlWebApplicationContext root = new XmlWebApplicationContext();
        root.setServletContext(sc);
        root.setConfigLocations("/org/springframework/web/servlet/handler/map1.xml");
        root.refresh();
        XmlWebApplicationContext wac = new XmlWebApplicationContext();
        wac.setParent(root);
        wac.setServletContext(sc);
        wac.setNamespace("map2err");
        wac.setConfigLocations("/org/springframework/web/servlet/handler/map2err.xml");
        try {
            wac.refresh();
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (FatalBeanException ex) {
            NoSuchBeanDefinitionException nestedEx = ((NoSuchBeanDefinitionException) (ex.getCause()));
            Assert.assertEquals("mainControlle", nestedEx.getBeanName());
        }
    }

    @Test
    public void urlMappingWithUrlMap() throws Exception {
        checkMappings("urlMapping");
    }

    @Test
    public void urlMappingWithProps() throws Exception {
        checkMappings("urlMappingWithProps");
    }

    @Test
    public void testNewlineInRequest() throws Exception {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setUrlDecode(false);
        Object controller = new Object();
        Map<String, Object> urlMap = new LinkedHashMap<>();
        urlMap.put("/*/baz", controller);
        handlerMapping.setUrlMap(urlMap);
        handlerMapping.setApplicationContext(new StaticApplicationContext());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo%0a%0dbar/baz");
        HandlerExecutionChain hec = handlerMapping.getHandler(request);
        Assert.assertNotNull(hec);
        Assert.assertSame(controller, hec.getHandler());
    }
}

