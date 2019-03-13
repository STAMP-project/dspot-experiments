package dev.morphia.query;


import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class QueryHasAnyOfForReferencedList extends TestBase {
    @Test
    public void testInQuery() throws Exception {
        QueryHasAnyOfForReferencedList.Plan plan1 = new QueryHasAnyOfForReferencedList.Plan();
        plan1.name = "Trial";
        QueryHasAnyOfForReferencedList.Plan plan2 = new QueryHasAnyOfForReferencedList.Plan();
        plan2.name = "Trial";
        getDs().save(plan1);
        getDs().save(plan2);
        QueryHasAnyOfForReferencedList.Org org1 = new QueryHasAnyOfForReferencedList.Org();
        org1.plan = plan1;
        org1.name = "Test Org1";
        QueryHasAnyOfForReferencedList.Org org2 = new QueryHasAnyOfForReferencedList.Org();
        org2.plan = plan2;
        org2.name = "Test Org2";
        getDs().save(org1);
        getDs().save(org2);
        long count = getDs().find(QueryHasAnyOfForReferencedList.Org.class).field("name").equal("Test Org1").count();
        Assert.assertEquals(1, count);
        List<QueryHasAnyOfForReferencedList.Plan> plans = new ArrayList<QueryHasAnyOfForReferencedList.Plan>();
        plans.add(plan1);
        count = getDs().find(QueryHasAnyOfForReferencedList.Org.class).field("plan").hasAnyOf(plans).count();
        Assert.assertEquals(1, count);
        plans = new ArrayList<QueryHasAnyOfForReferencedList.Plan>();
        plans.add(plan1);
        plans.add(plan2);
        count = getDs().find(QueryHasAnyOfForReferencedList.Org.class).field("plan").hasAnyOf(plans).count();
        Assert.assertEquals(2, count);
    }

    @Entity(noClassnameStored = true)
    private static class Plan implements Serializable {
        @Id
        private ObjectId id;

        @Property("name")
        private String name;
    }

    @Entity(noClassnameStored = true)
    private static class Org implements Serializable {
        @Id
        private ObjectId id;

        @Property("name")
        private String name;

        @Reference("plan")
        private QueryHasAnyOfForReferencedList.Plan plan;
    }
}

