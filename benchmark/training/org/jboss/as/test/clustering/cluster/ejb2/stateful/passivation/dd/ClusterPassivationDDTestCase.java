/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.clustering.cluster.ejb2.stateful.passivation.dd;


import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.clustering.cluster.ejb2.stateful.passivation.ClusterPassivationTestBase;
import org.jboss.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Clustering ejb passivation of EJB2 bean defined by descriptor.
 *
 * @author Ondrej Chaloupka
 */
@Ignore("Uses legacy client hack")
@RunWith(Arquillian.class)
public class ClusterPassivationDDTestCase extends ClusterPassivationTestBase {
    private static Logger log = Logger.getLogger(ClusterPassivationDDTestCase.class);

    @ArquillianResource
    private ContainerController controller;

    @ArquillianResource
    private Deployer deployer;

    @Test
    @InSequence(-2)
    public void startServersForTests() {
        startServers(null, null);
    }
}

