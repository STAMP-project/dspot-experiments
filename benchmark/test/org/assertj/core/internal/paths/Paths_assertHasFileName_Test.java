/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.internal.paths;


import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class Paths_assertHasFileName_Test extends PathsBaseTest {
    public static PathsBaseTest.FileSystemResource resource;

    private static Path existingFile;

    private static Path symlinkToExistingFile;

    private static Path nonExistingPath;

    private static Path symlinkToNonExistingPath;

    private static Path existingDirectory;

    private static Path symlinkToExistingDirectory;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertHasFileName(info, null, "file.txt")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_given_file_name_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertHasFileName(info, Paths_assertHasFileName_Test.existingFile, null)).withMessage("expected fileName should not be null");
    }

    @Test
    public void should_pass_if_actual_file_has_the_given_file_name() {
        paths.assertHasFileName(info, Paths_assertHasFileName_Test.existingFile, "gc.log");
    }

    @Test
    public void should_pass_if_actual_non_existent_path_has_the_given_file_name() {
        paths.assertHasFileName(info, Paths_assertHasFileName_Test.nonExistingPath, "fake.log");
    }

    @Test
    public void should_pass_if_actual_symbolic_link_has_the_given_file_name() {
        paths.assertHasFileName(info, Paths_assertHasFileName_Test.symlinkToNonExistingPath, "bad-symlink");
        paths.assertHasFileName(info, Paths_assertHasFileName_Test.symlinkToExistingFile, "good-symlink");
    }

    @Test
    public void should_pass_if_actual_directory_has_the_given_file_name() {
        paths.assertHasFileName(info, Paths_assertHasFileName_Test.existingDirectory, "dir2");
    }
}

