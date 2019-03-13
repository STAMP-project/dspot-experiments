/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja;


import java.io.File;
import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class AssetsControllerHelperTest {
    AssetsControllerHelper assetsControllerHelper;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testNormalizePathWithoutLeadingSlash() {
        Assert.assertEquals("dir1/test.test", assetsControllerHelper.normalizePathWithoutLeadingSlash("/dir1/test.test", true));
        Assert.assertEquals("dir1/test.test", assetsControllerHelper.normalizePathWithoutLeadingSlash("dir1/test.test", true));
        Assert.assertEquals(null, assetsControllerHelper.normalizePathWithoutLeadingSlash("/../test.test", true));
        Assert.assertEquals(null, assetsControllerHelper.normalizePathWithoutLeadingSlash("../test.test", true));
        Assert.assertEquals("dir2/file.test", assetsControllerHelper.normalizePathWithoutLeadingSlash("/dir1/../dir2/file.test", true));
        Assert.assertEquals(null, assetsControllerHelper.normalizePathWithoutLeadingSlash(null, true));
        Assert.assertEquals("", assetsControllerHelper.normalizePathWithoutLeadingSlash("", true));
    }

    @Test
    public void testIsDirectoryURLWithJarProtocol() throws Exception {
        boolean result = assetsControllerHelper.isDirectoryURL(new URL("jar:file:/home/ninja/ninja.jar!/"));
        MatcherAssert.assertThat(result, CoreMatchers.is(false));
    }

    @Test
    public void testIsDirectoryURLWithFile() throws Exception {
        boolean result = assetsControllerHelper.isDirectoryURL(this.getClass().getResource("/assets/testasset.txt"));
        MatcherAssert.assertThat(result, CoreMatchers.is(false));
    }

    @Test
    public void testIsDirectoryURLWithDirectory() throws Exception {
        boolean result = assetsControllerHelper.isDirectoryURL(this.getClass().getResource("/assets/assets/"));
        MatcherAssert.assertThat(result, CoreMatchers.is(true));
    }

    @Test
    public void testIsDirectoryURLWithDirectoryContainsSpecialCharacters() throws Exception {
        File dir = tempFolder.newFolder("a#b");
        boolean result = assetsControllerHelper.isDirectoryURL(dir.toURI().toURL());
        MatcherAssert.assertThat(result, CoreMatchers.is(true));
    }
}

