package com.github.dockerjava.core;


import org.apache.commons.lang.SerializationUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class RemoteApiVersionTest {
    @Test
    public void testSerial() {
        SerializationUtils.serialize(RemoteApiVersion.unknown());
        final RemoteApiVersion remoteApiVersion = RemoteApiVersion.create(1, 20);
        final byte[] serialized = SerializationUtils.serialize(remoteApiVersion);
        RemoteApiVersion deserialized = ((RemoteApiVersion) (SerializationUtils.deserialize(serialized)));
        MatcherAssert.assertThat("Deserialized object mush match source object", deserialized, Matchers.equalTo(remoteApiVersion));
    }
}

