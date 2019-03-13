/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.cacheable.cachemodes;


import AvailableSettings.SHARED_CACHE_RETRIEVE_MODE;
import AvailableSettings.SHARED_CACHE_STORE_MODE;
import CacheMode.GET;
import CacheMode.IGNORE;
import CacheMode.NORMAL;
import CacheMode.PUT;
import CacheStoreMode.BYPASS;
import CacheStoreMode.REFRESH;
import CacheStoreMode.USE;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SharedCacheModesTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testEntityManagerCacheModes() {
        EntityManager em;
        Session session;
        em = getOrCreateEntityManager();
        session = getSession();
        // defaults...
        Assert.assertEquals(USE, em.getProperties().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(CacheRetrieveMode.USE, em.getProperties().get(SHARED_CACHE_RETRIEVE_MODE));
        Assert.assertEquals(NORMAL, session.getCacheMode());
        // overrides...
        em.setProperty(SHARED_CACHE_STORE_MODE, REFRESH);
        Assert.assertEquals(REFRESH, em.getProperties().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(CacheMode.REFRESH, session.getCacheMode());
        em.setProperty(SHARED_CACHE_STORE_MODE, BYPASS);
        Assert.assertEquals(BYPASS, em.getProperties().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(GET, session.getCacheMode());
        em.setProperty(SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        Assert.assertEquals(CacheRetrieveMode.BYPASS, em.getProperties().get(SHARED_CACHE_RETRIEVE_MODE));
        Assert.assertEquals(IGNORE, session.getCacheMode());
        em.setProperty(SHARED_CACHE_STORE_MODE, USE);
        Assert.assertEquals(USE, em.getProperties().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(PUT, session.getCacheMode());
        em.setProperty(SHARED_CACHE_STORE_MODE, REFRESH);
        Assert.assertEquals(REFRESH, em.getProperties().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(CacheMode.REFRESH, session.getCacheMode());
    }

    @Test
    public void testQueryCacheModes() {
        EntityManager em = getOrCreateEntityManager();
        org.hibernate.query.Query query = em.createQuery("from SimpleEntity").unwrap(Query.class);
        query.setHint(SHARED_CACHE_STORE_MODE, USE);
        Assert.assertEquals(USE, query.getHints().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(NORMAL, query.getCacheMode());
        query.setHint(SHARED_CACHE_STORE_MODE, BYPASS);
        Assert.assertEquals(BYPASS, query.getHints().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(GET, query.getCacheMode());
        query.setHint(SHARED_CACHE_STORE_MODE, REFRESH);
        Assert.assertEquals(REFRESH, query.getHints().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(CacheMode.REFRESH, query.getCacheMode());
        query.setHint(SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        Assert.assertEquals(CacheRetrieveMode.BYPASS, query.getHints().get(SHARED_CACHE_RETRIEVE_MODE));
        Assert.assertEquals(REFRESH, query.getHints().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(CacheMode.REFRESH, query.getCacheMode());
        query.setHint(SHARED_CACHE_STORE_MODE, BYPASS);
        Assert.assertEquals(CacheRetrieveMode.BYPASS, query.getHints().get(SHARED_CACHE_RETRIEVE_MODE));
        Assert.assertEquals(BYPASS, query.getHints().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(IGNORE, query.getCacheMode());
        query.setHint(SHARED_CACHE_STORE_MODE, USE);
        Assert.assertEquals(CacheRetrieveMode.BYPASS, query.getHints().get(SHARED_CACHE_RETRIEVE_MODE));
        Assert.assertEquals(USE, query.getHints().get(SHARED_CACHE_STORE_MODE));
        Assert.assertEquals(PUT, query.getCacheMode());
    }
}

