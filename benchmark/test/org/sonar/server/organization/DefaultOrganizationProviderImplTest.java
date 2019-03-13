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
package org.sonar.server.organization;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.property.InternalProperties;


public class DefaultOrganizationProviderImplTest {
    private static final OrganizationDto ORGANIZATION_DTO_1 = newOrganizationDto().setUuid("uuid1").setName("the name of 1").setKey("the key 1");

    private static final long DATE_1 = 1999888L;

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public DbTester dbTester = DbTester.create(system2).setDisableDefaultOrganization(true);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DbClient dbClient = dbTester.getDbClient();

    private DbSession dbSession = dbTester.getSession();

    private DefaultOrganizationProviderImpl underTest = new DefaultOrganizationProviderImpl(dbClient);

    @Test
    public void get_fails_with_ISE_if_default_organization_internal_property_does_not_exist() {
        expectISENoDefaultOrganizationUuid();
        underTest.get();
    }

    @Test
    public void get_fails_with_ISE_if_default_organization_internal_property_is_empty() {
        dbClient.internalPropertiesDao().saveAsEmpty(dbSession, InternalProperties.DEFAULT_ORGANIZATION);
        dbSession.commit();
        expectISENoDefaultOrganizationUuid();
        underTest.get();
    }

    @Test
    public void get_fails_with_ISE_if_default_organization_does_not_exist() {
        dbClient.internalPropertiesDao().save(dbSession, InternalProperties.DEFAULT_ORGANIZATION, "bla");
        dbSession.commit();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization with uuid 'bla' does not exist");
        underTest.get();
    }

    @Test
    public void get_returns_DefaultOrganization_populated_from_DB() {
        insertOrganization(DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1, DefaultOrganizationProviderImplTest.DATE_1);
        dbClient.internalPropertiesDao().save(dbSession, InternalProperties.DEFAULT_ORGANIZATION, DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1.getUuid());
        dbSession.commit();
        DefaultOrganization defaultOrganization = underTest.get();
        assertThat(defaultOrganization.getUuid()).isEqualTo(DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1.getUuid());
        assertThat(defaultOrganization.getKey()).isEqualTo(DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1.getKey());
        assertThat(defaultOrganization.getName()).isEqualTo(DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1.getName());
        assertThat(defaultOrganization.getCreatedAt()).isEqualTo(DefaultOrganizationProviderImplTest.DATE_1);
        assertThat(defaultOrganization.getUpdatedAt()).isEqualTo(DefaultOrganizationProviderImplTest.DATE_1);
    }

    @Test
    public void get_returns_new_DefaultOrganization_with_each_call_when_cache_is_not_loaded() {
        insertOrganization(DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1, DefaultOrganizationProviderImplTest.DATE_1);
        dbClient.internalPropertiesDao().save(dbSession, InternalProperties.DEFAULT_ORGANIZATION, DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1.getUuid());
        dbSession.commit();
        assertThat(underTest.get()).isNotSameAs(underTest.get());
    }

    @Test
    public void unload_does_not_fail_if_load_has_not_been_called() {
        underTest.unload();
    }

    @Test
    public void load_resets_thread_local_when_called_twice() {
        insertOrganization(DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1, DefaultOrganizationProviderImplTest.DATE_1);
        dbClient.internalPropertiesDao().save(dbSession, InternalProperties.DEFAULT_ORGANIZATION, DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1.getUuid());
        dbSession.commit();
        underTest.load();
        DefaultOrganization org1 = underTest.get();
        underTest.load();
        DefaultOrganization org2 = underTest.get();
        assertThat(org1).isNotSameAs(org2);
    }

    @Test
    public void load_and_unload_cache_DefaultOrganization_object_by_thread() throws InterruptedException {
        insertOrganization(DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1, DefaultOrganizationProviderImplTest.DATE_1);
        dbClient.internalPropertiesDao().save(dbSession, InternalProperties.DEFAULT_ORGANIZATION, DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1.getUuid());
        dbSession.commit();
        try {
            underTest.load();
            DefaultOrganization cachedForThread1 = underTest.get();
            assertThat(cachedForThread1).isSameAs(underTest.get());
            Thread otherThread = new Thread(() -> {
                try {
                    underTest.load();
                    assertThat(underTest.get()).isNotSameAs(cachedForThread1).isSameAs(underTest.get());
                } finally {
                    underTest.unload();
                }
            });
            otherThread.start();
            otherThread.join();
        } finally {
            underTest.unload();
        }
    }

    @Test
    public void get_returns_new_instance_for_each_call_once_unload_has_been_called() {
        insertOrganization(DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1, DefaultOrganizationProviderImplTest.DATE_1);
        dbClient.internalPropertiesDao().save(dbSession, InternalProperties.DEFAULT_ORGANIZATION, DefaultOrganizationProviderImplTest.ORGANIZATION_DTO_1.getUuid());
        dbSession.commit();
        try {
            underTest.load();
            DefaultOrganization cached = underTest.get();
            assertThat(cached).isSameAs(underTest.get());
            underTest.unload();
            assertThat(underTest.get()).isNotSameAs(underTest.get()).isNotSameAs(cached);
        } finally {
            // fail safe
            underTest.unload();
        }
    }
}

