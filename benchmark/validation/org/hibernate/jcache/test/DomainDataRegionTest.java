/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.jcache.test;


import AccessType.READ_WRITE;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.support.DomainDataRegionTemplate;
import org.hibernate.jcache.test.domain.Event;
import org.hibernate.jcache.test.domain.Item;
import org.hibernate.jcache.test.domain.VersionedItem;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class DomainDataRegionTest extends BaseFunctionalTest {
    @Test
    public void testBasicUsage() {
        final Region region = sessionFactory().getCache().getRegion(TestHelper.entityRegionNames[0]);
        final DomainDataRegionTemplate domainDataRegion = assertTyping(DomainDataRegionTemplate.class, region);
        // see if we can get access to all of the access objects we think should be defined in this region
        final EntityDataAccess itemAccess = domainDataRegion.getEntityDataAccess(sessionFactory().getMetamodel().entityPersister(Item.class).getNavigableRole());
        MatcherAssert.assertThat(itemAccess.getAccessType(), CoreMatchers.equalTo(READ_WRITE));
        MatcherAssert.assertThat(sessionFactory().getMetamodel().entityPersister(VersionedItem.class).getCacheAccessStrategy().getAccessType(), CoreMatchers.equalTo(READ_WRITE));
        MatcherAssert.assertThat(sessionFactory().getMetamodel().entityPersister(Event.class).getCacheAccessStrategy().getAccessType(), CoreMatchers.equalTo(READ_WRITE));
        MatcherAssert.assertThat(sessionFactory().getMetamodel().collectionPersister(((Event.class.getName()) + ".participants")).getCacheAccessStrategy().getAccessType(), CoreMatchers.equalTo(READ_WRITE));
    }
}

