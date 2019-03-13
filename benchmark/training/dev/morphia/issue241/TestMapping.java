package dev.morphia.issue241;


import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.dao.BasicDAO;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for testing morphia mappings with generics.
 */
public class TestMapping extends TestBase {
    @Test
    public void testMapping() {
        final BasicDAO<TestMapping.Message, ObjectId> messageDAO = new BasicDAO<TestMapping.Message, ObjectId>(TestMapping.Message.class, getDs());
        Assert.assertNotNull(messageDAO);
    }

    @Entity
    private static class Message<U extends TestMapping.User> {
        @Id
        private ObjectId id;

        private U user;

        public U getUser() {
            return user;
        }

        public void setUser(final U user) {
            this.user = user;
        }
    }

    @Entity
    private static class User {
        @Id
        private ObjectId id;

        @Override
        public int hashCode() {
            int hash = 3;
            hash = (97 * hash) + ((id) != null ? id.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            final TestMapping.User other = ((TestMapping.User) (obj));
            return !(((id) != (other.id)) && (((id) == null) || (!(id.equals(other.id)))));
        }
    }
}

