package org.keycloak.testsuite.console.roles;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.console.page.roles.DefaultRoles;
import org.keycloak.testsuite.console.page.users.UserRoleMappings;
import org.keycloak.testsuite.console.page.users.Users;


/**
 * Created by fkiss.
 */
public class DefaultRolesTest extends AbstractRolesTest {
    @Page
    private DefaultRoles defaultRolesPage;

    @Page
    private UserRoleMappings userRolesPage;

    private RoleRepresentation defaultRoleRep;

    @Page
    private Users users;

    @Test
    public void defaultRoleAssignedToNewUser() {
        String defaultRoleName = defaultRoleRep.getName();
        defaultRolesPage.form().addAvailableRole(defaultRoleName);
        assertAlertSuccess();
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("new_user");
        createUserWithAdminClient(testRealmResource(), newUser);
        users.navigateTo();
        users.table().search(newUser.getUsername());
        users.table().clickUser(newUser.getUsername());
        userPage.tabs().roleMappings();
        Assert.assertTrue(userRolesPage.form().isAssignedRole(defaultRoleName));
    }
}

