/**
 * Copyright 2013-2019 the original author or authors.
 *
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
package org.springframework.cloud.netflix.eureka.server;


import com.netflix.appinfo.ApplicationInfoManager;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


public class EurekaControllerTests {
    private ApplicationInfoManager infoManager;

    private ApplicationInfoManager original;

    @Test
    public void testStatus() throws Exception {
        Map<String, Object> model = new HashMap<>();
        EurekaController controller = new EurekaController(infoManager);
        controller.status(new MockHttpServletRequest("GET", "/"), model);
        Map<String, Object> app = getFirst(model, "apps");
        Map<String, Object> instanceInfo = getFirst(app, "instanceInfos");
        Map<String, Object> instance = getFirst(instanceInfo, "instances");
        assertThat(((String) (instance.get("id")))).as("id was wrong").isEqualTo("myapp:1");
        assertThat(instance.get("url")).as("url was not null").isNull();
        assertThat(((Boolean) (instance.get("isHref")))).as("isHref was wrong").isFalse();
    }
}

