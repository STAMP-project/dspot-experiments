/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.??See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.??The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.??You may obtain a copy of the License at
 *
 * ???? http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.security;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.EnumSet;
import org.apache.hadoop.hdds.protocol.proto.HddsProtos.BlockTokenSecretProto.AccessModeProto;
import org.apache.hadoop.hdds.security.token.OzoneBlockTokenIdentifier;
import org.apache.hadoop.hdds.security.x509.certificate.client.CertificateClient;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for {@link OzoneBlockTokenSecretManager}.
 */
public class TestOzoneBlockTokenSecretManager {
    private OzoneBlockTokenSecretManager secretManager;

    private KeyPair keyPair;

    private X509Certificate x509Certificate;

    private long expiryTime;

    private String omCertSerialId;

    private CertificateClient client;

    private static final String BASEDIR = GenericTestUtils.getTempPath(TestOzoneBlockTokenSecretManager.class.getSimpleName());

    @Test
    public void testGenerateToken() throws Exception {
        Token<OzoneBlockTokenIdentifier> token = secretManager.generateToken("101", EnumSet.allOf(AccessModeProto.class), 100);
        OzoneBlockTokenIdentifier identifier = OzoneBlockTokenIdentifier.readFieldsProtobuf(new DataInputStream(new ByteArrayInputStream(token.getIdentifier())));
        // Check basic details.
        Assert.assertTrue(identifier.getBlockId().equals("101"));
        Assert.assertTrue(identifier.getAccessModes().equals(EnumSet.allOf(AccessModeProto.class)));
        Assert.assertTrue(identifier.getOmCertSerialId().equals(omCertSerialId));
        validateHash(token.getPassword(), token.getIdentifier());
    }

    @Test
    public void testCreateIdentifierSuccess() throws Exception {
        OzoneBlockTokenIdentifier btIdentifier = secretManager.createIdentifier("testUser", "101", EnumSet.allOf(AccessModeProto.class), 100);
        // Check basic details.
        Assert.assertTrue(btIdentifier.getOwnerId().equals("testUser"));
        Assert.assertTrue(btIdentifier.getBlockId().equals("101"));
        Assert.assertTrue(btIdentifier.getAccessModes().equals(EnumSet.allOf(AccessModeProto.class)));
        Assert.assertTrue(btIdentifier.getOmCertSerialId().equals(omCertSerialId));
        byte[] hash = secretManager.createPassword(btIdentifier);
        validateHash(hash, btIdentifier.getBytes());
    }

    @Test
    public void testCreateIdentifierFailure() throws Exception {
        LambdaTestUtils.intercept(SecurityException.class, ("Ozone block token can't be created without owner and access mode " + "information."), () -> {
            secretManager.createIdentifier();
        });
    }

    @Test
    public void testRenewToken() throws Exception {
        LambdaTestUtils.intercept(UnsupportedOperationException.class, ("Renew token operation is not supported for ozone block" + " tokens."), () -> {
            secretManager.renewToken(null, null);
        });
    }

    @Test
    public void testCancelToken() throws Exception {
        LambdaTestUtils.intercept(UnsupportedOperationException.class, ("Cancel token operation is not supported for ozone block" + " tokens."), () -> {
            secretManager.cancelToken(null, null);
        });
    }

    @Test
    public void testVerifySignatureFailure() throws Exception {
        OzoneBlockTokenIdentifier id = new OzoneBlockTokenIdentifier("testUser", "4234", EnumSet.allOf(AccessModeProto.class), ((Time.now()) + ((60 * 60) * 24)), "123444", 1024);
        LambdaTestUtils.intercept(UnsupportedOperationException.class, ("operation" + " is not supported for block tokens"), () -> secretManager.verifySignature(id, client.signData(id.getBytes())));
    }
}

