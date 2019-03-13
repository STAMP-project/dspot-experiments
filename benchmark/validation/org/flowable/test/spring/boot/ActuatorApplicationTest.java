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
package org.flowable.test.spring.boot;


import HttpMethod.GET;
import HttpStatus.OK;
import SpringBootTest.WebEnvironment;
import flowable.Application;
import java.util.Map;
import org.flowable.spring.boot.actuate.endpoint.ProcessEngineEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


/**
 *
 *
 * @author Filip Hrisafov
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient(registerRestTemplate = true)
public class ActuatorApplicationTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProcessEngineEndpoint processEngineEndpoint;

    @Test
    public void mvcEndpoint() throws Throwable {
        ResponseEntity<Map<String, Object>> mapResponseEntity = restTemplate.exchange((("http://localhost:" + (serverPort)) + "/actuator/flowable/"), GET, null, new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> map = mapResponseEntity.getBody();
        Map<String, Object> invokedResults = processEngineEndpoint.invoke();
        String[] criticalKeys = new String[]{ "completedTaskCount", "openTaskCount", "cachedProcessDefinitionCount" };
        assertThat(map).containsKeys(criticalKeys);
        for (String criticalKey : criticalKeys) {
            Number criticalValue = ((Number) (map.get(criticalKey)));
            Number invokedValue = ((Number) (invokedResults.get(criticalKey)));
            assertThat(criticalValue.longValue()).isEqualTo(invokedValue.longValue());
        }
    }

    @Test
    public void infoEndpoint() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange((("http://localhost:" + (serverPort)) + "/actuator/info/"), GET, null, new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).containsKeys("flowable");
        Map<String, Object> flowableInfo = ((Map<String, Object>) (response.getBody().get("flowable")));
        assertThat(flowableInfo).containsExactly(entry("dbVersion", FlowableVersions.CURRENT_VERSION), entry("version", flowableInfoContributorImplementationVersion()));
    }
}

