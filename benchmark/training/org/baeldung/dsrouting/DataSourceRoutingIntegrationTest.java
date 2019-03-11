package org.baeldung.dsrouting;


import ClientDatabase.CLIENT_A;
import ClientDatabase.CLIENT_B;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DataSourceRoutingTestConfiguration.class)
@DirtiesContext
public class DataSourceRoutingIntegrationTest {
    @Autowired
    DataSource routingDatasource;

    @Autowired
    ClientService clientService;

    @Test
    public void givenClientDbs_whenContextsSwitch_thenRouteToCorrectDatabase() throws Exception {
        // test ACME WIDGETS
        String clientName = clientService.getClientName(CLIENT_A);
        Assert.assertEquals(clientName, "CLIENT A");
        // test WIDGETS_ARE_US
        clientName = clientService.getClientName(CLIENT_B);
        Assert.assertEquals(clientName, "CLIENT B");
    }
}

