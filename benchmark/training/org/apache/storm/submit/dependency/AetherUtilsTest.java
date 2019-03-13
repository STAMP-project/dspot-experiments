/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.submit.dependency;


import JavaScopes.COMPILE;
import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;
import org.junit.Assert;
import org.junit.Test;


public class AetherUtilsTest {
    @Test
    public void parseDependency() throws Exception {
        String testDependency = "testgroup:testartifact:1.0.0^testgroup:testexcartifact^testgroup:*";
        Dependency dependency = AetherUtils.parseDependency(testDependency);
        Assert.assertEquals("testgroup", dependency.getArtifact().getGroupId());
        Assert.assertEquals("testartifact", dependency.getArtifact().getArtifactId());
        Assert.assertEquals("1.0.0", dependency.getArtifact().getVersion());
        Assert.assertEquals(COMPILE, dependency.getScope());
        Assert.assertEquals(2, dependency.getExclusions().size());
        List<Exclusion> exclusions = Lists.newArrayList(dependency.getExclusions());
        Exclusion exclusion = exclusions.get(0);
        Assert.assertEquals("testgroup", exclusion.getGroupId());
        Assert.assertEquals("testexcartifact", exclusion.getArtifactId());
        Assert.assertEquals(COMPILE, dependency.getScope());
        exclusion = exclusions.get(1);
        Assert.assertEquals("testgroup", exclusion.getGroupId());
        Assert.assertEquals("*", exclusion.getArtifactId());
        Assert.assertEquals(COMPILE, dependency.getScope());
    }

    @Test
    public void createExclusion() throws Exception {
        String testExclusion = "group";
        Exclusion exclusion = AetherUtils.createExclusion(testExclusion);
        Assert.assertEquals("group", exclusion.getGroupId());
        Assert.assertEquals("*", exclusion.getArtifactId());
        Assert.assertEquals("*", exclusion.getClassifier());
        Assert.assertEquals("*", exclusion.getExtension());
        testExclusion = "group:artifact";
        exclusion = AetherUtils.createExclusion(testExclusion);
        Assert.assertEquals("group", exclusion.getGroupId());
        Assert.assertEquals("artifact", exclusion.getArtifactId());
        Assert.assertEquals("*", exclusion.getClassifier());
        Assert.assertEquals("*", exclusion.getExtension());
        testExclusion = "group:artifact:site";
        exclusion = AetherUtils.createExclusion(testExclusion);
        Assert.assertEquals("group", exclusion.getGroupId());
        Assert.assertEquals("artifact", exclusion.getArtifactId());
        Assert.assertEquals("site", exclusion.getClassifier());
        Assert.assertEquals("*", exclusion.getExtension());
        testExclusion = "group:artifact:site:jar";
        exclusion = AetherUtils.createExclusion(testExclusion);
        Assert.assertEquals("group", exclusion.getGroupId());
        Assert.assertEquals("artifact", exclusion.getArtifactId());
        Assert.assertEquals("site", exclusion.getClassifier());
        Assert.assertEquals("jar", exclusion.getExtension());
    }

    @Test
    public void artifactToString() throws Exception {
        Artifact testArtifact = new DefaultArtifact("org.apache.storm:storm-core:1.0.0");
        String ret = AetherUtils.artifactToString(testArtifact);
        Assert.assertEquals("org.apache.storm:storm-core:jar:1.0.0", ret);
    }
}

