/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.storage;


import com.google.cloud.storage.SignatureInfo.Builder;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.PUT;


public class SignatureInfoTest {
    private static final String RESOURCE = "/bucketName/blobName";

    @Test(expected = IllegalArgumentException.class)
    public void requireHttpVerb() {
        new SignatureInfo.Builder(null, 0L, URI.create(SignatureInfoTest.RESOURCE)).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void requireResource() {
        build();
    }

    @Test
    public void constructUnsignedPayload() {
        Builder builder = new SignatureInfo.Builder(PUT, 0L, URI.create(SignatureInfoTest.RESOURCE));
        String unsignedPayload = builder.build().constructUnsignedPayload();
        Assert.assertEquals(("PUT\n\n\n0\n" + (SignatureInfoTest.RESOURCE)), unsignedPayload);
    }

    @Test
    public void constructUnsignedPayloadWithExtensionHeaders() {
        Builder builder = new SignatureInfo.Builder(PUT, 0L, URI.create(SignatureInfoTest.RESOURCE));
        Map<String, String> extensionHeaders = new HashMap<>();
        extensionHeaders.put("x-goog-acl", "public-read");
        extensionHeaders.put("x-goog-meta-owner", "myself");
        builder.setCanonicalizedExtensionHeaders(extensionHeaders);
        String unsignedPayload = builder.build().constructUnsignedPayload();
        String rawPayload = "PUT\n\n\n0\nx-goog-acl:public-read\nx-goog-meta-owner:myself\n" + (SignatureInfoTest.RESOURCE);
        Assert.assertEquals(rawPayload, unsignedPayload);
    }
}

