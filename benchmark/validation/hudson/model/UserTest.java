/**
 * The MIT License
 *
 * Copyright (c) 2004-2012, Sun Microsystems, Inc., Kohsuke Kawaguchi, Erik Ramfelt,
 * Vincent Latombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.model;


import Jenkins.ADMINISTER;
import Jenkins.ANONYMOUS;
import JenkinsRule.DummySecurityRealm;
import JenkinsRule.WebClient;
import Permission.READ;
import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import hudson.ExtensionList;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.AbstractPasswordBasedSecurityRealm;
import hudson.security.AccessDeniedException2;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.GroupDetails;
import hudson.security.HudsonPrivateSecurityRealm;
import hudson.security.UserMayOrMayNotExistException;
import hudson.tasks.MailAddressResolver;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import jenkins.model.IdStrategy;
import jenkins.security.ApiTokenProperty;
import jenkins.security.apitoken.ApiTokenTestHelper;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.jvnet.hudson.test.FakeChangeLogSCM;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.SmokeTest;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.recipes.LocalData;


@Category(SmokeTest.class)
public class UserTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    public static class UserPropertyImpl extends UserProperty implements Action {
        private final String testString;

        private UserPropertyDescriptor descriptorImpl = new UserTest.UserPropertyImpl.UserPropertyDescriptorImpl();

        public UserPropertyImpl(String testString) {
            this.testString = testString;
        }

        public String getTestString() {
            return testString;
        }

        @Override
        public UserPropertyDescriptor getDescriptor() {
            return descriptorImpl;
        }

        public String getIconFileName() {
            return "/images/24x24/gear.png";
        }

        public String getDisplayName() {
            return "UserPropertyImpl";
        }

        public String getUrlName() {
            return "userpropertyimpl";
        }

        public static class UserPropertyDescriptorImpl extends UserPropertyDescriptor {
            @Override
            public UserProperty newInstance(User user) {
                return null;
            }
        }
    }

    @Issue("JENKINS-2331")
    @Test
    public void userPropertySummaryAndActionAreShownInUserPage() throws Exception {
        UserProperty property = new UserTest.UserPropertyImpl("NeedleInPage");
        UserProperty.all().add(property.getDescriptor());
        User user = User.get("user-test-case");
        user.addProperty(property);
        HtmlPage page = j.createWebClient().goTo("user/user-test-case");
        WebAssert.assertTextPresentInElement(page, "NeedleInPage", "main-panel");
        WebAssert.assertTextPresentInElement(page, getDisplayName(), "side-panel");
    }

    /**
     * Asserts that the default user avatar can be fetched (ie no 404)
     */
    @Issue("JENKINS-7494")
    @Test
    public void defaultUserAvatarCanBeFetched() throws Exception {
        User user = User.get("avatar-user", true);
        HtmlPage page = j.createWebClient().goTo(("user/" + (user.getDisplayName())));
        j.assertAllImageLoadSuccessfully(page);
    }

    @Test
    public void getAuthorities() throws Exception {
        JenkinsRule.DummySecurityRealm realm = j.createDummySecurityRealm();
        realm.addGroups("administrator", "admins");
        realm.addGroups("alice", "users");
        realm.addGroups("bob", "users", "lpadmin", "bob");
        j.jenkins.setSecurityRealm(realm);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        auth.add(ADMINISTER, "admins");
        auth.add(READ, "users");
        j.jenkins.setAuthorizationStrategy(auth);
        SecurityContext seccon = SecurityContextHolder.getContext();
        Authentication orig = seccon.getAuthentication();
        try {
            seccon.setAuthentication(User.get("administrator").impersonate());
            Assert.assertEquals("[admins]", User.get("administrator").getAuthorities().toString());
            Assert.assertEquals("[users]", User.get("alice").getAuthorities().toString());
            Assert.assertEquals("[lpadmin, users]", User.get("bob").getAuthorities().toString());
            Assert.assertEquals("[]", User.get("MasterOfXaos").getAuthorities().toString());
            seccon.setAuthentication(User.get("alice").impersonate());
            Assert.assertEquals("[]", User.get("alice").getAuthorities().toString());
            Assert.assertEquals("[]", User.get("bob").getAuthorities().toString());
        } finally {
            seccon.setAuthentication(orig);
        }
    }

    @Test
    public void testGetUser() throws Exception {
        {
            User user = User.get("John Smith");
            User user2 = User.get("John Smith2");
            user2.setFullName("John Smith");
            Assert.assertNotSame("Users should not have the same id.", user.getId(), user2.getId());
        }
        j.jenkins.reload();
        {
            User user3 = User.get("John Smith");
            user3.setFullName("Alice Smith");
            Assert.assertEquals("What was this asserting exactly?", "John Smith", user3.getId());
            User user4 = User.get("Marie", false, Collections.EMPTY_MAP);
            Assert.assertNull("User should not be created because Marie does not exists.", user4);
        }
    }

    @Test
    public void caseInsensitivity() {
        j.jenkins.setSecurityRealm(new UserTest.IdStrategySpecifyingSecurityRealm(new IdStrategy.CaseInsensitive()));
        User user = User.get("john smith");
        User user2 = User.get("John Smith");
        Assert.assertSame("Users should have the same id.", user.getId(), user2.getId());
    }

    @Test
    public void caseSensitivity() {
        j.jenkins.setSecurityRealm(new UserTest.IdStrategySpecifyingSecurityRealm(new IdStrategy.CaseSensitive()));
        User user = User.get("john smith");
        User user2 = User.get("John Smith");
        Assert.assertNotSame("Users should not have the same id.", user.getId(), user2.getId());
        Assert.assertEquals("john smith", User.idStrategy().keyFor(user.getId()));
        Assert.assertEquals("John Smith", User.idStrategy().keyFor(user2.getId()));
    }

    @Test
    public void caseSensitivityEmail() {
        j.jenkins.setSecurityRealm(new UserTest.IdStrategySpecifyingSecurityRealm(new IdStrategy.CaseSensitiveEmailAddress()));
        User user = User.get("john.smith@acme.org");
        User user2 = User.get("John.Smith@acme.org");
        Assert.assertNotSame("Users should not have the same id.", user.getId(), user2.getId());
        Assert.assertEquals("john.smith@acme.org", User.idStrategy().keyFor(user.getId()));
        Assert.assertEquals("John.Smith@acme.org", User.idStrategy().keyFor(user2.getId()));
        user2 = User.get("john.smith@ACME.ORG");
        Assert.assertEquals("Users should have the same id.", user.getId(), user2.getId());
        Assert.assertEquals("john.smith@acme.org", User.idStrategy().keyFor(user2.getId()));
    }

    private static class IdStrategySpecifyingSecurityRealm extends HudsonPrivateSecurityRealm {
        private final IdStrategy idStrategy;

        IdStrategySpecifyingSecurityRealm(IdStrategy idStrategy) {
            super(true, false, null);
            this.idStrategy = idStrategy;
        }

        @Override
        public IdStrategy getUserIdStrategy() {
            return idStrategy;
        }
    }

    @Test
    public void testAddAndGetProperty() throws Exception {
        {
            User user = User.get("John Smith");
            UserProperty prop = new UserTest.SomeUserProperty();
            user.addProperty(prop);
            Assert.assertNotNull("User should have SomeUserProperty property.", user.getProperty(UserTest.SomeUserProperty.class));
            Assert.assertEquals("UserProperty1 should be assigned to its descriptor", prop, user.getProperties().get(prop.getDescriptor()));
            Assert.assertTrue("User should should contain SomeUserProperty.", user.getAllProperties().contains(prop));
        }
        j.jenkins.reload();
        {
            Assert.assertNotNull("User should have SomeUserProperty property.", User.getById("John Smith", false).getProperty(UserTest.SomeUserProperty.class));
        }
    }

    @Test
    public void testImpersonateAndCurrent() {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        User user = User.get("John Smith");
        Assert.assertNotSame("User John Smith should not be the current user.", User.current().getId(), user.getId());
        SecurityContextHolder.getContext().setAuthentication(user.impersonate());
        Assert.assertEquals("User John Smith should be the current user.", user.getId(), User.current().getId());
    }

    @Test
    public void testGetUnknown() {
        User user = User.get("John Smith");
        Assert.assertNotNull("User should not be null.", user);
    }

    @Test
    public void testGetAndGetAll() {
        User user = User.get("John Smith", false, Collections.emptyMap());
        Assert.assertNull("User John Smith should not be created.", user);
        Assert.assertFalse("Jenkins should not contain user John Smith.", User.getAll().contains(user));
        User user2 = User.get("John Smith2", true, Collections.emptyMap());
        Assert.assertNotNull("User John Smith2 should be created.", user2);
        Assert.assertTrue("Jenkins should contain user John Smith2.", User.getAll().contains(user2));
        user = User.get("John Smith2", false, Collections.emptyMap());
        Assert.assertNotNull("User John Smith should be created.", user);
        Assert.assertTrue("Jenkins should contain user John Smith.", User.getAll().contains(user));
    }

    @Test
    public void testReload() throws Exception {
        String originalName = "John Smith";
        User user = User.get(originalName, true, Collections.emptyMap());
        user.save();
        String temporaryName = "Alice Smith";
        user.setFullName(temporaryName);
        j.jenkins.reload();
        user = User.get(originalName, false, Collections.emptyMap());
        Assert.assertEquals("User should have original name.", originalName, user.getFullName());
    }

    @Test
    public void testGetBuildsAndGetProjects() throws Exception {
        User user = User.get("John Smith", true, Collections.emptyMap());
        FreeStyleProject project = j.createFreeStyleProject("free");
        FreeStyleProject project2 = j.createFreeStyleProject("free2");
        project.save();
        FakeChangeLogSCM scm = new FakeChangeLogSCM();
        scm.addChange().withAuthor(user.getId());
        project.setScm(scm);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project2);
        Build build = project.getLastBuild();
        Build build2 = project2.getLastBuild();
        Assert.assertTrue("User should participate in the last build of project free.", user.getBuilds().contains(build));
        Assert.assertFalse("User should not participate in the last build of project free2.", user.getBuilds().contains(build2));
        Assert.assertTrue("User should participate in the project free.", user.getProjects().contains(project));
        Assert.assertFalse("User should not participate in the project free2.", user.getProjects().contains(project2));
        // JENKINS-16178: build should include also builds scheduled by user
        build2.replaceAction(new CauseAction(new Cause.UserIdCause()));
        Assert.assertFalse("User should not participate in the last build of project free2.", user.getBuilds().contains(build2));
        Assert.assertFalse("Current user should not participate in the last build of project free.", User.current().getBuilds().contains(build));
        Assert.assertTrue("Current user should participate in the last build of project free2.", User.current().getBuilds().contains(build2));
    }

    @Test
    public void testSave() throws Exception {
        {
            User user = User.get("John Smith", true, Collections.emptyMap());
        }
        j.jenkins.reload();
        {
            User user = User.get("John Smith", false, Collections.emptyMap());
            Assert.assertNull("User should be null.", user);
            user = User.get("John Smithl", true, Collections.emptyMap());
            user.addProperty(new UserTest.SomeUserProperty());
            user.save();
        }
        j.jenkins.reload();
        {
            User user = User.get("John Smithl", false, Collections.emptyMap());
            Assert.assertNotNull("User should not be null.", user);
            Assert.assertNotNull("User should be saved with all changes.", user.getProperty(UserTest.SomeUserProperty.class));
        }
    }

    @Issue("JENKINS-16332")
    @Test
    public void unrecoverableFullName() throws Throwable {
        String id;
        {
            User u = User.get("John Smith <jsmith@nowhere.net>");
            Assert.assertEquals("jsmith@nowhere.net", MailAddressResolver.resolve(u));
            id = u.getId();
        }
        j.jenkins.reload();
        {
            User u = User.get(id);
            Assert.assertEquals("jsmith@nowhere.net", MailAddressResolver.resolve(u));
        }
    }

    @Test
    public void testDelete() throws Exception {
        {
            User user = User.get("John Smith", true, Collections.emptyMap());
            user.save();
            File configFolder = user.getUserFolder();
            user.delete();
            Assert.assertFalse("User should be deleted with his persistent data.", configFolder.exists());
            Assert.assertFalse("User should be deleted from memory.", User.getAll().contains(user));
            user = User.get("John Smith", false, Collections.emptyMap());
            Assert.assertNull("User should be deleted from memory.", user);
        }
        j.jenkins.reload();
        {
            boolean contained = false;
            for (User u : User.getAll()) {
                if (u.getId().equals("John Smith")) {
                    contained = true;
                    break;
                }
            }
            Assert.assertFalse("User should not be loaded.", contained);
        }
    }

    @Test
    public void testDoConfigSubmit() throws Exception {
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
        j.jenkins.setSecurityRealm(realm);
        User user = realm.createAccount("John Smith", "password");
        User user2 = realm.createAccount("John Smith2", "password");
        user2.save();
        auth.add(ADMINISTER, user.getId());
        auth.add(Jenkins.READ, user2.getId());
        SecurityContextHolder.getContext().setAuthentication(user.impersonate());
        HtmlForm form = j.createWebClient().withBasicCredentials(user.getId(), "password").goTo(((user2.getUrl()) + "/configure")).getFormByName("config");
        form.getInputByName("_.fullName").setValueAttribute("Alice Smith");
        j.submit(form);
        Assert.assertEquals("User should have full name Alice Smith.", "Alice Smith", user2.getFullName());
        SecurityContextHolder.getContext().setAuthentication(user2.impersonate());
        try {
            user.doConfigSubmit(null, null);
            Assert.fail("User should not have permission to configure another user.");
        } catch (Exception e) {
            if (!(e.getClass().isAssignableFrom(AccessDeniedException2.class))) {
                Assert.fail("AccessDeniedException should be thrown.");
            }
        }
        form = j.createWebClient().withBasicCredentials(user2.getId(), "password").goTo(((user2.getUrl()) + "/configure")).getFormByName("config");
        form.getInputByName("_.fullName").setValueAttribute("John");
        j.submit(form);
        Assert.assertEquals("User should be albe to configure himself.", "John", user2.getFullName());
    }

    /* TODO cannot follow what this is purporting to test
    @Test
    public void testDoDoDelete() throws Exception {
    GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
    j.jenkins.setAuthorizationStrategy(auth);
    j.jenkins.setCrumbIssuer(null);
    HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
    j.jenkins.setSecurityRealm(realm);
    User user = realm.createAccount("John Smith", "password");
    User user2 = realm.createAccount("John Smith2", "password");
    user2.save();
    auth.add(Jenkins.ADMINISTER, user.getId());
    auth.add(Jenkins.READ, user2.getId());
    SecurityContextHolder.getContext().setAuthentication(user.impersonate());
    HtmlForm form = j.createWebClient().login(user.getId(), "password").goTo(user2.getUrl() + "/delete").getFormByName("delete");
    j.submit(form);
    assertFalse("User should be deleted from memory.", User.getAll().contains(user2));
    assertFalse("User should be deleted with his persistent data.", user2.getConfigFile().exists());
    User.reload();
    assertNull("Deleted user should not be loaded.", User.get(user2.getId(),false, Collections.EMPTY_MAP));
    user2 = realm.createAccount("John Smith2", "password");
    SecurityContextHolder.getContext().setAuthentication(user2.impersonate());
    try{
    user.doDoDelete(null, null);
    fail("User should not have permission to delete another user.");
    }
    catch(Exception e){
    if(!(e.getClass().isAssignableFrom(AccessDeniedException2.class))){
    fail("AccessDeniedException should be thrown.");
    }
    }
    user.save();
    JenkinsRule.WebClient client = j.createWebClient();
    form = client.login(user.getId(), "password").goTo(user.getUrl() + "/delete").getFormByName("delete");
    try{
    j.submit(form);
    fail("User should not be able to delete himself");
    }
    catch(FailingHttpStatusCodeException e){
    //ok exception should be thrown
    Assert.assertEquals(400, e.getStatusCode());
    }
    assertTrue("User should not delete himself from memory.", User.getAll().contains(user));
    assertTrue("User should not delete his persistent data.", user.getConfigFile().exists());
    }
     */
    @Test
    public void testHasPermission() throws IOException {
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
        j.jenkins.setSecurityRealm(realm);
        User user = realm.createAccount("John Smith", "password");
        User user2 = realm.createAccount("John Smith2", "password");
        SecurityContextHolder.getContext().setAuthentication(user.impersonate());
        Assert.assertFalse("Current user should not have permission read.", user2.hasPermission(READ));
        Assert.assertTrue("Current user should always have permission read to himself.", user.hasPermission(READ));
        auth.add(ADMINISTER, user.getId());
        Assert.assertTrue("Current user should have permission read, because he has permission administer.", user2.hasPermission(READ));
        SecurityContextHolder.getContext().setAuthentication(ANONYMOUS);
        user2 = User.get("anonymous");
        Assert.assertFalse("Current user should not have permission read, because does not have global permission read and authentication is anonymous.", user2.hasPermission(READ));
    }

    @Test
    public void testCanDelete() throws IOException {
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
        j.jenkins.setSecurityRealm(realm);
        User user = realm.createAccount("John Smith", "password");
        User user2 = realm.createAccount("John Smith2", "password");
        user2.save();
        SecurityContextHolder.getContext().setAuthentication(user.impersonate());
        Assert.assertFalse("Ordinary user cannot delete somebody else", user2.canDelete());
        auth.add(ADMINISTER, user.getId());
        Assert.assertTrue("Administrator can delete anybody else", user2.canDelete());
        Assert.assertFalse("User (even admin) cannot delete himself", user.canDelete());
        SecurityContextHolder.getContext().setAuthentication(user2.impersonate());
        auth.add(ADMINISTER, user2.getId());
        User user3 = User.get("Random Somebody");
        Assert.assertFalse("Storage-less temporary user cannot be deleted", user3.canDelete());
        user3.save();
        Assert.assertTrue("But once storage is allocated, he can be deleted", user3.canDelete());
    }

    // @Issue("SECURITY-180")
    @Test
    public void security180() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        final GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        User alice = User.get("alice");
        User bob = User.get("bob");
        User admin = User.get("admin");
        auth.add(Jenkins.READ, alice.getId());
        auth.add(Jenkins.READ, bob.getId());
        auth.add(ADMINISTER, admin.getId());
        // Admin can change everyone's token
        SecurityContextHolder.getContext().setAuthentication(admin.impersonate());
        admin.getProperty(ApiTokenProperty.class).changeApiToken();
        alice.getProperty(ApiTokenProperty.class).changeApiToken();
        // User can change only own token
        SecurityContextHolder.getContext().setAuthentication(bob.impersonate());
        bob.getProperty(ApiTokenProperty.class).changeApiToken();
        try {
            alice.getProperty(ApiTokenProperty.class).changeApiToken();
            Assert.fail("Bob should not be authorized to change alice's token");
        } catch (AccessDeniedException expected) {
        }
        // ANONYMOUS can not change any token
        SecurityContextHolder.getContext().setAuthentication(ANONYMOUS);
        try {
            alice.getProperty(ApiTokenProperty.class).changeApiToken();
            Assert.fail("Anonymous should not be authorized to change alice's token");
        } catch (AccessDeniedException expected) {
        }
    }

    @Issue("SECURITY-243")
    @Test
    public void resolveByIdThenName() throws Exception {
        j.jenkins.setSecurityRealm(new HudsonPrivateSecurityRealm(true, false, null));
        User u1 = User.get("user1");
        u1.setFullName("User One");
        u1.save();
        User u2 = User.get("user2");
        u2.setFullName("User Two");
        u2.save();
        Assert.assertNotSame("Users should not have the same id.", u1.getId(), u2.getId());
        User u = User.get("User One");
        Assert.assertEquals("'User One' should resolve to u1", u1.getId(), u.getId());
        u = User.get("User Two");
        Assert.assertEquals("'User Two' should resolve to u2", u2.getId(), u.getId());
        u = User.get("user1");
        Assert.assertEquals("'user1' should resolve to u1", u1.getId(), u.getId());
        u = User.get("user2");
        Assert.assertEquals("'user2' should resolve to u2", u2.getId(), u.getId());
        u1.setFullName("user2");
        u1.save();
        u = User.get("user2");
        Assert.assertEquals("'user2' should resolve to u2", u2.getId(), u.getId());
        u = User.get("user1");
        Assert.assertEquals("'user1' should resolve to u1", u1.getId(), u.getId());
        u1.setFullName("user1");
        u1.save();
        u2.setFullName("user1");
        u2.save();
        u = User.get("user1");
        Assert.assertEquals("'user1' should resolve to u1", u1.getId(), u.getId());
        u = User.get("user2");
        Assert.assertEquals("'user2' should resolve to u2", u2.getId(), u.getId());
    }

    @Issue("SECURITY-243")
    @Test
    public void resolveByUnloadedIdThenName() throws Exception {
        j.jenkins.setSecurityRealm(new UserTest.ExternalSecurityRealm());
        // do *not* call this here: User.get("victim");
        User attacker1 = User.get("attacker1");
        attacker1.setFullName("victim1");
        User victim1 = User.get("victim1");
        Assert.assertEquals("victim1 is a real user ID, we must ignore the attacker1?s fullName", "victim1", victim1.getId());
        Assert.assertEquals("a recursive call to User.get was OK", null, victim1.getProperty(MyViewsProperty.class).getPrimaryViewName());
        Assert.assertEquals("(though the realm mistakenly added metadata to the attacker)", "victim1", attacker1.getProperty(MyViewsProperty.class).getPrimaryViewName());
        User.get("attacker2").setFullName("nonexistent");
        Assert.assertEquals("but if we cannot find such a user ID, allow the fullName", "attacker2", User.get("nonexistent").getId());
        User.get("attacker3").setFullName("unknown");
        Assert.assertEquals("or if we are not sure, allow the fullName", "attacker3", User.get("unknown").getId());
        User.get("attacker4").setFullName("Victim2");
        Assert.assertEquals("victim2 is a real (canonical) user ID", "victim2", User.get("Victim2").getId());
    }

    private static class ExternalSecurityRealm extends AbstractPasswordBasedSecurityRealm {
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            if (username.equals("nonexistent")) {
                throw new UsernameNotFoundException(username);
            } else
                if (username.equals("unknown")) {
                    throw new UserMayOrMayNotExistException(username);
                } else {
                    String canonicalName = username.toLowerCase(Locale.ENGLISH);
                    try {
                        User.get(canonicalName).addProperty(new MyViewsProperty(canonicalName));
                    } catch (IOException x) {
                        throw new RuntimeException(x);
                    }
                    return new org.acegisecurity.userdetails.User(canonicalName, "", true, true, true, true, new GrantedAuthority[]{ AUTHENTICATED_AUTHORITY });
                }

        }

        @Override
        protected UserDetails authenticate(String username, String password) throws AuthenticationException {
            return loadUserByUsername(username);// irrelevant

        }

        @Override
        public GroupDetails loadGroupByGroupname(String groupname) throws UsernameNotFoundException {
            throw new UsernameNotFoundException(groupname);// irrelevant

        }
    }

    @Test
    public void resolveById() throws Exception {
        User u1 = User.get("user1");
        u1.setFullName("User One");
        u1.save();
        User u2 = User.get("user2");
        u2.setFullName("User Two");
        u2.save();
        Assert.assertNotSame("Users should not have the same id.", u1.getId(), u2.getId());
        // We can get the same user back.
        User u = User.getById("user1", false);
        Assert.assertSame("'user1' should return u1", u1, u);
        // passing true should not create a new user if it does not exist.
        u = User.getById("user1", true);
        Assert.assertSame("'user1' should return u1", u1, u);
        // should not lookup by name.
        u = User.getById("User One", false);
        Assert.assertNull("'User One' should not resolve to any user", u);
        // We can get the same user back.
        u = User.getById("user2", false);
        Assert.assertSame("'user2' should return u2", u2, u);
        // passing true should not create a new user if it does not exist.
        u = User.getById("user2", true);
        Assert.assertSame("'user2' should return u1", u2, u);
        // should not lookup by name.
        u = User.getById("User Two", false);
        Assert.assertNull("'User Two' should not resolve to any user", u);
        u1.setFullName("user1");
        u1.save();
        u2.setFullName("user1");
        u2.save();
        u = User.getById("user1", false);
        Assert.assertSame("'user1' should resolve to u1", u1, u);
        u = User.getById("user2", false);
        Assert.assertSame("'user2' should resolve to u2", u2, u);
    }

    @Test
    @Issue("SECURITY-514")
    public void getAllPropertiesRequiresAdmin() {
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to("admin").grant(Jenkins.READ).everywhere().toEveryone());
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        User admin = User.get("admin");
        User alice = User.get("alice");
        User bob = User.get("bob");
        // Admin can access user properties for all users
        try (ACLContext as = ACL.as(admin)) {
            Assert.assertThat(alice.getAllProperties(), not(empty()));
            Assert.assertThat(bob.getAllProperties(), not(empty()));
            Assert.assertThat(admin.getAllProperties(), not(empty()));
        }
        // Non admins can only view their own
        try (ACLContext as = ACL.as(alice)) {
            Assert.assertThat(alice.getAllProperties(), not(empty()));
            Assert.assertThat(bob.getAllProperties(), empty());
            Assert.assertThat(admin.getAllProperties(), empty());
        }
    }

    @Test
    @LocalData
    public void differentUserIdInConfigFileIsIgnored() {
        String fredUserId = "fred";
        User fred = User.getById(fredUserId, false);
        Assert.assertThat(fred, notNullValue());
        Assert.assertThat(fred.getId(), is(fredUserId));
        Assert.assertThat(fred.getFullName(), is("Fred Smith"));
        User jane = User.getById("jane", false);
        Assert.assertThat(jane, nullValue());
    }

    @Test
    @LocalData
    public void corruptConfigFile() {
        String fredUserId = "fred";
        User fred = User.getById(fredUserId, true);
        Assert.assertThat(fred, notNullValue());
        Assert.assertThat(fred.getFullName(), is("fred"));
    }

    @Test
    public void parentDirectoryUserDoesNotExist() {
        String userId = "admin";
        User admin = User.getById(userId, true);
        Assert.assertNotNull(admin);
        Assert.assertThat(admin.getId(), is(userId));
        User parentDirectoryUserId = User.getById(("../" + admin), false);
        Assert.assertThat(parentDirectoryUserId, nullValue());
    }

    public static class SomeUserProperty extends UserProperty {
        @TestExtension
        public static class DescriptorImpl extends UserPropertyDescriptor {
            @Override
            public UserProperty newInstance(User user) {
                return new UserTest.SomeUserProperty();
            }
        }
    }

    @Issue("JENKINS-45977")
    @Test
    public void missingDescriptor() throws Exception {
        ExtensionList.lookup(Descriptor.class).remove(j.jenkins.getDescriptor(UserTest.SomeUserProperty.class));
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(Jenkins.READ).everywhere().to("alice"));
        User alice = User.get("alice");
        alice.addProperty(new UserTest.SomeUserProperty());
        Assert.assertThat(alice.getProperties().values(), not(empty()));
        JenkinsRule.WebClient wc = j.createWebClient();
        final List<URL> failingResources = new ArrayList<>();
        new WebConnectionWrapper(wc) {
            // https://stackoverflow.com/a/18853796/12916
            @Override
            public WebResponse getResponse(WebRequest request) throws IOException {
                WebResponse r = super.getResponse(request);
                if ((r.getStatusCode()) >= 400) {
                    failingResources.add(request.getUrl());
                }
                return r;
            }
        };
        wc.login("alice").goTo("me/configure");
        Assert.assertThat(failingResources, empty());
    }
}

