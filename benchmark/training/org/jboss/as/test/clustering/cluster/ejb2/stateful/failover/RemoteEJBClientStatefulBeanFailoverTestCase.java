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
package org.jboss.as.test.clustering.cluster.ejb2.stateful.failover;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that invocations on a clustered stateful session EJB2 bean from a remote EJB client, failover to
 * other node(s) in cases like a node going down.
 * This test is taken from test of ejb3 beans.
 *
 * @author Jaikiran Pai
 * @author Radoslav Husar
 * @author Ondrej Chaloupka
 */
@RunWith(Arquillian.class)
public class RemoteEJBClientStatefulBeanFailoverTestCase extends RemoteEJBClientStatefulFailoverTestBase {
    @Override
    @Test
    public void testFailoverFromRemoteClientWhenOneNodeGoesDown() throws Exception {
        failoverFromRemoteClient(false);
    }

    @Override
    @Test
    public void testFailoverFromRemoteClientWhenOneNodeUndeploys() throws Exception {
        failoverFromRemoteClient(true);
    }
}

