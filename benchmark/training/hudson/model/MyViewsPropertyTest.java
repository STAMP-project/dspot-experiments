/**
 * The MIT License
 *
 * Copyright 2013 Red Hat, Inc.
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


import AllView.DEFAULT_VIEW_NAME;
import Jenkins.ADMINISTER;
import Permission.CONFIGURE;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.model.Descriptor.FormException;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.Permission;
import java.io.IOException;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.context.SecurityContextHolder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import static AllView.DEFAULT_VIEW_NAME;


/**
 *
 *
 * @author Lucie Votypkova
 */
public class MyViewsPropertyTest {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Test
    public void testReadResolve() throws IOException {
        User user = User.get("User");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.setUser(user);
        user.addProperty(property);
        property.readResolve();
        Assert.assertNotNull((("Property should contain " + (DEFAULT_VIEW_NAME)) + " by default."), property.getView(DEFAULT_VIEW_NAME));
    }

    /* TODO unclear what exactly this is purporting to assert
    @Test
    public void testSave() throws IOException {
    User user = User.get("User");
    MyViewsProperty property = new MyViewsProperty(AllView.DEFAULT_VIEW_NAME);
    property.readResolve();
    property.setUser(user);
    user.addProperty(property);
    View view = new ListView("foo", property);
    property.addView(view);
    property.setPrimaryViewName(view.name);
    User.reload();
    property = User.get("User").getProperty(property.getClass());
    assertNotSame("Property should not have primary view " + view.name, view.name, property.getPrimaryViewName());
    property.setUser(user);
    property.addView(view);
    property.setPrimaryViewName(view.name);
    property.save();
    User.reload();
    property = User.get("User").getProperty(property.getClass());
    assertEquals("Property should have primary view " + view.name + " instead of " + property.getPrimaryViewName(), view.name, property.getPrimaryViewName());
    }
     */
    @Test
    public void testGetViews() throws IOException {
        User user = User.get("User");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.readResolve();
        property.setUser(user);
        Assert.assertTrue(("Property should contain " + (DEFAULT_VIEW_NAME)), property.getViews().contains(property.getView(DEFAULT_VIEW_NAME)));
        View view = new ListView("foo", property);
        property.addView(view);
        Assert.assertTrue(("Property should contain " + (view.name)), property.getViews().contains(view));
    }

    @Test
    public void testGetView() throws IOException {
        User user = User.get("User");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.readResolve();
        property.setUser(user);
        Assert.assertNotNull(("Property should contain " + (DEFAULT_VIEW_NAME)), property.getView(DEFAULT_VIEW_NAME));
        View view = new ListView("foo", property);
        property.addView(view);
        Assert.assertEquals(("Property should contain " + (view.name)), view, property.getView(view.name));
    }

    @Test
    public void testGetPrimaryView() throws IOException {
        User user = User.get("User");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.readResolve();
        property.setUser(user);
        user.addProperty(property);
        Assert.assertEquals(((("Property should have primary view " + (DEFAULT_VIEW_NAME)) + " instead of ") + (property.getPrimaryView().name)), property.getView(DEFAULT_VIEW_NAME), property.getPrimaryView());
        View view = new ListView("foo", property);
        property.addView(view);
        property.setPrimaryViewName(view.name);
        Assert.assertEquals(((("Property should have primary view " + (view.name)) + " instead of ") + (property.getPrimaryView().name)), view, property.getPrimaryView());
    }

    @Test
    public void testCanDelete() throws IOException {
        User user = User.get("User");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.readResolve();
        property.setUser(user);
        user.addProperty(property);
        Assert.assertFalse(("Property should not enable to delete view " + (DEFAULT_VIEW_NAME)), property.canDelete(property.getView(DEFAULT_VIEW_NAME)));
        View view = new ListView("foo", property);
        property.addView(view);
        Assert.assertTrue(("Property should enable to delete view " + (view.name)), property.canDelete(view));
        property.setPrimaryViewName(view.name);
        Assert.assertFalse(("Property should not enable to delete view " + (view.name)), property.canDelete(view));
        Assert.assertTrue(("Property should enable to delete view " + (DEFAULT_VIEW_NAME)), property.canDelete(property.getView(DEFAULT_VIEW_NAME)));
    }

    @Test
    public void testDeleteView() throws IOException {
        User user = User.get("User");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.readResolve();
        property.setUser(user);
        user.addProperty(property);
        boolean ex = false;
        try {
            property.deleteView(property.getView(DEFAULT_VIEW_NAME));
        } catch (IllegalStateException e) {
            ex = true;
        }
        Assert.assertTrue("Property should throw IllegalStateException.", ex);
        Assert.assertTrue(("Property should contain view " + (DEFAULT_VIEW_NAME)), property.getViews().contains(property.getView(DEFAULT_VIEW_NAME)));
        View view = new ListView("foo", property);
        property.addView(view);
        ex = false;
        try {
            property.deleteView(view);
        } catch (IllegalStateException e) {
            ex = true;
        }
        Assert.assertFalse(("Property should not contain view " + (view.name)), property.getViews().contains(view));
        property.addView(view);
        property.setPrimaryViewName(view.name);
        Assert.assertTrue(("Property should not contain view " + (view.name)), property.getViews().contains(view));
        property.deleteView(property.getView(DEFAULT_VIEW_NAME));
        Assert.assertFalse(("Property should not contains view " + (DEFAULT_VIEW_NAME)), property.getViews().contains(property.getView(DEFAULT_VIEW_NAME)));
    }

    @Test
    public void testOnViewRenamed() throws FormException, Failure, IOException {
        User user = User.get("User");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.readResolve();
        property.setUser(user);
        user.addProperty(property);
        View view = new ListView("foo", property);
        property.addView(view);
        property.setPrimaryViewName(view.name);
        view.rename("primary-renamed");
        Assert.assertEquals("Property should rename its primary view ", "primary-renamed", property.getPrimaryViewName());
    }

    @Test
    public void testAddView() throws Exception {
        {
            User user = User.get("User");
            MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
            property.readResolve();
            property.setUser(user);
            user.addProperty(property);
            View view = new ListView("foo", property);
            property.addView(view);
            Assert.assertTrue(("Property should contain view " + (view.name)), property.getViews().contains(view));
        }
        rule.jenkins.reload();
        {
            User user = User.get("User");
            MyViewsProperty property = user.getProperty(MyViewsProperty.class);
            Assert.assertTrue("Property should save changes.", property.getViews().contains(property.getView("foo")));
        }
    }

    @Test
    public void testDoCreateView() throws Exception {
        {
            User user = User.get("User");
            MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
            property.readResolve();
            property.setUser(user);
            user.addProperty(property);
            HtmlForm form = rule.createWebClient().goTo(((property.getUrl()) + "/newView")).getFormByName("createItem");
            form.getInputByName("name").setValueAttribute("foo");
            form.getRadioButtonsByName("mode").get(0).setChecked(true);
            rule.submit(form);
            Assert.assertNotNull("Property should contain view foo", property.getView("foo"));
        }
        rule.jenkins.reload();
        {
            MyViewsProperty property = User.get("User").getProperty(MyViewsProperty.class);
            Assert.assertNotNull("Property should save changes", property.getView("foo"));
        }
    }

    @Test
    public void testGetACL() throws IOException {
        User user = User.get("User");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.readResolve();
        property.setUser(user);
        user.addProperty(property);
        for (Permission p : Permission.getAll()) {
            Assert.assertEquals("Property should have the same ACL as its user", property.getACL().hasPermission(p), user.getACL().hasPermission(p));
        }
    }

    @Test
    public void testCheckPermission() throws IOException {
        rule.jenkins.setSecurityRealm(rule.createDummySecurityRealm());
        User user = User.get("User");
        User user2 = User.get("User2");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.readResolve();
        property.setUser(user);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        rule.jenkins.setAuthorizationStrategy(auth);
        user.addProperty(property);
        boolean ex = false;
        SecurityContextHolder.getContext().setAuthentication(user2.impersonate());
        try {
            property.checkPermission(CONFIGURE);
        } catch (AccessDeniedException e) {
            ex = true;
        }
        Assert.assertTrue("Property should throw AccessDeniedException.", ex);
        SecurityContextHolder.getContext().setAuthentication(user.impersonate());
        try {
            property.checkPermission(CONFIGURE);
        } catch (AccessDeniedException e) {
            Assert.fail("Property should not throw AccessDeniedException - user should control of himself.");
        }
        SecurityContextHolder.getContext().setAuthentication(user2.impersonate());
        auth.add(ADMINISTER, "User2");
        try {
            property.checkPermission(CONFIGURE);
        } catch (AccessDeniedException e) {
            Assert.fail("Property should not throw AccessDeniedException.");
        }
    }

    @Test
    public void testHasPermission() throws IOException {
        rule.jenkins.setSecurityRealm(rule.createDummySecurityRealm());
        User user = User.get("User");
        User user2 = User.get("User2");
        MyViewsProperty property = new MyViewsProperty(DEFAULT_VIEW_NAME);
        property.readResolve();
        property.setUser(user);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        rule.jenkins.setAuthorizationStrategy(auth);
        user.addProperty(property);
        SecurityContextHolder.getContext().setAuthentication(user2.impersonate());
        Assert.assertFalse("User User2 should not configure permission for user User", property.hasPermission(CONFIGURE));
        SecurityContextHolder.getContext().setAuthentication(user.impersonate());
        Assert.assertTrue("User should control of himself.", property.hasPermission(CONFIGURE));
        auth.add(ADMINISTER, "User2");
        Assert.assertTrue("User User2 should configure permission for user User", property.hasPermission(CONFIGURE));
    }

    @Test
    @Issue("JENKINS-48157")
    public void shouldNotFailWhenMigratingLegacyViewsWithoutPrimaryOne() throws IOException {
        rule.jenkins.setSecurityRealm(rule.createDummySecurityRealm());
        User user = User.get("User");
        // Emulates creation of a new object with Reflection in User#load() does.
        MyViewsProperty property = new MyViewsProperty(null);
        user.addProperty(property);
        // At AllView with non-default to invoke NPE path in AllView.migrateLegacyPrimaryAllViewLocalizedName()
        property.addView(new AllView("foobar"));
        property.readResolve();
    }
}

