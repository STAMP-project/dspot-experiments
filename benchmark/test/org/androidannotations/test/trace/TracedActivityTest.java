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
package org.androidannotations.test.trace;


import android.net.Uri;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class TracedActivityTest {
    private TracedActivity activity;

    @Test
    public void servicesAreInjected() throws IOException {
        assertThat(activity.tracedMethodCalled).isFalse();
        activity.tracedMethod(null, null);
        assertThat(activity.tracedMethodCalled).isTrue();
        assertThat(activity.voidTracedMethodCalled).isFalse();
        activity.voidTracedMethod(null, null);
        assertThat(activity.voidTracedMethodCalled).isTrue();
        assertThat(activity.voidTracedMethodDebugCalled).isFalse();
        activity.voidTracedMethodDebug();
        assertThat(activity.voidTracedMethodDebugCalled).isTrue();
        assertThat(activity.voidTracedMethodErrorCalled).isFalse();
        activity.voidTracedMethodError();
        assertThat(activity.voidTracedMethodErrorCalled).isTrue();
        assertThat(activity.voidTracedMethodInfoCalled).isFalse();
        activity.voidTracedMethodInfo();
        assertThat(activity.voidTracedMethodInfoCalled).isTrue();
        assertThat(activity.voidTracedMethodVerboseCalled).isFalse();
        activity.voidTracedMethodVerbose();
        assertThat(activity.voidTracedMethodVerboseCalled).isTrue();
        assertThat(activity.voidTracedMethodWarnCalled).isFalse();
        activity.voidTracedMethodWarn();
        assertThat(activity.voidTracedMethodWarnCalled).isTrue();
        assertThat(activity.overloadedMethodInt).isFalse();
        activity.overloadedMethod(0);
        assertThat(activity.overloadedMethodInt).isTrue();
        assertThat(activity.overloadedMethodIntFLoat).isFalse();
        activity.overloadedMethod(0, 0.0F);
        assertThat(activity.overloadedMethodIntFLoat).isTrue();
    }

    @Test
    public void noReturnNoParam() {
        activity.noReturnNoParam();
        Assert.assertTrue(TracedActivityTest.logContains("Entering [void noReturnNoParam()]"));
        Assert.assertTrue(TracedActivityTest.logContains("Exiting [void noReturnNoParam()], duration in ms: "));
    }

    @Test
    public void noReturnStringParam() {
        activity.noReturnStringParam("test");
        Assert.assertTrue(TracedActivityTest.logContains("Entering [void noReturnStringParam(param = test)]"));
        Assert.assertTrue(TracedActivityTest.logContains("Exiting [void noReturnStringParam(String)], duration in ms: "));
    }

    @Test
    public void noReturnIntArrayParam() {
        activity.noReturnIntArrayParam(new int[]{ 1, 2, 3 });
        Assert.assertTrue(TracedActivityTest.logContains("Entering [void noReturnIntArrayParam(param = [1, 2, 3])]"));
        Assert.assertTrue(TracedActivityTest.logContains("Exiting [void noReturnIntArrayParam(int[])], duration in ms: "));
    }

    @Test
    public void noReturnStringAndIntArrayParam() {
        activity.noReturnStringAndIntArrayParam("test", new int[]{ 1, 2, 3 });
        Assert.assertTrue(TracedActivityTest.logContains("Entering [void noReturnStringAndIntArrayParam(param1 = test, param2 = [1, 2, 3])]"));
        Assert.assertTrue(TracedActivityTest.logContains("Exiting [void noReturnStringAndIntArrayParam(String, int[])], duration in ms: "));
    }

    @Test
    public void noReturnIntentParam() {
        activity.noReturnIntentParam(new android.content.Intent("TEST", Uri.parse("http://www.androidannotations.org")));
        Assert.assertTrue(TracedActivityTest.logContains("Entering [void noReturnIntentParam(param = Intent { act=TEST dat=http://www.androidannotations.org })]"));
        Assert.assertTrue(TracedActivityTest.logContains("Exiting [void noReturnIntentParam(Intent)], duration in ms: "));
    }

    @Test
    public void booleanReturnNoParam() {
        activity.booleanReturnNoParam();
        Assert.assertTrue(TracedActivityTest.logContains("Entering [boolean booleanReturnNoParam()]"));
        Assert.assertTrue(TracedActivityTest.logContains("Exiting [boolean booleanReturnNoParam() returning: true], duration in ms: "));
    }

    @Test
    public void stringReturnStringParam() {
        activity.stringReturnStringParam("test");
        Assert.assertTrue(TracedActivityTest.logContains("Entering [java.lang.String stringReturnStringParam(param = test)]"));
        Assert.assertTrue(TracedActivityTest.logContains("Exiting [java.lang.String stringReturnStringParam(String) returning: test], duration in ms: "));
    }
}

