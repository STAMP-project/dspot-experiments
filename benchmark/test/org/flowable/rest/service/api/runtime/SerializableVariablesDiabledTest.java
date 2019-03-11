/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.rest.service.api.runtime;


import RestUrls.URL_PROCESS_INSTANCE_VARIABLE_COLLECTION;
import RestUrls.URL_TASK_VARIABLES_COLLECTION;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.rest.service.HttpMultipartHelper;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.task.api.Task;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class SerializableVariablesDiabledTest {
    private RepositoryService repositoryService;

    private RuntimeService runtimeService;

    private IdentityService identityService;

    private TaskService taskService;

    private String serverUrlPrefix;

    private String testUserId;

    private String testGroupId;

    @Test
    public void testCreateSingleSerializableProcessVariable() throws Exception {
        repositoryService.createDeployment().addClasspathResource("org/flowable/rest/service/api/runtime/ProcessInstanceVariablesCollectionResourceTest.testProcess.bpmn20.xml").deploy();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
        TestSerializableVariable serializable = new TestSerializableVariable();
        serializable.setSomeField("some value");
        // Serialize object to readable stream for representation
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(buffer);
        output.writeObject(serializable);
        output.close();
        InputStream binaryContent = new ByteArrayInputStream(buffer.toByteArray());
        // Add name, type and scope
        Map<String, String> additionalFields = new HashMap<>();
        additionalFields.put("name", "serializableVariable");
        additionalFields.put("type", "serializable");
        // Upload a valid BPMN-file using multipart-data
        HttpPost httpPost = new HttpPost(((serverUrlPrefix) + (RestUrls.createRelativeResourceUrl(URL_PROCESS_INSTANCE_VARIABLE_COLLECTION, processInstance.getId()))));
        httpPost.setEntity(HttpMultipartHelper.getMultiPartEntity("value", "application/x-java-serialized-object", binaryContent, additionalFields));
        // We have serializeable object disabled, we should get a 415.
        assertResponseStatus(httpPost, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void testCreateSingleSerializableTaskVariable() throws Exception {
        repositoryService.createDeployment().addClasspathResource("org/flowable/rest/service/api/runtime/ProcessInstanceVariablesCollectionResourceTest.testProcess.bpmn20.xml").deploy();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        TestSerializableVariable serializable = new TestSerializableVariable();
        serializable.setSomeField("some value");
        // Serialize object to readable stream for representation
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(buffer);
        output.writeObject(serializable);
        output.close();
        InputStream binaryContent = new ByteArrayInputStream(buffer.toByteArray());
        // Add name, type and scope
        Map<String, String> additionalFields = new HashMap<>();
        additionalFields.put("name", "serializableVariable");
        additionalFields.put("type", "serializable");
        HttpPost httpPost = new HttpPost(((serverUrlPrefix) + (RestUrls.createRelativeResourceUrl(URL_TASK_VARIABLES_COLLECTION, task.getId()))));
        httpPost.setEntity(HttpMultipartHelper.getMultiPartEntity("value", "application/x-java-serialized-object", binaryContent, additionalFields));
        // We have serializeable object disabled, we should get a 415.
        assertResponseStatus(httpPost, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }
}

