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
package org.apache.zeppelin.interpreter.remote;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.zeppelin.display.AngularObjectRegistryListener;
import org.apache.zeppelin.interpreter.AbstractInterpreterTest;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterSetting;
import org.junit.Assert;
import org.junit.Test;


public class RemoteAngularObjectTest extends AbstractInterpreterTest implements AngularObjectRegistryListener {
    private RemoteInterpreter intp;

    private InterpreterContext context;

    private RemoteAngularObjectRegistry localRegistry;

    private InterpreterSetting interpreterSetting;

    private AtomicInteger onAdd;

    private AtomicInteger onUpdate;

    private AtomicInteger onRemove;

    @Test
    public void testAngularObjectInterpreterSideCRUD() throws InterruptedException, InterpreterException {
        InterpreterResult ret = intp.interpret("get", context);
        Thread.sleep(500);// waitFor eventpoller pool event

        String[] result = ret.message().get(0).getData().split(" ");
        Assert.assertEquals("0", result[0]);// size of registry

        Assert.assertEquals("0", result[1]);// num watcher called

        // create object
        ret = intp.interpret("add n1 v1", context);
        Thread.sleep(500);
        result = ret.message().get(0).getData().split(" ");
        Assert.assertEquals("1", result[0]);// size of registry

        Assert.assertEquals("0", result[1]);// num watcher called

        Assert.assertEquals("v1", localRegistry.get("n1", "note", null).get());
        // update object
        ret = intp.interpret("update n1 v11", context);
        result = ret.message().get(0).getData().split(" ");
        Thread.sleep(500);
        Assert.assertEquals("1", result[0]);// size of registry

        Assert.assertEquals("1", result[1]);// num watcher called

        Assert.assertEquals("v11", localRegistry.get("n1", "note", null).get());
        // remove object
        ret = intp.interpret("remove n1", context);
        result = ret.message().get(0).getData().split(" ");
        Thread.sleep(500);
        Assert.assertEquals("0", result[0]);// size of registry

        Assert.assertEquals("1", result[1]);// num watcher called

        Assert.assertEquals(null, localRegistry.get("n1", "note", null));
    }

    @Test
    public void testAngularObjectRemovalOnZeppelinServerSide() throws InterruptedException, InterpreterException {
        // test if angularobject removal from server side propagate to interpreter process's registry.
        // will happen when notebook is removed.
        InterpreterResult ret = intp.interpret("get", context);
        Thread.sleep(500);// waitFor eventpoller pool event

        String[] result = ret.message().get(0).getData().split(" ");
        Assert.assertEquals("0", result[0]);// size of registry

        // create object
        ret = intp.interpret("add n1 v1", context);
        Thread.sleep(500);
        result = ret.message().get(0).getData().split(" ");
        Assert.assertEquals("1", result[0]);// size of registry

        Assert.assertEquals("v1", localRegistry.get("n1", "note", null).get());
        // remove object in local registry.
        localRegistry.removeAndNotifyRemoteProcess("n1", "note", null);
        ret = intp.interpret("get", context);
        Thread.sleep(500);// waitFor eventpoller pool event

        result = ret.message().get(0).getData().split(" ");
        Assert.assertEquals("0", result[0]);// size of registry

    }

    @Test
    public void testAngularObjectAddOnZeppelinServerSide() throws InterruptedException, InterpreterException {
        // test if angularobject add from server side propagate to interpreter process's registry.
        // will happen when zeppelin server loads notebook and restore the object into registry
        InterpreterResult ret = intp.interpret("get", context);
        Thread.sleep(500);// waitFor eventpoller pool event

        String[] result = ret.message().get(0).getData().split(" ");
        Assert.assertEquals("0", result[0]);// size of registry

        // create object
        localRegistry.addAndNotifyRemoteProcess("n1", "v1", "note", null);
        // get from remote registry
        ret = intp.interpret("get", context);
        Thread.sleep(500);// waitFor eventpoller pool event

        result = ret.message().get(0).getData().split(" ");
        Assert.assertEquals("1", result[0]);// size of registry

    }
}

