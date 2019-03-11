/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.operations.daemon;


import org.junit.Assert;
import org.junit.Test;
import org.syncany.operations.daemon.messages.GetFileFolderRequest;
import org.syncany.operations.daemon.messages.GetFileFolderResponse;
import org.syncany.operations.daemon.messages.ListWatchesManagementRequest;
import org.syncany.operations.daemon.messages.UpUploadFileSyncExternalEvent;
import org.syncany.operations.daemon.messages.api.JsonMessageFactory;
import org.syncany.operations.daemon.messages.api.Message;
import org.syncany.operations.daemon.messages.api.Request;
import org.syncany.operations.daemon.messages.api.Response;


// TODO [low] Missing tests for the converters
public class JsonMessageFactoryTest {
    @Test
    public void testJsonToMessageSuccess() throws Exception {
        Message message = JsonMessageFactory.toMessage("{\"ListWatchesManagementRequest\":{\"id\":\"123\"}}");
        Assert.assertEquals(ListWatchesManagementRequest.class, message.getClass());
        Assert.assertEquals(123, getId());
    }

    @Test(expected = Exception.class)
    public void testJsonToMessageFailure() throws Exception {
        JsonMessageFactory.toMessage("This is invalid!");
    }

    @Test
    public void testJsonToRequestSuccess() throws Exception {
        Request request = JsonMessageFactory.toRequest("{\"GetFileFolderRequest\":{\"id\":\"1234\",\"root\":\"/some/path\",\"fileHistoryId\":\"beefbeefbeef\",\"version\":\"1337\"}}");
        Assert.assertEquals(GetFileFolderRequest.class, request.getClass());
        Assert.assertEquals(1234, getId());
        Assert.assertEquals("/some/path", getRoot());
        Assert.assertEquals("beefbeefbeef", getFileHistoryId());
        Assert.assertEquals(1337, getVersion());
    }

    @Test
    public void testJsonToResponseSuccess() throws Exception {
        Response response = JsonMessageFactory.toResponse("{\"GetFileFolderResponse\":{\"code\":\"200\",\"requestId\":\"1234\",\"root\":\"/some/path\",\"tempFileToken\":\"beefbeefbeef\"}}");
        Assert.assertEquals(GetFileFolderResponse.class, response.getClass());
        Assert.assertEquals(200, getCode());
        Assert.assertEquals(((Integer) (1234)), getRequestId());
        Assert.assertEquals("beefbeefbeef", getTempToken());
    }

    @Test
    public void testRequestToJson() throws Exception {
        UpUploadFileSyncExternalEvent event = new UpUploadFileSyncExternalEvent("/some/path", "filename.jpg");
        String xmlStr = JsonMessageFactory.toJson(event).replaceAll("\\s+", "");
        Assert.assertEquals("{\"UpUploadFileSyncExternalEvent\":{\"filename\":\"filename.jpg\",\"root\":\"/some/path\"}}", xmlStr);
    }
}

