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
package com.liferay.dynamic.data.mapping.form.values.query;


import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Marcellus Tavares
 */
@PrepareForTest(LocaleUtil.class)
@RunWith(PowerMockRunner.class)
public class DDMFormValuesQueryTest extends PowerMockito {
    @Test
    public void testSelectAllFromRootUsingMultipleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("//*");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 18, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectAllFromRootUsingSingleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("/*");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        Assert.assertEquals("text1", getDDMFormFieldValueFieldName(ddmFormFieldValues.get(0)));
        Assert.assertEquals("text2", getDDMFormFieldValueFieldName(ddmFormFieldValues.get(1)));
        Assert.assertEquals("text3", getDDMFormFieldValueFieldName(ddmFormFieldValues.get(2)));
    }

    @Test
    public void testSelectAllFromText1UsingSingleAndMultipleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("/text1//*");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 4, ddmFormFieldValues.size());
        Assert.assertEquals("text11", getDDMFormFieldValueFieldName(ddmFormFieldValues.get(0)));
        Assert.assertEquals("text11", getDDMFormFieldValueFieldName(ddmFormFieldValues.get(1)));
        Assert.assertEquals("text11", getDDMFormFieldValueFieldName(ddmFormFieldValues.get(2)));
        Assert.assertEquals("text12", getDDMFormFieldValueFieldName(ddmFormFieldValues.get(3)));
    }

    @Test
    public void testSelectAllFromText3UsingMultipleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("//text3//*");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 7, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectAllFromText3UsingSingleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("/text3/*");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 2, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectText2FromRootUsingSingleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("/text2");
        DDMFormFieldValue ddmFormFieldValue = ddmFormValuesQuery.selectSingleDDMFormFieldValue();
        Assert.assertEquals("text2", ddmFormFieldValue.getName());
    }

    @Test
    public void testSelectText12FromRootUsingMultipleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("//text12");
        DDMFormFieldValue ddmFormFieldValue = ddmFormValuesQuery.selectSingleDDMFormFieldValue();
        Assert.assertEquals("text12", ddmFormFieldValue.getName());
    }

    @Test
    public void testSelectText12FromRootUsingSingleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("/text12");
        DDMFormFieldValue ddmFormFieldValue = ddmFormValuesQuery.selectSingleDDMFormFieldValue();
        Assert.assertNull(ddmFormFieldValue);
    }

    @Test
    public void testSelectText311From31UsingMultipleAndSingleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("//text31/text313");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        Assert.assertEquals("text313", getDDMFormFieldValueFieldName(ddmFormFieldValues.get(0)));
    }

    @Test
    public void testSelectText311From31UsingMultipleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("//text313");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        Assert.assertEquals("text313", getDDMFormFieldValueFieldName(ddmFormFieldValues.get(0)));
    }

    @Test
    public void testSelectUsingAndExpressionAndSingleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery(("//*[@value('en_US') = 'En text22' and @value('pt_BR') = 'Pt " + "text22']"));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("//*[@value('en_US') = 'En text22' and @type = 'text']");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("//*[@value('en_US') = 'En text22' and @value('pt_BR') = 'wrong']");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 0, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectUsingAndExpressionForLocalizedFieldAndMultipleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery(("//*[@value('en_US') = 'En text22' and @value('pt_BR') = 'Pt " + "text22']"));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("//*[@value('en_US') = 'En text22' and @type = 'text']");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("//*[@value('en_US') = 'En text22' and @value('pt_BR') = 'wrong']");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 0, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectUsingLocalizedValueMatcherAndMultipleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("//*[@value('en_US') = 'En text22']");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("//*[@value('en_US') = 'Pt text22']");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 0, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectUsingLocalizedValueMatcherAndSingleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("/text3[@value('en_US') = 'text3']");
        DDMFormFieldValue ddmFormFieldValue = ddmFormValuesQuery.selectSingleDDMFormFieldValue();
        Assert.assertEquals("text3", getDDMFormFieldValueFieldName(ddmFormFieldValue));
    }

    @Test
    public void testSelectUsingMixedBooleanExpressionMatchers() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery(("//*[@value('en_US') = 'wrong' and @value('en_US') = 'wrong' or" + "@value('pt_BR') = 'Pt text22']"));
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectUsingMultipleAndSingleSteps() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("//text31/text311");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("//text3/text311");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 0, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("/text3//text311");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("/text31//text311");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 0, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("//*/text22");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectUsingOrPredicateMatchers() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("/*[@value('en_US') = 'En text22' or @value('pt_BR') = 'wrong']");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 0, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("/*[@value('en_US') = 'wrong' or @type = 'text']");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("/*[@value('en_US') = 'En text1' or @value('pt_BR') = 'wrong']");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectUsingTypePredicateMatcherAndMultipleStep() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("//*[@type = 'text']");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 18, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("/*[@type = 'date']");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 0, ddmFormFieldValues.size());
    }

    @Test
    public void testSelectUsingValuePredicateMatcherForLocalizedField() throws Exception {
        DDMFormValuesQuery ddmFormValuesQuery = createDDMFormValuesQuery("//*[@value = 'En text22']");
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
        ddmFormValuesQuery = createDDMFormValuesQuery("//*[@value = 'Pt text22']");
        ddmFormFieldValues = ddmFormValuesQuery.selectDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());
    }

    @Test(expected = DDMFormValuesQuerySyntaxException.class)
    public void testSyntaxError() throws Exception {
        createDDMFormValuesQuery("~/]");
    }

    private DDMFormValues _ddmFormValues;

    private DDMFormValuesQueryFactory _ddmFormValuesQueryFactory;
}

