/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.model.view.controller;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Date: 12/20/15 - 2:19 PM
 *
 * @author Jeroen Meulemeester
 */
public class GiantControllerTest {
    /**
     * Verify if the controller passes the health level through to the model and vice versa
     */
    @Test
    public void testSetHealth() {
        final GiantModel model = Mockito.mock(GiantModel.class);
        final GiantView view = Mockito.mock(GiantView.class);
        final GiantController controller = new GiantController(model, view);
        Mockito.verifyZeroInteractions(model, view);
        for (final Health health : Health.values()) {
            controller.setHealth(health);
            Mockito.verify(model).setHealth(health);
            Mockito.verifyZeroInteractions(view);
        }
        controller.getHealth();
        Mockito.verify(model).getHealth();
        Mockito.verifyNoMoreInteractions(model, view);
    }

    /**
     * Verify if the controller passes the fatigue level through to the model and vice versa
     */
    @Test
    public void testSetFatigue() {
        final GiantModel model = Mockito.mock(GiantModel.class);
        final GiantView view = Mockito.mock(GiantView.class);
        final GiantController controller = new GiantController(model, view);
        Mockito.verifyZeroInteractions(model, view);
        for (final Fatigue fatigue : Fatigue.values()) {
            controller.setFatigue(fatigue);
            Mockito.verify(model).setFatigue(fatigue);
            Mockito.verifyZeroInteractions(view);
        }
        controller.getFatigue();
        Mockito.verify(model).getFatigue();
        Mockito.verifyNoMoreInteractions(model, view);
    }

    /**
     * Verify if the controller passes the nourishment level through to the model and vice versa
     */
    @Test
    public void testSetNourishment() {
        final GiantModel model = Mockito.mock(GiantModel.class);
        final GiantView view = Mockito.mock(GiantView.class);
        final GiantController controller = new GiantController(model, view);
        Mockito.verifyZeroInteractions(model, view);
        for (final Nourishment nourishment : Nourishment.values()) {
            controller.setNourishment(nourishment);
            Mockito.verify(model).setNourishment(nourishment);
            Mockito.verifyZeroInteractions(view);
        }
        controller.getNourishment();
        Mockito.verify(model).getNourishment();
        Mockito.verifyNoMoreInteractions(model, view);
    }

    @Test
    public void testUpdateView() {
        final GiantModel model = Mockito.mock(GiantModel.class);
        final GiantView view = Mockito.mock(GiantView.class);
        final GiantController controller = new GiantController(model, view);
        Mockito.verifyZeroInteractions(model, view);
        controller.updateView();
        Mockito.verify(view).displayGiant(model);
        Mockito.verifyNoMoreInteractions(model, view);
    }
}

