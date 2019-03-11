package uk.gov.gchq.gaffer.integration.impl;


import TestGroups.ENTITY_2;
import TestPropertyNames.INT;
import TestPropertyNames.TIMESTAMP;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.TraitRequirement;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.user.User;


public class StoreValidationIT extends AbstractStoreIT {
    private static final String VERTEX = "vertex";

    @Test
    @TraitRequirement(StoreTrait.STORE_VALIDATION)
    public void shouldAgeOffDataBasedOnTimestampAndAgeOffFunctionInSchema() throws InterruptedException, OperationException {
        // Given
        final User user = new User();
        final long now = System.currentTimeMillis();
        final Entity entity = new Entity(TestGroups.ENTITY_2, StoreValidationIT.VERTEX);
        entity.putProperty(TIMESTAMP, now);
        entity.putProperty(INT, 5);
        AbstractStoreIT.graph.execute(new AddElements.Builder().input(entity).build(), user);
        // When 1 - before age off
        final CloseableIterable<? extends Element> results1 = AbstractStoreIT.graph.execute(new GetElements.Builder().input(new EntitySeed(StoreValidationIT.VERTEX)).view(new View.Builder().entity(ENTITY_2).build()).build(), user);
        // Then 1
        final List<Element> results1List = Lists.newArrayList(results1);
        Assert.assertEquals(1, results1List.size());
        Assert.assertEquals(StoreValidationIT.VERTEX, getVertex());
        // Wait until after the age off time
        while (((System.currentTimeMillis()) - now) < (AbstractStoreIT.AGE_OFF_TIME)) {
            Thread.sleep(1000L);
        } 
        // When 2 - after age off
        final CloseableIterable<? extends Element> results2 = AbstractStoreIT.graph.execute(new GetElements.Builder().input(new EntitySeed(StoreValidationIT.VERTEX)).view(new View.Builder().entity(ENTITY_2).build()).build(), user);
        // Then 2
        final List<Element> results2List = Lists.newArrayList(results2);
        Assert.assertTrue(results2List.isEmpty());
    }

    @Test
    @TraitRequirement(StoreTrait.STORE_VALIDATION)
    public void shouldRemoveInvalidElements() throws OperationException {
        // Given
        final User user = new User();
        final Entity entity = new Entity(TestGroups.ENTITY_2, StoreValidationIT.VERTEX);
        entity.putProperty(INT, 100);
        // add elements but skip the validation
        AbstractStoreIT.graph.execute(new AddElements.Builder().input(Collections.<Element>singleton(entity)).validate(false).build(), user);
        // When
        final CloseableIterable<? extends Element> results1 = AbstractStoreIT.graph.execute(new GetElements.Builder().input(new EntitySeed(StoreValidationIT.VERTEX)).view(new View.Builder().entity(ENTITY_2).build()).build(), user);
        // Then
        final List<Element> results1List = Lists.newArrayList(results1);
        Assert.assertTrue(results1List.isEmpty());
    }
}

