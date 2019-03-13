/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.clustering.cluster.ejb2.stateless;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.clustering.cluster.AbstractClusteringTestCase;
import org.jboss.as.test.clustering.ejb.EJBDirectory;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * EJB2 stateless bean - basic cluster tests - failover and load balancing.
 *
 * @author Paul Ferraro
 * @author Ondrej Chaloupka
 */
@RunWith(Arquillian.class)
public class RemoteStatelessFailoverTestCase {
    private static final Logger log = Logger.getLogger(RemoteStatelessFailoverTestCase.class);

    private static EJBDirectory directoryAnnotation;

    private static EJBDirectory directoryDD;

    private static final String MODULE_NAME = RemoteStatelessFailoverTestCase.class.getSimpleName();

    private static final String MODULE_NAME_DD = (RemoteStatelessFailoverTestCase.MODULE_NAME) + "dd";

    private static final Map<String, Boolean> deployed = new HashMap<String, Boolean>();

    private static final Map<String, Boolean> started = new HashMap<String, Boolean>();

    private static final Map<String, List<String>> container2deployment = new HashMap<String, List<String>>();

    @ArquillianResource
    private ContainerController container;

    @ArquillianResource
    private Deployer deployer;

    @Test
    public void testFailoverOnStopAnnotatedBean() throws Exception {
        doFailover(true, RemoteStatelessFailoverTestCase.directoryAnnotation, AbstractClusteringTestCase.DEPLOYMENT_1, AbstractClusteringTestCase.DEPLOYMENT_2);
    }

    @Test
    public void testFailoverOnStopBeanSpecifiedByDescriptor() throws Exception {
        doFailover(true, RemoteStatelessFailoverTestCase.directoryDD, AbstractClusteringTestCase.DEPLOYMENT_HELPER_1, AbstractClusteringTestCase.DEPLOYMENT_HELPER_2);
    }

    @Test
    public void testFailoverOnUndeployAnnotatedBean() throws Exception {
        doFailover(false, RemoteStatelessFailoverTestCase.directoryAnnotation, AbstractClusteringTestCase.DEPLOYMENT_1, AbstractClusteringTestCase.DEPLOYMENT_2);
    }

    @Test
    public void testFailoverOnUndeploySpecifiedByDescriptor() throws Exception {
        doFailover(false, RemoteStatelessFailoverTestCase.directoryDD, AbstractClusteringTestCase.DEPLOYMENT_HELPER_1, AbstractClusteringTestCase.DEPLOYMENT_HELPER_2);
    }

    @Test
    public void testLoadbalanceAnnotatedBean() throws Exception {
        loadbalance(RemoteStatelessFailoverTestCase.directoryAnnotation, AbstractClusteringTestCase.DEPLOYMENT_1, AbstractClusteringTestCase.DEPLOYMENT_2);
    }

    @Test
    public void testLoadbalanceSpecifiedByDescriptor() throws Exception {
        loadbalance(RemoteStatelessFailoverTestCase.directoryDD, AbstractClusteringTestCase.DEPLOYMENT_HELPER_1, AbstractClusteringTestCase.DEPLOYMENT_HELPER_2);
    }
}

