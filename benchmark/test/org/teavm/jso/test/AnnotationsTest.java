/**
 * Copyright 2016 Alexey Andreev.
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
package org.teavm.jso.test;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@SkipJVM
public class AnnotationsTest {
    @Test
    public void staticBodyWorks() {
        Assert.assertEquals(12, AnnotationsTest.add(5, 7));
    }

    @Test
    public void memberBodyWorks() {
        Assert.assertEquals(12, AnnotationsTest.convert(AnnotationsTest.convert(5).add(AnnotationsTest.convert(7))));
    }

    @Test
    public void abstractWrapperWorks() {
        AnnotationsTest.AbstractWrapper obj = AnnotationsTest.AbstractWrapper.create(5);
        Assert.assertEquals(5, obj.getValue());
        Assert.assertEquals(12, obj.testMethod(6));
        Assert.assertEquals(13, obj.renamedMethod(6));
        Assert.assertEquals(25, obj.javaMethod(6));
    }

    @Test
    public void interfaceWrapperWorks() {
        AnnotationsTest.InterfaceWrapper obj = AnnotationsTest.createWrapper(5);
        Assert.assertEquals(5, obj.getValue());
        Assert.assertEquals(12, obj.testMethod(6));
        Assert.assertEquals(13, obj.renamedMethod(6));
    }

    abstract static class Num implements JSObject {
        @JSBody(params = "other", script = "return this + other;")
        public native final AnnotationsTest.Num add(AnnotationsTest.Num other);
    }

    abstract static class AbstractWrapper implements JSObject {
        private AbstractWrapper() {
        }

        @JSProperty
        public abstract int getValue();

        public abstract int testMethod(int num);

        @JSMethod("renamedJSMethod")
        public abstract int renamedMethod(int num);

        public final int javaMethod(int num) {
            return (testMethod(num)) + (renamedMethod(num));
        }

        @JSBody(params = "value", script = "" + (((("return {" + "'value' : value, ") + "testMethod : function(num) { return this.value + num + 1; }, ") + "renamedJSMethod : function(num) { return this.value + num + 2; }") + "};"))
        public static native AnnotationsTest.AbstractWrapper create(int value);
    }

    interface InterfaceWrapper extends JSObject {
        @JSProperty
        int getValue();

        int testMethod(int num);

        @JSMethod("renamedJSMethod")
        int renamedMethod(int num);
    }
}

