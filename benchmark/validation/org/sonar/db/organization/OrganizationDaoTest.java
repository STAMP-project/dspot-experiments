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
package org.sonar.db.organization;


import CoreMetrics.NCLOC_KEY;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.ibatis.exceptions.PersistenceException;
import org.assertj.core.groups.Tuple;
import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.Pagination;
import org.sonar.db.alm.ALM;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.qualitygate.QGateWithOrgDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.GroupTesting;
import org.sonar.db.user.UserDto;


public class OrganizationDaoTest {
    private static final long SOME_DATE = 1200999L;

    private static final long DATE_1 = 1999000L;

    private static final long DATE_2 = 8999999L;

    private static final long DATE_3 = 3999000L;

    private static final OrganizationDto ORGANIZATION_DTO_1 = new OrganizationDto().setUuid("uuid 1").setKey("the_key 1").setName("the name 1").setDescription("the description 1").setUrl("the url 1").setAvatarUrl("the avatar url 1").setGuarded(false).setSubscription(Subscription.FREE).setDefaultQualityGateUuid("1");

    private static final OrganizationDto ORGANIZATION_DTO_2 = new OrganizationDto().setUuid("uuid 2").setKey("the_key 2").setName("the name 2").setDescription("the description 2").setUrl("the url 2").setAvatarUrl("the avatar url 2").setGuarded(true).setSubscription(Subscription.FREE).setDefaultQualityGateUuid("1");

    private static final String PERMISSION_1 = "foo";

    private static final String PERMISSION_2 = "bar";

    private static final Random RANDOM = new Random();

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public final DbTester db = DbTester.create(system2).setDisableDefaultOrganization(true);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private OrganizationDao underTest = dbClient.organizationDao();

    @Test
    public void insert_fails_with_NPE_if_OrganizationDto_is_null() {
        expectDtoCanNotBeNull();
        underTest.insert(dbSession, null, false);
    }

    @Test
    public void insert_populates_createdAt_and_updateAt_with_same_date_from_System2() {
        Mockito.when(system2.now()).thenReturn(OrganizationDaoTest.DATE_1, ((OrganizationDaoTest.DATE_1) + 1000000L));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setCreatedAt(1000L).setUpdatedAt(6000L));
        Map<String, Object> row = selectSingleRow();
        assertThat(row.get("createdAt")).isEqualTo(OrganizationDaoTest.DATE_1);
        assertThat(row.get("updatedAt")).isEqualTo(OrganizationDaoTest.DATE_1);
    }

    @Test
    public void insert_persists_properties_of_OrganizationDto() {
        OrganizationDto organization = OrganizationTesting.newOrganizationDto();
        insertOrganization(organization);
        Map<String, Object> row = selectSingleRow();
        assertThat(row.get("uuid")).isEqualTo(organization.getUuid());
        assertThat(row.get("key")).isEqualTo(organization.getKey());
        assertThat(row.get("name")).isEqualTo(organization.getName());
        assertThat(row.get("description")).isEqualTo(organization.getDescription());
        assertThat(row.get("url")).isEqualTo(organization.getUrl());
        assertThat(row.get("avatarUrl")).isEqualTo(organization.getAvatarUrl());
        assertThat(row.get("createdAt")).isEqualTo(organization.getCreatedAt());
        assertThat(row.get("updatedAt")).isEqualTo(organization.getUpdatedAt());
        assertThat(row.get("guarded")).isEqualTo(toBool(organization.isGuarded()));
        assertThat(row.get("subscription")).isEqualTo(organization.getSubscription().name());
        assertThat(row.get("defaultTemplate")).isNull();
        assertThat(row.get("projectDefaultTemplate")).isNull();
        assertThat(row.get("viewDefaultTemplate")).isNull();
    }

    @Test
    public void insert_persists_boolean_property_guarded_of_OrganizationDto() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        Map<String, Object> row = selectSingleRow();
        assertThat(row.get("guarded")).isEqualTo(toBool(OrganizationDaoTest.ORGANIZATION_DTO_2.isGuarded()));
    }

    @Test
    public void description_url_avatarUrl_and_userId_are_optional() {
        Mockito.when(system2.now()).thenReturn(OrganizationDaoTest.SOME_DATE);
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setDescription(null).setUrl(null).setAvatarUrl(null));
        Map<String, Object> row = selectSingleRow();
        assertThat(row.get("uuid")).isEqualTo(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid());
        assertThat(row.get("key")).isEqualTo(OrganizationDaoTest.ORGANIZATION_DTO_1.getKey());
        assertThat(row.get("name")).isEqualTo(OrganizationDaoTest.ORGANIZATION_DTO_1.getName());
        assertThat(row.get("description")).isNull();
        assertThat(row.get("url")).isNull();
        assertThat(row.get("avatarUrl")).isNull();
        assertThat(row.get("guarded")).isEqualTo(toBool(OrganizationDaoTest.ORGANIZATION_DTO_1.isGuarded()));
        assertThat(row.get("userId")).isNull();
        assertThat(row.get("createdAt")).isEqualTo(OrganizationDaoTest.SOME_DATE);
        assertThat(row.get("updatedAt")).isEqualTo(OrganizationDaoTest.SOME_DATE);
        assertThat(row.get("defaultTemplate")).isNull();
        assertThat(row.get("projectDefaultTemplate")).isNull();
        assertThat(row.get("viewDefaultTemplate")).isNull();
    }

    @Test
    public void insert_fails_if_row_with_uuid_already_exists() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        OrganizationDto dto = new OrganizationDto().setUuid(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid()).setKey("other key").setName("other name").setCreatedAt(2999000L).setUpdatedAt(2888000L);
        expectedException.expect(PersistenceException.class);
        underTest.insert(dbSession, dto, false);
    }

    @Test
    public void selectByKey_returns_empty_when_table_is_empty() {
        assertThat(underTest.selectByKey(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getKey())).isEmpty();
    }

    @Test
    public void selectByKey_returns_row_data_when_key_exists() {
        OrganizationDto organizationDto = OrganizationTesting.newOrganizationDto();
        insertOrganization(organizationDto);
        Optional<OrganizationDto> optional = underTest.selectByKey(dbSession, organizationDto.getKey());
        verifyOrganization(optional.get(), organizationDto);
    }

    @Test
    public void selectByKey_returns_row_data_of_specified_key() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.selectByKey(dbSession, "foo key")).isEmpty();
    }

    @Test
    public void selectByKey_is_case_sensitive() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.selectByKey(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getKey().toUpperCase(Locale.ENGLISH))).isEmpty();
    }

    @Test
    public void selectByUuid_returns_empty_when_table_is_empty() {
        assertThat(underTest.selectByUuid(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid())).isEmpty();
    }

    @Test
    public void selectByUuid_returns_row_data_when_uuid_exists() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        Optional<OrganizationDto> optional = underTest.selectByUuid(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid());
        verifyOrganization1(optional);
    }

    @Test
    public void selectByUuid_returns_row_data_of_specified_uuid() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.selectByUuid(dbSession, "foo uuid")).isEmpty();
    }

    @Test
    public void selectByUuid_is_case_sensitive() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.selectByUuid(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid().toUpperCase(Locale.ENGLISH))).isEmpty();
    }

    @Test
    public void selectByUuids_returns_empty_when_table_is_empty() {
        assertThat(underTest.selectByUuids(dbSession, ImmutableSet.of(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), OrganizationDaoTest.ORGANIZATION_DTO_2.getUuid()))).isEmpty();
    }

    @Test
    public void selectByUuids_returns_empty_when_argument_is_empty() {
        assertThat(underTest.selectByUuids(dbSession, Collections.emptySet())).isEmpty();
    }

    @Test
    public void selectByUuids_returns_row_data_of_single_uuid_when_uuid_exists() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        List<OrganizationDto> dtos = underTest.selectByUuids(dbSession, Collections.singleton(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid()));
        assertThat(dtos).hasSize(1);
        verifyOrganization1(dtos.iterator().next());
    }

    @Test
    public void selectByUuids_returns_row_data_of_multiple_uuid_when_uuid_exists() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        List<OrganizationDto> dtos = underTest.selectByUuids(dbSession, ImmutableSet.of(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), OrganizationDaoTest.ORGANIZATION_DTO_2.getUuid()));
        assertThat(dtos).hasSize(2);
        verifyOrganization1(dtos.stream().filter(( t) -> t.getUuid().equals(ORGANIZATION_DTO_1.getUuid())).findFirst().get());
        verifyOrganization(dtos.stream().filter(( t) -> t.getUuid().equals(ORGANIZATION_DTO_2.getUuid())).findFirst().get(), OrganizationDaoTest.ORGANIZATION_DTO_2);
    }

    @Test
    public void selectByUuids_returns_empty_when_no_uuid_exist() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        assertThat(underTest.selectByUuids(dbSession, ImmutableSet.of("foo uuid", "bar uuid"))).isEmpty();
    }

    @Test
    public void selectByUuids_returns_empty_when_no_single_uuid_exist() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        assertThat(underTest.selectByUuids(dbSession, ImmutableSet.of("foo uuid"))).isEmpty();
    }

    @Test
    public void selectByUuids_ignores_non_existing_uuids() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        List<OrganizationDto> dtos = underTest.selectByUuids(dbSession, ImmutableSet.of(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), "foo uuid", OrganizationDaoTest.ORGANIZATION_DTO_2.getUuid(), "bar uuid"));
        assertThat(dtos).extracting(OrganizationDto::getUuid).containsOnly(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), OrganizationDaoTest.ORGANIZATION_DTO_2.getUuid());
    }

    @Test
    public void selectByUuids_is_case_sensitive() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        assertThat(underTest.selectByUuids(dbSession, ImmutableSet.of(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid().toUpperCase(Locale.ENGLISH), OrganizationDaoTest.ORGANIZATION_DTO_2.getUuid().toUpperCase(Locale.ENGLISH)))).isEmpty();
    }

    @Test
    public void selectByOwnerId_returns_row_data_when_key_exists() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrgAlmBinding(OrganizationDaoTest.ORGANIZATION_DTO_1, ALM.GITHUB, "123456");
        Optional<OrganizationDto> result = underTest.selectByOrganizationAlmId(dbSession, ALM.GITHUB, "123456");
        verifyOrganization1(result);
    }

    @Test
    public void selectByOwnerId_returns_empty_if_organization_is_not_bound() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.selectByOrganizationAlmId(dbSession, ALM.GITHUB, "123456")).isEmpty();
    }

    @Test
    public void selectByOwnerId_returns_empty_if_ownerId_doesnt_match_any_install() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrgAlmBinding(OrganizationDaoTest.ORGANIZATION_DTO_1, ALM.GITHUB, "123456");
        assertThat(underTest.selectByOrganizationAlmId(dbSession, ALM.GITHUB, "unknown")).isEmpty();
    }

    @Test
    public void countByQuery() {
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid3").setKey("key-3"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid1").setKey("key-1"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid2").setKey("key-2"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid5").setKey("key-5"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid4").setKey("key-4"));
        assertThat(underTest.countByQuery(dbSession, OrganizationQuery.returnAll())).isEqualTo(5);
        assertThat(underTest.countByQuery(dbSession, OrganizationDaoTest.newQueryWithKeys("key-1", "key-2"))).isEqualTo(2);
        assertThat(underTest.countByQuery(dbSession, OrganizationDaoTest.newQueryWithKeys("unknown"))).isZero();
    }

    @Test
    public void selectByQuery_returns_empty_when_table_is_empty() {
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(2).andSize(1))).isEmpty();
    }

    @Test
    public void selectByQuery_returns_single_row_of_table_when_requesting_first_page_of_size_1_or_more() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(1).andSize(1))).hasSize(1).extracting("uuid").containsOnly(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid());
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(1).andSize(10))).hasSize(1).extracting("uuid").containsOnly(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid());
    }

    @Test
    public void selectByQuery_returns_empty_on_table_with_single_row_when_not_requesting_the_first_page() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(2).andSize(1))).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(((Math.abs(new Random().nextInt(10))) + 2)).andSize(1))).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(2).andSize(10))).isEmpty();
    }

    @Test
    public void selectByQuery_returns_rows_ordered_by_createdAt_descending_applying_requested_paging() {
        long time = 1999999L;
        Mockito.when(system2.now()).thenReturn(time, (time + 1000), (time + 2000), (time + 3000), (time + 5000));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid3").setKey("key-3"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid1").setKey("key-1"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid2").setKey("key-2"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid5").setKey("key-5"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid4").setKey("key-4"));
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(1).andSize(1))).extracting("uuid", "key").containsExactly(tuple("uuid4", "key-4"));
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(2).andSize(1))).extracting("uuid", "key").containsExactly(tuple("uuid5", "key-5"));
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(3).andSize(1))).extracting("uuid", "key").containsExactly(tuple("uuid2", "key-2"));
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(4).andSize(1))).extracting("uuid", "key").containsExactly(tuple("uuid1", "key-1"));
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(5).andSize(1))).extracting("uuid", "key").containsExactly(tuple("uuid3", "key-3"));
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(6).andSize(1))).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(1).andSize(5))).extracting("uuid").containsExactly("uuid4", "uuid5", "uuid2", "uuid1", "uuid3");
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(6).andSize(5))).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(1).andSize(3))).extracting("uuid").containsExactly("uuid4", "uuid5", "uuid2");
        assertThat(underTest.selectByQuery(dbSession, OrganizationQuery.returnAll(), Pagination.forPage(2).andSize(3))).extracting("uuid").containsExactly("uuid1", "uuid3");
    }

    @Test
    public void selectByQuery_with_keys_returns_empty_when_table_is_empty() {
        assertThat(underTest.selectByQuery(dbSession, OrganizationDaoTest.newQueryWithKeys("key1", "key2"), Pagination.forPage(2).andSize(1))).isEmpty();
    }

    @Test
    public void selectByQuery_with_keys_returns_single_row_of_table_when_requesting_first_page_of_size_1_or_more() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        OrganizationQuery organizationQuery = OrganizationDaoTest.newQueryWithKeys(OrganizationDaoTest.ORGANIZATION_DTO_1.getKey(), OrganizationDaoTest.ORGANIZATION_DTO_2.getKey());
        assertThat(underTest.selectByQuery(dbSession, organizationQuery, Pagination.forPage(1).andSize(1))).hasSize(1);
        assertThat(underTest.selectByQuery(dbSession, organizationQuery, Pagination.forPage(1).andSize(10))).hasSize(2).extracting(OrganizationDto::getUuid).containsOnly(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), OrganizationDaoTest.ORGANIZATION_DTO_2.getUuid());
    }

    @Test
    public void selectByQuery_with_empty_list_of_keys_returns_all() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        OrganizationQuery organizationQuery = OrganizationQuery.newOrganizationQueryBuilder().setKeys(Lists.emptyList()).build();
        assertThat(underTest.selectByQuery(dbSession, organizationQuery, Pagination.forPage(1).andSize(10))).extracting(OrganizationDto::getUuid).containsOnly(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), OrganizationDaoTest.ORGANIZATION_DTO_2.getUuid());
    }

    @Test
    public void selectByQuery_with_only_non_existent_keys_returns_empty() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        OrganizationQuery organizationQuery = OrganizationDaoTest.newQueryWithKeys(OrganizationDaoTest.PERMISSION_1, OrganizationDaoTest.PERMISSION_2, "dog");
        assertThat(underTest.selectByQuery(dbSession, organizationQuery, Pagination.forPage(1).andSize(10))).isEmpty();
    }

    @Test
    public void selectByQuery_with_ignores_non_existent_keys() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        OrganizationQuery organizationQuery = OrganizationDaoTest.newQueryWithKeys(OrganizationDaoTest.ORGANIZATION_DTO_1.getKey(), OrganizationDaoTest.PERMISSION_1, OrganizationDaoTest.ORGANIZATION_DTO_2.getKey(), OrganizationDaoTest.PERMISSION_2, "dog");
        assertThat(underTest.selectByQuery(dbSession, organizationQuery, Pagination.forPage(1).andSize(10))).hasSize(2).extracting(OrganizationDto::getUuid).containsOnly(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), OrganizationDaoTest.ORGANIZATION_DTO_2.getUuid());
    }

    @Test
    public void selectByQuery_with_keys_returns_empty_on_table_with_single_row_when_not_requesting_the_first_page() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_2);
        OrganizationQuery organizationQuery = OrganizationDaoTest.newQueryWithKeys(OrganizationDaoTest.ORGANIZATION_DTO_1.getKey(), OrganizationDaoTest.ORGANIZATION_DTO_2.getKey());
        assertThat(underTest.selectByQuery(dbSession, organizationQuery, Pagination.forPage(2).andSize(2))).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, organizationQuery, Pagination.forPage(((Math.abs(new Random().nextInt(10))) + 3)).andSize(1))).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, organizationQuery, Pagination.forPage(3).andSize(10))).isEmpty();
    }

    @Test
    public void selectByQuery_with_keys_returns_rows_ordered_by_createdAt_descending_applying_requested_paging() {
        long time = 1999999L;
        Mockito.when(system2.now()).thenReturn(time, (time + 1000), (time + 2000), (time + 3000), (time + 5000));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid3").setKey("key-3"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid1").setKey("key-1"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid2").setKey("key-2"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid5").setKey("key-5"));
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid("uuid4").setKey("key-4"));
        OrganizationQuery allExistingKeys = OrganizationDaoTest.newQueryWithKeys("key-1", "key-2", "key-3", "key-4", "key-5");
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(1).andSize(1))).extracting(OrganizationDto::getUuid, OrganizationDto::getKey).containsExactly(tuple("uuid4", "key-4"));
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(2).andSize(1))).extracting(OrganizationDto::getUuid, OrganizationDto::getKey).containsExactly(tuple("uuid5", "key-5"));
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(3).andSize(1))).extracting(OrganizationDto::getUuid, OrganizationDto::getKey).containsExactly(tuple("uuid2", "key-2"));
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(4).andSize(1))).extracting(OrganizationDto::getUuid, OrganizationDto::getKey).containsExactly(tuple("uuid1", "key-1"));
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(5).andSize(1))).extracting(OrganizationDto::getUuid, OrganizationDto::getKey).containsExactly(tuple("uuid3", "key-3"));
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(6).andSize(1))).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(1).andSize(5))).extracting(OrganizationDto::getUuid).containsExactly("uuid4", "uuid5", "uuid2", "uuid1", "uuid3");
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(2).andSize(5))).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(1).andSize(3))).extracting(OrganizationDto::getUuid).containsExactly("uuid4", "uuid5", "uuid2");
        assertThat(underTest.selectByQuery(dbSession, allExistingKeys, Pagination.forPage(2).andSize(3))).extracting(OrganizationDto::getUuid).containsExactly("uuid1", "uuid3");
    }

    @Test
    public void selectByQuery_filter_on_a_member() {
        OrganizationDto organization = db.organizations().insert();
        OrganizationDto anotherOrganization = db.organizations().insert();
        OrganizationDto organizationWithoutMember = db.organizations().insert();
        UserDto user = db.users().insertUser();
        db.organizations().addMember(organization, user);
        db.organizations().addMember(anotherOrganization, user);
        List<OrganizationDto> result = underTest.selectByQuery(dbSession, OrganizationQuery.newOrganizationQueryBuilder().setMember(user.getId()).build(), Pagination.forPage(1).andSize(100));
        assertThat(result).extracting(OrganizationDto::getUuid).containsExactlyInAnyOrder(organization.getUuid(), anotherOrganization.getUuid()).doesNotContain(organizationWithoutMember.getUuid());
    }

    @Test
    public void selectByQuery_filter_on_a_member_and_keys() {
        OrganizationDto organization = db.organizations().insert();
        OrganizationDto anotherOrganization = db.organizations().insert();
        OrganizationDto organizationWithoutKeyProvided = db.organizations().insert();
        OrganizationDto organizationWithoutMember = db.organizations().insert();
        UserDto user = db.users().insertUser();
        db.organizations().addMember(organization, user);
        db.organizations().addMember(anotherOrganization, user);
        db.organizations().addMember(organizationWithoutKeyProvided, user);
        List<OrganizationDto> result = underTest.selectByQuery(dbSession, OrganizationQuery.newOrganizationQueryBuilder().setKeys(Arrays.asList(organization.getKey(), anotherOrganization.getKey(), organizationWithoutMember.getKey())).setMember(user.getId()).build(), Pagination.forPage(1).andSize(100));
        assertThat(result).extracting(OrganizationDto::getUuid).containsExactlyInAnyOrder(organization.getUuid(), anotherOrganization.getUuid()).doesNotContain(organizationWithoutKeyProvided.getUuid(), organizationWithoutMember.getUuid());
    }

    @Test
    public void selectByQuery_filter_on_type() {
        OrganizationDto personalOrg1 = db.organizations().insert();
        db.users().insertUser(( u) -> u.setOrganizationUuid(personalOrg1.getUuid()));
        OrganizationDto personalOrg2 = db.organizations().insert();
        db.users().insertUser(( u) -> u.setOrganizationUuid(personalOrg2.getUuid()));
        OrganizationDto teamOrg1 = db.organizations().insert();
        assertThat(selectUuidsByQuery(( q) -> q.setOnlyPersonal(), Pagination.forPage(1).andSize(100))).containsExactlyInAnyOrder(personalOrg1.getUuid(), personalOrg2.getUuid());
        assertThat(selectUuidsByQuery(( q) -> q.setOnlyTeam(), Pagination.forPage(1).andSize(100))).containsExactlyInAnyOrder(teamOrg1.getUuid());
    }

    @Test
    public void selectByQuery_filter_on_withAnalyses() {
        assertThat(selectUuidsByQuery(( q) -> q.setWithAnalyses(), Pagination.forPage(1).andSize(100))).isEmpty();
        // has projects and analyses
        OrganizationDto orgWithAnalyses = db.organizations().insert();
        ComponentDto analyzedProject = db.components().insertPrivateProject(orgWithAnalyses);
        db.components().insertSnapshot(analyzedProject, ( s) -> s.setLast(true));
        // has projects but no analyses
        OrganizationDto orgWithProjects = db.organizations().insert();
        db.components().insertPrivateProject(orgWithProjects);
        db.components().insertPrivateProject(orgWithProjects, ( p) -> p.setEnabled(false));
        // has no projects
        db.organizations().insert();
        // has only disabled projects
        OrganizationDto orgWithOnlyDisabledProjects = db.organizations().insert();
        db.components().insertPrivateProject(orgWithOnlyDisabledProjects, ( p) -> p.setEnabled(false));
        assertThat(selectUuidsByQuery(( q) -> q.setWithAnalyses(), Pagination.forPage(1).andSize(100))).containsExactlyInAnyOrder(orgWithAnalyses.getUuid());
    }

    @Test
    public void getDefaultTemplates_returns_empty_when_table_is_empty() {
        assertThat(underTest.getDefaultTemplates(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid())).isEmpty();
    }

    @Test
    public void getDefaultTemplates_returns_empty_when_row_exists_but_all_default_templates_columns_are_null() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.getDefaultTemplates(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid())).isEmpty();
    }

    @Test
    public void getDefaultTemplates_returns_data_when_project_default_templates_column_is_not_null() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        underTest.setDefaultTemplates(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), new DefaultTemplates().setProjectUuid(OrganizationDaoTest.PERMISSION_1));
        verifyGetDefaultTemplates(OrganizationDaoTest.ORGANIZATION_DTO_1, OrganizationDaoTest.PERMISSION_1, null);
    }

    @Test
    public void getDefaultTemplates_returns_data_when_project_and_view_default_template_column_are_not_null() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        setDefaultTemplate(OrganizationDaoTest.ORGANIZATION_DTO_1, OrganizationDaoTest.PERMISSION_1, OrganizationDaoTest.PERMISSION_2);
        verifyGetDefaultTemplates(OrganizationDaoTest.ORGANIZATION_DTO_1, OrganizationDaoTest.PERMISSION_1, OrganizationDaoTest.PERMISSION_2);
    }

    @Test
    public void getDefaultTemplates_returns_empty_when_only_view_default_template_column_is_not_null() {
        dirtyInsertWithDefaultTemplate("uuid1", null, OrganizationDaoTest.PERMISSION_2);
        assertThat(underTest.getDefaultTemplates(dbSession, "uuid1")).isEmpty();
    }

    @Test
    public void getDefaultTemplates_returns_empty_when_project_and_view_default_template_column_are_not_null() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.getDefaultTemplates(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid())).isEmpty();
    }

    @Test
    public void getDefaultTemplates_is_case_sensitive() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        underTest.setDefaultTemplates(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), new DefaultTemplates().setProjectUuid(OrganizationDaoTest.PERMISSION_1).setApplicationsUuid(OrganizationDaoTest.PERMISSION_2));
        assertThat(underTest.getDefaultTemplates(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid().toUpperCase(Locale.ENGLISH))).isEmpty();
    }

    @Test
    public void setDefaultTemplates_throws_NPE_when_uuid_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("uuid can't be null");
        underTest.setDefaultTemplates(dbSession, null, new DefaultTemplates().setProjectUuid("p"));
    }

    @Test
    public void setDefaultTemplates_throws_NPE_when_defaultTemplate_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("defaultTemplates can't be null");
        underTest.setDefaultTemplates(dbSession, "uuid", null);
    }

    @Test
    public void setDefaultTemplates_throws_NPE_when_defaultTemplate_project_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("defaultTemplates.project can't be null");
        underTest.setDefaultTemplates(dbSession, "uuid", new DefaultTemplates());
    }

    @Test
    public void setDefaultTemplates_throws_NPE_when_defaultTemplate_project_is_null_and_view_is_not() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("defaultTemplates.project can't be null");
        underTest.setDefaultTemplates(dbSession, "uuid", new DefaultTemplates().setApplicationsUuid(OrganizationDaoTest.PERMISSION_1));
    }

    @Test
    public void getDefaultGroupId_returns_empty_when_default_group_id_is_null() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1.setDefaultGroupId(null));
        assertThat(underTest.getDefaultGroupId(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid())).isEmpty();
    }

    @Test
    public void getDefaultGroupId_returns_data_when_default_group_id_is_not_null() {
        Mockito.when(system2.now()).thenReturn(OrganizationDaoTest.DATE_3);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        underTest.setDefaultGroupId(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), GroupTesting.newGroupDto().setId(10));
        Optional<Integer> optional = underTest.getDefaultGroupId(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid());
        assertThat(optional).isNotEmpty();
        assertThat(optional.get()).isEqualTo(10);
        verifyOrganizationUpdatedAt(OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid(), OrganizationDaoTest.DATE_3);
    }

    @Test
    public void setDefaultGroupId_throws_NPE_when_uuid_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("uuid can't be null");
        underTest.setDefaultGroupId(dbSession, null, GroupTesting.newGroupDto().setId(10));
    }

    @Test
    public void setDefaultGroupId_throws_NPE_when_default_group_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Default group cannot be null");
        underTest.setDefaultGroupId(dbSession, "uuid", null);
    }

    @Test
    public void setDefaultGroupId_throws_NPE_when_default_group_id_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Default group id cannot be null");
        underTest.setDefaultGroupId(dbSession, "uuid", GroupTesting.newGroupDto().setId(null));
    }

    @Test
    public void setDefaultQualityGate() {
        Mockito.when(system2.now()).thenReturn(OrganizationDaoTest.DATE_3);
        OrganizationDto organization = db.organizations().insert();
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(organization);
        underTest.setDefaultQualityGate(dbSession, organization, qualityGate);
        db.commit();
        assertThat(dbClient.qualityGateDao().selectDefault(dbSession, organization).getUuid()).isEqualTo(qualityGate.getUuid());
        verifyOrganizationUpdatedAt(organization.getUuid(), OrganizationDaoTest.DATE_3);
    }

    @Test
    public void update_fails_with_NPE_if_OrganizationDto_is_null() {
        expectDtoCanNotBeNull();
        underTest.update(dbSession, null);
    }

    @Test
    public void update_does_not_fail_if_specified_row_does_not_exist() {
        underTest.update(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1);
    }

    @Test
    public void update_with_same_information_succeeds_but_has_no_effect() {
        OrganizationDto organizationDto = OrganizationTesting.newOrganizationDto();
        insertOrganization(organizationDto);
        underTest.update(dbSession, organizationDto);
        dbSession.commit();
        Map<String, Object> row = selectSingleRow();
        assertThat(row.get("uuid")).isEqualTo(organizationDto.getUuid());
        assertThat(row.get("key")).isEqualTo(organizationDto.getKey());
        assertThat(row.get("name")).isEqualTo(organizationDto.getName());
        assertThat(row.get("description")).isEqualTo(organizationDto.getDescription());
        assertThat(row.get("url")).isEqualTo(organizationDto.getUrl());
        assertThat(row.get("avatarUrl")).isEqualTo(organizationDto.getAvatarUrl());
        assertThat(row.get("subscription")).isEqualTo(organizationDto.getSubscription().name());
        assertThat(row.get("createdAt")).isEqualTo(organizationDto.getCreatedAt());
        assertThat(row.get("updatedAt")).isEqualTo(organizationDto.getUpdatedAt());
    }

    @Test
    public void update_populates_updatedAt_with_date_from_System2() {
        Mockito.when(system2.now()).thenReturn(OrganizationDaoTest.DATE_1);
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        Mockito.when(system2.now()).thenReturn(OrganizationDaoTest.DATE_2);
        underTest.update(dbSession, copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUpdatedAt(2000L));
        dbSession.commit();
        Map<String, Object> row = selectSingleRow();
        assertThat(row.get("createdAt")).isEqualTo(OrganizationDaoTest.DATE_1);
        assertThat(row.get("updatedAt")).isEqualTo(OrganizationDaoTest.DATE_2);
    }

    @Test
    public void update() {
        Mockito.when(system2.now()).thenReturn(OrganizationDaoTest.DATE_1);
        OrganizationDto oldOrganization = OrganizationTesting.newOrganizationDto().setUuid("new_uuid").setKey("old_key").setName("old_name").setDescription("old_desc").setAvatarUrl("old_avatar").setSubscription(Subscription.FREE).setUrl("old_url");
        insertOrganization(oldOrganization);
        Mockito.when(system2.now()).thenReturn(OrganizationDaoTest.DATE_3);
        OrganizationDto updatedOrganization = OrganizationTesting.newOrganizationDto().setUuid("new_uuid").setKey("new_key").setName("new_name").setDescription("new_desc").setAvatarUrl("new_avatar").setDefaultGroupId(11).setSubscription(Subscription.PAID).setUrl("new_url").setCreatedAt(2000L).setUpdatedAt(3000L);
        underTest.update(dbSession, updatedOrganization);
        dbSession.commit();
        Map<String, Object> row = selectSingleRow();
        assertThat(row.get("uuid")).isEqualTo(updatedOrganization.getUuid());
        assertThat(row.get("key")).isEqualTo(updatedOrganization.getKey());
        assertThat(row.get("name")).isEqualTo(updatedOrganization.getName());
        assertThat(row.get("description")).isEqualTo(updatedOrganization.getDescription());
        assertThat(row.get("url")).isEqualTo(updatedOrganization.getUrl());
        assertThat(row.get("avatarUrl")).isEqualTo(updatedOrganization.getAvatarUrl());
        assertThat(row.get("subscription")).isEqualTo(updatedOrganization.getSubscription().name());
        assertThat(row.get("createdAt")).isEqualTo(OrganizationDaoTest.DATE_1);
        assertThat(row.get("updatedAt")).isEqualTo(OrganizationDaoTest.DATE_3);
    }

    @Test
    public void update_fails_if_name_is_null() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        expectedException.expect(PersistenceException.class);
        underTest.update(dbSession, copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setName(null));
    }

    @Test
    public void deleteByUuid_does_not_fail_on_empty_table() {
        assertThat(underTest.deleteByUuid(dbSession, "uuid")).isEqualTo(0);
        dbSession.commit();
    }

    @Test
    public void deleteByUuid_does_not_fail_on_non_existing_row() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        assertThat(underTest.deleteByUuid(dbSession, "uuid")).isEqualTo(0);
        dbSession.commit();
    }

    @Test
    public void deleteByUuid_deletes_row_with_specified_uuid() {
        insertOrganization(OrganizationDaoTest.ORGANIZATION_DTO_1);
        String anotherUuid = "uuid";
        insertOrganization(copyOf(OrganizationDaoTest.ORGANIZATION_DTO_1).setUuid(anotherUuid).setKey("key"));
        assertThat(db.countRowsOfTable("organizations")).isEqualTo(2);
        assertThat(underTest.deleteByUuid(dbSession, anotherUuid)).isEqualTo(1);
        dbSession.commit();
        assertThat(underTest.selectByUuid(dbSession, anotherUuid)).isEmpty();
        assertThat(underTest.selectByUuid(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid())).isNotEmpty();
        assertThat(db.countRowsOfTable("organizations")).isEqualTo(1);
        assertThat(underTest.deleteByUuid(dbSession, anotherUuid)).isEqualTo(0);
        assertThat(underTest.deleteByUuid(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid())).isEqualTo(1);
        dbSession.commit();
        assertThat(underTest.selectByUuid(dbSession, OrganizationDaoTest.ORGANIZATION_DTO_1.getUuid())).isEmpty();
        assertThat(db.countRowsOfTable("organizations")).isEqualTo(0);
    }

    @Test
    public void selectByPermission_returns_organization_when_user_has_ADMIN_user_permission_on_some_organization() {
        UserDto user = db.users().insertUser();
        OrganizationDto organization1 = db.organizations().insert();
        db.users().insertPermissionOnUser(organization1, user, OrganizationDaoTest.PERMISSION_2);
        OrganizationDto organization2 = db.organizations().insert();
        db.users().insertPermissionOnUser(organization2, user, OrganizationDaoTest.PERMISSION_2);
        UserDto otherUser = db.users().insertUser();
        OrganizationDto organization3 = db.organizations().insert();
        db.users().insertPermissionOnUser(organization3, otherUser, OrganizationDaoTest.PERMISSION_2);
        assertThat(underTest.selectByPermission(dbSession, user.getId(), OrganizationDaoTest.PERMISSION_2)).extracting(OrganizationDto::getUuid).containsOnly(organization1.getUuid(), organization2.getUuid());
        assertThat(underTest.selectByPermission(dbSession, otherUser.getId(), OrganizationDaoTest.PERMISSION_2)).extracting(OrganizationDto::getUuid).containsOnly(organization3.getUuid());
        assertThat(underTest.selectByPermission(dbSession, 1234, OrganizationDaoTest.PERMISSION_2)).isEmpty();
    }

    @Test
    public void selectByPermission_returns_organization_when_user_has_ADMIN_group_permission_on_some_organization() {
        UserDto user = db.users().insertUser();
        OrganizationDto organization1 = db.organizations().insert();
        GroupDto defaultGroup = db.users().insertGroup(organization1);
        db.users().insertPermissionOnGroup(defaultGroup, OrganizationDaoTest.PERMISSION_1);
        db.users().insertMember(defaultGroup, user);
        OrganizationDto organization2 = db.organizations().insert();
        GroupDto group1 = db.users().insertGroup(organization2);
        db.users().insertPermissionOnGroup(group1, OrganizationDaoTest.PERMISSION_1);
        db.users().insertMember(group1, user);
        UserDto otherUser = db.users().insertUser();
        OrganizationDto organization3 = db.organizations().insert();
        GroupDto group2 = db.users().insertGroup(organization3);
        db.users().insertPermissionOnGroup(group2, OrganizationDaoTest.PERMISSION_1);
        db.users().insertMember(group2, otherUser);
        assertThat(underTest.selectByPermission(dbSession, user.getId(), OrganizationDaoTest.PERMISSION_1)).extracting(OrganizationDto::getUuid).containsOnly(organization1.getUuid(), organization2.getUuid());
        assertThat(underTest.selectByPermission(dbSession, otherUser.getId(), OrganizationDaoTest.PERMISSION_1)).extracting(OrganizationDto::getUuid).containsOnly(organization3.getUuid());
        assertThat(underTest.selectByPermission(dbSession, 1234, OrganizationDaoTest.PERMISSION_1)).isEmpty();
    }

    @Test
    public void selectByPermission_return_organization_only_once_even_if_user_has_ADMIN_permission_twice_or_more() {
        String permission = "destroy";
        UserDto user = db.users().insertUser();
        OrganizationDto organization = db.organizations().insert();
        GroupDto group1 = db.users().insertGroup(organization);
        db.users().insertPermissionOnGroup(group1, permission);
        db.users().insertMember(group1, user);
        GroupDto group2 = db.users().insertGroup(organization);
        db.users().insertPermissionOnGroup(group2, permission);
        db.users().insertMember(group2, user);
        db.users().insertPermissionOnUser(organization, user, permission);
        assertThat(underTest.selectByPermission(dbSession, user.getId(), permission)).extracting(OrganizationDto::getUuid).containsOnlyOnce(organization.getUuid());
    }

    @Test
    public void selectByPermission_returns_organization_only_if_user_has_specific_permission_by_user_permission() {
        OrganizationDto organization = db.organizations().insert();
        OrganizationDto otherOrganization = db.organizations().insert();
        UserDto user = db.users().insertUser();
        db.users().insertPermissionOnUser(organization, user, OrganizationDaoTest.PERMISSION_1);
        db.users().insertPermissionOnUser(otherOrganization, user, OrganizationDaoTest.PERMISSION_2);
        UserDto otherUser = db.users().insertUser();
        db.users().insertPermissionOnUser(organization, otherUser, OrganizationDaoTest.PERMISSION_2);
        db.users().insertPermissionOnUser(otherOrganization, otherUser, OrganizationDaoTest.PERMISSION_1);
        assertThat(underTest.selectByPermission(dbSession, user.getId(), OrganizationDaoTest.PERMISSION_1)).extracting(OrganizationDto::getUuid).containsOnlyOnce(organization.getUuid());
        assertThat(underTest.selectByPermission(dbSession, user.getId(), OrganizationDaoTest.PERMISSION_2)).extracting(OrganizationDto::getUuid).containsOnlyOnce(otherOrganization.getUuid());
        assertThat(underTest.selectByPermission(dbSession, otherUser.getId(), OrganizationDaoTest.PERMISSION_1)).extracting(OrganizationDto::getUuid).containsOnlyOnce(otherOrganization.getUuid());
        assertThat(underTest.selectByPermission(dbSession, otherUser.getId(), OrganizationDaoTest.PERMISSION_2)).extracting(OrganizationDto::getUuid).containsOnlyOnce(organization.getUuid());
    }

    @Test
    public void selectByPermission_returns_organization_only_if_user_has_specific_permission_by_group_permission() {
        OrganizationDto organization = db.organizations().insert();
        OrganizationDto otherOrganization = db.organizations().insert();
        GroupDto group1 = db.users().insertGroup(organization);
        GroupDto group2 = db.users().insertGroup(organization);
        GroupDto otherGroup1 = db.users().insertGroup(otherOrganization);
        GroupDto otherGroup2 = db.users().insertGroup(otherOrganization);
        db.users().insertPermissionOnGroup(group1, OrganizationDaoTest.PERMISSION_1);
        db.users().insertPermissionOnGroup(otherGroup2, OrganizationDaoTest.PERMISSION_2);
        db.users().insertPermissionOnGroup(group2, OrganizationDaoTest.PERMISSION_2);
        db.users().insertPermissionOnGroup(otherGroup1, OrganizationDaoTest.PERMISSION_1);
        UserDto user = db.users().insertUser();
        db.users().insertMember(group1, user);
        db.users().insertMember(otherGroup2, user);
        UserDto otherUser = db.users().insertUser();
        db.users().insertMember(group2, otherUser);
        db.users().insertMember(otherGroup1, otherUser);
        assertThat(underTest.selectByPermission(dbSession, user.getId(), OrganizationDaoTest.PERMISSION_1)).extracting(OrganizationDto::getUuid).containsOnlyOnce(organization.getUuid());
        assertThat(underTest.selectByPermission(dbSession, user.getId(), OrganizationDaoTest.PERMISSION_2)).extracting(OrganizationDto::getUuid).containsOnlyOnce(otherOrganization.getUuid());
        assertThat(underTest.selectByPermission(dbSession, otherUser.getId(), OrganizationDaoTest.PERMISSION_1)).extracting(OrganizationDto::getUuid).containsOnlyOnce(otherOrganization.getUuid());
        assertThat(underTest.selectByPermission(dbSession, otherUser.getId(), OrganizationDaoTest.PERMISSION_2)).extracting(OrganizationDto::getUuid).containsOnlyOnce(organization.getUuid());
    }

    @Test
    public void selectOrganizationsWithNcloc_on_zero_orgs() {
        assertThat(underTest.selectOrganizationsWithNcloc(dbSession, new ArrayList())).isEmpty();
    }

    @Test
    public void selectOrganizationsWithNcloc_with_not_existing_uuid() {
        MetricDto ncloc = db.measures().insertMetric(( m) -> m.setKey(NCLOC_KEY));
        OrganizationDto org1 = db.organizations().insert();
        assertThat(underTest.selectOrganizationsWithNcloc(dbSession, Lists.newArrayList("xxxx"))).isEmpty();
    }

    @Test
    public void selectOrganizationsWithNcloc_with_organization_without_projects() {
        MetricDto ncloc = db.measures().insertMetric(( m) -> m.setKey(NCLOC_KEY));
        OrganizationDto org1 = db.organizations().insert();
        assertThat(underTest.selectOrganizationsWithNcloc(dbSession, Lists.newArrayList(org1.getUuid()))).extracting(OrganizationWithNclocDto::getId, OrganizationWithNclocDto::getKee, OrganizationWithNclocDto::getName, OrganizationWithNclocDto::getNcloc).containsExactlyInAnyOrder(tuple(org1.getUuid(), org1.getKey(), org1.getName(), 0L));
    }

    @Test
    public void selectOrganizationsWithNcloc_with_one_organization() {
        MetricDto ncloc = db.measures().insertMetric(( m) -> m.setKey(NCLOC_KEY));
        OrganizationDto org1 = db.organizations().insert();
        // private project with highest ncloc in non-main branch
        ComponentDto project1 = db.components().insertMainBranch(org1);
        ComponentDto project1Branch = db.components().insertProjectBranch(project1);
        db.measures().insertLiveMeasure(project1, ncloc, ( m) -> m.setValue(1000.0));
        db.measures().insertLiveMeasure(project1Branch, ncloc, ( m) -> m.setValue(110000.0));
        // public project that must be ignored
        ComponentDto project2 = db.components().insertPublicProject(org1);
        ComponentDto project2Branch = db.components().insertProjectBranch(project2);
        db.measures().insertLiveMeasure(project2, ncloc, ( m) -> m.setValue(1.0E9));
        db.measures().insertLiveMeasure(project2Branch, ncloc, ( m) -> m.setValue(1000000.0));
        assertThat(underTest.selectOrganizationsWithNcloc(dbSession, Lists.newArrayList(org1.getUuid()))).extracting(OrganizationWithNclocDto::getId, OrganizationWithNclocDto::getKee, OrganizationWithNclocDto::getName, OrganizationWithNclocDto::getNcloc).containsExactlyInAnyOrder(tuple(org1.getUuid(), org1.getKey(), org1.getName(), 110000L));
    }

    @Test
    public void selectOrganizationsWithNcloc_with_multiple_organizations() {
        MetricDto ncloc = db.measures().insertMetric(( m) -> m.setKey(NCLOC_KEY));
        Tuple[] expectedResults = new Tuple[9];
        List<String> orgUuids = new ArrayList<>();
        IntStream.range(0, 9).forEach(( i) -> {
            OrganizationDto org = db.organizations().insert();
            orgUuids.add(org.getUuid());
            int maxPrivate = insertPrivateProjectsWithBranches(org, ncloc);
            // Now we are creating public project asking for maxPrivate as minimum ncloc
            // because those projects *MUST* not be taken during the calculation of ncloc for a private
            // organization
            insertPublicProjectsWithBranches(org, ncloc, maxPrivate);
            expectedResults[i] = tuple(org.getUuid(), org.getKey(), org.getName(), ((long) (maxPrivate)));
        });
        assertThat(underTest.selectOrganizationsWithNcloc(dbSession, orgUuids)).extracting(OrganizationWithNclocDto::getId, OrganizationWithNclocDto::getKee, OrganizationWithNclocDto::getName, OrganizationWithNclocDto::getNcloc).containsExactlyInAnyOrder(expectedResults);
    }
}

