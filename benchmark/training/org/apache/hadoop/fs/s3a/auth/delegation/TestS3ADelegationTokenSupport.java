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
package org.apache.hadoop.fs.s3a.auth.delegation;


import MarshalledCredentials.CredentialTypeRequired.AnyNonEmpty;
import UserGroupInformation.AuthenticationMethod.TOKEN;
import java.net.URI;
import org.apache.hadoop.fs.s3a.S3AEncryptionMethods;
import org.apache.hadoop.fs.s3a.S3ATestUtils;
import org.apache.hadoop.fs.s3a.auth.MarshalledCredentialBinding;
import org.apache.hadoop.fs.s3a.auth.MarshalledCredentials;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.token.Token;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests related to S3A DT support.
 */
public class TestS3ADelegationTokenSupport {
    private static URI landsatUri;

    @Test
    public void testSessionTokenKind() throws Throwable {
        AbstractS3ATokenIdentifier identifier = new SessionTokenIdentifier();
        Assert.assertEquals(DelegationConstants.SESSION_TOKEN_KIND, identifier.getKind());
    }

    @Test
    public void testSessionTokenDecode() throws Throwable {
        Text alice = new Text("alice");
        AbstractS3ATokenIdentifier identifier = new SessionTokenIdentifier(DelegationConstants.SESSION_TOKEN_KIND, alice, new URI("s3a://landsat-pds/"), new MarshalledCredentials("a", "b", ""), new EncryptionSecrets(S3AEncryptionMethods.SSE_S3, ""), "origin");
        Token<AbstractS3ATokenIdentifier> t1 = new Token(identifier, new TestS3ADelegationTokenSupport.SessionSecretManager());
        AbstractS3ATokenIdentifier decoded = t1.decodeIdentifier();
        decoded.validate();
        MarshalledCredentials creds = getMarshalledCredentials();
        Assert.assertNotNull("credentials", MarshalledCredentialBinding.toAWSCredentials(creds, AnyNonEmpty, ""));
        Assert.assertEquals(alice, decoded.getOwner());
        UserGroupInformation decodedUser = decoded.getUser();
        Assert.assertEquals(("name of " + decodedUser), "alice", decodedUser.getUserName());
        Assert.assertEquals(("Authentication method of " + decodedUser), TOKEN, decodedUser.getAuthenticationMethod());
        Assert.assertEquals("origin", decoded.getOrigin());
    }

    @Test
    public void testFullTokenKind() throws Throwable {
        AbstractS3ATokenIdentifier identifier = new FullCredentialsTokenIdentifier();
        Assert.assertEquals(DelegationConstants.FULL_TOKEN_KIND, identifier.getKind());
    }

    @Test
    public void testSessionTokenIdentifierRoundTrip() throws Throwable {
        SessionTokenIdentifier id = new SessionTokenIdentifier(DelegationConstants.SESSION_TOKEN_KIND, new Text(), TestS3ADelegationTokenSupport.landsatUri, new MarshalledCredentials("a", "b", "c"), new EncryptionSecrets(), "");
        SessionTokenIdentifier result = S3ATestUtils.roundTrip(id, null);
        String ids = id.toString();
        Assert.assertEquals(("URI in " + ids), id.getUri(), result.getUri());
        Assert.assertEquals(("credentials in " + ids), id.getMarshalledCredentials(), result.getMarshalledCredentials());
    }

    @Test
    public void testRoleTokenIdentifierRoundTrip() throws Throwable {
        RoleTokenIdentifier id = new RoleTokenIdentifier(TestS3ADelegationTokenSupport.landsatUri, new Text(), new MarshalledCredentials("a", "b", "c"), new EncryptionSecrets(), "");
        RoleTokenIdentifier result = S3ATestUtils.roundTrip(id, null);
        String ids = id.toString();
        Assert.assertEquals(("URI in " + ids), id.getUri(), result.getUri());
        Assert.assertEquals(("credentials in " + ids), id.getMarshalledCredentials(), result.getMarshalledCredentials());
    }

    @Test
    public void testFullTokenIdentifierRoundTrip() throws Throwable {
        FullCredentialsTokenIdentifier id = new FullCredentialsTokenIdentifier(TestS3ADelegationTokenSupport.landsatUri, new Text(), new MarshalledCredentials("a", "b", ""), new EncryptionSecrets(), "");
        FullCredentialsTokenIdentifier result = S3ATestUtils.roundTrip(id, null);
        String ids = id.toString();
        Assert.assertEquals(("URI in " + ids), id.getUri(), result.getUri());
        Assert.assertEquals(("credentials in " + ids), id.getMarshalledCredentials(), result.getMarshalledCredentials());
    }

    /**
     * The secret manager always uses the same secret; the
     * factory for new identifiers is that of the token manager.
     */
    private class SessionSecretManager extends SecretManager<AbstractS3ATokenIdentifier> {
        @Override
        protected byte[] createPassword(AbstractS3ATokenIdentifier identifier) {
            return "PASSWORD".getBytes();
        }

        @Override
        public byte[] retrievePassword(AbstractS3ATokenIdentifier identifier) throws InvalidToken {
            return "PASSWORD".getBytes();
        }

        @Override
        public AbstractS3ATokenIdentifier createIdentifier() {
            return new SessionTokenIdentifier();
        }
    }
}

