package com.uber.okbuck.transform;


import org.junit.Test;
import org.mockito.Mockito;


public class CliTransformTest {
    @Test(expected = IllegalArgumentException.class)
    public void whenStarting_withOneArgument_shouldThrowException() throws Exception {
        CliTransform.main(new String[]{ "any" });
    }

    @Test
    public void whenStarting_shouldRunTransform() throws Exception {
        final TransformRunner runner = Mockito.mock(TransformRunner.class);
        CliTransform.main(new CliTransform.TransformRunnerProvider() {
            @Override
            public TransformRunner provide() {
                return runner;
            }
        });
        Mockito.verify(runner).runTransform();
    }
}

