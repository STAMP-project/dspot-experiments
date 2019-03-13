/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
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
package com.fernandocejas.android10.sample.data.cache.serializer;


import com.fernandocejas.android10.sample.data.entity.UserEntity;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SerializerTest {
    private static final String JSON_RESPONSE = "{\n" + (((((("    \"id\": 1,\n" + "    \"cover_url\": \"http://www.android10.org/myapi/cover_1.jpg\",\n") + "    \"full_name\": \"Simon Hill\",\n") + "    \"description\": \"Curabitur gravida nisi at nibh. In hac habitasse platea dictumst. Aliquam augue quam, sollicitudin vitae, consectetuer eget, rutrum at, lorem.\\n\\nInteger tincidunt ante vel ipsum. Praesent blandit lacinia erat. Vestibulum sed magna at nunc commodo placerat.\\n\\nPraesent blandit. Nam nulla. Integer pede justo, lacinia eget, tincidunt eget, tempus vel, pede.\",\n") + "    \"followers\": 7484,\n") + "    \"email\": \"jcooper@babbleset.edu\"\n") + "}");

    private Serializer serializer;

    @Test
    public void testSerializeHappyCase() {
        final UserEntity userEntityOne = serializer.deserialize(SerializerTest.JSON_RESPONSE, UserEntity.class);
        final String jsonString = serializer.serialize(userEntityOne, UserEntity.class);
        final UserEntity userEntityTwo = serializer.deserialize(jsonString, UserEntity.class);
        Assert.assertThat(userEntityOne.getUserId(), CoreMatchers.is(userEntityTwo.getUserId()));
        Assert.assertThat(userEntityOne.getFullname(), CoreMatchers.is(CoreMatchers.equalTo(userEntityTwo.getFullname())));
        Assert.assertThat(userEntityOne.getFollowers(), CoreMatchers.is(userEntityTwo.getFollowers()));
    }

    @Test
    public void testDesearializeHappyCase() {
        final UserEntity userEntity = serializer.deserialize(SerializerTest.JSON_RESPONSE, UserEntity.class);
        Assert.assertThat(userEntity.getUserId(), CoreMatchers.is(1));
        Assert.assertThat(userEntity.getFullname(), CoreMatchers.is("Simon Hill"));
        Assert.assertThat(userEntity.getFollowers(), CoreMatchers.is(7484));
    }
}

