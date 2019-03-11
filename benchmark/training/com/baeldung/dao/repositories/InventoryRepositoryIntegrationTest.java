package com.baeldung.dao.repositories;


import com.baeldung.config.PersistenceConfiguration;
import com.baeldung.domain.MerchandiseEntity;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = { PersistenceConfiguration.class })
public class InventoryRepositoryIntegrationTest {
    private static final String ORIGINAL_TITLE = "Pair of Pants";

    private static final String UPDATED_TITLE = "Branded Luxury Pants";

    private static final String UPDATED_BRAND = "Armani";

    private static final String ORIGINAL_SHORTS_TITLE = "Pair of Shorts";

    @Autowired
    private InventoryRepository repository;

    @Test
    public void shouldCreateNewEntryInDB() {
        MerchandiseEntity pants = new MerchandiseEntity(InventoryRepositoryIntegrationTest.ORIGINAL_TITLE, BigDecimal.ONE);
        pants = repository.save(pants);
        MerchandiseEntity shorts = new MerchandiseEntity(InventoryRepositoryIntegrationTest.ORIGINAL_SHORTS_TITLE, new BigDecimal(3));
        shorts = repository.save(shorts);
        Assert.assertNotNull(pants.getId());
        Assert.assertNotNull(shorts.getId());
        Assert.assertNotEquals(pants.getId(), shorts.getId());
    }

    @Test
    public void shouldUpdateExistingEntryInDB() {
        MerchandiseEntity pants = new MerchandiseEntity(InventoryRepositoryIntegrationTest.ORIGINAL_TITLE, BigDecimal.ONE);
        pants = repository.save(pants);
        Long originalId = pants.getId();
        pants.setTitle(InventoryRepositoryIntegrationTest.UPDATED_TITLE);
        pants.setPrice(BigDecimal.TEN);
        pants.setBrand(InventoryRepositoryIntegrationTest.UPDATED_BRAND);
        MerchandiseEntity result = repository.save(pants);
        Assert.assertEquals(originalId, result.getId());
        Assert.assertEquals(InventoryRepositoryIntegrationTest.UPDATED_TITLE, result.getTitle());
        Assert.assertEquals(BigDecimal.TEN, result.getPrice());
        Assert.assertEquals(InventoryRepositoryIntegrationTest.UPDATED_BRAND, result.getBrand());
    }
}

