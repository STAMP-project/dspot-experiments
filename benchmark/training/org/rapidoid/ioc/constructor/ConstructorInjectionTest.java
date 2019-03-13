/**
 * -
 * #%L
 * rapidoid-inject
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.rapidoid.ioc.constructor;


import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ioc.AbstractInjectTest;
import org.rapidoid.ioc.IoC;


@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ConstructorInjectionTest extends AbstractInjectTest {
    static class A {
        A() {
        }
    }

    class XYZ {
        final ConstructorInjectionTest.A x;

        private final B b;

        String y;

        boolean z;

        @Inject
        public XYZ(ConstructorInjectionTest.A x, B b) {
            this.x = x;
            this.b = b;
        }

        public XYZ(ConstructorInjectionTest.A x, String y, boolean z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.b = null;
        }
    }

    @Test
    public void shouldInjectUsingConstructor() {
        ConstructorInjectionTest.XYZ xyz = IoC.singleton(ConstructorInjectionTest.XYZ.class);
        ConstructorInjectionTest.XYZ xyz2 = IoC.singleton(ConstructorInjectionTest.XYZ.class);
        notNull(xyz.x);
        notNull(xyz.b);
        isTrue((xyz == xyz2));
    }
}

