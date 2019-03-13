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
package org.springframework.web.bind.support;


import HttpServletResponse.SC_OK;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.ServletWebRequest;


/**
 *
 *
 * @author Brian Clozel
 * @author Sam Brannen
 */
public class WebRequestDataBinderIntegrationTests {
    private static Server jettyServer;

    private static final WebRequestDataBinderIntegrationTests.PartsServlet partsServlet = new WebRequestDataBinderIntegrationTests.PartsServlet();

    private static final WebRequestDataBinderIntegrationTests.PartListServlet partListServlet = new WebRequestDataBinderIntegrationTests.PartListServlet();

    private final RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    protected static String baseUrl;

    protected static MediaType contentType;

    @Test
    public void partsBinding() {
        WebRequestDataBinderIntegrationTests.PartsBean bean = new WebRequestDataBinderIntegrationTests.PartsBean();
        WebRequestDataBinderIntegrationTests.partsServlet.setBean(bean);
        MultiValueMap<String, Object> parts = new org.springframework.util.LinkedMultiValueMap();
        Resource firstPart = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
        parts.add("firstPart", firstPart);
        parts.add("secondPart", "secondValue");
        template.postForLocation(((WebRequestDataBinderIntegrationTests.baseUrl) + "/parts"), parts);
        Assert.assertNotNull(bean.getFirstPart());
        Assert.assertNotNull(bean.getSecondPart());
    }

    @Test
    public void partListBinding() {
        WebRequestDataBinderIntegrationTests.PartListBean bean = new WebRequestDataBinderIntegrationTests.PartListBean();
        WebRequestDataBinderIntegrationTests.partListServlet.setBean(bean);
        MultiValueMap<String, Object> parts = new org.springframework.util.LinkedMultiValueMap();
        parts.add("partList", "first value");
        parts.add("partList", "second value");
        Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
        parts.add("partList", logo);
        template.postForLocation(((WebRequestDataBinderIntegrationTests.baseUrl) + "/partlist"), parts);
        Assert.assertNotNull(bean.getPartList());
        Assert.assertEquals(parts.get("partList").size(), bean.getPartList().size());
    }

    @SuppressWarnings("serial")
    private abstract static class AbstractStandardMultipartServlet<T> extends HttpServlet {
        private T bean;

        @Override
        public void service(HttpServletRequest request, HttpServletResponse response) {
            WebRequestDataBinder binder = new WebRequestDataBinder(bean);
            ServletWebRequest webRequest = new ServletWebRequest(request, response);
            binder.bind(webRequest);
            response.setStatus(SC_OK);
        }

        public void setBean(T bean) {
            this.bean = bean;
        }
    }

    private static class PartsBean {
        public Part firstPart;

        public Part secondPart;

        public Part getFirstPart() {
            return firstPart;
        }

        @SuppressWarnings("unused")
        public void setFirstPart(Part firstPart) {
            this.firstPart = firstPart;
        }

        public Part getSecondPart() {
            return secondPart;
        }

        @SuppressWarnings("unused")
        public void setSecondPart(Part secondPart) {
            this.secondPart = secondPart;
        }
    }

    @SuppressWarnings("serial")
    private static class PartsServlet extends WebRequestDataBinderIntegrationTests.AbstractStandardMultipartServlet<WebRequestDataBinderIntegrationTests.PartsBean> {}

    private static class PartListBean {
        public List<Part> partList;

        public List<Part> getPartList() {
            return partList;
        }

        @SuppressWarnings("unused")
        public void setPartList(List<Part> partList) {
            this.partList = partList;
        }
    }

    @SuppressWarnings("serial")
    private static class PartListServlet extends WebRequestDataBinderIntegrationTests.AbstractStandardMultipartServlet<WebRequestDataBinderIntegrationTests.PartListBean> {}
}

