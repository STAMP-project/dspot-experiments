/**
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
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
package jenkins.security;


import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


/**
 * Tests for {@link UserDetailsCache}.
 */
public class UserDetailsCacheTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void getCachedTrue() throws Exception {
        UserDetailsCache cache = UserDetailsCache.get();
        Assert.assertNotNull(cache);
        UserDetails alice = cache.loadUserByUsername("alice");
        Assert.assertNotNull(alice);
        UserDetails alice1 = cache.getCached("alice");
        Assert.assertNotNull(alice1);
    }

    @Test
    public void getCachedFalse() throws Exception {
        UserDetailsCache cache = UserDetailsCache.get();
        Assert.assertNotNull(cache);
        UserDetails alice1 = cache.getCached("alice");
        Assert.assertNull(alice1);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void getCachedTrueNotFound() throws Exception {
        UserDetailsCache cache = UserDetailsCache.get();
        Assert.assertNotNull(cache);
        try {
            cache.loadUserByUsername("bob");
            Assert.fail("Bob should not be found");
        } catch (UsernameNotFoundException e) {
            // as expected
        }
        cache.getCached("bob");
    }

    @Test
    public void getCachedFalseNotFound() throws Exception {
        UserDetailsCache cache = UserDetailsCache.get();
        Assert.assertNotNull(cache);
        UserDetails bob = cache.getCached("bob");
        Assert.assertNull(bob);
    }
}

