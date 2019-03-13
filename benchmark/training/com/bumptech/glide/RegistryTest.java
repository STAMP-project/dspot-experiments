package com.bumptech.glide;


import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RegistryTest {
    @Mock
    private ModelLoaderFactory<RegistryTest.Model, RegistryTest.Data> modelLoaderFactory;

    @Mock
    private ResourceDecoder<RegistryTest.Data, RegistryTest.ResourceOne> resourceOneDecoder;

    @Mock
    private ResourceDecoder<RegistryTest.Data, RegistryTest.ResourceTwo> resourceTwoDecoder;

    @Mock
    private ResourceTranscoder<RegistryTest.ResourceOne, RegistryTest.TranscodeOne> resourceOneTranscodeOneTranscoder;

    private Registry registry;

    @Test
    public void getRegisteredResourceClasses_withNoResources_isEmpty() {
        assertThat(getRegisteredResourceClasses()).isEmpty();
    }

    @Test
    public void getRegisteredResourceClasses_withOneDataClass_noResourceClasses_isEmpty() {
        registry.append(RegistryTest.Model.class, RegistryTest.Data.class, modelLoaderFactory);
        assertThat(getRegisteredResourceClasses()).isEmpty();
    }

    @Test
    public void getRegisteredResourceClasses_withOneDataAndResourceClass_noTranscodeClass_isEmpty() {
        registry.append(RegistryTest.Model.class, RegistryTest.Data.class, modelLoaderFactory);
        registry.append(RegistryTest.Data.class, RegistryTest.ResourceOne.class, resourceOneDecoder);
        assertThat(getRegisteredResourceClasses()).isEmpty();
    }

    @Test
    public void getRegisteredResourceClasses_withOneDataAndResourceAndTranscodeClass_isNotEmpty() {
        registry.append(RegistryTest.Model.class, RegistryTest.Data.class, modelLoaderFactory);
        registry.append(RegistryTest.Data.class, RegistryTest.ResourceOne.class, resourceOneDecoder);
        registry.register(RegistryTest.ResourceOne.class, RegistryTest.TranscodeOne.class, resourceOneTranscodeOneTranscoder);
        assertThat(getRegisteredResourceClasses()).containsExactly(RegistryTest.ResourceOne.class);
    }

    @Test
    public void getRegisteredResourceClasses_withMissingTranscodeForOneOfTwoResources_isNotEmpty() {
        // The loop allows us to make sure that the order in which we call getRegisteredResourceClasses
        // doesn't affect the output.
        for (int i = 0; i < 2; i++) {
            Registry registry = new Registry();
            registry.append(RegistryTest.Model.class, RegistryTest.Data.class, modelLoaderFactory);
            registry.append(RegistryTest.Data.class, RegistryTest.ResourceOne.class, resourceOneDecoder);
            registry.append(RegistryTest.Data.class, RegistryTest.ResourceTwo.class, resourceTwoDecoder);
            registry.register(RegistryTest.ResourceOne.class, RegistryTest.TranscodeOne.class, resourceOneTranscodeOneTranscoder);
            List<Class<?>> resourceOneClasses;
            List<Class<?>> resourceTwoClasses;
            if (i == 0) {
                resourceOneClasses = registry.getRegisteredResourceClasses(RegistryTest.Model.class, RegistryTest.ResourceOne.class, RegistryTest.TranscodeOne.class);
                resourceTwoClasses = registry.getRegisteredResourceClasses(RegistryTest.Model.class, RegistryTest.ResourceTwo.class, RegistryTest.TranscodeOne.class);
            } else {
                resourceTwoClasses = registry.getRegisteredResourceClasses(RegistryTest.Model.class, RegistryTest.ResourceTwo.class, RegistryTest.TranscodeOne.class);
                resourceOneClasses = registry.getRegisteredResourceClasses(RegistryTest.Model.class, RegistryTest.ResourceOne.class, RegistryTest.TranscodeOne.class);
            }
            // ResourceOne has a corresponding transcode class, so we should return it.
            assertThat(resourceOneClasses).containsExactly(RegistryTest.ResourceOne.class);
            // ResourceTwo has no matching transcode class, so we shouldn't return it.
            assertThat(resourceTwoClasses).isEmpty();
        }
    }

    @Test
    public void getRegisteredResourceClasses_withOneOfTwoMissingTranscoders_isNotEmpty() {
        // The loop allows us to make sure that the order in which we call getRegisteredResourceClasses
        // doesn't affect the output.
        for (int i = 0; i < 2; i++) {
            Registry registry = new Registry();
            registry.append(RegistryTest.Model.class, RegistryTest.Data.class, modelLoaderFactory);
            registry.append(RegistryTest.Data.class, RegistryTest.ResourceOne.class, resourceOneDecoder);
            registry.register(RegistryTest.ResourceOne.class, RegistryTest.TranscodeOne.class, resourceOneTranscodeOneTranscoder);
            List<Class<?>> transcodeOneClasses;
            List<Class<?>> transcodeTwoClasses;
            if (i == 0) {
                transcodeOneClasses = registry.getRegisteredResourceClasses(RegistryTest.Model.class, RegistryTest.ResourceOne.class, RegistryTest.TranscodeOne.class);
                transcodeTwoClasses = registry.getRegisteredResourceClasses(RegistryTest.Model.class, RegistryTest.ResourceOne.class, RegistryTest.TranscodeTwo.class);
            } else {
                transcodeTwoClasses = registry.getRegisteredResourceClasses(RegistryTest.Model.class, RegistryTest.ResourceOne.class, RegistryTest.TranscodeTwo.class);
                transcodeOneClasses = registry.getRegisteredResourceClasses(RegistryTest.Model.class, RegistryTest.ResourceOne.class, RegistryTest.TranscodeOne.class);
            }
            // TranscodeOne has a corresponding ResourceTranscoder, so we expect to see the resource
            // class.
            assertThat(transcodeOneClasses).containsExactly(RegistryTest.ResourceOne.class);
            // TranscodeTwo has no corresponding ResourceTranscoder class, so we shouldn't return the
            // resource class.
            assertThat(transcodeTwoClasses).isEmpty();
        }
    }

    // Empty class to represent model classes for readability.
    private static final class Model {}

    // Empty class to represent data classes for readability.
    private static final class Data {}

    // Empty class to represent resource classes for readability.
    private static final class ResourceOne {}

    // Empty class to represent another resource class for readability.
    private static final class ResourceTwo {}

    // Empty class to represent transcode classes for readability.
    private static final class TranscodeOne {}

    // Empty class to represent transcode classes for readability.
    private static final class TranscodeTwo {}
}

