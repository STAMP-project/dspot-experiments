/**
 * The MIT License
 *
 * Copyright 2015 Jesse Glick.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.util;


import hudson.FilePath;
import java.io.File;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;


// TODO merge into VirtualFileTest after the security release
public class VirtualFileSEC904Test {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Issue("SECURITY-904")
    @Test
    public void forFile_isDescendant() throws Exception {
        this.prepareFileStructureForIsDescendant();
        File root = tmp.getRoot();
        File a = new File(root, "a");
        File aa = new File(a, "aa");
        VirtualFile virtualRoot = VirtualFile.forFile(root);
        // keep the root information for isDescendant
        VirtualFile virtualRootChildA = virtualRoot.child("a");
        VirtualFile virtualFromA = VirtualFile.forFile(a);
        checkCommonAssertionForIsDescendant(virtualRoot, virtualRootChildA, virtualFromA, aa.getAbsolutePath());
    }

    @Test
    @Issue("SECURITY-904")
    public void forFilePath_isDescendant() throws Exception {
        this.prepareFileStructureForIsDescendant();
        File root = tmp.getRoot();
        File a = new File(root, "a");
        File aa = new File(a, "aa");
        VirtualFile virtualRoot = VirtualFile.forFilePath(new FilePath(root));
        // keep the root information for isDescendant
        VirtualFile virtualRootChildA = virtualRoot.child("a");
        VirtualFile virtualFromA = VirtualFile.forFilePath(new FilePath(a));
        checkCommonAssertionForIsDescendant(virtualRoot, virtualRootChildA, virtualFromA, aa.getAbsolutePath());
    }

    @Test
    @Issue("JENKINS-55050")
    public void forFile_listOnlyDescendants_withoutIllegal() throws Exception {
        this.prepareFileStructureForIsDescendant();
        File root = tmp.getRoot();
        File a = new File(root, "a");
        File b = new File(root, "b");
        VirtualFile virtualRoot = VirtualFile.forFile(root);
        VirtualFile virtualFromA = VirtualFile.forFile(a);
        VirtualFile virtualFromB = VirtualFile.forFile(b);
        checkCommonAssertionForList(virtualRoot, virtualFromA, virtualFromB);
    }

    @Test
    @Issue("SECURITY-904")
    public void forFilePath_listOnlyDescendants_withoutIllegal() throws Exception {
        this.prepareFileStructureForIsDescendant();
        File root = tmp.getRoot();
        File a = new File(root, "a");
        File b = new File(root, "b");
        VirtualFile virtualRoot = VirtualFile.forFilePath(new FilePath(root));
        VirtualFile virtualFromA = VirtualFile.forFilePath(new FilePath(a));
        VirtualFile virtualFromB = VirtualFile.forFilePath(new FilePath(b));
        checkCommonAssertionForList(virtualRoot, virtualFromA, virtualFromB);
    }

    private abstract static class VFMatcher extends TypeSafeMatcher<VirtualFile> {
        private final String description;

        private VFMatcher(String description) {
            this.description = description;
        }

        public void describeTo(Description description) {
            description.appendText(this.description);
        }

        public static VirtualFileSEC904Test.VFMatcher hasName(String expectedName) {
            return new VirtualFileSEC904Test.VFMatcher(("Has name: " + expectedName)) {
                protected boolean matchesSafely(VirtualFile vf) {
                    return expectedName.equals(vf.getName());
                }
            };
        }
    }
}

