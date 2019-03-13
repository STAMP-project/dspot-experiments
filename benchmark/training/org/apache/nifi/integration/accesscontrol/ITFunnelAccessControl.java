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
import org.apache.nifi.web.api.dto.FunnelDTO;
import org.apache.nifi.web.api.dto.PositionDTO;
import org.apache.nifi.web.api.dto.RevisionDTO;
import org.apache.nifi.web.api.entity.FunnelEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Access control test for funnels.
 */
public class ITFunnelAccessControl {
    private static AccessControlHelper helper;

    /**
     * Ensures the READ user can get a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserGetFunnel() throws Exception {
        final FunnelEntity entity = getRandomFunnel(ITFunnelAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the READ WRITE user can get a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserGetFunnel() throws Exception {
        final FunnelEntity entity = getRandomFunnel(ITFunnelAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the WRITE user can get a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserGetFunnel() throws Exception {
        final FunnelEntity entity = getRandomFunnel(ITFunnelAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
    }

    /**
     * Ensures the NONE user can get a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserGetFunnel() throws Exception {
        final FunnelEntity entity = getRandomFunnel(ITFunnelAccessControl.helper.getNoneUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
    }

    /**
     * Ensures the READ user cannot put a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserPutFunnel() throws Exception {
        final FunnelEntity entity = getRandomFunnel(ITFunnelAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        // attempt update the position
        entity.getRevision().setClientId(AccessControlHelper.READ_CLIENT_ID);
        entity.getComponent().setPosition(new PositionDTO(0.0, 10.0));
        // perform the request
        final Response response = updateFunnel(ITFunnelAccessControl.helper.getReadUser(), entity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ_WRITE user can put a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserPutFunnel() throws Exception {
        final FunnelEntity entity = getRandomFunnel(ITFunnelAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        final double y = 15.0;
        // attempt to update the position
        final long version = entity.getRevision().getVersion();
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setPosition(new PositionDTO(0.0, y));
        // perform the request
        final Response response = updateFunnel(ITFunnelAccessControl.helper.getReadWriteUser(), entity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final FunnelEntity responseEntity = response.readEntity(FunnelEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.READ_WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
        Assert.assertEquals(y, responseEntity.getComponent().getPosition().getY().doubleValue(), 0);
    }

    /**
     * Ensures the WRITE user can put a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserPutFunnel() throws Exception {
        final FunnelEntity entity = getRandomFunnel(ITFunnelAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        final double y = 15.0;
        // attempt to update the position
        final FunnelDTO requestDto = new FunnelDTO();
        requestDto.setId(entity.getId());
        requestDto.setPosition(new PositionDTO(0.0, y));
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.WRITE_CLIENT_ID);
        final FunnelEntity requestEntity = new FunnelEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateFunnel(ITFunnelAccessControl.helper.getWriteUser(), requestEntity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final FunnelEntity responseEntity = response.readEntity(FunnelEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
    }

    /**
     * Ensures the NONE user cannot put a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserPutFunnel() throws Exception {
        final FunnelEntity entity = getRandomFunnel(ITFunnelAccessControl.helper.getNoneUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        // attempt to update the position
        final FunnelDTO requestDto = new FunnelDTO();
        requestDto.setId(entity.getId());
        requestDto.setPosition(new PositionDTO(0.0, 15.0));
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.NONE_CLIENT_ID);
        final FunnelEntity requestEntity = new FunnelEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateFunnel(ITFunnelAccessControl.helper.getNoneUser(), requestEntity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ user cannot delete a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserDeleteFunnel() throws Exception {
        verifyDelete(ITFunnelAccessControl.helper.getReadUser(), AccessControlHelper.READ_CLIENT_ID, 403);
    }

    /**
     * Ensures the READ WRITE user can delete a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserDeleteFunnel() throws Exception {
        verifyDelete(ITFunnelAccessControl.helper.getReadWriteUser(), AccessControlHelper.READ_WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the WRITE user can delete a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserDeleteFunnel() throws Exception {
        verifyDelete(ITFunnelAccessControl.helper.getWriteUser(), AccessControlHelper.WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the NONE user can delete a funnel.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserDeleteFunnel() throws Exception {
        verifyDelete(ITFunnelAccessControl.helper.getNoneUser(), AccessControlHelper.NONE_CLIENT_ID, 403);
    }
}

