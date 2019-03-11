/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.search.elasticsearch6.internal.connection;


import OperationMode.REMOTE;
import StringPool.BLANK;
import StringPool.CLOSE_CURLY_BRACE;
import StringPool.OPEN_CURLY_BRACE;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class RemoteElasticsearchConnectionTest {
    @Test
    public void testModifyConnected() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("operationMode", REMOTE.name());
        _remoteElasticsearchConnection.activate(properties);
        Assert.assertFalse(_remoteElasticsearchConnection.isConnected());
        _remoteElasticsearchConnection.connect();
        Assert.assertTrue(_remoteElasticsearchConnection.isConnected());
        assertTransportAddress("localhost", 9300);
        properties.put("transportAddresses", "127.0.0.1:9999");
        _remoteElasticsearchConnection.modified(properties);
        Assert.assertTrue(_remoteElasticsearchConnection.isConnected());
        assertTransportAddress("127.0.0.1", 9999);
    }

    @Test
    public void testModifyConnectedWithInvalidPropertiesThenValidProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("operationMode", REMOTE.name());
        _remoteElasticsearchConnection.activate(properties);
        _remoteElasticsearchConnection.connect();
        properties.put("additionalConfigurations", StringBundler.concat(OPEN_CURLY_BRACE, RandomTestUtil.randomString(), CLOSE_CURLY_BRACE));
        try {
            _remoteElasticsearchConnection.modified(properties);
            Assert.fail();
        } catch (NullPointerException npe) {
        }
        Assert.assertFalse(_remoteElasticsearchConnection.isConnected());
        properties.replace("additionalConfigurations", BLANK);
        _remoteElasticsearchConnection.modified(properties);
        Assert.assertTrue(_remoteElasticsearchConnection.isConnected());
    }

    @Test
    public void testModifyUnconnected() {
        Assert.assertFalse(_remoteElasticsearchConnection.isConnected());
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("operationMode", REMOTE.name());
        _remoteElasticsearchConnection.modified(properties);
        Assert.assertTrue(_remoteElasticsearchConnection.isConnected());
    }

    private RemoteElasticsearchConnection _remoteElasticsearchConnection;
}

