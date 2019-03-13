/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.hibernate.search;


import javax.ejb.EJB;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.engine.impl.MutableSearchFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Verify deployed applications can use the default Hibernate Search module via JPA APIs.
 *
 * @author Sanne Grinovero <sanne@hibernate.org> (C) 2014 Red Hat Inc.
 */
@RunWith(Arquillian.class)
public class HibernateSearchJPATestCase {
    private static final String ARCHIVE_NAME = "hibernate4native_search_test";

    @EJB(mappedName = "java:module/SearchBean")
    private SearchBean searchBean;

    @Test
    public void testFullTextQuery() {
        searchBean.storeNewBook("Hello");
        searchBean.storeNewBook("Hello world");
        searchBean.storeNewBook("Hello planet Mars");
        Assert.assertEquals(3, searchBean.findByKeyword("hello").size());
        Assert.assertEquals(1, searchBean.findByKeyword("mars").size());
    }

    @Test
    public void testCustomConfigurationApplied() {
        SearchFactory searchFactory = searchBean.retrieveHibernateSearchEngine();
        MutableSearchFactory internalSearchEngine = searchFactory.unwrap(MutableSearchFactory.class);
        Assert.assertTrue(internalSearchEngine.isIndexUninvertingAllowed());
    }
}

