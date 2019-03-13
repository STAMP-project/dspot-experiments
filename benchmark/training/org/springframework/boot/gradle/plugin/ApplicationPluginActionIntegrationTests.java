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
package org.springframework.boot.gradle.plugin;


import TaskOutcome.SUCCESS;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.gradle.junit.GradleCompatibilitySuite;
import org.springframework.boot.gradle.testkit.GradleBuild;


/**
 * Integration tests for {@link ApplicationPluginAction}.
 *
 * @author Andy Wilkinson
 */
@RunWith(GradleCompatibilitySuite.class)
public class ApplicationPluginActionIntegrationTests {
    @Rule
    public GradleBuild gradleBuild;

    @Test
    public void noBootDistributionWithoutApplicationPluginApplied() {
        assertThat(this.gradleBuild.build("distributionExists", "-PdistributionName=boot").getOutput()).contains("boot exists = false");
    }

    @Test
    public void applyingApplicationPluginCreatesBootDistribution() {
        assertThat(this.gradleBuild.build("distributionExists", "-PdistributionName=boot", "-PapplyApplicationPlugin").getOutput()).contains("boot exists = true");
    }

    @Test
    public void noBootStartScriptsTaskWithoutApplicationPluginApplied() {
        assertThat(this.gradleBuild.build("taskExists", "-PtaskName=bootStartScripts").getOutput()).contains("bootStartScripts exists = false");
    }

    @Test
    public void applyingApplicationPluginCreatesBootStartScriptsTask() {
        assertThat(this.gradleBuild.build("taskExists", "-PtaskName=bootStartScripts", "-PapplyApplicationPlugin").getOutput()).contains("bootStartScripts exists = true");
    }

    @Test
    public void createsBootStartScriptsTaskUsesApplicationPluginsDefaultJvmOpts() {
        assertThat(this.gradleBuild.build("startScriptsDefaultJvmOpts", "-PapplyApplicationPlugin").getOutput()).contains("bootStartScripts defaultJvmOpts = [-Dcom.example.a=alpha, -Dcom.example.b=bravo]");
    }

    @Test
    public void zipDistributionForJarCanBeBuilt() throws IOException {
        assertThat(this.gradleBuild.build("bootDistZip").task(":bootDistZip").getOutcome()).isEqualTo(SUCCESS);
        String name = this.gradleBuild.getProjectDir().getName();
        File distribution = new File(this.gradleBuild.getProjectDir(), (("build/distributions/" + name) + "-boot.zip"));
        assertThat(distribution).isFile();
        assertThat(zipEntryNames(distribution)).containsExactlyInAnyOrder((name + "-boot/"), (name + "-boot/lib/"), (((name + "-boot/lib/") + name) + ".jar"), (name + "-boot/bin/"), ((name + "-boot/bin/") + name), (((name + "-boot/bin/") + name) + ".bat"));
    }

    @Test
    public void tarDistributionForJarCanBeBuilt() throws IOException {
        assertThat(this.gradleBuild.build("bootDistTar").task(":bootDistTar").getOutcome()).isEqualTo(SUCCESS);
        String name = this.gradleBuild.getProjectDir().getName();
        File distribution = new File(this.gradleBuild.getProjectDir(), (("build/distributions/" + name) + "-boot.tar"));
        assertThat(distribution).isFile();
        assertThat(tarEntryNames(distribution)).containsExactlyInAnyOrder((name + "-boot/"), (name + "-boot/lib/"), (((name + "-boot/lib/") + name) + ".jar"), (name + "-boot/bin/"), ((name + "-boot/bin/") + name), (((name + "-boot/bin/") + name) + ".bat"));
    }

    @Test
    public void zipDistributionForWarCanBeBuilt() throws IOException {
        assertThat(this.gradleBuild.build("bootDistZip").task(":bootDistZip").getOutcome()).isEqualTo(SUCCESS);
        String name = this.gradleBuild.getProjectDir().getName();
        File distribution = new File(this.gradleBuild.getProjectDir(), (("build/distributions/" + name) + "-boot.zip"));
        assertThat(distribution).isFile();
        assertThat(zipEntryNames(distribution)).containsExactlyInAnyOrder((name + "-boot/"), (name + "-boot/lib/"), (((name + "-boot/lib/") + name) + ".war"), (name + "-boot/bin/"), ((name + "-boot/bin/") + name), (((name + "-boot/bin/") + name) + ".bat"));
    }

    @Test
    public void tarDistributionForWarCanBeBuilt() throws IOException {
        assertThat(this.gradleBuild.build("bootDistTar").task(":bootDistTar").getOutcome()).isEqualTo(SUCCESS);
        String name = this.gradleBuild.getProjectDir().getName();
        File distribution = new File(this.gradleBuild.getProjectDir(), (("build/distributions/" + name) + "-boot.tar"));
        assertThat(distribution).isFile();
        assertThat(tarEntryNames(distribution)).containsExactlyInAnyOrder((name + "-boot/"), (name + "-boot/lib/"), (((name + "-boot/lib/") + name) + ".war"), (name + "-boot/bin/"), ((name + "-boot/bin/") + name), (((name + "-boot/bin/") + name) + ".bat"));
    }

    @Test
    public void applicationNameCanBeUsedToCustomizeDistributionName() throws IOException {
        assertThat(this.gradleBuild.build("bootDistTar").task(":bootDistTar").getOutcome()).isEqualTo(SUCCESS);
        File distribution = new File(this.gradleBuild.getProjectDir(), "build/distributions/custom-boot.tar");
        assertThat(distribution).isFile();
        String name = this.gradleBuild.getProjectDir().getName();
        assertThat(tarEntryNames(distribution)).containsExactlyInAnyOrder("custom-boot/", "custom-boot/lib/", (("custom-boot/lib/" + name) + ".jar"), "custom-boot/bin/", "custom-boot/bin/custom", "custom-boot/bin/custom.bat");
    }

    @Test
    public void scriptsHaveCorrectPermissions() throws IOException {
        assertThat(this.gradleBuild.build("bootDistTar").task(":bootDistTar").getOutcome()).isEqualTo(SUCCESS);
        String name = this.gradleBuild.getProjectDir().getName();
        File distribution = new File(this.gradleBuild.getProjectDir(), (("build/distributions/" + name) + "-boot.tar"));
        assertThat(distribution).isFile();
        tarEntries(distribution, ( entry) -> {
            int filePermissions = (entry.getMode()) & 511;
            if ((entry.isFile()) && (!(entry.getName().startsWith((name + "-boot/bin/"))))) {
                assertThat(filePermissions).isEqualTo(420);
            } else {
                assertThat(filePermissions).isEqualTo(493);
            }
        });
    }
}

