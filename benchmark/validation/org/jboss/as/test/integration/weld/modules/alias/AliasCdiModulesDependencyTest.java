/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.weld.modules.alias;


import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.module.util.TestModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:bspyrkos@redhat.com">Bartosz Spyrko-Smietanko</a>
 */
@RunWith(Arquillian.class)
public class AliasCdiModulesDependencyTest {
    private static final String REF_MODULE_NAME = "weld-module-ref";

    private static final String IMPL_MODULE_NAME = "weld-module-impl";

    private static final String ALIAS_MODULE_NAME = "weld-module-alias";

    private static TestModule testModuleRef;

    private static TestModule testModuleImpl;

    private static TestModule testModuleAlias;

    @Inject
    WarBean warBean;

    @Test
    public void testBeanAccessibilities() {
        Assert.assertNotNull(warBean.getInjectedBean());
    }
}

