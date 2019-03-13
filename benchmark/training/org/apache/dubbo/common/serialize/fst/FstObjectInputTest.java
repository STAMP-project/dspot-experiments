/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.serialize.fst;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.dubbo.common.serialize.model.person.FullAddress;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class FstObjectInputTest {
    private FstObjectInput fstObjectInput;

    @Test
    public void testWrongClassInput() throws IOException, ClassNotFoundException {
        Assertions.assertThrows(IOException.class, () -> {
            this.fstObjectInput = new FstObjectInput(new ByteArrayInputStream("{animal: 'cat'}".getBytes()));
            fstObjectInput.readObject(FullAddress.class);
        });
    }

    @Test
    public void testEmptyByteArrayForEmptyInput() throws IOException {
        this.fstObjectInput = new FstObjectInput(new ByteArrayInputStream("".getBytes()));
        byte[] bytes = fstObjectInput.readBytes();
        MatcherAssert.assertThat(bytes.length, CoreMatchers.is(0));
    }
}

