/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.spi.impl.proxyservice.impl;


import com.hazelcast.core.DistributedObject;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DistributedObjectFutureTest {
    private DistributedObject object = Mockito.mock(DistributedObjectFutureTest.InitializingDistributedObject.class);

    private DistributedObjectFuture future = new DistributedObjectFuture();

    @Test
    public void isSet_returnsFalse_whenNotSet() throws Exception {
        Assert.assertFalse(future.isSetAndInitialized());
    }

    @Test
    public void isSet_returnsTrue_whenSet() throws Exception {
        future.set(object, true);
        Assert.assertTrue(future.isSetAndInitialized());
    }

    @Test
    public void isSet_returnsFalse_whenSetUninitialized() throws Exception {
        future.set(object, false);
        Assert.assertFalse(future.isSetAndInitialized());
    }

    @Test
    public void isSet_returnsTrue_whenErrorSet() throws Exception {
        future.setError(new Throwable());
        Assert.assertTrue(future.isSetAndInitialized());
    }

    @Test
    public void get_returnsObject_whenObjectSet() throws Exception {
        future.set(object, true);
        Assert.assertSame(object, future.get());
        InitializingObject initializingObject = ((InitializingObject) (object));
        Mockito.verify(initializingObject, Mockito.never()).initialize();
    }

    @Test
    public void get_returnsInitializedObject_whenUninitializedObjectSet() throws Exception {
        future.set(object, false);
        Assert.assertSame(object, future.get());
        InitializingObject initializingObject = ((InitializingObject) (object));
        Mockito.verify(initializingObject).initialize();
    }

    @Test
    public void get_throwsGivenException_whenUncheckedExceptionSet() throws Exception {
        Throwable error = new RuntimeException();
        future.setError(error);
        try {
            future.get();
        } catch (Exception e) {
            Assert.assertSame(error, e);
        }
    }

    @Test
    public void get_throwsWrappedException_whenCheckedExceptionSet() throws Exception {
        Throwable error = new Throwable();
        future.setError(error);
        try {
            future.get();
        } catch (Exception e) {
            Assert.assertSame(error, e.getCause());
        }
    }

    private interface InitializingDistributedObject extends DistributedObject , InitializingObject {}
}

