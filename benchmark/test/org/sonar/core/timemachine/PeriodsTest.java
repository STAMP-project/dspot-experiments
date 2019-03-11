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
package org.sonar.core.timemachine;


import java.util.Date;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.i18n.I18n;
import org.sonar.core.config.CorePropertyDefinitions;


public class PeriodsTest {
    static String NUMBER_OF_DAYS = "5";

    static String STRING_DATE = "2015-01-01";

    static Date DATE = parseDate(PeriodsTest.STRING_DATE);

    static String VERSION = "1.1";

    static int PERIOD_INDEX = 1;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    MapSettings settings = new MapSettings();

    I18n i18n = Mockito.mock(I18n.class);

    Periods periods = new Periods(settings.asConfig(), i18n);

    @Test
    public void return_over_x_days_label_when_no_date() {
        periods.label(CorePropertyDefinitions.LEAK_PERIOD_MODE_DAYS, PeriodsTest.NUMBER_OF_DAYS, ((String) (null)));
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("over_x_days"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.NUMBER_OF_DAYS));
    }

    @Test
    public void return_over_x_days_abbreviation_when_no_date() {
        periods.abbreviation(CorePropertyDefinitions.LEAK_PERIOD_MODE_DAYS, PeriodsTest.NUMBER_OF_DAYS, null);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("over_x_days.short"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.NUMBER_OF_DAYS));
    }

    @Test
    public void return_over_x_days_detailed_label_when_date_is_set() {
        periods.label(CorePropertyDefinitions.LEAK_PERIOD_MODE_DAYS, PeriodsTest.NUMBER_OF_DAYS, PeriodsTest.STRING_DATE);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("over_x_days_detailed"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.NUMBER_OF_DAYS), ArgumentMatchers.eq(PeriodsTest.STRING_DATE));
    }

    @Test
    public void return_over_x_days_detailed_abbreviation_when_date_is_set() {
        periods.abbreviation(CorePropertyDefinitions.LEAK_PERIOD_MODE_DAYS, PeriodsTest.NUMBER_OF_DAYS, PeriodsTest.DATE);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("over_x_days_detailed.short"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.NUMBER_OF_DAYS), ArgumentMatchers.anyString());
    }

    @Test
    public void return_over_x_days_label_using_settings() {
        settings.setProperty(((CorePropertyDefinitions.LEAK_PERIOD) + (PeriodsTest.PERIOD_INDEX)), PeriodsTest.NUMBER_OF_DAYS);
        periods.label(PeriodsTest.PERIOD_INDEX);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("over_x_days"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.NUMBER_OF_DAYS));
    }

    @Test
    public void return_since_version_label_when_no_date() {
        periods.label(CorePropertyDefinitions.LEAK_PERIOD_MODE_VERSION, PeriodsTest.VERSION, ((String) (null)));
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_version"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.VERSION));
    }

    @Test
    public void return_since_version_abbreviation_when_no_date() {
        periods.abbreviation(CorePropertyDefinitions.LEAK_PERIOD_MODE_VERSION, PeriodsTest.VERSION, null);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_version.short"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.VERSION));
    }

    @Test
    public void return_since_version_detailed_label_when_date_is_set() {
        periods.label(CorePropertyDefinitions.LEAK_PERIOD_MODE_VERSION, PeriodsTest.VERSION, PeriodsTest.STRING_DATE);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_version_detailed"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.VERSION), ArgumentMatchers.eq(PeriodsTest.STRING_DATE));
    }

    @Test
    public void return_since_version_detailed_abbreviation_when_date_is_set() {
        periods.abbreviation(CorePropertyDefinitions.LEAK_PERIOD_MODE_VERSION, PeriodsTest.VERSION, PeriodsTest.DATE);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_version_detailed.short"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.VERSION), ArgumentMatchers.anyString());
    }

    @Test
    public void return_since_version_label_using_settings() {
        settings.setProperty(((CorePropertyDefinitions.LEAK_PERIOD) + (PeriodsTest.PERIOD_INDEX)), PeriodsTest.VERSION);
        periods.label(PeriodsTest.PERIOD_INDEX);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_version"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.VERSION));
    }

    @Test
    public void return_since_previous_version_label_when_no_param() {
        periods.label(CorePropertyDefinitions.LEAK_PERIOD_MODE_PREVIOUS_VERSION, null, ((String) (null)));
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_previous_version"), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void return_since_previous_version_abbreviation_when_no_param() {
        periods.abbreviation(CorePropertyDefinitions.LEAK_PERIOD_MODE_PREVIOUS_VERSION, null, null);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_previous_version.short"), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void return_since_previous_version_detailed_label_when_param_is_set_and_no_date() {
        periods.label(CorePropertyDefinitions.LEAK_PERIOD_MODE_PREVIOUS_VERSION, PeriodsTest.VERSION, ((String) (null)));
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_previous_version_detailed"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.VERSION));
    }

    @Test
    public void return_since_previous_version_detailed_abbreviation_when_param_is_set_and_no_date() {
        periods.abbreviation(CorePropertyDefinitions.LEAK_PERIOD_MODE_PREVIOUS_VERSION, PeriodsTest.VERSION, null);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_previous_version_detailed.short"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.VERSION));
    }

    @Test
    public void return_since_previous_version_detailed_label_when_param_and_date_are_set() {
        periods.label(CorePropertyDefinitions.LEAK_PERIOD_MODE_PREVIOUS_VERSION, PeriodsTest.VERSION, PeriodsTest.STRING_DATE);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_previous_version_detailed"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.VERSION), ArgumentMatchers.eq(PeriodsTest.STRING_DATE));
    }

    @Test
    public void return_since_previous_version_with_only_date_label_when_no_param_and_date_is_set() {
        periods.label(CorePropertyDefinitions.LEAK_PERIOD_MODE_PREVIOUS_VERSION, null, PeriodsTest.STRING_DATE);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_previous_version_with_only_date"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.STRING_DATE));
    }

    @Test
    public void return_since_previous_version_detailed_abbreviation_when_param_and_date_are_set() {
        periods.abbreviation(CorePropertyDefinitions.LEAK_PERIOD_MODE_PREVIOUS_VERSION, PeriodsTest.VERSION, PeriodsTest.DATE);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_previous_version_detailed.short"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.VERSION), ArgumentMatchers.anyString());
    }

    @Test
    public void return_since_previous_version_label_using_settings() {
        settings.setProperty(((CorePropertyDefinitions.LEAK_PERIOD) + (PeriodsTest.PERIOD_INDEX)), CorePropertyDefinitions.LEAK_PERIOD_MODE_PREVIOUS_VERSION);
        periods.label(PeriodsTest.PERIOD_INDEX);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_previous_version"), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void return_since_x_label() {
        periods.label(CorePropertyDefinitions.LEAK_PERIOD_MODE_DATE, null, PeriodsTest.STRING_DATE);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_x"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.STRING_DATE));
    }

    @Test
    public void return_since_x_label_using_settings() {
        settings.setProperty(((CorePropertyDefinitions.LEAK_PERIOD) + (PeriodsTest.PERIOD_INDEX)), PeriodsTest.STRING_DATE);
        periods.label(PeriodsTest.PERIOD_INDEX);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_x"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.anyString());
    }

    @Test
    public void return_since_x_abbreviation() {
        periods.abbreviation(CorePropertyDefinitions.LEAK_PERIOD_MODE_DATE, null, PeriodsTest.DATE);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("since_x.short"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.anyString());
    }

    @Test
    public void throw_IAE_when_mode_is_unknown() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("This mode is not supported : unknown");
        periods.label("unknown", null, ((String) (null)));
    }

    @Test
    public void return_abbreviation_using_settings() {
        settings.setProperty(((CorePropertyDefinitions.LEAK_PERIOD) + (PeriodsTest.PERIOD_INDEX)), PeriodsTest.NUMBER_OF_DAYS);
        periods.abbreviation(PeriodsTest.PERIOD_INDEX);
        Mockito.verify(i18n).message(ArgumentMatchers.any(Locale.class), ArgumentMatchers.eq("over_x_days.short"), ArgumentMatchers.isNull(String.class), ArgumentMatchers.eq(PeriodsTest.NUMBER_OF_DAYS));
    }

    @Test
    public void throw_IAE_when_period_property_is_empty() {
        settings.setProperty(((CorePropertyDefinitions.LEAK_PERIOD) + (PeriodsTest.PERIOD_INDEX)), "");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Period property should not be empty");
        periods.label(PeriodsTest.PERIOD_INDEX);
    }

    @Test
    public void throw_IAE_when_period_property_is_null() {
        settings.setProperty(((CorePropertyDefinitions.LEAK_PERIOD) + (PeriodsTest.PERIOD_INDEX)), ((String) (null)));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Period property should not be empty");
        periods.label(PeriodsTest.PERIOD_INDEX);
    }
}

