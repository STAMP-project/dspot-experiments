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
package org.springframework.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 */
public class DigestUtilsTests {
    private byte[] bytes;

    @Test
    public void md5() throws IOException {
        byte[] expected = new byte[]{ -79, 10, -115, -79, 100, -32, 117, 65, 5, -73, -87, -101, -25, 46, 63, -27 };
        byte[] result = DigestUtils.md5Digest(bytes);
        Assert.assertArrayEquals("Invalid hash", expected, result);
        result = DigestUtils.md5Digest(new ByteArrayInputStream(bytes));
        Assert.assertArrayEquals("Invalid hash", expected, result);
    }

    @Test
    public void md5Hex() throws IOException {
        String expected = "b10a8db164e0754105b7a99be72e3fe5";
        String hash = DigestUtils.md5DigestAsHex(bytes);
        Assert.assertEquals("Invalid hash", expected, hash);
        hash = DigestUtils.md5DigestAsHex(new ByteArrayInputStream(bytes));
        Assert.assertEquals("Invalid hash", expected, hash);
    }

    @Test
    public void md5StringBuilder() throws IOException {
        String expected = "b10a8db164e0754105b7a99be72e3fe5";
        StringBuilder builder = new StringBuilder();
        DigestUtils.appendMd5DigestAsHex(bytes, builder);
        Assert.assertEquals("Invalid hash", expected, builder.toString());
        builder = new StringBuilder();
        DigestUtils.appendMd5DigestAsHex(new ByteArrayInputStream(bytes), builder);
        Assert.assertEquals("Invalid hash", expected, builder.toString());
    }
}

