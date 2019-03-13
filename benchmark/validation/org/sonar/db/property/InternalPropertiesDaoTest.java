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
package org.sonar.db.property;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.assertj.core.api.AbstractAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;


public class InternalPropertiesDaoTest {
    private static final String EMPTY_STRING = "";

    private static final String A_KEY = "a_key";

    private static final String ANOTHER_KEY = "another_key";

    private static final String VALUE_1 = "one";

    private static final String VALUE_2 = "two";

    private static final long DATE_1 = 1500000000000L;

    private static final long DATE_2 = 1600000000000L;

    private static final String VALUE_SMALL = "some small value";

    private static final String OTHER_VALUE_SMALL = "other small value";

    private static final String VALUE_SIZE_4000 = String.format("%1$4000.4000s", "*");

    private static final String VALUE_SIZE_4001 = (InternalPropertiesDaoTest.VALUE_SIZE_4000) + "P";

    private static final String OTHER_VALUE_SIZE_4001 = (InternalPropertiesDaoTest.VALUE_SIZE_4000) + "D";

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(system2);

    private DbSession dbSession = dbTester.getSession();

    private InternalPropertiesDao underTest = new InternalPropertiesDao(system2);

    @Test
    public void save_throws_IAE_if_key_is_null() {
        expectKeyNullOrEmptyIAE();
        underTest.save(dbSession, null, InternalPropertiesDaoTest.VALUE_SMALL);
    }

    @Test
    public void save_throws_IAE_if_key_is_empty() {
        expectKeyNullOrEmptyIAE();
        underTest.save(dbSession, InternalPropertiesDaoTest.EMPTY_STRING, InternalPropertiesDaoTest.VALUE_SMALL);
    }

    @Test
    public void save_throws_IAE_if_value_is_null() {
        expectValueNullOrEmptyIAE();
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, null);
    }

    @Test
    public void save_throws_IAE_if_value_is_empty() {
        expectValueNullOrEmptyIAE();
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.EMPTY_STRING);
    }

    @Test
    public void save_persists_value_in_varchar_if_less_than_4000() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_2);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SMALL);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasTextValue(InternalPropertiesDaoTest.VALUE_SMALL).hasCreatedAt(InternalPropertiesDaoTest.DATE_2);
    }

    @Test
    public void save_persists_value_in_varchar_if_4000() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_1);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SIZE_4000);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasTextValue(InternalPropertiesDaoTest.VALUE_SIZE_4000).hasCreatedAt(InternalPropertiesDaoTest.DATE_1);
    }

    @Test
    public void save_persists_value_in_varchar_if_more_than_4000() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_2);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SIZE_4001);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasClobValue(InternalPropertiesDaoTest.VALUE_SIZE_4001).hasCreatedAt(InternalPropertiesDaoTest.DATE_2);
    }

    @Test
    public void save_persists_new_value_in_varchar_if_4000_when_old_one_was_in_varchar() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_1, InternalPropertiesDaoTest.DATE_2);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SMALL);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasTextValue(InternalPropertiesDaoTest.VALUE_SMALL).hasCreatedAt(InternalPropertiesDaoTest.DATE_1);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SIZE_4000);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasTextValue(InternalPropertiesDaoTest.VALUE_SIZE_4000).hasCreatedAt(InternalPropertiesDaoTest.DATE_2);
    }

    @Test
    public void save_persists_new_value_in_clob_if_more_than_4000_when_old_one_was_in_varchar() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_1, InternalPropertiesDaoTest.DATE_2);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SMALL);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasTextValue(InternalPropertiesDaoTest.VALUE_SMALL).hasCreatedAt(InternalPropertiesDaoTest.DATE_1);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SIZE_4001);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasClobValue(InternalPropertiesDaoTest.VALUE_SIZE_4001).hasCreatedAt(InternalPropertiesDaoTest.DATE_2);
    }

    @Test
    public void save_persists_new_value_in_varchar_if_less_than_4000_when_old_one_was_in_clob() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_1, InternalPropertiesDaoTest.DATE_2);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SIZE_4001);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasClobValue(InternalPropertiesDaoTest.VALUE_SIZE_4001).hasCreatedAt(InternalPropertiesDaoTest.DATE_1);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SMALL);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasTextValue(InternalPropertiesDaoTest.VALUE_SMALL).hasCreatedAt(InternalPropertiesDaoTest.DATE_2);
    }

    @Test
    public void save_persists_new_value_in_clob_if_more_than_4000_when_old_one_was_in_clob() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_1, InternalPropertiesDaoTest.DATE_2);
        String oldValue = (InternalPropertiesDaoTest.VALUE_SIZE_4001) + "blabla";
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, oldValue);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasClobValue(oldValue).hasCreatedAt(InternalPropertiesDaoTest.DATE_1);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SIZE_4001);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasClobValue(InternalPropertiesDaoTest.VALUE_SIZE_4001).hasCreatedAt(InternalPropertiesDaoTest.DATE_2);
    }

    @Test
    public void saveAsEmpty_throws_IAE_if_key_is_null() {
        expectKeyNullOrEmptyIAE();
        underTest.saveAsEmpty(dbSession, null);
    }

    @Test
    public void saveAsEmpty_throws_IAE_if_key_is_empty() {
        expectKeyNullOrEmptyIAE();
        underTest.saveAsEmpty(dbSession, InternalPropertiesDaoTest.EMPTY_STRING);
    }

    @Test
    public void saveAsEmpty_persist_property_without_textvalue_nor_clob_value() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_2);
        underTest.saveAsEmpty(dbSession, InternalPropertiesDaoTest.A_KEY);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).isEmpty().hasCreatedAt(InternalPropertiesDaoTest.DATE_2);
    }

    @Test
    public void saveAsEmpty_persist_property_without_textvalue_nor_clob_value_when_old_value_was_in_varchar() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_1, InternalPropertiesDaoTest.DATE_2);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SMALL);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasTextValue(InternalPropertiesDaoTest.VALUE_SMALL).hasCreatedAt(InternalPropertiesDaoTest.DATE_1);
        underTest.saveAsEmpty(dbSession, InternalPropertiesDaoTest.A_KEY);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).isEmpty().hasCreatedAt(InternalPropertiesDaoTest.DATE_2);
    }

    @Test
    public void saveAsEmpty_persist_property_without_textvalue_nor_clob_value_when_old_value_was_in_clob() {
        Mockito.when(system2.now()).thenReturn(InternalPropertiesDaoTest.DATE_2, InternalPropertiesDaoTest.DATE_1);
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SIZE_4001);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).hasClobValue(InternalPropertiesDaoTest.VALUE_SIZE_4001).hasCreatedAt(InternalPropertiesDaoTest.DATE_2);
        underTest.saveAsEmpty(dbSession, InternalPropertiesDaoTest.A_KEY);
        assertThatInternalProperty(InternalPropertiesDaoTest.A_KEY).isEmpty().hasCreatedAt(InternalPropertiesDaoTest.DATE_1);
    }

    @Test
    public void selectByKey_throws_IAE_when_key_is_null() {
        expectKeyNullOrEmptyIAE();
        underTest.selectByKey(dbSession, null);
    }

    @Test
    public void selectByKey_throws_IAE_when_key_is_empty() {
        expectKeyNullOrEmptyIAE();
        underTest.selectByKey(dbSession, InternalPropertiesDaoTest.EMPTY_STRING);
    }

    @Test
    public void selectByKey_returns_empty_optional_when_property_does_not_exist_in_DB() {
        assertThat(underTest.selectByKey(dbSession, InternalPropertiesDaoTest.A_KEY)).isEmpty();
    }

    @Test
    public void selectByKey_returns_empty_string_when_property_is_empty_in_DB() {
        underTest.saveAsEmpty(dbSession, InternalPropertiesDaoTest.A_KEY);
        assertThat(underTest.selectByKey(dbSession, InternalPropertiesDaoTest.A_KEY)).contains(InternalPropertiesDaoTest.EMPTY_STRING);
    }

    @Test
    public void selectByKey_returns_value_when_property_has_value_stored_in_varchar() {
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SMALL);
        assertThat(underTest.selectByKey(dbSession, InternalPropertiesDaoTest.A_KEY)).contains(InternalPropertiesDaoTest.VALUE_SMALL);
    }

    @Test
    public void selectByKey_returns_value_when_property_has_value_stored_in_clob() {
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SIZE_4001);
        assertThat(underTest.selectByKey(dbSession, InternalPropertiesDaoTest.A_KEY)).contains(InternalPropertiesDaoTest.VALUE_SIZE_4001);
    }

    @Test
    public void selectByKeys_returns_empty_map_if_keys_is_null() {
        assertThat(underTest.selectByKeys(dbSession, null)).isEmpty();
    }

    @Test
    public void selectByKeys_returns_empty_map_if_keys_is_empty() {
        assertThat(underTest.selectByKeys(dbSession, Collections.emptySet())).isEmpty();
    }

    @Test
    public void selectByKeys_throws_IAE_when_keys_contains_null() {
        Random random = new Random();
        Set<String> keysIncludingANull = Stream.of(IntStream.range(0, random.nextInt(10)).mapToObj(( i) -> "b_" + i), Stream.of(((String) (null))), IntStream.range(0, random.nextInt(10)).mapToObj(( i) -> "a_" + i)).flatMap(( s) -> s).collect(Collectors.toSet());
        expectKeyNullOrEmptyIAE();
        underTest.selectByKeys(dbSession, keysIncludingANull);
    }

    @Test
    public void selectByKeys_throws_IAE_when_keys_contains_empty_string() {
        Random random = new Random();
        Set<String> keysIncludingAnEmptyString = Stream.of(IntStream.range(0, random.nextInt(10)).mapToObj(( i) -> "b_" + i), Stream.of(""), IntStream.range(0, random.nextInt(10)).mapToObj(( i) -> "a_" + i)).flatMap(( s) -> s).collect(Collectors.toSet());
        expectKeyNullOrEmptyIAE();
        underTest.selectByKeys(dbSession, keysIncludingAnEmptyString);
    }

    @Test
    public void selectByKeys_returns_empty_optional_when_property_does_not_exist_in_DB() {
        assertThat(underTest.selectByKeys(dbSession, ImmutableSet.of(InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.ANOTHER_KEY))).containsOnly(entry(InternalPropertiesDaoTest.A_KEY, Optional.empty()), entry(InternalPropertiesDaoTest.ANOTHER_KEY, Optional.empty()));
    }

    @Test
    public void selectByKeys_returns_empty_string_when_property_is_empty_in_DB() {
        underTest.saveAsEmpty(dbSession, InternalPropertiesDaoTest.A_KEY);
        underTest.saveAsEmpty(dbSession, InternalPropertiesDaoTest.ANOTHER_KEY);
        assertThat(underTest.selectByKeys(dbSession, ImmutableSet.of(InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.ANOTHER_KEY))).containsOnly(entry(InternalPropertiesDaoTest.A_KEY, Optional.of("")), entry(InternalPropertiesDaoTest.ANOTHER_KEY, Optional.of("")));
    }

    @Test
    public void selectByKeys_returns_value_when_property_has_value_stored_in_varchar() {
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SMALL);
        underTest.save(dbSession, InternalPropertiesDaoTest.ANOTHER_KEY, InternalPropertiesDaoTest.OTHER_VALUE_SMALL);
        assertThat(underTest.selectByKeys(dbSession, ImmutableSet.of(InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.ANOTHER_KEY))).containsOnly(entry(InternalPropertiesDaoTest.A_KEY, Optional.of(InternalPropertiesDaoTest.VALUE_SMALL)), entry(InternalPropertiesDaoTest.ANOTHER_KEY, Optional.of(InternalPropertiesDaoTest.OTHER_VALUE_SMALL)));
    }

    @Test
    public void selectByKeys_returns_values_when_properties_has_value_stored_in_clob() {
        underTest.save(dbSession, InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.VALUE_SIZE_4001);
        underTest.save(dbSession, InternalPropertiesDaoTest.ANOTHER_KEY, InternalPropertiesDaoTest.OTHER_VALUE_SIZE_4001);
        assertThat(underTest.selectByKeys(dbSession, ImmutableSet.of(InternalPropertiesDaoTest.A_KEY, InternalPropertiesDaoTest.ANOTHER_KEY))).containsOnly(entry(InternalPropertiesDaoTest.A_KEY, Optional.of(InternalPropertiesDaoTest.VALUE_SIZE_4001)), entry(InternalPropertiesDaoTest.ANOTHER_KEY, Optional.of(InternalPropertiesDaoTest.OTHER_VALUE_SIZE_4001)));
    }

    @Test
    public void selectByKeys_queries_only_clob_properties_with_clob_SQL_query() {
        underTest.saveAsEmpty(dbSession, InternalPropertiesDaoTest.A_KEY);
        underTest.save(dbSession, "key2", InternalPropertiesDaoTest.VALUE_SMALL);
        underTest.save(dbSession, "key3", InternalPropertiesDaoTest.VALUE_SIZE_4001);
        Set<String> keys = ImmutableSet.of(InternalPropertiesDaoTest.A_KEY, "key2", "key3", "non_existent_key");
        List<InternalPropertyDto> allInternalPropertyDtos = dbSession.getMapper(InternalPropertiesMapper.class).selectAsText(ImmutableList.copyOf(keys));
        List<InternalPropertyDto> clobPropertyDtos = dbSession.getMapper(InternalPropertiesMapper.class).selectAsClob(ImmutableList.of("key3"));
        InternalPropertiesMapper mapperMock = Mockito.mock(InternalPropertiesMapper.class);
        DbSession dbSessionMock = Mockito.mock(DbSession.class);
        Mockito.when(dbSessionMock.getMapper(InternalPropertiesMapper.class)).thenReturn(mapperMock);
        Mockito.when(mapperMock.selectAsText(ImmutableList.copyOf(keys))).thenReturn(allInternalPropertyDtos);
        Mockito.when(mapperMock.selectAsClob(ImmutableList.of("key3"))).thenReturn(clobPropertyDtos);
        underTest.selectByKeys(dbSessionMock, keys);
        Mockito.verify(mapperMock).selectAsText(ImmutableList.copyOf(keys));
        Mockito.verify(mapperMock).selectAsClob(ImmutableList.of("key3"));
        Mockito.verifyNoMoreInteractions(mapperMock);
    }

    private static class InternalPropertyAssert extends AbstractAssert<InternalPropertiesDaoTest.InternalPropertyAssert, InternalPropertiesDaoTest.InternalProperty> {
        private InternalPropertyAssert(DbTester dbTester, DbSession dbSession, String internalPropertyKey) {
            super(InternalPropertiesDaoTest.InternalPropertyAssert.asInternalProperty(dbTester, dbSession, internalPropertyKey), InternalPropertiesDaoTest.InternalPropertyAssert.class);
        }

        private static InternalPropertiesDaoTest.InternalProperty asInternalProperty(DbTester dbTester, DbSession dbSession, String internalPropertyKey) {
            Map<String, Object> row = dbTester.selectFirst(dbSession, ((("select" + ((" is_empty as \"isEmpty\", text_value as \"textValue\", clob_value as \"clobValue\", created_at as \"createdAt\"" + " from internal_properties") + " where kee='")) + internalPropertyKey) + "'"));
            return new InternalPropertiesDaoTest.InternalProperty(InternalPropertiesDaoTest.InternalPropertyAssert.isEmpty(row), ((String) (row.get("textValue"))), ((String) (row.get("clobValue"))), ((Long) (row.get("createdAt"))));
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

        public void doesNotExist() {
            isNull();
        }

        public InternalPropertiesDaoTest.InternalPropertyAssert isEmpty() {
            isNotNull();
            if (!(Objects.equals(actual.isEmpty(), Boolean.TRUE))) {
                failWithMessage("Expected Internal property to have column IS_EMPTY to be <%s> but was <%s>", true, actual.isEmpty());
            }
            if ((actual.getTextValue()) != null) {
                failWithMessage("Expected Internal property to have column TEXT_VALUE to be null but was <%s>", actual.getTextValue());
            }
            if ((actual.getClobValue()) != null) {
                failWithMessage("Expected Internal property to have column CLOB_VALUE to be null but was <%s>", actual.getClobValue());
            }
            return this;
        }

        public InternalPropertiesDaoTest.InternalPropertyAssert hasTextValue(String expected) {
            isNotNull();
            if (!(Objects.equals(actual.getTextValue(), expected))) {
                failWithMessage("Expected Internal property to have column TEXT_VALUE to be <%s> but was <%s>", true, actual.getTextValue());
            }
            if ((actual.getClobValue()) != null) {
                failWithMessage("Expected Internal property to have column CLOB_VALUE to be null but was <%s>", actual.getClobValue());
            }
            if (!(Objects.equals(actual.isEmpty(), Boolean.FALSE))) {
                failWithMessage("Expected Internal property to have column IS_EMPTY to be <%s> but was <%s>", false, actual.isEmpty());
            }
            return this;
        }

        public InternalPropertiesDaoTest.InternalPropertyAssert hasClobValue(String expected) {
            isNotNull();
            if (!(Objects.equals(actual.getClobValue(), expected))) {
                failWithMessage("Expected Internal property to have column CLOB_VALUE to be <%s> but was <%s>", true, actual.getClobValue());
            }
            if ((actual.getTextValue()) != null) {
                failWithMessage("Expected Internal property to have column TEXT_VALUE to be null but was <%s>", actual.getTextValue());
            }
            if (!(Objects.equals(actual.isEmpty(), Boolean.FALSE))) {
                failWithMessage("Expected Internal property to have column IS_EMPTY to be <%s> but was <%s>", false, actual.isEmpty());
            }
            return this;
        }

        public InternalPropertiesDaoTest.InternalPropertyAssert hasCreatedAt(long expected) {
            isNotNull();
            if (!(Objects.equals(actual.getCreatedAt(), expected))) {
                failWithMessage("Expected Internal property to have column CREATED_AT to be <%s> but was <%s>", expected, actual.getCreatedAt());
            }
            return this;
        }
    }

    private static final class InternalProperty {
        private final Boolean empty;

        private final String textValue;

        private final String clobValue;

        private final Long createdAt;

        public InternalProperty(@Nullable
        Boolean empty, @Nullable
        String textValue, @Nullable
        String clobValue, @Nullable
        Long createdAt) {
            this.empty = empty;
            this.textValue = textValue;
            this.clobValue = clobValue;
            this.createdAt = createdAt;
        }

        @CheckForNull
        public Boolean isEmpty() {
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

