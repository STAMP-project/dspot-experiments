/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.html4j.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@SkipJVM
public class JavaScriptBodyTest {
    @Test
    public void readResource() throws IOException {
        InputStream is = JavaScriptBodyTest.class.getResourceAsStream("jvm.txt");
        Assert.assertNotNull("Resource jvm.txt found", is);
        try (BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
            String line = r.readLine();
            Assert.assertEquals("Line read", "TeaVM", line);
        }
    }

    @Test
    public void javaScriptBodyHandled() {
        Assert.assertEquals(23, simpleNativeMethod());
    }

    @Test
    public void dependencyPropagated() {
        A a = ((A) (returnValuePassed(new JavaScriptBodyTest.AImpl())));
        Assert.assertEquals(23, a.foo());
    }

    @Test
    public void dependencyPropagatedThroughProperty() {
        storeObject(new JavaScriptBodyTest.AImpl());
        A a = ((A) (retrieveObject()));
        Assert.assertEquals(23, a.foo());
    }

    @Test
    public void dependencyPropagatedThroughArray() {
        storeObject(new Object[]{ new JavaScriptBodyTest.AImpl() });
        A[] array = ((A[]) (retrieveObject()));
        Assert.assertEquals(23, array[0].foo());
    }

    @Test
    public void valuePropagatedToCallback() {
        A a = new JavaScriptBodyTest.AImpl();
        Assert.assertEquals(23, invokeCallback(a));
    }

    @Test
    public void staticCallbackInvoked() {
        Assert.assertEquals(23, invokeStaticCallback(new JavaScriptBodyTest.AImpl()));
    }

    @Test
    public void unusedArgumentIgnored() {
        int[] array = new int[1];
        invokeCallback(( input) -> {
            array[0] = 23;
        });
        Assert.assertEquals(23, array[0]);
    }

    private static class AImpl implements A {
        @Override
        public int foo() {
            return 23;
        }
    }
}

