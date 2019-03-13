/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.as2.api.util;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.component.as2.api.entity.AS2DispositionType;
import org.apache.camel.component.as2.api.entity.AS2MessageDispositionNotificationEntity;
import org.apache.camel.component.as2.api.entity.DispositionMode;
import org.apache.camel.component.as2.api.entity.EntityParser;
import org.apache.camel.component.as2.api.io.AS2SessionInputBuffer;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.message.BasicLineParser;
import org.apache.http.util.CharArrayBuffer;
import org.junit.Assert;
import org.junit.Test;


public class DispositionNotificationContentUtilsTest {
    public static final String DISPOSITION_NOTIFICATION_CONTENT = "Reporting-UA: AS2 Server\r\n" + (((((((((("MDN-Gateway: dns; example.com\r\n" + "Original-Recipient: rfc822; 0123456780000\r\n") + "Final-Recipient: rfc822; 0123456780000\r\n") + "Original-Message-ID: <200207310834482A70BF63@\\\"~~foo~~\\\">\r\n") + "Disposition: automatic-action/MDN-sent-automatically;\r\n") + "  processed/warning: you\'re awesome\r\n") + "Failure: oops-a-failure\r\n") + "Error: oops-an-error\r\n") + "Warning: oops-a-warning\r\n") + "Received-content-MIC: 7v7F++fQaNB1sVLFtMRp+dF+eG4=, sha1\r\n") + "\r\n");

    public static final String EXPECTED_REPORTING_UA = "AS2 Server";

    public static final String EXPECTED_MTN_NAME = "example.com";

    public static final String EXPECTED_ORIGINAL_RECIPIENT = "rfc822; 0123456780000";

    public static final String EXPECTED_FINAL_RECIPIENT = "0123456780000";

    public static final String EXPECTED_ORIGINAL_MESSAGE_ID = "<200207310834482A70BF63@\\\"~~foo~~\\\">";

    public static final DispositionMode EXPECTED_DISPOSITION_MODE = DispositionMode.AUTOMATIC_ACTION_MDN_SENT_AUTOMATICALLY;

    public static final String EXPECTED_DISPOSITION_MODIFIER = "warning: you're awesome";

    public static final AS2DispositionType EXPECTED_DISPOSITION_TYPE = AS2DispositionType.PROCESSED;

    public static final String[] EXPECTED_FAILURE = new String[]{ "oops-a-failure" };

    public static final String[] EXPECTED_ERROR = new String[]{ "oops-an-error" };

    public static final String[] EXPECTED_WARNING = new String[]{ "oops-a-warning" };

    public static final String EXPECTED_ENCODED_MESSAGE_DIGEST = "7v7F++fQaNB1sVLFtMRp+dF+eG4=";

    public static final String EXPECTED_DIGEST_ALGORITHM_ID = "sha1";

    @Test
    public void test() throws Exception {
        InputStream is = new ByteArrayInputStream(DispositionNotificationContentUtilsTest.DISPOSITION_NOTIFICATION_CONTENT.getBytes());
        AS2SessionInputBuffer inbuffer = new AS2SessionInputBuffer(new HttpTransportMetricsImpl(), (8 * 1024));
        inbuffer.bind(is);
        List<CharArrayBuffer> dispositionNotificationFields = EntityParser.parseBodyPartFields(inbuffer, null, INSTANCE, new ArrayList<CharArrayBuffer>());
        AS2MessageDispositionNotificationEntity messageDispositionNotificationEntity = DispositionNotificationContentUtils.parseDispositionNotification(dispositionNotificationFields);
        Assert.assertEquals("Unexpected Reporting UA value", DispositionNotificationContentUtilsTest.EXPECTED_REPORTING_UA, messageDispositionNotificationEntity.getReportingUA());
        Assert.assertEquals("Unexpected MTN Name", DispositionNotificationContentUtilsTest.EXPECTED_MTN_NAME, messageDispositionNotificationEntity.getMtnName());
        Assert.assertEquals("Unexpected Original Recipient", DispositionNotificationContentUtilsTest.EXPECTED_ORIGINAL_RECIPIENT, messageDispositionNotificationEntity.getExtensionFields().get("Original-Recipient"));
        Assert.assertEquals("Unexpected Final Reciptient", DispositionNotificationContentUtilsTest.EXPECTED_FINAL_RECIPIENT, messageDispositionNotificationEntity.getFinalRecipient());
        Assert.assertEquals("Unexpected Original Message ID", DispositionNotificationContentUtilsTest.EXPECTED_ORIGINAL_MESSAGE_ID, messageDispositionNotificationEntity.getOriginalMessageId());
        Assert.assertEquals("Unexpected Disposition Mode", DispositionNotificationContentUtilsTest.EXPECTED_DISPOSITION_MODE, messageDispositionNotificationEntity.getDispositionMode());
        Assert.assertNotNull("Unexpected Null Disposition Modifier", messageDispositionNotificationEntity.getDispositionModifier());
        Assert.assertEquals("Unexpected Disposition Modifier", DispositionNotificationContentUtilsTest.EXPECTED_DISPOSITION_MODIFIER, messageDispositionNotificationEntity.getDispositionModifier().getModifier());
        Assert.assertEquals("Unexpected Disposition Type", DispositionNotificationContentUtilsTest.EXPECTED_DISPOSITION_TYPE, messageDispositionNotificationEntity.getDispositionType());
        Assert.assertArrayEquals("Unexpected Failure Array value", DispositionNotificationContentUtilsTest.EXPECTED_FAILURE, messageDispositionNotificationEntity.getFailureFields());
        Assert.assertArrayEquals("Unexpected Error Array value", DispositionNotificationContentUtilsTest.EXPECTED_ERROR, messageDispositionNotificationEntity.getErrorFields());
        Assert.assertArrayEquals("Unexpected Warning Array value", DispositionNotificationContentUtilsTest.EXPECTED_WARNING, messageDispositionNotificationEntity.getWarningFields());
        Assert.assertNotNull("Unexpected Null Received Content MIC", messageDispositionNotificationEntity.getReceivedContentMic());
        Assert.assertEquals("Unexpected Encoded Message Digest", DispositionNotificationContentUtilsTest.EXPECTED_ENCODED_MESSAGE_DIGEST, messageDispositionNotificationEntity.getReceivedContentMic().getEncodedMessageDigest());
        Assert.assertEquals("Unexpected Digest Algorithm ID", DispositionNotificationContentUtilsTest.EXPECTED_DIGEST_ALGORITHM_ID, messageDispositionNotificationEntity.getReceivedContentMic().getDigestAlgorithmId());
    }
}

