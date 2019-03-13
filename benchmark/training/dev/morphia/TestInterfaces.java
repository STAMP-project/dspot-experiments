/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package dev.morphia;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import dev.morphia.mapping.cache.DefaultEntityCache;
import dev.morphia.testmodel.Circle;
import dev.morphia.testmodel.Rectangle;
import dev.morphia.testmodel.Shape;
import dev.morphia.testmodel.ShapeShifter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Olafur Gauti Gudmundsson
 */
public class TestInterfaces extends TestBase {
    @Test
    public void testDynamicInstantiation() throws Exception {
        final DBCollection shapes = getDb().getCollection("shapes");
        final DBCollection shapeshifters = getDb().getCollection("shapeshifters");
        getMorphia().map(Circle.class).map(Rectangle.class).map(ShapeShifter.class);
        final Shape rectangle = new Rectangle(2, 5);
        final DBObject rectangleDbObj = getMorphia().toDBObject(rectangle);
        shapes.save(rectangleDbObj);
        final BasicDBObject rectangleDbObjLoaded = ((BasicDBObject) (shapes.findOne(new BasicDBObject("_id", rectangleDbObj.get("_id")))));
        final Shape rectangleLoaded = getMorphia().fromDBObject(getDs(), Shape.class, rectangleDbObjLoaded, new DefaultEntityCache());
        Assert.assertTrue(((rectangle.getArea()) == (rectangleLoaded.getArea())));
        Assert.assertTrue((rectangleLoaded instanceof Rectangle));
        final ShapeShifter shifter = new ShapeShifter();
        shifter.setReferencedShape(rectangleLoaded);
        shifter.setMainShape(new Circle(2.2));
        shifter.getAvailableShapes().add(new Rectangle(3, 3));
        shifter.getAvailableShapes().add(new Circle(4.4));
        final DBObject shifterDbObj = getMorphia().toDBObject(shifter);
        shapeshifters.save(shifterDbObj);
        final BasicDBObject shifterDbObjLoaded = ((BasicDBObject) (shapeshifters.findOne(new BasicDBObject("_id", shifterDbObj.get("_id")))));
        final ShapeShifter shifterLoaded = getMorphia().fromDBObject(getDs(), ShapeShifter.class, shifterDbObjLoaded, new DefaultEntityCache());
        Assert.assertNotNull(shifterLoaded);
        Assert.assertNotNull(shifterLoaded.getReferencedShape());
        Assert.assertNotNull(shifterLoaded.getReferencedShape().getArea());
        Assert.assertNotNull(rectangle);
        Assert.assertNotNull(rectangle.getArea());
        Assert.assertTrue(((rectangle.getArea()) == (shifterLoaded.getReferencedShape().getArea())));
        Assert.assertTrue(((shifterLoaded.getReferencedShape()) instanceof Rectangle));
        Assert.assertTrue(((shifter.getMainShape().getArea()) == (shifterLoaded.getMainShape().getArea())));
        Assert.assertEquals(shifter.getAvailableShapes().size(), shifterLoaded.getAvailableShapes().size());
    }
}

