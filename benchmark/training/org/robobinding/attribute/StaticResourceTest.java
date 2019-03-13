package org.robobinding.attribute;


import android.content.Context;
import android.content.res.Resources;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 * @author Cheng Wei
 */
@RunWith(Theories.class)
public class StaticResourceTest {
    private static final String RESOURCE_NAME = "resourceName";

    private static final String RESOURCE_TYPE = "resourceType";

    private static final String RESOURCE_PACKAGE = "resourcePackage";

    @DataPoints
    public static StaticResourceTest.LegalStaticResource[] legalStaticResources = new StaticResourceTest.LegalStaticResource[]{ new StaticResourceTest.LegalStaticResource("@layout/layoutX", "layoutX", "layout", null), new StaticResourceTest.LegalStaticResource("@android:layout/layoutY", "layoutY", "layout", "android"), new StaticResourceTest.LegalStaticResource("@com.somecompany.widget:layout/layoutY", "layoutY", "layout", "com.somecompany.widget") };

    @Test
    public void givenResourceNameTypeAndPackage_thenGetResourceIdFromContextResources() {
        MockResourcesBuilder aContextOfResources = MockResourcesBuilder.aContextOfResources();
        int expectedResourceId = aContextOfResources.declareResource(StaticResourceTest.RESOURCE_NAME, StaticResourceTest.RESOURCE_TYPE, StaticResourceTest.RESOURCE_PACKAGE);
        StaticResource resource = new StaticResource(StaticResourceTest.resourceValue(StaticResourceTest.RESOURCE_NAME, StaticResourceTest.RESOURCE_TYPE, StaticResourceTest.RESOURCE_PACKAGE));
        Assert.assertThat(resource.getResourceId(aContextOfResources.build()), CoreMatchers.equalTo(expectedResourceId));
    }

    @Test
    public void givenOnlyResourceNameAndType_thenUseContextPackageToGetResourceId() {
        MockResourcesBuilder aContextOfResources = MockResourcesBuilder.aContextOfResources();
        int expectedResourceId = aContextOfResources.withDefaultPackage(StaticResourceTest.RESOURCE_PACKAGE).declareResource(StaticResourceTest.RESOURCE_NAME, StaticResourceTest.RESOURCE_TYPE, StaticResourceTest.RESOURCE_PACKAGE);
        StaticResource resource = new StaticResource(StaticResourceTest.resourceValue(StaticResourceTest.RESOURCE_NAME, StaticResourceTest.RESOURCE_TYPE));
        Assert.assertThat(resource.getResourceId(aContextOfResources.build()), CoreMatchers.equalTo(expectedResourceId));
    }

    @Test(expected = RuntimeException.class)
    public void givenAResourceThatDoesNotExist_thenThrowRuntimeExceptionWhenGettingResourceId() {
        Resources resources = Mockito.mock(Resources.class);
        Mockito.when(resources.getIdentifier(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(0);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getResources()).thenReturn(resources);
        StaticResource resource = new StaticResource("@layout/non_existent_resource");
        resource.getResourceId(context);
    }

    static class LegalStaticResource {
        final String value;

        private final String expectedName;

        private final String expectedType;

        private final String expectedPackage;

        public LegalStaticResource(String value, String expectedName, String expectedType, String expectedPackage) {
            this.value = value;
            this.expectedName = expectedName;
            this.expectedType = expectedType;
            this.expectedPackage = expectedPackage;
        }

        public void assertPointToSameResource(StaticResource resource) {
            MockResourcesBuilder aContextOfResources = MockResourcesBuilder.aContextOfResources();
            int expectedResourceId = aContextOfResources.declareResource(expectedName, expectedType, expectedPackage);
            Assert.assertThat(resource.getResourceId(aContextOfResources.build()), CoreMatchers.equalTo(expectedResourceId));
        }
    }
}

