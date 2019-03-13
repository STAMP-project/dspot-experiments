package org.keycloak.testsuite.console.events;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.events.Config;


/**
 *
 *
 * @author mhajas
 */
public class ConfigTest extends AbstractConsoleTest {
    @Page
    private Config configPage;

    @Test
    public void configLoginEventsTest() {
        configPage.form().setSaveEvents(true);
        // IE webdriver has problem with clicking not visible (scrolling is needed) items in the menu,
        // so we need to select some type from the beginning of the menu
        configPage.form().addSaveType("CLIENT_INFO");
        // after removeSavedType method stay input focused -> in phantomjs drop menu doesn't appear after first click
        configPage.form().removeSaveType("LOGIN");
        configPage.form().setExpiration("50", "Days");
        configPage.form().save();
        assertAlertSuccess();
        RealmRepresentation realm = testRealmResource().toRepresentation();
        Assert.assertTrue(realm.isEventsEnabled());
        Assert.assertFalse(realm.getEnabledEventTypes().contains("LOGIN"));
        Assert.assertTrue(realm.getEnabledEventTypes().contains("CLIENT_INFO"));
        Assert.assertEquals(4320000L, realm.getEventsExpiration().longValue());
    }

    @Test
    public void configAdminEventsTest() {
        configPage.form().setSaveAdminEvents(true);
        configPage.form().setIncludeRepresentation(true);
        configPage.form().save();
        assertAlertSuccess();
        RealmRepresentation realm = testRealmResource().toRepresentation();
        Assert.assertTrue(realm.isAdminEventsEnabled());
        Assert.assertTrue(realm.isAdminEventsDetailsEnabled());
    }
}

