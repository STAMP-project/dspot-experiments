/**
 * Copyright 2016 Google, Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.firebase.jobdispatcher;


import GooglePlayMessageHandler.MSG_RESULT;
import JobService.RESULT_SUCCESS;
import android.os.Message;
import android.os.Messenger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests {@link GooglePlayMessengerCallback}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
public class GooglePlayMessengerCallbackTest {
    @Mock
    Messenger messengerMock;

    GooglePlayMessengerCallback callback;

    @Test
    public void jobFinished() throws Exception {
        final ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        callback.jobFinished(RESULT_SUCCESS);
        Mockito.verify(messengerMock).send(messageCaptor.capture());
        Message message = messageCaptor.getValue();
        Assert.assertEquals(MSG_RESULT, message.what);
        Assert.assertEquals(RESULT_SUCCESS, message.arg1);
        Assert.assertEquals("tag", message.getData().getString(GooglePlayJobWriter.REQUEST_PARAM_TAG));
    }
}

