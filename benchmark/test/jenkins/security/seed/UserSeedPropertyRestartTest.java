/**
 * The MIT License
 *
 * Copyright (c) 2018 CloudBees, Inc.
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
package jenkins.security.seed;


import hudson.model.User;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.RestartableJenkinsRule;


@For(UserSeedProperty.class)
public class UserSeedPropertyRestartTest {
    @Rule
    public RestartableJenkinsRule rr = new RestartableJenkinsRule();

    @Test
    @Issue("SECURITY-901")
    public void initialSeedIsSaved() throws Exception {
        AtomicReference<String> initialSeedRef = new AtomicReference<>();
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                rr.j.jenkins.setCrumbIssuer(null);
                rr.j.jenkins.save();
                User alice = User.getById("alice", true);
                alice.save();
                initialSeedRef.set(alice.getProperty(UserSeedProperty.class).getSeed());
            }
        });
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                User alice = User.getById("alice", false);
                String initialSeed = alice.getProperty(UserSeedProperty.class).getSeed();
                Assert.assertEquals(initialSeed, initialSeedRef.get());
            }
        });
    }

    @Test
    @Issue("SECURITY-901")
    public void renewSeedSavesTheChange() throws Exception {
        AtomicReference<String> initialSeedRef = new AtomicReference<>();
        AtomicReference<String> seedRef = new AtomicReference<>();
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                rr.j.jenkins.setCrumbIssuer(null);
                rr.j.jenkins.save();
                User alice = User.getById("alice", true);
                alice.save();
                initialSeedRef.set(alice.getProperty(UserSeedProperty.class).getSeed());
                requestRenewSeedForUser(alice);
                seedRef.set(alice.getProperty(UserSeedProperty.class).getSeed());
                Assert.assertNotEquals(initialSeedRef.get(), seedRef.get());
            }
        });
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                User alice = User.getById("alice", false);
                Assert.assertNotNull(alice);
                String currentSeed = alice.getProperty(UserSeedProperty.class).getSeed();
                Assert.assertEquals(currentSeed, seedRef.get());
            }
        });
    }
}

