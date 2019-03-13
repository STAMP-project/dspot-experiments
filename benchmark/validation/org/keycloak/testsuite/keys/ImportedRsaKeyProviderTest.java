/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.keys;


import Algorithm.RS256;
import AlgorithmType.RSA;
import Attributes.ACTIVE_KEY;
import Attributes.CERTIFICATE_KEY;
import Attributes.ENABLED_KEY;
import Attributes.PRIORITY_KEY;
import Attributes.PRIVATE_KEY_KEY;
import ComponentRepresentation.SECRET_VALUE;
import ImportedRsaKeyProviderFactory.ID;
import KeysMetadataRepresentation.KeyMetadataRepresentation;
import java.security.KeyPair;
import java.security.cert.Certificate;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.common.util.CertificateUtils;
import org.keycloak.common.util.KeyUtils;
import org.keycloak.common.util.PemUtils;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.KeysMetadataRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ImportedRsaKeyProviderTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Test
    public void privateKeyOnly() throws Exception {
        long priority = System.currentTimeMillis();
        KeyPair keyPair = KeyUtils.generateRsaKeyPair(2048);
        String kid = KeyUtils.createKeyId(keyPair.getPublic());
        ComponentRepresentation rep = createRep("valid", ID);
        rep.getConfig().putSingle(PRIVATE_KEY_KEY, PemUtils.encodeKey(keyPair.getPrivate()));
        rep.getConfig().putSingle(PRIORITY_KEY, Long.toString(priority));
        Response response = adminClient.realm("test").components().add(rep);
        String id = ApiUtil.getCreatedId(response);
        response.close();
        ComponentRepresentation createdRep = adminClient.realm("test").components().component(id).toRepresentation();
        Assert.assertEquals(SECRET_VALUE, createdRep.getConfig().getFirst(PRIVATE_KEY_KEY));
        Assert.assertNotNull(createdRep.getConfig().getFirst(CERTIFICATE_KEY));
        Assert.assertEquals(keyPair.getPublic(), PemUtils.decodeCertificate(createdRep.getConfig().getFirst(CERTIFICATE_KEY)).getPublicKey());
        KeysMetadataRepresentation keys = adminClient.realm("test").keys().getKeyMetadata();
        Assert.assertEquals(kid, keys.getActive().get(RS256));
        KeysMetadataRepresentation.KeyMetadataRepresentation key = keys.getKeys().get(0);
        Assert.assertEquals(id, key.getProviderId());
        Assert.assertEquals(RSA.name(), key.getType());
        Assert.assertEquals(priority, key.getProviderPriority());
        Assert.assertEquals(kid, key.getKid());
        Assert.assertEquals(PemUtils.encodeKey(keyPair.getPublic()), keys.getKeys().get(0).getPublicKey());
        Assert.assertEquals(keyPair.getPublic(), PemUtils.decodeCertificate(key.getCertificate()).getPublicKey());
    }

    @Test
    public void keyAndCertificate() throws Exception {
        long priority = System.currentTimeMillis();
        KeyPair keyPair = KeyUtils.generateRsaKeyPair(2048);
        Certificate certificate = CertificateUtils.generateV1SelfSignedCertificate(keyPair, "test");
        String certificatePem = PemUtils.encodeCertificate(certificate);
        ComponentRepresentation rep = createRep("valid", ID);
        rep.getConfig().putSingle(PRIVATE_KEY_KEY, PemUtils.encodeKey(keyPair.getPrivate()));
        rep.getConfig().putSingle(CERTIFICATE_KEY, certificatePem);
        rep.getConfig().putSingle(PRIORITY_KEY, Long.toString(priority));
        Response response = adminClient.realm("test").components().add(rep);
        String id = ApiUtil.getCreatedId(response);
        response.close();
        ComponentRepresentation createdRep = adminClient.realm("test").components().component(id).toRepresentation();
        Assert.assertEquals(SECRET_VALUE, createdRep.getConfig().getFirst(PRIVATE_KEY_KEY));
        Assert.assertEquals(certificatePem, createdRep.getConfig().getFirst(CERTIFICATE_KEY));
        KeysMetadataRepresentation keys = adminClient.realm("test").keys().getKeyMetadata();
        KeysMetadataRepresentation.KeyMetadataRepresentation key = keys.getKeys().get(0);
        Assert.assertEquals(certificatePem, key.getCertificate());
    }

    @Test
    public void invalidPriority() throws Exception {
        KeyPair keyPair = KeyUtils.generateRsaKeyPair(2048);
        ComponentRepresentation rep = createRep("invalid", ID);
        rep.getConfig().putSingle(PRIVATE_KEY_KEY, PemUtils.encodeKey(keyPair.getPrivate()));
        rep.getConfig().putSingle(PRIORITY_KEY, "invalid");
        Response response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "'Priority' should be a number");
    }

    @Test
    public void invalidEnabled() throws Exception {
        KeyPair keyPair = KeyUtils.generateRsaKeyPair(2048);
        ComponentRepresentation rep = createRep("invalid", ID);
        rep.getConfig().putSingle(PRIVATE_KEY_KEY, PemUtils.encodeKey(keyPair.getPrivate()));
        rep.getConfig().putSingle(ENABLED_KEY, "invalid");
        Response response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "'Enabled' should be 'true' or 'false'");
    }

    @Test
    public void invalidActive() throws Exception {
        KeyPair keyPair = KeyUtils.generateRsaKeyPair(2048);
        ComponentRepresentation rep = createRep("invalid", ID);
        rep.getConfig().putSingle(PRIVATE_KEY_KEY, PemUtils.encodeKey(keyPair.getPrivate()));
        rep.getConfig().putSingle(ACTIVE_KEY, "invalid");
        Response response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "'Active' should be 'true' or 'false'");
    }

    @Test
    public void invalidPrivateKey() throws Exception {
        KeyPair keyPair = KeyUtils.generateRsaKeyPair(2048);
        ComponentRepresentation rep = createRep("invalid", ID);
        Response response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "'Private RSA Key' is required");
        rep.getConfig().putSingle(PRIVATE_KEY_KEY, "nonsense");
        response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "Failed to decode private key");
        rep.getConfig().putSingle(PRIVATE_KEY_KEY, PemUtils.encodeKey(keyPair.getPublic()));
        response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "Failed to decode private key");
    }

    @Test
    public void invalidCertificate() throws Exception {
        KeyPair keyPair = KeyUtils.generateRsaKeyPair(2048);
        Certificate invalidCertificate = CertificateUtils.generateV1SelfSignedCertificate(KeyUtils.generateRsaKeyPair(2048), "test");
        ComponentRepresentation rep = createRep("invalid", ID);
        rep.getConfig().putSingle(PRIVATE_KEY_KEY, PemUtils.encodeKey(keyPair.getPrivate()));
        rep.getConfig().putSingle(CERTIFICATE_KEY, "nonsense");
        Response response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "Failed to decode certificate");
        rep.getConfig().putSingle(CERTIFICATE_KEY, PemUtils.encodeCertificate(invalidCertificate));
        response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "Certificate does not match private key");
    }
}

