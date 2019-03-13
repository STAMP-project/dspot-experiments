/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.guice;


import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class PolyBindTest {
    private Properties props;

    private Injector injector;

    @Test
    public void testSanity() {
        setUp(new Module() {
            @Override
            public void configure(Binder binder) {
                final MapBinder<String, PolyBindTest.Gogo> gogoBinder = PolyBind.optionBinder(binder, Key.get(PolyBindTest.Gogo.class));
                gogoBinder.addBinding("a").to(PolyBindTest.GoA.class);
                gogoBinder.addBinding("b").to(PolyBindTest.GoB.class);
                final MapBinder<String, PolyBindTest.GogoSally> gogoSallyBinder = PolyBind.optionBinder(binder, Key.get(PolyBindTest.GogoSally.class));
                gogoSallyBinder.addBinding("a").to(PolyBindTest.GoA.class);
                gogoSallyBinder.addBinding("b").to(PolyBindTest.GoB.class);
                PolyBind.createChoice(binder, "billy", Key.get(PolyBindTest.Gogo.class, Names.named("reverse")), Key.get(PolyBindTest.GoB.class));
                final MapBinder<String, PolyBindTest.Gogo> annotatedGogoBinder = PolyBind.optionBinder(binder, Key.get(PolyBindTest.Gogo.class, Names.named("reverse")));
                annotatedGogoBinder.addBinding("a").to(PolyBindTest.GoB.class);
                annotatedGogoBinder.addBinding("b").to(PolyBindTest.GoA.class);
            }
        });
        Assert.assertEquals("A", injector.getInstance(PolyBindTest.Gogo.class).go());
        Assert.assertEquals("B", injector.getInstance(Key.get(PolyBindTest.Gogo.class, Names.named("reverse"))).go());
        props.setProperty("billy", "b");
        Assert.assertEquals("B", injector.getInstance(PolyBindTest.Gogo.class).go());
        Assert.assertEquals("A", injector.getInstance(Key.get(PolyBindTest.Gogo.class, Names.named("reverse"))).go());
        props.setProperty("billy", "a");
        Assert.assertEquals("A", injector.getInstance(PolyBindTest.Gogo.class).go());
        Assert.assertEquals("B", injector.getInstance(Key.get(PolyBindTest.Gogo.class, Names.named("reverse"))).go());
        props.setProperty("billy", "b");
        Assert.assertEquals("B", injector.getInstance(PolyBindTest.Gogo.class).go());
        Assert.assertEquals("A", injector.getInstance(Key.get(PolyBindTest.Gogo.class, Names.named("reverse"))).go());
        props.setProperty("billy", "c");
        try {
            Assert.assertEquals("A", injector.getInstance(PolyBindTest.Gogo.class).go());
            Assert.fail();// should never be reached

        } catch (Exception e) {
            Assert.assertTrue((e instanceof ProvisionException));
            Assert.assertTrue(e.getMessage().contains("Unknown provider[c] of Key[type=org.apache.druid.guice.PolyBindTest$Gogo"));
        }
        try {
            Assert.assertEquals("B", injector.getInstance(Key.get(PolyBindTest.Gogo.class, Names.named("reverse"))).go());
            Assert.fail();// should never be reached

        } catch (Exception e) {
            Assert.assertTrue((e instanceof ProvisionException));
            Assert.assertTrue(e.getMessage().contains("Unknown provider[c] of Key[type=org.apache.druid.guice.PolyBindTest$Gogo"));
        }
        // test default property value
        Assert.assertEquals("B", injector.getInstance(PolyBindTest.GogoSally.class).go());
        props.setProperty("sally", "a");
        Assert.assertEquals("A", injector.getInstance(PolyBindTest.GogoSally.class).go());
        props.setProperty("sally", "b");
        Assert.assertEquals("B", injector.getInstance(PolyBindTest.GogoSally.class).go());
        props.setProperty("sally", "c");
        try {
            injector.getInstance(PolyBindTest.GogoSally.class).go();
            Assert.fail();// should never be reached

        } catch (Exception e) {
            Assert.assertTrue((e instanceof ProvisionException));
            Assert.assertTrue(e.getMessage().contains("Unknown provider[c] of Key[type=org.apache.druid.guice.PolyBindTest$GogoSally"));
        }
    }

    public interface Gogo {
        String go();
    }

    public interface GogoSally {
        String go();
    }

    public static class GoA implements PolyBindTest.Gogo , PolyBindTest.GogoSally {
        @Override
        public String go() {
            return "A";
        }
    }

    public static class GoB implements PolyBindTest.Gogo , PolyBindTest.GogoSally {
        @Override
        public String go() {
            return "B";
        }
    }
}

