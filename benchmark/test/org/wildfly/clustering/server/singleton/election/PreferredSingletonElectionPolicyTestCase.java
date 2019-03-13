/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.wildfly.clustering.server.singleton.election;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.group.Node;
import org.wildfly.clustering.singleton.SingletonElectionPolicy;
import org.wildfly.clustering.singleton.election.Preference;


/**
 *
 *
 * @author Paul Ferraro
 */
public class PreferredSingletonElectionPolicyTestCase {
    @Test
    public void elect() {
        SingletonElectionPolicy policy = Mockito.mock(SingletonElectionPolicy.class);
        Preference preference1 = Mockito.mock(Preference.class);
        Preference preference2 = Mockito.mock(Preference.class);
        Node node1 = Mockito.mock(Node.class);
        Node node2 = Mockito.mock(Node.class);
        Node node3 = Mockito.mock(Node.class);
        Node node4 = Mockito.mock(Node.class);
        Mockito.when(preference1.preferred(ArgumentMatchers.same(node1))).thenReturn(true);
        Mockito.when(preference1.preferred(ArgumentMatchers.same(node2))).thenReturn(false);
        Mockito.when(preference1.preferred(ArgumentMatchers.same(node3))).thenReturn(false);
        Mockito.when(preference1.preferred(ArgumentMatchers.same(node4))).thenReturn(false);
        Mockito.when(preference2.preferred(ArgumentMatchers.same(node1))).thenReturn(false);
        Mockito.when(preference2.preferred(ArgumentMatchers.same(node2))).thenReturn(true);
        Mockito.when(preference2.preferred(ArgumentMatchers.same(node3))).thenReturn(false);
        Mockito.when(preference2.preferred(ArgumentMatchers.same(node4))).thenReturn(false);
        Assert.assertSame(node1, new org.wildfly.clustering.singleton.election.PreferredSingletonElectionPolicy(policy, preference1, preference2).elect(Arrays.asList(node1, node2, node3, node4)));
        Assert.assertSame(node1, new org.wildfly.clustering.singleton.election.PreferredSingletonElectionPolicy(policy, preference1, preference2).elect(Arrays.asList(node4, node3, node2, node1)));
        Assert.assertSame(node2, new org.wildfly.clustering.singleton.election.PreferredSingletonElectionPolicy(policy, preference1, preference2).elect(Arrays.asList(node2, node3, node4)));
        Assert.assertSame(node2, new org.wildfly.clustering.singleton.election.PreferredSingletonElectionPolicy(policy, preference1, preference2).elect(Arrays.asList(node4, node3, node2)));
        List<Node> nodes = Arrays.asList(node3, node4);
        Mockito.when(policy.elect(nodes)).thenReturn(node3);
        Assert.assertSame(node3, new org.wildfly.clustering.singleton.election.PreferredSingletonElectionPolicy(policy, preference1, preference2).elect(nodes));
        Mockito.when(policy.elect(nodes)).thenReturn(node4);
        Assert.assertSame(node4, new org.wildfly.clustering.singleton.election.PreferredSingletonElectionPolicy(policy, preference1, preference2).elect(nodes));
        Mockito.when(policy.elect(nodes)).thenReturn(null);
        Assert.assertNull(new org.wildfly.clustering.singleton.election.PreferredSingletonElectionPolicy(policy, preference1, preference2).elect(nodes));
    }
}

