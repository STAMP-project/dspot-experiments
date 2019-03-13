/**
 * Copyright 2013 MovingBlocks
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
package org.terasology.persistence;


import EntityData.NameValue;
import EntityData.Value;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.context.Context;
import org.terasology.engine.module.ModuleManager;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.stubs.GetterSetterComponent;
import org.terasology.entitySystem.stubs.StringComponent;
import org.terasology.persistence.serializers.ComponentSerializer;
import org.terasology.protobuf.EntityData;
import org.terasology.reflection.copy.CopyStrategyLibrary;
import org.terasology.reflection.reflect.ReflectFactory;
import org.terasology.reflection.reflect.ReflectionReflectFactory;


/**
 *
 */
public class ComponentSerializerTest {
    private static ModuleManager moduleManager;

    private ComponentSerializer componentSerializer;

    private ReflectFactory reflectFactory = new ReflectionReflectFactory();

    private CopyStrategyLibrary copyStrategyLibrary = new CopyStrategyLibrary(reflectFactory);

    private Context context;

    @Test
    public void testGetterSetterUtilization() throws Exception {
        GetterSetterComponent comp = new GetterSetterComponent();
        GetterSetterComponent newComp = ((GetterSetterComponent) (componentSerializer.deserialize(componentSerializer.serialize(comp))));
        Assert.assertTrue(comp.getterUsed);
        Assert.assertTrue(newComp.setterUsed);
    }

    @Test
    public void testSerializeComponentDeltas() throws Exception {
        EntityData.Component componentData = componentSerializer.serialize(new StringComponent("Original"), new StringComponent("Delta"));
        Assert.assertEquals("value", componentData.getField(0).getName());
        Assert.assertEquals("Delta", componentData.getField(0).getValue().getString(0));
    }

    @Test
    public void testComponentTypeIdUsedWhenLookupTableEnabled() throws Exception {
        componentSerializer.setIdMapping(ImmutableMap.<Class<? extends Component>, Integer>builder().put(StringComponent.class, 1).build());
        Component stringComponent = new StringComponent("Test");
        EntityData.Component compData = componentSerializer.serialize(stringComponent);
        Assert.assertEquals(1, compData.getTypeIndex());
        Assert.assertFalse(compData.hasType());
    }

    @Test
    public void testComponentTypeIdUsedWhenLookupTableEnabledForComponentDeltas() throws Exception {
        componentSerializer.setIdMapping(ImmutableMap.<Class<? extends Component>, Integer>builder().put(StringComponent.class, 413).build());
        EntityData.Component componentData = componentSerializer.serialize(new StringComponent("Original"), new StringComponent("Value"));
        Assert.assertEquals(413, componentData.getTypeIndex());
    }

    @Test
    public void testComponentTypeIdDeserializes() throws Exception {
        componentSerializer.setIdMapping(ImmutableMap.<Class<? extends Component>, Integer>builder().put(StringComponent.class, 1).build());
        EntityData.Component compData = EntityData.Component.newBuilder().setTypeIndex(1).addField(NameValue.newBuilder().setName("value").setValue(Value.newBuilder().addString("item"))).build();
        Component comp = componentSerializer.deserialize(compData);
        Assert.assertTrue((comp instanceof StringComponent));
        Assert.assertEquals("item", ((StringComponent) (comp)).value);
    }

    @Test
    public void testDeltaComponentTypeIdDeserializesWithValue() throws Exception {
        componentSerializer.setIdMapping(ImmutableMap.<Class<? extends Component>, Integer>builder().put(StringComponent.class, 1).build());
        EntityData.Component compData = EntityData.Component.newBuilder().setTypeIndex(1).addField(NameValue.newBuilder().setName("value").setValue(Value.newBuilder().addString("item"))).build();
        StringComponent original = new StringComponent("test");
        componentSerializer.deserializeOnto(original, compData);
        Assert.assertEquals("item", original.value);
    }

    @Test
    public void testDeltaComponentTypeIdDeserializesWithoutValue() throws Exception {
        componentSerializer.setIdMapping(ImmutableMap.<Class<? extends Component>, Integer>builder().put(StringComponent.class, 1).build());
        EntityData.Component compData = EntityData.Component.newBuilder().setTypeIndex(1).addField(NameValue.newBuilder().setName("value")).build();
        StringComponent original = new StringComponent("test");
        componentSerializer.deserializeOnto(original, compData);
        Assert.assertEquals(null, original.value);
    }
}

