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
package org.kaaproject.kaa.server.control;


import NotificationTypeDto.SYSTEM;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.NotificationDto;


/**
 * The Class ControlServerNotificationIT.
 */
public class ControlServerNotificationIT extends AbstractTestControlServer {
    /**
     * Send notification.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void sendNotification() throws Exception {
        NotificationDto notification = sendNotification(null, null, SYSTEM);
        Assert.assertNotNull(notification.getId());
    }
}

