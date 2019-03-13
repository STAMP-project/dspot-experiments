package org.keycloak.testsuite.user;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.util.Timer;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author tkyjovsk
 */
public class ManyUsersTest extends AbstractUserTest {
    private static final int COUNT = Integer.parseInt(System.getProperty("many.users.count", "10000"));

    private static final int BATCH = Integer.parseInt(System.getProperty("many.users.batch", "1000"));

    // When true, then it will always send another request to GET user after he is created (this trigger some DB queries and cache user on Keycloak side)
    private static final boolean READ_USER_AFTER_CREATE = Boolean.parseBoolean(System.getProperty("many.users.read.after.create", "false"));

    // When true, then each user will be updated with password, 2 additional attributes, 2 default groups and some required action
    private static final boolean CREATE_OBJECTS = Boolean.parseBoolean(System.getProperty("many.users.create.objects", "false"));

    // When true, then each user will be updated with 2 federated identity links
    private static final boolean CREATE_SOCIAL_LINKS = Boolean.parseBoolean(System.getProperty("many.users.create.social.links", "false"));

    private static final boolean REIMPORT = Boolean.parseBoolean(System.getProperty("many.users.reimport", "false"));

    private static final String REALM = "realm_with_many_users";

    private List<UserRepresentation> users;

    private final Timer realmTimer = new Timer();

    private final Timer usersTimer = new Timer();

    private static final long MIN_TOKEN_VALIDITY = Long.parseLong(System.getProperty("many.users.minTokenValidity", "10000"));

    long tokenExpirationTime = 0;

    @Test
    public void manyUsers() throws IOException {
        RealmRepresentation realm = realmResource().toRepresentation();
        realm.setUsers(users);
        // CREATE
        realmTimer.reset((("create " + (users.size())) + " users"));
        usersTimer.reset((("create " + (ManyUsersTest.BATCH)) + " users"));
        int i = 0;
        for (UserRepresentation user : users) {
            refreshTokenIfMinValidityExpired();
            UserRepresentation createdUser = createUser(realmResource().users(), user);
            // Send additional request to read every user after he is created
            if (ManyUsersTest.READ_USER_AFTER_CREATE) {
                UserRepresentation returned = realmResource().users().get(createdUser.getId()).toRepresentation();
                Assert.assertEquals(returned.getId(), createdUser.getId());
            }
            // Send additional request to read social links of user
            if (ManyUsersTest.CREATE_SOCIAL_LINKS) {
                List<FederatedIdentityRepresentation> fedIdentities = realmResource().users().get(createdUser.getId()).getFederatedIdentity();
            }
            if (((++i) % (ManyUsersTest.BATCH)) == 0) {
                usersTimer.reset();
                log.info(((("Created users: " + i) + " / ") + (users.size())));
            }
        }
        if ((i % (ManyUsersTest.BATCH)) != 0) {
            usersTimer.reset();
            log.info(((("Created users: " + i) + " / ") + (users.size())));
        }
        if (ManyUsersTest.REIMPORT) {
            // SAVE REALM
            realmTimer.reset((("save realm with " + (users.size())) + " users"));
            File realmFile = new File(PROJECT_BUILD_DIRECTORY, ((ManyUsersTest.REALM) + ".json"));
            JsonSerialization.writeValueToStream(new BufferedOutputStream(new FileOutputStream(realmFile)), realm);
            // DELETE REALM
            realmTimer.reset((("delete realm with " + (users.size())) + " users"));
            realmResource().remove();
            try {
                realmResource().toRepresentation();
                Assert.fail("realm not deleted");
            } catch (Exception ex) {
                log.debug("realm deleted");
            }
            // RE-IMPORT SAVED REALM
            realmTimer.reset((("re-import realm with " + (realm.getUsers().size())) + " users"));
            realmsResouce().create(realm);
            realmTimer.reset((("load " + (realm.getUsers().size())) + " users"));
            users = realmResource().users().search("", 0, Integer.MAX_VALUE);
        }
        // DELETE INDIVIDUAL USERS
        realmTimer.reset((("delete " + (users.size())) + " users"));
        usersTimer.reset((("delete " + (ManyUsersTest.BATCH)) + " users"), false);
        i = 0;
        for (UserRepresentation user : users) {
            refreshTokenIfMinValidityExpired();
            realmResource().users().get(user.getId()).remove();
            if (((++i) % (ManyUsersTest.BATCH)) == 0) {
                usersTimer.reset();
                log.info(((("Deleted users: " + i) + " / ") + (users.size())));
            }
        }
        if ((i % (ManyUsersTest.BATCH)) != 0) {
            usersTimer.reset();
            log.info(((("Deleted users: " + i) + " / ") + (users.size())));
        }
        realmTimer.reset();
    }
}

