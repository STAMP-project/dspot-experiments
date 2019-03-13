/**
 * -
 * #%L
 * rapidoid-integration-tests
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
package org.rapidoid.ioc;


import java.util.concurrent.Callable;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.IsolatedIntegrationTest;


@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
@Manage(MyCallable.class)
public class AutowireDependenciesTest extends IsolatedIntegrationTest {
    static class B {
        @Inject
        private Foo foo;
    }

    @Inject
    private Foo foo;

    @Inject
    Callable<?> callable;

    @Inject
    Callable<?> callable2;

    @Inject
    AutowireDependenciesTest.B b1;

    @Wired
    AutowireDependenciesTest.B b2;

    @Test
    public void shouldInjectManagedDependencies() {
        notNull(foo);
        notNull(b1);
        notNull(b2);
        notNull(b1.foo);
        notNull(b2.foo);
        notNull(b1.foo.callable);
        notNull(b2.foo.callable);
        isTrue(((foo) == (b2.foo)));
        isTrue(((b1.foo) == (b2.foo)));
        isTrue(((foo.callable) == (callable)));
        isTrue(((callable) == (callable2)));
    }
}

