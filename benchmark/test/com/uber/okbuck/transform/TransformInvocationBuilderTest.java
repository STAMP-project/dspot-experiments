package com.uber.okbuck.transform;


import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import org.junit.Test;
import org.mockito.Mock;


public class TransformInvocationBuilderTest {
    @Mock
    private TransformInput transformInput;

    @Mock
    private TransformOutputProvider outputProvider;

    private TransformInvocationBuilder builder;

    @Test
    public void addInput_shouldAddAnInput() throws Exception {
        TransformInvocation invocation = builder.addInput(transformInput).setOutputProvider(outputProvider).build();
        assertThat(invocation.getInputs()).containsExactly(transformInput);
        assertThat(invocation.getReferencedInputs()).isEmpty();
        assertThat(invocation.getOutputProvider()).isEqualTo(outputProvider);
    }

    @Test
    public void addReferencedInput_shouldAddReferencedInput() throws Exception {
        TransformInvocation invocation = builder.addReferencedInput(transformInput).setOutputProvider(outputProvider).build();
        assertThat(invocation.getInputs()).isEmpty();
        assertThat(invocation.getReferencedInputs()).containsExactly(transformInput);
        assertThat(invocation.getOutputProvider()).isEqualTo(outputProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenOutputProviderNotSet_shouldThrowException() throws Exception {
        builder.build();
    }
}

