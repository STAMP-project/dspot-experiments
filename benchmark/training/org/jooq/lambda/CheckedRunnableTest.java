/**
 * Copyright (c), Data Geekery GmbH, contact@datageekery.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jooq.lambda;


import Unchecked.RETHROW_ALL;
import java.util.function.Consumer;
import org.jooq.lambda.fi.lang.CheckedRunnable;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class CheckedRunnableTest {
    @Test
    public void testCheckedRunnable() {
        final CheckedRunnable runnable = () -> {
            throw new Exception("runnable");
        };
        Runnable r1 = Unchecked.runnable(runnable);
        Runnable r2 = CheckedRunnable.unchecked(runnable);
        Runnable r3 = Sneaky.runnable(runnable);
        Runnable r4 = CheckedRunnable.sneaky(runnable);
        assertRunnable(r1, UncheckedException.class);
        assertRunnable(r2, UncheckedException.class);
        assertRunnable(r3, Exception.class);
        assertRunnable(r4, Exception.class);
    }

    @Test
    public void testCheckedRunnableWithCustomHandler() {
        final CheckedRunnable runnable = () -> {
            throw new Exception("runnable");
        };
        final Consumer<Throwable> handler = ( e) -> {
            throw new IllegalStateException(e);
        };
        Runnable test = Unchecked.runnable(runnable, handler);
        Runnable alias = CheckedRunnable.unchecked(runnable, handler);
        assertRunnable(test, IllegalStateException.class);
        assertRunnable(alias, IllegalStateException.class);
    }

    @Test
    public void testCheckedRunnableRethrowAll() {
        Runnable test = Unchecked.runnable(() -> {
            throw new Throwable("runnable");
        }, RETHROW_ALL);
        try {
            test.run();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals("runnable", e.getMessage());
        }
    }
}

