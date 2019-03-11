/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.yarn;


import SecurityOptions.KERBEROS_LOGIN_KEYTAB;
import SecurityOptions.KERBEROS_LOGIN_PRINCIPAL;
import YarnConfigKeys.KEYTAB_PATH;
import YarnConfigKeys.KEYTAB_PRINCIPAL;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.security.SecurityUtils;
import org.apache.flink.runtime.security.modules.HadoopModule;
import org.apache.flink.runtime.security.modules.SecurityModule;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static Utils.KEYTAB_FILE_NAME;


/**
 * Tests for the {@link YarnTaskExecutorRunner}.
 */
public class YarnTaskExecutorRunnerTest extends TestLogger {
    @Test
    public void testKerberosKeytabConfiguration() throws Exception {
        final String resourceDirPath = Paths.get("src", "test", "resources").toAbsolutePath().toString();
        final Map<String, String> envs = new HashMap<>(2);
        envs.put(KEYTAB_PRINCIPAL, "testuser1@domain");
        envs.put(KEYTAB_PATH, resourceDirPath);
        Configuration configuration = new Configuration();
        YarnTaskExecutorRunner.setupConfigurationAndInstallSecurityContext(configuration, resourceDirPath, envs);
        final List<SecurityModule> modules = SecurityUtils.getInstalledModules();
        Optional<SecurityModule> moduleOpt = modules.stream().filter(( module) -> module instanceof HadoopModule).findFirst();
        if (moduleOpt.isPresent()) {
            HadoopModule hadoopModule = ((HadoopModule) (moduleOpt.get()));
            Assert.assertThat(hadoopModule.getSecurityConfig().getPrincipal(), Matchers.is("testuser1@domain"));
            Assert.assertThat(hadoopModule.getSecurityConfig().getKeytab(), Matchers.is(new File(resourceDirPath, KEYTAB_FILE_NAME).getAbsolutePath()));
        } else {
            Assert.fail("Can not find HadoopModule!");
        }
        Assert.assertThat(configuration.getString(KERBEROS_LOGIN_KEYTAB), Matchers.is(new File(resourceDirPath, KEYTAB_FILE_NAME).getAbsolutePath()));
        Assert.assertThat(configuration.getString(KERBEROS_LOGIN_PRINCIPAL), Matchers.is("testuser1@domain"));
    }
}

