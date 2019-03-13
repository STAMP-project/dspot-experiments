/**
 * The MIT License
 *
 * Copyright 2018 CloudBees, Inc.
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
package hudson.console;


import hudson.model.FreeStyleProject;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


public class HyperlinkNoteTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Issue("JENKINS-53016")
    @Test
    public void textWithNewlines() throws Exception {
        String url = (r.getURL().toString()) + "test";
        String noteText = "\nthis string\nhas newline\r\ncharacters\n\r";
        String input = HyperlinkNote.encodeTo(url, noteText);
        String noteTextSanitized = input.substring(((input.length()) - (noteText.length())));
        // Throws IndexOutOfBoundsException before https://github.com/jenkinsci/jenkins/pull/3580.
        String output = HyperlinkNoteTest.annotate(input);
        Assert.assertThat(output, Matchers.allOf(Matchers.containsString((("href='" + url) + "'")), Matchers.containsString(((">" + noteTextSanitized) + "</a>"))));
    }

    @Issue("JENKINS-53016")
    @Test
    public void textWithNewlinesModelHyperlinkNote() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        String noteText = "\nthis string\nhas newline\r\ncharacters\n\r";
        String input = ModelHyperlinkNote.encodeTo(p, noteText);
        String noteTextSanitized = input.substring(((input.length()) - (noteText.length())));
        // Throws IndexOutOfBoundsException before https://github.com/jenkinsci/jenkins/pull/3580.
        String output = HyperlinkNoteTest.annotate(input);
        Assert.assertThat(output, Matchers.allOf(Matchers.containsString(((("href='" + (r.getURL().toString())) + (p.getUrl())) + "'")), Matchers.containsString(new ModelHyperlinkNote("", 0).extraAttributes()), Matchers.containsString(((">" + noteTextSanitized) + "</a>"))));
    }
}

