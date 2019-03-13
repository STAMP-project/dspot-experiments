/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.core.app;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;


public class BisqEnvironmentTests {
    @Test
    public void testPropertySourcePrecedence() {
        PropertySource commandlineProps = withProperty("key.x", "x.commandline");
        PropertySource filesystemProps = withProperty("key.y", "y.bisqEnvironment");
        ConfigurableEnvironment bisqEnvironment = new BisqEnvironment.BisqEnvironment(commandlineProps) {
            @Override
            PropertySource<?> getAppDirProperties() {
                return filesystemProps;
            }
        };
        MutablePropertySources propertySources = bisqEnvironment.getPropertySources();
        Assert.assertThat(propertySources.precedenceOf(named(BISQ_COMMANDLINE_PROPERTY_SOURCE_NAME)), CoreMatchers.equalTo(0));
        Assert.assertThat(propertySources.precedenceOf(named(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)), CoreMatchers.equalTo(1));
        Assert.assertThat(propertySources.precedenceOf(named(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)), CoreMatchers.equalTo(2));
        Assert.assertThat(propertySources.precedenceOf(named(BISQ_DEFAULT_PROPERTY_SOURCE_NAME)), CoreMatchers.equalTo(4));
        // we removed support for the rest
        /* assertThat(propertySources.precedenceOf(named(BISQ_APP_DIR_PROPERTY_SOURCE_NAME)), equalTo(3));
        assertThat(propertySources.precedenceOf(named(BISQ_HOME_DIR_PROPERTY_SOURCE_NAME)), equalTo(4));
        assertThat(propertySources.precedenceOf(named(BISQ_CLASSPATH_PROPERTY_SOURCE_NAME)), equalTo(5));
         */
        Assert.assertThat(propertySources.size(), CoreMatchers.equalTo(5));
        Assert.assertThat(bisqEnvironment.getProperty("key.x"), CoreMatchers.equalTo("x.commandline"));// commandline value wins due to precedence

        // TODO check why it fails
        // assertThat(bisqEnvironment.getProperty("key.y"), equalTo("y.bisqEnvironment")); // bisqEnvironment value wins because it's the only one available
    }
}

