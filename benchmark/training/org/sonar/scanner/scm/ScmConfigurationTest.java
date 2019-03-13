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
package org.sonar.scanner.scm;


import CoreProperties.SCM_DISABLED_KEY;
import CoreProperties.SCM_EXCLUSIONS_DISABLED_KEY;
import ScannerProperties.LINKS_SOURCES_DEV;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Optional;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.batch.AnalysisMode;
import org.sonar.api.batch.fs.internal.InputModuleHierarchy;
import org.sonar.api.batch.scm.ScmProvider;
import org.sonar.api.config.Configuration;
import org.sonar.api.notifications.AnalysisWarnings;
import org.sonar.api.utils.MessageException;
import org.sonar.api.utils.log.LogTester;


@RunWith(DataProviderRunner.class)
public class ScmConfigurationTest {
    private final InputModuleHierarchy inputModuleHierarchy = Mockito.mock(InputModuleHierarchy.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_MOCKS));

    private final AnalysisMode analysisMode = Mockito.mock(AnalysisMode.class);

    private final AnalysisWarnings analysisWarnings = Mockito.mock(AnalysisWarnings.class);

    private final Configuration settings = Mockito.mock(Configuration.class);

    private final String scmProviderKey = "dummyScmProviderKey";

    private final ScmProvider scmProvider = Mockito.mock(ScmProvider.class);

    private final ScmConfiguration underTest;

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public ScmConfigurationTest() {
        Mockito.when(analysisMode.isIssues()).thenReturn(false);
        Mockito.when(scmProvider.key()).thenReturn(scmProviderKey);
        underTest = new ScmConfiguration(inputModuleHierarchy, analysisMode, settings, analysisWarnings, scmProvider);
    }

    @Test
    public void do_not_register_warning_when_success_to_autodetect_scm_provider() {
        Mockito.when(scmProvider.supports(ArgumentMatchers.any())).thenReturn(true);
        underTest.start();
        assertThat(underTest.provider()).isNotNull();
        Mockito.verifyZeroInteractions(analysisWarnings);
    }

    @Test
    public void register_warning_when_fail_to_detect_scm_provider() {
        underTest.start();
        assertThat(underTest.provider()).isNull();
        Mockito.verify(analysisWarnings).addUnique(ArgumentMatchers.anyString());
    }

    @Test
    public void log_when_disabled() {
        Mockito.when(settings.getBoolean(SCM_DISABLED_KEY)).thenReturn(Optional.of(true));
        underTest.start();
        assertThat(logTester.logs()).contains(ScmConfiguration.MESSAGE_SCM_STEP_IS_DISABLED_BY_CONFIGURATION);
    }

    @Test
    public void log_when_exclusion_is_disabled() {
        Mockito.when(settings.getBoolean(SCM_EXCLUSIONS_DISABLED_KEY)).thenReturn(Optional.of(true));
        underTest.start();
        assertThat(logTester.logs()).contains(ScmConfiguration.MESSAGE_SCM_EXLUSIONS_IS_DISABLED_BY_CONFIGURATION);
    }

    @Test
    public void return_early_from_start_in_issues_mode() {
        // return early = doesn't reach the logging when disabled
        Mockito.when(settings.getBoolean(SCM_DISABLED_KEY)).thenReturn(Optional.of(true));
        Mockito.when(analysisMode.isIssues()).thenReturn(true);
        underTest.start();
        assertThat(logTester.logs()).isEmpty();
    }

    @Test
    public void fail_when_multiple_scm_providers_claim_support() {
        Mockito.when(scmProvider.supports(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(scmProvider.key()).thenReturn("key1", "key2");
        ScmProvider[] providers = new ScmProvider[]{ scmProvider, scmProvider };
        ScmConfiguration underTest = new ScmConfiguration(inputModuleHierarchy, analysisMode, settings, analysisWarnings, providers);
        thrown.expect(MessageException.class);
        thrown.expectMessage(new BaseMatcher<String>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            public boolean matches(Object item) {
                return ((String) (item)).matches(("SCM provider autodetection failed. " + ("Both .* and .* claim to support this project. " + "Please use \"sonar.scm.provider\" to define SCM of your project.")));
            }
        });
        underTest.start();
    }

    @Test
    public void fail_when_considerOldScmUrl_finds_invalid_provider_in_link() {
        Mockito.when(settings.get(LINKS_SOURCES_DEV)).thenReturn(Optional.of("scm:invalid"));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("no SCM provider found for this key");
        underTest.start();
    }

    @Test
    public void set_provider_from_valid_link() {
        Mockito.when(settings.get(LINKS_SOURCES_DEV)).thenReturn(Optional.of(("scm:" + (scmProviderKey))));
        underTest.start();
        assertThat(underTest.provider()).isSameAs(scmProvider);
    }
}

