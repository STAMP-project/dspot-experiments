/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.dep;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonatype.aether.RepositoryException;


public class DependencyResolverTest {
    private static DependencyResolver resolver;

    private static String testPath;

    private static File testCopyPath;

    private static File tmpDir;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testAddRepo() {
        int reposCnt = DependencyResolverTest.resolver.getRepos().size();
        DependencyResolverTest.resolver.addRepo("securecentral", "https://repo1.maven.org/maven2", false);
        Assert.assertEquals((reposCnt + 1), DependencyResolverTest.resolver.getRepos().size());
    }

    @Test
    public void testDelRepo() {
        DependencyResolverTest.resolver.addRepo("securecentral", "https://repo1.maven.org/maven2", false);
        int reposCnt = DependencyResolverTest.resolver.getRepos().size();
        DependencyResolverTest.resolver.delRepo("securecentral");
        DependencyResolverTest.resolver.delRepo("badId");
        Assert.assertEquals((reposCnt - 1), DependencyResolverTest.resolver.getRepos().size());
    }

    @Test
    public void testLoad() throws Exception {
        // basic load
        DependencyResolverTest.resolver.load("com.databricks:spark-csv_2.10:1.3.0", DependencyResolverTest.testCopyPath);
        Assert.assertEquals(DependencyResolverTest.testCopyPath.list().length, 4);
        FileUtils.cleanDirectory(DependencyResolverTest.testCopyPath);
        // load with exclusions parameter
        DependencyResolverTest.resolver.load("com.databricks:spark-csv_2.10:1.3.0", Collections.singletonList("org.scala-lang:scala-library"), DependencyResolverTest.testCopyPath);
        Assert.assertEquals(DependencyResolverTest.testCopyPath.list().length, 3);
        FileUtils.cleanDirectory(DependencyResolverTest.testCopyPath);
        // load from added repository
        DependencyResolverTest.resolver.addRepo("sonatype", "https://oss.sonatype.org/content/repositories/agimatec-releases/", false);
        DependencyResolverTest.resolver.load("com.agimatec:agimatec-validation:0.9.3", DependencyResolverTest.testCopyPath);
        Assert.assertEquals(DependencyResolverTest.testCopyPath.list().length, 8);
        // load invalid artifact
        DependencyResolverTest.resolver.delRepo("sonatype");
        exception.expect(RepositoryException.class);
        DependencyResolverTest.resolver.load("com.agimatec:agimatec-validation:0.9.3", DependencyResolverTest.testCopyPath);
    }

    @Test
    public void should_throw_exception_if_dependency_not_found() throws Exception {
        expectedException.expectMessage("Source 'one.two:1.0' does not exist");
        expectedException.expect(FileNotFoundException.class);
        DependencyResolverTest.resolver.load("one.two:1.0", DependencyResolverTest.testCopyPath);
    }
}

