/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config.materials.perforce;


import JobResult.Passed;
import com.thoughtworks.go.buildsession.BuildSessionBasedTestCase;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.domain.materials.perforce.P4Client;
import com.thoughtworks.go.domain.materials.perforce.P4Fixture;
import com.thoughtworks.go.helper.P4TestRepo;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class P4MaterialUpdaterTestBase extends BuildSessionBasedTestCase {
    protected File workingDir;

    protected P4TestRepo repo;

    protected P4Fixture p4Fixture;

    protected P4Client p4;

    protected final StringRevision REVISION_1 = new StringRevision("1");

    protected final StringRevision REVISION_2 = new StringRevision("2");

    protected final StringRevision REVISION_3 = new StringRevision("3");

    protected static final String VIEW = "//depot/... //something/...";

    @Test
    public void shouldCleanWorkingDir() throws Exception {
        P4Material material = p4Fixture.material(P4MaterialUpdaterTestBase.VIEW);
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(REVISION_2), Passed);
        File tmpFile = new File(workingDir, "shouldBeDeleted");
        FileUtils.writeStringToFile(tmpFile, "testing", StandardCharsets.UTF_8);
        assert tmpFile.exists();
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(REVISION_2), Passed);
        assert !(tmpFile.exists());
    }

    @Test
    public void shouldSyncToSpecifiedRevision() throws Exception {
        P4Material material = p4Fixture.material(P4MaterialUpdaterTestBase.VIEW);
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(REVISION_2), Passed);
        Assert.assertThat(workingDir.listFiles().length, Matchers.is(7));
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(REVISION_3), Passed);
        Assert.assertThat(workingDir.listFiles().length, Matchers.is(6));
    }

    @Test
    public void shouldNotFailIfDestDoesNotExist() throws Exception {
        FileUtils.deleteDirectory(workingDir);
        assert !(workingDir.exists());
        P4Material material = p4Fixture.material(P4MaterialUpdaterTestBase.VIEW);
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(REVISION_2), Passed);
        assert workingDir.exists();
    }

    @Test
    public void shouldSupportCustomDestinations() throws Exception {
        P4Material material = p4Fixture.material(P4MaterialUpdaterTestBase.VIEW);
        material.setFolder("dest");
        updateTo(material, new com.thoughtworks.go.domain.materials.RevisionContext(REVISION_2), Passed);
        Assert.assertThat(workingDir.listFiles().length, Matchers.is(1));
        Assert.assertThat(new File(workingDir, "dest").listFiles().length, Matchers.is(7));
    }
}

