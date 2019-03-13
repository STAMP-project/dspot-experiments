/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.web.accept;


import MediaType.APPLICATION_JSON;
import MediaType.TEXT_HTML;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;


/**
 * Test fixture for {@link MappingMediaTypeFileExtensionResolver}.
 *
 * @author Rossen Stoyanchev
 * @author Melissa Hartsock
 */
public class MappingMediaTypeFileExtensionResolverTests {
    @Test
    public void resolveExtensions() {
        Map<String, MediaType> mapping = Collections.singletonMap("json", APPLICATION_JSON);
        MappingMediaTypeFileExtensionResolver resolver = new MappingMediaTypeFileExtensionResolver(mapping);
        List<String> extensions = resolver.resolveFileExtensions(APPLICATION_JSON);
        Assert.assertEquals(1, extensions.size());
        Assert.assertEquals("json", extensions.get(0));
    }

    @Test
    public void resolveExtensionsNoMatch() {
        Map<String, MediaType> mapping = Collections.singletonMap("json", APPLICATION_JSON);
        MappingMediaTypeFileExtensionResolver resolver = new MappingMediaTypeFileExtensionResolver(mapping);
        List<String> extensions = resolver.resolveFileExtensions(TEXT_HTML);
        Assert.assertTrue(extensions.isEmpty());
    }

    /**
     * Unit test for SPR-13747 - ensures that reverse lookup of media type from media
     * type key is case-insensitive.
     */
    @Test
    public void lookupMediaTypeCaseInsensitive() {
        Map<String, MediaType> mapping = Collections.singletonMap("json", APPLICATION_JSON);
        MappingMediaTypeFileExtensionResolver resolver = new MappingMediaTypeFileExtensionResolver(mapping);
        MediaType mediaType = resolver.lookupMediaType("JSON");
        Assert.assertEquals(APPLICATION_JSON, mediaType);
    }
}

