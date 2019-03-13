/**
 * The MIT License
 *
 * Copyright (c) 2014
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


import JDK.DEFAULT_NAME;
import hudson.model.JDK;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class JDKNameTest {
    @Test
    public void nullIsDefaultName() {
        MatcherAssert.assertThat(JDK.isDefaultName(null), CoreMatchers.is(true));
    }

    @Test
    public void recognizeOldDefaultName() {
        // DEFAULT_NAME took this value prior to 1.598.
        MatcherAssert.assertThat(JDK.isDefaultName("(Default)"), CoreMatchers.is(true));
    }

    @Test
    public void recognizeDefaultName() {
        MatcherAssert.assertThat(JDK.isDefaultName(DEFAULT_NAME), CoreMatchers.is(true));
    }

    @Test
    public void othernameNotDefault() {
        MatcherAssert.assertThat(JDK.isDefaultName("I'm a customized name"), CoreMatchers.is(false));
    }
}

