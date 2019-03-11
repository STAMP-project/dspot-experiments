/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.fetchstrategyhelper;


import FetchTiming.IMMEDIATE;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.FetchMode.JOIN;
import org.hibernate.FetchMode.SELECT;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.org.hibernate.FetchMode;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.persister.walking.internal.FetchStrategyHelper;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.type.AssociationType;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class NoProxyFetchStrategyHelperTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testManyToOneDefaultFetch() {
        final AssociationType associationType = determineAssociationType(NoProxyFetchStrategyHelperTest.AnEntity.class, "otherEntityDefault");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(NoProxyFetchStrategyHelperTest.AnEntity.class, "otherEntityDefault");
        Assert.assertSame(JOIN, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(FetchStyle.JOIN, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(IMMEDIATE, fetchTiming);
    }

    @Test
    public void testManyToOneJoinFetch() {
        final AssociationType associationType = determineAssociationType(NoProxyFetchStrategyHelperTest.AnEntity.class, "otherEntityJoin");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(NoProxyFetchStrategyHelperTest.AnEntity.class, "otherEntityJoin");
        Assert.assertSame(JOIN, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(FetchStyle.JOIN, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(IMMEDIATE, fetchTiming);
    }

    @Test
    public void testManyToOneSelectFetch() {
        final AssociationType associationType = determineAssociationType(NoProxyFetchStrategyHelperTest.AnEntity.class, "otherEntitySelect");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(NoProxyFetchStrategyHelperTest.AnEntity.class, "otherEntitySelect");
        Assert.assertSame(SELECT, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(FetchStyle.SELECT, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        // Proxies are not allowed, so it should be FetchTiming.IMMEDIATE
        Assert.assertSame(IMMEDIATE, fetchTiming);
    }

    // @Fetch(FetchMode.SUBSELECT) is not allowed for ToOne associations
    @Entity
    @Table(name = "entity")
    public static class AnEntity {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne
        private NoProxyFetchStrategyHelperTest.OtherEntity otherEntityDefault;

        @ManyToOne
        @Fetch(FetchMode.JOIN)
        private NoProxyFetchStrategyHelperTest.OtherEntity otherEntityJoin;

        @ManyToOne
        @Fetch(FetchMode.SELECT)
        private NoProxyFetchStrategyHelperTest.OtherEntity otherEntitySelect;
    }

    @Entity
    @Table(name = "otherentity")
    @Proxy(lazy = false)
    public static class OtherEntity {
        @Id
        @GeneratedValue
        private Long id;
    }
}

