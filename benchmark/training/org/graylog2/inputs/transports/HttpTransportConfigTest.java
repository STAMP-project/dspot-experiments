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
package org.graylog2.inputs.transports;


import ConfigurationField.Optional.OPTIONAL;
import HttpTransport.CK_ENABLE_CORS;
import HttpTransport.CK_MAX_CHUNK_SIZE;
import HttpTransport.Config;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.junit.Assert;
import org.junit.Test;


public class HttpTransportConfigTest {
    @Test
    public void testGetRequestedConfiguration() {
        HttpTransport.Config config = new HttpTransport.Config();
        final ConfigurationRequest requestedConfiguration = config.getRequestedConfiguration();
        Assert.assertTrue(requestedConfiguration.containsField(CK_ENABLE_CORS));
        Assert.assertEquals(OPTIONAL, requestedConfiguration.getField(CK_ENABLE_CORS).isOptional());
        Assert.assertEquals(true, requestedConfiguration.getField(CK_ENABLE_CORS).getDefaultValue());
        Assert.assertTrue(requestedConfiguration.containsField(CK_MAX_CHUNK_SIZE));
        Assert.assertEquals(OPTIONAL, requestedConfiguration.getField(CK_MAX_CHUNK_SIZE).isOptional());
        Assert.assertEquals(65536, requestedConfiguration.getField(CK_MAX_CHUNK_SIZE).getDefaultValue());
    }
}

