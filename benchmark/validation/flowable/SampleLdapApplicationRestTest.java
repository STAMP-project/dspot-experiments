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
package flowable;


import HttpMethod.GET;
import HttpStatus.FORBIDDEN;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import SpringBootTest.WebEnvironment;
import org.flowable.common.rest.api.DataResponse;
import org.flowable.rest.service.api.repository.ProcessDefinitionResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Filip Hrisafov
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient(registerRestTemplate = true)
@RunWith(SpringRunner.class)
public class SampleLdapApplicationRestTest extends AbstractSampleLdapTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void invalidPasswordShouldBeRejected() {
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/process-api/repository/process-definitions";
        HttpEntity<?> request = new HttpEntity(SampleLdapApplicationRestTest.createHeaders("kermit", "password"));
        ResponseEntity<DataResponse<ProcessDefinitionResponse>> response = testRestTemplate.exchange(processDefinitionsUrl, GET, request, new org.springframework.core.ParameterizedTypeReference<DataResponse<ProcessDefinitionResponse>>() {});
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void correctPasswordShouldBeAccepted() {
        String processDefinitionsUrl = ("http://localhost:" + (serverPort)) + "/process-api/repository/process-definitions";
        HttpEntity<?> request = new HttpEntity(SampleLdapApplicationRestTest.createHeaders("kermit", "pass"));
        ResponseEntity<DataResponse<ProcessDefinitionResponse>> response = testRestTemplate.exchange(processDefinitionsUrl, GET, request, new org.springframework.core.ParameterizedTypeReference<DataResponse<ProcessDefinitionResponse>>() {});
        assertThat(response.getStatusCode()).isEqualTo(OK);
        request = new HttpEntity(SampleLdapApplicationRestTest.createHeaders("pepe", "pass"));
        response = testRestTemplate.exchange(processDefinitionsUrl, GET, request, new org.springframework.core.ParameterizedTypeReference<DataResponse<ProcessDefinitionResponse>>() {});
        assertThat(response.getStatusCode()).isEqualTo(OK);
        String jobsUrl = ("http://localhost:" + (serverPort)) + "/process-api/management/jobs";
        request = new HttpEntity(SampleLdapApplicationRestTest.createHeaders("kermit", "pass"));
        response = testRestTemplate.exchange(jobsUrl, GET, request, new org.springframework.core.ParameterizedTypeReference<DataResponse<ProcessDefinitionResponse>>() {});
        assertThat(response.getStatusCode()).isEqualTo(OK);
        request = new HttpEntity(SampleLdapApplicationRestTest.createHeaders("fozzie", "pass"));
        response = testRestTemplate.exchange(jobsUrl, GET, request, new org.springframework.core.ParameterizedTypeReference<DataResponse<ProcessDefinitionResponse>>() {});
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void lackingPermissionsShouldBeForbidden() {
        String jobsUrl = ("http://localhost:" + (serverPort)) + "/process-api/management/jobs";
        HttpEntity<?> request = new HttpEntity(SampleLdapApplicationRestTest.createHeaders("pepe", "pass"));
        ResponseEntity<DataResponse<ProcessDefinitionResponse>> response = testRestTemplate.exchange(jobsUrl, GET, request, new org.springframework.core.ParameterizedTypeReference<DataResponse<ProcessDefinitionResponse>>() {});
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }
}

