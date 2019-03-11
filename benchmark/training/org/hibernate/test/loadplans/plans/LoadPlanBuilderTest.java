/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.loadplans.plans;


import LoadPlanTreePrinter.INSTANCE;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.LockMode;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.loader.plan.build.internal.CascadeStyleLoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.Return;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class LoadPlanBuilderTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSimpleBuild() {
        EntityPersister ep = ((EntityPersister) (sessionFactory().getClassMetadata(LoadPlanBuilderTest.Message.class)));
        FetchStyleLoadPlanBuildingAssociationVisitationStrategy strategy = new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(sessionFactory(), LoadQueryInfluencers.NONE, LockMode.NONE);
        LoadPlan plan = MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan(strategy, ep);
        Assert.assertFalse(plan.hasAnyScalarReturns());
        Assert.assertEquals(1, plan.getReturns().size());
        Return rtn = plan.getReturns().get(0);
        EntityReturn entityReturn = ExtraAssertions.assertTyping(EntityReturn.class, rtn);
        Assert.assertNotNull(entityReturn.getFetches());
        Assert.assertEquals(1, entityReturn.getFetches().length);
        Fetch fetch = entityReturn.getFetches()[0];
        EntityFetch entityFetch = ExtraAssertions.assertTyping(EntityFetch.class, fetch);
        Assert.assertNotNull(entityFetch.getFetches());
        Assert.assertEquals(0, entityFetch.getFetches().length);
        INSTANCE.logTree(plan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sessionFactory()));
    }

    @Test
    public void testCascadeBasedBuild() {
        EntityPersister ep = ((EntityPersister) (sessionFactory().getClassMetadata(LoadPlanBuilderTest.Message.class)));
        CascadeStyleLoadPlanBuildingAssociationVisitationStrategy strategy = new CascadeStyleLoadPlanBuildingAssociationVisitationStrategy(CascadingActions.MERGE, sessionFactory(), LoadQueryInfluencers.NONE, LockMode.NONE);
        LoadPlan plan = MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan(strategy, ep);
        Assert.assertFalse(plan.hasAnyScalarReturns());
        Assert.assertEquals(1, plan.getReturns().size());
        Return rtn = plan.getReturns().get(0);
        EntityReturn entityReturn = ExtraAssertions.assertTyping(EntityReturn.class, rtn);
        Assert.assertNotNull(entityReturn.getFetches());
        Assert.assertEquals(1, entityReturn.getFetches().length);
        Fetch fetch = entityReturn.getFetches()[0];
        EntityFetch entityFetch = ExtraAssertions.assertTyping(EntityFetch.class, fetch);
        Assert.assertNotNull(entityFetch.getFetches());
        Assert.assertEquals(0, entityFetch.getFetches().length);
        INSTANCE.logTree(plan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sessionFactory()));
    }

    @Test
    public void testCollectionInitializerCase() {
        CollectionPersister cp = sessionFactory().getCollectionPersister(((LoadPlanBuilderTest.Poster.class.getName()) + ".messages"));
        FetchStyleLoadPlanBuildingAssociationVisitationStrategy strategy = new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(sessionFactory(), LoadQueryInfluencers.NONE, LockMode.NONE);
        LoadPlan plan = MetamodelDrivenLoadPlanBuilder.buildRootCollectionLoadPlan(strategy, cp);
        Assert.assertFalse(plan.hasAnyScalarReturns());
        Assert.assertEquals(1, plan.getReturns().size());
        Return rtn = plan.getReturns().get(0);
        CollectionReturn collectionReturn = ExtraAssertions.assertTyping(CollectionReturn.class, rtn);
        Assert.assertNotNull(collectionReturn.getElementGraph());
        Assert.assertNotNull(collectionReturn.getElementGraph().getFetches());
        // the collection Message elements are fetched, but Message.poster is not fetched
        // (because that collection is owned by that Poster)
        Assert.assertEquals(0, collectionReturn.getElementGraph().getFetches().length);
        EntityReference entityReference = ExtraAssertions.assertTyping(EntityReference.class, collectionReturn.getElementGraph());
        Assert.assertNotNull(entityReference.getFetches());
        Assert.assertEquals(0, entityReference.getFetches().length);
        INSTANCE.logTree(plan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sessionFactory()));
    }

    @Entity(name = "Message")
    public static class Message {
        @Id
        private Integer mid;

        private String msgTxt;

        @ManyToOne(cascade = CascadeType.MERGE)
        @JoinColumn
        private LoadPlanBuilderTest.Poster poster;
    }

    @Entity(name = "Poster")
    public static class Poster {
        @Id
        private Integer pid;

        private String name;

        @OneToMany(mappedBy = "poster")
        private List<LoadPlanBuilderTest.Message> messages;
    }
}

