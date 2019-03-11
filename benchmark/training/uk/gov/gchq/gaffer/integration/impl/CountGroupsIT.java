package uk.gov.gchq.gaffer.integration.impl;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestPropertyNames.INT;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.GroupCounts;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.operation.OperationChain.Builder;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.CountGroups;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.user.User;


public class CountGroupsIT extends AbstractStoreIT {
    private static final String VERTEX = "vertex";

    @Test
    public void shouldCountGroupsOfElements() throws OperationException {
        // Given
        final User user = new User();
        final Entity entity = new Entity(TestGroups.ENTITY_2, CountGroupsIT.VERTEX);
        entity.putProperty(INT, 100);
        // When
        final GroupCounts counts = AbstractStoreIT.graph.execute(new Builder().first(new GetAllElements()).then(new CountGroups()).build(), user);
        // Then
        Assert.assertEquals(1, counts.getEntityGroups().size());
        Assert.assertEquals(getEntities().size(), ((int) (counts.getEntityGroups().get(ENTITY))));
        Assert.assertEquals(1, counts.getEdgeGroups().size());
        Assert.assertEquals(getEdges().size(), ((int) (counts.getEdgeGroups().get(EDGE))));
        Assert.assertFalse(counts.isLimitHit());
    }

    @Test
    public void shouldCountGroupsOfElementsWhenLessElementsThanLimit() throws OperationException {
        // Given
        final User user = new User();
        final Integer limit = ((getEntities().size()) + (getEdges().size())) + 1;
        final Entity entity = new Entity(TestGroups.ENTITY_2, CountGroupsIT.VERTEX);
        entity.putProperty(INT, 100);
        // When
        final GroupCounts counts = AbstractStoreIT.graph.execute(new Builder().first(new GetAllElements()).then(new CountGroups(limit)).build(), user);
        // Then
        Assert.assertEquals(1, counts.getEntityGroups().size());
        Assert.assertEquals(getEntities().size(), ((int) (counts.getEntityGroups().get(ENTITY))));
        Assert.assertEquals(1, counts.getEdgeGroups().size());
        Assert.assertEquals(getEdges().size(), ((int) (counts.getEdgeGroups().get(EDGE))));
        Assert.assertFalse(counts.isLimitHit());
    }

    @Test
    public void shouldCountGroupsOfElementsWhenMoreElementsThanLimit() throws OperationException {
        // Given
        final User user = new User();
        final int limit = 5;
        final Entity entity = new Entity(TestGroups.ENTITY_2, CountGroupsIT.VERTEX);
        entity.putProperty(INT, 100);
        // When
        final GroupCounts counts = AbstractStoreIT.graph.execute(new Builder().first(new GetAllElements()).then(new CountGroups(limit)).build(), user);
        // Then
        int totalCount = (null != (counts.getEntityGroups().get(ENTITY))) ? counts.getEntityGroups().get(ENTITY) : 0;
        totalCount += (null != (counts.getEdgeGroups().get(EDGE))) ? counts.getEdgeGroups().get(EDGE) : 0;
        Assert.assertEquals(limit, totalCount);
    }
}

