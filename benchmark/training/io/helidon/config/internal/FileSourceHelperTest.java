/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config.internal;


import io.helidon.config.test.infra.TemporaryFolderExt;
import java.io.File;
import java.nio.file.Files;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;


/**
 * Tests {@link FileSourceHelper}.
 */
public class FileSourceHelperTest {
    @RegisterExtension
    static TemporaryFolderExt folder = TemporaryFolderExt.build();

    @Test
    public void testDigestSameContent() throws Exception {
        File file1 = FileSourceHelperTest.folder.newFile("test1");
        File file2 = FileSourceHelperTest.folder.newFile("test2");
        Files.write(file1.toPath(), "test file".getBytes());
        Files.write(file2.toPath(), "test file".getBytes());
        MatcherAssert.assertThat(FileSourceHelper.digest(file1.toPath()), CoreMatchers.equalTo(FileSourceHelper.digest(file2.toPath())));
    }

    @Test
    public void testDigestDifferentContent() throws Exception {
        File file1 = FileSourceHelperTest.folder.newFile("test1");
        File file2 = FileSourceHelperTest.folder.newFile("test2");
        Files.write(file1.toPath(), "test file1".getBytes());
        Files.write(file2.toPath(), "test file2".getBytes());
        MatcherAssert.assertThat(FileSourceHelper.digest(file1.toPath()), Matchers.not(CoreMatchers.equalTo(FileSourceHelper.digest(file2.toPath()))));
    }
}

