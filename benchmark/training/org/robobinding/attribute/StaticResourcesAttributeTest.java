package org.robobinding.attribute;


import com.google.android.collect.Lists;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class StaticResourcesAttributeTest {
    @Test
    public void shouldGetResourceIds() {
        String name1 = "layoutX";
        String type1 = "layout";
        String name2 = "layoutY";
        String type2 = "layout";
        String package2 = "android";
        StaticResourcesAttribute attribute = new StaticResourcesAttribute("name", resourcesValue(StaticResourceTest.resourceValue(name1, type1), StaticResourceTest.resourceValue(name2, type2, package2)));
        MockResourcesBuilder aContextOfResources = MockResourcesBuilder.aContextOfResources().withDefaultPackage();
        int expectedResourceId1 = aContextOfResources.declareResource(name1, type1);
        int expectedResourceId2 = aContextOfResources.declareResource(name2, type2, package2);
        List<Integer> expectedResourceIds = Lists.newArrayList(expectedResourceId1, expectedResourceId2);
        List<Integer> resourceIds = attribute.getResourceIds(aContextOfResources.build());
        Assert.assertThat(resourceIds, Matchers.equalTo(expectedResourceIds));
    }
}

