/**
 * Copyright 2015 Roland Huss
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
package io.fabric8.maven.docker.util;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.interpolation.fixed.FixedStringSearchInterpolator;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 21/01/16
 */
public class DockerFileUtilTest {
    @Test
    public void testSimple() throws Exception {
        File toTest = copyToTempDir("Dockerfile_from_simple");
        Assert.assertEquals("fabric8/s2i-java", DockerFileUtil.extractBaseImage(toTest, FixedStringSearchInterpolator.create()));
    }

    @Test
    public void interpolate() throws Exception {
        MojoParameters params = mockMojoParams();
        Map<String, String> filterMapping = new HashMap<>();
        filterMapping.put("none", "false");
        filterMapping.put("var", "${*}");
        filterMapping.put("at", "@");
        for (Map.Entry<String, String> entry : filterMapping.entrySet()) {
            for (int i = 1; i < 2; i++) {
                File dockerFile = getDockerfilePath(i, entry.getKey());
                File expectedDockerFile = new File(dockerFile.getParent(), ((dockerFile.getName()) + ".expected"));
                File actualDockerFile = PathTestUtil.createTmpFile(dockerFile.getName());
                FixedStringSearchInterpolator interpolator = DockerFileUtil.createInterpolator(params, entry.getValue());
                FileUtils.write(actualDockerFile, DockerFileUtil.interpolate(dockerFile, interpolator), "UTF-8");
                // Compare text lines without regard to EOL delimiters
                Assert.assertEquals(FileUtils.readLines(expectedDockerFile), FileUtils.readLines(actualDockerFile));
            }
        }
    }
}

