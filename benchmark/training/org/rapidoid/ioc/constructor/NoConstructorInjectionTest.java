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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ioc.AbstractInjectTest;
import org.rapidoid.ioc.IoC;


@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class NoConstructorInjectionTest extends AbstractInjectTest {
    static class A {}

    class XYZ {
        final NoConstructorInjectionTest.A a;

        public XYZ(NoConstructorInjectionTest.A a) {
            this.a = a;
        }
    }

    @Test
    public void shouldFailOnNoAnnotatedConstructors() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> IoC.singleton(NoConstructorInjectionTest.XYZ.class));
    }
}

