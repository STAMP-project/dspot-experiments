/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.integration.accesscontrol;


import javax.ws.rs.core.Response;
import org.apache.nifi.integration.util.NiFiTestAuthorizer;
import org.apache.nifi.web.api.dto.ConnectionDTO;
import org.apache.nifi.web.api.dto.RevisionDTO;
import org.apache.nifi.web.api.entity.ConnectionEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Access control test for connections.
 */
public class ITConnectionAccessControl {
    private static AccessControlHelper helper;

    /**
     * Ensures the READ user can get a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserGetConnection() throws Exception {
        final ConnectionEntity entity = getRandomConnection(ITConnectionAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the READ WRITE user can get a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserGetConnection() throws Exception {
        final ConnectionEntity entity = getRandomConnection(ITConnectionAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the WRITE user can get a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserGetConnection() throws Exception {
        final ConnectionEntity entity = getRandomConnection(ITConnectionAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
    }

    /**
     * Ensures the NONE user can get a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserGetConnection() throws Exception {
        final ConnectionEntity entity = getRandomConnection(ITConnectionAccessControl.helper.getNoneUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
    }

    /**
     * Ensures the READ user cannot put a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserPutConnection() throws Exception {
        final ConnectionEntity entity = getRandomConnection(ITConnectionAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        // attempt update the name
        entity.getRevision().setClientId(AccessControlHelper.READ_CLIENT_ID);
        entity.getComponent().setName("Updated Name");
        // perform the request
        final Response response = updateConnection(ITConnectionAccessControl.helper.getReadUser(), entity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ_WRITE user can put a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserPutConnection() throws Exception {
        final ConnectionEntity entity = getRandomConnection(ITConnectionAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        final String updatedName = "Updated Name";
        // attempt to update the name
        final long version = entity.getRevision().getVersion();
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setName(updatedName);
        // perform the request
        final Response response = updateConnection(ITConnectionAccessControl.helper.getReadWriteUser(), entity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final ConnectionEntity responseEntity = response.readEntity(ConnectionEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.READ_WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
        Assert.assertEquals(updatedName, responseEntity.getComponent().getName());
    }

    /**
     * Ensures the READ_WRITE user can put a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserPutConnectionThroughInheritedPolicy() throws Exception {
        final ConnectionEntity entity = createConnection(NiFiTestAuthorizer.NO_POLICY_COMPONENT_NAME);
        final String updatedName = "Updated name";
        // attempt to update the name
        final long version = entity.getRevision().getVersion();
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setName(updatedName);
        // perform the request
        final Response response = updateConnection(ITConnectionAccessControl.helper.getReadWriteUser(), entity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final ConnectionEntity responseEntity = response.readEntity(ConnectionEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.READ_WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
        Assert.assertEquals(updatedName, responseEntity.getComponent().getName());
    }

    /**
     * Ensures the WRITE user can put a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserPutConnection() throws Exception {
        final ConnectionEntity entity = getRandomConnection(ITConnectionAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        final String updatedName = "Updated Name";
        // attempt to update the name
        final ConnectionDTO requestDto = new ConnectionDTO();
        requestDto.setId(entity.getId());
        requestDto.setName(updatedName);
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.WRITE_CLIENT_ID);
        final ConnectionEntity requestEntity = new ConnectionEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateConnection(ITConnectionAccessControl.helper.getWriteUser(), requestEntity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final ConnectionEntity responseEntity = response.readEntity(ConnectionEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
    }

    /**
     * Ensures the NONE user cannot put a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserPutConnection() throws Exception {
        final ConnectionEntity entity = getRandomConnection(ITConnectionAccessControl.helper.getNoneUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        final String updatedName = "Updated Name";
        // attempt to update the name
        final ConnectionDTO requestDto = new ConnectionDTO();
        requestDto.setId(entity.getId());
        requestDto.setName(updatedName);
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.NONE_CLIENT_ID);
        final ConnectionEntity requestEntity = new ConnectionEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateConnection(ITConnectionAccessControl.helper.getNoneUser(), requestEntity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ user cannot delete a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserDeleteConnection() throws Exception {
        verifyDelete(ITConnectionAccessControl.helper.getReadUser(), AccessControlHelper.READ_CLIENT_ID, 403);
    }

    /**
     * Ensures the READ WRITE user can delete a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserDeleteConnection() throws Exception {
        verifyDelete(ITConnectionAccessControl.helper.getReadWriteUser(), AccessControlHelper.READ_WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the WRITE user can delete a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserDeleteConnection() throws Exception {
        verifyDelete(ITConnectionAccessControl.helper.getWriteUser(), AccessControlHelper.WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the NONE user can delete a connection.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserDeleteConnection() throws Exception {
        verifyDelete(ITConnectionAccessControl.helper.getNoneUser(), AccessControlHelper.NONE_CLIENT_ID, 403);
    }
}

