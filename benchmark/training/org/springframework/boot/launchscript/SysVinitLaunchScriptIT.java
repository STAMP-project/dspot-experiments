/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.launchscript;


import AnsiColor.GREEN;
import AnsiColor.RED;
import AnsiColor.YELLOW;
import com.github.dockerjava.api.command.DockerCmd;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.util.CompressArchiveUtil;
import com.github.dockerjava.jaxrs.AbstrSyncDockerCmdExec;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Integration tests for Spring Boot's launch script on OSs that use SysVinit.
 *
 * @author Andy Wilkinson
 * @author Ali Shahbour
 */
@RunWith(Parameterized.class)
public class SysVinitLaunchScriptIT {
    private final SysVinitLaunchScriptIT.SpringBootDockerCmdExecFactory commandExecFactory = new SysVinitLaunchScriptIT.SpringBootDockerCmdExecFactory();

    private static final char ESC = 27;

    private final String os;

    private final String version;

    public SysVinitLaunchScriptIT(String os, String version) {
        this.os = os;
        this.version = version;
    }

    @Test
    public void statusWhenStopped() throws Exception {
        String output = doTest("status-when-stopped.sh");
        assertThat(output).contains("Status: 3");
        assertThat(output).has(coloredString(RED, "Not running"));
    }

    @Test
    public void statusWhenStarted() throws Exception {
        String output = doTest("status-when-started.sh");
        assertThat(output).contains("Status: 0");
        assertThat(output).has(coloredString(GREEN, (("Started [" + (extractPid(output))) + "]")));
    }

    @Test
    public void statusWhenKilled() throws Exception {
        String output = doTest("status-when-killed.sh");
        assertThat(output).contains("Status: 1");
        assertThat(output).has(coloredString(RED, (("Not running (process " + (extractPid(output))) + " not found)")));
    }

    @Test
    public void stopWhenStopped() throws Exception {
        String output = doTest("stop-when-stopped.sh");
        assertThat(output).contains("Status: 0");
        assertThat(output).has(coloredString(YELLOW, "Not running (pidfile not found)"));
    }

    @Test
    public void forceStopWhenStopped() throws Exception {
        String output = doTest("force-stop-when-stopped.sh");
        assertThat(output).contains("Status: 0");
        assertThat(output).has(coloredString(YELLOW, "Not running (pidfile not found)"));
    }

    @Test
    public void startWhenStarted() throws Exception {
        String output = doTest("start-when-started.sh");
        assertThat(output).contains("Status: 0");
        assertThat(output).has(coloredString(YELLOW, (("Already running [" + (extractPid(output))) + "]")));
    }

    @Test
    public void restartWhenStopped() throws Exception {
        String output = doTest("restart-when-stopped.sh");
        assertThat(output).contains("Status: 0");
        assertThat(output).has(coloredString(YELLOW, "Not running (pidfile not found)"));
        assertThat(output).has(coloredString(GREEN, (("Started [" + (extractPid(output))) + "]")));
    }

    @Test
    public void restartWhenStarted() throws Exception {
        String output = doTest("restart-when-started.sh");
        assertThat(output).contains("Status: 0");
        assertThat(output).has(coloredString(GREEN, (("Started [" + (extract("PID1", output))) + "]")));
        assertThat(output).has(coloredString(GREEN, (("Stopped [" + (extract("PID1", output))) + "]")));
        assertThat(output).has(coloredString(GREEN, (("Started [" + (extract("PID2", output))) + "]")));
    }

    @Test
    public void startWhenStopped() throws Exception {
        String output = doTest("start-when-stopped.sh");
        assertThat(output).contains("Status: 0");
        assertThat(output).has(coloredString(GREEN, (("Started [" + (extractPid(output))) + "]")));
    }

    @Test
    public void basicLaunch() throws Exception {
        String output = doTest("basic-launch.sh");
        assertThat(output).doesNotContain("PID_FOLDER");
    }

    @Test
    public void launchWithMissingLogFolderGeneratesAWarning() throws Exception {
        String output = doTest("launch-with-missing-log-folder.sh");
        assertThat(output).has(coloredString(YELLOW, "LOG_FOLDER /does/not/exist does not exist. Falling back to /tmp"));
    }

    @Test
    public void launchWithMissingPidFolderGeneratesAWarning() throws Exception {
        String output = doTest("launch-with-missing-pid-folder.sh");
        assertThat(output).has(coloredString(YELLOW, "PID_FOLDER /does/not/exist does not exist. Falling back to /tmp"));
    }

    @Test
    public void launchWithSingleCommandLineArgument() throws Exception {
        doLaunch("launch-with-single-command-line-argument.sh");
    }

    @Test
    public void launchWithMultipleCommandLineArguments() throws Exception {
        doLaunch("launch-with-multiple-command-line-arguments.sh");
    }

    @Test
    public void launchWithSingleRunArg() throws Exception {
        doLaunch("launch-with-single-run-arg.sh");
    }

    @Test
    public void launchWithMultipleRunArgs() throws Exception {
        doLaunch("launch-with-multiple-run-args.sh");
    }

    @Test
    public void launchWithSingleJavaOpt() throws Exception {
        doLaunch("launch-with-single-java-opt.sh");
    }

    @Test
    public void launchWithDoubleLinkSingleJavaOpt() throws Exception {
        doLaunch("launch-with-double-link-single-java-opt.sh");
    }

    @Test
    public void launchWithMultipleJavaOpts() throws Exception {
        doLaunch("launch-with-multiple-java-opts.sh");
    }

    @Test
    public void launchWithUseOfStartStopDaemonDisabled() throws Exception {
        // CentOS doesn't have start-stop-daemon
        Assume.assumeThat(this.os, Matchers.is(Matchers.not("CentOS")));
        doLaunch("launch-with-use-of-start-stop-daemon-disabled.sh");
    }

    @Test
    public void launchWithRelativePidFolder() throws Exception {
        String output = doTest("launch-with-relative-pid-folder.sh");
        assertThat(output).has(coloredString(GREEN, (("Started [" + (extractPid(output))) + "]")));
        assertThat(output).has(coloredString(GREEN, (("Running [" + (extractPid(output))) + "]")));
        assertThat(output).has(coloredString(GREEN, (("Stopped [" + (extractPid(output))) + "]")));
    }

    @Test
    public void pidFolderOwnership() throws Exception {
        String output = doTest("pid-folder-ownership.sh");
        assertThat(output).contains("phil root");
    }

    @Test
    public void pidFileOwnership() throws Exception {
        String output = doTest("pid-file-ownership.sh");
        assertThat(output).contains("phil root");
    }

    @Test
    public void logFileOwnership() throws Exception {
        String output = doTest("log-file-ownership.sh");
        assertThat(output).contains("phil root");
    }

    @Test
    public void logFileOwnershipIsChangedWhenCreated() throws Exception {
        String output = doTest("log-file-ownership-is-changed-when-created.sh");
        assertThat(output).contains("andy root");
    }

    @Test
    public void logFileOwnershipIsUnchangedWhenExists() throws Exception {
        String output = doTest("log-file-ownership-is-unchanged-when-exists.sh");
        assertThat(output).contains("root root");
    }

    @Test
    public void launchWithRelativeLogFolder() throws Exception {
        String output = doTest("launch-with-relative-log-folder.sh");
        assertThat(output).contains("Log written");
    }

    private static final class CopyToContainerCmdExec extends AbstrSyncDockerCmdExec<SysVinitLaunchScriptIT.CopyToContainerCmd, Void> {
        private CopyToContainerCmdExec(WebTarget baseResource, DockerClientConfig dockerClientConfig) {
            super(baseResource, dockerClientConfig);
        }

        @Override
        protected Void execute(SysVinitLaunchScriptIT.CopyToContainerCmd command) {
            try (InputStream streamToUpload = new FileInputStream(CompressArchiveUtil.archiveTARFiles(command.getFile().getParentFile(), Arrays.asList(command.getFile()), command.getFile().getName()))) {
                WebTarget webResource = getBaseResource().path("/containers/{id}/archive").resolveTemplate("id", command.getContainer());
                webResource.queryParam("path", ".").queryParam("noOverwriteDirNonDir", false).request().put(Entity.entity(streamToUpload, "application/x-tar")).close();
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static final class CopyToContainerCmd implements DockerCmd<Void> {
        private final String container;

        private final File file;

        private CopyToContainerCmd(String container, File file) {
            this.container = container;
            this.file = file;
        }

        public String getContainer() {
            return this.container;
        }

        public File getFile() {
            return this.file;
        }

        @Override
        public void close() {
        }
    }

    private static final class SpringBootDockerCmdExecFactory extends JerseyDockerCmdExecFactory {
        private SysVinitLaunchScriptIT.CopyToContainerCmdExec createCopyToContainerCmdExec() {
            return new SysVinitLaunchScriptIT.CopyToContainerCmdExec(getBaseResource(), getDockerClientConfig());
        }
    }
}

