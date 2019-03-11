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
package org.axonframework.commandhandling.distributed;


import org.axonframework.commandhandling.GenericCommandResultMessage;
import org.junit.Assert;
import org.junit.Test;


public class CommandCallbackRepositoryTest {
    private int successCounter;

    private int failCounter;

    @Test
    public void testCallback() {
        CommandCallbackRepository<Object> repository = new CommandCallbackRepository();
        CommandCallbackWrapper<Object, Object, Object> commandCallbackWrapper = createWrapper("A");
        repository.store("A", commandCallbackWrapper);
        Assert.assertEquals(1, repository.callbacks().size());
        CommandCallbackWrapper<Object, Object, Object> fetchedCallback = repository.fetchAndRemove("A");
        Assert.assertEquals(commandCallbackWrapper, fetchedCallback);
        Assert.assertEquals(0, repository.callbacks().size());
        fetchedCallback.reportResult(GenericCommandResultMessage.asCommandResultMessage(new Object()));
        Assert.assertEquals(1, successCounter);
    }

    @Test
    public void testOverwriteCallback() {
        CommandCallbackRepository<Object> repository = new CommandCallbackRepository();
        CommandCallbackWrapper<Object, Object, Object> commandCallbackWrapper = createWrapper("A");
        repository.store("A", commandCallbackWrapper);
        Assert.assertEquals(1, repository.callbacks().size());
        CommandCallbackWrapper<Object, Object, Object> commandCallbackWrapper2 = createWrapper("A");
        repository.store("A", commandCallbackWrapper2);
        Assert.assertEquals(1, failCounter);
        Assert.assertTrue(repository.callbacks().containsValue(commandCallbackWrapper2));
        Assert.assertFalse(repository.callbacks().containsValue(commandCallbackWrapper));
    }

    @Test
    public void testCancelCallbacks() {
        CommandCallbackRepository<Object> repository = new CommandCallbackRepository();
        repository.store("A", createWrapper("A"));
        repository.store("B", createWrapper("A"));
        repository.store("C", createWrapper("A"));
        repository.store("D", createWrapper("B"));
        Assert.assertEquals(4, repository.callbacks().size());
        repository.cancelCallbacks("C");
        Assert.assertEquals(4, repository.callbacks().size());
        repository.cancelCallbacks("A");
        Assert.assertEquals(1, repository.callbacks().size());
        Assert.assertEquals(3, failCounter);
        Assert.assertEquals(0, successCounter);
    }
}

