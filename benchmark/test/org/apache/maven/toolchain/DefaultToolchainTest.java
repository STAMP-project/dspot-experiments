/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.toolchain;


import java.io.InputStream;
import java.util.Collections;
import org.apache.maven.toolchain.model.PersistedToolchains;
import org.apache.maven.toolchain.model.ToolchainModel;
import org.apache.maven.toolchain.model.io.xpp3.MavenToolchainsXpp3Reader;
import org.codehaus.plexus.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DefaultToolchainTest {
    @Mock
    private Logger logger;

    private MavenToolchainsXpp3Reader reader = new MavenToolchainsXpp3Reader();

    @Test
    public void testGetModel() {
        ToolchainModel model = new ToolchainModel();
        DefaultToolchain toolchain = newDefaultToolchain(model);
        Assert.assertEquals(model, toolchain.getModel());
    }

    @Test
    public void testGetType() {
        ToolchainModel model = new ToolchainModel();
        DefaultToolchain toolchain = newDefaultToolchain(model, "TYPE");
        Assert.assertEquals("TYPE", toolchain.getType());
        model.setType("MODEL_TYPE");
        toolchain = newDefaultToolchain(model);
        Assert.assertEquals("MODEL_TYPE", toolchain.getType());
    }

    @Test
    public void testGetLogger() {
        ToolchainModel model = new ToolchainModel();
        DefaultToolchain toolchain = newDefaultToolchain(model);
        Assert.assertEquals(logger, toolchain.getLog());
    }

    @Test
    public void testMissingRequirementProperty() {
        ToolchainModel model = new ToolchainModel();
        model.setType("TYPE");
        DefaultToolchain toolchain = newDefaultToolchain(model);
        Assert.assertFalse(toolchain.matchesRequirements(Collections.singletonMap("name", "John Doe")));
        Mockito.verify(logger).debug("Toolchain type:TYPE{} is missing required property: name");
    }

    @Test
    public void testNonMatchingRequirementProperty() {
        ToolchainModel model = new ToolchainModel();
        model.setType("TYPE");
        DefaultToolchain toolchain = newDefaultToolchain(model);
        toolchain.addProvideToken("name", RequirementMatcherFactory.createExactMatcher("Jane Doe"));
        Assert.assertFalse(toolchain.matchesRequirements(Collections.singletonMap("name", "John Doe")));
        Mockito.verify(logger).debug("Toolchain type:TYPE{name = Jane Doe} doesn't match required property: name");
    }

    @Test
    public void testEquals() throws Exception {
        try (InputStream jdksIS = ToolchainModel.class.getResourceAsStream("toolchains-jdks.xml");InputStream jdksExtraIS = ToolchainModel.class.getResourceAsStream("toolchains-jdks-extra.xml")) {
            PersistedToolchains jdks = reader.read(jdksIS);
            PersistedToolchains jdksExtra = reader.read(jdksExtraIS);
            DefaultToolchain tc1 = new org.apache.maven.toolchain.java.DefaultJavaToolChain(jdks.getToolchains().get(0), null);
            DefaultToolchain tc2 = new org.apache.maven.toolchain.java.DefaultJavaToolChain(jdksExtra.getToolchains().get(0), null);
            Assert.assertTrue(tc1.equals(tc1));
            Assert.assertFalse(tc1.equals(tc2));
            Assert.assertFalse(tc2.equals(tc1));
            Assert.assertTrue(tc2.equals(tc2));
        }
    }
}

