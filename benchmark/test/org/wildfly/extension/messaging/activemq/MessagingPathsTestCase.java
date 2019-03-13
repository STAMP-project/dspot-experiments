/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.wildfly.extension.messaging.activemq;


import ActiveMQServerService.PathConfig;
import ServerEnvironment.SERVER_DATA_DIR;
import org.jboss.as.controller.services.path.PathManagerService;
import org.jboss.msc.service.ServiceContainer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://jmesnil.net">Jeff Mesnil</a> (c) 2012 Red Hat Inc.
 */
public class MessagingPathsTestCase {
    private static final String MY_SERVER_DATA_DIR = (System.getProperty("java.io.tmpdir")) + "datadir";

    private static final String MY_RELATIVE_JOURNAL_DIR = "my-relative-journal";

    private static final String MY_ABSOLUTE_BINDINGS_DIR = (System.getProperty("java.io.tmpdir")) + "bindingsdir";

    private static final String MY_PAGING_RELATIVE_TO = "paging.relative-to.dir";

    private static final String MY_PAGING_RELATIVE_TO_DIR = (System.getProperty("java.io.tmpdir")) + "pagingdir";

    private ServiceContainer container;

    @Test
    public void testAddPath() throws Exception {
        PathManagerService pathManagerService = new PathManagerService() {
            {
                // define the standard server data dir that is the default relative-to for messaging paths
                super.addHardcodedAbsolutePath(container, SERVER_DATA_DIR, MessagingPathsTestCase.MY_SERVER_DATA_DIR);
                // define another related-to specific for paging directory
                super.addHardcodedAbsolutePath(container, MessagingPathsTestCase.MY_PAGING_RELATIVE_TO, MessagingPathsTestCase.MY_PAGING_RELATIVE_TO_DIR);
            }
        };
        ActiveMQServerService.PathConfig pathConfig = // => binding dir is absolute
        // => specific journal dir is relative to default relative-to
        // => default largeMessage is relative to default relative-to
        new ActiveMQServerService.PathConfig(MessagingPathsTestCase.MY_ABSOLUTE_BINDINGS_DIR, PathDefinition.DEFAULT_RELATIVE_TO, MessagingPathsTestCase.MY_RELATIVE_JOURNAL_DIR, PathDefinition.DEFAULT_RELATIVE_TO, PathDefinition.DEFAULT_LARGE_MESSAGE_DIR, PathDefinition.DEFAULT_RELATIVE_TO, PathDefinition.DEFAULT_PAGING_DIR, MessagingPathsTestCase.MY_PAGING_RELATIVE_TO);// => paging is relative to specific relative-to

        String resolvedJournalPath = pathConfig.resolveJournalPath(pathManagerService);
        Assert.assertTrue(((("the specific relative path must be prepended by the resolved default relative-to, resolvedJournalPath=" + resolvedJournalPath) + ", MY_SERVER_DATA_DIR") + (MessagingPathsTestCase.MY_SERVER_DATA_DIR)), resolvedJournalPath.startsWith(MessagingPathsTestCase.MY_SERVER_DATA_DIR));
        Assert.assertTrue(resolvedJournalPath.endsWith(MessagingPathsTestCase.MY_RELATIVE_JOURNAL_DIR));
        String resolvedBindingsPath = pathConfig.resolveBindingsPath(pathManagerService);
        Assert.assertEquals(("the speficic absolute path must not be prepended by the resolved default relative-to, resolvedBindingsPath=" + resolvedBindingsPath), MessagingPathsTestCase.MY_ABSOLUTE_BINDINGS_DIR, resolvedBindingsPath);
        String resolvedPagingPath = pathConfig.resolvePagingPath(pathManagerService);
        Assert.assertTrue(("the default path must be prepended by the resolved specific relative-to, resolvedPagingPath=" + resolvedPagingPath), resolvedPagingPath.startsWith(MessagingPathsTestCase.MY_PAGING_RELATIVE_TO_DIR));
        Assert.assertTrue(resolvedPagingPath.endsWith(PathDefinition.DEFAULT_PAGING_DIR));
        String resolvedLargeMessagePath = pathConfig.resolveLargeMessagePath(pathManagerService);
        Assert.assertTrue(("by default, the default path MUST prepended by the resolved default relative-to, resolvedLargeMessagePath=" + resolvedLargeMessagePath), resolvedLargeMessagePath.startsWith(MessagingPathsTestCase.MY_SERVER_DATA_DIR));
        Assert.assertTrue(resolvedLargeMessagePath.endsWith(PathDefinition.DEFAULT_LARGE_MESSAGE_DIR));
    }
}

