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
package org.sonar.api.server.profile;


import BuiltInQualityProfilesDefinition.Context;
import Severity.BLOCKER;
import org.junit.Test;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.NewBuiltInQualityProfile;


public class BuiltInQualityProfileAnnotationLoaderTest {
    @Test
    public void shouldParseAnnotatedClasses() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        NewBuiltInQualityProfile newProfile = context.createBuiltInQualityProfile("Foo way", "java");
        new BuiltInQualityProfileAnnotationLoader().load(newProfile, "squid", FakeRule.class, RuleNoProfile.class);
        newProfile.done();
        assertThat(context.profile("java", "Foo way").rule(RuleKey.of("squid", "fake")).overriddenSeverity()).isEqualTo(BLOCKER);
    }

    @Test
    public void shouldParseOnlyWantedProfile() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        NewBuiltInQualityProfile newProfile = context.createBuiltInQualityProfile("Foo way", "java");
        new BuiltInQualityProfileAnnotationLoader().load(newProfile, "squid", FakeRule.class, RuleOnOtherProfile.class, RuleNoProfile.class);
        newProfile.done();
        assertThat(context.profile("java", "Foo way").rule(RuleKey.of("squid", "fake"))).isNotNull();
        assertThat(context.profile("java", "Foo way").rule(RuleKey.of("squid", "other"))).isNull();
    }
}

