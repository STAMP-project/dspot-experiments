/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.core.v1_2;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DMN12specificTest extends BaseInterpretedVsCompiledTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMN12specificTest.class);

    public DMN12specificTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testDMN12typeAliases() {
        // DROOLS-
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("typeAliases.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_9f6be450-17c0-49d9-a67f-960ad04b046f", "Drawing 1");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("a date and time", LocalDateTime.of(2018, 9, 28, 16, 7));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMN12specificTest.LOG.debug("{}", dmnResult);
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), CoreMatchers.is(false));
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("a decision"), CoreMatchers.is(LocalDateTime.of(2018, 9, 28, 16, 7).plusDays(1)));
    }

    @Test
    public void testItemDefCollection() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-filter.dmn", getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_f52ca843-504b-4c3b-a6bc-4d377bffef7a", "filter01");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(dmnModel.getMessages().toString(), dmnModel.hasErrors(), CoreMatchers.is(false));
        final Object[][] data = new Object[][]{ new Object[]{ 7792, 10, "Clark" }, new Object[]{ 7934, 10, "Miller" }, new Object[]{ 7976, 20, "Adams" }, new Object[]{ 7902, 20, "Ford" }, new Object[]{ 7900, 30, "James" } };
        final List<Map<String, Object>> employees = new ArrayList<>();
        for (Object[] aData : data) {
            final Map<String, Object> e = new HashMap<>();
            e.put("id", aData[0]);
            e.put("dept", aData[1]);
            e.put("name", aData[2]);
            employees.add(e);
        }
        final DMNContext context = DMNFactory.newContext();
        context.set("Employees", employees);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMN12specificTest.LOG.debug("{}", dmnResult);
        Assert.assertThat(dmnResult.hasErrors(), CoreMatchers.is(false));
        Assert.assertThat(dmnResult.getContext().get("filter01"), CoreMatchers.is(Arrays.asList("Adams", "Ford")));
    }

    @Test
    public void testDMN12typeRefInformationItem() {
        // DROOLS-3544
        check_testDMN12typeRefInformationItem("typeRefInformationItem_original.dmn");
    }

    @Test
    public void testDMN12typeRefInformationItem_modified() {
        // DROOLS-3544
        check_testDMN12typeRefInformationItem("typeRefInformationItem_modified.dmn");
    }
}

