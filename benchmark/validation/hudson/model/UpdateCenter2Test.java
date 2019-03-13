/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc.
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


import hudson.model.UpdateCenter.DownloadJob;
import hudson.model.UpdateCenter.DownloadJob.Failure;
import hudson.model.UpdateCenter.DownloadJob.Success;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import static UpdateSite.neverUpdate;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class UpdateCenter2Test {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Makes sure a plugin installs fine.
     */
    // TODO randomly fails: SocketTimeoutException from goTo due to GET http://localhost:?/update-center.json??
    @Test
    public void install() throws Exception {
        neverUpdate = false;
        j.jenkins.pluginManager.doCheckUpdatesServer();// load the metadata

        DownloadJob job = ((DownloadJob) (j.jenkins.getUpdateCenter().getPlugin("changelog-history").deploy().get()));// this seems like one of the smallest plugin

        System.out.println(job.status);
        Assert.assertTrue(((job.status) instanceof Success));
    }

    @Test
    public void getLastUpdatedString() {
        neverUpdate = false;
        Assert.assertTrue(j.jenkins.getUpdateCenter().getById("default").isDue());
        Assert.assertEquals(Messages.UpdateCenter_n_a(), j.jenkins.getUpdateCenter().getLastUpdatedString());
    }

    @Issue("SECURITY-234")
    @Test
    public void installInvalidChecksum() throws Exception {
        neverUpdate = false;
        j.jenkins.pluginManager.doCheckUpdatesServer();// load the metadata

        String wrongChecksum = "ABCDEFG1234567890";
        // usually the problem is the file having a wrong checksum, but changing the expected one works just the same
        j.jenkins.getUpdateCenter().getSite("default").getPlugin("changelog-history").sha512 = wrongChecksum;
        DownloadJob job = ((DownloadJob) (j.jenkins.getUpdateCenter().getPlugin("changelog-history").deploy().get()));
        Assert.assertTrue(((job.status) instanceof Failure));
        Assert.assertTrue("error message references checksum", ((Failure) (job.status)).problem.getMessage().contains(wrongChecksum));
    }
}

