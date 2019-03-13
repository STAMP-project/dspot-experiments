/**
 * Copyright (c) 2010-2019. Axon Framework
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
package org.axonframework.eventhandling;


import org.axonframework.serialization.JavaSerializer;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MergedTrackingTokenTest {
    @Test
    public void testMergedTokenCoversOriginal() {
        MergedTrackingToken testSubject = new MergedTrackingToken(token(1), token(3));
        Assert.assertTrue(testSubject.covers(token(1)));
        Assert.assertFalse(testSubject.covers(token(2)));
        Assert.assertFalse(testSubject.covers(token(3)));
    }

    @Test
    public void testUpperBound() {
        MergedTrackingToken testSubject = new MergedTrackingToken(token(1), token(3));
        Assert.assertEquals(new MergedTrackingToken(token(2), token(3)), testSubject.upperBound(token(2)));
        Assert.assertEquals(token(3), testSubject.upperBound(token(3)));
    }

    @Test
    public void testLowerBound() {
        MergedTrackingToken testSubject = new MergedTrackingToken(token(1), token(3));
        Assert.assertEquals(new MergedTrackingToken(token(1), token(2)), testSubject.lowerBound(token(2)));
        Assert.assertEquals(token(1), testSubject.lowerBound(token(1)));
    }

    @Test
    public void testUnwrapToLowerBound() {
        Assert.assertEquals(token(1), lowerBound());
        Assert.assertEquals(token(1), lowerBound());
    }

    @Test
    public void testUpperBound_NestedTokens() {
        MergedTrackingToken testSubject = new MergedTrackingToken(new MergedTrackingToken(token(1), token(3)), token(5));
        Assert.assertEquals(new MergedTrackingToken(token(4), token(5)), testSubject.upperBound(token(4)));
        Assert.assertEquals(new MergedTrackingToken(new MergedTrackingToken(token(3), token(3)), token(5)), testSubject.upperBound(token(3)));
        Assert.assertEquals(new MergedTrackingToken(new MergedTrackingToken(token(2), token(3)), token(5)), testSubject.upperBound(token(2)));
    }

    @Test
    public void testLowerBound_NestedTokens() {
        MergedTrackingToken testSubject = new MergedTrackingToken(new MergedTrackingToken(token(1), token(5)), token(3));
        Assert.assertEquals(new MergedTrackingToken(new MergedTrackingToken(token(1), token(3)), token(3)), testSubject.lowerBound(token(3)));
        Assert.assertEquals(new MergedTrackingToken(new MergedTrackingToken(token(1), token(2)), token(2)), testSubject.lowerBound(token(2)));
        Assert.assertEquals(token(1), testSubject.lowerBound(token(1)));
    }

    @Test
    public void testSerializeTokens() {
        MergedTrackingToken testSubject = new MergedTrackingToken(new MergedTrackingToken(token(1), token(5)), token(3));
        Serializer[] serializers = new Serializer[]{ XStreamSerializer.builder().build(), JacksonSerializer.builder().build(), JavaSerializer.builder().build() };
        for (Serializer serializer : serializers) {
            SerializedObject<byte[]> serialized = serializer.serialize(testSubject, byte[].class);
            MergedTrackingToken deserialized = serializer.deserialize(serialized);
            Assert.assertEquals(("Objects not equal with " + (serializer.getClass().getSimpleName())), testSubject, deserialized);
        }
    }

    @Test
    public void testAdvanceWithNestedReplayToken() {
        TrackingToken incomingMessage = new GlobalSequenceTrackingToken(0);
        MergedTrackingToken currentToken = new MergedTrackingToken(new ReplayToken(new GlobalSequenceTrackingToken(9), new GlobalSequenceTrackingToken(9)), new ReplayToken(new GlobalSequenceTrackingToken(9), new GlobalSequenceTrackingToken((-1))));
        TrackingToken advancedToken = currentToken.advancedTo(incomingMessage);
        Assert.assertTrue((advancedToken instanceof MergedTrackingToken));
        MergedTrackingToken actual = ((MergedTrackingToken) (advancedToken));
        Assert.assertTrue(((actual.lowerSegmentToken()) instanceof ReplayToken));// this token should not have been modified

        Assert.assertTrue(("Wrong upper segment: " + (actual.upperSegmentToken())), ((actual.upperSegmentToken()) instanceof ReplayToken));// this token should not have been modified

    }

    @Test
    public void testUnwrapPrefersLastAdvancedToken_LowerSegmenAdvanced() {
        TrackingToken merged = new MergedTrackingToken(token(1), token(3)).advancedTo(token(2));
        Assert.assertTrue((merged instanceof MergedTrackingToken));
        Assert.assertEquals(token(2), WrappedToken.unwrap(merged, GlobalSequenceTrackingToken.class).orElse(null));
    }

    @Test
    public void testUnwrapPrefersLastAdvancedToken_UpperSegmentAdvanced() {
        TrackingToken merged = new MergedTrackingToken(token(3), token(1)).advancedTo(token(2));
        Assert.assertTrue((merged instanceof MergedTrackingToken));
        Assert.assertEquals(token(2), WrappedToken.unwrap(merged, GlobalSequenceTrackingToken.class).orElse(null));
    }

    @Test
    public void testUnwrapPrefersLastAdvancedToken_NeitherSegmentAdvanced() {
        TrackingToken merged = new MergedTrackingToken(token(3), token(3)).advancedTo(token(2));
        Assert.assertTrue((merged instanceof MergedTrackingToken));
        Assert.assertEquals(token(3), WrappedToken.unwrap(merged, GlobalSequenceTrackingToken.class).orElse(null));
    }

    @Test
    public void testUnwrapPrefersLastAdvancedToken_NeitherSegmentAdvanced_OnlyLowerIsCandidate() {
        MergedTrackingToken merged = new MergedTrackingToken(token(3), Mockito.mock(TrackingToken.class));
        Assert.assertEquals(token(3), merged.unwrap(GlobalSequenceTrackingToken.class).orElse(null));
    }

    @Test
    public void testUnwrapPrefersLastAdvancedToken_NeitherSegmentAdvanced_OnlyUpperIsCandidate() {
        MergedTrackingToken merged = new MergedTrackingToken(Mockito.mock(TrackingToken.class), token(3));
        Assert.assertEquals(token(3), merged.unwrap(GlobalSequenceTrackingToken.class).orElse(null));
    }
}

