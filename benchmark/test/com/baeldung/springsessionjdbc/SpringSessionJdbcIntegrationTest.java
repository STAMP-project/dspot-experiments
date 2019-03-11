package com.baeldung.springsessionjdbc;


import SpringBootTest.WebEnvironment;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.List;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SpringSessionJdbcIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void givenApiHasStarted_whenH2DbIsQueried_thenSessionTablesAreEmpty() throws SQLException {
        Assert.assertEquals(0, getSessionIdsFromDatabase().size());
        Assert.assertEquals(0, getSessionAttributeBytesFromDatabase().size());
    }

    @Test
    public void givenGetInvoked_whenH2DbIsQueried_thenOneSessionIsCreated() throws SQLException {
        assertThat(this.testRestTemplate.getForObject((("http://localhost:" + (port)) + "/"), String.class)).isNotEmpty();
        Assert.assertEquals(1, getSessionIdsFromDatabase().size());
    }

    @Test
    public void givenPostInvoked_whenH2DbIsQueried_thenSessionAttributeIsRetrieved() throws IOException, ClassNotFoundException, SQLException {
        MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap();
        map.add("color", "red");
        this.testRestTemplate.postForObject((("http://localhost:" + (port)) + "/saveColor"), map, String.class);
        List<byte[]> queryResponse = getSessionAttributeBytesFromDatabase();
        Assert.assertEquals(1, queryResponse.size());
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(queryResponse.get(0)));
        List<String> obj = ((List<String>) (in.readObject()));// Deserialize byte[] to object

        Assert.assertEquals("red", obj.get(0));
    }
}

