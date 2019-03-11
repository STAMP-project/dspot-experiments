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
import org.junit.Assert;
import org.junit.Test;


/**
 * Access control test for funnels.
 */
public class ITFlowAccessControl {
    private static AccessControlHelper helper;

    /**
     * Test get flow.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetFlow() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/process-groups/root"));
    }

    // TODO - test update flow
    /**
     * Test generate client.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGenerateClientId() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/client-id"));
    }

    /**
     * Test get identity.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetIdentity() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/current-user"));
    }

    /**
     * Test get controller services.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetControllerServices() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/controller/controller-services"));
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/process-groups/root/controller-services"));
    }

    /**
     * Test get reporting tasks.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetReportingTasks() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/reporting-tasks"));
    }

    /**
     * Test search.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testSearch() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/search-results"));
    }

    /**
     * Test status.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testStatus() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/status"));
    }

    /**
     * Test banners.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testBanners() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/status"));
    }

    /**
     * Test bulletin board.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testBulletinBoard() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/bulletin-board"));
    }

    /**
     * Test about.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testAbout() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/about"));
    }

    /**
     * Test get flow config.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetFlowConfig() throws Exception {
        ITFlowAccessControl.helper.testGenericGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/config"));
    }

    /**
     * Test get status.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetStatus() throws Exception {
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/processors/my-component/status"));
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/input-ports/my-component/status"));
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/output-ports/my-component/status"));
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/remote-process-groups/my-component/status"));
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/process-groups/my-component/status"));
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/connections/my-component/status"));
    }

    /**
     * Test get status history.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetStatusHistory() throws Exception {
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/processors/my-component/status/history"));
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/remote-process-groups/my-component/status/history"));
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/process-groups/my-component/status/history"));
        testComponentSpecificGetUri(((ITFlowAccessControl.helper.getBaseUrl()) + "/flow/connections/my-component/status/history"));
    }

    /**
     * Test get action.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetAction() throws Exception {
        final String uri = (ITFlowAccessControl.helper.getBaseUrl()) + "/flow/history/98766";
        Response response;
        // the action does not exist... should return 404
        // read
        response = ITFlowAccessControl.helper.getReadUser().testGet(uri);
        Assert.assertEquals(404, response.getStatus());
        // read/write
        response = ITFlowAccessControl.helper.getReadWriteUser().testGet(uri);
        Assert.assertEquals(404, response.getStatus());
        // no read access should return 403
        // write
        response = ITFlowAccessControl.helper.getWriteUser().testGet(uri);
        Assert.assertEquals(403, response.getStatus());
        // none
        response = ITFlowAccessControl.helper.getNoneUser().testGet(uri);
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Test get action.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetComponentHistory() throws Exception {
        final String uri = (ITFlowAccessControl.helper.getBaseUrl()) + "/flow/history/components/my-component-id";
        // will succeed due to controller level access
        // read
        Response response = ITFlowAccessControl.helper.getReadUser().testGet(uri);
        Assert.assertEquals(200, response.getStatus());
        // read/write
        response = ITFlowAccessControl.helper.getReadWriteUser().testGet(uri);
        Assert.assertEquals(200, response.getStatus());
        // will be denied because component does not exist and no controller level access
        // write
        response = ITFlowAccessControl.helper.getWriteUser().testGet(uri);
        Assert.assertEquals(403, response.getStatus());
        // none
        response = ITFlowAccessControl.helper.getNoneUser().testGet(uri);
        Assert.assertEquals(403, response.getStatus());
    }
}

