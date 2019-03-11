/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.domain;


import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.util.ReflectionUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CaseInsensitiveStringTest {
    @Test
    public void shouldIgnoreCaseInEquals() {
        CaseInsensitiveString name = new CaseInsensitiveString("someName");
        Assert.assertThat(name, Matchers.is(new CaseInsensitiveString("someName")));
        Assert.assertThat(name, Matchers.is(new CaseInsensitiveString("SOMENAME")));
        Assert.assertThat(name, Matchers.not(new CaseInsensitiveString("SOMECRAP")));
    }

    @Test
    public void shouldUnderstandBlankString() {
        Assert.assertThat(new CaseInsensitiveString("someName").isBlank(), Matchers.is(false));
        Assert.assertThat(new CaseInsensitiveString(null).isBlank(), Matchers.is(true));
        Assert.assertThat(new CaseInsensitiveString("").isBlank(), Matchers.is(true));
        Assert.assertThat(new CaseInsensitiveString(" ").isBlank(), Matchers.is(false));
    }

    @Test
    public void shouldClone() throws Exception {
        CaseInsensitiveString foo = new CaseInsensitiveString("foo");
        CaseInsensitiveString fooClone = ((CaseInsensitiveString) (ReflectionUtil.invoke(foo, "clone")));
        Assert.assertThat(foo, Matchers.is(fooClone));
        Assert.assertThat(foo, Matchers.not(Matchers.sameInstance(fooClone)));
    }

    @Test
    public void shouldCompare() {
        CaseInsensitiveString foo = new CaseInsensitiveString("foo");
        CaseInsensitiveString fOO = new CaseInsensitiveString("fOO");
        CaseInsensitiveString bar = new CaseInsensitiveString("bar");
        Assert.assertThat(foo.compareTo(fOO), Matchers.is(0));
        Assert.assertThat(fOO.compareTo(foo), Matchers.is(0));
        Assert.assertThat(bar.compareTo(foo), Matchers.lessThan(0));
        Assert.assertThat(bar.compareTo(fOO), Matchers.lessThan(0));
        Assert.assertThat(foo.compareTo(bar), Matchers.greaterThan(0));
        Assert.assertThat(fOO.compareTo(bar), Matchers.greaterThan(0));
    }

    @Test
    public void shouldUnderstandCase() {
        Assert.assertThat(new CaseInsensitiveString("foo").toUpper(), Matchers.is("FOO"));
        Assert.assertThat(new CaseInsensitiveString("FOO").toLower(), Matchers.is("foo"));
    }

    @Test
    public void shouldReturnNullSafeStringRepresentation() {
        Assert.assertThat(CaseInsensitiveString.str(new CaseInsensitiveString("foo")), Matchers.is("foo"));
        Assert.assertThat(CaseInsensitiveString.str(new CaseInsensitiveString("")), Matchers.is(""));
        Assert.assertThat(CaseInsensitiveString.str(new CaseInsensitiveString(null)), Matchers.nullValue());
        Assert.assertThat(CaseInsensitiveString.str(null), Matchers.nullValue());
    }
}

