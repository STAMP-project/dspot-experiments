/**
 * The MIT License
 *
 * Copyright (c) 2010, CloudBees, Inc.
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
package hudson.util;


import ParameterizedJobMixIn.BUILD_NOW_TEXT;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.StringParameterDefinition;
import jenkins.model.ParameterizedJobMixIn;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;


public class AlternativeUiTextProviderTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @TestExtension
    public static class Impl extends AlternativeUiTextProvider {
        static boolean oldschool;

        @SuppressWarnings("deprecation")
        @Override
        public <T> String getText(Message<T> text, T context) {
            if ((AlternativeUiTextProviderTest.Impl.oldschool) && (text == (ParameterizedJobMixIn.BUILD_NOW_TEXT))) {
                return "oldschool:" + (BUILD_NOW_TEXT.cast(context).getDisplayName());
            }
            if ((!(AlternativeUiTextProviderTest.Impl.oldschool)) && (text == (AbstractProject.BUILD_NOW_TEXT))) {
                return "newschool:" + (AbstractProject.BUILD_NOW_TEXT.cast(context).getDisplayName());
            }
            return null;
        }
    }

    /**
     * Makes sure that {@link AlternativeUiTextProvider} actually works at some basic level.
     */
    @Test
    public void basics() throws Exception {
        AlternativeUiTextProviderTest.Impl.oldschool = false;
        FreeStyleProject p = j.createFreeStyleProject("aaa");
        Assert.assertThat(j.createWebClient().getPage(p).asText(), Matchers.containsString("newschool:aaa"));
        AlternativeUiTextProviderTest.Impl.oldschool = true;
        Assert.assertThat(j.createWebClient().getPage(p).asText(), Matchers.containsString("oldschool:aaa"));
    }

    /**
     * Makes sure that {@link AlternativeUiTextProvider} actually works with a parameterized Job.
     */
    @Test
    @Issue("JENKINS-41757")
    public void basicsWithParameter() throws Exception {
        AlternativeUiTextProviderTest.Impl.oldschool = false;
        FreeStyleProject p = j.createFreeStyleProject("aaa");
        p.addProperty(new hudson.model.ParametersDefinitionProperty(new StringParameterDefinition("FOO", null)));
        String pageText = j.createWebClient().getPage(p).asText();
        Assert.assertThat(pageText, Matchers.containsString("newschool:aaa"));
        AlternativeUiTextProviderTest.Impl.oldschool = true;
        pageText = j.createWebClient().getPage(p).asText();
        Assert.assertThat(pageText, Matchers.containsString("oldschool:aaa"));
    }
}

