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
import org.apache.nifi.web.api.dto.LabelDTO;
import org.apache.nifi.web.api.dto.RevisionDTO;
import org.apache.nifi.web.api.entity.LabelEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Access control test for labels.
 */
public class ITLabelAccessControl {
    private static AccessControlHelper helper;

    /**
     * Ensures the READ user can get a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserGetLabel() throws Exception {
        final LabelEntity entity = getRandomLabel(ITLabelAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the READ WRITE user can get a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserGetLabel() throws Exception {
        final LabelEntity entity = getRandomLabel(ITLabelAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the WRITE user can get a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserGetLabel() throws Exception {
        final LabelEntity entity = getRandomLabel(ITLabelAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
    }

    /**
     * Ensures the NONE user can get a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserGetLabel() throws Exception {
        final LabelEntity entity = getRandomLabel(ITLabelAccessControl.helper.getNoneUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
    }

    /**
     * Ensures the READ user cannot put a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserPutLabel() throws Exception {
        final LabelEntity entity = getRandomLabel(ITLabelAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        // attempt update the name
        entity.getRevision().setClientId(AccessControlHelper.READ_CLIENT_ID);
        entity.getComponent().setLabel("Updated Label");
        // perform the request
        final Response response = updateLabel(ITLabelAccessControl.helper.getReadUser(), entity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ_WRITE user can put a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserPutLabel() throws Exception {
        final LabelEntity entity = getRandomLabel(ITLabelAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        final String updatedLabel = "Updated Name";
        // attempt to update the name
        final long version = entity.getRevision().getVersion();
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setLabel(updatedLabel);
        // perform the request
        final Response response = updateLabel(ITLabelAccessControl.helper.getReadWriteUser(), entity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final LabelEntity responseEntity = response.readEntity(LabelEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.READ_WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
        Assert.assertEquals(updatedLabel, responseEntity.getComponent().getLabel());
    }

    /**
     * Ensures the READ_WRITE user can put a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserPutLabelThroughInheritedPolicy() throws Exception {
        final LabelEntity entity = createLabel(NiFiTestAuthorizer.NO_POLICY_COMPONENT_NAME);
        final String updatedLabel = "Updated name";
        // attempt to update the name
        final long version = entity.getRevision().getVersion();
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setLabel(updatedLabel);
        // perform the request
        final Response response = updateLabel(ITLabelAccessControl.helper.getReadWriteUser(), entity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final LabelEntity responseEntity = response.readEntity(LabelEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.READ_WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
        Assert.assertEquals(updatedLabel, responseEntity.getComponent().getLabel());
    }

    /**
     * Ensures the WRITE user can put a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserPutLabel() throws Exception {
        final LabelEntity entity = getRandomLabel(ITLabelAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        final String updatedLabel = "Updated Name";
        // attempt to update the label
        final LabelDTO requestDto = new LabelDTO();
        requestDto.setId(entity.getId());
        requestDto.setLabel(updatedLabel);
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.WRITE_CLIENT_ID);
        final LabelEntity requestEntity = new LabelEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateLabel(ITLabelAccessControl.helper.getWriteUser(), requestEntity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final LabelEntity responseEntity = response.readEntity(LabelEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
    }

    /**
     * Ensures the NONE user cannot put a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserPutLabel() throws Exception {
        final LabelEntity entity = getRandomLabel(ITLabelAccessControl.helper.getNoneUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        final String updatedName = "Updated Name";
        // attempt to update the name
        final LabelDTO requestDto = new LabelDTO();
        requestDto.setId(entity.getId());
        requestDto.setLabel(updatedName);
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.NONE_CLIENT_ID);
        final LabelEntity requestEntity = new LabelEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateLabel(ITLabelAccessControl.helper.getNoneUser(), requestEntity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ user cannot delete a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserDeleteLabel() throws Exception {
        verifyDelete(ITLabelAccessControl.helper.getReadUser(), AccessControlHelper.READ_CLIENT_ID, 403);
    }

    /**
     * Ensures the READ WRITE user can delete a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserDeleteLabel() throws Exception {
        verifyDelete(ITLabelAccessControl.helper.getReadWriteUser(), AccessControlHelper.READ_WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the WRITE user can delete a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserDeleteLabel() throws Exception {
        verifyDelete(ITLabelAccessControl.helper.getWriteUser(), AccessControlHelper.WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the NONE user can delete a label.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserDeleteLabel() throws Exception {
        verifyDelete(ITLabelAccessControl.helper.getNoneUser(), AccessControlHelper.NONE_CLIENT_ID, 403);
    }
}

