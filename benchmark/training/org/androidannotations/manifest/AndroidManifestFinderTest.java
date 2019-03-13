/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.manifest;


import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Test;


public class AndroidManifestFinderTest extends AAProcessorTestHelper {
    @Test
    public void failsIfNoManifest() {
        CompileResult result = compileFiles(SomeClass.class);
        assertCompilationErrorWithNoSource(result);
        assertCompilationErrorCount(1, result);
    }

    @Test
    public void findsSpecifiedManifest() {
        addManifestProcessorParameter(AndroidManifestFinderTest.class);
        CompileResult result = compileFiles(SomeClass.class);
        assertCompilationSuccessful(result);
    }

    @Test
    public void failsIfCannotFindSpecifiedManifest() {
        addProcessorParameter("androidManifestFile", "/some/random/path/AndroidManifest.xml");
        CompileResult result = compileFiles(SomeClass.class);
        assertCompilationErrorWithNoSource(result);
        assertCompilationErrorCount(1, result);
    }

    @Test
    public void failsIfCannotParseManifest() {
        addManifestProcessorParameter(AndroidManifestFinderTest.class, "ParseErrorManifest.xml");
        CompileResult result = compileFiles(SomeClass.class);
        assertCompilationErrorWithNoSource(result);
        assertCompilationErrorCount(1, result);
    }
}

