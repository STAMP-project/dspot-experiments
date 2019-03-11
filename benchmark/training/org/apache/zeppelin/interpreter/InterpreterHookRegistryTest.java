/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.interpreter;


import org.junit.Assert;
import org.junit.Test;

import static InterpreterHookRegistry.GLOBAL_KEY;


public class InterpreterHookRegistryTest {
    @Test
    public void testBasic() throws InvalidHookException {
        final String GLOBAL_KEY = GLOBAL_KEY;
        final String noteId = "note";
        final String className = "class";
        final String preExecHook = "pre";
        final String postExecHook = "post";
        InterpreterHookRegistry registry = new InterpreterHookRegistry();
        // Test register()
        registry.register(noteId, className, HookType.PRE_EXEC.getName(), preExecHook);
        registry.register(noteId, className, HookType.POST_EXEC.getName(), postExecHook);
        registry.register(noteId, className, HookType.PRE_EXEC_DEV.getName(), preExecHook);
        registry.register(noteId, className, HookType.POST_EXEC_DEV.getName(), postExecHook);
        // Test get()
        Assert.assertEquals(registry.get(noteId, className, HookType.PRE_EXEC.getName()), preExecHook);
        Assert.assertEquals(registry.get(noteId, className, HookType.POST_EXEC.getName()), postExecHook);
        Assert.assertEquals(registry.get(noteId, className, HookType.PRE_EXEC_DEV.getName()), preExecHook);
        Assert.assertEquals(registry.get(noteId, className, HookType.POST_EXEC_DEV.getName()), postExecHook);
        // Test Unregister
        registry.unregister(noteId, className, HookType.PRE_EXEC.getName());
        registry.unregister(noteId, className, HookType.POST_EXEC.getName());
        registry.unregister(noteId, className, HookType.PRE_EXEC_DEV.getName());
        registry.unregister(noteId, className, HookType.POST_EXEC_DEV.getName());
        Assert.assertNull(registry.get(noteId, className, HookType.PRE_EXEC.getName()));
        Assert.assertNull(registry.get(noteId, className, HookType.POST_EXEC.getName()));
        Assert.assertNull(registry.get(noteId, className, HookType.PRE_EXEC_DEV.getName()));
        Assert.assertNull(registry.get(noteId, className, HookType.POST_EXEC_DEV.getName()));
        // Test Global Scope
        registry.register(null, className, HookType.PRE_EXEC.getName(), preExecHook);
        Assert.assertEquals(registry.get(GLOBAL_KEY, className, HookType.PRE_EXEC.getName()), preExecHook);
    }

    @Test(expected = InvalidHookException.class)
    public void testValidEventCode() throws InvalidHookException {
        InterpreterHookRegistry registry = new InterpreterHookRegistry();
        // Test that only valid event codes ("pre_exec", "post_exec") are accepted
        registry.register("foo", "bar", "baz", "whatever");
    }
}

