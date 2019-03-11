/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.fetchstrategyhelper;


import FetchStyle.SUBSELECT;
import FetchTiming.DELAYED;
import FetchTiming.IMMEDIATE;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.FetchMode.JOIN;
import org.hibernate.FetchMode.SELECT;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
public class FetchStrategyHelperTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testManyToOneDefaultFetch() {
        final AssociationType associationType = determineAssociationType(FetchStrategyHelperTest.AnEntity.class, "otherEntityDefault");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(FetchStrategyHelperTest.AnEntity.class, "otherEntityDefault");
        Assert.assertSame(JOIN, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(FetchStyle.JOIN, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(IMMEDIATE, fetchTiming);
    }

    @Test
    public void testManyToOneJoinFetch() {
        final AssociationType associationType = determineAssociationType(FetchStrategyHelperTest.AnEntity.class, "otherEntityJoin");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(FetchStrategyHelperTest.AnEntity.class, "otherEntityJoin");
        Assert.assertSame(JOIN, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(FetchStyle.JOIN, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(IMMEDIATE, fetchTiming);
    }

    @Test
    public void testManyToOneSelectFetch() {
        final AssociationType associationType = determineAssociationType(FetchStrategyHelperTest.AnEntity.class, "otherEntitySelect");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(FetchStrategyHelperTest.AnEntity.class, "otherEntitySelect");
        Assert.assertSame(SELECT, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(FetchStyle.SELECT, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(DELAYED, fetchTiming);
    }

    @Test
    public void testCollectionDefaultFetch() {
        final AssociationType associationType = determineAssociationType(FetchStrategyHelperTest.AnEntity.class, "colorsDefault");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(FetchStrategyHelperTest.AnEntity.class, "colorsDefault");
        Assert.assertSame(SELECT, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(FetchStyle.SELECT, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(DELAYED, fetchTiming);
    }

    @Test
    public void testCollectionJoinFetch() {
        final AssociationType associationType = determineAssociationType(FetchStrategyHelperTest.AnEntity.class, "colorsJoin");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(FetchStrategyHelperTest.AnEntity.class, "colorsJoin");
        Assert.assertSame(JOIN, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(FetchStyle.JOIN, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(IMMEDIATE, fetchTiming);
    }

    @Test
    public void testCollectionSelectFetch() {
        final AssociationType associationType = determineAssociationType(FetchStrategyHelperTest.AnEntity.class, "colorsSelect");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(FetchStrategyHelperTest.AnEntity.class, "colorsSelect");
        Assert.assertSame(SELECT, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(FetchStyle.SELECT, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(DELAYED, fetchTiming);
    }

    @Test
    public void testCollectionSubselectFetch() {
        final AssociationType associationType = determineAssociationType(FetchStrategyHelperTest.AnEntity.class, "colorsSubselect");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(FetchStrategyHelperTest.AnEntity.class, "colorsSubselect");
        Assert.assertSame(SELECT, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(SUBSELECT, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(DELAYED, fetchTiming);
    }

    @Entity
    @Table(name = "entity")
    public static class AnEntity {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne
        private FetchStrategyHelperTest.OtherEntity otherEntityDefault;

        @ManyToOne
        @Fetch(FetchMode.JOIN)
        private FetchStrategyHelperTest.OtherEntity otherEntityJoin;

        @ManyToOne
        @Fetch(FetchMode.SELECT)
        private FetchStrategyHelperTest.OtherEntity otherEntitySelect;

        // @Fetch(FetchMode.SUBSELECT) is not allowed for ToOne associations
        @ElementCollection
        private Set<String> colorsDefault = new HashSet<String>();

        @ElementCollection
        @Fetch(FetchMode.JOIN)
        private Set<String> colorsJoin = new HashSet<String>();

        @ElementCollection
        @Fetch(FetchMode.SELECT)
        private Set<String> colorsSelect = new HashSet<String>();

        @ElementCollection
        @Fetch(FetchMode.SUBSELECT)
        private Set<String> colorsSubselect = new HashSet<String>();
    }

    @Entity
    @Table(name = "otherentity")
    public static class OtherEntity {
        @Id
        @GeneratedValue
        private Long id;
    }
}

