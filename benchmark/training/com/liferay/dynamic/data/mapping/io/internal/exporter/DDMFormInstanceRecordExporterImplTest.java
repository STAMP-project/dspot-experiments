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
package com.liferay.dynamic.data.mapping.io.internal.exporter;


import DDMFormInstanceRecordExporterRequest.Builder;
import QueryUtil.ALL_POS;
import StringPool.BLANK;
import WorkflowConstants.STATUS_APPROVED;
import com.liferay.dynamic.data.mapping.exception.FormInstanceRecordExporterException;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesTracker;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterRequest;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterResponse;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriter;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterRequest;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterResponse;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterTracker;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.OrderByComparator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;


/**
 *
 *
 * @author Leonardo Barros
 */
@RunWith(MockitoJUnitRunner.class)
public class DDMFormInstanceRecordExporterImplTest extends PowerMockito {
    @Test
    public void testExport() throws Exception {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = mock(DDMFormInstanceRecordExporterImpl.class);
        ddmFormInstanceRecordExporter.ddmFormInstanceRecordLocalService = _ddmFormInstanceRecordLocalService;
        DDMFormInstanceRecordExporterRequest.Builder builder = Builder.newBuilder(1, "csv");
        OrderByComparator<DDMFormInstanceRecord> orderByComparator = mock(OrderByComparator.class);
        Locale locale = new Locale("pt", "BR");
        DDMFormInstanceRecordExporterRequest ddmFormInstanceRecordExporterRequest = builder.withStatus(STATUS_APPROVED).withLocale(locale).withStart(1).withEnd(5).withOrderByComparator(orderByComparator).build();
        List<DDMFormInstanceRecord> ddmFormInstanceRecords = Collections.emptyList();
        when(_ddmFormInstanceRecordLocalService.getFormInstanceRecords(1, STATUS_APPROVED, 1, 5, orderByComparator)).thenReturn(ddmFormInstanceRecords);
        Map<String, DDMFormField> ddmFormFields = Collections.emptyMap();
        when(ddmFormInstanceRecordExporter.getDistinctFields(1)).thenReturn(ddmFormFields);
        Map<String, String> ddmFormFieldsLabel = Collections.emptyMap();
        when(ddmFormInstanceRecordExporter.getDDMFormFieldsLabel(ddmFormFields, locale)).thenReturn(ddmFormFieldsLabel);
        List<Map<String, String>> ddmFormFieldsValues = Collections.emptyList();
        when(ddmFormInstanceRecordExporter.getDDMFormFieldValues(ddmFormFields, ddmFormInstanceRecords, locale)).thenReturn(ddmFormFieldsValues);
        when(ddmFormInstanceRecordExporter.write("csv", ddmFormFieldsLabel, ddmFormFieldsValues)).thenReturn(new byte[]{ 1, 2, 3 });
        when(ddmFormInstanceRecordExporter.export(ddmFormInstanceRecordExporterRequest)).thenCallRealMethod();
        DDMFormInstanceRecordExporterResponse ddmFormInstanceRecordExporterResponse = ddmFormInstanceRecordExporter.export(ddmFormInstanceRecordExporterRequest);
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3 }, ddmFormInstanceRecordExporterResponse.getContent());
        InOrder inOrder = Mockito.inOrder(_ddmFormInstanceRecordLocalService, ddmFormInstanceRecordExporter);
        inOrder.verify(_ddmFormInstanceRecordLocalService, Mockito.times(1)).getFormInstanceRecords(1, STATUS_APPROVED, 1, 5, orderByComparator);
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).getDistinctFields(1);
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).getDDMFormFieldsLabel(ddmFormFields, locale);
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).getDDMFormFieldValues(ddmFormFields, ddmFormInstanceRecords, locale);
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).write("csv", ddmFormFieldsLabel, ddmFormFieldsValues);
    }

    @Test(expected = FormInstanceRecordExporterException.class)
    public void testExportCatchException() throws Exception {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = new DDMFormInstanceRecordExporterImpl();
        ddmFormInstanceRecordExporter.ddmFormInstanceRecordLocalService = _ddmFormInstanceRecordLocalService;
        when(_ddmFormInstanceRecordLocalService.getFormInstanceRecords(Matchers.anyLong(), Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt(), Matchers.any(OrderByComparator.class))).thenThrow(Exception.class);
        DDMFormInstanceRecordExporterRequest.Builder builder = Builder.newBuilder(1, "csv");
        ddmFormInstanceRecordExporter.export(builder.build());
    }

    @Test
    public void testFormatDate() {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = new DDMFormInstanceRecordExporterImpl();
        DateTimeFormatter dateTimeFormatter = ddmFormInstanceRecordExporter.getDateTimeFormatter(new Locale("pt", "BR"));
        LocalDate localDate = LocalDate.of(2018, 2, 1);
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);
        String actual = ddmFormInstanceRecordExporter.formatDate(date, dateTimeFormatter);
        Assert.assertEquals("01/02/18 00:00", actual);
    }

    @Test
    public void testGetDateTimeFormatter() {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = new DDMFormInstanceRecordExporterImpl();
        DateTimeFormatter dateTimeFormatter = ddmFormInstanceRecordExporter.getDateTimeFormatter(new Locale("pt", "BR"));
        Assert.assertEquals("Localized(SHORT,SHORT)", dateTimeFormatter.toString());
    }

    @Test
    public void testGetDDMFormFieldsLabel() {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = new DDMFormInstanceRecordExporterImpl();
        Locale locale = new Locale("pt", "BR");
        when(_language.get(locale, "status")).thenReturn("Estado");
        when(_language.get(locale, "modified-date")).thenReturn("Data de Modifica??o");
        when(_language.get(locale, "author")).thenReturn("Autor");
        DDMFormField ddmFormField1 = new DDMFormField("field1", "text");
        LocalizedValue localizedValue1 = new LocalizedValue();
        localizedValue1.addString(locale, "Campo 1");
        ddmFormField1.setLabel(localizedValue1);
        DDMFormField ddmFormField2 = new DDMFormField("field2", "text");
        LocalizedValue localizedValue2 = new LocalizedValue();
        localizedValue2.addString(locale, "Campo 2");
        ddmFormField2.setLabel(localizedValue2);
        Map<String, DDMFormField> ddmFormFieldMap = new HashMap<>();
        ddmFormFieldMap.put("field1", ddmFormField1);
        ddmFormFieldMap.put("field2", ddmFormField2);
        Map<String, String> ddmFormFieldsLabel = ddmFormInstanceRecordExporter.getDDMFormFieldsLabel(ddmFormFieldMap, locale);
        Assert.assertEquals("Campo 1", ddmFormFieldsLabel.get("field1"));
        Assert.assertEquals("Campo 2", ddmFormFieldsLabel.get("field2"));
        Assert.assertEquals("Estado", ddmFormFieldsLabel.get("status"));
        Assert.assertEquals("Data de Modifica??o", ddmFormFieldsLabel.get("modifiedDate"));
        Assert.assertEquals("Autor", ddmFormFieldsLabel.get("author"));
    }

    @Test
    public void testGetDDMFormFieldValue() throws Exception {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = new DDMFormInstanceRecordExporterImpl();
        ddmFormInstanceRecordExporter.ddmFormFieldTypeServicesTracker = _ddmFormFieldTypeServicesTracker;
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = mock(DDMFormFieldValueRenderer.class);
        DDMFormField ddmFormField = new DDMFormField("field1", "text");
        Map<String, List<DDMFormFieldValue>> ddmFormFieldValueMap = new HashMap<>();
        List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("field1", new UnlocalizedValue("value1"));
        ddmFormFieldValues.add(ddmFormFieldValue);
        ddmFormFieldValueMap.put("field1", ddmFormFieldValues);
        Locale locale = new Locale("pt", "BR");
        when(_ddmFormFieldTypeServicesTracker.getDDMFormFieldValueRenderer("text")).thenReturn(ddmFormFieldValueRenderer);
        when(ddmFormFieldValueRenderer.render(ddmFormFieldValue, locale)).thenReturn("value1");
        when(_html.render("value1")).thenReturn("value1");
        String actualValue = ddmFormInstanceRecordExporter.getDDMFormFieldValue(ddmFormField, ddmFormFieldValueMap, locale);
        Assert.assertEquals("value1", actualValue);
        Mockito.verify(_ddmFormFieldTypeServicesTracker, Mockito.times(1)).getDDMFormFieldValueRenderer("text");
        Mockito.verify(ddmFormFieldValueRenderer, Mockito.times(1)).render(ddmFormFieldValue, locale);
        Mockito.verify(_html, Mockito.times(1)).render("value1");
    }

    @Test
    public void testGetDDMFormFieldValues() throws Exception {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = mock(DDMFormInstanceRecordExporterImpl.class);
        Locale locale = new Locale("pt", "BR");
        List<DDMFormInstanceRecord> ddmFormInstanceRecords = new ArrayList<>();
        DDMFormInstanceRecord ddmFormInstanceRecord = mock(DDMFormInstanceRecord.class);
        ddmFormInstanceRecords.add(ddmFormInstanceRecord);
        Map<String, DDMFormField> ddmFormFields = new HashMap<>();
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField1 = new DDMFormField("field1", "text");
        ddmForm.addDDMFormField(ddmFormField1);
        DDMFormField ddmFormField2 = new DDMFormField("field2", "text");
        ddmForm.addDDMFormField(ddmFormField2);
        ddmFormFields.put("field1", ddmFormField1);
        ddmFormFields.put("field2", ddmFormField2);
        DDMFormValues ddmFormValues1 = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        DDMFormFieldValue ddmFormFieldValue2 = DDMFormValuesTestUtil.createDDMFormFieldValue("field2", new UnlocalizedValue("value2"));
        ddmFormValues1.addDDMFormFieldValue(ddmFormFieldValue2);
        DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion = mock(DDMFormInstanceRecordVersion.class);
        DateTimeFormatter dateTimeFormatter = mock(DateTimeFormatter.class);
        when(ddmFormInstanceRecordExporter.getDateTimeFormatter(locale)).thenReturn(dateTimeFormatter);
        when(ddmFormInstanceRecord.getDDMFormValues()).thenReturn(ddmFormValues1);
        when(ddmFormInstanceRecordExporter.getDDMFormFieldValue(Matchers.any(DDMFormField.class), Matchers.anyMap(), Matchers.any(Locale.class))).thenReturn("value");
        when(ddmFormInstanceRecord.getFormInstanceRecordVersion()).thenReturn(ddmFormInstanceRecordVersion);
        when(ddmFormInstanceRecordVersion.getStatus()).thenReturn(STATUS_APPROVED);
        when(ddmFormInstanceRecordVersion.getStatusDate()).thenReturn(new Date());
        when(ddmFormInstanceRecordVersion.getUserName()).thenReturn("User Name");
        when(ddmFormInstanceRecordExporter.getStatusMessage(Matchers.anyInt(), Matchers.any(Locale.class))).thenReturn("aprovado");
        when(ddmFormInstanceRecordExporter.formatDate(Matchers.any(Date.class), Matchers.any(DateTimeFormatter.class))).thenReturn("01/02/2018 00:00");
        when(ddmFormInstanceRecordExporter.getDDMFormFieldValues(ddmFormFields, ddmFormInstanceRecords, locale)).thenCallRealMethod();
        List<Map<String, String>> ddmFormFieldValues = ddmFormInstanceRecordExporter.getDDMFormFieldValues(ddmFormFields, ddmFormInstanceRecords, locale);
        Map<String, String> valuesMap = ddmFormFieldValues.get(0);
        Assert.assertEquals(BLANK, valuesMap.get("field1"));
        Assert.assertEquals("value", valuesMap.get("field2"));
        Assert.assertEquals("aprovado", valuesMap.get("status"));
        Assert.assertEquals("01/02/2018 00:00", valuesMap.get("modifiedDate"));
        Assert.assertEquals("User Name", valuesMap.get("author"));
        InOrder inOrder = Mockito.inOrder(ddmFormInstanceRecordExporter, ddmFormInstanceRecord, ddmFormInstanceRecordVersion);
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).getDateTimeFormatter(locale);
        inOrder.verify(ddmFormInstanceRecord, Mockito.times(1)).getDDMFormValues();
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).getDDMFormFieldValue(Matchers.any(DDMFormField.class), Matchers.anyMap(), Matchers.any(Locale.class));
        inOrder.verify(ddmFormInstanceRecord, Mockito.times(1)).getFormInstanceRecordVersion();
        inOrder.verify(ddmFormInstanceRecordVersion, Mockito.times(1)).getStatus();
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).getStatusMessage(Matchers.anyInt(), Matchers.any(Locale.class));
        inOrder.verify(ddmFormInstanceRecordVersion, Mockito.times(1)).getStatusDate();
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).formatDate(Matchers.any(Date.class), Matchers.any(DateTimeFormatter.class));
    }

    @Test
    public void testGetDistinctFields() throws Exception {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = mock(DDMFormInstanceRecordExporterImpl.class);
        DDMStructureVersion ddmStructureVersion = mock(DDMStructureVersion.class);
        List<DDMStructureVersion> ddmStructureVersions = new ArrayList<>();
        ddmStructureVersions.add(ddmStructureVersion);
        when(ddmFormInstanceRecordExporter.getStructureVersions(1L)).thenReturn(ddmStructureVersions);
        Map<String, DDMFormField> ddmFormFields = new LinkedHashMap<>();
        DDMFormField ddmFormField1 = new DDMFormField("field1", "text");
        DDMFormField ddmFormField2 = new DDMFormField("field2", "text");
        ddmFormFields.put("field1", ddmFormField1);
        ddmFormFields.put("field2", ddmFormField2);
        when(ddmFormInstanceRecordExporter.getNontransientDDMFormFieldsMap(ddmStructureVersion)).thenReturn(ddmFormFields);
        when(ddmFormInstanceRecordExporter.getDistinctFields(1L)).thenCallRealMethod();
        Map<String, DDMFormField> distinctFields = ddmFormInstanceRecordExporter.getDistinctFields(1);
        Assert.assertEquals(ddmFormField1, distinctFields.get("field1"));
        Assert.assertEquals(ddmFormField2, distinctFields.get("field2"));
        InOrder inOrder = Mockito.inOrder(ddmFormInstanceRecordExporter);
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).getStructureVersions(1);
        inOrder.verify(ddmFormInstanceRecordExporter, Mockito.times(1)).getNontransientDDMFormFieldsMap(ddmStructureVersion);
    }

    @Test
    public void testGetNontransientDDMFormFieldsMap() {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = new DDMFormInstanceRecordExporterImpl();
        DDMStructureVersion ddmStructureVersion = mock(DDMStructureVersion.class);
        DDMForm ddmForm = mock(DDMForm.class);
        when(ddmStructureVersion.getDDMForm()).thenReturn(ddmForm);
        ddmFormInstanceRecordExporter.getNontransientDDMFormFieldsMap(ddmStructureVersion);
        InOrder inOrder = Mockito.inOrder(ddmStructureVersion, ddmForm);
        inOrder.verify(ddmStructureVersion, Mockito.times(1)).getDDMForm();
        inOrder.verify(ddmForm, Mockito.times(1)).getNontransientDDMFormFieldsMap(true);
    }

    @Test
    public void testGetStatusMessage() {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = new DDMFormInstanceRecordExporterImpl();
        Locale locale = new Locale("pt", "BR");
        when(_language.get(locale, "approved")).thenReturn("approvado");
        String statusMessage = ddmFormInstanceRecordExporter.getStatusMessage(STATUS_APPROVED, locale);
        Assert.assertEquals("approvado", statusMessage);
        Mockito.verify(_language, Mockito.times(1)).get(locale, "approved");
    }

    @Test
    public void testGetStructureVersions() throws Exception {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = new DDMFormInstanceRecordExporterImpl();
        ddmFormInstanceRecordExporter.ddmFormInstanceVersionLocalService = _ddmFormInstanceVersionLocalService;
        List<DDMFormInstanceVersion> ddmFormInstanceVersions = new ArrayList();
        DDMFormInstanceVersion ddmFormInstanceVersion = mock(DDMFormInstanceVersion.class);
        ddmFormInstanceVersions.add(ddmFormInstanceVersion);
        when(_ddmFormInstanceVersionLocalService.getFormInstanceVersions(1, ALL_POS, ALL_POS, null)).thenReturn(ddmFormInstanceVersions);
        DDMStructureVersion ddmStructureVersion = mock(DDMStructureVersion.class);
        when(ddmFormInstanceVersion.getStructureVersion()).thenReturn(ddmStructureVersion);
        List<DDMStructureVersion> structureVersions = ddmFormInstanceRecordExporter.getStructureVersions(1);
        Assert.assertEquals(ddmStructureVersion, structureVersions.get(0));
        InOrder inOrder = Mockito.inOrder(_ddmFormInstanceVersionLocalService, ddmFormInstanceVersion);
        inOrder.verify(_ddmFormInstanceVersionLocalService, Mockito.times(1)).getFormInstanceVersions(1, ALL_POS, ALL_POS, null);
        inOrder.verify(ddmFormInstanceVersion, Mockito.times(1)).getStructureVersion();
    }

    @Test
    public void testWrite() throws Exception {
        DDMFormInstanceRecordExporterImpl ddmFormInstanceRecordExporter = new DDMFormInstanceRecordExporterImpl();
        ddmFormInstanceRecordExporter.ddmFormInstanceRecordWriterTracker = _ddmFormInstanceRecordWriterTracker;
        DDMFormInstanceRecordWriter ddmFormInstanceRecordWriter = mock(DDMFormInstanceRecordWriter.class);
        when(_ddmFormInstanceRecordWriterTracker.getDDMFormInstanceRecordWriter("txt")).thenReturn(ddmFormInstanceRecordWriter);
        DDMFormInstanceRecordWriterResponse.Builder builder = DDMFormInstanceRecordWriterResponse.Builder.newBuilder(new byte[]{ 1, 2, 3 });
        when(ddmFormInstanceRecordWriter.write(Matchers.any(DDMFormInstanceRecordWriterRequest.class))).thenReturn(builder.build());
        byte[] content = ddmFormInstanceRecordExporter.write("txt", Collections.emptyMap(), Collections.emptyList());
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3 }, content);
        InOrder inOrder = Mockito.inOrder(_ddmFormInstanceRecordWriterTracker, ddmFormInstanceRecordWriter);
        inOrder.verify(_ddmFormInstanceRecordWriterTracker, Mockito.times(1)).getDDMFormInstanceRecordWriter("txt");
        inOrder.verify(ddmFormInstanceRecordWriter, Mockito.times(1)).write(Matchers.any(DDMFormInstanceRecordWriterRequest.class));
    }

    @Mock
    private DDMFormFieldTypeServicesTracker _ddmFormFieldTypeServicesTracker;

    @Mock
    private DDMFormInstanceRecordLocalService _ddmFormInstanceRecordLocalService;

    @Mock
    private DDMFormInstanceRecordWriterTracker _ddmFormInstanceRecordWriterTracker;

    @Mock
    private DDMFormInstanceVersionLocalService _ddmFormInstanceVersionLocalService;

    @Mock
    private Html _html;

    @Mock
    private Language _language;
}

