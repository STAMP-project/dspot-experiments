/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.devtools.treeshaker;


import com.google.devtools.j2objc.util.CodeReferenceMap;
import com.google.devtools.j2objc.util.CodeReferenceMap.Builder;
import com.google.devtools.j2objc.util.ErrorUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import junit.framework.TestCase;


/**
 * System tests for the TreeShaker tool.
 *
 * @author Priyank Malvania
 */
public class TreeShakerTest extends TestCase {
    File tempDir;

    List<String> inputFiles;

    static {
        // Prevents errors and warnings from being printed to the console.
        ErrorUtil.setTestMode();
    }

    public void testUnusedCodeAcrossFiles() throws IOException {
        addSourceFile("A.java", ("class A { static { launch(); }\n" + "public static void launch() { new B().abc(\"zoo\"); } }"));
        addSourceFile("B.java", "class B { public void abc(String s) {} }");
        addSourceFile("C.java", "class C { public void xyz(String s) {} }");
        CodeReferenceMap unusedCodeMap = getUnusedCode();
        TestCase.assertFalse(unusedCodeMap.containsClass("A"));
        TestCase.assertFalse(unusedCodeMap.containsClass("B"));
        TestCase.assertFalse(unusedCodeMap.containsMethod("B", "abc", "(Ljava/lang/String;)V"));
        TestCase.assertTrue(unusedCodeMap.containsClass("C"));
        TestCase.assertTrue(unusedCodeMap.containsMethod("C", "xyz", "(Ljava/lang/String;)V"));
    }

    public void testNoPublicRootSet() throws IOException {
        addSourceFile("A.java", "class A { public void launch() { new B().abc(\"zoo\"); } }");
        addSourceFile("B.java", "class B { public void abc(String s) {} }");
        addSourceFile("C.java", "class C { public void xyz(String s) {} }");
        CodeReferenceMap unusedCodeMap = getUnusedCode();
        TestCase.assertTrue(unusedCodeMap.containsClass("A"));
        TestCase.assertTrue(unusedCodeMap.containsClass("B"));
        TestCase.assertTrue(unusedCodeMap.containsMethod("B", "abc", "(Ljava/lang/String;)V"));
        TestCase.assertTrue(unusedCodeMap.containsClass("C"));
        TestCase.assertTrue(unusedCodeMap.containsMethod("C", "xyz", "(Ljava/lang/String;)V"));
    }

    public void testWithPublicRootSet() throws IOException {
        addSourceFile("A.java", "class A { public void launch() { new B().abc(\"zoo\"); } }");
        addSourceFile("B.java", "class B { public void abc(String s) {} }");
        addSourceFile("C.java", "class C { public void xyz(String s) {} }");
        CodeReferenceMap rootSet = new Builder().addClass("A").build();
        CodeReferenceMap unusedCodeMap = getUnusedCode(rootSet);
        TestCase.assertFalse(unusedCodeMap.containsClass("A"));
        TestCase.assertFalse(unusedCodeMap.containsClass("B"));
        TestCase.assertFalse(unusedCodeMap.containsMethod("B", "abc", "(Ljava/lang/String;)V"));
        TestCase.assertTrue(unusedCodeMap.containsClass("C"));
        TestCase.assertTrue(unusedCodeMap.containsMethod("C", "xyz", "(Ljava/lang/String;)V"));
    }
}

