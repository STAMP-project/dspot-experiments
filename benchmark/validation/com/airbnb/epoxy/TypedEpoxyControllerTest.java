package com.airbnb.epoxy;


import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class TypedEpoxyControllerTest {
    static class TestTypedController extends TypedEpoxyController<String> {
        int numTimesBuiltModels = 0;

        @Override
        protected void buildModels(String data) {
            Assert.assertEquals("data", data);
            (numTimesBuiltModels)++;
        }
    }

    @Test
    public void setData() {
        TypedEpoxyControllerTest.TestTypedController controller = new TypedEpoxyControllerTest.TestTypedController();
        controller.setData("data");
        controller.setData("data");
        cancelPendingModelBuild();
        controller.setData("data");
        controller.setData("data");
        Assert.assertEquals(4, controller.numTimesBuiltModels);
    }
}

