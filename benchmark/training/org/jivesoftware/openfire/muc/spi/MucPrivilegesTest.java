package org.jivesoftware.openfire.muc.spi;


import MUCRole.Affiliation.admin;
import MUCRole.Affiliation.member;
import MUCRole.Affiliation.none;
import MUCRole.Affiliation.owner;
import MUCRole.Role.moderator;
import MUCRole.Role.participant;
import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Christian Schudt
 */
public class MucPrivilegesTest {
    @Test
    public void ownerShouldBeAbleToDoAnything() {
        Assert.assertTrue(LocalMUCRoom.isPrivilegedToChangeAffiliationAndRole(owner, moderator, owner, moderator, none, MUCRole.Role.none));
    }

    @Test
    public void adminShouldBeAbleToRevokeModeratorPrivilegesFromOtherAdmin() {
        Assert.assertTrue(LocalMUCRoom.isPrivilegedToChangeAffiliationAndRole(admin, MUCRole.Role.none, admin, moderator, admin, MUCRole.Role.none));
    }

    @Test
    public void adminShouldBeAbleToGrantMembership() {
        Assert.assertTrue(LocalMUCRoom.isPrivilegedToChangeAffiliationAndRole(admin, MUCRole.Role.none, none, MUCRole.Role.none, member, participant));
    }

    @Test
    public void adminModeratorShouldNotBeAbleToRevokeModeratorPrivilegesFromOwner() {
        Assert.assertFalse(LocalMUCRoom.isPrivilegedToChangeAffiliationAndRole(admin, moderator, owner, moderator, none, MUCRole.Role.none));
    }

    @Test
    public void ownerModeratorShouldBeAbleToRevokeModeratorPrivilegesFromOwner() {
        Assert.assertTrue(LocalMUCRoom.isPrivilegedToChangeAffiliationAndRole(owner, moderator, owner, moderator, none, MUCRole.Role.none));
    }

    @Test
    public void ownerModeratorShouldBeAbleToRevokeModeratorPrivilegesFromAdmin() {
        Assert.assertTrue(LocalMUCRoom.isPrivilegedToChangeAffiliationAndRole(owner, moderator, admin, moderator, none, MUCRole.Role.none));
    }

    @Test
    public void memberModeratorShouldNotBeAbleToRevokeModeratorPrivilegesFromOwner() {
        Assert.assertFalse(LocalMUCRoom.isPrivilegedToChangeAffiliationAndRole(member, moderator, owner, moderator, none, MUCRole.Role.none));
    }

    @Test
    public void memberModeratorShouldNotBeAbleToRevokeModeratorPrivilegesFromAdmin() {
        Assert.assertFalse(LocalMUCRoom.isPrivilegedToChangeAffiliationAndRole(member, moderator, admin, moderator, none, MUCRole.Role.none));
    }

    @Test
    public void memberShouldNotBeAbleToDoAnything() {
        Assert.assertFalse(LocalMUCRoom.isPrivilegedToChangeAffiliationAndRole(member, participant, admin, moderator, none, MUCRole.Role.none));
    }
}

