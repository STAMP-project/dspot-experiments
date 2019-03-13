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
package org.springframework.web.servlet.view.tiles3;


import AbstractRequest.FORCE_INCLUDE_ATTRIBUTE_NAME;
import java.util.HashMap;
import java.util.Map;
import org.apache.tiles.request.render.Renderer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;


/**
 * Test fixture for {@link TilesView}.
 *
 * @author mick semb wever
 * @author Sebastien Deleuze
 */
public class TilesViewTests {
    private static final String VIEW_PATH = "template.test";

    private TilesView view;

    private Renderer renderer;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test
    public void render() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("modelAttribute", "modelValue");
        view.render(model, request, response);
        Assert.assertEquals("modelValue", request.getAttribute("modelAttribute"));
        verify(renderer).render(eq(TilesViewTests.VIEW_PATH), isA(org.apache.tiles.request.Request.class));
    }

    @Test
    public void alwaysIncludeDefaults() throws Exception {
        view.render(new HashMap(), request, response);
        Assert.assertNull(request.getAttribute(FORCE_INCLUDE_ATTRIBUTE_NAME));
    }

    @Test
    public void alwaysIncludeEnabled() throws Exception {
        view.setAlwaysInclude(true);
        view.render(new HashMap(), request, response);
        Assert.assertTrue(((Boolean) (request.getAttribute(FORCE_INCLUDE_ATTRIBUTE_NAME))));
    }
}

