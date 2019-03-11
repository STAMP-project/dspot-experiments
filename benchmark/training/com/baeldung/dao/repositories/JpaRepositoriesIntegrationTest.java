package com.baeldung.dao.repositories;


import com.baeldung.config.PersistenceConfiguration;
import com.baeldung.config.PersistenceProductConfiguration;
import com.baeldung.config.PersistenceUserConfiguration;
import com.baeldung.domain.Item;
import com.baeldung.domain.ItemType;
import com.baeldung.domain.Location;
import com.baeldung.domain.Store;
import java.util.List;
import java.util.Optional;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = { PersistenceConfiguration.class, PersistenceUserConfiguration.class, PersistenceProductConfiguration.class })
public class JpaRepositoriesIntegrationTest {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ItemTypeRepository compositeRepository;

    @Autowired
    private ReadOnlyLocationRepository readOnlyRepository;

    @Test
    public void whenSaveLocation_ThenGetSameLocation() {
        Location location = new Location();
        location.setId(100L);
        location.setCountry("Country H");
        location.setCity("City Hundred");
        location = locationRepository.saveAndFlush(location);
        Location otherLocation = locationRepository.getOne(location.getId());
        TestCase.assertEquals("Country H", otherLocation.getCountry());
        TestCase.assertEquals("City Hundred", otherLocation.getCity());
        locationRepository.delete(otherLocation);
    }

    @Test
    public void givenLocationId_whenFindStores_thenGetStores() {
        List<Store> stores = storeRepository.findStoreByLocationId(1L);
        TestCase.assertEquals(1, stores.size());
    }

    @Test
    public void givenItemTypeId_whenDeleted_ThenItemTypeDeleted() {
        Optional<ItemType> itemType = compositeRepository.findById(1L);
        TestCase.assertTrue(itemType.isPresent());
        compositeRepository.deleteCustom(itemType.get());
        itemType = compositeRepository.findById(1L);
        TestCase.assertFalse(itemType.isPresent());
    }

    @Test
    public void givenItemId_whenUsingCustomRepo_ThenDeleteAppropriateEntity() {
        Item item = compositeRepository.findItemById(1L);
        TestCase.assertNotNull(item);
        compositeRepository.deleteCustom(item);
        item = compositeRepository.findItemById(1L);
        TestCase.assertNull(item);
    }

    @Test
    public void givenItemAndItemType_WhenAmbiguousDeleteCalled_ThenItemTypeDeletedAndNotItem() {
        Optional<ItemType> itemType = compositeRepository.findById(1L);
        TestCase.assertTrue(itemType.isPresent());
        Item item = compositeRepository.findItemById(2L);
        TestCase.assertNotNull(item);
        compositeRepository.findThenDelete(1L);
        Optional<ItemType> sameItemType = compositeRepository.findById(1L);
        TestCase.assertFalse(sameItemType.isPresent());
        Item sameItem = compositeRepository.findItemById(2L);
        TestCase.assertNotNull(sameItem);
    }

    @Test
    public void whenCreatingReadOnlyRepo_thenHaveOnlyReadOnlyOperationsAvailable() {
        Optional<Location> location = readOnlyRepository.findById(1L);
        TestCase.assertNotNull(location);
    }
}

