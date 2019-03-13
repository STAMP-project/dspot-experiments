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


import HttpHeaders.CONTENT_RANGE;
import MediaType.ALL;
import MediaType.APPLICATION_OCTET_STREAM;
import MediaType.TEXT_PLAIN;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.util.StringUtils;


/**
 * Test cases for {@link ResourceRegionHttpMessageConverter} class.
 *
 * @author Brian Clozel
 */
public class ResourceRegionHttpMessageConverterTests {
    private final ResourceRegionHttpMessageConverter converter = new ResourceRegionHttpMessageConverter();

    @Test
    public void canReadResource() {
        Assert.assertFalse(converter.canRead(Resource.class, APPLICATION_OCTET_STREAM));
        Assert.assertFalse(converter.canRead(Resource.class, ALL));
        Assert.assertFalse(converter.canRead(List.class, APPLICATION_OCTET_STREAM));
        Assert.assertFalse(converter.canRead(List.class, ALL));
    }

    @Test
    public void canWriteResource() {
        Assert.assertTrue(converter.canWrite(ResourceRegion.class, null, APPLICATION_OCTET_STREAM));
        Assert.assertTrue(converter.canWrite(ResourceRegion.class, null, ALL));
        Assert.assertFalse(converter.canWrite(Object.class, null, ALL));
    }

    @Test
    public void canWriteResourceCollection() {
        Type resourceRegionList = getType();
        Assert.assertTrue(converter.canWrite(resourceRegionList, null, APPLICATION_OCTET_STREAM));
        Assert.assertTrue(converter.canWrite(resourceRegionList, null, ALL));
        Assert.assertFalse(converter.canWrite(List.class, APPLICATION_OCTET_STREAM));
        Assert.assertFalse(converter.canWrite(List.class, ALL));
        Type resourceObjectList = getType();
        Assert.assertFalse(converter.canWrite(resourceObjectList, null, ALL));
    }

    @Test
    public void shouldWritePartialContentByteRange() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        Resource body = new ClassPathResource("byterangeresource.txt", getClass());
        ResourceRegion region = HttpRange.createByteRange(0, 5).toResourceRegion(body);
        converter.write(region, TEXT_PLAIN, outputMessage);
        org.springframework.http.HttpHeaders headers = outputMessage.getHeaders();
        Assert.assertThat(headers.getContentType(), Matchers.is(TEXT_PLAIN));
        Assert.assertThat(headers.getContentLength(), Matchers.is(6L));
        Assert.assertThat(headers.get(CONTENT_RANGE).size(), Matchers.is(1));
        Assert.assertThat(headers.get(CONTENT_RANGE).get(0), Matchers.is("bytes 0-5/39"));
        Assert.assertThat(outputMessage.getBodyAsString(StandardCharsets.UTF_8), Matchers.is("Spring"));
    }

    @Test
    public void shouldWritePartialContentByteRangeNoEnd() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        Resource body = new ClassPathResource("byterangeresource.txt", getClass());
        ResourceRegion region = HttpRange.createByteRange(7).toResourceRegion(body);
        converter.write(region, TEXT_PLAIN, outputMessage);
        org.springframework.http.HttpHeaders headers = outputMessage.getHeaders();
        Assert.assertThat(headers.getContentType(), Matchers.is(TEXT_PLAIN));
        Assert.assertThat(headers.getContentLength(), Matchers.is(32L));
        Assert.assertThat(headers.get(CONTENT_RANGE).size(), Matchers.is(1));
        Assert.assertThat(headers.get(CONTENT_RANGE).get(0), Matchers.is("bytes 7-38/39"));
        Assert.assertThat(outputMessage.getBodyAsString(StandardCharsets.UTF_8), Matchers.is("Framework test resource content."));
    }

    @Test
    public void partialContentMultipleByteRanges() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        Resource body = new ClassPathResource("byterangeresource.txt", getClass());
        List<HttpRange> rangeList = HttpRange.parseRanges("bytes=0-5,7-15,17-20,22-38");
        List<ResourceRegion> regions = new ArrayList<>();
        for (HttpRange range : rangeList) {
            regions.add(range.toResourceRegion(body));
        }
        converter.write(regions, TEXT_PLAIN, outputMessage);
        org.springframework.http.HttpHeaders headers = outputMessage.getHeaders();
        Assert.assertThat(headers.getContentType().toString(), Matchers.startsWith("multipart/byteranges;boundary="));
        String boundary = "--" + (headers.getContentType().toString().substring(30));
        String content = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        String[] ranges = StringUtils.tokenizeToStringArray(content, "\r\n", false, true);
        Assert.assertThat(ranges[0], Matchers.is(boundary));
        Assert.assertThat(ranges[1], Matchers.is("Content-Type: text/plain"));
        Assert.assertThat(ranges[2], Matchers.is("Content-Range: bytes 0-5/39"));
        Assert.assertThat(ranges[3], Matchers.is("Spring"));
        Assert.assertThat(ranges[4], Matchers.is(boundary));
        Assert.assertThat(ranges[5], Matchers.is("Content-Type: text/plain"));
        Assert.assertThat(ranges[6], Matchers.is("Content-Range: bytes 7-15/39"));
        Assert.assertThat(ranges[7], Matchers.is("Framework"));
        Assert.assertThat(ranges[8], Matchers.is(boundary));
        Assert.assertThat(ranges[9], Matchers.is("Content-Type: text/plain"));
        Assert.assertThat(ranges[10], Matchers.is("Content-Range: bytes 17-20/39"));
        Assert.assertThat(ranges[11], Matchers.is("test"));
        Assert.assertThat(ranges[12], Matchers.is(boundary));
        Assert.assertThat(ranges[13], Matchers.is("Content-Type: text/plain"));
        Assert.assertThat(ranges[14], Matchers.is("Content-Range: bytes 22-38/39"));
        Assert.assertThat(ranges[15], Matchers.is("resource content."));
    }

    // SPR-15041
    @Test
    public void applicationOctetStreamDefaultContentType() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        ClassPathResource body = Mockito.mock(ClassPathResource.class);
        BDDMockito.given(body.getFilename()).willReturn("spring.dat");
        BDDMockito.given(body.contentLength()).willReturn(12L);
        BDDMockito.given(body.getInputStream()).willReturn(new ByteArrayInputStream("Spring Framework".getBytes()));
        HttpRange range = HttpRange.createByteRange(0, 5);
        ResourceRegion resourceRegion = range.toResourceRegion(body);
        converter.write(Collections.singletonList(resourceRegion), null, outputMessage);
        Assert.assertThat(outputMessage.getHeaders().getContentType(), Matchers.is(APPLICATION_OCTET_STREAM));
        Assert.assertThat(outputMessage.getHeaders().getFirst(CONTENT_RANGE), Matchers.is("bytes 0-5/12"));
        Assert.assertThat(outputMessage.getBodyAsString(StandardCharsets.UTF_8), Matchers.is("Spring"));
    }
}

