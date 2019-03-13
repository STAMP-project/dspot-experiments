package com.alibaba.fastjson.support.jaxrs;


import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


public class TestIssue885 extends JerseyTest {
    @Path("user")
    public static class UserResource {
        @GET
        public User getUser() {
            User user = new User();
            user.setId(12345L);
            user.setName("smallnest");
            user.setCreatedOn(new Date());
            return user;
        }
    }

    @Test
    public void testWriteTo() {
        final String user = target("user").request().accept("application/json").get(String.class);
        // {"createdOn":1412036891919,"id":12345,"name":"smallnest"}]
        Assert.assertTrue(((user.indexOf("createdOn")) > 0));
        Assert.assertTrue(((user.indexOf("\"id\":12345")) > 0));
        Assert.assertTrue(((user.indexOf("\"name\":\"smallnest\"")) > 0));
    }

    @Test
    public void testWriteToWithPretty() {
        // System.out.println("@@@@@Test Pretty");
        final String user = target("user").queryParam("pretty", "true").request().accept("application/json").get(String.class);
        // {"createdOn":1412036891919,"id":12345,"name":"smallnest"}]
        Assert.assertTrue(((user.indexOf("createdOn")) > 0));
        Assert.assertTrue(((user.indexOf("\"id\":12345")) > 0));
        Assert.assertTrue(((user.indexOf("\"name\":\"smallnest\"")) > 0));
        // response does not contain a return character
        // assertTrue(user.indexOf("\n\t") > 0);
    }

    @Test
    public void testReadFrom() {
        final User user = target("user").request().accept("application/json").get(User.class);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getCreatedOn());
        Assert.assertEquals(user.getId().longValue(), 12345L);
        Assert.assertEquals(user.getName(), "smallnest");
    }
}

