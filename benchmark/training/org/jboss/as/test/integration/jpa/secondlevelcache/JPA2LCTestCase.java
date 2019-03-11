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
package org.jboss.as.test.integration.jpa.secondlevelcache;


import java.sql.Connection;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * JPA Second level cache tests
 *
 * @author Scott Marlow and Zbynek Roubalik
 */
@RunWith(Arquillian.class)
public class JPA2LCTestCase {
    private static final String ARCHIVE_NAME = "jpa_SecondLevelCacheTestCase";

    // cache region name prefix, use getCacheRegionName() method to get the value!
    private static String CACHE_REGION_NAME = null;

    @ArquillianResource
    private InitialContext iniCtx;

    @Test
    @InSequence(1)
    public void testMultipleNonTXTransactionalEntityManagerInvocations() throws Exception {
        SFSB1 sfsb1 = lookup("SFSB1", SFSB1.class);
        sfsb1.createEmployee("Kelly Smith", "Watford, England", 1000);
        sfsb1.createEmployee("Alex Scott", "London, England", 2000);
        sfsb1.getEmployeeNoTX(1000);
        sfsb1.getEmployeeNoTX(2000);
        DataSource ds = rawLookup("java:jboss/datasources/ExampleDS", DataSource.class);
        Connection conn = ds.getConnection();
        try {
            int deleted = conn.prepareStatement("delete from Employee").executeUpdate();
            // verify that delete worked (or test is invalid)
            Assert.assertTrue(("was able to delete added rows.  delete count=" + deleted), (deleted > 1));
        } finally {
            conn.close();
        }
        // read deleted data from second level cache
        Employee emp = sfsb1.getEmployeeNoTX(1000);
        Assert.assertTrue("was able to read deleted database row from second level cache", (emp != null));
    }

    // When caching is disabled, no extra action is done or exception happens
    // even if the code marks an entity and/or a query as cacheable
    @Test
    @InSequence(2)
    public void testDisabledCache() throws Exception {
        SFSB2LC sfsb = lookup("SFSB2LC", SFSB2LC.class);
        String message = sfsb.disabled2LCCheck();
        if (!(message.equals("OK"))) {
            Assert.fail(message);
        }
    }

    // When query caching is enabled, running the same query twice
    // without any operations between them will perform SQL queries only once.
    @Test
    @InSequence(6)
    public void testSameQueryTwice() throws Exception {
        SFSB2LC sfsb = lookup("SFSB2LC", SFSB2LC.class);
        String id = "1";
        String message = sfsb.queryCacheCheck(id);
        if (!(message.equals("OK"))) {
            Assert.fail(message);
        }
    }

    // When query caching is enabled, running a query to return all entities of a class
    // and then adding one entity of such class would invalidate the cache
    @Test
    @InSequence(7)
    public void testInvalidateQuery() throws Exception {
        SFSB2LC sfsb = lookup("SFSB2LC", SFSB2LC.class);
        String id = "2";
        String message = sfsb.queryCacheCheck(id);
        if (!(message.equals("OK"))) {
            Assert.fail(message);
        }
        // invalidate the cache
        sfsb.createEmployee("Newman", "Paul", 400);
        message = sfsb.queryCacheCheck(id);
        if (!(message.equals("OK"))) {
            Assert.fail(message);
        }
    }

    // Check if evicting query cache is working as expected
    @Test
    @InSequence(8)
    public void testEvictQueryCache() throws Exception {
        SFSB2LC sfsb = lookup("SFSB2LC", SFSB2LC.class);
        String id = "3";
        String message = sfsb.queryCacheCheck(id);
        if (!(message.equals("OK"))) {
            Assert.fail(message);
        }
        // evict query cache
        sfsb.evictQueryCache();
        message = sfsb.queryCacheCheckIfEmpty(id);
        if (!(message.equals("OK"))) {
            Assert.fail(message);
        }
    }
}

