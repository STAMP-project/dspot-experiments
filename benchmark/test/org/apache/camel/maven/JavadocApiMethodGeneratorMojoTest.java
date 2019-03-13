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


import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;


/**
 * Tests {@link JavadocApiMethodGeneratorMojo}
 */
public class JavadocApiMethodGeneratorMojoTest extends AbstractGeneratorMojoTest {
    @Test
    public void testExecute() throws IOException, MojoExecutionException, MojoFailureException {
        // delete target file to begin
        final File outFile = new File(AbstractGeneratorMojoTest.OUT_DIR, ((AbstractGeneratorMojoTest.PACKAGE_PATH) + "VelocityContextApiMethod.java"));
        if (outFile.exists()) {
            outFile.delete();
        }
        final JavadocApiMethodGeneratorMojo mojo = createGeneratorMojo();
        mojo.execute();
        // check target file was generated
        assertExists(outFile);
    }
}

