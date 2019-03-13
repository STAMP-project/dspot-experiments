/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.fetchstrategyhelper;


import FetchStyle.BATCH;
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
import org.hibernate.annotations.BatchSize;
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
public class BatchFetchStrategyHelperTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testManyToOneDefaultFetch() {
        final AssociationType associationType = determineAssociationType(BatchFetchStrategyHelperTest.AnEntity.class, "otherEntityDefault");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(BatchFetchStrategyHelperTest.AnEntity.class, "otherEntityDefault");
        Assert.assertSame(JOIN, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        // batch size is ignored with org.hibernate.FetchMode.JOIN
        Assert.assertSame(FetchStyle.JOIN, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(IMMEDIATE, fetchTiming);
    }

    @Test
    public void testManyToOneJoinFetch() {
        final AssociationType associationType = determineAssociationType(BatchFetchStrategyHelperTest.AnEntity.class, "otherEntityJoin");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(BatchFetchStrategyHelperTest.AnEntity.class, "otherEntityJoin");
        Assert.assertSame(JOIN, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        // batch size is ignored with org.hibernate.FetchMode.JOIN
        Assert.assertSame(FetchStyle.JOIN, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(IMMEDIATE, fetchTiming);
    }

    @Test
    public void testManyToOneSelectFetch() {
        final AssociationType associationType = determineAssociationType(BatchFetchStrategyHelperTest.AnEntity.class, "otherEntitySelect");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(BatchFetchStrategyHelperTest.AnEntity.class, "otherEntitySelect");
        Assert.assertSame(SELECT, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(BATCH, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(DELAYED, fetchTiming);
    }

    @Test
    public void testCollectionDefaultFetch() {
        final AssociationType associationType = determineAssociationType(BatchFetchStrategyHelperTest.AnEntity.class, "colorsDefault");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(BatchFetchStrategyHelperTest.AnEntity.class, "colorsDefault");
        Assert.assertSame(SELECT, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(BATCH, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(DELAYED, fetchTiming);
    }

    @Test
    public void testCollectionJoinFetch() {
        final AssociationType associationType = determineAssociationType(BatchFetchStrategyHelperTest.AnEntity.class, "colorsJoin");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(BatchFetchStrategyHelperTest.AnEntity.class, "colorsJoin");
        Assert.assertSame(JOIN, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        // batch size is ignored with org.hibernate.FetchMode.JOIN
        Assert.assertSame(FetchStyle.JOIN, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(IMMEDIATE, fetchTiming);
    }

    @Test
    public void testCollectionSelectFetch() {
        final AssociationType associationType = determineAssociationType(BatchFetchStrategyHelperTest.AnEntity.class, "colorsSelect");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(BatchFetchStrategyHelperTest.AnEntity.class, "colorsSelect");
        Assert.assertSame(SELECT, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        Assert.assertSame(BATCH, fetchStyle);
        final FetchTiming fetchTiming = FetchStrategyHelper.determineFetchTiming(fetchStyle, associationType, sessionFactory());
        Assert.assertSame(DELAYED, fetchTiming);
    }

    @Test
    public void testCollectionSubselectFetch() {
        final AssociationType associationType = determineAssociationType(BatchFetchStrategyHelperTest.AnEntity.class, "colorsSubselect");
        final org.hibernate.FetchMode fetchMode = determineFetchMode(BatchFetchStrategyHelperTest.AnEntity.class, "colorsSubselect");
        Assert.assertSame(SELECT, fetchMode);
        final FetchStyle fetchStyle = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, associationType, sessionFactory());
        // Batch size is ignored with FetchMode.SUBSELECT
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
        private BatchFetchStrategyHelperTest.OtherEntity otherEntityDefault;

        @ManyToOne
        @Fetch(FetchMode.JOIN)
        private BatchFetchStrategyHelperTest.OtherEntity otherEntityJoin;

        @ManyToOne
        @Fetch(FetchMode.SELECT)
        private BatchFetchStrategyHelperTest.OtherEntity otherEntitySelect;

        // @Fetch(FetchMode.SUBSELECT) is not allowed for ToOne associations
        @ElementCollection
        @BatchSize(size = 5)
        private Set<String> colorsDefault = new HashSet<String>();

        @ElementCollection
        @Fetch(FetchMode.JOIN)
        @BatchSize(size = 5)
        private Set<String> colorsJoin = new HashSet<String>();

        @ElementCollection
        @Fetch(FetchMode.SELECT)
        @BatchSize(size = 5)
        private Set<String> colorsSelect = new HashSet<String>();

        @ElementCollection
        @Fetch(FetchMode.SUBSELECT)
        @BatchSize(size = 5)
        private Set<String> colorsSubselect = new HashSet<String>();
    }

    @Entity
    @Table(name = "otherentity")
    @BatchSize(size = 5)
    public static class OtherEntity {
        @Id
        @GeneratedValue
        private Long id;
    }
}

