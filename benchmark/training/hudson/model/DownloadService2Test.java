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
package hudson.model;


import DownloadService.Downloadable;
import FormValidation.Kind.OK;
import hudson.util.FormValidation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;


@Issue("SECURITY-163")
public class DownloadService2Test {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void updateNow() throws Exception {
        for (DownloadService.Downloadable d : Downloadable.all()) {
            FormValidation v = d.updateNow();
            Assert.assertEquals(v.toString(), OK, v.kind);
        }
    }

    @WithoutJenkins
    @Test
    public void loadJSONHTML() throws Exception {
        DownloadService2Test.assertRoots("[list, signature]", "hudson.tasks.Maven.MavenInstaller.json.html");// format used by most tools

        DownloadService2Test.assertRoots("[data, signature, version]", "hudson.tools.JDKInstaller.json.html");// anomalous format

    }
}

