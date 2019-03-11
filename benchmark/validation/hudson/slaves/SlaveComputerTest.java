/**
 * The MIT License
 *
 * Copyright (c) 2018, suren <zxjlwt@126.com>
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
package hudson.slaves;


import Computer.CONFIGURE;
import Computer.CONNECT;
import Jenkins.READ;
import hudson.security.ACL;
import hudson.security.ACLContext;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;


/**
 *
 *
 * @author suren
 */
public class SlaveComputerTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testGetAbsoluteRemotePath() throws Exception {
        // default auth
        Node nodeA = j.createOnlineSlave();
        String path = getComputer().getAbsoluteRemotePath();
        Assert.assertNotNull(path);
        Assert.assertEquals(getRemoteFS(nodeA, null), path);
        // not auth
        String userAlice = "alice";
        MockAuthorizationStrategy authStrategy = new MockAuthorizationStrategy();
        authStrategy.grant(CONFIGURE, READ).everywhere().to(userAlice);
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(authStrategy);
        try (ACLContext context = ACL.as(User.getById(userAlice, true))) {
            path = getComputer().getAbsoluteRemotePath();
            Assert.assertNull(path);
            Assert.assertNull(getRemoteFS(nodeA, userAlice));
        }
        // with auth
        String userBob = "bob";
        authStrategy.grant(CONNECT, READ).everywhere().to(userBob);
        try (ACLContext context = ACL.as(User.getById(userBob, true))) {
            path = getComputer().getAbsoluteRemotePath();
            Assert.assertNotNull(path);
            Assert.assertNotNull(getRemoteFS(nodeA, userBob));
        }
    }
}

