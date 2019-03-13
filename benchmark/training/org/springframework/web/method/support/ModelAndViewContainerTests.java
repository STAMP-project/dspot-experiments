/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.web.method.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.ui.ModelMap;


/**
 * Test fixture for {@link ModelAndViewContainer}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ModelAndViewContainerTests {
    private ModelAndViewContainer mavContainer;

    @Test
    public void getModel() {
        this.mavContainer.addAttribute("name", "value");
        Assert.assertEquals(1, this.mavContainer.getModel().size());
        Assert.assertEquals("value", this.mavContainer.getModel().get("name"));
    }

    @Test
    public void redirectScenarioWithRedirectModel() {
        this.mavContainer.addAttribute("name1", "value1");
        this.mavContainer.setRedirectModel(new ModelMap("name2", "value2"));
        this.mavContainer.setRedirectModelScenario(true);
        Assert.assertEquals(1, this.mavContainer.getModel().size());
        Assert.assertEquals("value2", this.mavContainer.getModel().get("name2"));
    }

    @Test
    public void redirectScenarioWithoutRedirectModel() {
        this.mavContainer.addAttribute("name", "value");
        this.mavContainer.setRedirectModelScenario(true);
        Assert.assertEquals(1, this.mavContainer.getModel().size());
        Assert.assertEquals("value", this.mavContainer.getModel().get("name"));
    }

    @Test
    public void ignoreDefaultModel() {
        this.mavContainer.setIgnoreDefaultModelOnRedirect(true);
        this.mavContainer.addAttribute("name", "value");
        this.mavContainer.setRedirectModelScenario(true);
        Assert.assertTrue(this.mavContainer.getModel().isEmpty());
    }

    // SPR-14045
    @Test
    public void ignoreDefaultModelAndWithoutRedirectModel() {
        this.mavContainer.setIgnoreDefaultModelOnRedirect(true);
        this.mavContainer.setRedirectModelScenario(true);
        this.mavContainer.addAttribute("name", "value");
        Assert.assertEquals(1, this.mavContainer.getModel().size());
        Assert.assertEquals("value", this.mavContainer.getModel().get("name"));
    }
}

