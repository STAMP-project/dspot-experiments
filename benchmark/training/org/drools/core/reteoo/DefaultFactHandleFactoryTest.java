/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
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
 */
package org.drools.core.reteoo;


import org.drools.core.common.DefaultFactHandle;
import org.junit.Assert;
import org.junit.Test;


public class DefaultFactHandleFactoryTest {
    /* Class under test for FactHandle newFactHandle() */
    @Test
    public void testNewFactHandle() {
        final ReteooFactHandleFactory factory = new ReteooFactHandleFactory();
        DefaultFactHandle handle = ((DefaultFactHandle) (factory.newFactHandle("cheese", null, null, null)));
        Assert.assertEquals(1, handle.getId());
        Assert.assertEquals(1, handle.getRecency());
        // issue  new handle
        handle = ((DefaultFactHandle) (factory.newFactHandle("cheese", null, null, null)));
        Assert.assertEquals(2, handle.getId());
        Assert.assertEquals(2, handle.getRecency());
        // issue  new handle, under a different reference so we  can destroy later
        final DefaultFactHandle handle2 = ((DefaultFactHandle) (factory.newFactHandle("cheese", null, null, null)));
        Assert.assertEquals(3, handle2.getId());
        Assert.assertEquals(3, handle2.getRecency());
        // Check recency increasion works
        factory.increaseFactHandleRecency(handle);
        Assert.assertEquals(4, handle.getRecency());
        // issue new handle and make sure  recency is still inline
        handle = ((DefaultFactHandle) (factory.newFactHandle("cheese", null, null, null)));
        Assert.assertEquals(4, handle.getId());
        Assert.assertEquals(5, handle.getRecency());
        // destroy handle
        factory.destroyFactHandle(handle2);
        // @FIXME recycling is currently disabled
        // // issue  new  fact handle and  make sure it  recycled the  id=2
        // handle = (DefaultFactHandle) factory.newFactHandle( "cheese", false, null );
        // assertEquals( 2,
        // handle.getId() );
        // assertEquals( 5,
        // handle.getRecency() );
        // 
        // // issue new  handle  making  sure it correctly resumes  ids  and recency
        // handle = (DefaultFactHandle) factory.newFactHandle( "cheese", false, null );
        // assertEquals( 4,
        // handle.getId() );
        // assertEquals( 6,
        // handle.getRecency() );
    }
}

