/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.rest.bulk.handler.impl;


import java.io.IOException;
import java.util.ArrayList;
import org.elasticsearch.hadoop.rest.EsHadoopRemoteException;
import org.elasticsearch.hadoop.rest.bulk.handler.BulkWriteFailure;
import org.elasticsearch.hadoop.util.BytesArray;
import org.elasticsearch.hadoop.util.DateUtils;
import org.elasticsearch.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class BulkErrorEventConverterTest {
    @Test
    public void generateEvent() throws IOException {
        BytesArray bytes = new BytesArray("{\"action\":\"index\"}\n{\"field\":\"value\"}\n");
        BulkErrorEventConverter eventConverter = new BulkErrorEventConverter();
        BulkWriteFailure iaeFailure = new BulkWriteFailure(400, new IllegalArgumentException("garbage"), bytes, 4, new ArrayList<String>());
        BulkWriteFailure remoteFailure = new BulkWriteFailure(400, new EsHadoopRemoteException("io_exception", "Faiiiil"), bytes, 4, new ArrayList<String>());
        String rawEvent = eventConverter.getRawEvent(iaeFailure);
        Assert.assertEquals(bytes.toString(), rawEvent);
        String timestamp = eventConverter.getTimestamp(iaeFailure);
        Assert.assertTrue(StringUtils.hasText(timestamp));
        Assert.assertTrue(((DateUtils.parseDate(timestamp).getTime().getTime()) > 1L));
        {
            String exceptionType = eventConverter.renderExceptionType(iaeFailure);
            Assert.assertEquals("illegal_argument_exception", exceptionType);
        }
        {
            String exceptionType = eventConverter.renderExceptionType(remoteFailure);
            Assert.assertEquals("io_exception", exceptionType);
        }
        {
            String exceptionMessage = eventConverter.renderExceptionMessage(iaeFailure);
            Assert.assertEquals("garbage", exceptionMessage);
        }
        {
            String exceptionMessage = eventConverter.renderExceptionMessage(remoteFailure);
            Assert.assertEquals("Faiiiil", exceptionMessage);
        }
        String eventMessage = eventConverter.renderEventMessage(iaeFailure);
        Assert.assertEquals("Encountered Bulk Failure", eventMessage);
    }
}

