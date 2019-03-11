/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db.alm;


import ALM.GITHUB;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.assertj.core.api.AbstractAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.core.util.UuidFactory;
import org.sonar.core.util.Uuids;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;


public class AlmAppInstallDaoTest {
    private static final String A_UUID = "abcde1234";

    private static final String A_UUID_2 = "xyz789";

    private static final String EMPTY_STRING = "";

    private static final String AN_ORGANIZATION_ALM_ID = "my_org_id";

    private static final String ANOTHER_ORGANIZATION_ALM_ID = "another_org";

    private static final long DATE = 1600000000000L;

    private static final long DATE_LATER = 1700000000000L;

    private static final String AN_INSTALL = "some install id";

    private static final String OTHER_INSTALL = "other install id";

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(system2);

    private DbSession dbSession = db.getSession();

    private UuidFactory uuidFactory = Mockito.mock(UuidFactory.class);

    private AlmAppInstallDao underTest = new AlmAppInstallDao(system2, uuidFactory);

    @Test
    public void selectByUuid() {
        Mockito.when(uuidFactory.create()).thenReturn(AlmAppInstallDaoTest.A_UUID);
        Mockito.when(system2.now()).thenReturn(AlmAppInstallDaoTest.DATE);
        String userUuid = Uuids.createFast();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.AN_INSTALL, userUuid);
        assertThat(underTest.selectByUuid(dbSession, AlmAppInstallDaoTest.A_UUID).get()).extracting(AlmAppInstallDto::getUuid, AlmAppInstallDto::getAlm, AlmAppInstallDto::getInstallId, AlmAppInstallDto::getOrganizationAlmId, AlmAppInstallDto::getUserExternalId, AlmAppInstallDto::getCreatedAt, AlmAppInstallDto::getUpdatedAt).contains(AlmAppInstallDaoTest.A_UUID, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, AlmAppInstallDaoTest.AN_INSTALL, userUuid, AlmAppInstallDaoTest.DATE, AlmAppInstallDaoTest.DATE);
        assertThat(underTest.selectByUuid(dbSession, "foo")).isNotPresent();
    }

    @Test
    public void selectByOrganizationAlmId() {
        Mockito.when(uuidFactory.create()).thenReturn(AlmAppInstallDaoTest.A_UUID);
        Mockito.when(system2.now()).thenReturn(AlmAppInstallDaoTest.DATE);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.AN_INSTALL, null);
        assertThat(underTest.selectByOrganizationAlmId(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID).get()).extracting(AlmAppInstallDto::getUuid, AlmAppInstallDto::getAlm, AlmAppInstallDto::getInstallId, AlmAppInstallDto::getOrganizationAlmId, AlmAppInstallDto::getCreatedAt, AlmAppInstallDto::getUpdatedAt).contains(AlmAppInstallDaoTest.A_UUID, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, AlmAppInstallDaoTest.AN_INSTALL, AlmAppInstallDaoTest.DATE, AlmAppInstallDaoTest.DATE);
        assertThat(underTest.selectByOrganizationAlmId(dbSession, ALM.BITBUCKETCLOUD, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID)).isNotPresent();
        assertThat(underTest.selectByOrganizationAlmId(dbSession, ALM.GITHUB, "Unknown owner")).isNotPresent();
    }

    @Test
    public void selectByOwner_throws_NPE_when_alm_is_null() {
        expectAlmNPE();
        underTest.selectByOrganizationAlmId(dbSession, null, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID);
    }

    @Test
    public void selectByOwner_throws_IAE_when_organization_alm_id_is_null() {
        expectOrganizationAlmIdNullOrEmptyIAE();
        underTest.selectByOrganizationAlmId(dbSession, ALM.GITHUB, null);
    }

    @Test
    public void selectByOwner_throws_IAE_when_organization_alm_id_is_empty() {
        expectOrganizationAlmIdNullOrEmptyIAE();
        underTest.selectByOrganizationAlmId(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.EMPTY_STRING);
    }

    @Test
    public void selectByInstallationId() {
        Mockito.when(uuidFactory.create()).thenReturn(AlmAppInstallDaoTest.A_UUID);
        Mockito.when(system2.now()).thenReturn(AlmAppInstallDaoTest.DATE);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.AN_INSTALL, Uuids.createFast());
        assertThat(underTest.selectByInstallationId(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_INSTALL).get()).extracting(AlmAppInstallDto::getUuid, AlmAppInstallDto::getAlm, AlmAppInstallDto::getInstallId, AlmAppInstallDto::getOrganizationAlmId, AlmAppInstallDto::getCreatedAt, AlmAppInstallDto::getUpdatedAt).contains(AlmAppInstallDaoTest.A_UUID, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, AlmAppInstallDaoTest.AN_INSTALL, AlmAppInstallDaoTest.DATE, AlmAppInstallDaoTest.DATE);
        assertThat(underTest.selectByInstallationId(dbSession, ALM.GITHUB, "unknown installation")).isEmpty();
        assertThat(underTest.selectByInstallationId(dbSession, ALM.BITBUCKETCLOUD, AlmAppInstallDaoTest.AN_INSTALL)).isEmpty();
    }

    @Test
    public void selectUnboundByUserExternalId() {
        Mockito.when(uuidFactory.create()).thenReturn(AlmAppInstallDaoTest.A_UUID);
        Mockito.when(system2.now()).thenReturn(AlmAppInstallDaoTest.DATE);
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        OrganizationDto organization1 = db.organizations().insert();
        OrganizationDto organization2 = db.organizations().insert();
        AlmAppInstallDto almAppInstall1 = db.alm().insertAlmAppInstall(( app) -> app.setUserExternalId(user1.getExternalId()));
        AlmAppInstallDto almAppInstall2 = db.alm().insertAlmAppInstall(( app) -> app.setUserExternalId(user1.getExternalId()));
        AlmAppInstallDto almAppInstall3 = db.alm().insertAlmAppInstall(( app) -> app.setUserExternalId(user2.getExternalId()));
        db.alm().insertOrganizationAlmBinding(organization1, almAppInstall1, true);
        db.alm().insertOrganizationAlmBinding(organization2, almAppInstall3, true);
        assertThat(underTest.selectUnboundByUserExternalId(dbSession, user1.getExternalId())).extracting(AlmAppInstallDto::getUuid).containsExactlyInAnyOrder(almAppInstall2.getUuid());
        assertThat(underTest.selectUnboundByUserExternalId(dbSession, user2.getExternalId())).isEmpty();
    }

    @Test
    public void selectByOrganization() {
        OrganizationDto organization = db.organizations().insert();
        db.getDbClient().almAppInstallDao().insertOrUpdate(db.getSession(), GITHUB, "the-owner", false, "123456", null);
        // could be improved, insertOrUpdate should return the DTO with its uuid
        Optional<AlmAppInstallDto> install = db.getDbClient().almAppInstallDao().selectByOrganizationAlmId(db.getSession(), GITHUB, "the-owner");
        db.getDbClient().organizationAlmBindingDao().insert(db.getSession(), organization, install.get(), "xxx", "xxx", true);
        db.commit();
        assertThat(underTest.selectByOrganization(db.getSession(), ALM.GITHUB, organization).get().getUuid()).isEqualTo(install.get().getUuid());
        assertThat(underTest.selectByOrganization(db.getSession(), ALM.BITBUCKETCLOUD, organization)).isEmpty();
        assertThat(underTest.selectByOrganization(db.getSession(), ALM.GITHUB, new OrganizationDto().setUuid("other-organization"))).isEmpty();
    }

    @Test
    public void insert_throws_NPE_if_alm_is_null() {
        expectAlmNPE();
        underTest.insertOrUpdate(dbSession, null, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.AN_INSTALL, null);
    }

    @Test
    public void insert_throws_IAE_if_organization_alm_id_is_null() {
        expectOrganizationAlmIdNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, null, true, AlmAppInstallDaoTest.AN_INSTALL, null);
    }

    @Test
    public void insert_throws_IAE_if_organization_alm_id_is_empty() {
        expectOrganizationAlmIdNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.EMPTY_STRING, true, AlmAppInstallDaoTest.AN_INSTALL, null);
    }

    @Test
    public void insert_throws_IAE_if_install_id_is_null() {
        expectInstallIdNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, null, null);
    }

    @Test
    public void insert_throws_IAE_if_install_id_is_empty() {
        expectInstallIdNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.EMPTY_STRING, null);
    }

    @Test
    public void insert() {
        Mockito.when(uuidFactory.create()).thenReturn(AlmAppInstallDaoTest.A_UUID);
        Mockito.when(system2.now()).thenReturn(AlmAppInstallDaoTest.DATE);
        String userUuid = Uuids.createFast();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.AN_INSTALL, userUuid);
        assertThatAlmAppInstall(ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID).hasInstallId(AlmAppInstallDaoTest.AN_INSTALL).hasUserExternalId(userUuid).hasCreatedAt(AlmAppInstallDaoTest.DATE).hasUpdatedAt(AlmAppInstallDaoTest.DATE);
    }

    @Test
    public void delete() {
        Mockito.when(uuidFactory.create()).thenReturn(AlmAppInstallDaoTest.A_UUID);
        Mockito.when(system2.now()).thenReturn(AlmAppInstallDaoTest.DATE);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.AN_INSTALL, null);
        underTest.delete(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID);
        assertThatAlmAppInstall(ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID).doesNotExist();
    }

    @Test
    public void delete_does_not_fail() {
        assertThatAlmAppInstall(ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID).doesNotExist();
        underTest.delete(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID);
    }

    @Test
    public void update() {
        Mockito.when(uuidFactory.create()).thenReturn(AlmAppInstallDaoTest.A_UUID);
        Mockito.when(system2.now()).thenReturn(AlmAppInstallDaoTest.DATE);
        String userExternalId1 = randomAlphanumeric(10);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.AN_INSTALL, userExternalId1);
        Mockito.when(system2.now()).thenReturn(AlmAppInstallDaoTest.DATE_LATER);
        String userExternalId2 = randomAlphanumeric(10);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.OTHER_INSTALL, userExternalId2);
        assertThatAlmAppInstall(ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID).hasInstallId(AlmAppInstallDaoTest.OTHER_INSTALL).hasUserExternalId(userExternalId2).hasCreatedAt(AlmAppInstallDaoTest.DATE).hasUpdatedAt(AlmAppInstallDaoTest.DATE_LATER);
    }

    @Test
    public void putMultiple() {
        Mockito.when(system2.now()).thenReturn(AlmAppInstallDaoTest.DATE);
        Mockito.when(uuidFactory.create()).thenReturn(AlmAppInstallDaoTest.A_UUID).thenReturn(AlmAppInstallDaoTest.A_UUID_2);
        String userExternalId1 = randomAlphanumeric(10);
        String userExternalId2 = randomAlphanumeric(10);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID, true, AlmAppInstallDaoTest.AN_INSTALL, userExternalId1);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, AlmAppInstallDaoTest.ANOTHER_ORGANIZATION_ALM_ID, false, AlmAppInstallDaoTest.OTHER_INSTALL, userExternalId2);
        assertThatAlmAppInstall(ALM.GITHUB, AlmAppInstallDaoTest.AN_ORGANIZATION_ALM_ID).hasInstallId(AlmAppInstallDaoTest.AN_INSTALL).hasOwnerUser(true).hasUserExternalId(userExternalId1).hasCreatedAt(AlmAppInstallDaoTest.DATE).hasUpdatedAt(AlmAppInstallDaoTest.DATE);
        assertThatAlmAppInstall(ALM.GITHUB, AlmAppInstallDaoTest.ANOTHER_ORGANIZATION_ALM_ID).hasInstallId(AlmAppInstallDaoTest.OTHER_INSTALL).hasOwnerUser(false).hasUserExternalId(userExternalId2).hasCreatedAt(AlmAppInstallDaoTest.DATE).hasUpdatedAt(AlmAppInstallDaoTest.DATE);
    }

    private static class AlmAppInstallAssert extends AbstractAssert<AlmAppInstallDaoTest.AlmAppInstallAssert, AlmAppInstallDto> {
        private AlmAppInstallAssert(DbTester dbTester, DbSession dbSession, ALM alm, String organizationAlmId) {
            super(AlmAppInstallDaoTest.AlmAppInstallAssert.asAlmAppInstall(dbTester, dbSession, alm, organizationAlmId), AlmAppInstallDaoTest.AlmAppInstallAssert.class);
        }

        private static AlmAppInstallDto asAlmAppInstall(DbTester db, DbSession dbSession, ALM alm, String organizationAlmId) {
            Optional<AlmAppInstallDto> almAppInstall = db.getDbClient().almAppInstallDao().selectByOrganizationAlmId(dbSession, alm, organizationAlmId);
            return almAppInstall.orElse(null);
        }

        public void doesNotExist() {
            isNull();
        }

        AlmAppInstallDaoTest.AlmAppInstallAssert hasInstallId(String expected) {
            isNotNull();
            if (!(Objects.equals(actual.getInstallId(), expected))) {
                failWithMessage("Expected ALM App Install to have column INSTALL_ID to be <%s> but was <%s>", expected, actual.getInstallId());
            }
            return this;
        }

        AlmAppInstallDaoTest.AlmAppInstallAssert hasOwnerUser(boolean expected) {
            isNotNull();
            if (!(Objects.equals(actual.isOwnerUser(), expected))) {
                failWithMessage("Expected ALM App Install to have column IS_OWNER_USER to be <%s> but was <%s>", expected, actual.isOwnerUser());
            }
            return this;
        }

        AlmAppInstallDaoTest.AlmAppInstallAssert hasUserExternalId(String expected) {
            isNotNull();
            if (!(Objects.equals(actual.getUserExternalId(), expected))) {
                failWithMessage("Expected ALM App Install to have column USER_EXTERNAL_ID to be <%s> but was <%s>", expected, actual.getUserExternalId());
            }
            return this;
        }

        AlmAppInstallDaoTest.AlmAppInstallAssert hasCreatedAt(long expected) {
            isNotNull();
            if (!(Objects.equals(actual.getCreatedAt(), expected))) {
                failWithMessage("Expected ALM App Install to have column CREATED_AT to be <%s> but was <%s>", expected, actual.getCreatedAt());
            }
            return this;
        }

        AlmAppInstallDaoTest.AlmAppInstallAssert hasUpdatedAt(long expected) {
            isNotNull();
            if (!(Objects.equals(actual.getUpdatedAt(), expected))) {
                failWithMessage("Expected ALM App Install to have column UPDATED_AT to be <%s> but was <%s>", expected, actual.getUpdatedAt());
            }
            return this;
        }
    }
}

