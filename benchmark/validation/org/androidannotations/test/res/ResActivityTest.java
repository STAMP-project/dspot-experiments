/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test.res;


import R.string.hello_html;
import android.text.Html;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ResActivityTest {
    private ResActivity_ activity;

    @Test
    public void stringSnakeCaseInjected() {
        assertThat(activity.injected_string).isEqualTo("test");
    }

    @Test
    public void stringCamelCaseInjected() {
        assertThat(activity.injectedString).isEqualTo("test");
    }

    @Test
    public void methodInjectedStringNotNull() {
        Assert.assertNotNull(activity.methodInjectedString);
    }

    @Test
    public void multiInjectedStringNotNull() {
        Assert.assertNotNull(activity.multiInjectedString);
    }

    @Test
    public void animNotNull() {
        assertThat(activity.fadein).isNotNull();
    }

    @Test
    public void methodInjectedAnimationNotNull() {
        Assert.assertNotNull(activity.methodInjectedAnimation);
    }

    @Test
    public void multiInjectedAnimationNotNull() {
        Assert.assertNotNull(activity.multiInjectedAnimation);
    }

    @Test
    public void drawableResNotNull() {
        Assert.assertNotNull(activity.icon);
    }

    @Test
    public void methodInjectedDrawableNotNull() {
        Assert.assertNotNull(activity.methodInjectedDrawable);
    }

    @Test
    public void multiInjectedDrawableNotNull() {
        Assert.assertNotNull(activity.multiInjectedDrawable);
    }

    @Test
    public void htmlResNotNull() {
        Assert.assertNotNull(activity.helloHtml);
    }

    @Test
    public void htmlInjectedNotNull() {
        Assert.assertNotNull(activity.htmlInjected);
    }

    @Test
    public void htmlResCorrectlySet() {
        Assert.assertEquals(Html.fromHtml(activity.getString(hello_html)).toString(), activity.helloHtml.toString());
    }

    @Test
    public void htmlInjectedCorrectlySet() {
        Assert.assertEquals(Html.fromHtml(activity.getString(hello_html)).toString(), activity.htmlInjected.toString());
    }

    @Test
    public void methodInjectedHtmlNotNull() {
        Assert.assertNotNull(activity.methodInjectedHtml);
    }

    @Test
    public void multiInjectedHtmlNotNull() {
        Assert.assertNotNull(activity.multiInjectedHtml);
    }
}

