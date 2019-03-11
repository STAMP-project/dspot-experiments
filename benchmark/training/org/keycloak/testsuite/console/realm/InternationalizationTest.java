package org.keycloak.testsuite.console.realm;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.fragment.LocaleDropdown;
import org.keycloak.testsuite.console.page.realm.ThemeSettings;
import org.openqa.selenium.support.FindBy;


/**
 *
 *
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class InternationalizationTest extends AbstractRealmTest {
    private static final String THEME_NAME = "internat-test";

    private static final String LOCALE_CS_NAME = "?e?tina";

    private static final String LABEL_CS_PASSWORD = "Heslo";

    private static final String LABEL_CS_REALM_SETTINGS = "Nastaven? Realmu";

    private static final String LABEL_CS_EDIT_ACCOUNT = "Upravit ??et";

    @Page
    private ThemeSettings themeSettingsPage;

    @FindBy(id = "kc-locale-dropdown")
    private LocaleDropdown localeDropdown;

    /**
     * Change locale before login
     */
    @Test
    public void loginInternationalization() {
        testRealmAdminConsolePage.navigateTo();
        localeDropdown.selectByText(InternationalizationTest.LOCALE_CS_NAME);
        assertLocale(".//label[@for='password']", InternationalizationTest.LABEL_CS_PASSWORD);
        loginToTestRealmConsoleAs(testUser);
        assertConsoleLocale(InternationalizationTest.LABEL_CS_REALM_SETTINGS);
        testRealmAccountPage.navigateTo();
        assertAccountLocale(InternationalizationTest.LABEL_CS_EDIT_ACCOUNT);
    }

    /**
     * Change locale on the Account page
     */
    @Test
    public void accountInternationalization() {
        testRealmAccountPage.navigateTo();
        loginPage.form().login(testUser);
        localeDropdown.selectByText(InternationalizationTest.LOCALE_CS_NAME);
        testRealmAccountPage.navigateTo();
        assertAccountLocale(InternationalizationTest.LABEL_CS_EDIT_ACCOUNT);
        deleteAllCookiesForTestRealm();
        loginToTestRealmConsoleAs(testUser);
        assertConsoleLocale(InternationalizationTest.LABEL_CS_REALM_SETTINGS);
    }
}

