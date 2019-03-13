/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.beats;


import BeatsTransport.Config;
import NettyTransport.CK_PORT;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.junit.Test;


public class BeatsTransportConfigTest {
    @Test
    public void getRequestedConfigurationOverridesDefaultPort() throws Exception {
        final BeatsTransport.Config config = new BeatsTransport.Config();
        final ConfigurationRequest requestedConfiguration = config.getRequestedConfiguration();
        assertThat(requestedConfiguration.containsField(CK_PORT)).isTrue();
        assertThat(requestedConfiguration.getField(CK_PORT).getDefaultValue()).isEqualTo(5044);
    }
}

