/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mongodb;


import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class MongoClientsIT {
    private static List<ServerAddress> addresses;

    private MongoClients clients;

    @Test
    public void shouldReturnSameInstanceForSameAddress() {
        MongoClientsIT.addresses.forEach(( address) -> {
            MongoClient client1 = clients.clientFor(address);
            MongoClient client2 = clients.clientFor(address);
            assertThat(client1).isSameAs(client2);
            MongoClient client3 = clients.clientFor(address.toString());
            MongoClient client4 = clients.clientFor(address);
            assertThat(client3).isSameAs(client4);
            assertThat(client3).isSameAs(client1);
            MongoClient client5 = clients.clientFor(address.toString());
            MongoClient client6 = clients.clientFor(address.toString());
            assertThat(client5).isSameAs(client6);
            assertThat(client5).isSameAs(client1);
        });
    }

    @Test
    public void shouldReturnSameInstanceForSameAddresses() {
        MongoClient client1 = clients.clientForMembers(MongoClientsIT.addresses);
        MongoClient client2 = clients.clientForMembers(MongoClientsIT.addresses);
        assertThat(client1).isSameAs(client2);
        ServerAddress[] array = MongoClientsIT.addresses.toArray(new ServerAddress[MongoClientsIT.addresses.size()]);
        MongoClient client3 = clients.clientForMembers(array);
        MongoClient client4 = clients.clientForMembers(array);
        assertThat(client3).isSameAs(client4);
        assertThat(client3).isSameAs(client1);
        String addressesStr = MongoUtil.toString(MongoClientsIT.addresses);
        MongoClient client5 = clients.clientForMembers(addressesStr);
        MongoClient client6 = clients.clientForMembers(addressesStr);
        assertThat(client5).isSameAs(client6);
        assertThat(client5).isSameAs(client1);
    }
}

