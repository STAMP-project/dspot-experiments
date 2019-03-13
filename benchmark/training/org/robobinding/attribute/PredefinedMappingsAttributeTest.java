package org.robobinding.attribute;


import android.content.Context;
import android.content.res.Resources;
import com.google.common.collect.Lists;
import java.util.Collection;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robobinding.PredefinedPendingAttributesForView;
import org.robobinding.attribute.PredefinedMappingsAttribute.ViewMapping;
import org.robobinding.widget.adapterview.AbstractAdaptedDataSetAttributes;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
@RunWith(Theories.class)
public class PredefinedMappingsAttributeTest {
    private static final String MAPPING_ATTRIBUTE_VALUE = "[text1.text:{property}]";

    private static final int TEXT_1_ID = 10;

    private static final int TEXT_2_ID = 20;

    @DataPoints
    public static PredefinedMappingsAttributeTest.MappingExpectation[] mappingExpections = new PredefinedMappingsAttributeTest.MappingExpectation[]{ PredefinedMappingsAttributeTest.attribute("[text1.text:{title}]").shouldMapTo(PredefinedMappingsAttributeTest.viewMapping("text1", PredefinedMappingsAttributeTest.TEXT_1_ID, "text", "{title}")), PredefinedMappingsAttributeTest.attribute("[text2.text:{artist}]").shouldMapTo(PredefinedMappingsAttributeTest.viewMapping("text2", PredefinedMappingsAttributeTest.TEXT_2_ID, "text", "{artist}")), PredefinedMappingsAttributeTest.attribute("[text1.text:{title}, text2.text:{artist}]").shouldMapTo(PredefinedMappingsAttributeTest.viewMapping("text1", PredefinedMappingsAttributeTest.TEXT_1_ID, "text", "{title}"), PredefinedMappingsAttributeTest.viewMapping("text2", PredefinedMappingsAttributeTest.TEXT_2_ID, "text", "{artist}")), PredefinedMappingsAttributeTest.attribute("[text1.visibility:{titleVisible},text2.enabled:{artistEnabled}]").shouldMapTo(PredefinedMappingsAttributeTest.viewMapping("text1", PredefinedMappingsAttributeTest.TEXT_1_ID, "visibility", "{titleVisible}"), PredefinedMappingsAttributeTest.viewMapping("text2", PredefinedMappingsAttributeTest.TEXT_2_ID, "enabled", "{artistEnabled}")) };

    @DataPoints
    public static String[] illegalAttributeValues = new String[]{ "[text1.text: {title}", "text:{title}", "[text1.text:title]", "[text1.text:{title}],text2.text:{artist}]", "[text1.text:{title},text2..text:{artist}]" };

    @Mock
    Context context;

    @Mock
    Resources resources;

    @Test(expected = MalformedAttributeException.class)
    public void givenALegalAttributeValue_whenViewCantBeFound_thenThrowException() {
        PredefinedMappingsAttribute predefinedMappingsAttribute = new PredefinedMappingsAttribute(AbstractAdaptedDataSetAttributes.ITEM_MAPPING, PredefinedMappingsAttributeTest.MAPPING_ATTRIBUTE_VALUE);
        Mockito.when(resources.getIdentifier("text1", "id", "android")).thenReturn(0);
        predefinedMappingsAttribute.getViewMappings(context);
    }

    private static class MappingExpectation {
        final String attributeValue;

        final Collection<PredefinedPendingAttributesForView> expectedViewMappings;

        final PredefinedMappingsAttributeTest.ViewMappingData[] viewMappingData;

        public MappingExpectation(String attributeValue, PredefinedMappingsAttributeTest.ViewMappingData[] viewMappings) {
            this.attributeValue = attributeValue;
            viewMappingData = viewMappings;
            this.expectedViewMappings = Lists.newArrayList();
            for (PredefinedMappingsAttributeTest.ViewMappingData viewMappingData : viewMappings) {
                expectedViewMappings.add(new ViewMapping(viewMappingData.viewId, viewMappingData.attributeName, viewMappingData.attributeValue));
            }
        }
    }

    private static class Attribute {
        private final String attributeValue;

        public Attribute(String attributeValue) {
            this.attributeValue = attributeValue;
        }

        public PredefinedMappingsAttributeTest.MappingExpectation shouldMapTo(PredefinedMappingsAttributeTest.ViewMappingData... viewMappings) {
            return new PredefinedMappingsAttributeTest.MappingExpectation(attributeValue, viewMappings);
        }
    }

    private static class ViewMappingData {
        private final String viewName;

        private final int viewId;

        private final String attributeName;

        private final String attributeValue;

        public ViewMappingData(String viewName, int viewId, String attributeName, String attributeValue) {
            this.viewName = viewName;
            this.viewId = viewId;
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
        }
    }
}

