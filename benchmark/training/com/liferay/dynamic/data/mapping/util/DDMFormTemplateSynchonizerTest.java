/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.dynamic.data.mapping.util;


import DDMFormSerializerSerializeRequest.Builder;
import DDMTemplateConstants.TEMPLATE_MODE_CREATE;
import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.internal.util.DDMFormTemplateSynchonizer;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.impl.DDMTemplateLocalServiceImpl;
import com.liferay.dynamic.data.mapping.service.util.ServiceProps;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.util.PropsValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Marcellus Tavares
 */
@PrepareForTest({ LocaleUtil.class, PropsValues.class, ServiceProps.class })
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil", "com.liferay.portal.kernel.xml.SAXReaderUtil", "com.liferay.portal.util.PropsValues" })
public class DDMFormTemplateSynchonizerTest extends BaseDDMTestCase {
    @Test
    public void testAddRequiredFieldsIfTheyWereAddedInStructureAndModeIsCreate() throws Exception {
        DDMForm structureDDMForm = createDDMForm();
        addDDMFormField(structureDDMForm, createTextDDMFormField("name1", false));
        createFormTemplates(structureDDMForm);
        addDDMFormField(structureDDMForm, createTextDDMFormField("name2", true));
        DDMFormTemplateSynchonizer ddmFormTemplateSynchonizer = new DDMFormTemplateSynchonizerTest.MockDDMFormTemplateSynchronizer(structureDDMForm);
        ddmFormTemplateSynchonizer.synchronize();
        testFormTemplatesAfterAddRequiredFields();
    }

    @Test
    public void testRemoveTemplateFieldsIfTheyWereRemovedFromStructure() throws Exception {
        DDMForm structureDDMForm = createDDMForm();
        addDDMFormField(structureDDMForm, createTextDDMFormField("name1", false));
        addDDMFormField(structureDDMForm, createTextDDMFormField("name2", false));
        createFormTemplates(structureDDMForm);
        removeDDMFormField(structureDDMForm, "name1");
        DDMFormTemplateSynchonizer ddmFormTemplateSynchonizer = new DDMFormTemplateSynchonizerTest.MockDDMFormTemplateSynchronizer(structureDDMForm);
        ddmFormTemplateSynchonizer.synchronize();
        testFormTemplatesAfterRemoveFields();
    }

    @Test
    public void testUpdateDDMFormFieldOptionsIfTheyWereModifiedFromStructure() throws Exception {
        DDMForm structureDDMForm = createDDMForm();
        DDMFormField selectDDMFormField = createSelectDDMFormField("Select", false, 2);
        addDDMFormField(structureDDMForm, selectDDMFormField);
        DDMFormField radioDDMFormField = createSelectDDMFormField("Radio", true, 3);
        addDDMFormField(structureDDMForm, radioDDMFormField);
        createFormTemplates(structureDDMForm);
        selectDDMFormField.setDDMFormFieldOptions(createDDMFormFieldOptions(1));
        radioDDMFormField.setDDMFormFieldOptions(createDDMFormFieldOptions(2));
        DDMFormTemplateSynchonizer ddmFormTemplateSynchonizer = new DDMFormTemplateSynchonizerTest.MockDDMFormTemplateSynchronizer(structureDDMForm);
        ddmFormTemplateSynchonizer.synchronize();
        testFormTemplatesAfterChangeDDMFormFieldOptionsProperty(structureDDMForm, "Select", "Radio");
    }

    @Test
    public void testUpdateRequiredPropertyIfTemplateModeIsCreate() throws Exception {
        DDMForm structureDDMForm = createDDMForm();
        addDDMFormField(structureDDMForm, createTextDDMFormField("name1", false));
        addDDMFormField(structureDDMForm, createTextDDMFormField("name2", true));
        createFormTemplates(structureDDMForm);
        updateDDMFormFieldRequiredProperty(structureDDMForm, "name1", true);
        updateDDMFormFieldRequiredProperty(structureDDMForm, "name2", false);
        DDMFormTemplateSynchonizer ddmFormTemplateSynchonizer = new DDMFormTemplateSynchonizerTest.MockDDMFormTemplateSynchronizer(structureDDMForm);
        ddmFormTemplateSynchonizer.synchronize();
        testFormTemplatesAfterUpdateRequiredFieldProperties();
    }

    private DDMTemplate _createDDMTemplate;

    private DDMTemplate _editDDMTemplate;

    private class MockDDMFormTemplateSynchronizer extends DDMFormTemplateSynchonizer {
        public MockDDMFormTemplateSynchronizer(DDMForm structureDDMForm) {
            super(structureDDMForm, ddmFormJSONDeserializer, ddmFormJSONSerializer, new DDMTemplateLocalServiceImpl());
        }

        @Override
        protected List<DDMTemplate> getDDMFormTemplates() {
            List<DDMTemplate> ddmFormTemplates = new ArrayList<>();
            ddmFormTemplates.add(_createDDMTemplate);
            ddmFormTemplates.add(_editDDMTemplate);
            return ddmFormTemplates;
        }

        @Override
        protected void updateDDMTemplate(DDMTemplate ddmTemplate, DDMForm templateDDMForm) {
            DDMFormSerializerSerializeRequest.Builder builder = Builder.newBuilder(templateDDMForm);
            DDMFormSerializerSerializeResponse ddmFormSerializerSerializeResponse = ddmFormJSONSerializer.serialize(builder.build());
            String script = ddmFormSerializerSerializeResponse.getContent();
            ddmTemplate.setScript(script);
            if (Objects.equals(ddmTemplate.getMode(), TEMPLATE_MODE_CREATE)) {
                _createDDMTemplate = ddmTemplate;
            } else {
                _editDDMTemplate = ddmTemplate;
            }
        }
    }
}

