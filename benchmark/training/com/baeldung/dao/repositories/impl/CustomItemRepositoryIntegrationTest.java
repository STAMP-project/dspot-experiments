package com.baeldung.dao.repositories.impl;


import com.baeldung.config.PersistenceConfiguration;
import com.baeldung.config.PersistenceProductConfiguration;
import com.baeldung.config.PersistenceUserConfiguration;
import com.baeldung.dao.repositories.CustomItemRepository;
import com.baeldung.domain.Item;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;


@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = { PersistenceConfiguration.class, PersistenceUserConfiguration.class, PersistenceProductConfiguration.class })
public class CustomItemRepositoryIntegrationTest {
    @Autowired
    CustomItemRepository customItemRepositoryImpl;

    @Autowired
    EntityManager entityManager;

    @Test
    public void givenItems_whenFindItemsByColorAndGrade_thenReturnItems() {
        List<Item> items = customItemRepositoryImpl.findItemsByColorAndGrade();
        Assert.assertFalse("No items found", CollectionUtils.isEmpty(items));
        Assert.assertEquals("There should be only one item", 1, items.size());
        Item item = items.get(0);
        Assert.assertEquals("this item do not have blue color", "blue", item.getColor());
        Assert.assertEquals("this item does not belong to A grade", "A", item.getGrade());
    }

    @Test
    public void givenItems_whenFindItemByColorOrGrade_thenReturnItems() {
        List<Item> items = customItemRepositoryImpl.findItemByColorOrGrade();
        Assert.assertFalse("No items found", CollectionUtils.isEmpty(items));
        Assert.assertEquals("There should be only one item", 1, items.size());
        Item item = items.get(0);
        Assert.assertEquals("this item do not have red color", "red", item.getColor());
        Assert.assertEquals("this item does not belong to D grade", "D", item.getGrade());
    }
}

