/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.nar;


import BundleCoordinate.DEFAULT_GROUP;
import BundleCoordinate.DEFAULT_VERSION;
import java.io.File;
import java.io.IOException;
import org.apache.nifi.bundle.BundleDetails;
import org.junit.Assert;
import org.junit.Test;


public class NarBundleUtilTest {
    @Test
    public void testManifestWithVersioningAndBuildInfo() throws IOException {
        final File narDir = new File("src/test/resources/nars/nar-with-versioning");
        final BundleDetails narDetails = NarBundleUtil.fromNarDirectory(narDir);
        Assert.assertEquals(narDir.getPath(), narDetails.getWorkingDirectory().getPath());
        Assert.assertEquals("org.apache.nifi", narDetails.getCoordinate().getGroup());
        Assert.assertEquals("nifi-hadoop-nar", narDetails.getCoordinate().getId());
        Assert.assertEquals("1.2.0", narDetails.getCoordinate().getVersion());
        Assert.assertEquals("org.apache.nifi.hadoop", narDetails.getDependencyCoordinate().getGroup());
        Assert.assertEquals("nifi-hadoop-libraries-nar", narDetails.getDependencyCoordinate().getId());
        Assert.assertEquals("1.2.1", narDetails.getDependencyCoordinate().getVersion());
        Assert.assertEquals("NIFI-3380", narDetails.getBuildBranch());
        Assert.assertEquals("1.8.0_74", narDetails.getBuildJdk());
        Assert.assertEquals("a032175", narDetails.getBuildRevision());
        Assert.assertEquals("HEAD", narDetails.getBuildTag());
        Assert.assertEquals("2017-01-23T10:36:27Z", narDetails.getBuildTimestamp());
        Assert.assertEquals("bbende", narDetails.getBuiltBy());
    }

    @Test
    public void testManifestWithoutVersioningAndBuildInfo() throws IOException {
        final File narDir = new File("src/test/resources/nars/nar-without-versioning");
        final BundleDetails narDetails = NarBundleUtil.fromNarDirectory(narDir);
        Assert.assertEquals(narDir.getPath(), narDetails.getWorkingDirectory().getPath());
        Assert.assertEquals(DEFAULT_GROUP, narDetails.getCoordinate().getGroup());
        Assert.assertEquals("nifi-hadoop-nar", narDetails.getCoordinate().getId());
        Assert.assertEquals(DEFAULT_VERSION, narDetails.getCoordinate().getVersion());
        Assert.assertEquals(DEFAULT_GROUP, narDetails.getDependencyCoordinate().getGroup());
        Assert.assertEquals("nifi-hadoop-libraries-nar", narDetails.getDependencyCoordinate().getId());
        Assert.assertEquals(DEFAULT_VERSION, narDetails.getDependencyCoordinate().getVersion());
        Assert.assertNull(narDetails.getBuildBranch());
        Assert.assertEquals("1.8.0_74", narDetails.getBuildJdk());
        Assert.assertNull(narDetails.getBuildRevision());
        Assert.assertNull(narDetails.getBuildTag());
        Assert.assertNull(narDetails.getBuildTimestamp());
        Assert.assertEquals("bbende", narDetails.getBuiltBy());
    }

    @Test
    public void testManifestWithoutNarDependency() throws IOException {
        final File narDir = new File("src/test/resources/nars/nar-without-dependency");
        final BundleDetails narDetails = NarBundleUtil.fromNarDirectory(narDir);
        Assert.assertEquals(narDir.getPath(), narDetails.getWorkingDirectory().getPath());
        Assert.assertEquals("org.apache.nifi", narDetails.getCoordinate().getGroup());
        Assert.assertEquals("nifi-hadoop-nar", narDetails.getCoordinate().getId());
        Assert.assertEquals("1.2.0", narDetails.getCoordinate().getVersion());
        Assert.assertNull(narDetails.getDependencyCoordinate());
        Assert.assertEquals("NIFI-3380", narDetails.getBuildBranch());
        Assert.assertEquals("1.8.0_74", narDetails.getBuildJdk());
        Assert.assertEquals("a032175", narDetails.getBuildRevision());
        Assert.assertEquals("HEAD", narDetails.getBuildTag());
        Assert.assertEquals("2017-01-23T10:36:27Z", narDetails.getBuildTimestamp());
        Assert.assertEquals("bbende", narDetails.getBuiltBy());
    }

    @Test(expected = IOException.class)
    public void testFromManifestWhenNarDirectoryDoesNotExist() throws IOException {
        final File manifest = new File("src/test/resources/nars/nar-does-not-exist");
        NarBundleUtil.fromNarDirectory(manifest);
    }
}

