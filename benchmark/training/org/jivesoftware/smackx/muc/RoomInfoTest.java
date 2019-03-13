/**
 * Copyright 2011 Robin Collier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.muc;


import DataForm.Type;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.junit.Assert;
import org.junit.Test;


public class RoomInfoTest {
    @Test
    public void validateRoomWithEmptyForm() {
        DataForm dataForm = new DataForm(Type.result);
        DiscoverInfo discoInfo = new DiscoverInfo();
        discoInfo.addExtension(dataForm);
        RoomInfo roomInfo = new RoomInfo(discoInfo);
        Assert.assertTrue(roomInfo.getDescription().isEmpty());
        Assert.assertTrue(roomInfo.getSubject().isEmpty());
        Assert.assertEquals((-1), roomInfo.getOccupantsCount());
    }

    @Test
    public void validateRoomWithForm() {
        DataForm dataForm = new DataForm(Type.result);
        FormField desc = new FormField("muc#roominfo_description");
        desc.addValue("The place for all good witches!");
        dataForm.addField(desc);
        FormField subject = new FormField("muc#roominfo_subject");
        subject.addValue("Spells");
        dataForm.addField(subject);
        FormField occupants = new FormField("muc#roominfo_occupants");
        occupants.addValue("3");
        dataForm.addField(occupants);
        DiscoverInfo discoInfo = new DiscoverInfo();
        discoInfo.addExtension(dataForm);
        RoomInfo roomInfo = new RoomInfo(discoInfo);
        Assert.assertEquals("The place for all good witches!", roomInfo.getDescription());
        Assert.assertEquals("Spells", roomInfo.getSubject());
        Assert.assertEquals(3, roomInfo.getOccupantsCount());
    }
}

