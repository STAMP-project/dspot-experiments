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
package org.springframework.http.codec.multipart;


import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.MULTIPART_FORM_DATA;
import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link SynchronossPartHttpMessageReader}.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class SynchronossPartHttpMessageReaderTests {
    private final MultipartHttpMessageReader reader = new MultipartHttpMessageReader(new SynchronossPartHttpMessageReader());

    @Test
    public void canRead() {
        Assert.assertTrue(this.reader.canRead(forClassWithGenerics(MultiValueMap.class, String.class, Part.class), MULTIPART_FORM_DATA));
        Assert.assertFalse(this.reader.canRead(forClassWithGenerics(MultiValueMap.class, String.class, Object.class), MULTIPART_FORM_DATA));
        Assert.assertFalse(this.reader.canRead(forClassWithGenerics(MultiValueMap.class, String.class, String.class), MULTIPART_FORM_DATA));
        Assert.assertFalse(this.reader.canRead(forClassWithGenerics(Map.class, String.class, String.class), MULTIPART_FORM_DATA));
        Assert.assertFalse(this.reader.canRead(forClassWithGenerics(MultiValueMap.class, String.class, Part.class), APPLICATION_FORM_URLENCODED));
    }

    @Test
    public void resolveParts() {
        ServerHttpRequest request = generateMultipartRequest();
        ResolvableType elementType = forClassWithGenerics(MultiValueMap.class, String.class, Part.class);
        MultiValueMap<String, Part> parts = this.reader.readMono(elementType, request, Collections.emptyMap()).block();
        Assert.assertEquals(2, parts.size());
        Assert.assertTrue(parts.containsKey("fooPart"));
        Part part = parts.getFirst("fooPart");
        Assert.assertTrue((part instanceof FilePart));
        Assert.assertEquals("fooPart", part.name());
        Assert.assertEquals("foo.txt", filename());
        DataBuffer buffer = DataBufferUtils.join(part.content()).block();
        Assert.assertEquals(12, buffer.readableByteCount());
        byte[] byteContent = new byte[12];
        buffer.read(byteContent);
        Assert.assertEquals("Lorem Ipsum.", new String(byteContent));
        Assert.assertTrue(parts.containsKey("barPart"));
        part = parts.getFirst("barPart");
        Assert.assertTrue((part instanceof FormFieldPart));
        Assert.assertEquals("barPart", part.name());
        Assert.assertEquals("bar", value());
    }

    // SPR-16545
    @Test
    public void transferTo() {
        ServerHttpRequest request = generateMultipartRequest();
        ResolvableType elementType = forClassWithGenerics(MultiValueMap.class, String.class, Part.class);
        MultiValueMap<String, Part> parts = this.reader.readMono(elementType, request, Collections.emptyMap()).block();
        Assert.assertNotNull(parts);
        FilePart part = ((FilePart) (parts.getFirst("fooPart")));
        Assert.assertNotNull(part);
        File dest = new File((((System.getProperty("java.io.tmpdir")) + "/") + (part.filename())));
        part.transferTo(dest).block(Duration.ofSeconds(5));
        Assert.assertTrue(dest.exists());
        Assert.assertEquals(12, dest.length());
        Assert.assertTrue(dest.delete());
    }

    @Test
    public void bodyError() {
        ServerHttpRequest request = generateErrorMultipartRequest();
        ResolvableType elementType = forClassWithGenerics(MultiValueMap.class, String.class, Part.class);
        StepVerifier.create(this.reader.readMono(elementType, request, Collections.emptyMap())).verifyError();
    }
}

