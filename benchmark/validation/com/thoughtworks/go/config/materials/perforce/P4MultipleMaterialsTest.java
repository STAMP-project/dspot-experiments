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


import com.thoughtworks.go.config.MaterialRevisionsMatchers;
import com.thoughtworks.go.config.materials.Materials;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.domain.MaterialRevisions;
import com.thoughtworks.go.domain.materials.TestSubprocessExecutionContext;
import com.thoughtworks.go.domain.materials.perforce.P4Client;
import com.thoughtworks.go.domain.materials.perforce.P4Fixture;
import com.thoughtworks.go.helper.P4TestRepo;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class P4MultipleMaterialsTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected P4Client p4;

    protected File clientFolder;

    public static final String DEFAULT_CLIENT_NAME = "p4test_2";

    protected P4Fixture p4Fixture;

    private static final String VIEW_SRC = "//depot/src/... //something/...";

    private static final String VIEW_LIB = "//depot/lib/... //something/...";

    P4TestRepo p4TestRepo;

    @Test
    public void shouldUpdateToItsDestFolder() {
        P4Material p4Material = p4Fixture.material(P4MultipleMaterialsTest.VIEW_SRC, "dest1");
        MaterialRevision revision = new MaterialRevision(p4Material, p4Material.latestModification(clientFolder, new TestSubprocessExecutionContext()));
        revision.updateTo(clientFolder, ProcessOutputStreamConsumer.inMemoryConsumer(), new TestSubprocessExecutionContext());
        Assert.assertThat(new File(clientFolder, "dest1/net").exists(), Matchers.is(true));
    }

    @Test
    public void shouldIgnoreDestinationFolderWhenUpdateToOnServerSide() {
        P4Material p4Material = p4Fixture.material(P4MultipleMaterialsTest.VIEW_SRC, "dest1");
        MaterialRevision revision = new MaterialRevision(p4Material, p4Material.latestModification(clientFolder, new TestSubprocessExecutionContext()));
        revision.updateTo(clientFolder, ProcessOutputStreamConsumer.inMemoryConsumer(), new TestSubprocessExecutionContext(true));
        Assert.assertThat(new File(clientFolder, "dest1/net").exists(), Matchers.is(false));
        Assert.assertThat(new File(clientFolder, "net").exists(), Matchers.is(true));
    }

    @Test
    public void shouldFoundModificationsForEachMaterial() throws Exception {
        P4Material p4Material1 = p4Fixture.material(P4MultipleMaterialsTest.VIEW_SRC, "src");
        P4Material p4Material2 = p4Fixture.material(P4MultipleMaterialsTest.VIEW_LIB, "lib");
        Materials materials = new Materials(p4Material1, p4Material2);
        p4TestRepo.checkInOneFile(p4Material1, "filename.txt");
        p4TestRepo.checkInOneFile(p4Material2, "filename2.txt");
        MaterialRevisions materialRevisions = materials.latestModification(clientFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(materialRevisions.getRevisions().size(), Matchers.is(2));
        Assert.assertThat(materialRevisions, MaterialRevisionsMatchers.containsModifiedFile("src/filename.txt"));
        Assert.assertThat(materialRevisions, MaterialRevisionsMatchers.containsModifiedFile("lib/filename2.txt"));
    }
}

