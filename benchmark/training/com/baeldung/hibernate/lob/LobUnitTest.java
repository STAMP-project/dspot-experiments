package com.baeldung.hibernate.lob;


import com.baeldung.hibernate.lob.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;


public class LobUnitTest {
    private Session session;

    @Test
    public void givenValidInsertLobObject_whenQueried_returnSameDataAsInserted() {
        User user = new User();
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("profile.png")) {
            // Get Image file from the resource
            if (inputStream == null)
                Assert.fail("Unable to get resources");

            user.setId("1");
            user.setName("User");
            user.setPhoto(IOUtils.toByteArray(inputStream));
            session.persist(user);
        } catch (IOException e) {
            Assert.fail("Unable to read input stream");
        }
        User result = session.find(User.class, "1");
        Assert.assertNotNull("Query result is null", result);
        Assert.assertEquals("User's name is invalid", user.getName(), result.getName());
        Assert.assertTrue("User's photo is corrupted", Arrays.equals(user.getPhoto(), result.getPhoto()));
    }
}

