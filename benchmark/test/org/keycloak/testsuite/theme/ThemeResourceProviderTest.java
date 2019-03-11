package org.keycloak.testsuite.theme;


import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.theme.Theme;
import org.keycloak.theme.ThemeProvider;


public class ThemeResourceProviderTest extends AbstractTestRealmKeycloakTest {
    @Test
    public void getTheme() {
        testingClient.server().run(( session) -> {
            try {
                ThemeProvider extending = session.getProvider(.class, "extending");
                Theme theme = extending.getTheme("base", Theme.Type.LOGIN);
                Assert.assertNotNull(theme.getTemplate("test.ftl"));
            } catch ( e) {
                Assert.fail(e.getMessage());
            }
        });
    }

    @Test
    public void getResourceAsStream() {
        testingClient.server().run(( session) -> {
            try {
                ThemeProvider extending = session.getProvider(.class, "extending");
                Theme theme = extending.getTheme("base", Theme.Type.LOGIN);
                Assert.assertNotNull(theme.getResourceAsStream("test.js"));
            } catch ( e) {
                Assert.fail(e.getMessage());
            }
        });
    }
}

