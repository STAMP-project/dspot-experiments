/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.messageprocessors;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import org.graylog2.plugin.Messages;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.messageprocessors.MessageProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class OrderedMessageProcessorsTest {
    private OrderedMessageProcessors orderedMessageProcessors;

    private ClusterConfigService clusterConfigService;

    @Test
    public void testIterator() throws Exception {
        final Iterator<MessageProcessor> iterator = orderedMessageProcessors.iterator();
        Assert.assertEquals("A is first", OrderedMessageProcessorsTest.A.class, iterator.next().getClass());
        Assert.assertEquals("B is last", OrderedMessageProcessorsTest.B.class, iterator.next().getClass());
        Assert.assertFalse("Iterator exhausted", iterator.hasNext());
        Mockito.when(clusterConfigService.get(MessageProcessorsConfig.class)).thenReturn(MessageProcessorsConfig.create(Lists.newArrayList(OrderedMessageProcessorsTest.B.class.getCanonicalName(), OrderedMessageProcessorsTest.A.class.getCanonicalName())));
        orderedMessageProcessors.handleOrderingUpdate(getClusterConfigChangedEvent());
        final Iterator<MessageProcessor> it2 = orderedMessageProcessors.iterator();
        Assert.assertEquals("B is first", OrderedMessageProcessorsTest.B.class, it2.next().getClass());
        Assert.assertEquals("A is last", OrderedMessageProcessorsTest.A.class, it2.next().getClass());
        Assert.assertFalse("Iterator exhausted", it2.hasNext());
        Mockito.when(clusterConfigService.get(MessageProcessorsConfig.class)).thenReturn(MessageProcessorsConfig.create(Lists.newArrayList(OrderedMessageProcessorsTest.B.class.getCanonicalName(), OrderedMessageProcessorsTest.A.class.getCanonicalName()), Sets.newHashSet(OrderedMessageProcessorsTest.B.class.getCanonicalName())));
        orderedMessageProcessors.handleOrderingUpdate(getClusterConfigChangedEvent());
        final Iterator<MessageProcessor> it3 = orderedMessageProcessors.iterator();
        Assert.assertEquals("A is only element", OrderedMessageProcessorsTest.A.class, it3.next().getClass());
        Assert.assertFalse("Iterator exhausted", it3.hasNext());
    }

    private static class A implements MessageProcessor {
        @Override
        public Messages process(Messages messages) {
            return null;
        }
    }

    private static class B implements MessageProcessor {
        @Override
        public Messages process(Messages messages) {
            return null;
        }
    }
}

