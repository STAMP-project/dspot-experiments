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
import org.syncany.operations.daemon.messages.api.Message;
import org.syncany.operations.daemon.messages.api.Request;
import org.syncany.operations.daemon.messages.api.Response;
import org.syncany.operations.daemon.messages.api.XmlMessageFactory;


// TODO [low] Missing tests for the converters
public class XmlMessageFactoryTest {
    @Test
    public void testXmlToMessageSuccess() throws Exception {
        Message message = XmlMessageFactory.toMessage("<listWatchesManagementRequest><id>123</id></listWatchesManagementRequest>");
        Assert.assertEquals(ListWatchesManagementRequest.class, message.getClass());
        Assert.assertEquals(123, getId());
    }

    @Test(expected = Exception.class)
    public void testXmlToMessageFailure() throws Exception {
        XmlMessageFactory.toMessage("This is invalid!");
    }

    @Test
    public void testXmlToRequestSuccess() throws Exception {
        Request request = XmlMessageFactory.toRequest("<getFileFolderRequest><id>1234</id><root>/some/path</root><fileHistoryId>beefbeefbeef</fileHistoryId><version>1337</version></getFileFolderRequest>");
        Assert.assertEquals(GetFileFolderRequest.class, request.getClass());
        Assert.assertEquals(1234, getId());
        Assert.assertEquals("/some/path", getRoot());
        Assert.assertEquals("beefbeefbeef", getFileHistoryId());
        Assert.assertEquals(1337, getVersion());
    }

    @Test(expected = Exception.class)
    public void testXmlToRequestFailure() throws Exception {
        XmlMessageFactory.toRequest("<showMessageExternalEvent><message>Hi there.</message></showMessageExternalEvent>");
    }

    @Test
    public void testXmlToResponseSuccess() throws Exception {
        Response response = XmlMessageFactory.toResponse("<getFileFolderResponse><code>200</code><requestId>1234</requestId><root>/some/path</root><tempFileToken>beefbeefbeef</tempFileToken></getFileFolderResponse>");
        Assert.assertEquals(GetFileFolderResponse.class, response.getClass());
        Assert.assertEquals(200, getCode());
        Assert.assertEquals(((Integer) (1234)), getRequestId());
        Assert.assertEquals("beefbeefbeef", getTempToken());
    }

    @Test(expected = Exception.class)
    public void testXmlToResponseFailure() throws Exception {
        XmlMessageFactory.toResponse("<watchEndSyncExternalEvent><root>/some/path</root></watchEndSyncExternalEvent>");
    }

    @Test
    public void testRequestToXml() throws Exception {
        UpUploadFileSyncExternalEvent event = new UpUploadFileSyncExternalEvent("/some/path", "filename.jpg");
        String xmlStr = XmlMessageFactory.toXml(event).replaceAll("\\s+", "");
        Assert.assertEquals("<upUploadFileSyncExternalEvent><root>/some/path</root><filename>filename.jpg</filename></upUploadFileSyncExternalEvent>", xmlStr);
    }
}

