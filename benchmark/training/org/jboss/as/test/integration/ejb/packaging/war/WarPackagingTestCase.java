/**
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.test.integration.ejb.packaging.war;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Testing packaging ejb in war.
 *
 * @author Ondrej Chaloupka
 */
@RunWith(Arquillian.class)
public class WarPackagingTestCase {
    private static final Logger log = Logger.getLogger(WarPackagingTestCase.class);

    private static final String ARCHIVE_NAME = "ejbinwar";

    private static final String JAR_SUCCESS_STRING = "Hi war from jar";

    private static final String WAR_SUCCESS_STRING = "Hi jar from war";

    @ArquillianResource
    private InitialContext ctx;

    @Test
    public void test() throws Exception {
        JarBean jarBean = ((JarBean) (ctx.lookup(((("java:module/" + (JarBean.class.getSimpleName())) + "!") + (JarBean.class.getName())))));
        Assert.assertEquals(WarPackagingTestCase.JAR_SUCCESS_STRING, jarBean.checkMe());
        jarBean = ((JarBean) (ctx.lookup(((((("java:global/" + (WarPackagingTestCase.ARCHIVE_NAME)) + "/") + (JarBean.class.getSimpleName())) + "!") + (JarBean.class.getName())))));
        Assert.assertEquals(WarPackagingTestCase.JAR_SUCCESS_STRING, jarBean.checkMe());
        WarBean warBean = ((WarBean) (ctx.lookup(((("java:module/" + (WarBean.class.getSimpleName())) + "!") + (WarBean.class.getName())))));
        Assert.assertEquals(WarPackagingTestCase.WAR_SUCCESS_STRING, warBean.checkMe());
        warBean = ((WarBean) (ctx.lookup(((("java:module/" + (WarBean.class.getSimpleName())) + "!") + (WarBean.class.getName())))));
        Assert.assertEquals(WarPackagingTestCase.WAR_SUCCESS_STRING, warBean.checkMe());
    }
}

