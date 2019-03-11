/**
 * Copyright 2017 MovingBlocks
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
package org.terasology.particles.updating;


import com.google.common.collect.BiMap;
import java.util.Iterator;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.particles.components.ParticleEmitterComponent;
import org.terasology.particles.functions.affectors.AffectorFunction;
import org.terasology.particles.functions.generators.GeneratorFunction;


/**
 * Unit test for {@link ParticleUpdaterImpl}.
 */
public class ParticleUpdaterImplTest {
    private ParticleUpdater particleUpdater;

    private BiMap<Class<Component>, GeneratorFunction> registeredGeneratorFunctions;

    private BiMap<Class<Component>, AffectorFunction> registeredAffectorFunctions;

    @Test(expected = IllegalArgumentException.class)
    public void testNullEmitterRegistration() {
        particleUpdater.register(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonEmitterRegistration() {
        EntityRef emitterEntity = Mockito.mock(EntityRef.class);
        Mockito.when(emitterEntity.getComponent(ParticleEmitterComponent.class)).thenReturn(null);
        particleUpdater.register(emitterEntity);
    }

    @Test
    public void testEmitterRegistration() {
        EntityRef emitterEntity = Mockito.mock(EntityRef.class);
        Mockito.when(emitterEntity.getComponent(ParticleEmitterComponent.class)).thenReturn(new ParticleEmitterComponent());
        particleUpdater.register(emitterEntity);
    }

    @Test
    public void testEmitterConfiguration() {
        EntityRef emitterEntity = Mockito.mock(EntityRef.class);
        Iterator<Component> componentIterator = getTestGeneratorsAndAffectors();
        Mockito.when(emitterEntity.iterateComponents()).thenReturn(() -> componentIterator);
        ParticleEmitterComponent particleEmitterComponent = new ParticleEmitterComponent();
        particleEmitterComponent.ownerEntity = emitterEntity;
        Mockito.when(emitterEntity.getComponent(ParticleEmitterComponent.class)).thenReturn(particleEmitterComponent);
        particleUpdater.register(emitterEntity);
        particleUpdater.configureEmitter(particleEmitterComponent, registeredAffectorFunctions, registeredGeneratorFunctions);
        for (Component component : ((Iterable<Component>) (() -> componentIterator))) {
            if (registeredGeneratorFunctions.containsKey(component.getClass())) {
                TestCase.assertTrue(particleEmitterComponent.generatorFunctionMap.containsKey(component));
            } else
                if (registeredGeneratorFunctions.containsKey(component.getClass())) {
                    TestCase.assertTrue(particleEmitterComponent.generatorFunctionMap.containsKey(component));
                }

        }
    }
}

