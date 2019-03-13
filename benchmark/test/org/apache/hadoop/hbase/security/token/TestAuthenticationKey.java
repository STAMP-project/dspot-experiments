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
package org.apache.hadoop.hbase.security.token;


import java.io.UnsupportedEncodingException;
import javax.crypto.SecretKey;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ SecurityTests.class, SmallTests.class })
public class TestAuthenticationKey {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAuthenticationKey.class);

    @Test
    public void test() throws UnsupportedEncodingException {
        SecretKey secret = Mockito.mock(SecretKey.class);
        Mockito.when(secret.getEncoded()).thenReturn("secret".getBytes("UTF-8"));
        AuthenticationKey key = new AuthenticationKey(0, 1234, secret);
        Assert.assertEquals(key.hashCode(), new AuthenticationKey(0, 1234, secret).hashCode());
        Assert.assertEquals(key, new AuthenticationKey(0, 1234, secret));
        AuthenticationKey otherID = new AuthenticationKey(1, 1234, secret);
        Assert.assertNotEquals(key.hashCode(), otherID.hashCode());
        Assert.assertNotEquals(key, otherID);
        AuthenticationKey otherExpiry = new AuthenticationKey(0, 8765, secret);
        Assert.assertNotEquals(key.hashCode(), otherExpiry.hashCode());
        Assert.assertNotEquals(key, otherExpiry);
        SecretKey other = Mockito.mock(SecretKey.class);
        Mockito.when(secret.getEncoded()).thenReturn("other".getBytes("UTF-8"));
        AuthenticationKey otherSecret = new AuthenticationKey(0, 1234, other);
        Assert.assertNotEquals(key.hashCode(), otherSecret.hashCode());
        Assert.assertNotEquals(key, otherSecret);
    }
}

