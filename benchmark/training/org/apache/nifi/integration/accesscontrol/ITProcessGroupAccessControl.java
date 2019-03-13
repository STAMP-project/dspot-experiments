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
import org.apache.nifi.web.api.dto.ProcessGroupDTO;
import org.apache.nifi.web.api.dto.RevisionDTO;
import org.apache.nifi.web.api.entity.ProcessGroupEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Access control test for process groups.
 */
public class ITProcessGroupAccessControl {
    private static AccessControlHelper helper;

    private static int count = 0;

    /**
     * Ensures the READ user can get a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserGetProcessGroup() throws Exception {
        final ProcessGroupEntity entity = getRandomProcessGroup(ITProcessGroupAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the READ WRITE user can get a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserGetProcessGroup() throws Exception {
        final ProcessGroupEntity entity = getRandomProcessGroup(ITProcessGroupAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the WRITE user can get a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserGetProcessGroup() throws Exception {
        final ProcessGroupEntity entity = getRandomProcessGroup(ITProcessGroupAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
    }

    /**
     * Ensures the NONE user can get a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserGetProcessGroup() throws Exception {
        final ProcessGroupEntity entity = getRandomProcessGroup(ITProcessGroupAccessControl.helper.getNoneUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
    }

    /**
     * Ensures the READ user cannot put a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserPutProcessGroup() throws Exception {
        final ProcessGroupEntity entity = getRandomProcessGroup(ITProcessGroupAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        // attempt update the name
        entity.getRevision().setClientId(AccessControlHelper.READ_CLIENT_ID);
        entity.getComponent().setName(("Updated Name" + ((ITProcessGroupAccessControl.count)++)));
        // perform the request
        final Response response = updateProcessGroup(ITProcessGroupAccessControl.helper.getReadUser(), entity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ_WRITE user can put a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserPutProcessGroup() throws Exception {
        final ProcessGroupEntity entity = getRandomProcessGroup(ITProcessGroupAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        final String updatedName = "Updated Name" + ((ITProcessGroupAccessControl.count)++);
        // attempt to update the name
        final long version = entity.getRevision().getVersion();
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setName(updatedName);
        // perform the request
        final Response response = updateProcessGroup(ITProcessGroupAccessControl.helper.getReadWriteUser(), entity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final ProcessGroupEntity responseEntity = response.readEntity(ProcessGroupEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.READ_WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
        Assert.assertEquals(updatedName, responseEntity.getComponent().getName());
    }

    /**
     * Ensures the READ_WRITE user can put a process grup.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserPutProcessGroupThroughInheritedPolicy() throws Exception {
        final ProcessGroupEntity entity = createProcessGroup(NiFiTestAuthorizer.NO_POLICY_COMPONENT_NAME);
        final String updatedName = "Updated name" + ((ITProcessGroupAccessControl.count)++);
        // attempt to update the name
        final long version = entity.getRevision().getVersion();
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setName(updatedName);
        // perform the request
        final Response response = updateProcessGroup(ITProcessGroupAccessControl.helper.getReadWriteUser(), entity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final ProcessGroupEntity responseEntity = response.readEntity(ProcessGroupEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.READ_WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
        Assert.assertEquals(updatedName, responseEntity.getComponent().getName());
    }

    /**
     * Ensures the WRITE user can put a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserPutProcessGroup() throws Exception {
        final ProcessGroupEntity entity = getRandomProcessGroup(ITProcessGroupAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        final String updatedName = "Updated Name" + ((ITProcessGroupAccessControl.count)++);
        // attempt to update the name
        final ProcessGroupDTO requestDto = new ProcessGroupDTO();
        requestDto.setId(entity.getId());
        requestDto.setName(updatedName);
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.WRITE_CLIENT_ID);
        final ProcessGroupEntity requestEntity = new ProcessGroupEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateProcessGroup(ITProcessGroupAccessControl.helper.getWriteUser(), requestEntity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final ProcessGroupEntity responseEntity = response.readEntity(ProcessGroupEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
    }

    /**
     * Ensures the NONE user cannot put a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserPutProcessGroup() throws Exception {
        final ProcessGroupEntity entity = getRandomProcessGroup(ITProcessGroupAccessControl.helper.getNoneUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        final String updatedName = "Updated Name" + ((ITProcessGroupAccessControl.count)++);
        // attempt to update the name
        final ProcessGroupDTO requestDto = new ProcessGroupDTO();
        requestDto.setId(entity.getId());
        requestDto.setName(updatedName);
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.NONE_CLIENT_ID);
        final ProcessGroupEntity requestEntity = new ProcessGroupEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateProcessGroup(ITProcessGroupAccessControl.helper.getNoneUser(), requestEntity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ user cannot delete a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserDeleteProcessGroup() throws Exception {
        verifyDelete(ITProcessGroupAccessControl.helper.getReadUser(), AccessControlHelper.READ_CLIENT_ID, 403);
    }

    /**
     * Ensures the READ WRITE user can delete a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserDeleteProcessGroup() throws Exception {
        verifyDelete(ITProcessGroupAccessControl.helper.getReadWriteUser(), AccessControlHelper.READ_WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the WRITE user can delete a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserDeleteProcessGroup() throws Exception {
        verifyDelete(ITProcessGroupAccessControl.helper.getWriteUser(), AccessControlHelper.WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the NONE user can delete a process group.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserDeleteProcessGroup() throws Exception {
        verifyDelete(ITProcessGroupAccessControl.helper.getNoneUser(), AccessControlHelper.NONE_CLIENT_ID, 403);
    }

    /**
     * Ensures malicious string inputs added to the end of a process group
     * are handled safely and a stack trace is not returned.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testProcessGroupRejectMaliciousString() throws Exception {
        final ProcessGroupEntity entity = createProcessGroup(NiFiTestAuthorizer.NO_POLICY_COMPONENT_NAME);
        final String updatedName = "Updated name" + ((ITProcessGroupAccessControl.count)++);
        final String maliciousString = "z--><qss>;<script>alert(\"hello\")</script>";
        final String maliciousErrorMessage = "The request was rejected because the URL contained a potentially malicious String \";\"";
        // attempt to update the name
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setName(updatedName);
        // perform the request
        final Response response = updateProcessGroup(ITProcessGroupAccessControl.helper.getReadWriteUser(), entity, maliciousString);
        String maliciousStringResponse = response.readEntity(String.class);
        // ensure successful response
        Assert.assertEquals(500, response.getStatus());
        // verify
        Assert.assertEquals(maliciousErrorMessage, maliciousStringResponse);
    }
}

