/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.docker.client;


import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.text.MessageFormat;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CompressedDirectoryMatchFilepathTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Parameterized.Parameter(0)
    public String pattern;

    @Parameterized.Parameter(1)
    public String pathString;

    @Parameterized.Parameter(2)
    public boolean matched;

    @Parameterized.Parameter(3)
    public Class<? extends Exception> exception;

    private FileSystem fs;

    @Test
    public void testMatchFilepath() {
        if ((exception) != null) {
            expectedException.expect(exception);
        }
        final Path path = fs.getPath(pathString);
        final boolean result = CompressedDirectory.goPathMatcher(fs, pattern).matches(path);
        final String description;
        if (matched) {
            description = MessageFormat.format("the pattern {0} to match {1}", pattern, pathString);
        } else {
            description = MessageFormat.format("the pattern {0} not to match {1}", pattern, pathString);
        }
        Assert.assertThat(result, Matchers.describedAs(description, Matchers.is(matched)));
    }
}

