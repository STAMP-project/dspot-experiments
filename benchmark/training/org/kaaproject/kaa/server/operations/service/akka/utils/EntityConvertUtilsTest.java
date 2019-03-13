/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.operations.service.akka.utils;


import LogDeliveryErrorCode.APPENDER_INTERNAL_ERROR;
import LogDeliveryErrorCode.NO_APPENDERS_CONFIGURED;
import LogDeliveryErrorCode.REMOTE_CONNECTION_ERROR;
import LogDeliveryErrorCode.REMOTE_INTERNAL_ERROR;
import SyncStatus.FAILURE;
import SyncStatus.SUCCESS;
import UserVerifierErrorCode.CONNECTION_ERROR;
import UserVerifierErrorCode.INTERNAL_ERROR;
import UserVerifierErrorCode.NO_VERIFIER_CONFIGURED;
import UserVerifierErrorCode.OTHER;
import UserVerifierErrorCode.REMOTE_ERROR;
import UserVerifierErrorCode.TOKEN_EXPIRED;
import UserVerifierErrorCode.TOKEN_INVALID;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.log.shared.appender.LogDeliveryErrorCode;
import org.kaaproject.kaa.server.common.verifier.UserVerifierErrorCode;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.logs.LogDeliveryMessage;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.user.verification.UserVerificationResponseMessage;
import org.kaaproject.kaa.server.sync.LogServerSync;


public class EntityConvertUtilsTest {
    @Test
    public void toUserVerifierErrorCodeTest() {
        Assert.assertEquals(EntityConvertUtils.toErrorCode(((UserVerifierErrorCode) (null))), null);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(NO_VERIFIER_CONFIGURED), org.kaaproject.kaa.server.sync.UserVerifierErrorCode.NO_VERIFIER_CONFIGURED);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(TOKEN_INVALID), org.kaaproject.kaa.server.sync.UserVerifierErrorCode.TOKEN_INVALID);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(TOKEN_EXPIRED), org.kaaproject.kaa.server.sync.UserVerifierErrorCode.TOKEN_EXPIRED);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(INTERNAL_ERROR), org.kaaproject.kaa.server.sync.UserVerifierErrorCode.INTERNAL_ERROR);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(CONNECTION_ERROR), org.kaaproject.kaa.server.sync.UserVerifierErrorCode.CONNECTION_ERROR);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(REMOTE_ERROR), org.kaaproject.kaa.server.sync.UserVerifierErrorCode.REMOTE_ERROR);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(OTHER), org.kaaproject.kaa.server.sync.UserVerifierErrorCode.OTHER);
    }

    @Test
    public void toLogDeliveryErrorCodeTest() {
        Assert.assertEquals(EntityConvertUtils.toErrorCode(((LogDeliveryErrorCode) (null))), null);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(APPENDER_INTERNAL_ERROR), org.kaaproject.kaa.server.sync.LogDeliveryErrorCode.APPENDER_INTERNAL_ERROR);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(NO_APPENDERS_CONFIGURED), org.kaaproject.kaa.server.sync.LogDeliveryErrorCode.NO_APPENDERS_CONFIGURED);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(REMOTE_CONNECTION_ERROR), org.kaaproject.kaa.server.sync.LogDeliveryErrorCode.REMOTE_CONNECTION_ERROR);
        Assert.assertEquals(EntityConvertUtils.toErrorCode(REMOTE_INTERNAL_ERROR), org.kaaproject.kaa.server.sync.LogDeliveryErrorCode.REMOTE_INTERNAL_ERROR);
    }

    @Test
    public void convertTest() {
        UserVerificationResponseMessage successMessage = getUserVerificationResponseMessage(true, null);
        UserVerificationResponseMessage failureMessage = getUserVerificationResponseMessage(false, null);
        Assert.assertEquals(EntityConvertUtils.convert(successMessage).getResult(), SUCCESS);
        Assert.assertEquals(EntityConvertUtils.convert(failureMessage).getResult(), FAILURE);
        Map<Integer, LogDeliveryMessage> responseMap = new HashMap<>();
        LogDeliveryMessage successLogDeliveryMessage = getLogDeliveryMessage(true, null);
        LogDeliveryMessage failureLogDeliveryMessage = getLogDeliveryMessage(false, APPENDER_INTERNAL_ERROR);
        responseMap.put(1, successLogDeliveryMessage);
        responseMap.put(2, failureLogDeliveryMessage);
        LogServerSync sync = EntityConvertUtils.convert(responseMap);
        Assert.assertNull(sync.getDeliveryStatuses().get(0).getErrorCode());
        Assert.assertEquals(sync.getDeliveryStatuses().get(1).getErrorCode(), org.kaaproject.kaa.server.sync.LogDeliveryErrorCode.APPENDER_INTERNAL_ERROR);
    }
}

