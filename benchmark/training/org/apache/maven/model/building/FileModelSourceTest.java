/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.model.building;


import java.io.File;
import junit.framework.TestCase;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test that validate the solution of MNG-6261 issue
 */
public class FileModelSourceTest {
    /**
     * Test of equals method, of class FileModelSource.
     */
    @Test
    public void testEquals() throws Exception {
        File tempFile = createTempFile("pomTest");
        FileModelSource instance = new FileModelSource(tempFile);
        TestCase.assertFalse(instance.equals(null));
        TestCase.assertFalse(instance.equals(new Object()));
        TestCase.assertTrue(instance.equals(instance));
        TestCase.assertTrue(instance.equals(new FileModelSource(tempFile)));
    }

    @Test
    public void testWindowsPaths() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        File upperCaseFile = createTempFile("TESTE");
        String absolutePath = upperCaseFile.getAbsolutePath();
        File lowerCaseFile = new File(absolutePath.toLowerCase());
        FileModelSource upperCaseFileSouce = new FileModelSource(upperCaseFile);
        FileModelSource lowerCaseFileSouce = new FileModelSource(lowerCaseFile);
        TestCase.assertTrue(upperCaseFileSouce.equals(lowerCaseFileSouce));
    }
}

