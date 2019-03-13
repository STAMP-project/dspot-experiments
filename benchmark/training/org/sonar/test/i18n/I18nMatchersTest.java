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
package org.sonar.test.i18n;


import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class I18nMatchersTest {
    @Test
    public void testBundlesInsideSonarPlugin() {
        // synchronized bundle
        Assert.assertThat("myPlugin_fr_CA.properties", I18nMatchers.isBundleUpToDate());
        Assert.assertFalse(new File("target/l10n/myPlugin_fr_CA.properties.report.txt").exists());
        // missing keys
        try {
            Assert.assertThat("myPlugin_fr.properties", I18nMatchers.isBundleUpToDate());
            Assert.assertTrue(new File("target/l10n/myPlugin_fr.properties.report.txt").exists());
        } catch (AssertionError e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Missing translations are:\nsecond.prop"));
        }
    }

    @Test
    public void shouldNotFailIfNoMissingKeysButAdditionalKeys() {
        Assert.assertThat("noMissingKeys_fr.properties", I18nMatchers.isBundleUpToDate());
    }
}

