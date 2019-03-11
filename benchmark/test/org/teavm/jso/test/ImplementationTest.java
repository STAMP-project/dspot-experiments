/**
 * Copyright 2015 Alexey Andreev.
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
import org.teavm.jso.JSObject;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@SkipJVM
public class ImplementationTest {
    @Test
    public void respectsPrecedence() {
        Assert.assertEquals(12, ImplementationTest.mul(ImplementationTest.add(2, 2), 3));
        Assert.assertEquals(8, ImplementationTest.add(2, ImplementationTest.mul(2, 3)));
    }

    @Test
    public void inliningUsageCounterWorksProperly() {
        ImplementationTest.ForInliningTest instance = ImplementationTest.ForInliningTest.create();
        ImplementationTest.wrongInlineCandidate(instance.foo());
        Assert.assertEquals(1, instance.counter);
    }

    static class ForInliningTest implements JSObject {
        public int counter;

        public ImplementationTest.ForInliningTest foo() {
            ++(counter);
            return this;
        }

        public static ImplementationTest.ForInliningTest create() {
            return new ImplementationTest.ForInliningTest();
        }
    }
}

