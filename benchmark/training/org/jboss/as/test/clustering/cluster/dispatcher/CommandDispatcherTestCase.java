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
package org.jboss.as.test.clustering.cluster.dispatcher;


import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.clustering.cluster.AbstractClusteringTestCase;
import org.jboss.as.test.clustering.cluster.dispatcher.bean.ClusterTopology;
import org.jboss.as.test.clustering.cluster.dispatcher.bean.ClusterTopologyRetriever;
import org.jboss.as.test.clustering.cluster.dispatcher.bean.ClusterTopologyRetrieverBean;
import org.jboss.as.test.clustering.ejb.EJBDirectory;
import org.jboss.as.test.clustering.ejb.RemoteEJBDirectory;
import org.jboss.as.test.shared.TimeoutUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class CommandDispatcherTestCase extends AbstractClusteringTestCase {
    private static final String MODULE_NAME = CommandDispatcherTestCase.class.getSimpleName();

    private static final long VIEW_CHANGE_WAIT = TimeoutUtil.adjust(2000);

    @Test
    public void test() throws Exception {
        try (EJBDirectory directory = new RemoteEJBDirectory(CommandDispatcherTestCase.MODULE_NAME)) {
            ClusterTopologyRetriever bean = directory.lookupStateless(ClusterTopologyRetrieverBean.class, ClusterTopologyRetriever.class);
            ClusterTopology topology = bean.getClusterTopology();
            Assert.assertEquals(2, topology.getNodes().size());
            Assert.assertTrue(topology.getNodes().toString(), topology.getNodes().contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertTrue(topology.getNodes().toString(), topology.getNodes().contains(AbstractClusteringTestCase.NODE_2));
            Assert.assertFalse((((topology.getRemoteNodes().toString()) + " should not contain ") + (topology.getLocalNode())), topology.getRemoteNodes().contains(topology.getLocalNode()));
            undeploy(AbstractClusteringTestCase.DEPLOYMENT_2);
            topology = bean.getClusterTopology();
            Assert.assertEquals(1, topology.getNodes().size());
            Assert.assertTrue(topology.getNodes().contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertEquals(AbstractClusteringTestCase.NODE_1, topology.getLocalNode());
            Assert.assertTrue(topology.getRemoteNodes().toString(), topology.getRemoteNodes().isEmpty());
            deploy(AbstractClusteringTestCase.DEPLOYMENT_2);
            Thread.sleep(CommandDispatcherTestCase.VIEW_CHANGE_WAIT);
            topology = bean.getClusterTopology();
            Assert.assertEquals(2, topology.getNodes().size());
            Assert.assertTrue(topology.getNodes().contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertTrue(topology.getNodes().contains(AbstractClusteringTestCase.NODE_2));
            Assert.assertFalse((((topology.getRemoteNodes().toString()) + " should not contain ") + (topology.getLocalNode())), topology.getRemoteNodes().contains(topology.getLocalNode()));
            stop(AbstractClusteringTestCase.NODE_1);
            topology = bean.getClusterTopology();
            Assert.assertEquals(1, topology.getNodes().size());
            Assert.assertTrue(topology.getNodes().contains(AbstractClusteringTestCase.NODE_2));
            Assert.assertEquals(AbstractClusteringTestCase.NODE_2, topology.getLocalNode());
            Assert.assertTrue(topology.getRemoteNodes().toString(), topology.getRemoteNodes().isEmpty());
            start(AbstractClusteringTestCase.NODE_1);
            Thread.sleep(CommandDispatcherTestCase.VIEW_CHANGE_WAIT);
            topology = bean.getClusterTopology();
            Assert.assertEquals(topology.getNodes().toString(), 2, topology.getNodes().size());
            Assert.assertTrue((((topology.getNodes().toString()) + " should contain ") + (AbstractClusteringTestCase.NODE_1)), topology.getNodes().contains(AbstractClusteringTestCase.NODE_1));
            Assert.assertTrue((((topology.getNodes().toString()) + " should contain ") + (AbstractClusteringTestCase.NODE_2)), topology.getNodes().contains(AbstractClusteringTestCase.NODE_2));
            Assert.assertFalse((((topology.getRemoteNodes().toString()) + " should not contain ") + (topology.getLocalNode())), topology.getRemoteNodes().contains(topology.getLocalNode()));
        }
    }
}

