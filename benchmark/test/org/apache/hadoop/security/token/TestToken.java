/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.security.token;


import Token.LOG;
import java.io.IOException;
import java.util.concurrent.Callable;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.hadoop.security.token.delegation.TestDelegationToken;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for Token
 */
public class TestToken {
    /**
     * Test token serialization
     */
    @Test
    public void testTokenSerialization() throws IOException {
        // Get a token
        Token<TokenIdentifier> sourceToken = new Token<TokenIdentifier>();
        sourceToken.setService(new Text("service"));
        // Write it to an output buffer
        DataOutputBuffer out = new DataOutputBuffer();
        sourceToken.write(out);
        // Read the token back
        DataInputBuffer in = new DataInputBuffer();
        in.reset(out.getData(), out.getLength());
        Token<TokenIdentifier> destToken = new Token<TokenIdentifier>();
        destToken.readFields(in);
        Assert.assertTrue(TestToken.checkEqual(sourceToken, destToken));
    }

    @Test
    public void testEncodeWritable() throws Exception {
        String[] values = new String[]{ "", "a", "bb", "ccc", "dddd", "eeeee", "ffffff", "ggggggg", "hhhhhhhh", "iiiiiiiii", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLM" + "NOPQRSTUVWXYZ01234567890!@#$%^&*()-=_+[]{}|;':,./<>?" };
        Token<AbstractDelegationTokenIdentifier> orig;
        Token<AbstractDelegationTokenIdentifier> copy = new Token();
        // ensure that for each string the input and output values match
        for (int i = 0; i < (values.length); ++i) {
            String val = values[i];
            LOG.info("Input = {}", val);
            orig = new Token(val.getBytes(), val.getBytes(), new Text(val), new Text(val));
            String encode = orig.encodeToUrlString();
            copy.decodeFromUrlString(encode);
            Assert.assertEquals(orig, copy);
            TestToken.checkUrlSafe(encode);
        }
    }

    /* Test decodeWritable() with null newValue string argument,
    should throw HadoopIllegalArgumentException.
     */
    @Test
    public void testDecodeWritableArgSanityCheck() throws Exception {
        Token<AbstractDelegationTokenIdentifier> token = new Token();
        LambdaTestUtils.intercept(HadoopIllegalArgumentException.class, () -> token.decodeFromUrlString(null));
    }

    @Test
    public void testDecodeIdentifier() throws IOException {
        TestDelegationToken.TestDelegationTokenSecretManager secretManager = new TestDelegationToken.TestDelegationTokenSecretManager(0, 0, 0, 0);
        startThreads();
        TestDelegationToken.TestDelegationTokenIdentifier id = new TestDelegationToken.TestDelegationTokenIdentifier(new Text("owner"), new Text("renewer"), new Text("realUser"));
        Token<TestDelegationToken.TestDelegationTokenIdentifier> token = new Token(id, secretManager);
        TokenIdentifier idCopy = token.decodeIdentifier();
        Assert.assertNotSame(id, idCopy);
        Assert.assertEquals(id, idCopy);
    }
}

