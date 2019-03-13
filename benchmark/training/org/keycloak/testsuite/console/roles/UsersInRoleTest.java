/**
 *
 */
package org.keycloak.testsuite.console.roles;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.console.page.roles.DefaultRoles;
import org.keycloak.testsuite.console.page.roles.RealmRoles;
import org.keycloak.testsuite.console.page.roles.UsersInRole;
import org.keycloak.testsuite.console.page.users.UserRoleMappings;


/**
 * See KEYCLOAK-2035
 *
 * @author <a href="mailto:antonio.ferreira@fiercely.pt">Antonio Ferreira</a>
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class UsersInRoleTest extends AbstractRolesTest {
    @Page
    private DefaultRoles defaultRolesPage;

    @Page
    private UserRoleMappings userRolesPage;

    @Page
    private UsersInRole usersInRolePage;

    @Page
    private RealmRoles realmRolesPage;

    private RoleRepresentation testRoleRep;

    private RoleRepresentation emptyTestRoleRep;

    private UserRepresentation newUser;

    @Test
    public void userInRoleIsPresent() {
        // Clicking user name link
        navigateToUsersInRole(testRoleRep);
        Assert.assertEquals(1, usersInRolePage.usersTable().getUsersFromTableRows().size());
        usersInRolePage.usersTable().clickUser(newUser.getUsername());
        assertCurrentUrlEquals(userPage);
        // Clicking edit button
        navigateToUsersInRole(testRoleRep);
        usersInRolePage.usersTable().editUser(newUser.getUsername());
        assertCurrentUrlEquals(userPage);
    }

    @Test
    public void emptyRoleTest() {
        navigateToUsersInRole(emptyTestRoleRep);
        Assert.assertEquals(0, usersInRolePage.usersTable().getUsersFromTableRows().size());
        Assert.assertTrue("No roles members message is not displayed", usersInRolePage.usersTable().noRoleMembersIsDisplayed());
    }
}

