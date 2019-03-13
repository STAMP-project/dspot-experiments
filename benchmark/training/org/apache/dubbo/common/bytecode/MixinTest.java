/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.bytecode;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MixinTest {
    @Test
    public void testMain() throws Exception {
        Mixin mixin = Mixin.mixin(new Class[]{ MixinTest.I1.class, MixinTest.I2.class, MixinTest.I3.class }, new Class[]{ MixinTest.C1.class, MixinTest.C2.class });
        Object o = mixin.newInstance(new Object[]{ new MixinTest.C1(), new MixinTest.C2() });
        Assertions.assertEquals((o instanceof MixinTest.I1), true);
        Assertions.assertEquals((o instanceof MixinTest.I2), true);
        Assertions.assertEquals((o instanceof MixinTest.I3), true);
        ((MixinTest.I1) (o)).m1();
        ((MixinTest.I2) (o)).m2();
        ((MixinTest.I3) (o)).m3();
    }

    interface I1 {
        void m1();
    }

    interface I2 {
        void m2();
    }

    interface I3 {
        void m3();
    }

    class C1 implements Mixin.MixinAware {
        public void m1() {
            System.out.println("c1.m1();");
        }

        public void m2() {
            System.out.println("c1.m2();");
        }

        public void setMixinInstance(Object mi) {
            System.out.println(("setMixinInstance:" + mi));
        }
    }

    class C2 implements Mixin.MixinAware {
        public void m3() {
            System.out.println("c2.m3();");
        }

        public void setMixinInstance(Object mi) {
            System.out.println(("setMixinInstance:" + mi));
        }
    }
}

