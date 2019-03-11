/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.misuse;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.WrongTypeOfReturnValue;


public class SpyStubbingMisuseTest {
    @Test
    public void nestedWhenTest() {
        SpyStubbingMisuseTest.Strategy mfoo = Mockito.mock(SpyStubbingMisuseTest.Strategy.class);
        SpyStubbingMisuseTest.Sampler mpoo = Mockito.mock(SpyStubbingMisuseTest.Sampler.class);
        SpyStubbingMisuseTest.Producer out = Mockito.spy(new SpyStubbingMisuseTest.Producer(mfoo));
        try {
            Mockito.when(out.produce()).thenReturn(mpoo);
            Assert.fail();
        } catch (WrongTypeOfReturnValue e) {
            assertThat(e.getMessage()).contains("spy").contains("syntax").contains("doReturn|Throw");
        }
    }

    public class Sample {}

    public class Strategy {
        SpyStubbingMisuseTest.Sample getSample() {
            return new SpyStubbingMisuseTest.Sample();
        }
    }

    public class Sampler {
        SpyStubbingMisuseTest.Sample sample;

        Sampler(SpyStubbingMisuseTest.Strategy f) {
            sample = f.getSample();
        }
    }

    public class Producer {
        SpyStubbingMisuseTest.Strategy strategy;

        Producer(SpyStubbingMisuseTest.Strategy f) {
            strategy = f;
        }

        SpyStubbingMisuseTest.Sampler produce() {
            return new SpyStubbingMisuseTest.Sampler(strategy);
        }
    }
}

