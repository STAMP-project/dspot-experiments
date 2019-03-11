/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.messaging.unitofwork;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class CurrentUnitOfWorkTest {
    @Test(expected = IllegalStateException.class)
    public void testGetSession_NoCurrentSession() {
        CurrentUnitOfWork.get();
    }

    @Test
    public void testSetSession() {
        UnitOfWork<?> mockUnitOfWork = Mockito.mock(UnitOfWork.class);
        CurrentUnitOfWork.set(mockUnitOfWork);
        Assert.assertSame(mockUnitOfWork, CurrentUnitOfWork.get());
        CurrentUnitOfWork.clear(mockUnitOfWork);
        Assert.assertFalse(CurrentUnitOfWork.isStarted());
    }

    @Test
    public void testNotCurrentUnitOfWorkCommitted() {
        DefaultUnitOfWork<?> outerUoW = new DefaultUnitOfWork(null);
        outerUoW.start();
        new DefaultUnitOfWork(null).start();
        try {
            outerUoW.commit();
        } catch (IllegalStateException e) {
            return;
        }
        throw new AssertionError("The unit of work is not the current");
    }
}

