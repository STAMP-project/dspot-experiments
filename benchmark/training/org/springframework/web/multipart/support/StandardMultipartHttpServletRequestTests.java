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
package org.springframework.web.multipart.support;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;


/**
 * Unit tests for {@link StandardMultipartHttpServletRequest}.
 *
 * @author Rossen Stoyanchev
 */
public class StandardMultipartHttpServletRequestTests {
    @Test
    public void filename() throws Exception {
        String disposition = "form-data; name=\"file\"; filename=\"myFile.txt\"";
        StandardMultipartHttpServletRequest request = requestWithPart("file", disposition, "");
        MultipartFile multipartFile = request.getFile("file");
        Assert.assertNotNull(multipartFile);
        Assert.assertEquals("myFile.txt", multipartFile.getOriginalFilename());
    }

    // SPR-13319
    @Test
    public void filenameRfc5987() throws Exception {
        String disposition = "form-data; name=\"file\"; filename*=\"UTF-8\'\'foo-%c3%a4-%e2%82%ac.html\"";
        StandardMultipartHttpServletRequest request = requestWithPart("file", disposition, "");
        MultipartFile multipartFile = request.getFile("file");
        Assert.assertNotNull(multipartFile);
        Assert.assertEquals("foo-?-?.html", multipartFile.getOriginalFilename());
    }

    // SPR-15205
    @Test
    public void filenameRfc2047() throws Exception {
        String disposition = "form-data; name=\"file\"; filename=\"=?UTF-8?Q?Declara=C3=A7=C3=A3o.pdf?=\"";
        StandardMultipartHttpServletRequest request = requestWithPart("file", disposition, "");
        MultipartFile multipartFile = request.getFile("file");
        Assert.assertNotNull(multipartFile);
        Assert.assertEquals("Declara??o.pdf", multipartFile.getOriginalFilename());
    }

    @Test
    public void multipartFileResource() throws IOException {
        String name = "file";
        String disposition = ("form-data; name=\"" + name) + "\"; filename=\"myFile.txt\"";
        StandardMultipartHttpServletRequest request = requestWithPart(name, disposition, "myBody");
        MultipartFile multipartFile = request.getFile(name);
        Assert.assertNotNull(multipartFile);
        MultiValueMap<String, Object> map = new org.springframework.util.LinkedMultiValueMap();
        map.add(name, multipartFile.getResource());
        MockHttpOutputMessage output = new MockHttpOutputMessage();
        new FormHttpMessageConverter().write(map, null, output);
        Assert.assertThat(output.getBodyAsString(StandardCharsets.UTF_8), CoreMatchers.containsString(("Content-Disposition: form-data; name=\"file\"; filename=\"myFile.txt\"\r\n" + ((("Content-Type: text/plain\r\n" + "Content-Length: 6\r\n") + "\r\n") + "myBody\r\n"))));
    }
}

