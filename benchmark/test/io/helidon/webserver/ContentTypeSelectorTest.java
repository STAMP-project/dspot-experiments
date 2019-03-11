/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import MediaType.APPLICATION_OCTET_STREAM;
import MediaType.APPLICATION_XML;
import MediaType.TEXT_HTML;
import io.helidon.common.http.MediaType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests {@link ContentTypeSelector}.
 */
public class ContentTypeSelectorTest {
    @Test
    public void testContentTypeSelection() throws Exception {
        Map<String, MediaType> map = new HashMap<>();
        map.put("txt", MediaType.create("foo", "bar"));
        ContentTypeSelector selector = new ContentTypeSelector(map);
        // Empty headers
        RequestHeaders headers = Mockito.mock(RequestHeaders.class);
        Mockito.when(headers.isAccepted(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(headers.acceptedTypes()).thenReturn(Collections.emptyList());
        MatcherAssert.assertThat(selector.determine("foo.xml", headers), CoreMatchers.is(APPLICATION_XML));
        MatcherAssert.assertThat(selector.determine("foo.txt", headers), CoreMatchers.is(MediaType.create("foo", "bar")));
        MatcherAssert.assertThat(selector.determine("foo.undefined", headers), CoreMatchers.is(APPLICATION_OCTET_STREAM));
        MatcherAssert.assertThat(selector.determine("undefined", headers), CoreMatchers.is(APPLICATION_OCTET_STREAM));
        // Accept text/html
        headers = Mockito.mock(RequestHeaders.class);
        Mockito.when(headers.acceptedTypes()).thenReturn(Collections.singletonList(TEXT_HTML));
        MatcherAssert.assertThat(selector.determine("foo.undefined", headers), CoreMatchers.is(TEXT_HTML));
    }

    @Test
    public void testInvalidFile() {
        ContentTypeSelector selector = new ContentTypeSelector(Collections.emptyMap());
        RequestHeaders headers = Mockito.mock(RequestHeaders.class);
        Mockito.when(headers.isAccepted(ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(headers.acceptedTypes()).thenReturn(Collections.singletonList(TEXT_HTML));
        HttpException ex = Assertions.assertThrows(HttpException.class, () -> {
            selector.determine("foo.xml", headers);
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.is("Not accepted media-type!"));
    }
}

