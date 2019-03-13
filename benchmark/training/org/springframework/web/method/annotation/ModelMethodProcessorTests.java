/**
 * Copyright 2002-2018 the original author or authors.
 *
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
package org.springframework.web.method.annotation;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * Test fixture with {@link org.springframework.web.method.annotation.ModelMethodProcessor}.
 *
 * @author Rossen Stoyanchev
 */
public class ModelMethodProcessorTests {
    private ModelMethodProcessor processor;

    private ModelAndViewContainer mavContainer;

    private MethodParameter paramModel;

    private MethodParameter returnParamModel;

    private NativeWebRequest webRequest;

    @Test
    public void supportsParameter() {
        Assert.assertTrue(processor.supportsParameter(paramModel));
    }

    @Test
    public void supportsReturnType() {
        Assert.assertTrue(processor.supportsReturnType(returnParamModel));
    }

    @Test
    public void resolveArgumentValue() throws Exception {
        Assert.assertSame(mavContainer.getModel(), processor.resolveArgument(paramModel, mavContainer, webRequest, null));
    }

    @Test
    public void handleModelReturnValue() throws Exception {
        mavContainer.addAttribute("attr1", "value1");
        Model returnValue = new ExtendedModelMap();
        returnValue.addAttribute("attr2", "value2");
        processor.handleReturnValue(returnValue, returnParamModel, mavContainer, webRequest);
        Assert.assertEquals("value1", mavContainer.getModel().get("attr1"));
        Assert.assertEquals("value2", mavContainer.getModel().get("attr2"));
    }
}

