/**
 * Copyright 2013-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.co.real_logic.sbe.ir.Ir;


public class TargetCodeGeneratorTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNoTargetLanguage() throws Exception {
        SbeTool.generate(Mockito.mock(Ir.class), ".", "none");
    }

    @Test
    public void shouldLoadAndInstantiateNonStandardTargetLanguage() throws Exception {
        final Ir ir = Mockito.mock(Ir.class);
        final String outputDir = ".";
        SbeTool.generate(ir, outputDir, "uk.co.real_logic.sbe.TestTargetLanguage");
        Mockito.verify(TestTargetLanguage.SINGLETON).generate();
        Assert.assertThat(ir, Is.is(TestTargetLanguage.ir));
        Assert.assertThat(outputDir, Is.is(TestTargetLanguage.outputDir));
    }
}

