/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.editor.language.xml;


import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;


/**
 *
 *
 * @see <a href="https://activiti.atlassian.net/browse/ACT-2055">https://activiti.atlassian.net/browse/ACT-2055</a>
 */
public class SubProcessWithExtensionsConverterTest extends AbstractConverterTest {
    protected static final String YOURCO_EXTENSIONS_NAMESPACE = "http://yourco/bpmn";

    protected static final String YOURCO_EXTENSIONS_PREFIX = "yourco";

    protected static final String ELEMENT_ATTRIBUTES = "attributes";

    protected static final String ELEMENT_ATTRIBUTE = "attribute";

    protected static final String ATTRIBUTE_NAME = "name";

    protected static final String ATTRIBUTE_VALUE = "value";

    protected static final String ELEMENT_I18LN_LOCALIZATION = "i18ln";

    protected static final String ATTRIBUTE_RESOURCE_BUNDLE_KEY_FOR_NAME = "resourceBundleKeyForName";

    protected static final String ATTRIBUTE_RESOURCE_BUNDLE_KEY_FOR_DESCRIPTION = "resourceBundleKeyForDescription";

    protected static final String ATTRIBUTE_LABELED_ENTITY_ID_FOR_NAME = "labeledEntityIdForName";

    protected static final String ATTRIBUTE_LABELED_ENTITY_ID_FOR_DESCRIPTION = "labeledEntityIdForDescription";

    private SubProcessWithExtensionsConverterTest.Localization localization = new SubProcessWithExtensionsConverterTest.Localization();

    /* Inner class used to hold localization DataObject extension values */
    public class Localization {
        private String resourceBundleKeyForName;

        private String resourceBundleKeyForDescription;

        private String labeledEntityIdForName;

        private String labeledEntityIdForDescription;

        public String getResourceBundleKeyForName() {
            return resourceBundleKeyForName;
        }

        public void setResourceBundleKeyForName(String resourceBundleKeyForName) {
            this.resourceBundleKeyForName = resourceBundleKeyForName;
        }

        public String getResourceBundleKeyForDescription() {
            return resourceBundleKeyForDescription;
        }

        public void setResourceBundleKeyForDescription(String resourceBundleKeyForDescription) {
            this.resourceBundleKeyForDescription = resourceBundleKeyForDescription;
        }

        public String getLabeledEntityIdForName() {
            return labeledEntityIdForName;
        }

        public void setLabeledEntityIdForName(String labeledEntityIdForName) {
            this.labeledEntityIdForName = labeledEntityIdForName;
        }

        public String getLabeledEntityIdForDescription() {
            return labeledEntityIdForDescription;
        }

        public void setLabeledEntityIdForDescription(String labeledEntityIdForDescription) {
            this.labeledEntityIdForDescription = labeledEntityIdForDescription;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(100);
            sb.append("Localization: [");
            sb.append("resourceBundleKeyForName=").append(resourceBundleKeyForName);
            sb.append(", resourceBundleKeyForDescription=").append(resourceBundleKeyForDescription);
            sb.append(", labeledEntityIdForName=").append(labeledEntityIdForName);
            sb.append(", labeledEntityIdForDescription=").append(labeledEntityIdForDescription);
            sb.append("]");
            return sb.toString();
        }
    }

    /* End of inner classes */
    @Test
    public void convertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        BpmnModel parsedModel = exportAndReadXMLFile(bpmnModel);
        validateModel(parsedModel);
    }
}

