/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.maven;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class MatchPatternsFileFilterTest {
    File basedir;

    MatchPatternsFileFilter fileFilter;

    @SuppressWarnings("unchecked")
    @Test
    public void shouldIncludeAllIfEmpty() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = basedir.listFiles(fileFilter);
        MatcherAssert.assertThat("all of the files were found.", Arrays.asList(files), CoreMatchers.hasItems(CoreMatchers.equalTo(file("sub1")), CoreMatchers.equalTo(file("excluded")), CoreMatchers.equalTo(file("example.json")), CoreMatchers.equalTo(file("README.md"))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldIncludeMatchesAndDirectoriesWhenIncluding() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addIncludes(Arrays.asList("**/*.json")).withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = basedir.listFiles(fileFilter);
        MatcherAssert.assertThat("all of the files were found.", Arrays.asList(files), CoreMatchers.hasItems(CoreMatchers.equalTo(file("sub1")), CoreMatchers.equalTo(file("excluded")), CoreMatchers.equalTo(file("example.json"))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldIncludeMatchesAndDirectoriesWhenIncludingAndDefaultExcludes() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addIncludes(Arrays.asList("**/*.json")).addDefaultExcludes().withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = basedir.listFiles(fileFilter);
        MatcherAssert.assertThat("all of the files were found.", Arrays.asList(files), CoreMatchers.hasItems(CoreMatchers.equalTo(file("sub1")), CoreMatchers.equalTo(file("excluded")), CoreMatchers.equalTo(file("example.json"))));
    }

    @Test
    public void shouldNoIncludedUnmatchedFiles() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addIncludes(Arrays.asList("**/*.json")).withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = basedir.listFiles(fileFilter);
        MatcherAssert.assertThat("the markdown file was not found.", Arrays.asList(files), CoreMatchers.not(CoreMatchers.hasItem(file("README.md"))));
    }

    @Test
    public void shouldNoIncludedNestedUnmatchedFiles() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addIncludes(Arrays.asList("**/*.json")).withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = new File(basedir, "sub1").listFiles(fileFilter);
        MatcherAssert.assertThat("the markdown file was not found.", Arrays.asList(files), CoreMatchers.not(CoreMatchers.hasItem(file("README.md"))));
    }

    @Test
    public void shouldExcludeNested() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addExcludes(Arrays.asList("**/*.md")).withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = new File(basedir, "sub1").listFiles(fileFilter);
        MatcherAssert.assertThat("the markdown file was not found.", Arrays.asList(files), CoreMatchers.not(CoreMatchers.hasItem(file("README.md"))));
    }

    @Test
    public void shouldExcludeDirectories() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addExcludes(Arrays.asList("**/excluded/**")).withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = basedir.listFiles(fileFilter);
        MatcherAssert.assertThat("the markdown file was not found.", Arrays.asList(files), CoreMatchers.not(CoreMatchers.hasItem(file("excluded"))));
    }

    @Test
    public void ahouldNotExcludeRegularDirectoriesWithDefaultExcludes() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addDefaultExcludes().addIncludes(Arrays.asList("**")).withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = basedir.listFiles(fileFilter);
        MatcherAssert.assertThat("the sub directory was not found.", Arrays.asList(files), CoreMatchers.hasItem(file("excluded")));
    }

    @Test
    public void shouldExcludeSvnDirectoriesWithDefaultExcludes() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addDefaultExcludes().addIncludes(Arrays.asList("**")).withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = basedir.listFiles(fileFilter);
        MatcherAssert.assertThat("the files in .svn directory were execluded.", Arrays.asList(files), CoreMatchers.not(CoreMatchers.hasItems(file(".svn"))));
    }

    @Test
    public void shouldExcludeFilesInSvnDirectoriesWithDefaultExcludes() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addDefaultExcludes().addIncludes(Arrays.asList("**/*.json")).withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = new File(basedir, ".svn").listFiles(fileFilter);
        MatcherAssert.assertThat("the files in .svn directory were execluded.", Arrays.asList(files), CoreMatchers.not(CoreMatchers.hasItems(file("svn-file.json"))));
    }

    @Test
    public void shouldExcludeNestedFilesInSvnDirectoriesWithDefaultExcludes() throws IOException {
        fileFilter = new MatchPatternsFileFilter.Builder().addDefaultExcludes().addIncludes(Arrays.asList("**/*.json")).withSourceDirectory(basedir.getCanonicalPath()).build();
        File[] files = new File(basedir, ".svn/sub").listFiles(fileFilter);
        MatcherAssert.assertThat("the files in .svn directory were execluded.", Arrays.asList(files), CoreMatchers.not(CoreMatchers.hasItems(file("sub-svn-file.json"))));
    }
}

