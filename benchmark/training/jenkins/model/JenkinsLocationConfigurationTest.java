/**
 * The MIT License
 *
 * Copyright 2015 CloudBees Inc., Oleg Nenashev.
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
package jenkins.model;


import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


/**
 * Tests for {@link JenkinsLocationConfiguration}.
 *
 * @author Oleg Nenashev
 */
public class JenkinsLocationConfigurationTest {
    JenkinsLocationConfiguration config;

    @Test
    public void setAdminEmail() {
        final String email = "test@foo.bar";
        final String email2 = "test@bar.foo";
        // Assert the default value
        Assert.assertEquals(Messages.Mailer_Address_Not_Configured(), config.getAdminAddress());
        // Basic case
        config.setAdminAddress(email);
        Assert.assertEquals(email, config.getAdminAddress());
        // Quoted value
        config.setAdminAddress((("\"" + email2) + "\""));
        Assert.assertEquals(email2, config.getAdminAddress());
    }

    @Test
    @Issue("JENKINS-28419")
    public void resetAdminEmail() {
        final String email = "test@foo.bar";
        // Set the e-mail
        config.setAdminAddress(email);
        Assert.assertEquals(email, config.getAdminAddress());
        // Reset it
        config.setAdminAddress(null);
        Assert.assertEquals(Messages.Mailer_Address_Not_Configured(), config.getAdminAddress());
    }
}

