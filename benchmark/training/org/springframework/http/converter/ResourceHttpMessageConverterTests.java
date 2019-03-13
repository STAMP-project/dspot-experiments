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
package org.springframework.http.converter;


import MediaType.ALL;
import MediaType.APPLICATION_OCTET_STREAM;
import MediaType.IMAGE_JPEG;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.util.FileCopyUtils;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Kazuki Shimizu
 * @author Brian Clozel
 */
public class ResourceHttpMessageConverterTests {
    private final ResourceHttpMessageConverter converter = new ResourceHttpMessageConverter();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canReadResource() {
        Assert.assertTrue(converter.canRead(Resource.class, new MediaType("application", "octet-stream")));
    }

    @Test
    public void canWriteResource() {
        Assert.assertTrue(converter.canWrite(Resource.class, new MediaType("application", "octet-stream")));
        Assert.assertTrue(converter.canWrite(Resource.class, ALL));
    }

    @Test
    public void shouldReadImageResource() throws IOException {
        byte[] body = FileCopyUtils.copyToByteArray(getClass().getResourceAsStream("logo.jpg"));
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body);
        inputMessage.getHeaders().setContentType(IMAGE_JPEG);
        inputMessage.getHeaders().setContentDisposition(ContentDisposition.builder("attachment").filename("yourlogo.jpg").build());
        Resource actualResource = converter.read(Resource.class, inputMessage);
        Assert.assertThat(FileCopyUtils.copyToByteArray(actualResource.getInputStream()), Is.is(body));
        Assert.assertEquals("yourlogo.jpg", actualResource.getFilename());
    }

    // SPR-13443
    @Test
    public void shouldReadInputStreamResource() throws IOException {
        try (InputStream body = getClass().getResourceAsStream("logo.jpg")) {
            MockHttpInputMessage inputMessage = new MockHttpInputMessage(body);
            inputMessage.getHeaders().setContentType(IMAGE_JPEG);
            inputMessage.getHeaders().setContentDisposition(ContentDisposition.builder("attachment").filename("yourlogo.jpg").build());
            Resource actualResource = converter.read(InputStreamResource.class, inputMessage);
            Assert.assertThat(actualResource, IsInstanceOf.instanceOf(InputStreamResource.class));
            Assert.assertThat(actualResource.getInputStream(), Is.is(body));
            Assert.assertEquals("yourlogo.jpg", actualResource.getFilename());
        }
    }

    // SPR-14882
    @Test
    public void shouldNotReadInputStreamResource() throws IOException {
        ResourceHttpMessageConverter noStreamConverter = new ResourceHttpMessageConverter(false);
        try (InputStream body = getClass().getResourceAsStream("logo.jpg")) {
            this.thrown.expect(HttpMessageNotReadableException.class);
            MockHttpInputMessage inputMessage = new MockHttpInputMessage(body);
            inputMessage.getHeaders().setContentType(IMAGE_JPEG);
            noStreamConverter.read(InputStreamResource.class, inputMessage);
        }
    }

    @Test
    public void shouldWriteImageResource() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        Resource body = new ClassPathResource("logo.jpg", getClass());
        converter.write(body, null, outputMessage);
        Assert.assertEquals("Invalid content-type", IMAGE_JPEG, outputMessage.getHeaders().getContentType());
        Assert.assertEquals("Invalid content-length", body.getFile().length(), outputMessage.getHeaders().getContentLength());
    }

    // SPR-10848
    @Test
    public void writeByteArrayNullMediaType() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        byte[] byteArray = new byte[]{ 1, 2, 3 };
        Resource body = new ByteArrayResource(byteArray);
        converter.write(body, null, outputMessage);
        Assert.assertTrue(Arrays.equals(byteArray, outputMessage.getBodyAsBytes()));
    }

    // SPR-12999
    @Test
    @SuppressWarnings("unchecked")
    public void writeContentNotGettingInputStream() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        Resource resource = Mockito.mock(Resource.class);
        BDDMockito.given(resource.getInputStream()).willThrow(FileNotFoundException.class);
        converter.write(resource, APPLICATION_OCTET_STREAM, outputMessage);
        Assert.assertEquals(0, outputMessage.getHeaders().getContentLength());
    }

    // SPR-12999
    @Test
    public void writeContentNotClosingInputStream() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        Resource resource = Mockito.mock(Resource.class);
        InputStream inputStream = Mockito.mock(InputStream.class);
        BDDMockito.given(resource.getInputStream()).willReturn(inputStream);
        BDDMockito.given(inputStream.read(ArgumentMatchers.any())).willReturn((-1));
        Mockito.doThrow(new NullPointerException()).when(inputStream).close();
        converter.write(resource, APPLICATION_OCTET_STREAM, outputMessage);
        Assert.assertEquals(0, outputMessage.getHeaders().getContentLength());
    }

    // SPR-13620
    @Test
    @SuppressWarnings("unchecked")
    public void writeContentInputStreamThrowingNullPointerException() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        Resource resource = Mockito.mock(Resource.class);
        InputStream in = Mockito.mock(InputStream.class);
        BDDMockito.given(resource.getInputStream()).willReturn(in);
        BDDMockito.given(in.read(ArgumentMatchers.any())).willThrow(NullPointerException.class);
        converter.write(resource, APPLICATION_OCTET_STREAM, outputMessage);
        Assert.assertEquals(0, outputMessage.getHeaders().getContentLength());
    }
}

