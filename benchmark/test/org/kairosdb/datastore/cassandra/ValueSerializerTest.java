/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.datastore.cassandra;


import java.io.IOException;
import java.nio.ByteBuffer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ValueSerializerTest {
    @Test
    public void testLongs() {
        ByteBuffer buf = ValueSerializer.toByteBuffer(0L);
        MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(0));
        MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(0L));
        buf = ValueSerializer.toByteBuffer(256);
        MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(2));
        MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(256L));
        for (long I = 1; I < 256; I++) {
            buf = ValueSerializer.toByteBuffer(I);
            MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(1));
            MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(I));
        }
        for (long I = 256; I < 65536; I++) {
            buf = ValueSerializer.toByteBuffer(I);
            MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(2));
            MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(I));
        }
        for (long I = 65536; I < 16777216; I++) {
            buf = ValueSerializer.toByteBuffer(I);
            MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(3));
            MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(I));
        }
        for (long I = 16777216; I < 4294967296L; I += 4194304) {
            buf = ValueSerializer.toByteBuffer(I);
            MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(4));
            MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(I));
        }
        for (long I = 4294967296L; I < 1099511627776L; I += 1073741824L) {
            buf = ValueSerializer.toByteBuffer(I);
            MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(5));
            MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(I));
        }
        for (long I = 1099511627776L; I < 281474976710656L; I += 274877906944L) {
            buf = ValueSerializer.toByteBuffer(I);
            MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(6));
            MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(I));
        }
        for (long I = 281474976710656L; I < 72057594037927936L; I += 70368744177664L) {
            buf = ValueSerializer.toByteBuffer(I);
            MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(7));
            MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(I));
        }
        for (long I = 72057594037927936L; I < 8070450532247928832L; I += 18014398509481984L) {
            buf = ValueSerializer.toByteBuffer(I);
            MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(8));
            MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo(I));
        }
        buf = ValueSerializer.toByteBuffer((-1));
        MatcherAssert.assertThat(buf.remaining(), Matchers.equalTo(8));
        MatcherAssert.assertThat(ValueSerializer.getLongFromByteBuffer(buf), Matchers.equalTo((-1L)));
    }

    @Test
    public void testPackUnpack() throws IOException {
        testValue(0L);
        testValue(256);
        for (long I = 1; I < 256; I++) {
            testValue(I);
        }
        for (long I = 256; I < 65536; I++) {
            testValue(I);
        }
        for (long I = 65536; I < 16777216; I++) {
            testValue(I);
        }
        for (long I = 16777216; I < 4294967296L; I += 4194304) {
            testValue(I);
        }
        for (long I = 4294967296L; I < 1099511627776L; I += 1073741824L) {
            testValue(I);
        }
        for (long I = 1099511627776L; I < 281474976710656L; I += 274877906944L) {
            testValue(I);
        }
        for (long I = 281474976710656L; I < 72057594037927936L; I += 70368744177664L) {
            testValue(I);
        }
        for (long I = 72057594037927936L; I < 8070450532247928832L; I += 18014398509481984L) {
            testValue(I);
        }
        testValue((-1));
    }
}

