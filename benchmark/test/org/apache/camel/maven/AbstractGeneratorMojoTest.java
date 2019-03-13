/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.maven;


import AbstractSourceGeneratorMojo.CompileRoots.all;
import AbstractSourceGeneratorMojo.CompileRoots.none;
import AbstractSourceGeneratorMojo.CompileRoots.source;
import AbstractSourceGeneratorMojo.CompileRoots.test;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assume;
import org.junit.Test;


/**
 * Base class for Generator MOJO tests.
 */
public abstract class AbstractGeneratorMojoTest {
    protected static final String OUT_DIR = "target/generated-test-sources/camel-component";

    protected static final String COMPONENT_PACKAGE = "org.apache.camel.component.test";

    protected static final String OUT_PACKAGE = (AbstractGeneratorMojoTest.COMPONENT_PACKAGE) + ".internal";

    protected static final String PACKAGE_PATH = (AbstractGeneratorMojoTest.OUT_PACKAGE.replaceAll("\\.", "/")) + "/";

    protected static final String COMPONENT_PACKAGE_PATH = (AbstractGeneratorMojoTest.COMPONENT_PACKAGE.replaceAll("\\.", "/")) + "/";

    protected static final String COMPONENT_NAME = "Test";

    protected static final String SCHEME = "testComponent";

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddCompilationRootsByDefault() throws Exception {
        AbstractSourceGeneratorMojo mojo = createGeneratorMojo();
        Assume.assumeThat("Ignored because createGeneratorMojo is not implemented", mojo, CoreMatchers.notNullValue());
        // Differentiate target folders to simplify assertion
        mojo.generatedSrcDir = new File(AbstractGeneratorMojoTest.OUT_DIR.replace("-test-", ""));
        mojo.generatedTestDir = new File(AbstractGeneratorMojoTest.OUT_DIR);
        mojo.execute();
        assertCompileSourceRoots(mojo.project::getCompileSourceRoots, mojo.generatedSrcDir);
        assertCompileSourceRoots(mojo.project::getTestCompileSourceRoots, mojo.generatedTestDir);
    }

    @Test
    public void shouldAddCompilationRootsByConfiguration() throws Exception {
        File srcDir = new File(AbstractGeneratorMojoTest.OUT_DIR.replace("-test-", ""));
        File testDir = new File(AbstractGeneratorMojoTest.OUT_DIR);
        File[] empty = new File[0];
        assertCompilationRootsByConfiguration(source, srcDir, testDir, new File[]{ srcDir, testDir }, empty);
        assertCompilationRootsByConfiguration(test, srcDir, testDir, empty, new File[]{ srcDir, testDir });
        assertCompilationRootsByConfiguration(all, srcDir, testDir, new File[]{ srcDir }, new File[]{ testDir });
        assertCompilationRootsByConfiguration(none, srcDir, testDir, empty, empty);
    }
}

