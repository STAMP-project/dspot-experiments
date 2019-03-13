/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.data.digest;


import bt.TestUtil;
import bt.data.range.ByteRange;
import java.security.MessageDigest;
import org.junit.Assert;
import org.junit.Test;


public class JavaSecurityDigesterTest {
    private static final String algorithm = "SHA-1";

    private final MessageDigest digest;

    public JavaSecurityDigesterTest() throws Exception {
        this.digest = MessageDigest.getInstance(JavaSecurityDigesterTest.algorithm);
    }

    @Test
    public void testDigester_DataLengthEqualToStep() {
        int len = 10000;
        byte[] data = TestUtil.sequence(len);
        byte[] hash = digest.digest(data);
        JavaSecurityDigester digester = new JavaSecurityDigester(JavaSecurityDigesterTest.algorithm, len);
        Assert.assertArrayEquals(hash, digester.digest(new ByteRange(data)));
    }

    @Test
    public void testDigester_DataLengthLessThanStep() {
        int len = 10000;
        byte[] data = TestUtil.sequence(len);
        byte[] hash = digest.digest(data);
        JavaSecurityDigester digester = new JavaSecurityDigester(JavaSecurityDigesterTest.algorithm, (len + 1));
        Assert.assertArrayEquals(hash, digester.digest(new ByteRange(data)));
    }

    @Test
    public void testDigester_DataLengthFactorOfStep() {
        int len = 10000;
        byte[] data = TestUtil.sequence(len);
        byte[] hash = digest.digest(data);
        JavaSecurityDigester digester = new JavaSecurityDigester(JavaSecurityDigesterTest.algorithm, (len / 10));
        Assert.assertArrayEquals(hash, digester.digest(new ByteRange(data)));
    }

    @Test
    public void testDigester_DataLengthMoreThanStep() {
        int len = 10000;
        byte[] data = TestUtil.sequence(len);
        byte[] hash = digest.digest(data);
        JavaSecurityDigester digester = new JavaSecurityDigester(JavaSecurityDigesterTest.algorithm, (len / 7));
        Assert.assertArrayEquals(hash, digester.digest(new ByteRange(data)));
    }
}

