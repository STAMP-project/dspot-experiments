/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.internal.schedulers;


import org.junit.Assert;
import org.junit.Test;


public class ComputationSchedulerInternalTest {
    @Test
    public void capPoolSize() {
        Assert.assertEquals(8, ComputationScheduler.cap(8, (-1)));
        Assert.assertEquals(8, ComputationScheduler.cap(8, 0));
        Assert.assertEquals(4, ComputationScheduler.cap(8, 4));
        Assert.assertEquals(8, ComputationScheduler.cap(8, 8));
        Assert.assertEquals(8, ComputationScheduler.cap(8, 9));
        Assert.assertEquals(8, ComputationScheduler.cap(8, 16));
    }
}

