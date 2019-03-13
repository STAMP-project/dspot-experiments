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
package com.hazelcast.internal.management;


import MapService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.management.operation.ScriptExecutorOperation;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.security.AccessControlException;
import java.util.concurrent.ExecutionException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


/**
 * Tests possibility to disable scripting on members.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ScriptingProtectionTest extends HazelcastTestSupport {
    private static final String SCRIPT_RETURN_VAL = "John";

    private static final String SCRIPT = ("\"" + (ScriptingProtectionTest.SCRIPT_RETURN_VAL)) + "\"";

    // Let's use Groovy on classpath, because Azul Zulu 6-7 doesn't include the JavaScript engine (Rhino)
    private static final String ENGINE = "groovy";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testScritpingDisabled() throws InterruptedException, ExecutionException {
        testInternal(false, false);
    }

    @Test
    public void testScritpingDisabledOnSrc() throws InterruptedException, ExecutionException {
        testInternal(false, true);
    }

    @Test
    public void testScritpingDisabledOnDest() throws InterruptedException, ExecutionException {
        testInternal(true, false);
    }

    @Test
    public void testScritpingEnabled() throws InterruptedException, ExecutionException {
        testInternal(true, true);
    }

    @Test
    public void testDefaultValue() throws InterruptedException, ExecutionException {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        ScriptExecutorOperation op = createScriptExecutorOp();
        InternalCompletableFuture<Object> result = HazelcastTestSupport.getOperationService(hz1).invokeOnTarget(SERVICE_NAME, op, HazelcastTestSupport.getAddress(hz2));
        if (!(getScriptingEnabledDefaultValue())) {
            expectedException.expect(ExecutionException.class);
            expectedException.expectCause(CoreMatchers.<Throwable>instanceOf(AccessControlException.class));
        }
        Assert.assertEquals(ScriptingProtectionTest.SCRIPT_RETURN_VAL, result.get());
    }
}

