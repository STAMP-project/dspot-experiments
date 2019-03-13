/**
 * Copyright ? 2010-2017 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsonschema2pojo.util;


import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class LanguageFeaturesTest {
    public static enum VersionEnum {

        BEFORE_6(false, false, false),
        MAX_6(true, false, false),
        MAX_7(true, true, false),
        MAX_8(true, true, true),
        AFTER_8(true, true, true);
        public final boolean canUse6;

        public final boolean canUse7;

        public final boolean canUse8;

        VersionEnum(boolean canUse6, boolean canUse7, boolean canUse8) {
            this.canUse6 = canUse6;
            this.canUse7 = canUse7;
            this.canUse8 = canUse8;
        }
    }

    private String version;

    private LanguageFeaturesTest.VersionEnum versionSpec;

    public LanguageFeaturesTest(String version, LanguageFeaturesTest.VersionEnum versionSpec) {
        this.version = version;
        this.versionSpec = versionSpec;
    }

    @Test
    public void correctTestForJava7() {
        MatcherAssert.assertThat(LanguageFeatures.canUseJava7(LanguageFeaturesTest.mockConfig(version)), equalTo(versionSpec.canUse7));
    }

    @Test
    public void correctTestForJava8() {
        MatcherAssert.assertThat(LanguageFeatures.canUseJava8(LanguageFeaturesTest.mockConfig(version)), equalTo(versionSpec.canUse8));
    }
}

