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


import java.io.IOException;
import java.util.Properties;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.interpreter.InterpreterOption;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * In the future, test may use minikube on travis for end-to-end test
 * https://github.com/LiliC/travis-minikube
 * https://blog.travis-ci.com/2017-10-26-running-kubernetes-on-travis-ci-with-minikube
 */
public class K8sStandardInterpreterLauncherTest {
    @Test
    public void testK8sLauncher() throws IOException {
        // given
        Kubectl kubectl = Mockito.mock(Kubectl.class);
        Mockito.when(kubectl.getNamespace()).thenReturn("default");
        ZeppelinConfiguration zConf = new ZeppelinConfiguration();
        K8sStandardInterpreterLauncher launcher = new K8sStandardInterpreterLauncher(zConf, null, kubectl);
        Properties properties = new Properties();
        properties.setProperty("ENV_1", "VALUE_1");
        properties.setProperty("property_1", "value_1");
        properties.setProperty("CALLBACK_HOST", "zeppelin-server.default.svc.cluster.local");
        properties.setProperty("CALLBACK_PORT", "12320");
        InterpreterOption option = new InterpreterOption();
        option.setUserImpersonate(true);
        InterpreterLaunchContext context = new InterpreterLaunchContext(properties, option, null, "user1", "intpGroupId", "groupId", "sh", "name", 0, "host");
        // when
        InterpreterClient client = launcher.launch(context);
        // then
        Assert.assertTrue((client instanceof K8sRemoteInterpreterProcess));
    }
}

