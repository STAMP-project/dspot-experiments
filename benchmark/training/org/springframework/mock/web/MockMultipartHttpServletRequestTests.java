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
package org.springframework.mock.web;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class MockMultipartHttpServletRequestTests {
    @Test
    public void mockMultipartHttpServletRequestWithByteArray() throws IOException {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        Assert.assertFalse(request.getFileNames().hasNext());
        Assert.assertNull(request.getFile("file1"));
        Assert.assertNull(request.getFile("file2"));
        Assert.assertTrue(request.getFileMap().isEmpty());
        request.addFile(new MockMultipartFile("file1", "myContent1".getBytes()));
        request.addFile(new MockMultipartFile("file2", "myOrigFilename", "text/plain", "myContent2".getBytes()));
        doTestMultipartHttpServletRequest(request);
    }

    @Test
    public void mockMultipartHttpServletRequestWithInputStream() throws IOException {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.addFile(new MockMultipartFile("file1", new ByteArrayInputStream("myContent1".getBytes())));
        request.addFile(new MockMultipartFile("file2", "myOrigFilename", "text/plain", new ByteArrayInputStream("myContent2".getBytes())));
        doTestMultipartHttpServletRequest(request);
    }
}

