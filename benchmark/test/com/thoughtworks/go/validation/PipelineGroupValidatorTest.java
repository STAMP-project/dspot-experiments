/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.validation;


import Validator.PIPELINEGROUP;
import com.thoughtworks.go.domain.materials.ValidationBean;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PipelineGroupValidatorTest {
    @Test
    public void shouldRejectEmptyPipelineGroup() {
        ValidationBean validationBean = PIPELINEGROUP.validate(null);
        Assert.assertThat(validationBean.isValid(), Matchers.is(true));
    }

    @Test
    public void shouldValidateNormalPipelineGroupName() {
        ValidationBean validationBean = PIPELINEGROUP.validate("pipelineGroupName");
        Assert.assertThat(validationBean.isValid(), Matchers.is(true));
    }

    @Test
    public void shouldRejectWhiteSpace() {
        ValidationBean validationBean = PIPELINEGROUP.validate("pipeline GroupName");
        Assert.assertThat(validationBean.isValid(), Matchers.is(false));
        Assert.assertThat(validationBean.getError(), Matchers.is(PipelineGroupValidator.ERRORR_MESSAGE));
    }

    @Test
    public void shouldRejectAmpersand() {
        ValidationBean validationBean = PIPELINEGROUP.validate("pipeline& GroupName");
        Assert.assertThat(validationBean.isValid(), Matchers.is(false));
        Assert.assertThat(validationBean.getError(), Matchers.is(PipelineGroupValidator.ERRORR_MESSAGE));
    }
}

