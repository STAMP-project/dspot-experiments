/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.webservices.config;


import org.jboss.wsf.spi.management.ServerConfig;
import org.jboss.wsf.spi.management.StackConfig;
import org.jboss.wsf.spi.management.StackConfigFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:alessio.soldano@jboss.com>Alessio Soldano</a>
 */
public class ServerConfigImplTestCase {
    private static ClassLoader origTCCL;

    public ServerConfigImplTestCase() {
    }

    @Test
    public void testIsModifiable() throws Exception {
        ServerConfigImpl sc = ServerConfigImplTestCase.newServerConfigImpl();
        sc.create();
        Assert.assertTrue(sc.isModifiable());
        sc.incrementWSDeploymentCount();
        Assert.assertFalse(sc.isModifiable());
        sc.decrementWSDeploymentCount();
        Assert.assertTrue(sc.isModifiable());
        sc.incrementWSDeploymentCount();
        sc.incrementWSDeploymentCount();
        Assert.assertFalse(sc.isModifiable());
        sc.create();
        Assert.assertTrue(sc.isModifiable());
    }

    @Test
    public void testSingleAttributeUpdate() throws Exception {
        internalTestSingleAttributeUpdate(new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setModifySOAPAddress(true);
            }
        });
        internalTestSingleAttributeUpdate(new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setWebServiceHost("foo");
            }
        });
        internalTestSingleAttributeUpdate(new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setWebServicePort(976);
            }
        });
        internalTestSingleAttributeUpdate(new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setWebServiceSecurePort(5435);
            }
        });
        internalTestSingleAttributeUpdate(new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setWebServicePathRewriteRule("MY/TEST/PATH");
            }
        });
    }

    @Test
    public void testMultipleAttributesUpdate() throws Exception {
        ServerConfigImplTestCase.Callback cbA = new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setModifySOAPAddress(true);
            }
        };
        ServerConfigImplTestCase.Callback cbB = new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setWebServiceHost("foo");
            }
        };
        ServerConfigImplTestCase.Callback cbC = new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setWebServicePort(976);
            }
        };
        ServerConfigImplTestCase.Callback cbD = new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setWebServiceSecurePort(5435);
            }
        };
        ServerConfigImplTestCase.Callback cbE = new ServerConfigImplTestCase.Callback() {
            @Override
            public void setAttribute(ServerConfig sc) throws Exception {
                sc.setWebServicePathRewriteRule("MY/TEST/PATH");
            }
        };
        internalTestMultipleAttributeUpdate(cbA, new ServerConfigImplTestCase.Callback[]{ cbB, cbC, cbD, cbE });
        internalTestMultipleAttributeUpdate(cbB, new ServerConfigImplTestCase.Callback[]{ cbA, cbC, cbD, cbE });
        internalTestMultipleAttributeUpdate(cbC, new ServerConfigImplTestCase.Callback[]{ cbA, cbB, cbD, cbE });
        internalTestMultipleAttributeUpdate(cbD, new ServerConfigImplTestCase.Callback[]{ cbA, cbB, cbC, cbE });
        internalTestMultipleAttributeUpdate(cbE, new ServerConfigImplTestCase.Callback[]{ cbA, cbB, cbC, cbD });
    }

    public interface Callback {
        void setAttribute(ServerConfig sc) throws Exception;
    }

    public static class TestStackConfigFactory extends StackConfigFactory {
        @Override
        public StackConfig getStackConfig() {
            return new ServerConfigImplTestCase.TestStackConfig();
        }
    }

    public static class TestStackConfig implements StackConfig {
        @Override
        public String getImplementationTitle() {
            return null;
        }

        @Override
        public String getImplementationVersion() {
            return null;
        }

        public void validatePathRewriteRule(String rule) {
            // NOOP
        }
    }
}

