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
package org.sonar.ce.task.projectanalysis.qualitymodel;


import CoreMetrics.COMPLEXITY_KEY;
import CoreMetrics.NCLOC_KEY;
import CoreProperties.RATING_GRID;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.CoreProperties;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.MessageException;
import org.sonar.core.config.CorePropertyDefinitions;


public class RatingSettingsTest {
    private MapSettings settings = new MapSettings(new org.sonar.api.config.PropertyDefinitions(CorePropertyDefinitions.all()));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void load_rating_grid() {
        settings.setProperty(RATING_GRID, "1,3.4,8,50");
        RatingSettings configurationLoader = new RatingSettings(settings.asConfig());
        double[] grid = configurationLoader.getDebtRatingGrid().getGridValues();
        assertThat(grid).hasSize(4);
        assertThat(grid[0]).isEqualTo(1.0);
        assertThat(grid[1]).isEqualTo(3.4);
        assertThat(grid[2]).isEqualTo(8.0);
        assertThat(grid[3]).isEqualTo(50.0);
    }

    @Test
    public void load_work_units_for_language() {
        settings.setProperty(CoreProperties.DEVELOPMENT_COST, "50");
        RatingSettings configurationLoader = new RatingSettings(settings.asConfig());
        assertThat(configurationLoader.getDevCost("defaultLanguage")).isEqualTo(50L);
    }

    @Test
    public void load_overridden_values_for_language() {
        String aLanguage = "aLanguage";
        String anotherLanguage = "anotherLanguage";
        settings.setProperty(CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS, "0,1");
        settings.setProperty((((((CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS) + ".") + "0") + ".") + (CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS_LANGUAGE_KEY)), aLanguage);
        settings.setProperty((((((CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS) + ".") + "0") + ".") + (CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS_MAN_DAYS_KEY)), "30");
        settings.setProperty((((((CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS) + ".") + "0") + ".") + (CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS_SIZE_METRIC_KEY)), NCLOC_KEY);
        settings.setProperty((((((CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS) + ".") + "1") + ".") + (CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS_LANGUAGE_KEY)), anotherLanguage);
        settings.setProperty((((((CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS) + ".") + "1") + ".") + (CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS_MAN_DAYS_KEY)), "40");
        settings.setProperty((((((CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS) + ".") + "1") + ".") + (CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS_SIZE_METRIC_KEY)), COMPLEXITY_KEY);
        RatingSettings configurationLoader = new RatingSettings(settings.asConfig());
        assertThat(configurationLoader.getDevCost(aLanguage)).isEqualTo(30L);
        assertThat(configurationLoader.getDevCost(anotherLanguage)).isEqualTo(40L);
    }

    @Test
    public void fail_on_invalid_rating_grid_configuration() {
        expectedException.expect(IllegalArgumentException.class);
        settings.setProperty(RATING_GRID, "a b c");
        new RatingSettings(settings.asConfig());
    }

    @Test
    public void use_generic_value_when_specific_setting_is_missing() {
        String aLanguage = "aLanguage";
        settings.setProperty(CoreProperties.DEVELOPMENT_COST, "30");
        settings.setProperty(CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS, "0");
        settings.setProperty((((((CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS) + ".") + "0") + ".") + (CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS_LANGUAGE_KEY)), aLanguage);
        settings.setProperty((((((CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS) + ".") + "0") + ".") + (CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS_MAN_DAYS_KEY)), "40");
        RatingSettings configurationLoader = new RatingSettings(settings.asConfig());
        assertThat(configurationLoader.getDevCost(aLanguage)).isEqualTo(40L);
    }

    @Test
    public void constructor_fails_with_ME_if_language_specific_parameter_language_is_missing() {
        settings.setProperty(CoreProperties.DEVELOPMENT_COST, "30");
        settings.setProperty(CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS, "0");
        settings.setProperty((((((CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS) + ".") + "0") + ".") + (CoreProperties.LANGUAGE_SPECIFIC_PARAMETERS_MAN_DAYS_KEY)), "40");
        expectedException.expect(MessageException.class);
        expectedException.expectMessage(("Technical debt configuration is corrupted. At least one language specific parameter has no Language key. " + "Contact your administrator to update this configuration in the global administration section of SonarQube."));
        new RatingSettings(settings.asConfig());
    }
}

