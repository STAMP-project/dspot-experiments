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
package org.sonar.test;


import java.io.File;
import java.net.URL;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JsonAssertTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void isSimilarAs_strings() {
        JsonAssert.assertJson("{}").isSimilarTo("{}");
        try {
            JsonAssert.assertJson("{}").isSimilarTo("[]");
            Assert.fail();
        } catch (ComparisonFailure error) {
            assertThat(error.getMessage()).isEqualTo("Not a super-set of expected JSON - expected:<[[]]> but was:<[{}]>");
            assertThat(error.getActual()).isEqualTo("{}");
            assertThat(error.getExpected()).isEqualTo("[]");
        }
    }

    @Test
    public void isSimilarAs_urls() {
        URL url1 = getClass().getResource("JsonAssertTest/sample1.json");
        URL url2 = getClass().getResource("JsonAssertTest/sample2.json");
        JsonAssert.assertJson(url1).isSimilarTo(url1);
        expectedException.expect(AssertionError.class);
        JsonAssert.assertJson(url1).isSimilarTo(url2);
    }

    @Test
    public void actual_can_be_superset_of_expected() {
        JsonAssert.assertJson("{\"foo\": \"bar\"}").isSimilarTo("{}");
        expectedException.expect(AssertionError.class);
        JsonAssert.assertJson("{}").isSimilarTo("{\"foo\": \"bar\"}");
    }

    @Test
    public void fail_to_load_url() throws Exception {
        expectedException.expect(IllegalStateException.class);
        JsonAssert.assertJson(new File("target/missing").toURI().toURL());
    }

    @Test
    public void enable_strict_order_of_arrays() {
        expectedException.expect(AssertionError.class);
        JsonAssert.assertJson("[1,2]").withStrictArrayOrder().isSimilarTo("[2, 1]");
    }

    @Test
    public void enable_strict_timezone() {
        expectedException.expect(AssertionError.class);
        JsonAssert.assertJson("[\"2010-05-18T15:50:45+0100\"]").withStrictTimezone().isSimilarTo("[\"2010-05-18T16:50:45+0200\"]");
    }

    @Test
    public void ignore_fields() {
        JsonAssert.assertJson("{\"foo\": \"bar\"}").ignoreFields("ignore-me").isSimilarTo("{\"foo\": \"bar\", \"ignore-me\": \"value\"}");
    }
}

