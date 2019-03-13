package dev.morphia.issue45;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import dev.morphia.query.FindOptions;
import dev.morphia.testutil.TestEntity;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unused")
public class TestEmptyEntityMapping extends TestBase {
    @Test
    public void testEmptyEmbeddedNotNullAfterReload() {
        TestEmptyEntityMapping.A a = new TestEmptyEntityMapping.A();
        a.b = new TestEmptyEntityMapping.B();
        getDs().save(a);
        Assert.assertNotNull(a.b);
        a = getDs().find(TestEmptyEntityMapping.A.class).filter("_id", a.getId()).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNull(a.b);
    }

    @Test
    public void testSizeOnEmptyElements() {
        TestEmptyEntityMapping.User u = new TestEmptyEntityMapping.User();
        u.setFullName("User Name");
        u.setUserId("USERID");
        getDs().save(u);
        Assert.assertNull("Should not find the user.", getDs().find(TestEmptyEntityMapping.User.class).filter("rights size", 0).find(new FindOptions().limit(1)).tryNext());
        Assert.assertNull("Should not find the user.", getDs().find(TestEmptyEntityMapping.User.class).field("rights").sizeEq(0).find(new FindOptions().limit(1)).tryNext());
        Assert.assertNotNull("Should find the user.", getDs().find(TestEmptyEntityMapping.User.class).field("rights").doesNotExist().find(new FindOptions().limit(1)).next());
        getDs().delete(getDs().find(TestEmptyEntityMapping.User.class));
        u = new TestEmptyEntityMapping.User();
        u.setFullName("User Name");
        u.setUserId("USERID");
        u.getRights().add(TestEmptyEntityMapping.Rights.ADMIN);
        getDs().save(u);
        Assert.assertNotNull("Should find the user.", getDs().find(TestEmptyEntityMapping.User.class).filter("rights size", 1).find(new FindOptions().limit(1)).next());
        Assert.assertNotNull("Should find the user.", getDs().find(TestEmptyEntityMapping.User.class).field("rights").sizeEq(1).find(new FindOptions().limit(1)).next());
        Assert.assertNotNull("Should find the user.", getDs().find(TestEmptyEntityMapping.User.class).field("rights").exists().find(new FindOptions().limit(1)).next());
    }

    @Entity
    public enum Rights {

        ADMIN;}

    @Entity
    static class A extends TestEntity {
        @Embedded
        private TestEmptyEntityMapping.B b;
    }

    @Embedded
    static class B {
        @Transient
        private String foo;
    }

    @Entity
    public static class UserType extends TestEntity {}

    @Entity
    public static class NotificationAddress extends TestEntity {}

    @Entity
    public static class User extends TestEntity {
        private String userId;

        private String fullName;

        @Embedded
        private TestEmptyEntityMapping.UserType userType;

        @Embedded
        private Set<TestEmptyEntityMapping.Rights> rights = new HashSet<TestEmptyEntityMapping.Rights>();

        @Embedded
        private Set<TestEmptyEntityMapping.NotificationAddress> notificationAddresses = new HashSet<TestEmptyEntityMapping.NotificationAddress>();

        public String getFullName() {
            return fullName;
        }

        public void setFullName(final String fullName) {
            this.fullName = fullName;
        }

        public Set<TestEmptyEntityMapping.NotificationAddress> getNotificationAddresses() {
            return notificationAddresses;
        }

        public void setNotificationAddresses(final Set<TestEmptyEntityMapping.NotificationAddress> notificationAddresses) {
            this.notificationAddresses = notificationAddresses;
        }

        public Set<TestEmptyEntityMapping.Rights> getRights() {
            return rights;
        }

        public void setRights(final Set<TestEmptyEntityMapping.Rights> rights) {
            this.rights = rights;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(final String userId) {
            this.userId = userId;
        }

        public TestEmptyEntityMapping.UserType getUserType() {
            return userType;
        }

        public void setUserType(final TestEmptyEntityMapping.UserType userType) {
            this.userType = userType;
        }
    }
}

