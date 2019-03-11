/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.server.dispatcher;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.dispatcher.Command;
import org.wildfly.clustering.dispatcher.CommandDispatcher;
import org.wildfly.clustering.dispatcher.CommandDispatcherException;
import org.wildfly.clustering.group.Node;


/**
 * Unit test for {@link ManagedCommandDispatcher}.
 *
 * @author Paul Ferraro
 */
public class ManagedCommandDispatcherTestCase {
    @Test
    public void test() throws CommandDispatcherException {
        CommandDispatcher<String> dispatcher = Mockito.mock(CommandDispatcher.class);
        Runnable closeTask = Mockito.mock(Runnable.class);
        String context = "context";
        try (CommandDispatcher<String> subject = new ManagedCommandDispatcher(dispatcher, closeTask)) {
            Mockito.when(dispatcher.getContext()).thenReturn(context);
            Assert.assertSame(context, subject.getContext());
            Command<Void, String> command = Mockito.mock(Command.class);
            Node node = Mockito.mock(Node.class);
            Node[] nodes = new Node[]{ node };
            CompletionStage<Void> stage = Mockito.mock(CompletionStage.class);
            Map<Node, CompletionStage<Void>> stages = Collections.singletonMap(node, stage);
            Mockito.when(dispatcher.executeOnGroup(command, nodes)).thenReturn(stages);
            Assert.assertSame(stages, subject.executeOnGroup(command, nodes));
            Mockito.when(dispatcher.executeOnMember(command, node)).thenReturn(stage);
            Assert.assertSame(stage, subject.executeOnMember(command, node));
        }
        Mockito.verify(dispatcher, Mockito.never()).close();
        Mockito.verify(closeTask).run();
    }
}

