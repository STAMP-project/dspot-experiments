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
import org.apache.nifi.integration.util.RestrictedProcessor;
import org.apache.nifi.util.Tuple;
import org.apache.nifi.web.api.dto.ProcessorDTO;
import org.apache.nifi.web.api.dto.RevisionDTO;
import org.apache.nifi.web.api.entity.CopySnippetRequestEntity;
import org.apache.nifi.web.api.entity.CreateTemplateRequestEntity;
import org.apache.nifi.web.api.entity.FlowEntity;
import org.apache.nifi.web.api.entity.InstantiateTemplateRequestEntity;
import org.apache.nifi.web.api.entity.ProcessorEntity;
import org.apache.nifi.web.api.entity.SnippetEntity;
import org.apache.nifi.web.api.entity.TemplateEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Access control test for processors.
 */
public class ITProcessorAccessControl {
    private static AccessControlHelper helper;

    /**
     * Ensures the READ user can get a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserGetProcessor() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the READ WRITE user can get a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserGetProcessor() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
    }

    /**
     * Ensures the WRITE user can get a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserGetProcessor() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
    }

    /**
     * Ensures the NONE user can get a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserGetProcessor() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getNoneUser());
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
    public void testReadUserPutProcessor() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        // attempt update the name
        entity.getRevision().setClientId(AccessControlHelper.READ_CLIENT_ID);
        entity.getComponent().setName("Updated Name");
        // perform the request
        final Response response = updateProcessor(ITProcessorAccessControl.helper.getReadUser(), entity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ_WRITE user can put a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserPutProcessor() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getReadWriteUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        final String updatedName = "Updated Name";
        // attempt to update the name
        final long version = entity.getRevision().getVersion();
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setName(updatedName);
        // perform the request
        final Response response = updateProcessor(ITProcessorAccessControl.helper.getReadWriteUser(), entity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final ProcessorEntity responseEntity = response.readEntity(ProcessorEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.READ_WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
        Assert.assertEquals(updatedName, responseEntity.getComponent().getName());
    }

    /**
     * Ensures the READ_WRITE user can put a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserPutProcessorThroughInheritedPolicy() throws Exception {
        final ProcessorEntity entity = ITProcessorAccessControl.createProcessor(ITProcessorAccessControl.helper, NiFiTestAuthorizer.NO_POLICY_COMPONENT_NAME);
        final String updatedName = "Updated name";
        // attempt to update the name
        final long version = entity.getRevision().getVersion();
        entity.getRevision().setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        entity.getComponent().setName(updatedName);
        // perform the request
        final Response response = updateProcessor(ITProcessorAccessControl.helper.getReadWriteUser(), entity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final ProcessorEntity responseEntity = response.readEntity(ProcessorEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.READ_WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
        Assert.assertEquals(updatedName, responseEntity.getComponent().getName());
    }

    /**
     * Ensures the WRITE user can put a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserPutProcessor() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getWriteUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertTrue(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        final String updatedName = "Updated Name";
        // attempt to update the name
        final ProcessorDTO requestDto = new ProcessorDTO();
        requestDto.setId(entity.getId());
        requestDto.setName(updatedName);
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.WRITE_CLIENT_ID);
        final ProcessorEntity requestEntity = new ProcessorEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateProcessor(ITProcessorAccessControl.helper.getWriteUser(), requestEntity);
        // ensure successful response
        Assert.assertEquals(200, response.getStatus());
        // get the response
        final ProcessorEntity responseEntity = response.readEntity(ProcessorEntity.class);
        // verify
        Assert.assertEquals(AccessControlHelper.WRITE_CLIENT_ID, responseEntity.getRevision().getClientId());
        Assert.assertEquals((version + 1), responseEntity.getRevision().getVersion().longValue());
    }

    /**
     * Ensures the NONE user cannot put a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserPutProcessor() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getNoneUser());
        Assert.assertFalse(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNull(entity.getComponent());
        final String updatedName = "Updated Name";
        // attempt to update the name
        final ProcessorDTO requestDto = new ProcessorDTO();
        requestDto.setId(entity.getId());
        requestDto.setName(updatedName);
        final long version = entity.getRevision().getVersion();
        final RevisionDTO requestRevision = new RevisionDTO();
        requestRevision.setVersion(version);
        requestRevision.setClientId(AccessControlHelper.NONE_CLIENT_ID);
        final ProcessorEntity requestEntity = new ProcessorEntity();
        requestEntity.setId(entity.getId());
        requestEntity.setRevision(requestRevision);
        requestEntity.setComponent(requestDto);
        // perform the request
        final Response response = updateProcessor(ITProcessorAccessControl.helper.getNoneUser(), requestEntity);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ user cannot clear state.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserClearState() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        final String url = (((ITProcessorAccessControl.helper.getBaseUrl()) + "/processors/") + (entity.getId())) + "/state/clear-requests";
        // perform the request
        final Response response = ITProcessorAccessControl.helper.getReadUser().testPost(url);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the NONE user cannot clear state.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserClearState() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        final String url = (((ITProcessorAccessControl.helper.getBaseUrl()) + "/processors/") + (entity.getId())) + "/state/clear-requests";
        // perform the request
        final Response response = ITProcessorAccessControl.helper.getNoneUser().testPost(url);
        // ensure forbidden response
        Assert.assertEquals(403, response.getStatus());
    }

    /**
     * Ensures the READ WRITE user can clear state.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserClearState() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        final String url = (((ITProcessorAccessControl.helper.getBaseUrl()) + "/processors/") + (entity.getId())) + "/state/clear-requests";
        // perform the request
        final Response response = ITProcessorAccessControl.helper.getReadWriteUser().testPost(url);
        // ensure ok response
        Assert.assertEquals(200, response.getStatus());
    }

    /**
     * Ensures the WRITE user can clear state.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserClearState() throws Exception {
        final ProcessorEntity entity = getRandomProcessor(ITProcessorAccessControl.helper.getReadUser());
        Assert.assertTrue(entity.getPermissions().getCanRead());
        Assert.assertFalse(entity.getPermissions().getCanWrite());
        Assert.assertNotNull(entity.getComponent());
        final String url = (((ITProcessorAccessControl.helper.getBaseUrl()) + "/processors/") + (entity.getId())) + "/state/clear-requests";
        // perform the request
        final Response response = ITProcessorAccessControl.helper.getWriteUser().testPost(url);
        // ensure ok response
        Assert.assertEquals(200, response.getStatus());
    }

    /**
     * Ensures the READ user cannot delete a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadUserDeleteProcessor() throws Exception {
        verifyDelete(ITProcessorAccessControl.helper.getReadUser(), AccessControlHelper.READ_CLIENT_ID, 403);
    }

    /**
     * Ensures the READ WRITE user can delete a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testReadWriteUserDeleteProcessor() throws Exception {
        verifyDelete(ITProcessorAccessControl.helper.getReadWriteUser(), AccessControlHelper.READ_WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the WRITE user can delete a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testWriteUserDeleteProcessor() throws Exception {
        verifyDelete(ITProcessorAccessControl.helper.getWriteUser(), AccessControlHelper.WRITE_CLIENT_ID, 200);
    }

    /**
     * Ensures the NONE user can delete a processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testNoneUserDeleteProcessor() throws Exception {
        verifyDelete(ITProcessorAccessControl.helper.getNoneUser(), AccessControlHelper.NONE_CLIENT_ID, 403);
    }

    /**
     * Tests attempt to create a restricted processor.
     *
     * @throws Exception
     * 		if there is an error creating this processor
     */
    @Test
    public void testCreateRestrictedProcessor() throws Exception {
        String url = (ITProcessorAccessControl.helper.getBaseUrl()) + "/process-groups/root/processors";
        // create the processor
        ProcessorDTO processor = new ProcessorDTO();
        processor.setName("restricted");
        processor.setType(RestrictedProcessor.class.getName());
        // create the revision
        final RevisionDTO revision = new RevisionDTO();
        revision.setClientId(AccessControlHelper.READ_WRITE_CLIENT_ID);
        revision.setVersion(0L);
        // create the entity body
        ProcessorEntity entity = new ProcessorEntity();
        entity.setRevision(revision);
        entity.setComponent(processor);
        // perform the request as a user with read/write but no restricted access
        Response response = ITProcessorAccessControl.helper.getReadWriteUser().testPost(url, entity);
        // ensure the request is successful
        Assert.assertEquals(403, response.getStatus());
        // perform the request as a user with read/write and only execute code restricted access
        response = ITProcessorAccessControl.helper.getExecuteCodeUser().testPost(url, entity);
        // ensure the request is successful
        Assert.assertEquals(403, response.getStatus());
        // perform the request as a user with read/write and restricted access
        response = ITProcessorAccessControl.helper.getPrivilegedUser().testPost(url, entity);
        // ensure the request is successful
        Assert.assertEquals(201, response.getStatus());
        final ProcessorEntity responseEntity = response.readEntity(ProcessorEntity.class);
        // remove the restricted component
        deleteRestrictedComponent(responseEntity, ITProcessorAccessControl.helper.getPrivilegedUser());
    }

    /**
     * Tests attempt to create a restricted processor requiring execute code permissions.
     *
     * @throws Exception
     * 		if there is an error creating this processor
     */
    @Test
    public void testCreateExecuteCodeRestrictedProcessor() throws Exception {
        createExecuteCodeRestrictedProcessor(ITProcessorAccessControl.helper.getPrivilegedUser());
        createExecuteCodeRestrictedProcessor(ITProcessorAccessControl.helper.getExecuteCodeUser());
    }

    /**
     * Tests attempting to copy/paste a restricted processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testCopyPasteRestrictedProcessor() throws Exception {
        final String copyUrl = (ITProcessorAccessControl.helper.getBaseUrl()) + "/process-groups/root/snippet-instance";
        final Tuple<ProcessorEntity, SnippetEntity> tuple = createSnippetWithRestrictedComponent(RestrictedProcessor.class.getName(), ITProcessorAccessControl.helper.getPrivilegedUser());
        final SnippetEntity snippetEntity = tuple.getValue();
        // build the copy/paste request
        final CopySnippetRequestEntity copyRequest = new CopySnippetRequestEntity();
        copyRequest.setSnippetId(snippetEntity.getSnippet().getId());
        copyRequest.setOriginX(0.0);
        copyRequest.setOriginY(0.0);
        // create the snippet
        Response response = ITProcessorAccessControl.helper.getReadWriteUser().testPost(copyUrl, copyRequest);
        // ensure the request failed... need privileged users since snippet comprised of the restricted components
        Assert.assertEquals(403, response.getStatus());
        // perform the request as a user with read/write and only execute code restricted access
        response = ITProcessorAccessControl.helper.getExecuteCodeUser().testPost(copyUrl, copyRequest);
        // ensure the request is successful
        Assert.assertEquals(403, response.getStatus());
        // create the snippet
        response = ITProcessorAccessControl.helper.getPrivilegedUser().testPost(copyUrl, copyRequest);
        // ensure the request is successful
        Assert.assertEquals(201, response.getStatus());
        final FlowEntity flowEntity = response.readEntity(FlowEntity.class);
        // remove the restricted processors
        deleteRestrictedComponent(tuple.getKey(), ITProcessorAccessControl.helper.getPrivilegedUser());
        deleteRestrictedComponent(flowEntity.getFlow().getProcessors().stream().findFirst().orElse(null), ITProcessorAccessControl.helper.getPrivilegedUser());
    }

    /**
     * Tests attempting to copy/paste a restricted processor requiring execute code permissions.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testCopyPasteExecuteCodeRestrictedProcessor() throws Exception {
        copyPasteExecuteCodeRestrictedProcessor(ITProcessorAccessControl.helper.getPrivilegedUser());
        copyPasteExecuteCodeRestrictedProcessor(ITProcessorAccessControl.helper.getExecuteCodeUser());
    }

    /**
     * Tests attempting to use a template with a restricted processor.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testTemplateWithRestrictedProcessor() throws Exception {
        final String createTemplateUrl = (ITProcessorAccessControl.helper.getBaseUrl()) + "/process-groups/root/templates";
        final String instantiateTemplateUrl = (ITProcessorAccessControl.helper.getBaseUrl()) + "/process-groups/root/template-instance";
        final Tuple<ProcessorEntity, SnippetEntity> tuple = createSnippetWithRestrictedComponent(RestrictedProcessor.class.getName(), ITProcessorAccessControl.helper.getPrivilegedUser());
        final SnippetEntity snippetEntity = tuple.getValue();
        // create the template
        final CreateTemplateRequestEntity createTemplateRequest = new CreateTemplateRequestEntity();
        createTemplateRequest.setSnippetId(snippetEntity.getSnippet().getId());
        createTemplateRequest.setName("test");
        // create the snippet
        Response response = ITProcessorAccessControl.helper.getWriteUser().testPost(createTemplateUrl, createTemplateRequest);
        // ensure the request failed... need read perms to the components in the snippet
        Assert.assertEquals(403, response.getStatus());
        response = ITProcessorAccessControl.helper.getReadWriteUser().testPost(createTemplateUrl, createTemplateRequest);
        // ensure the request is successful
        Assert.assertEquals(201, response.getStatus());
        final TemplateEntity templateEntity = response.readEntity(TemplateEntity.class);
        // build the template request
        final InstantiateTemplateRequestEntity instantiateTemplateRequest = new InstantiateTemplateRequestEntity();
        instantiateTemplateRequest.setTemplateId(templateEntity.getTemplate().getId());
        instantiateTemplateRequest.setOriginX(0.0);
        instantiateTemplateRequest.setOriginY(0.0);
        // create the snippet
        response = ITProcessorAccessControl.helper.getReadWriteUser().testPost(instantiateTemplateUrl, instantiateTemplateRequest);
        // ensure the request failed... need privileged user since the template is comprised of restricted components
        Assert.assertEquals(403, response.getStatus());
        // create the snippet
        response = ITProcessorAccessControl.helper.getExecuteCodeUser().testPost(instantiateTemplateUrl, instantiateTemplateRequest);
        // ensure the request failed... need privileged user since the template is comprised of restricted components
        Assert.assertEquals(403, response.getStatus());
        // create the snippet
        response = ITProcessorAccessControl.helper.getPrivilegedUser().testPost(instantiateTemplateUrl, instantiateTemplateRequest);
        // ensure the request is successful
        Assert.assertEquals(201, response.getStatus());
        final FlowEntity flowEntity = response.readEntity(FlowEntity.class);
        // clean up the resources created during this test
        deleteTemplate(templateEntity);
        deleteRestrictedComponent(tuple.getKey(), ITProcessorAccessControl.helper.getPrivilegedUser());
        deleteRestrictedComponent(flowEntity.getFlow().getProcessors().stream().findFirst().orElse(null), ITProcessorAccessControl.helper.getPrivilegedUser());
    }

    /**
     * Tests attempting to use a template with a restricted processor requiring execute code permissions.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testTemplateWithExecuteCodeRestrictedProcessor() throws Exception {
        templateWithExecuteCodeRestrictedProcessor(ITProcessorAccessControl.helper.getPrivilegedUser());
        templateWithExecuteCodeRestrictedProcessor(ITProcessorAccessControl.helper.getExecuteCodeUser());
    }
}

