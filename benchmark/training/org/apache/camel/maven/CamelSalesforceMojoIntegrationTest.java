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


import Status.SUCCESS;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.tools.JavaFileObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class CamelSalesforceMojoIntegrationTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testExecute() throws Exception {
        final GenerateMojo mojo = createMojo();
        // generate code
        mojo.execute();
        // validate generated code check that it was generated
        final Path packagePath = temp.getRoot().toPath().resolve("test").resolve("dto");
        assertThat(packagePath).as("Package directory was not created").exists();
        // test that the generated sources can be compiled
        final List<JavaFileObject> sources = Files.list(packagePath).map(( p) -> {
            try {
                return JavaFileObjects.forResource(p.toUri().toURL());
            } catch (final MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }).collect(Collectors.toList());
        final Compilation compilation = javac().compile(sources);
        assertThat(compilation.status()).isEqualTo(SUCCESS);
    }
}

