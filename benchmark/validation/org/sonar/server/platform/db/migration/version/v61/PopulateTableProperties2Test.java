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
package org.sonar.server.platform.db.migration.version.v61;


import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.assertj.core.api.AbstractAssert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.CoreDbTester;


public class PopulateTableProperties2Test {
    private static final String EMPTY_PROPERTY = "";

    private static final String VALUE_SMALL = "some small value";

    private static final String VALUE_SIZE_4000 = String.format("%1$4000.4000s", "*");

    private static final String VALUE_SIZE_4001 = (PopulateTableProperties2Test.VALUE_SIZE_4000) + "P";

    private static final long DATE_1 = 1555000L;

    private static final long DATE_2 = 2666000L;

    private static final long DATE_3 = 3777000L;

    private static final long DATE_4 = 4888000L;

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public CoreDbTester dbTester = CoreDbTester.createForSchema(PopulateTableProperties2Test.class, "properties_and_properties_2_tables.sql");

    private PopulateTableProperties2 underTest = new PopulateTableProperties2(dbTester.database(), system2);

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(dbTester.countRowsOfTable("properties")).isEqualTo(0);
        assertThat(dbTester.countRowsOfTable("properties2")).isEqualTo(0);
    }

    @Test
    public void migration_does_copy_again_properties_which_are_already_copied() throws SQLException {
        insertProperty(1, PopulateTableProperties2Test.VALUE_SMALL, null, null);
        insertProperty(10, PopulateTableProperties2Test.VALUE_SMALL, null, null);
        insertProperty(2, PopulateTableProperties2Test.VALUE_SMALL, null, 21);
        insertProperty(20, PopulateTableProperties2Test.VALUE_SMALL, null, 21);
        insertProperty(3, PopulateTableProperties2Test.VALUE_SMALL, 31, null);
        insertProperty(30, PopulateTableProperties2Test.VALUE_SMALL, 31, null);
        insertProperty2(1, PopulateTableProperties2Test.VALUE_SMALL, null, null);
        insertProperty2(2, PopulateTableProperties2Test.VALUE_SMALL, null, 21);
        insertProperty2(3, PopulateTableProperties2Test.VALUE_SMALL, 31, null);
        assertThat(dbTester.countRowsOfTable("properties")).isEqualTo(6);
        assertThat(dbTester.countRowsOfTable("properties2")).isEqualTo(3);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable("properties")).isEqualTo(6);
        assertThat(dbTester.countRowsOfTable("properties2")).isEqualTo(6);
    }

    @Test
    public void migration_moves_global_properties() throws SQLException {
        Mockito.when(system2.now()).thenReturn(PopulateTableProperties2Test.DATE_1, PopulateTableProperties2Test.DATE_2, PopulateTableProperties2Test.DATE_3, PopulateTableProperties2Test.DATE_4);
        insertProperty(1, PopulateTableProperties2Test.VALUE_SMALL, null, null);
        insertProperty(2, PopulateTableProperties2Test.EMPTY_PROPERTY, null, null);
        insertProperty(3, PopulateTableProperties2Test.VALUE_SIZE_4000, null, null);
        insertProperty(4, PopulateTableProperties2Test.VALUE_SIZE_4001, null, null);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable("properties")).isEqualTo(4);
        assertThat(dbTester.countRowsOfTable("properties2")).isEqualTo(4);
        assertThatProperty2(1).hasNoResourceId().hasNoUserId().hasTextValue(PopulateTableProperties2Test.VALUE_SMALL).hasCreatedAt(PopulateTableProperties2Test.DATE_1);
        assertThatProperty2(2).hasNoResourceId().hasNoUserId().isEmpty().hasCreatedAt(PopulateTableProperties2Test.DATE_2);
        assertThatProperty2(3).hasNoResourceId().hasNoUserId().hasTextValue(PopulateTableProperties2Test.VALUE_SIZE_4000).hasCreatedAt(PopulateTableProperties2Test.DATE_3);
        assertThatProperty2(4).hasNoResourceId().hasNoUserId().hasClobValue(PopulateTableProperties2Test.VALUE_SIZE_4001).hasCreatedAt(PopulateTableProperties2Test.DATE_4);
    }

    @Test
    public void migration_moves_user_properties() throws SQLException {
        Mockito.when(system2.now()).thenReturn(PopulateTableProperties2Test.DATE_1, PopulateTableProperties2Test.DATE_2, PopulateTableProperties2Test.DATE_3, PopulateTableProperties2Test.DATE_4);
        insertProperty(1, PopulateTableProperties2Test.VALUE_SMALL, null, 11);
        insertProperty(2, PopulateTableProperties2Test.EMPTY_PROPERTY, null, 12);
        insertProperty(3, PopulateTableProperties2Test.VALUE_SIZE_4000, null, 13);
        insertProperty(4, PopulateTableProperties2Test.VALUE_SIZE_4001, null, 14);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable("properties")).isEqualTo(4);
        assertThat(dbTester.countRowsOfTable("properties2")).isEqualTo(4);
        assertThatProperty2(1).hasNoResourceId().hasUserId(11).hasTextValue(PopulateTableProperties2Test.VALUE_SMALL).hasCreatedAt(PopulateTableProperties2Test.DATE_1);
        assertThatProperty2(2).hasNoResourceId().hasUserId(12).isEmpty().hasCreatedAt(PopulateTableProperties2Test.DATE_2);
        assertThatProperty2(3).hasNoResourceId().hasUserId(13).hasTextValue(PopulateTableProperties2Test.VALUE_SIZE_4000).hasCreatedAt(PopulateTableProperties2Test.DATE_3);
        assertThatProperty2(4).hasNoResourceId().hasUserId(14).hasClobValue(PopulateTableProperties2Test.VALUE_SIZE_4001).hasCreatedAt(PopulateTableProperties2Test.DATE_4);
    }

    @Test
    public void migration_moves_component_properties() throws SQLException {
        Mockito.when(system2.now()).thenReturn(PopulateTableProperties2Test.DATE_1, PopulateTableProperties2Test.DATE_2, PopulateTableProperties2Test.DATE_3, PopulateTableProperties2Test.DATE_4);
        insertProperty(1, PopulateTableProperties2Test.VALUE_SMALL, 11, null);
        insertProperty(2, PopulateTableProperties2Test.EMPTY_PROPERTY, 12, null);
        insertProperty(3, PopulateTableProperties2Test.VALUE_SIZE_4000, 13, null);
        insertProperty(4, PopulateTableProperties2Test.VALUE_SIZE_4001, 14, null);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable("properties")).isEqualTo(4);
        assertThat(dbTester.countRowsOfTable("properties2")).isEqualTo(4);
        assertThatProperty2(1).hasResourceId(11).hasNoUserId().hasTextValue(PopulateTableProperties2Test.VALUE_SMALL).hasCreatedAt(PopulateTableProperties2Test.DATE_1);
        assertThatProperty2(2).hasResourceId(12).hasNoUserId().isEmpty().hasCreatedAt(PopulateTableProperties2Test.DATE_2);
        assertThatProperty2(3).hasResourceId(13).hasNoUserId().hasTextValue(PopulateTableProperties2Test.VALUE_SIZE_4000).hasCreatedAt(PopulateTableProperties2Test.DATE_3);
        assertThatProperty2(4).hasResourceId(14).hasNoUserId().hasClobValue(PopulateTableProperties2Test.VALUE_SIZE_4001).hasCreatedAt(PopulateTableProperties2Test.DATE_4);
    }

    private static class Property2Assert extends AbstractAssert<PopulateTableProperties2Test.Property2Assert, PopulateTableProperties2Test.Property2> {
        private Property2Assert(CoreDbTester dbTester, String internalPropertyKey) {
            super(PopulateTableProperties2Test.Property2Assert.asInternalProperty(dbTester, internalPropertyKey), PopulateTableProperties2Test.Property2Assert.class);
        }

        private static PopulateTableProperties2Test.Property2 asInternalProperty(CoreDbTester dbTester, String key) {
            Map<String, Object> row = dbTester.selectFirst(((("select" + ((" user_id as \"userId\", resource_id as \"resourceId\", is_empty as \"isEmpty\", text_value as \"textValue\", clob_value as \"clobValue\", created_at as \"createdAt\"" + " from properties2") + " where prop_key='")) + key) + "'"));
            Long userId = ((Long) (row.get("userId")));
            return new PopulateTableProperties2Test.Property2((userId == null ? null : userId.intValue()), ((Long) (row.get("resourceId"))), PopulateTableProperties2Test.Property2Assert.isEmpty(row), ((String) (row.get("textValue"))), ((String) (row.get("clobValue"))), ((Long) (row.get("createdAt"))));
        }

        private static Boolean isEmpty(Map<String, Object> row) {
            Object flag = row.get("isEmpty");
            if (flag instanceof Boolean) {
                return ((Boolean) (flag));
            }
            if (flag instanceof Long) {
                Long longBoolean = ((Long) (flag));
                return longBoolean.equals(1L);
            }
            throw new IllegalArgumentException(("Unsupported object type returned for column \"isEmpty\": " + (flag.getClass())));
        }

        public PopulateTableProperties2Test.Property2Assert hasNoUserId() {
            isNotNull();
            if ((actual.getUserId()) != null) {
                failWithMessage("Expected Property2 to have column USER_ID to be null but was <%s>", actual.getUserId());
            }
            return this;
        }

        public PopulateTableProperties2Test.Property2Assert hasUserId(int expected) {
            isNotNull();
            if (!(Objects.equals(actual.getUserId(), expected))) {
                failWithMessage("Expected Property2 to have column USER_ID to be <%s> but was <%s>", true, actual.getUserId());
            }
            return this;
        }

        public PopulateTableProperties2Test.Property2Assert hasNoResourceId() {
            isNotNull();
            if ((actual.getResourceId()) != null) {
                failWithMessage("Expected Property2 to have column RESOURCE_ID to be null but was <%s>", actual.getResourceId());
            }
            return this;
        }

        public PopulateTableProperties2Test.Property2Assert hasResourceId(long expected) {
            isNotNull();
            if (!(Objects.equals(actual.getResourceId(), expected))) {
                failWithMessage("Expected Property2 to have column RESOURCE_ID to be <%s> but was <%s>", true, actual.getResourceId());
            }
            return this;
        }

        public PopulateTableProperties2Test.Property2Assert isEmpty() {
            isNotNull();
            if (!(Objects.equals(actual.getEmpty(), Boolean.TRUE))) {
                failWithMessage("Expected Property2 to have column IS_EMPTY to be <%s> but was <%s>", true, actual.getEmpty());
            }
            if ((actual.getTextValue()) != null) {
                failWithMessage("Expected Property2 to have column TEXT_VALUE to be null but was <%s>", actual.getTextValue());
            }
            if ((actual.getClobValue()) != null) {
                failWithMessage("Expected Property2 to have column CLOB_VALUE to be null but was <%s>", actual.getClobValue());
            }
            return this;
        }

        public PopulateTableProperties2Test.Property2Assert hasTextValue(String expected) {
            isNotNull();
            if (!(Objects.equals(actual.getTextValue(), Objects.requireNonNull(expected)))) {
                failWithMessage("Expected Property2 to have column TEXT_VALUE to be <%s> but was <%s>", expected, actual.getTextValue());
            }
            if ((actual.getClobValue()) != null) {
                failWithMessage("Expected Property2 to have column CLOB_VALUE to be null but was <%s>", actual.getClobValue());
            }
            if (!(Objects.equals(actual.getEmpty(), Boolean.FALSE))) {
                failWithMessage("Expected Property2 to have column IS_EMPTY to be <%s> but was <%s>", false, actual.getEmpty());
            }
            return this;
        }

        public PopulateTableProperties2Test.Property2Assert hasClobValue(String expected) {
            isNotNull();
            if (!(Objects.equals(actual.getClobValue(), Objects.requireNonNull(expected)))) {
                failWithMessage("Expected Property2 to have column CLOB_VALUE to be <%s> but was <%s>", expected, actual.getClobValue());
            }
            if ((actual.getTextValue()) != null) {
                failWithMessage("Expected Property2 to have column TEXT_VALUE to be null but was <%s>", actual.getTextValue());
            }
            if (!(Objects.equals(actual.getEmpty(), Boolean.FALSE))) {
                failWithMessage("Expected Property2 to have column IS_EMPTY to be <%s> but was <%s>", false, actual.getEmpty());
            }
            return this;
        }

        public PopulateTableProperties2Test.Property2Assert hasCreatedAt(long expected) {
            isNotNull();
            if (!(Objects.equals(actual.getCreatedAt(), expected))) {
                failWithMessage("Expected Property2 to have column CREATED_AT to be <%s> but was <%s>", expected, actual.getCreatedAt());
            }
            return this;
        }
    }

    private static final class Property2 {
        private final Integer userId;

        private final Long resourceId;

        private final Boolean empty;

        private final String textValue;

        private final String clobValue;

        private final Long createdAt;

        private Property2(@Nullable
        Integer userId, @Nullable
        Long resourceId, @Nullable
        Boolean empty, @Nullable
        String textValue, @Nullable
        String clobValue, @Nullable
        Long createdAt) {
            this.userId = userId;
            this.resourceId = resourceId;
            this.empty = empty;
            this.textValue = textValue;
            this.clobValue = clobValue;
            this.createdAt = createdAt;
        }

        public Integer getUserId() {
            return userId;
        }

        public Long getResourceId() {
            return resourceId;
        }

        @CheckForNull
        public Boolean getEmpty() {
            return empty;
        }

        @CheckForNull
        public String getTextValue() {
            return textValue;
        }

        @CheckForNull
        public String getClobValue() {
            return clobValue;
        }

        @CheckForNull
        public Long getCreatedAt() {
            return createdAt;
        }
    }
}

