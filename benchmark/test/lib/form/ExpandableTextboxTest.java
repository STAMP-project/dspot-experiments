/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Yahoo! Inc.
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
package lib.form;


import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.UnprotectedRootAction;
import hudson.util.HttpResponses;
import javax.annotation.CheckForNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.WebMethod;
import org.w3c.dom.NodeList;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class ExpandableTextboxTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Issue("JENKINS-2816")
    @Test
    public void testMultiline() throws Exception {
        // because attribute values are normalized, it's not very easy to encode multi-line string as @value. So let's use the system message here.
        j.jenkins.setSystemMessage("foo\nbar\nzot");
        HtmlPage page = evaluateAsHtml("<l:layout><l:main-panel><table><j:set var='instance' value='${it}'/><f:expandableTextbox field='systemMessage' /></table></l:main-panel></l:layout>");
        // System.out.println(page.getWebResponse().getContentAsString());
        NodeList textareas = page.getElementsByTagName("textarea");
        Assert.assertEquals(1, textareas.getLength());
        Assert.assertEquals(j.jenkins.getSystemMessage(), textareas.item(0).getTextContent());
    }

    @Test
    public void noInjectionArePossible() throws Exception {
        ExpandableTextboxTest.TestRootAction testParams = j.jenkins.getExtensionList(UnprotectedRootAction.class).get(ExpandableTextboxTest.TestRootAction.class);
        Assert.assertNotNull(testParams);
        checkRegularCase(testParams);
        checkInjectionInName(testParams);
    }

    @TestExtension("noInjectionArePossible")
    public static final class TestRootAction implements UnprotectedRootAction {
        public String paramName;

        @Override
        @CheckForNull
        public String getIconFileName() {
            return null;
        }

        @Override
        @CheckForNull
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return "test";
        }

        @WebMethod(name = "submit")
        public HttpResponse doSubmit(StaplerRequest request) {
            return HttpResponses.plainText(("method:" + (request.getMethod())));
        }
    }
}

