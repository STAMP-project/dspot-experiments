/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.jvm.threads;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.message.Message;
import java.util.Scanner;
import org.junit.Test;


public class BxInputStreamTest {
    private InputRequestMessageFactory inputRequestMessageFactory;

    private KernelTest kernelTest = new KernelTest();

    @Test
    public void getUserInput() {
        // given
        kernelTest.addToStdin("John\n");
        kernelTest.addToStdin("1\n");
        // when
        Scanner scanner = new Scanner(new BxInputStream(kernelTest, inputRequestMessageFactory));
        // then
        String name = scanner.nextLine();
        int age = scanner.nextInt();
        assertThat(name).isEqualTo("John");
        assertThat(age).isEqualTo(1);
    }

    private static class InputRequestMessageFactoryMock implements InputRequestMessageFactory {
        @Override
        public Message create(Message parent) {
            return null;
        }
    }
}

