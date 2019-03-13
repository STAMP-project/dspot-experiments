/**
 * Copyright 2014 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.maven.docker.assembly;


import AssemblyFiles.Entry;
import java.io.File;
import java.util.List;
import mockit.Injectable;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.commons.io.FileUtils.touch;


/**
 *
 *
 * @author roland
 * @since 02/07/15
 */
public class MappingTrackArchiverTest {
    @Injectable
    private MavenSession session;

    private MappingTrackArchiver archiver;

    @Test(expected = IllegalArgumentException.class)
    public void noDirectory() throws Exception {
        archiver.setDestFile(new File("."));
        archiver.addDirectory(new File(System.getProperty("user.home")), "tmp");
        AssemblyFiles files = archiver.getAssemblyFiles(session);
    }

    @Test
    public void simple() throws Exception {
        archiver.setDestFile(new File("target/test-data/maven.tracker"));
        new File(archiver.getDestFile(), "maven").mkdirs();
        File tempFile = File.createTempFile("tracker", "txt");
        File destination = new File("target/test-data/maven/test.txt");
        FileUtils.copyFile(tempFile, destination);
        archiver.addFile(tempFile, "test.txt");
        AssemblyFiles files = archiver.getAssemblyFiles(session);
        Assert.assertNotNull(files);
        List<AssemblyFiles.Entry> entries = files.getUpdatedEntriesAndRefresh();
        Assert.assertEquals(0, entries.size());
        Thread.sleep(1000);
        touch(tempFile);
        entries = files.getUpdatedEntriesAndRefresh();
        Assert.assertEquals(1, entries.size());
        AssemblyFiles.Entry entry = entries.get(0);
        Assert.assertEquals(tempFile, entry.getSrcFile());
        Assert.assertEquals(destination, entry.getDestFile());
    }
}

