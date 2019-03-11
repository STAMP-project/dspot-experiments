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


import AlgorithmType.RSA;
import GeneratedRsaKeyProviderFactory.ID;
import KeysMetadataRepresentation.KeyMetadataRepresentation;
import java.security.interfaces.RSAPublicKey;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
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
public class GeneratedRsaKeyProviderTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Test
    public void defaultKeysize() throws Exception {
        long priority = System.currentTimeMillis();
        ComponentRepresentation rep = createRep("valid", ID);
        rep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        rep.getConfig().putSingle("priority", Long.toString(priority));
        Response response = adminClient.realm("test").components().add(rep);
        String id = ApiUtil.getCreatedId(response);
        getCleanup().addComponentId(id);
        response.close();
        ComponentRepresentation createdRep = adminClient.realm("test").components().component(id).toRepresentation();
        Assert.assertEquals(1, createdRep.getConfig().size());
        Assert.assertEquals(Long.toString(priority), createdRep.getConfig().getFirst("priority"));
        KeysMetadataRepresentation keys = adminClient.realm("test").keys().getKeyMetadata();
        KeysMetadataRepresentation.KeyMetadataRepresentation key = keys.getKeys().get(0);
        Assert.assertEquals(id, key.getProviderId());
        Assert.assertEquals(RSA.name(), key.getType());
        Assert.assertEquals(priority, key.getProviderPriority());
        Assert.assertEquals(2048, ((RSAPublicKey) (PemUtils.decodePublicKey(keys.getKeys().get(0).getPublicKey()))).getModulus().bitLength());
    }

    @Test
    public void largeKeysize() throws Exception {
        long priority = System.currentTimeMillis();
        ComponentRepresentation rep = createRep("valid", ID);
        rep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        rep.getConfig().putSingle("priority", Long.toString(priority));
        rep.getConfig().putSingle("keySize", "4096");
        Response response = adminClient.realm("test").components().add(rep);
        String id = ApiUtil.getCreatedId(response);
        getCleanup().addComponentId(id);
        response.close();
        ComponentRepresentation createdRep = adminClient.realm("test").components().component(id).toRepresentation();
        Assert.assertEquals(2, createdRep.getConfig().size());
        Assert.assertEquals("4096", createdRep.getConfig().getFirst("keySize"));
        KeysMetadataRepresentation keys = adminClient.realm("test").keys().getKeyMetadata();
        KeysMetadataRepresentation.KeyMetadataRepresentation key = keys.getKeys().get(0);
        Assert.assertEquals(id, key.getProviderId());
        Assert.assertEquals(RSA.name(), key.getType());
        Assert.assertEquals(priority, key.getProviderPriority());
        Assert.assertEquals(4096, ((RSAPublicKey) (PemUtils.decodePublicKey(keys.getKeys().get(0).getPublicKey()))).getModulus().bitLength());
    }

    @Test
    public void updatePriority() throws Exception {
        long priority = System.currentTimeMillis();
        ComponentRepresentation rep = createRep("valid", ID);
        rep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        rep.getConfig().putSingle("priority", Long.toString(priority));
        Response response = adminClient.realm("test").components().add(rep);
        String id = ApiUtil.getCreatedId(response);
        getCleanup().addComponentId(id);
        response.close();
        KeysMetadataRepresentation keys = adminClient.realm("test").keys().getKeyMetadata();
        String publicKey = keys.getKeys().get(0).getPublicKey();
        ComponentRepresentation createdRep = adminClient.realm("test").components().component(id).toRepresentation();
        priority += 1000;
        createdRep.getConfig().putSingle("priority", Long.toString(priority));
        adminClient.realm("test").components().component(id).update(createdRep);
        keys = adminClient.realm("test").keys().getKeyMetadata();
        String publicKey2 = keys.getKeys().get(0).getPublicKey();
        Assert.assertEquals(publicKey, publicKey2);
    }

    @Test
    public void updateKeysize() throws Exception {
        long priority = System.currentTimeMillis();
        ComponentRepresentation rep = createRep("valid", ID);
        rep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        rep.getConfig().putSingle("priority", Long.toString(priority));
        Response response = adminClient.realm("test").components().add(rep);
        String id = ApiUtil.getCreatedId(response);
        getCleanup().addComponentId(id);
        response.close();
        KeysMetadataRepresentation keys = adminClient.realm("test").keys().getKeyMetadata();
        String publicKey = keys.getKeys().get(0).getPublicKey();
        ComponentRepresentation createdRep = adminClient.realm("test").components().component(id).toRepresentation();
        createdRep.getConfig().putSingle("keySize", "4096");
        adminClient.realm("test").components().component(id).update(createdRep);
        keys = adminClient.realm("test").keys().getKeyMetadata();
        String publicKey2 = keys.getKeys().get(0).getPublicKey();
        Assert.assertNotEquals(publicKey, publicKey2);
        Assert.assertEquals(2048, ((RSAPublicKey) (PemUtils.decodePublicKey(publicKey))).getModulus().bitLength());
        Assert.assertEquals(4096, ((RSAPublicKey) (PemUtils.decodePublicKey(publicKey2))).getModulus().bitLength());
    }

    @Test
    public void invalidKeysize() throws Exception {
        ComponentRepresentation rep = createRep("invalid", ID);
        rep.getConfig().putSingle("keySize", "1234");
        Response response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "'Key size' should be 1024, 2048 or 4096");
    }
}

