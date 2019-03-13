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


import com.thoughtworks.go.validation.Validator;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MatcherTest {
    @Test
    public void shouldRemoveTheLastEmptyMatcher() {
        Assert.assertThat(new Matcher("JH,Pavan,"), Matchers.is(new Matcher("JH,Pavan")));
    }

    @Test
    public void shouldAllowWhiteSpace() {
        Assert.assertThat(new Matcher("JH Pavan").toCollection().size(), Matchers.is(1));
    }

    @Test
    public void shouldRemoveDuplication() {
        Assert.assertThat(new Matcher("JH,JH"), Matchers.is(new Matcher("JH")));
    }

    @Test
    public void shouldRemoveTheFirstEmptyMatcher() {
        Assert.assertThat(new Matcher(",JH,Pavan"), Matchers.is(new Matcher("JH,Pavan")));
    }

    @Test
    public void shouldRemoveTheEmptyMatcherInTheMiddle() {
        Assert.assertThat(new Matcher("JH,,Pavan"), Matchers.is(new Matcher("JH,Pavan")));
    }

    @Test
    public void shouldReturnCommaSplittedString() {
        Assert.assertThat(new Matcher("JH,Pavan").toString(), Matchers.is("JH,Pavan"));
    }

    @Test
    public void shouldSplitEachElement() {
        String[] array = new String[]{ "JH", "Pavan,Jez", "HK" };
        Assert.assertThat(new Matcher(array), Matchers.is(new Matcher("JH,Pavan,Jez,HK")));
    }

    @Test
    public void shouldRemoveTheDuplicationFromEachElement() {
        String[] array = new String[]{ "JH", "Pavan,Jez", "Pavan" };
        Assert.assertThat(new Matcher(array), Matchers.is(new Matcher("JH,Pavan,Jez")));
    }

    @Test
    public void shouldReturnMatchersAsArray() {
        Assert.assertThat(new Matcher("JH,Pavan").toCollection(), Matchers.is(Arrays.asList("JH", "Pavan")));
    }

    @Test
    public void shouldTrim() {
        Assert.assertThat(new Matcher("  JH   , Pavan "), Matchers.is(new Matcher("JH,Pavan")));
    }

    @Test
    public void shouldMatchWordBoundaries() throws Exception {
        Assert.assertThat(new Matcher("!!").matches("!!"), Matchers.is(true));
        Assert.assertThat(new Matcher("ja").matches(" ja"), Matchers.is(true));
        Assert.assertThat(new Matcher("ja").matches("ja "), Matchers.is(true));
        Assert.assertThat(new Matcher("ja").matches(" ja"), Matchers.is(true));
        Assert.assertThat(new Matcher("ja").matches("ja:"), Matchers.is(true));
        Assert.assertThat(new Matcher("jez.humble@thoughtworks.com").matches("[jez.humble@thoughtworks.com] i checkin"), Matchers.is(true));
        Assert.assertThat(new Matcher("ja").matches("ja&jh"), Matchers.is(true));
    }

    @Test
    public void shouldNotMatchWordContainsMatcher() throws Exception {
        Assert.assertThat(new Matcher("ja").matches("javascript"), Matchers.is(false));
        Assert.assertThat(new Matcher("ja").matches("kaja"), Matchers.is(false));
        Assert.assertThat(new Matcher("jez.humble@thoughtworks.com").matches("jez.humble"), Matchers.is(false));
    }

    @Test
    public void shouldEscapeRegexes() throws Exception {
        Assert.assertThat(new Matcher("[").matches("["), Matchers.is(true));
        Assert.assertThat(new Matcher("]").matches("]]"), Matchers.is(true));
        Assert.assertThat(new Matcher("\\").matches("\\\\"), Matchers.is(true));
        Assert.assertThat(new Matcher("^^").matches("^^"), Matchers.is(true));
        Assert.assertThat(new Matcher("$").matches("$$"), Matchers.is(true));
        Assert.assertThat(new Matcher("..").matches("..."), Matchers.is(true));
        Assert.assertThat(new Matcher("|||").matches("||||"), Matchers.is(true));
        Assert.assertThat(new Matcher("??").matches("???"), Matchers.is(true));
        Assert.assertThat(new Matcher("**").matches("**"), Matchers.is(true));
        Assert.assertThat(new Matcher("++").matches("++"), Matchers.is(true));
        Assert.assertThat(new Matcher("((").matches("((("), Matchers.is(true));
        Assert.assertThat(new Matcher("))").matches(")))"), Matchers.is(true));
    }

    @Test
    public void shouldNotMatchAnyThing() throws Exception {
        Assert.assertThat(new Matcher("").matches("ja"), Matchers.is(false));
    }

    @Test
    public void shouldValidateAllMatchersUsingAValidator() throws Exception {
        new Matcher(new String[]{ "aaa,a" }).validateUsing(Validator.lengthValidator(200));
    }

    @Test
    public void shouldMatchInMultiLineText() throws Exception {
        Assert.assertThat(new Matcher("abc").matches("abc def\nghi jkl"), Matchers.is(true));
        Assert.assertThat(new Matcher("ghi").matches("abc def\nghi jkl"), Matchers.is(true));
    }
}

