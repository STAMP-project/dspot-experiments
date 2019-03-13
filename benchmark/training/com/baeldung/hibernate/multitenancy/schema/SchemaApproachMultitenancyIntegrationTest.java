package com.baeldung.hibernate.multitenancy.schema;


import com.baeldung.hibernate.multitenancy.MultitenancyIntegrationTest;
import com.baeldung.hibernate.multitenancy.database.TenantIdNames;
import java.io.IOException;
import org.junit.Test;


public class SchemaApproachMultitenancyIntegrationTest extends MultitenancyIntegrationTest {
    @Test
    public void givenSchemaApproach_whenAddingEntries_thenOnlyAddedToConcreteSchema() throws IOException {
        whenCurrentTenantIs(TenantIdNames.MYDB1);
        whenAddCar("Ferrari");
        thenCarFound("Ferrari");
        whenCurrentTenantIs(TenantIdNames.MYDB2);
        thenCarNotFound("Ferrari");
    }
}

