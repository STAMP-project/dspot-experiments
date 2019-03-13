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
package org.jboss.as.test.manualmode.ejb.ssl;


import java.util.concurrent.Future;
import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.manualmode.ejb.ssl.beans.StatefulBean;
import org.jboss.as.test.manualmode.ejb.ssl.beans.StatefulBeanRemote;
import org.jboss.as.test.manualmode.ejb.ssl.beans.StatelessBean;
import org.jboss.as.test.manualmode.ejb.ssl.beans.StatelessBeanRemote;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Testing ssl connection of remote ejb client.
 *
 * @author Ondrej Chaloupka
 * @author Jan Martiska
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SSLEJBRemoteClientTestCase {
    private static final Logger log = Logger.getLogger(SSLEJBRemoteClientTestCase.class);

    private static final String MODULE_NAME_STATELESS = "ssl-remote-ejb-client-test";

    private static final String MODULE_NAME_STATEFUL = "ssl-remote-ejb-client-test-stateful";

    public static final String DEPLOYMENT_STATELESS = "dep_stateless";

    public static final String DEPLOYMENT_STATEFUL = "dep_stateful";

    private static boolean serverConfigDone = false;

    @ArquillianResource
    private static ContainerController container;

    @ArquillianResource
    private Deployer deployer;

    public static final String DEFAULT_JBOSSAS = "default-jbossas";

    @Test
    public void testStatelessBean() throws Exception {
        SSLEJBRemoteClientTestCase.log.trace("**** deploying deployment with stateless beans");
        deployer.deploy(SSLEJBRemoteClientTestCase.DEPLOYMENT_STATELESS);
        SSLEJBRemoteClientTestCase.log.trace("**** creating InitialContext");
        InitialContext ctx = new InitialContext(getEjbClientContextProperties());
        try {
            SSLEJBRemoteClientTestCase.log.trace("**** looking up StatelessBean through JNDI");
            StatelessBeanRemote bean = ((StatelessBeanRemote) (ctx.lookup(((((("ejb:/" + (SSLEJBRemoteClientTestCase.MODULE_NAME_STATELESS)) + "/") + (StatelessBean.class.getSimpleName())) + "!") + (StatelessBeanRemote.class.getCanonicalName())))));
            SSLEJBRemoteClientTestCase.log.trace("**** About to perform synchronous call on stateless bean");
            String response = bean.sayHello();
            SSLEJBRemoteClientTestCase.log.trace(("**** The answer is: " + response));
            Assert.assertEquals("Remote invocation of EJB was not successful", StatelessBeanRemote.ANSWER, response);
            deployer.undeploy(SSLEJBRemoteClientTestCase.DEPLOYMENT_STATELESS);
        } finally {
            ctx.close();
        }
    }

    @Test
    public void testStatelessBeanAsync() throws Exception {
        SSLEJBRemoteClientTestCase.log.trace("**** deploying deployment with stateless beans");
        deployer.deploy(SSLEJBRemoteClientTestCase.DEPLOYMENT_STATELESS);
        SSLEJBRemoteClientTestCase.log.trace("**** creating InitialContext");
        InitialContext ctx = new InitialContext(getEjbClientContextProperties());
        try {
            SSLEJBRemoteClientTestCase.log.trace("**** looking up StatelessBean through JNDI");
            StatelessBeanRemote bean = ((StatelessBeanRemote) (ctx.lookup(((((("ejb:/" + (SSLEJBRemoteClientTestCase.MODULE_NAME_STATELESS)) + "/") + (StatelessBean.class.getSimpleName())) + "!") + (StatelessBeanRemote.class.getCanonicalName())))));
            SSLEJBRemoteClientTestCase.log.trace("**** About to perform asynchronous call on stateless bean");
            Future<String> futureResponse = bean.sayHelloAsync();
            String response = futureResponse.get();
            SSLEJBRemoteClientTestCase.log.trace(("**** The answer is: " + response));
            Assert.assertEquals("Remote asynchronous invocation of EJB was not successful", StatelessBeanRemote.ANSWER, response);
            deployer.undeploy(SSLEJBRemoteClientTestCase.DEPLOYMENT_STATELESS);
        } finally {
            ctx.close();
        }
    }

    @Test
    public void testStatefulBean() throws Exception {
        SSLEJBRemoteClientTestCase.log.trace("**** deploying deployment with stateful beans");
        deployer.deploy(SSLEJBRemoteClientTestCase.DEPLOYMENT_STATEFUL);
        SSLEJBRemoteClientTestCase.log.trace("**** creating InitialContext");
        InitialContext ctx = new InitialContext(getEjbClientContextProperties());
        try {
            SSLEJBRemoteClientTestCase.log.trace("**** looking up StatefulBean through JNDI");
            StatefulBeanRemote bean = ((StatefulBeanRemote) (ctx.lookup((((((("ejb:/" + (SSLEJBRemoteClientTestCase.MODULE_NAME_STATEFUL)) + "/") + (StatefulBean.class.getSimpleName())) + "!") + (StatefulBeanRemote.class.getCanonicalName())) + "?stateful"))));
            SSLEJBRemoteClientTestCase.log.trace("**** About to perform synchronous call on stateful bean");
            String response = bean.sayHello();
            SSLEJBRemoteClientTestCase.log.trace(("**** The answer is: " + response));
            Assert.assertEquals("Remote invocation of EJB was not successful", StatefulBeanRemote.ANSWER, response);
            deployer.undeploy(SSLEJBRemoteClientTestCase.DEPLOYMENT_STATEFUL);
        } finally {
            ctx.close();
        }
    }

    @Test
    public void testStatefulBeanAsync() throws Exception {
        SSLEJBRemoteClientTestCase.log.trace("**** deploying deployment with stateful beans");
        deployer.deploy(SSLEJBRemoteClientTestCase.DEPLOYMENT_STATEFUL);
        SSLEJBRemoteClientTestCase.log.trace("**** creating InitialContext");
        InitialContext ctx = new InitialContext(getEjbClientContextProperties());
        try {
            SSLEJBRemoteClientTestCase.log.trace("**** looking up StatefulBean through JNDI");
            StatefulBeanRemote bean = ((StatefulBeanRemote) (ctx.lookup((((((("ejb:/" + (SSLEJBRemoteClientTestCase.MODULE_NAME_STATEFUL)) + "/") + (StatefulBean.class.getSimpleName())) + "!") + (StatefulBeanRemote.class.getCanonicalName())) + "?stateful"))));
            SSLEJBRemoteClientTestCase.log.trace("**** About to perform asynchronous call on stateful bean");
            Future<String> futureResponse = bean.sayHelloAsync();
            String response = futureResponse.get();
            SSLEJBRemoteClientTestCase.log.trace(("**** The answer is: " + response));
            Assert.assertEquals("Remote asynchronous invocation of EJB was not successful", StatefulBeanRemote.ANSWER, response);
            deployer.undeploy(SSLEJBRemoteClientTestCase.DEPLOYMENT_STATEFUL);
        } finally {
            ctx.close();
        }
    }
}

