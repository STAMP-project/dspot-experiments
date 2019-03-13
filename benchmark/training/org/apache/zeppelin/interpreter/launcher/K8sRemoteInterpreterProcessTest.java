/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.interpreter.launcher;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class K8sRemoteInterpreterProcessTest {
    @Test
    public void testGetHostPort() {
        // given
        Kubectl kubectl = Mockito.mock(Kubectl.class);
        Mockito.when(kubectl.getNamespace()).thenReturn("default");
        Properties properties = new Properties();
        HashMap<String, String> envs = new HashMap<String, String>();
        K8sRemoteInterpreterProcess intp = new K8sRemoteInterpreterProcess(kubectl, new File(".skip"), "interpreter-container:1.0", "shared_process", "sh", "shell", properties, envs, "zeppelin.server.hostname", "12320", false, "spark-container:1.0", 10);
        // when
        String host = intp.getHost();
        int port = intp.getPort();
        // then
        Assert.assertEquals(String.format("%s.%s.svc.cluster.local", intp.getPodName(), kubectl.getNamespace()), intp.getHost());
        Assert.assertEquals(12321, intp.getPort());
    }

    @Test
    public void testPredefinedPortNumbers() {
        // given
        Kubectl kubectl = Mockito.mock(Kubectl.class);
        Mockito.when(kubectl.getNamespace()).thenReturn("default");
        Properties properties = new Properties();
        HashMap<String, String> envs = new HashMap<String, String>();
        K8sRemoteInterpreterProcess intp = new K8sRemoteInterpreterProcess(kubectl, new File(".skip"), "interpreter-container:1.0", "shared_process", "sh", "shell", properties, envs, "zeppelin.server.hostname", "12320", false, "spark-container:1.0", 10);
        // following values are hardcoded in k8s/interpreter/100-interpreter.yaml.
        // when change those values, update the yaml file as well.
        Assert.assertEquals(12321, intp.getPort());
        Assert.assertEquals(22321, intp.getSparkDriverPort());
        Assert.assertEquals(22322, intp.getSparkBlockmanagerPort());
    }

    @Test
    public void testGetTemplateBindings() throws IOException {
        // given
        Kubectl kubectl = Mockito.mock(Kubectl.class);
        Mockito.when(kubectl.getNamespace()).thenReturn("default");
        Properties properties = new Properties();
        properties.put("my.key1", "v1");
        HashMap<String, String> envs = new HashMap<String, String>();
        envs.put("MY_ENV1", "V1");
        K8sRemoteInterpreterProcess intp = new K8sRemoteInterpreterProcess(kubectl, new File(".skip"), "interpreter-container:1.0", "shared_process", "sh", "shell", properties, envs, "zeppelin.server.hostname", "12320", false, "spark-container:1.0", 10);
        // when
        Properties p = intp.getTemplateBindings();
        // then
        Assert.assertEquals("default", p.get("zeppelin.k8s.namespace"));
        Assert.assertEquals(intp.getPodName(), p.get("zeppelin.k8s.interpreter.pod.name"));
        Assert.assertEquals("sh", p.get("zeppelin.k8s.interpreter.container.name"));
        Assert.assertEquals("interpreter-container:1.0", p.get("zeppelin.k8s.interpreter.container.image"));
        Assert.assertEquals("shared_process", p.get("zeppelin.k8s.interpreter.group.id"));
        Assert.assertEquals("sh", p.get("zeppelin.k8s.interpreter.group.name"));
        Assert.assertEquals("shell", p.get("zeppelin.k8s.interpreter.setting.name"));
        Assert.assertEquals(true, p.containsKey("zeppelin.k8s.interpreter.localRepo"));
        Assert.assertEquals("12321:12321", p.get("zeppelin.k8s.interpreter.rpc.portRange"));
        Assert.assertEquals("zeppelin.server.hostname", p.get("zeppelin.k8s.server.rpc.host"));
        Assert.assertEquals("12320", p.get("zeppelin.k8s.server.rpc.portRange"));
        Assert.assertEquals("v1", p.get("my.key1"));
        Assert.assertEquals("V1", envs.get("MY_ENV1"));
        envs = ((HashMap<String, String>) (p.get("zeppelin.k8s.envs")));
        Assert.assertEquals(true, envs.containsKey("SERVICE_DOMAIN"));
        Assert.assertEquals(true, envs.containsKey("ZEPPELIN_HOME"));
    }

    @Test
    public void testGetTemplateBindingsForSpark() throws IOException {
        // given
        Kubectl kubectl = Mockito.mock(Kubectl.class);
        Mockito.when(kubectl.getNamespace()).thenReturn("default");
        Properties properties = new Properties();
        properties.put("my.key1", "v1");
        properties.put("master", "k8s://http://api");
        HashMap<String, String> envs = new HashMap<String, String>();
        envs.put("MY_ENV1", "V1");
        envs.put("SPARK_SUBMIT_OPTIONS", "my options");
        envs.put("SERVICE_DOMAIN", "mydomain");
        K8sRemoteInterpreterProcess intp = new K8sRemoteInterpreterProcess(kubectl, new File(".skip"), "interpreter-container:1.0", "shared_process", "spark", "myspark", properties, envs, "zeppelin.server.hostname", "12320", false, "spark-container:1.0", 10);
        // when
        Properties p = intp.getTemplateBindings();
        // then
        Assert.assertEquals("spark-container:1.0", p.get("zeppelin.k8s.spark.container.image"));
        Assert.assertEquals(String.format("//4040-%s.%s", intp.getPodName(), "mydomain"), p.get("zeppelin.spark.uiWebUrl"));
        envs = ((HashMap<String, String>) (p.get("zeppelin.k8s.envs")));
        Assert.assertTrue(envs.containsKey("SPARK_HOME"));
        String sparkSubmitOptions = envs.get("SPARK_SUBMIT_OPTIONS");
        Assert.assertTrue(sparkSubmitOptions.startsWith("my options "));
        Assert.assertTrue(sparkSubmitOptions.contains(("spark.kubernetes.namespace=" + (kubectl.getNamespace()))));
        Assert.assertTrue(sparkSubmitOptions.contains(("spark.kubernetes.driver.pod.name=" + (intp.getPodName()))));
        Assert.assertTrue(sparkSubmitOptions.contains("spark.kubernetes.container.image=spark-container:1.0"));
        Assert.assertTrue(sparkSubmitOptions.contains(("spark.driver.host=" + (intp.getHost()))));
        Assert.assertTrue(sparkSubmitOptions.contains(("spark.driver.port=" + (intp.getSparkDriverPort()))));
        Assert.assertTrue(sparkSubmitOptions.contains(("spark.blockManager.port=" + (intp.getSparkBlockmanagerPort()))));
    }
}

