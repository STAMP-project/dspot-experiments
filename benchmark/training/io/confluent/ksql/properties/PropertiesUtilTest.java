/**
 * Copyright 2019 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.properties;


import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.util.KsqlException;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class PropertiesUtilTest {
    @ClassRule
    public static final TemporaryFolder TMP = new TemporaryFolder();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private File propsFile;

    @Test
    public void shouldLoadPropsFromFile() {
        // Given:
        givenPropsFileContains(((((("# Comment" + (System.lineSeparator())) + "some.prop=some value") + (System.lineSeparator())) + "some.other.prop=124") + (System.lineSeparator())));
        // When:
        final Map<String, String> result = PropertiesUtil.loadProperties(propsFile);
        // Then:
        MatcherAssert.assertThat(result.get("some.prop"), Matchers.is("some value"));
        MatcherAssert.assertThat(result.get("some.other.prop"), Matchers.is("124"));
    }

    @Test
    public void shouldLoadImmutablePropsFromFile() {
        // Given:
        givenPropsFileContains(("some.prop=some value" + (System.lineSeparator())));
        final Map<String, String> result = PropertiesUtil.loadProperties(propsFile);
        // Then:
        expectedException.expect(UnsupportedOperationException.class);
        // When:
        result.put("new", "value");
    }

    @Test
    public void shouldThrowIfFailedToLoadProps() {
        // Given:
        propsFile = new File("i_do_not_exist");
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Failed to load properties file: i_do_not_exist");
        // When:
        PropertiesUtil.loadProperties(propsFile);
    }

    @Test
    public void shouldThrowIfPropsFileContainsBlackListedProps() {
        // Given:
        givenPropsFileContains((("java.some.disallowed.setting=something" + (System.lineSeparator())) + "java.not.another.one=v"));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Property file contains the following blacklisted properties");
        expectedException.expectMessage("java.some.disallowed.setting");
        expectedException.expectMessage("java.not.another.one");
        // When:
        PropertiesUtil.loadProperties(propsFile);
    }

    @Test
    public void shouldApplyOverrides() {
        // Given:
        final Map<String, String> initial = ImmutableMap.of("should.be.overridden", "initial value", "should.be.not.overridden", "initial value");
        final Properties overrides = PropertiesUtilTest.properties("should.be.overridden", "new value", "additional.override", "value");
        // When:
        final Map<String, ?> result = PropertiesUtil.applyOverrides(initial, overrides);
        // Then:
        MatcherAssert.assertThat(result.get("should.be.overridden"), Matchers.is("new value"));
        MatcherAssert.assertThat(result.get("should.be.not.overridden"), Matchers.is("initial value"));
        MatcherAssert.assertThat(result.get("additional.override"), Matchers.is("value"));
    }

    @Test
    public void shouldFilterBlackListedFromOverrides() {
        Stream.of("java.", "os.", "sun.", "user.", "line.separator", "path.separator", "file.separator").forEach(( blackListed) -> {
            // Given:
            final Properties overrides = PropertiesUtilTest.properties((blackListed + "props.should.be.filtered"), "unexpected", "should.not.be.filtered", "value");
            // When:
            final Map<String, ?> result = PropertiesUtil.applyOverrides(Collections.emptyMap(), overrides);
            // Then:
            MatcherAssert.assertThat(result.keySet(), Matchers.hasItem("should.not.be.filtered"));
            MatcherAssert.assertThat(result.keySet(), Matchers.not(Matchers.hasItem("props.should.be.filtered")));
        });
    }

    @Test
    public void shouldFilterByKey() {
        // Given:
        final Map<String, String> props = ImmutableMap.of("keep.this", "v0", "keep that", "v1", "do not keep this", "keep");
        // When:
        final Map<String, String> result = PropertiesUtil.filterByKey(props, ( key) -> key.startsWith("keep"));
        // Then:
        MatcherAssert.assertThat(result.keySet(), Matchers.containsInAnyOrder("keep.this", "keep that"));
        MatcherAssert.assertThat(result.get("keep.this"), Matchers.is("v0"));
        MatcherAssert.assertThat(result.get("keep that"), Matchers.is("v1"));
    }
}

