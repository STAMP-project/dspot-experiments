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
package org.terasology.logic.location;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.TerasologyTestingEnvironment;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.testUtil.TeraAssert;


/**
 *
 */
public class LocationComponentTest extends TerasologyTestingEnvironment {
    LocationComponent loc;

    EntityRef entity;

    Vector3f pos1 = new Vector3f(1, 2, 3);

    Vector3f pos2 = new Vector3f(2, 3, 4);

    Vector3f pos1plus2 = new Vector3f(3, 5, 7);

    Quat4f yawRotation;

    Quat4f pitchRotation;

    Quat4f yawPitch;

    long nextFakeEntityId = 1;

    @Test
    public void testSetLocalPosition() {
        loc.setLocalPosition(pos1);
        Assert.assertEquals(pos1, loc.getLocalPosition());
    }

    @Test
    public void testSetLocalRotation() {
        loc.setLocalRotation(yawRotation);
        Assert.assertEquals(yawRotation, loc.getLocalRotation());
    }

    @Test
    public void testUnparentedWorldLocationSameAsLocal() {
        loc.setLocalPosition(pos1);
        Assert.assertEquals(loc.getLocalPosition(), loc.getWorldPosition());
    }

    @Test
    public void testOffsetParentAddsToWorldLocation() {
        LocationComponent parent = giveParent();
        loc.setLocalPosition(pos1);
        parent.setLocalPosition(pos2);
        Assert.assertEquals(pos1plus2, loc.getWorldPosition());
    }

    @Test
    public void testParentRotatesWorldLocation() {
        LocationComponent parent = giveParent();
        loc.setLocalPosition(pos1);
        parent.setLocalRotation(yawRotation);
        TeraAssert.assertEquals(new Vector3f(pos1.z, pos1.y, (-(pos1.x))), loc.getWorldPosition(), 1.0E-5F);
    }

    @Test
    public void testParentScalesWorldLocation() {
        LocationComponent parent = giveParent();
        loc.setLocalPosition(pos1);
        parent.setLocalScale(2.0F);
        Assert.assertEquals(new Vector3f(2, 4, 6), loc.getWorldPosition());
    }

    @Test
    public void testScaleRotateAndOffsetCombineCorrectlyForWorldPosition() {
        LocationComponent parent = giveParent();
        loc.setLocalPosition(pos1);
        parent.setLocalScale(2.0F);
        parent.setLocalPosition(pos2);
        parent.setLocalRotation(yawRotation);
        TeraAssert.assertEquals(new Vector3f(8, 7, 2), loc.getWorldPosition(), 1.0E-5F);
    }

    @Test
    public void testWorldRotationSameAsLocalRotationWhenNoParent() {
        loc.setLocalRotation(yawRotation);
        Assert.assertEquals(loc.getLocalRotation(), loc.getWorldRotation());
    }

    @Test
    public void testWorldRotationCombinedWithParent() {
        LocationComponent parent = giveParent();
        loc.setLocalRotation(pitchRotation);
        parent.setLocalRotation(yawRotation);
        Assert.assertEquals(yawPitch, loc.getWorldRotation());
    }

    @Test
    public void testWorldScaleSameAsLocalWhenNoParent() {
        loc.setLocalScale(2.0F);
        Assert.assertEquals(loc.getLocalScale(), loc.getWorldScale(), 1.0E-5F);
    }

    @Test
    public void testWorldScaleStacksWithParent() {
        LocationComponent parent = giveParent();
        loc.setLocalScale(2.0F);
        parent.setLocalScale(2.0F);
        Assert.assertEquals(4.0F, loc.getWorldScale(), 1.0E-6F);
    }

    @Test
    public void testSetWorldPositionWorksWithNoParent() {
        loc.setWorldPosition(pos1);
        Assert.assertEquals(pos1, loc.getWorldPosition());
    }

    @Test
    public void testSetWorldPositionWorksWithOffsetParent() {
        LocationComponent parent = giveParent();
        parent.setLocalPosition(pos1);
        loc.setWorldPosition(pos1plus2);
        Assert.assertEquals(pos2, loc.getLocalPosition());
        Assert.assertEquals(pos1plus2, loc.getWorldPosition());
    }

    @Test
    public void testSetWorldPositionWorksWithScaledParent() {
        LocationComponent parent = giveParent();
        parent.setLocalScale(2.0F);
        loc.setWorldPosition(pos1);
        Assert.assertEquals(pos1, loc.getWorldPosition());
    }

    @Test
    public void testSetWorldPositionWorksWithRotatedParent() {
        LocationComponent parent = giveParent();
        parent.setLocalRotation(yawRotation);
        loc.setWorldPosition(pos1);
        TeraAssert.assertEquals(pos1, loc.getWorldPosition(), 1.0E-6F);
    }

    @Test
    public void testSetWorldPositionWorksWithNestedRotatedParent() {
        LocationComponent first = new LocationComponent();
        EntityRef firstEntity = createFakeEntityWith(first);
        LocationComponent second = new LocationComponent();
        EntityRef secondEntity = createFakeEntityWith(second);
        LocationComponent third = new LocationComponent();
        EntityRef thirdEntity = createFakeEntityWith(third);
        Location.attachChild(firstEntity, secondEntity);
        second.setLocalPosition(new Vector3f(1, 0, 0));
        first.setLocalRotation(yawRotation);
        TeraAssert.assertEquals(new Vector3f(0, 0, (-1)), second.getWorldPosition(), 1.0E-6F);
        Location.attachChild(secondEntity, thirdEntity);
        second.setLocalRotation(pitchRotation);
        third.setLocalPosition(new Vector3f(0, 0, 0));
        TeraAssert.assertEquals(new Vector3f(0, 0, (-1)), third.getWorldPosition(), 1.0E-6F);
        third.setLocalPosition(new Vector3f(0, 0, 1));
        TeraAssert.assertEquals(new Vector3f((0.5F * ((float) (Math.sqrt(2)))), ((-0.5F) * ((float) (Math.sqrt(2)))), (-1)), third.getWorldPosition(), 1.0E-6F);
    }

    @Test
    public void testSetWorldPositionWorksWithComplexParent() {
        LocationComponent parent = giveParent();
        parent.setLocalRotation(yawRotation);
        parent.setLocalScale(2.0F);
        parent.setLocalPosition(pos2);
        loc.setWorldPosition(pos1);
        TeraAssert.assertEquals(pos1, loc.getWorldPosition(), 1.0E-6F);
    }

    @Test
    public void testSetWorldScaleWorksWithNoParent() {
        loc.setWorldScale(4.0F);
        Assert.assertEquals(4.0F, loc.getWorldScale(), 1.0E-6F);
        Assert.assertEquals(4.0F, loc.getLocalScale(), 1.0E-6F);
    }

    @Test
    public void testSetWorldScaleWorksWithScaledParent() {
        LocationComponent parent = giveParent();
        parent.setLocalScale(4.0F);
        loc.setWorldScale(2.0F);
        Assert.assertEquals(2.0F, loc.getWorldScale(), 1.0E-6F);
    }

    @Test
    public void testSetWorldRotationWorksWithNoParent() {
        loc.setWorldRotation(yawRotation);
        Assert.assertEquals(yawRotation, loc.getWorldRotation());
        Assert.assertEquals(yawRotation, loc.getLocalRotation());
    }

    @Test
    public void testSetWorldRotationWithRotatedParent() {
        LocationComponent parent = giveParent();
        parent.setLocalRotation(yawRotation);
        loc.setWorldRotation(yawPitch);
        TeraAssert.assertEquals(yawPitch, loc.getWorldRotation(), 1.0E-7F);
    }

    @Test
    public void testPositionMaintainedWhenAttachedToParent() {
        LocationComponent parent = new LocationComponent();
        EntityRef parentEntity = createFakeEntityWith(parent);
        parent.setWorldPosition(new Vector3f(1, 0, 0));
        loc.setWorldPosition(new Vector3f(2, 0, 0));
        Location.attachChild(parentEntity, entity);
        TeraAssert.assertEquals(new Vector3f(2, 0, 0), loc.getWorldPosition(), 1.0E-6F);
    }

    @Test
    public void testPositionMaintainedWhenRemovedFromParent() {
        LocationComponent parent = new LocationComponent();
        EntityRef parentEntity = createFakeEntityWith(parent);
        parent.setWorldPosition(new Vector3f(1, 0, 0));
        loc.setWorldPosition(new Vector3f(2, 0, 0));
        Location.attachChild(parentEntity, entity);
        Location.removeChild(parentEntity, entity);
        TeraAssert.assertEquals(new Vector3f(2, 0, 0), loc.getWorldPosition(), 1.0E-6F);
    }

    @Test
    public void testPositionMaintainedWhenParentDestroyed() {
        LocationComponent parent = new LocationComponent();
        EntityRef parentEntity = createFakeEntityWith(parent);
        parent.setWorldPosition(new Vector3f(1, 0, 0));
        loc.setWorldPosition(new Vector3f(2, 0, 0));
        Location.attachChild(parentEntity, entity);
        Location locationSystem = new Location();
        locationSystem.onDestroyed(BeforeRemoveComponent.newInstance(), parentEntity, parent);
        Mockito.when(parentEntity.getComponent(LocationComponent.class)).thenReturn(null);
        Mockito.when(parentEntity.exists()).thenReturn(false);
        TeraAssert.assertEquals(new Vector3f(2, 0, 0), loc.getWorldPosition(), 1.0E-6F);
    }
}

