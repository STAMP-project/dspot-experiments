/**
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
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
package jenkins.security.stapler;


import RoutingDecisionProvider.Decision.ACCEPTED;
import RoutingDecisionProvider.Decision.REJECTED;
import RoutingDecisionProvider.Decision.UNKNOWN;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 * Due to the fact we are using a @ClassRule for the other tests to improve performance,
 * we cannot use @LocalData to test the loading of the whitelist as that annotation seem to not work with @ClassRule.
 */
@Issue("SECURITY-400")
@For(StaticRoutingDecisionProvider.class)
public class StaticRoutingDecisionProvider2Test {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @LocalData("whitelist_empty")
    public void userControlledWhitelist_empty_Loading() throws Exception {
        StaticRoutingDecisionProvider wl = new StaticRoutingDecisionProvider();
        Assert.assertThat(wl.decide("public java.lang.Object jenkins.security.stapler.StaticRoutingDecisionProviderTest$ContentProvider.getObjectCustom()"), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(wl.decide("blabla"), CoreMatchers.is(UNKNOWN));
    }

    @Test
    @LocalData("whitelist_monoline")
    public void userControlledWhitelist_monoline_Loading() throws Exception {
        StaticRoutingDecisionProvider wl = new StaticRoutingDecisionProvider();
        Assert.assertThat(wl.decide("method jenkins.security.stapler.StaticRoutingDecisionProviderTest$ContentProvider getObjectCustom"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("blabla"), CoreMatchers.is(UNKNOWN));
    }

    @Test
    @LocalData("whitelist_multiline")
    public void userControlledWhitelist_multiline_Loading() throws Exception {
        StaticRoutingDecisionProvider wl = new StaticRoutingDecisionProvider();
        Assert.assertThat(wl.decide("method jenkins.security.stapler.StaticRoutingDecisionProviderTest$ContentProvider getObjectCustom"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("method jenkins.security.stapler.StaticRoutingDecisionProviderTest$ContentProvider getObjectCustom2"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("blabla"), CoreMatchers.is(UNKNOWN));
    }

    @Test
    @LocalData("comment_ignored")
    public void userControlledWhitelist_commentsAreIgnored() throws Exception {
        StaticRoutingDecisionProvider wl = new StaticRoutingDecisionProvider();
        Assert.assertThat(wl.decide("this line is not read"), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(wl.decide("not-this-one"), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(wl.decide("neither"), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(wl.decide("finally-not"), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(wl.decide("this-one-is"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("this-one-also"), CoreMatchers.is(ACCEPTED));
    }

    @Test
    @LocalData("whitelist_emptyline")
    public void userControlledWhitelist_emptyLinesAreIgnored() throws Exception {
        StaticRoutingDecisionProvider wl = new StaticRoutingDecisionProvider();
        Assert.assertThat(wl.decide("signature-1"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("signature-2"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("signature-3"), CoreMatchers.is(ACCEPTED));
        // neither the empty line or an exclamation mark followed by nothing or spaces are not considered
        Assert.assertThat(wl.decide(""), CoreMatchers.is(UNKNOWN));
    }

    @Test
    @LocalData("greylist_multiline")
    public void userControlledWhitelist_whiteAndBlack() throws Exception {
        StaticRoutingDecisionProvider wl = new StaticRoutingDecisionProvider();
        Assert.assertThat(wl.decide("signature-1-ok"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("signature-3-ok"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("signature-2-not-ok"), CoreMatchers.is(REJECTED));
        Assert.assertThat(wl.decide("signature-4-not-ok"), CoreMatchers.is(REJECTED));
        // the exclamation mark is not used
        Assert.assertThat(wl.decide("!signature-2-not-ok"), CoreMatchers.is(UNKNOWN));
    }

    @Test
    public void defaultList() throws Exception {
        StaticRoutingDecisionProvider wl = new StaticRoutingDecisionProvider();
        Assert.assertThat(wl.decide("method io.jenkins.blueocean.service.embedded.rest.AbstractRunImpl getLog"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("method io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeImpl getLog"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("method io.jenkins.blueocean.rest.impl.pipeline.PipelineStepImpl getLog"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("method jenkins.security.stapler.StaticRoutingDecisionProviderTest$ContentProvider getObjectCustom"), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(wl.decide("blabla"), CoreMatchers.is(UNKNOWN));
    }

    @Test
    public void userControlledWhitelist_savedCorrectly() throws Exception {
        File whitelistUserControlledList = new File(j.jenkins.getRootDir(), "stapler-whitelist.txt");
        Assert.assertFalse(whitelistUserControlledList.exists());
        StaticRoutingDecisionProvider wl = new StaticRoutingDecisionProvider();
        Assert.assertFalse(whitelistUserControlledList.exists());
        Assert.assertThat(wl.decide("nothing"), CoreMatchers.is(UNKNOWN));
        wl.save();
        Assert.assertTrue(whitelistUserControlledList.exists());
        Assert.assertThat(FileUtils.readFileToString(whitelistUserControlledList), CoreMatchers.is(""));
        wl.add("white-1");
        Assert.assertThat(wl.decide("white-1"), CoreMatchers.is(ACCEPTED));
        Assert.assertTrue(whitelistUserControlledList.exists());
        Assert.assertThat(FileUtils.readFileToString(whitelistUserControlledList), CoreMatchers.containsString("white-1"));
        {
            StaticRoutingDecisionProvider temp = new StaticRoutingDecisionProvider();
            Assert.assertThat(temp.decide("white-1"), CoreMatchers.is(ACCEPTED));
        }
        wl.addBlacklistSignature("black-2");
        Assert.assertThat(wl.decide("white-1"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("black-2"), CoreMatchers.is(REJECTED));
        Assert.assertThat(FileUtils.readFileToString(whitelistUserControlledList), CoreMatchers.allOf(CoreMatchers.containsString("white-1"), CoreMatchers.containsString("!black-2")));
        {
            StaticRoutingDecisionProvider temp = new StaticRoutingDecisionProvider();
            Assert.assertThat(temp.decide("white-1"), CoreMatchers.is(ACCEPTED));
            Assert.assertThat(temp.decide("black-2"), CoreMatchers.is(REJECTED));
        }
        wl.removeBlacklistSignature("black-2");
        Assert.assertThat(wl.decide("white-1"), CoreMatchers.is(ACCEPTED));
        Assert.assertThat(wl.decide("black-2"), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(FileUtils.readFileToString(whitelistUserControlledList), CoreMatchers.allOf(CoreMatchers.containsString("white-1"), CoreMatchers.not(CoreMatchers.containsString("black-2"))));
        {
            StaticRoutingDecisionProvider temp = new StaticRoutingDecisionProvider();
            Assert.assertThat(temp.decide("white-1"), CoreMatchers.is(ACCEPTED));
            Assert.assertThat(temp.decide("black-2"), CoreMatchers.is(UNKNOWN));
        }
        wl.remove("white-1");
        Assert.assertThat(wl.decide("white-1"), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(wl.decide("black-2"), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(FileUtils.readFileToString(whitelistUserControlledList), CoreMatchers.allOf(CoreMatchers.not(CoreMatchers.containsString("white-1")), CoreMatchers.not(CoreMatchers.containsString("black-2"))));
        {
            StaticRoutingDecisionProvider temp = new StaticRoutingDecisionProvider();
            Assert.assertThat(temp.decide("white-1"), CoreMatchers.is(UNKNOWN));
            Assert.assertThat(temp.decide("black-2"), CoreMatchers.is(UNKNOWN));
        }
    }
}

