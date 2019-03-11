/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.genconf;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests GenerateOzoneRequiredConfigurations.
 */
public class TestGenerateOzoneRequiredConfigurations {
    private static File outputBaseDir;

    private static GenerateOzoneRequiredConfigurations genconfTool;

    private static final Logger LOG = LoggerFactory.getLogger(TestGenerateOzoneRequiredConfigurations.class);

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private static final PrintStream OLD_OUT = System.out;

    private static final PrintStream OLD_ERR = System.err;

    /**
     * Tests a valid path and generates ozone-site.xml by calling
     * {@code GenerateOzoneRequiredConfigurations#generateConfigurations}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGenerateConfigurations() throws Exception {
        File tempPath = getRandomTempDir();
        String[] args = new String[]{ tempPath.getAbsolutePath() };
        execute(args, ("ozone-site.xml has been generated at " + (tempPath.getAbsolutePath())));
    }

    /**
     * Generates ozone-site.xml at specified path.
     * Verify that it does not overwrite if file already exists in path.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDoesNotOverwrite() throws Exception {
        File tempPath = getRandomTempDir();
        String[] args = new String[]{ tempPath.getAbsolutePath() };
        execute(args, ("ozone-site.xml has been generated at " + (tempPath.getAbsolutePath())));
        // attempt overwrite
        execute(args, (("ozone-site.xml already exists at " + (tempPath.getAbsolutePath())) + " and will not be overwritten"));
    }

    /**
     * Test to avoid generating ozone-site.xml when insufficient permission.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void genconfFailureByInsufficientPermissions() throws Exception {
        File tempPath = getRandomTempDir();
        tempPath.setReadOnly();
        String[] args = new String[]{ tempPath.getAbsolutePath() };
        executeWithException(args, "Insufficient permission.");
    }

    /**
     * Test to avoid generating ozone-site.xml when invalid path.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void genconfFailureByInvalidPath() throws Exception {
        File tempPath = getRandomTempDir();
        String[] args = new String[]{ "invalid-path" };
        executeWithException(args, "Invalid directory path.");
    }

    /**
     * Test to avoid generating ozone-site.xml when path not specified.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void genconfPathNotSpecified() throws Exception {
        File tempPath = getRandomTempDir();
        String[] args = new String[]{  };
        executeWithException(args, "Missing required parameter: <path>");
    }

    /**
     * Test to check help message.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void genconfHelp() throws Exception {
        File tempPath = getRandomTempDir();
        String[] args = new String[]{ "--help" };
        execute(args, "Usage: ozone genconf [-hV] [--verbose]");
    }
}

