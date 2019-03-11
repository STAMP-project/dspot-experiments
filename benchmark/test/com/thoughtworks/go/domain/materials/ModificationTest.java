/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.domain.materials;


import com.google.gson.Gson;
import com.thoughtworks.go.domain.MaterialInstance;
import com.thoughtworks.go.domain.materials.svn.SvnMaterialInstance;
import com.thoughtworks.go.util.json.JsonHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class ModificationTest {
    @Test
    public void shouldReturnAnonymousWhenUserNameIsEmpty() {
        Modification modification = new Modification("", "comment", null, null, null);
        Assert.assertThat(modification.getUserDisplayName(), is(Modification.ANONYMOUS));
        modification.setUserName("");
        Assert.assertThat(modification.getUserDisplayName(), is(Modification.ANONYMOUS));
        modification.setUserName("   ");
        Assert.assertThat(modification.getUserDisplayName(), is(Modification.ANONYMOUS));
    }

    @Test
    public void shouldAllowAdditionalData() throws Exception {
        String expected = "some additional data";
        Modification modification = new Modification("loser", "", null, new Date(), "rev-123", expected);
        Assert.assertThat(modification.getAdditionalData(), is(expected));
    }

    @Test
    public void shouldAllowNullComment() throws Exception {
        Modification mod = new Modification("user", null, "email", new Date(), "1");
        Assert.assertThat(mod.getComment(), is(nullValue()));
        Assert.assertThat(mod.getCardNumbersFromComment().isEmpty(), is(true));
    }

    @Test
    public void shouldReturnUserNameWhenUserNameIsNotEmpty() throws Exception {
        Modification modification = new Modification("jack", "", null, null, null);
        Assert.assertThat(modification.getUserDisplayName(), is("jack"));
    }

    @Test
    public void shouldConsiderPipelineLabelForEqualsAndHashcode() {
        Date date = new Date();
        Modification modification = new Modification("user", "comment", "foo@bar.com", date, "15");
        Modification anotherModificationWithoutLabel = new Modification("user", "comment", "foo@bar.com", date, "15");
        Modification modificationWithLabel = new Modification("user", "comment", "foo@bar.com", date, "15");
        modificationWithLabel.setPipelineLabel("foo-12");// even though it doesn't make sense, equals and hashcode must respect it

        Assert.assertThat(modification.hashCode(), not(modificationWithLabel.hashCode()));
        Assert.assertThat(modification.hashCode(), is(anotherModificationWithoutLabel.hashCode()));
        Assert.assertThat(modification, not(modificationWithLabel));
        Assert.assertThat(modification, is(anotherModificationWithoutLabel));
    }

    @Test
    public void shouldSerializeAndUnserializeAllAttributes() throws IOException, ClassNotFoundException {
        HashMap<String, String> additionalData = new HashMap<>();
        additionalData.put("foo", "bar");
        Modification modification = new Modification("user", "comment", "foo@bar.com", new Date(), "pipe/1/stage/2", JsonHelper.toJsonString(additionalData));
        modification.setPipelineLabel("label-1");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(modification);
        ObjectInputStream inputStream1 = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        Modification unserializedModification = ((Modification) (inputStream1.readObject()));
        Assert.assertThat(unserializedModification, is(modification));
        Assert.assertThat(unserializedModification.getAdditionalData(), is(JsonHelper.toJsonString(additionalData)));
        modification = new Modification("user", null, "foo@bar.com", new Date(), "pipe/1/stage/2", JsonHelper.toJsonString(additionalData));
        byteArrayOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(modification);
        inputStream1 = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        unserializedModification = ((Modification) (inputStream1.readObject()));
        Assert.assertThat(unserializedModification.getComment(), is(nullValue()));
        Assert.assertThat(unserializedModification.getAdditionalData(), is(JsonHelper.toJsonString(additionalData)));
        Assert.assertThat(unserializedModification, is(modification));
    }

    @Test
    public void shouldCopyConstructor() {
        Modification modification = new Modification("user", "comment", "foo@bar.com", new Date(), "pipe/1/stage/2");
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("a1", "v1");
        additionalData.put("a2", "v2");
        modification.setAdditionalData(new Gson().toJson(additionalData));
        MaterialInstance original = new SvnMaterialInstance("url", "username", UUID.randomUUID().toString(), true);
        modification.setMaterialInstance(original);
        Assert.assertThat(new Modification(modification), is(modification));
        modification = new Modification(new Date(), "rev", "label", 121L);
        Modification copiedModification = new Modification(modification);
        Assert.assertThat(copiedModification, is(modification));
        Assert.assertThat(copiedModification.getAdditionalDataMap(), is(modification.getAdditionalDataMap()));
    }

    @Test
    public void shouldParseCardNumberFromAComment() {
        Modification modification = new Modification(null, "Fixing #3455 and #1234", null, null, null);
        Assert.assertThat(modification.getCardNumbersFromComment().size(), is(2));
        Assert.assertThat(modification.getCardNumbersFromComment(), hasItems("3455", "1234"));
    }
}

