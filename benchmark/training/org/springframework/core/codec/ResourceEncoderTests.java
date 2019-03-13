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
package org.springframework.core.codec;


import MimeTypeUtils.APPLICATION_JSON;
import MimeTypeUtils.TEXT_PLAIN;
import ResolvableType.NONE;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class ResourceEncoderTests extends AbstractEncoderTestCase<ResourceEncoder> {
    private final byte[] bytes = "foo".getBytes(StandardCharsets.UTF_8);

    public ResourceEncoderTests() {
        super(new ResourceEncoder());
    }

    @Override
    @Test
    public void canEncode() {
        Assert.assertTrue(this.encoder.canEncode(ResolvableType.forClass(InputStreamResource.class), TEXT_PLAIN));
        Assert.assertTrue(this.encoder.canEncode(ResolvableType.forClass(ByteArrayResource.class), TEXT_PLAIN));
        Assert.assertTrue(this.encoder.canEncode(ResolvableType.forClass(Resource.class), TEXT_PLAIN));
        Assert.assertTrue(this.encoder.canEncode(ResolvableType.forClass(InputStreamResource.class), APPLICATION_JSON));
        // SPR-15464
        Assert.assertFalse(this.encoder.canEncode(NONE, null));
    }
}

