package com.baeldung.hibernate.multitenancy.database;


import com.baeldung.hibernate.multitenancy.MultitenancyIntegrationTest;
import java.io.IOException;
import org.junit.Test;


public class DatabaseApproachMultitenancyIntegrationTest extends MultitenancyIntegrationTest {
    @Test
    public void givenDatabaseApproach_whenAddingEntries_thenOnlyAddedToConcreteDatabase() throws IOException {
        whenCurrentTenantIs(TenantIdNames.MYDB1);
        whenAddCar("myCar");
        thenCarFound("myCar");
        whenCurrentTenantIs(TenantIdNames.MYDB2);
        thenCarNotFound("myCar");
    }
}

