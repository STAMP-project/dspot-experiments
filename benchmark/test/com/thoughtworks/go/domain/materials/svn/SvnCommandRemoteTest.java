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
package com.thoughtworks.go.domain.materials.svn;


import SvnCommand.SvnInfo;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.ValidationBean;
import com.thoughtworks.go.helper.SvnRemoteRepository;
import com.thoughtworks.go.util.command.InMemoryStreamConsumer;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.jdom2.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SvnCommandRemoteTest {
    public SvnRemoteRepository repository;

    private static final String HARRY = "harry";

    private static final String HARRYS_PASSWORD = "harryssecret";

    public SvnCommand command;

    public File workingDir;

    private InMemoryStreamConsumer outputStreamConsumer;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldSupportSvnInfo() throws Exception {
        SvnCommand.SvnInfo info = command.remoteInfo(new SAXBuilder());
        Assert.assertThat(info.getUrl(), Matchers.is(repository.getUrl()));
    }

    @Test
    public void shouldSupportSvnLog() throws Exception {
        List<Modification> info = command.latestModification();
        Assert.assertThat(info.get(0).getComment(), Matchers.is("Added simple build shell to dump the environment to console."));
    }

    @Test
    public void shouldSupportModificationsSince() throws Exception {
        List<Modification> info = command.modificationsSince(new SubversionRevision(2));
        Assert.assertThat(info.size(), Matchers.is(2));
        Assert.assertThat(info.get(0).getRevision(), Matchers.is("4"));
        Assert.assertThat(info.get(1).getRevision(), Matchers.is("3"));
    }

    @Test
    public void shouldSupportLocalSvnInfoWithoutPassword() throws Exception {
        command.checkoutTo(ProcessOutputStreamConsumer.inMemoryConsumer(), workingDir, new SubversionRevision(4));
        SvnCommand commandWithoutPassword = new SvnCommand(null, repository.getUrl(), null, null, true);
        SvnCommand.SvnInfo info = commandWithoutPassword.workingDirInfo(workingDir);
        Assert.assertThat(info.getUrl(), Matchers.is(repository.getUrl()));
    }

    @Test
    public void shouldMaskPassword_CheckConnection() {
        ValidationBean goodResponse = command.checkConnection();
        Assert.assertThat(goodResponse.isValid(), Matchers.is(true));
        Assert.assertThat("Plain text password detected!", goodResponse.getError().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        ValidationBean badResponse = badUserNameCommand().checkConnection();
        Assert.assertThat(badResponse.isValid(), Matchers.is(false));
        Assert.assertThat("Plain text password detected!", badResponse.getError().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        badResponse = badPasswordCommand().checkConnection();
        Assert.assertThat(badResponse.isValid(), Matchers.is(false));
        Assert.assertThat("Plain text password detected!", badResponse.getError().contains("some_bad_password"), Matchers.is(false));
        badResponse = badUrlCommand().checkConnection();
        Assert.assertThat(badResponse.isValid(), Matchers.is(false));
        Assert.assertThat("Plain text password detected!", badResponse.getError().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
    }

    @Test
    public void shouldMaskPassword_UpdateTo() {
        command.checkoutTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
        command.updateTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
        Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        try {
            badUserNameCommand().updateTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
            Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
        try {
            badPasswordCommand().updateTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains("some_bad_password"), Matchers.is(false));
            Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains("some_bad_password"), Matchers.is(false));
        }
        try {
            badUrlCommand().updateTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
            Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
    }

    @Test
    public void shouldMaskPassword_CheckoutTo() {
        command.checkoutTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
        Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        try {
            FileUtils.deleteQuietly(workingDir);
            badUserNameCommand().checkoutTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
            Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
        try {
            FileUtils.deleteQuietly(workingDir);
            badPasswordCommand().checkoutTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains("some_bad_password"), Matchers.is(false));
            Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains("some_bad_password"), Matchers.is(false));
        }
        try {
            FileUtils.deleteQuietly(workingDir);
            badUrlCommand().checkoutTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
            Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
    }

    @Test
    public void shouldMaskPassword_getAllExternalURLs() {
        try {
            badUserNameCommand().getAllExternalURLs();
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
        try {
            badPasswordCommand().getAllExternalURLs();
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains("some_bad_password"), Matchers.is(false));
        }
        try {
            badUrlCommand().getAllExternalURLs();
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
    }

    @Test
    public void shouldMaskPassword_latestModification() {
        try {
            badUserNameCommand().latestModification();
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
        try {
            badPasswordCommand().latestModification();
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains("some_bad_password"), Matchers.is(false));
        }
        try {
            badUrlCommand().latestModification();
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
    }

    @Test
    public void shouldMaskPassword_modificationsSince() {
        try {
            badUserNameCommand().latestModification();
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
        try {
            badPasswordCommand().latestModification();
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains("some_bad_password"), Matchers.is(false));
        }
        try {
            badUrlCommand().latestModification();
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
    }

    @Test
    public void shouldMaskPassword_remoteInfo() {
        try {
            badUserNameCommand().remoteInfo(new SAXBuilder());
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
        try {
            badPasswordCommand().remoteInfo(new SAXBuilder());
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains("some_bad_password"), Matchers.is(false));
        }
        try {
            badUrlCommand().remoteInfo(new SAXBuilder());
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
    }

    @Test
    public void shouldMaskPassword_workingDirInfo() {
        try {
            badUserNameCommand().workingDirInfo(workingDir);
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
        try {
            badPasswordCommand().workingDirInfo(workingDir);
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains("some_bad_password"), Matchers.is(false));
        }
        try {
            badUrlCommand().workingDirInfo(workingDir);
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
    }

    @Test
    public void shouldMaskPassword_commit() throws IOException {
        command.checkoutTo(outputStreamConsumer, workingDir, new SubversionRevision(2));
        File newFile = new File(((workingDir.getAbsolutePath()) + "/foo"));
        FileUtils.writeStringToFile(newFile, "content", StandardCharsets.UTF_8);
        command.add(outputStreamConsumer, newFile);
        try {
            badUserNameCommand().commit(outputStreamConsumer, workingDir, "message");
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
        try {
            badPasswordCommand().commit(outputStreamConsumer, workingDir, "message");
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains("some_bad_password"), Matchers.is(false));
            Assert.assertThat("Plain text password detected!", e.getMessage().contains("some_bad_password"), Matchers.is(false));
        }
        try {
            badUrlCommand().commit(outputStreamConsumer, workingDir, "message");
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", outputStreamConsumer.getAllOutput().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
    }

    @Test
    public void shouldMaskPassword_propset() throws IOException {
        try {
            badUserNameCommand().propset(workingDir, "svn:ignore", "*.foo");
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
        try {
            badPasswordCommand().propset(workingDir, "svn:ignore", "*.foo");
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains("some_bad_password"), Matchers.is(false));
        }
        try {
            badUrlCommand().propset(workingDir, "svn:ignore", "*.foo");
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertThat("Plain text password detected!", e.getMessage().contains(SvnCommandRemoteTest.HARRYS_PASSWORD), Matchers.is(false));
        }
    }
}

