/**
 * Copyright 2015 OWASP.
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
package org.owasp.dependencycheck.analyzer;


import java.io.File;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;


/**
 *
 *
 * @author jeremy long
 */
public class ArchiveAnalyzerTest extends BaseTest {
    /**
     * Test of analyzeDependency method, of class ArchiveAnalyzer.
     */
    @Test
    public void testZippableExtensions() throws Exception {
        Assume.assumeFalse(isPreviouslyLoaded("org.owasp.dependencycheck.analyzer.ArchiveAnalyzer"));
        ArchiveAnalyzer instance = new ArchiveAnalyzer();
        instance.initialize(getSettings());
        Assert.assertTrue(instance.getFileFilter().accept(new File("c:/test.zip")));
        Assert.assertTrue(instance.getFileFilter().accept(new File("c:/test.z2")));
        Assert.assertTrue(instance.getFileFilter().accept(new File("c:/test.z3")));
        Assert.assertFalse(instance.getFileFilter().accept(new File("c:/test.z4")));
    }
}

