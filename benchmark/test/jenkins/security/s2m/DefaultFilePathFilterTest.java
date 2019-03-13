/**
 * The MIT License
 *
 * Copyright 2014 Jesse Glick.
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
package jenkins.security.s2m;


import hudson.FilePath;
import hudson.Functions;
import hudson.model.Slave;
import hudson.remoting.Callable;
import java.io.File;
import javax.inject.Inject;
import org.jenkinsci.remoting.RoleChecker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static DefaultFilePathFilter.BYPASS;


public class DefaultFilePathFilterTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Inject
    AdminWhitelistRule rule;

    @Test
    public void remotePath() throws Exception {
        Slave s = r.createOnlineSlave();
        FilePath forward = s.getRootPath().child("forward");
        forward.write("hello", null);
        Assert.assertEquals("hello", s.getRootPath().act(new DefaultFilePathFilterTest.LocalCallable(forward)));
        FilePath reverse = new FilePath(new File(r.jenkins.root, "reverse"));
        Assert.assertFalse(reverse.exists());
        try {
            s.getChannel().call(new DefaultFilePathFilterTest.ReverseCallable(reverse));
            Assert.fail("should have failed");
        } catch (Exception x) {
            // good
            // make sure that the stack trace contains the call site info to help assist diagnosis
            Assert.assertThat(Functions.printThrowable(x), containsString(((DefaultFilePathFilterTest.class.getName()) + ".remotePath")));
        }
        Assert.assertFalse(reverse.exists());
        BYPASS = true;
        s.getChannel().call(new DefaultFilePathFilterTest.ReverseCallable(reverse));
        Assert.assertTrue(reverse.exists());
        Assert.assertEquals("goodbye", reverse.readToString());
    }

    private static class LocalCallable implements Callable<String, Exception> {
        private final FilePath p;

        LocalCallable(FilePath p) {
            this.p = p;
        }

        @Override
        public String call() throws Exception {
            Assert.assertFalse(p.isRemote());
            return p.readToString();
        }

        @Override
        public void checkRoles(RoleChecker checker) throws SecurityException {
            throw new NoSuchMethodError();// simulate legacy Callable impls

        }
    }

    private static class ReverseCallable implements Callable<Void, Exception> {
        private final FilePath p;

        ReverseCallable(FilePath p) {
            this.p = p;
        }

        @Override
        public Void call() throws Exception {
            Assert.assertTrue(p.isRemote());
            p.write("goodbye", null);
            return null;
        }

        @Override
        public void checkRoles(RoleChecker checker) throws SecurityException {
            throw new NoSuchMethodError();// simulate legacy Callable impls

        }
    }
}

