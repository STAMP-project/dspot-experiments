/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.agentdir;


import com.navercorp.pinpoint.bootstrap.AgentDirGenerator;
import com.navercorp.pinpoint.common.Version;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author emeroad
 */
public class AgentDirBaseClassPathResolverTest {
    private static final Logger logger = LoggerFactory.getLogger(AgentDirBaseClassPathResolverTest.class);

    private static final String BOOTSTRAP_JAR = ("pinpoint-bootstrap-" + (Version.VERSION)) + ".jar";

    private static final String TEST_AGENT_DIR = "testagent";

    private static final String SEPARATOR = File.separator;

    private static final AtomicInteger AGENT_ID_ALLOCATOR = new AtomicInteger();

    private static String agentBuildDir;

    private static String agentBootstrapPath;

    private static AgentDirGenerator agentDirGenerator;

    @Test
    public void testFindAgentJar() throws Exception {
        AgentDirBaseClassPathResolverTest.logger.debug("TEST_AGENT_DIR:{}", AgentDirBaseClassPathResolverTest.agentBuildDir);
        AgentDirBaseClassPathResolverTest.logger.debug("agentBootstrapPath:{}", AgentDirBaseClassPathResolverTest.agentBootstrapPath);
        AgentDirBaseClassPathResolver classPathResolver = new AgentDirBaseClassPathResolver(AgentDirBaseClassPathResolverTest.agentBootstrapPath);
        AgentDirectory agentDirectory = classPathResolver.resolve();
        Assert.assertTrue("verify agent directory ", (agentDirectory != null));
        String findAgentJar = agentDirectory.getAgentJarName();
        Assert.assertNotNull(findAgentJar);
        String agentJar = agentDirectory.getAgentJarName();
        Assert.assertEquals(AgentDirBaseClassPathResolverTest.BOOTSTRAP_JAR, agentJar);
        String agentPath = agentDirectory.getAgentJarFullPath();
        Assert.assertEquals(AgentDirBaseClassPathResolverTest.agentBootstrapPath, agentPath);
        String agentDirPath = agentDirectory.getAgentDirPath();
        Assert.assertEquals(AgentDirBaseClassPathResolverTest.agentBuildDir, agentDirPath);
        String agentLibPath = agentDirectory.getAgentLibPath();
        Assert.assertEquals((((AgentDirBaseClassPathResolverTest.agentBuildDir) + (File.separator)) + "lib"), agentLibPath);
        List<JarFile> bootstrapJarFile = agentDirectory.getBootDir().openJarFiles();
        closeJarFile(bootstrapJarFile);
    }

    @Test
    public void findAgentJar() {
        AgentDirBaseClassPathResolverTest.logger.debug("agentBuildDir:{}", AgentDirBaseClassPathResolverTest.agentBuildDir);
        AgentDirBaseClassPathResolverTest.logger.debug("agentBootstrapPath:{}", AgentDirBaseClassPathResolverTest.agentBootstrapPath);
        findAgentJar(AgentDirBaseClassPathResolverTest.agentBootstrapPath);
        findAgentJarAssertFail((((AgentDirBaseClassPathResolverTest.agentBuildDir) + (File.separator)) + "pinpoint-bootstrap-unknown.jar"));
    }
}

