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
package org.sonar.server.qualityprofile;


import java.util.Date;
import org.junit.Test;
import org.sonar.api.utils.DateUtils;


public class QualityProfileTest {
    private static final String SOME_QP_KEY = "qpKey";

    private static final String SOME_QP_NAME = "qpName";

    private static final String SOME_LANGUAGE_KEY = "languageKey";

    private static final Date SOME_DATE = DateUtils.parseDateTimeQuietly("2010-05-18T15:50:45+0100");

    private static final QualityProfile QUALITY_PROFILE = new QualityProfile(QualityProfileTest.SOME_QP_KEY, QualityProfileTest.SOME_QP_NAME, QualityProfileTest.SOME_LANGUAGE_KEY, QualityProfileTest.SOME_DATE);

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_if_qkKey_arg_is_null() {
        new QualityProfile(null, QualityProfileTest.SOME_QP_NAME, QualityProfileTest.SOME_LANGUAGE_KEY, QualityProfileTest.SOME_DATE);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_if_qpName_arg_is_null() {
        new QualityProfile(QualityProfileTest.SOME_QP_KEY, null, QualityProfileTest.SOME_LANGUAGE_KEY, QualityProfileTest.SOME_DATE);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_if_languageKey_arg_is_null() {
        new QualityProfile(QualityProfileTest.SOME_QP_KEY, QualityProfileTest.SOME_QP_NAME, null, QualityProfileTest.SOME_DATE);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_if_rulesUpdatedAt_arg_is_null() {
        new QualityProfile(QualityProfileTest.SOME_QP_KEY, QualityProfileTest.SOME_QP_NAME, QualityProfileTest.SOME_LANGUAGE_KEY, null);
    }

    @Test
    public void verify_properties() {
        assertThat(QualityProfileTest.QUALITY_PROFILE.getQpKey()).isEqualTo(QualityProfileTest.SOME_QP_KEY);
        assertThat(QualityProfileTest.QUALITY_PROFILE.getQpName()).isEqualTo(QualityProfileTest.SOME_QP_NAME);
        assertThat(QualityProfileTest.QUALITY_PROFILE.getLanguageKey()).isEqualTo(QualityProfileTest.SOME_LANGUAGE_KEY);
        assertThat(QualityProfileTest.QUALITY_PROFILE.getRulesUpdatedAt()).isEqualTo(QualityProfileTest.SOME_DATE);
    }

    @Test
    public void verify_getRulesUpdatedAt_keeps_object_immutable() {
        assertThat(QualityProfileTest.QUALITY_PROFILE.getRulesUpdatedAt()).isNotSameAs(QualityProfileTest.SOME_DATE);
    }

    @Test
    public void verify_equals() {
        assertThat(QualityProfileTest.QUALITY_PROFILE).isEqualTo(new QualityProfile(QualityProfileTest.SOME_QP_KEY, QualityProfileTest.SOME_QP_NAME, QualityProfileTest.SOME_LANGUAGE_KEY, QualityProfileTest.SOME_DATE));
        assertThat(QualityProfileTest.QUALITY_PROFILE).isEqualTo(QualityProfileTest.QUALITY_PROFILE);
        assertThat(QualityProfileTest.QUALITY_PROFILE).isNotEqualTo(null);
    }

    @Test
    public void verify_toString() {
        assertThat(QualityProfileTest.QUALITY_PROFILE.toString()).isEqualTo("QualityProfile{key=qpKey, name=qpName, language=languageKey, rulesUpdatedAt=1274194245000}");
    }
}

