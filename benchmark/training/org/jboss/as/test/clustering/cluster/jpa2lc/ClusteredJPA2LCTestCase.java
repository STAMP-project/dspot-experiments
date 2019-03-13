/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.clustering.cluster.jpa2lc;


import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.clustering.NodeUtil;
import org.jboss.as.test.clustering.cluster.AbstractClusteringTestCase;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Smoke test of clustered JPA 2nd level cache implemented by Infinispan.
 *
 * @author Jan Martiska
 */
@RunWith(Arquillian.class)
public class ClusteredJPA2LCTestCase {
    private static final String MODULE_NAME = ClusteredJPA2LCTestCase.class.getSimpleName();

    @ArquillianResource
    protected ContainerController controller;

    @ArquillianResource
    protected Deployer deployer;

    // management connection to node0
    private ModelControllerClient client0;

    // management connection to node1
    private ModelControllerClient client1;

    // REST client to control entity creation, caching, eviction,... on the servers
    private Client restClient = ClientBuilder.newClient();

    // /subsystem=infinispan/cache-container=hibernate/replicated-cache=entity-replicated
    static ModelNode CACHE_ADDRESS;

    static {
        ClusteredJPA2LCTestCase.CACHE_ADDRESS = new ModelNode();
        ClusteredJPA2LCTestCase.CACHE_ADDRESS.get("subsystem").set("infinispan");
        ClusteredJPA2LCTestCase.CACHE_ADDRESS.get("cache-container").set("hibernate");
        ClusteredJPA2LCTestCase.CACHE_ADDRESS.get("replicated-cache").set("entity-replicated");
    }

    @Test
    @InSequence(-1)
    public void setupCacheContainer() throws IOException {
        NodeUtil.start(controller, AbstractClusteringTestCase.TWO_NODES);
        final ModelNode createEntityReplicatedCacheOp = new ModelNode();
        createEntityReplicatedCacheOp.get(ADDRESS).set(ClusteredJPA2LCTestCase.CACHE_ADDRESS);
        createEntityReplicatedCacheOp.get(OP).set(ADD);
        createEntityReplicatedCacheOp.get("mode").set("sync");
        client0 = ClusteredJPA2LCTestCase.createClient0();
        client1 = ClusteredJPA2LCTestCase.createClient1();
        final ModelNode result0 = client0.execute(createEntityReplicatedCacheOp);
        Assert.assertTrue(result0.toJSONString(false), result0.get(OUTCOME).asString().equals(SUCCESS));
        final ModelNode result1 = client1.execute(createEntityReplicatedCacheOp);
        Assert.assertTrue(result1.toJSONString(false), result1.get(OUTCOME).asString().equals(SUCCESS));
        NodeUtil.deploy(this.deployer, AbstractClusteringTestCase.TWO_DEPLOYMENTS);
    }

    @Test
    @InSequence(Integer.MAX_VALUE)
    public void tearDown() throws IOException {
        final ModelNode removeOp = new ModelNode();
        removeOp.get(ADDRESS).set(ClusteredJPA2LCTestCase.CACHE_ADDRESS);
        removeOp.get(OP).set(REMOVE_OPERATION);
        if ((client0) != null) {
            client0.execute(removeOp);
            client0.close();
        }
        if ((client1) != null) {
            client1.execute(removeOp);
            client1.close();
        }
        if ((restClient) != null) {
            restClient.close();
        }
    }
}

