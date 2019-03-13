/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.web.context;


import java.io.File;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.util.StringUtils;


/**
 * Tests {@link WebServerPortFileWriter}.
 *
 * @author David Liu
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class WebServerPortFileWriterTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void createPortFile() throws Exception {
        File file = this.temporaryFolder.newFile();
        WebServerPortFileWriter listener = new WebServerPortFileWriter(file);
        listener.onApplicationEvent(mockEvent("", 8080));
        assertThat(contentOf(file)).isEqualTo("8080");
    }

    @Test
    public void overridePortFileWithDefault() throws Exception {
        System.setProperty("PORTFILE", this.temporaryFolder.newFile().getAbsolutePath());
        WebServerPortFileWriter listener = new WebServerPortFileWriter();
        listener.onApplicationEvent(mockEvent("", 8080));
        String content = contentOf(new File(System.getProperty("PORTFILE")));
        assertThat(content).isEqualTo("8080");
    }

    @Test
    public void overridePortFileWithExplicitFile() throws Exception {
        File file = this.temporaryFolder.newFile();
        System.setProperty("PORTFILE", this.temporaryFolder.newFile().getAbsolutePath());
        WebServerPortFileWriter listener = new WebServerPortFileWriter(file);
        listener.onApplicationEvent(mockEvent("", 8080));
        String content = contentOf(new File(System.getProperty("PORTFILE")));
        assertThat(content).isEqualTo("8080");
    }

    @Test
    public void createManagementPortFile() throws Exception {
        File file = this.temporaryFolder.newFile();
        WebServerPortFileWriter listener = new WebServerPortFileWriter(file);
        listener.onApplicationEvent(mockEvent("", 8080));
        listener.onApplicationEvent(mockEvent("management", 9090));
        assertThat(contentOf(file)).isEqualTo("8080");
        String managementFile = file.getName();
        managementFile = managementFile.substring(0, (((managementFile.length()) - (StringUtils.getFilenameExtension(managementFile).length())) - 1));
        managementFile = (managementFile + "-management.") + (StringUtils.getFilenameExtension(file.getName()));
        String content = contentOf(new File(file.getParentFile(), managementFile));
        assertThat(content).isEqualTo("9090");
        assertThat(collectFileNames(file.getParentFile())).contains(managementFile);
    }

    @Test
    public void createUpperCaseManagementPortFile() throws Exception {
        File file = this.temporaryFolder.newFile();
        file = new File(file.getParentFile(), file.getName().toUpperCase(Locale.ENGLISH));
        WebServerPortFileWriter listener = new WebServerPortFileWriter(file);
        listener.onApplicationEvent(mockEvent("management", 9090));
        String managementFile = file.getName();
        managementFile = managementFile.substring(0, (((managementFile.length()) - (StringUtils.getFilenameExtension(managementFile).length())) - 1));
        managementFile = (managementFile + "-MANAGEMENT.") + (StringUtils.getFilenameExtension(file.getName()));
        String content = contentOf(new File(file.getParentFile(), managementFile));
        assertThat(content).isEqualTo("9090");
        assertThat(collectFileNames(file.getParentFile())).contains(managementFile);
    }
}

