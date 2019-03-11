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


import Algorithm.HS256;
import GeneratedHmacKeyProviderFactory.ID;
import KeyType.OCT;
import KeysMetadataRepresentation.KeyMetadataRepresentation;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.common.util.Base64Url;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.KeysMetadataRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.runonserver.RunHelpers;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class GeneratedHmacKeyProviderTest extends AbstractKeycloakTest {
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
        response.close();
        ComponentRepresentation createdRep = adminClient.realm("test").components().component(id).toRepresentation();
        Assert.assertEquals(1, createdRep.getConfig().size());
        Assert.assertEquals(Long.toString(priority), createdRep.getConfig().getFirst("priority"));
        KeysMetadataRepresentation keys = adminClient.realm("test").keys().getKeyMetadata();
        KeysMetadataRepresentation.KeyMetadataRepresentation key = null;
        for (KeysMetadataRepresentation.KeyMetadataRepresentation k : keys.getKeys()) {
            if (k.getAlgorithm().equals(HS256)) {
                key = k;
                break;
            }
        }
        Assert.assertEquals(id, key.getProviderId());
        Assert.assertEquals(OCT, key.getType());
        Assert.assertEquals(priority, key.getProviderPriority());
        ComponentRepresentation component = testingClient.server("test").fetch(RunHelpers.internalComponent(id));
        Assert.assertEquals(64, Base64Url.decode(component.getConfig().getFirst("secret")).length);
    }

    @Test
    public void largeKeysize() {
        long priority = System.currentTimeMillis();
        ComponentRepresentation rep = createRep("valid", ID);
        rep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        rep.getConfig().putSingle("priority", Long.toString(priority));
        rep.getConfig().putSingle("secretSize", "512");
        Response response = adminClient.realm("test").components().add(rep);
        String id = ApiUtil.getCreatedId(response);
        response.close();
        ComponentRepresentation createdRep = adminClient.realm("test").components().component(id).toRepresentation();
        Assert.assertEquals(2, createdRep.getConfig().size());
        Assert.assertEquals("512", createdRep.getConfig().getFirst("secretSize"));
        KeysMetadataRepresentation keys = adminClient.realm("test").keys().getKeyMetadata();
        KeysMetadataRepresentation.KeyMetadataRepresentation key = null;
        for (KeysMetadataRepresentation.KeyMetadataRepresentation k : keys.getKeys()) {
            if (k.getAlgorithm().equals(HS256)) {
                key = k;
                break;
            }
        }
        Assert.assertEquals(id, key.getProviderId());
        Assert.assertEquals(OCT, key.getType());
        Assert.assertEquals(priority, key.getProviderPriority());
        ComponentRepresentation component = testingClient.server("test").fetch(RunHelpers.internalComponent(id));
        Assert.assertEquals(512, Base64Url.decode(component.getConfig().getFirst("secret")).length);
    }

    @Test
    public void updateKeysize() throws Exception {
        long priority = System.currentTimeMillis();
        ComponentRepresentation rep = createRep("valid", ID);
        rep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        rep.getConfig().putSingle("priority", Long.toString(priority));
        Response response = adminClient.realm("test").components().add(rep);
        String id = ApiUtil.getCreatedId(response);
        response.close();
        ComponentRepresentation component = testingClient.server("test").fetch(RunHelpers.internalComponent(id));
        Assert.assertEquals(64, Base64Url.decode(component.getConfig().getFirst("secret")).length);
        ComponentRepresentation createdRep = adminClient.realm("test").components().component(id).toRepresentation();
        createdRep.getConfig().putSingle("secretSize", "512");
        adminClient.realm("test").components().component(id).update(createdRep);
        component = testingClient.server("test").fetch(RunHelpers.internalComponent(id));
        Assert.assertEquals(512, Base64Url.decode(component.getConfig().getFirst("secret")).length);
    }

    @Test
    public void invalidKeysize() throws Exception {
        ComponentRepresentation rep = createRep("invalid", ID);
        rep.getConfig().putSingle("secretSize", "1234");
        Response response = adminClient.realm("test").components().add(rep);
        assertErrror(response, "'Secret size' should be 16, 24, 32, 64, 128, 256 or 512");
    }
}

