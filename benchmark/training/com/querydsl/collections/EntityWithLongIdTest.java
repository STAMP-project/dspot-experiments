package com.querydsl.collections;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static QEntityWithLongId.entityWithLongId;


public class EntityWithLongIdTest {
    private List<EntityWithLongId> entities = Arrays.asList(new EntityWithLongId(999L), new EntityWithLongId(1000L), new EntityWithLongId(1001L), new EntityWithLongId(1003L));

    @Test
    public void simpleEquals() {
        QEntityWithLongId root = entityWithLongId;
        CollQuery<?> query = new CollQuery<Void>().from(root, entities);
        query.where(root.id.eq(1000L));
        Long found = query.select(root.id).fetchFirst();
        Assert.assertNotNull(found);
        Assert.assertEquals(found.longValue(), 1000);
    }

    @Test
    public void cartesianEquals() {
        QEntityWithLongId root = new QEntityWithLongId("root1");
        QEntityWithLongId root2 = new QEntityWithLongId("root2");
        Assert.assertEquals(entities.size(), new CollQuery<Void>().from(root, entities).from(root2, entities).where(root2.id.eq(root.id)).fetchCount());
    }

    @Test
    public void cartesianPlus1() {
        QEntityWithLongId root = new QEntityWithLongId("root1");
        QEntityWithLongId root2 = new QEntityWithLongId("root2");
        Assert.assertEquals(2, new CollQuery<Void>().from(root, entities).from(root2, entities).where(root2.id.eq(root.id.add(1))).fetchCount());
    }
}

