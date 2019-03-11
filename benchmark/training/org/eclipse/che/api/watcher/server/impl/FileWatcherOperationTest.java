/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.watcher.server.impl;


import java.nio.file.StandardWatchEventKinds;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link FileWatcherOperation}
 */
@RunWith(MockitoJUnitRunner.class)
public class FileWatcherOperationTest {
    private static final int ID = 0;

    @Mock
    Consumer<String> create;

    @Mock
    Consumer<String> modify;

    @Mock
    Consumer<String> delete;

    FileWatcherOperation operation;

    @Test
    public void shouldProperlyGetCreateEventKindConsumer() throws Exception {
        Optional<Consumer<String>> consumer = operation.get(StandardWatchEventKinds.ENTRY_CREATE);
        Assert.assertTrue(consumer.isPresent());
        if (consumer.isPresent()) {
            Consumer<String> actual = consumer.get();
            Assert.assertEquals(create, actual);
        }
    }

    @Test
    public void shouldProperlyGetModifyEventKindConsumer() throws Exception {
        Optional<Consumer<String>> consumer = operation.get(StandardWatchEventKinds.ENTRY_MODIFY);
        Assert.assertTrue(consumer.isPresent());
        if (consumer.isPresent()) {
            Consumer<String> actual = consumer.get();
            Assert.assertEquals(modify, actual);
        }
    }

    @Test
    public void shouldProperlyGetDeleteEventKindConsumer() throws Exception {
        Optional<Consumer<String>> consumer = operation.get(StandardWatchEventKinds.ENTRY_DELETE);
        Assert.assertTrue(consumer.isPresent());
        if (consumer.isPresent()) {
            Consumer<String> actual = consumer.get();
            Assert.assertEquals(delete, actual);
        }
    }
}

