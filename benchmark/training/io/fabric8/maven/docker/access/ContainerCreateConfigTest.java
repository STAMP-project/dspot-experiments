package io.fabric8.maven.docker.access;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.fabric8.maven.docker.util.JsonFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/* Copyright 2014 Roland Huss

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
/**
 *
 *
 * @author roland
 * @since 27/03/15
 */
public class ContainerCreateConfigTest {
    @Test
    public void testEnvironment() throws Exception {
        ContainerCreateConfig cc = new ContainerCreateConfig("testImage");
        Map<String, String> envMap = getEnvMap();
        cc.environment(copyPropsToFile(), envMap, Collections.<String, String>emptyMap());
        JsonArray env = getEnvArray(cc);
        Assert.assertNotNull(env);
        Assert.assertEquals(6, env.size());
        List<String> envAsString = convertToList(env);
        Assert.assertTrue(envAsString.contains("JAVA_OPTS=-Xmx512m"));
        Assert.assertTrue(envAsString.contains("TEST_SERVICE=SECURITY"));
        Assert.assertTrue(envAsString.contains("EXTERNAL_ENV=TRUE"));
        Assert.assertTrue(envAsString.contains("TEST_HTTP_ADDR=${docker.container.consul.ip}"));
        Assert.assertTrue(envAsString.contains("TEST_CONSUL_IP=+${docker.container.consul.ip}:8080"));
        Assert.assertTrue(envAsString.contains("TEST_CONSUL_IP_WITHOUT_DELIM=${docker.container.consul.ip}:8225"));
    }

    @Test
    public void testEnvironmentEmptyPropertiesFile() {
        ContainerCreateConfig cc = new ContainerCreateConfig("testImage");
        cc.environment(null, getEnvMap(), Collections.<String, String>emptyMap());
        JsonArray env = getEnvArray(cc);
        Assert.assertEquals(5, env.size());
    }

    @Test
    public void testBind() {
        String[] testData = new String[]{ "c:\\this\\is\\my\\path:/data", "/data", "/home/user:/user", "/user", "c:\\this\\too:/data:ro", "/data" };
        for (int i = 0; i < (testData.length); i += 2) {
            ContainerCreateConfig cc = new ContainerCreateConfig("testImage");
            cc.binds(Arrays.asList(testData[i]));
            JsonObject volumes = ((JsonObject) (JsonFactory.newJsonObject(cc.toJson()).get("Volumes")));
            Assert.assertEquals(1, volumes.size());
            Assert.assertTrue(volumes.has(testData[(i + 1)]));
        }
    }

    @Test
    public void testNullEnvironment() {
        ContainerCreateConfig cc = new ContainerCreateConfig("testImage");
        cc.environment(null, null, Collections.<String, String>emptyMap());
        JsonObject config = JsonFactory.newJsonObject(cc.toJson());
        Assert.assertFalse(config.has("Env"));
    }

    @Test
    public void testEnvNoMap() throws IOException {
        ContainerCreateConfig cc = new ContainerCreateConfig("testImage");
        cc.environment(copyPropsToFile(), null, Collections.<String, String>emptyMap());
        JsonArray env = getEnvArray(cc);
        Assert.assertEquals(2, env.size());
        List<String> envAsString = convertToList(env);
        Assert.assertTrue(envAsString.contains("EXTERNAL_ENV=TRUE"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPropFile() {
        ContainerCreateConfig cc = new ContainerCreateConfig("testImage");
        cc.environment("/not/really/a/file", null, Collections.<String, String>emptyMap());
    }
}

