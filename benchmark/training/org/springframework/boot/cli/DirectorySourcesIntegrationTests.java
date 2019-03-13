/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.cli;


import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests for code in directories.
 *
 * @author Dave Syer
 */
public class DirectorySourcesIntegrationTests {
    @Rule
    public CliTester cli = new CliTester("src/test/resources/dir-sample/");

    @Test
    public void runDirectory() throws Exception {
        assertThat(this.cli.run("code")).contains("Hello World");
    }

    @Test
    public void runDirectoryRecursive() throws Exception {
        assertThat(this.cli.run("")).contains("Hello World");
    }

    @Test
    public void runPathPattern() throws Exception {
        assertThat(this.cli.run("**/*.groovy")).contains("Hello World");
    }
}

