package com.developerphil.adbidea.compatibility;


import com.developerphil.adbidea.test.Holder;
import org.hamcrest.core.Is;
import org.joor.ReflectException;
import org.junit.Assert;
import org.junit.Test;


public class BackwardCompatibleGetterTest {
    @Test
    public void onlyCallCurrentImplementationWhenItIsValid() throws Exception {
        final Holder<Boolean> currentImplementation = new Holder<Boolean>(false);
        get();
        Assert.assertThat(currentImplementation.get(), Is.is(true));
    }

    @Test
    public void callPreviousImplementationWhenCurrentThrowsErrors() throws Exception {
        BackwardCompatibleGetterTest.expectPreviousImplementationIsCalledFor(new ClassNotFoundException());
        BackwardCompatibleGetterTest.expectPreviousImplementationIsCalledFor(new NoSuchMethodException());
        BackwardCompatibleGetterTest.expectPreviousImplementationIsCalledFor(new NoSuchFieldException());
        BackwardCompatibleGetterTest.expectPreviousImplementationIsCalledFor(new LinkageError());
        BackwardCompatibleGetterTest.expectPreviousImplementationIsCalledFor(new ReflectException());
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionsWhenTheyAreNotRelatedToBackwardCompatibility() throws Exception {
        get();
    }
}

