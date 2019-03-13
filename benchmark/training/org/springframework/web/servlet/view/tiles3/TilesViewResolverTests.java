/**
 * Copyright 2002-2013 the original author or authors.
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


import java.util.Locale;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.Renderer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Test fixture for {@link TilesViewResolver}.
 *
 * @author mick semb wever
 */
public class TilesViewResolverTests {
    private TilesViewResolver viewResolver;

    private Renderer renderer;

    @Test
    public void testResolve() throws Exception {
        BDDMockito.given(this.renderer.isRenderable(ArgumentMatchers.eq("/template.test"), ArgumentMatchers.isA(Request.class))).willReturn(true);
        BDDMockito.given(this.renderer.isRenderable(ArgumentMatchers.eq("/nonexistent.test"), ArgumentMatchers.isA(Request.class))).willReturn(false);
        Assert.assertTrue(((this.viewResolver.resolveViewName("/template.test", Locale.ITALY)) instanceof TilesView));
        Assert.assertNull(this.viewResolver.resolveViewName("/nonexistent.test", Locale.ITALY));
        Mockito.verify(this.renderer).isRenderable(ArgumentMatchers.eq("/template.test"), ArgumentMatchers.isA(Request.class));
        Mockito.verify(this.renderer).isRenderable(ArgumentMatchers.eq("/nonexistent.test"), ArgumentMatchers.isA(Request.class));
    }
}

