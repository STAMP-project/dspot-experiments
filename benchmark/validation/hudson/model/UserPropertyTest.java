package hudson.model;


import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.recipes.LocalData;
import org.kohsuke.stapler.DataBoundConstructor;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class UserPropertyTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("JENKINS-9062")
    public void test() throws Exception {
        User u = User.get("foo");
        u.addProperty(new UserPropertyTest.UserProperty1());
        j.configRoundtrip(u);
        for (UserProperty p : u.getAllProperties())
            Assert.assertNotNull(p);

    }

    public static class UserProperty1 extends UserProperty {
        @TestExtension
        public static class DescriptorImpl extends UserPropertyDescriptor {
            @Override
            public UserProperty newInstance(User user) {
                return new UserPropertyTest.UserProperty1();
            }
        }
    }

    public static class UserProperty2 extends UserProperty {
        @TestExtension
        public static class DescriptorImpl extends UserPropertyDescriptor {
            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public UserProperty newInstance(User user) {
                return new UserPropertyTest.UserProperty1();
            }
        }
    }

    @Test
    @LocalData
    public void nestedUserReference() throws Exception {
        // first time it loads from FS into object
        User user = User.get("nestedUserReference", false, Collections.emptyMap());
        Assert.assertThat("nested reference should be updated after jenkins start", user, UserPropertyTest.nestedUserSet());
        UserPropertyTest.SetUserUserProperty property = user.getProperty(UserPropertyTest.SetUserUserProperty.class);
        File testFile = property.getInnerUserClass().userFile;
        List<String> fileLines = FileUtils.readLines(testFile);
        Assert.assertThat(fileLines, Matchers.hasSize(1));
        j.configRoundtrip(user);
        user = User.get("nestedUserReference", false, Collections.emptyMap());
        Assert.assertThat("nested reference should exist after user configuration change", user, UserPropertyTest.nestedUserSet());
        fileLines = FileUtils.readLines(testFile);
        Assert.assertThat(fileLines, Matchers.hasSize(1));
    }

    /**
     * User property that need update User object reference for InnerUserClass.
     */
    public static class SetUserUserProperty extends UserProperty {
        private UserPropertyTest.InnerUserClass innerUserClass = new UserPropertyTest.InnerUserClass();

        @DataBoundConstructor
        public SetUserUserProperty() {
        }

        public UserPropertyTest.InnerUserClass getInnerUserClass() {
            return innerUserClass;
        }

        public User getOwner() {
            return user;
        }

        @Override
        protected void setUser(User u) {
            super.setUser(u);
            innerUserClass.setUser(u);
        }

        public Object readResolve() {
            if ((innerUserClass) == null) {
                innerUserClass = new UserPropertyTest.InnerUserClass();
            }
            return this;
        }

        @TestExtension
        public static class DescriptorImpl extends UserPropertyDescriptor {
            @Override
            public UserProperty newInstance(User user) {
                if (user.getId().equals("nesteduserreference")) {
                    return new UserPropertyTest.SetUserUserProperty();
                }
                return null;
            }
        }
    }

    /**
     * Class that should get setUser(User) object reference update.
     */
    public static class InnerUserClass extends AbstractDescribableImpl<UserPropertyTest.InnerUserClass> {
        private transient User user;

        private transient File userFile;

        @DataBoundConstructor
        public InnerUserClass() {
        }

        public User getUser() {
            return user;
        }

        /**
         * Should be initialised separately.
         */
        public void setUser(User user) {
            this.user = user;
            try {
                File userFile = getUserFile();
                FileUtils.writeStringToFile(userFile, String.valueOf(System.currentTimeMillis()), true);
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }

        private File getUserFile() throws IOException {
            userFile = File.createTempFile("user", ".txt");
            userFile.deleteOnExit();
            if (!(userFile.exists())) {
                userFile.createNewFile();
            }
            return userFile;
        }

        @Override
        public UserPropertyTest.InnerUserClass.DescriptorImpl getDescriptor() {
            return ((UserPropertyTest.InnerUserClass.DescriptorImpl) (super.getDescriptor()));
        }

        @TestExtension
        public static class DescriptorImpl extends Descriptor<UserPropertyTest.InnerUserClass> {}
    }
}

