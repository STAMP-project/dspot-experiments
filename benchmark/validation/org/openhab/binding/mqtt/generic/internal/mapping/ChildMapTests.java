/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.mqtt.generic.internal.mapping;


import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.eclipse.jdt.annotation.Nullable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.DeviceCallback;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.Node;
import org.openhab.binding.mqtt.generic.internal.handler.ThingChannelConstants;
import org.openhab.binding.mqtt.generic.internal.tools.ChildMap;


/**
 * Tests cases for {@link ChildMap}.
 *
 * @author David Graeff - Initial contribution
 */
public class ChildMapTests {
    @Mock
    private DeviceCallback callback;

    private final String deviceID = ThingChannelConstants.testHomieThing.getId();

    private final String deviceTopic = "homie/" + (deviceID);

    // A completed future is returned for a subscribe call to the attributes
    final CompletableFuture<@Nullable
    Void> future = CompletableFuture.completedFuture(null);

    ChildMap<Node> subject = new ChildMap();

    public static class AddedAction implements Function<Node, CompletableFuture<Void>> {
        @Override
        public CompletableFuture<Void> apply(Node t) {
            return CompletableFuture.completedFuture(null);
        }
    }

    @Test
    public void testArrayToSubtopicCreateAndRemove() {
        ChildMapTests.AddedAction addedAction = Mockito.spy(new ChildMapTests.AddedAction());
        // Assign "abc,def" to the
        subject.apply(new String[]{ "abc", "def" }, addedAction, this::createNode, this::removedNode);
        Assert.assertThat(future.isDone(), CoreMatchers.is(true));
        Assert.assertThat(subject.get("abc").nodeID, CoreMatchers.is("abc"));
        Assert.assertThat(subject.get("def").nodeID, CoreMatchers.is("def"));
        Mockito.verify(addedAction, Mockito.times(2)).apply(ArgumentMatchers.any());
        Node soonToBeRemoved = subject.get("def");
        subject.apply(new String[]{ "abc" }, addedAction, this::createNode, this::removedNode);
        Mockito.verify(callback).nodeRemoved(ArgumentMatchers.eq(soonToBeRemoved));
    }
}

