/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.metadata;


import DeprecationHandler.THROW_UNSUPPORTED_OPERATION;
import JsonXContent.jsonXContent;
import ToXContent.EMPTY_PARAMS;
import io.crate.test.integration.CrateUnitTest;
import io.crate.user.SecureHash;
import java.io.IOException;
import java.util.HashMap;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class UsersMetaDataTest extends CrateUnitTest {
    @Test
    public void testUsersMetaDataStreaming() throws IOException {
        UsersMetaData users = new UsersMetaData(UserDefinitions.SINGLE_USER_ONLY);
        BytesStreamOutput out = new BytesStreamOutput();
        users.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        UsersMetaData users2 = new UsersMetaData(in);
        assertEquals(users, users2);
    }

    @Test
    public void testUsersMetaDataToXContent() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        // reflects the logic used to process custom metadata in the cluster state
        builder.startObject();
        UsersMetaData users = new UsersMetaData(UserDefinitions.DUMMY_USERS);
        users.toXContent(builder, EMPTY_PARAMS);
        builder.endObject();
        XContentParser parser = jsonXContent.createParser(xContentRegistry(), THROW_UNSUPPORTED_OPERATION, Strings.toString(builder));
        parser.nextToken();// start object

        UsersMetaData users2 = UsersMetaData.fromXContent(parser);
        assertEquals(users, users2);
        // a metadata custom must consume the surrounded END_OBJECT token, no token must be left
        assertThat(parser.nextToken(), Matchers.nullValue());
    }

    @Test
    public void testUsersMetaDataFromLegacyXContent() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        // Generate legacy (v1) XContent of UsersMetaData
        // { "users": [ "Ford", "Arthur" ] }
        builder.startObject();
        builder.startArray("users");
        builder.value("Ford");
        builder.value("Arthur");
        builder.endArray();
        builder.endObject();
        HashMap<String, SecureHash> expectedUsers = new HashMap<>();
        expectedUsers.put("Ford", null);
        expectedUsers.put("Arthur", null);
        XContentParser parser = jsonXContent.createParser(xContentRegistry(), THROW_UNSUPPORTED_OPERATION, Strings.toString(builder));
        parser.nextToken();// start object

        UsersMetaData users = UsersMetaData.fromXContent(parser);
        assertEquals(users, new UsersMetaData(expectedUsers));
        // a metadata custom must consume the surrounded END_OBJECT token, no token must be left
        assertThat(parser.nextToken(), Matchers.nullValue());
    }

    @Test
    public void testUsersMetaDataWithoutAttributesToXContent() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        // reflects the logic used to process custom metadata in the cluster state
        builder.startObject();
        UsersMetaData users = new UsersMetaData(UserDefinitions.SINGLE_USER_ONLY);
        users.toXContent(builder, EMPTY_PARAMS);
        builder.endObject();
        XContentParser parser = jsonXContent.createParser(xContentRegistry(), THROW_UNSUPPORTED_OPERATION, Strings.toString(builder));
        parser.nextToken();// start object

        UsersMetaData users2 = UsersMetaData.fromXContent(parser);
        assertEquals(users, users2);
        // a metadata custom must consume the surrounded END_OBJECT token, no token must be left
        assertThat(parser.nextToken(), Matchers.nullValue());
    }

    @Test
    public void testUserMetaDataWithAttributesStreaming() throws Exception {
        UsersMetaData writeUserMeta = new UsersMetaData(UserDefinitions.DUMMY_USERS);
        BytesStreamOutput out = new BytesStreamOutput();
        writeUserMeta.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        UsersMetaData readUserMeta = new UsersMetaData(in);
        assertThat(writeUserMeta.users(), Is.is(readUserMeta.users()));
    }
}

