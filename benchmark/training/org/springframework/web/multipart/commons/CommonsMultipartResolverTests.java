/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.web.multipart.commons;


import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import WebUtils.TEMP_DIR_CONTEXT_ATTRIBUTE;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.mock.web.test.MockFilterConfig;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.mock.web.test.PassThroughFilterChain;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.MultipartFilter;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @since 08.10.2003
 */
public class CommonsMultipartResolverTests {
    @Test
    public void withApplicationContext() throws Exception {
        doTestWithApplicationContext(false);
    }

    @Test
    public void withApplicationContextAndLazyResolution() throws Exception {
        doTestWithApplicationContext(true);
    }

    @Test
    public void withServletContextAndFilter() throws Exception {
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(new MockServletContext());
        wac.registerSingleton("filterMultipartResolver", CommonsMultipartResolverTests.MockCommonsMultipartResolver.class, new MutablePropertyValues());
        wac.getServletContext().setAttribute(TEMP_DIR_CONTEXT_ATTRIBUTE, new File("mytemp"));
        wac.refresh();
        wac.getServletContext().setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        CommonsMultipartResolver resolver = new CommonsMultipartResolver(wac.getServletContext());
        Assert.assertTrue(resolver.getFileItemFactory().getRepository().getAbsolutePath().endsWith("mytemp"));
        MockFilterConfig filterConfig = new MockFilterConfig(wac.getServletContext(), "filter");
        filterConfig.addInitParameter("class", "notWritable");
        filterConfig.addInitParameter("unknownParam", "someValue");
        final MultipartFilter filter = new MultipartFilter();
        filter.init(filterConfig);
        final List<MultipartFile> files = new ArrayList<>();
        final FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) {
                MultipartHttpServletRequest request = ((MultipartHttpServletRequest) (servletRequest));
                files.addAll(request.getFileMap().values());
            }
        };
        FilterChain filterChain2 = new PassThroughFilterChain(filter, filterChain);
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        originalRequest.setMethod("POST");
        originalRequest.setContentType("multipart/form-data");
        originalRequest.addHeader("Content-type", "multipart/form-data");
        filter.doFilter(originalRequest, response, filterChain2);
        CommonsMultipartFile file1 = ((CommonsMultipartFile) (files.get(0)));
        CommonsMultipartFile file2 = ((CommonsMultipartFile) (files.get(1)));
        Assert.assertTrue(((CommonsMultipartResolverTests.MockFileItem) (file1.getFileItem())).deleted);
        Assert.assertTrue(((CommonsMultipartResolverTests.MockFileItem) (file2.getFileItem())).deleted);
    }

    @Test
    public void withServletContextAndFilterWithCustomBeanName() throws Exception {
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(new MockServletContext());
        wac.refresh();
        wac.registerSingleton("myMultipartResolver", CommonsMultipartResolverTests.MockCommonsMultipartResolver.class, new MutablePropertyValues());
        wac.getServletContext().setAttribute(TEMP_DIR_CONTEXT_ATTRIBUTE, new File("mytemp"));
        wac.getServletContext().setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        CommonsMultipartResolver resolver = new CommonsMultipartResolver(wac.getServletContext());
        Assert.assertTrue(resolver.getFileItemFactory().getRepository().getAbsolutePath().endsWith("mytemp"));
        MockFilterConfig filterConfig = new MockFilterConfig(wac.getServletContext(), "filter");
        filterConfig.addInitParameter("multipartResolverBeanName", "myMultipartResolver");
        final List<MultipartFile> files = new ArrayList<>();
        FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest originalRequest, ServletResponse response) {
                if (originalRequest instanceof MultipartHttpServletRequest) {
                    MultipartHttpServletRequest request = ((MultipartHttpServletRequest) (originalRequest));
                    files.addAll(request.getFileMap().values());
                }
            }
        };
        MultipartFilter filter = new MultipartFilter() {
            private boolean invoked = false;

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
                super.doFilterInternal(request, response, filterChain);
                super.doFilterInternal(request, response, filterChain);
                if (invoked) {
                    throw new ServletException("Should not have been invoked twice");
                }
                invoked = true;
            }
        };
        filter.init(filterConfig);
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        originalRequest.setMethod("POST");
        originalRequest.setContentType("multipart/form-data");
        originalRequest.addHeader("Content-type", "multipart/form-data");
        HttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(originalRequest, response, filterChain);
        CommonsMultipartFile file1 = ((CommonsMultipartFile) (files.get(0)));
        CommonsMultipartFile file2 = ((CommonsMultipartFile) (files.get(1)));
        Assert.assertTrue(((CommonsMultipartResolverTests.MockFileItem) (file1.getFileItem())).deleted);
        Assert.assertTrue(((CommonsMultipartResolverTests.MockFileItem) (file2.getFileItem())).deleted);
    }

    public static class MockCommonsMultipartResolver extends CommonsMultipartResolver {
        private boolean empty;

        protected void setEmpty(boolean empty) {
            this.empty = empty;
        }

        @Override
        protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
            return new ServletFileUpload() {
                @Override
                public List<FileItem> parseRequest(HttpServletRequest request) {
                    if (request instanceof MultipartHttpServletRequest) {
                        throw new IllegalStateException("Already a multipart request");
                    }
                    List<FileItem> fileItems = new ArrayList<>();
                    CommonsMultipartResolverTests.MockFileItem fileItem1 = new CommonsMultipartResolverTests.MockFileItem("field1", "type1", (empty ? "" : "field1.txt"), (empty ? "" : "text1"));
                    CommonsMultipartResolverTests.MockFileItem fileItem1x = new CommonsMultipartResolverTests.MockFileItem("field1", "type1", (empty ? "" : "field1.txt"), (empty ? "" : "text1"));
                    CommonsMultipartResolverTests.MockFileItem fileItem2 = new CommonsMultipartResolverTests.MockFileItem("field2", "type2", (empty ? "" : "C:\\mypath/field2.txt"), (empty ? "" : "text2"));
                    CommonsMultipartResolverTests.MockFileItem fileItem2x = new CommonsMultipartResolverTests.MockFileItem("field2x", "type2", (empty ? "" : "C:/mypath\\field2x.txt"), (empty ? "" : "text2"));
                    CommonsMultipartResolverTests.MockFileItem fileItem3 = new CommonsMultipartResolverTests.MockFileItem("field3", null, null, "value3");
                    CommonsMultipartResolverTests.MockFileItem fileItem4 = new CommonsMultipartResolverTests.MockFileItem("field4", "text/html; charset=iso-8859-1", null, "value4");
                    CommonsMultipartResolverTests.MockFileItem fileItem5 = new CommonsMultipartResolverTests.MockFileItem("field4", null, null, "value5");
                    fileItems.add(fileItem1);
                    fileItems.add(fileItem1x);
                    fileItems.add(fileItem2);
                    fileItems.add(fileItem2x);
                    fileItems.add(fileItem3);
                    fileItems.add(fileItem4);
                    fileItems.add(fileItem5);
                    return fileItems;
                }
            };
        }
    }

    @SuppressWarnings("serial")
    private static class MockFileItem implements FileItem {
        private String fieldName;

        private String contentType;

        private String name;

        private String value;

        private File writtenFile;

        private boolean deleted;

        public MockFileItem(String fieldName, String contentType, String name, String value) {
            this.fieldName = fieldName;
            this.contentType = contentType;
            this.name = name;
            this.value = value;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(value.getBytes());
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isInMemory() {
            return true;
        }

        @Override
        public long getSize() {
            return value.length();
        }

        @Override
        public byte[] get() {
            return value.getBytes();
        }

        @Override
        public String getString(String encoding) throws UnsupportedEncodingException {
            return new String(get(), encoding);
        }

        @Override
        public String getString() {
            return value;
        }

        @Override
        public void write(File file) throws Exception {
            this.writtenFile = file;
        }

        @Override
        public void delete() {
            this.deleted = true;
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public void setFieldName(String s) {
            this.fieldName = s;
        }

        @Override
        public boolean isFormField() {
            return (this.name) == null;
        }

        @Override
        public void setFormField(boolean b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public FileItemHeaders getHeaders() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setHeaders(FileItemHeaders headers) {
            throw new UnsupportedOperationException();
        }
    }

    public class MultipartTestBean1 {
        private MultipartFile[] field1;

        private byte[] field2;

        public void setField1(MultipartFile[] field1) {
            this.field1 = field1;
        }

        public MultipartFile[] getField1() {
            return field1;
        }

        public void setField2(byte[] field2) {
            this.field2 = field2;
        }

        public byte[] getField2() {
            return field2;
        }
    }

    public class MultipartTestBean2 {
        private String[] field1;

        private String field2;

        public void setField1(String[] field1) {
            this.field1 = field1;
        }

        public String[] getField1() {
            return field1;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public String getField2() {
            return field2;
        }
    }
}

