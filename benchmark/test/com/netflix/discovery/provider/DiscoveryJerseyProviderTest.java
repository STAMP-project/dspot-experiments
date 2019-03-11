/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.discovery.provider;


import CodecWrappers.JacksonJson;
import MediaType.APPLICATION_JSON_TYPE;
import MediaType.APPLICATION_XML_TYPE;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.wrappers.CodecWrappers;
import com.netflix.discovery.util.InstanceInfoGenerator;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class DiscoveryJerseyProviderTest {
    private static final InstanceInfo INSTANCE = InstanceInfoGenerator.takeOne();

    private final DiscoveryJerseyProvider jerseyProvider = new DiscoveryJerseyProvider(CodecWrappers.getEncoder(JacksonJson.class), CodecWrappers.getDecoder(JacksonJson.class));

    @Test
    public void testJsonEncodingDecoding() throws Exception {
        testEncodingDecoding(APPLICATION_JSON_TYPE);
    }

    @Test
    public void testXmlEncodingDecoding() throws Exception {
        testEncodingDecoding(APPLICATION_XML_TYPE);
    }

    @Test
    public void testDecodingWithUtf8CharsetExplicitlySet() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("charset", "UTF-8");
        testEncodingDecoding(new MediaType("application", "json", params));
    }

    @Test
    public void testNonUtf8CharsetIsNotAccepted() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("charset", "ISO-8859");
        MediaType mediaTypeWithNonSupportedCharset = new MediaType("application", "json", params);
        Assert.assertThat(jerseyProvider.isReadable(InstanceInfo.class, InstanceInfo.class, null, mediaTypeWithNonSupportedCharset), CoreMatchers.is(false));
    }
}

