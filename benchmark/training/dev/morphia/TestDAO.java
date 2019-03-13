/**
 * Copyright (C) 2010 Olafur nGauti Gudmundsson
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


import com.mongodb.DBObject;
import dev.morphia.dao.DAO;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.TestQuery;
import dev.morphia.query.UpdateOperations;
import dev.morphia.testdaos.HotelDAO;
import dev.morphia.testmodel.Address;
import dev.morphia.testmodel.Hotel;
import dev.morphia.testmodel.PhoneNumber;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import static dev.morphia.testmodel.Hotel.Type.BUSINESS;
import static dev.morphia.testmodel.Hotel.Type.LEISURE;
import static dev.morphia.testmodel.PhoneNumber.Type.PHONE;


/**
 *
 *
 * @author Olafur Gauti Gudmundsson
 */
public class TestDAO extends TestBase {
    @Test
    public void testExists() {
        final HotelDAO hotelDAO = new HotelDAO(getMorphia(), getMongoClient());
        Hotel borg = new Hotel();
        borg.setName("Hotel Borg");
        borg.setStars(4);
        borg.setTakesCreditCards(true);
        borg.setStartDate(new Date());
        borg.setType(LEISURE);
        final Address address = new Address();
        address.setStreet("Posthusstraeti 11");
        address.setPostCode("101");
        address.setSecretWord("philodendron");
        borg.setAddress(address);
        save(borg);
        Hotel grand = new Hotel();
        grand.setName("The Grand Budapest Hotel");
        grand.setStars(5);
        grand.setStartDate(new Date());
        grand.setType(LEISURE);
        getDs().save(new TestQuery.Photo());
        save(grand);
        Assert.assertTrue(hotelDAO.exists(getDs().find(Hotel.class).field("address").exists()));
        Assert.assertFalse(hotelDAO.exists("name", "Hotel California"));
    }

    @Test
    public void testDAO() {
        getMorphia().map(Hotel.class);
        final Hotel borg = new Hotel();
        borg.setName("Hotel Borg");
        borg.setStars(4);
        borg.setTakesCreditCards(true);
        borg.setStartDate(new Date());
        borg.setType(LEISURE);
        final Address address = new Address();
        address.setStreet("Posthusstraeti 11");
        address.setPostCode("101");
        address.setSecretWord("philodendron");
        borg.setAddress(address);
        final HotelDAO hotelDAO = new HotelDAO(getMorphia(), getMongoClient());
        save(borg);
        Assert.assertEquals(1, hotelDAO.count());
        Assert.assertNotNull(borg.getId());
        final Hotel hotelLoaded = hotelDAO.get(borg.getId());
        Assert.assertEquals(borg.getName(), hotelLoaded.getName());
        Assert.assertEquals(borg.getAddress().getPostCode(), hotelLoaded.getAddress().getPostCode());
        final DBObject dbObject = getMorphia().toDBObject(borg);
        Assert.assertNull(get("secretWord"));
        Assert.assertNull(hotelLoaded.getAddress().getSecretWord());
        final Hotel hotelByValue = hotelDAO.findOne("name", "Hotel Borg");
        Assert.assertNotNull(hotelByValue);
        Assert.assertEquals(borg.getStartDate(), hotelByValue.getStartDate());
        Assert.assertTrue(hotelDAO.exists("stars", 4));
        final Hotel hilton = new Hotel();
        hilton.setName("Hilton Hotel");
        hilton.setStars(4);
        hilton.setTakesCreditCards(true);
        hilton.setStartDate(new Date());
        hilton.setType(BUSINESS);
        final Address hiltonAddress = new Address();
        hiltonAddress.setStreet("Some street 44");
        hiltonAddress.setPostCode("101");
        hilton.setAddress(hiltonAddress);
        save(hilton);
        final List<Hotel> allHotels = TestBase.toList(find());
        Assert.assertEquals(2, allHotels.size());
        Assert.assertEquals(1, TestBase.toList(find(new FindOptions().skip(1).limit(10))).size());
        Assert.assertEquals(1, TestBase.toList(find(new FindOptions().limit(1))).size());
        Assert.assertTrue(exists("type", BUSINESS));
        Assert.assertNotNull(findOne("type", LEISURE));
        // try updating
        final UpdateOperations<Hotel> mods = createUpdateOperations().inc("stars", 1);
        hotelDAO.update(createQuery().filter("stars", 4), mods);
        Assert.assertEquals(2, hotelDAO.count(createQuery().filter("stars", 5)));
        hotelDAO.deleteById(borg.getId());
        Assert.assertEquals(1, hotelDAO.count());
        getCollection().drop();
        Assert.assertEquals(0, hotelDAO.count());
    }

    @Test
    public void testNewDAO() {
        getMorphia().map(Hotel.class);
        final DAO<Hotel, ObjectId> hotelDAO = new dev.morphia.dao.BasicDAO<Hotel, ObjectId>(Hotel.class, getMongoClient(), getMorphia(), "morphia_test");
        final Hotel borg = new Hotel();
        borg.setName("Hotel Borg");
        borg.setStars(3);
        borg.setTakesCreditCards(true);
        borg.setStartDate(new Date());
        borg.setType(LEISURE);
        final Address address = new Address();
        address.setStreet("Posthusstraeti 11");
        address.setPostCode("101");
        borg.setAddress(address);
        hotelDAO.deleteByQuery(((Query<Hotel>) (hotelDAO.find())));
        hotelDAO.save(borg);
        Assert.assertEquals(1, hotelDAO.count());
        Assert.assertNotNull(borg.getId());
        final Hotel hotelLoaded = hotelDAO.get(borg.getId());
        Assert.assertEquals(borg.getName(), hotelLoaded.getName());
        Assert.assertEquals(borg.getAddress().getPostCode(), hotelLoaded.getAddress().getPostCode());
        final Hotel hotelByValue = hotelDAO.findOne("name", "Hotel Borg");
        Assert.assertNotNull(hotelByValue);
        Assert.assertEquals(borg.getStartDate(), hotelByValue.getStartDate());
        Assert.assertTrue(hotelDAO.exists("stars", 3));
        final Hotel hilton = new Hotel();
        hilton.setName("Hilton Hotel");
        hilton.setStars(4);
        hilton.setTakesCreditCards(true);
        hilton.setStartDate(new Date());
        hilton.setType(BUSINESS);
        final Address hiltonAddress = new Address();
        hiltonAddress.setStreet("Some street 44");
        hiltonAddress.setPostCode("101");
        hilton.setAddress(hiltonAddress);
        hilton.getPhoneNumbers().add(new PhoneNumber(354, 1234567, PHONE));
        hotelDAO.save(hilton);
        Assert.assertEquals(2, TestBase.toList(find()).size());
        Assert.assertEquals(2, hotelDAO.findIds().size());
        List<ObjectId> names = hotelDAO.findIds("name", hilton.getName());
        Assert.assertEquals(1, names.size());
        Assert.assertEquals(hilton.getId(), names.get(0));
        List<ObjectId> stars = hotelDAO.findIds(getDs().find(Hotel.class).field("stars").equal(4));
        Assert.assertEquals(1, stars.size());
        Assert.assertEquals(hilton.getId(), stars.get(0));
        Assert.assertEquals(borg.getId(), hotelDAO.findOneId().getId());
        Assert.assertEquals(hilton.getId(), hotelDAO.findOneId("name", hilton.getName()).getId());
        Assert.assertEquals(hilton.getId(), hotelDAO.findOneId(getDs().find(Hotel.class).field("stars").equal(4)).getId());
        Assert.assertEquals(1, TestBase.toList(find(new FindOptions().skip(1).limit(10))).size());
        Assert.assertEquals(1, TestBase.toList(find(new FindOptions().limit(1))).size());
        Assert.assertTrue(hotelDAO.exists("type", BUSINESS));
        Assert.assertNotNull(hotelDAO.findOne("type", LEISURE));
        Assert.assertEquals(1, hotelDAO.count(hotelDAO.createQuery().field("stars").notEqual(4)));
        Assert.assertEquals(2, hotelDAO.count(hotelDAO.createQuery().field("stars").lessThan(5)));
        Assert.assertEquals(1, hotelDAO.count(hotelDAO.createQuery().field("stars").greaterThanOrEq(4)));
        Assert.assertEquals(2, hotelDAO.count(hotelDAO.createQuery().field("stars").lessThan(5)));
        Assert.assertEquals(1, hotelDAO.count(hotelDAO.createQuery().field("phoneNumbers").sizeEq(1)));
        Assert.assertEquals(1, hotelDAO.count(hotelDAO.createQuery().filter("stars", 4).order("address.address_street")));
        Assert.assertEquals(hilton.getName(), hotelDAO.find(hotelDAO.createQuery().filter("stars", 4).order("address.address_street")).iterator().next().getName());
        Assert.assertEquals(hilton.getName(), hotelDAO.find(hotelDAO.createQuery().filter("stars", 4).order("-address.address_street")).iterator().next().getName());
        Assert.assertEquals(hilton.getName(), hotelDAO.find(hotelDAO.createQuery().filter("stars", 4).order("stars, -address.address_street")).iterator().next().getName());
        hotelDAO.deleteById(borg.getId());
        Assert.assertEquals(1, hotelDAO.count());
        hotelDAO.getCollection().drop();
        Assert.assertEquals(0, hotelDAO.count());
    }

    @Test
    public void testSaveEntityWithId() {
        final HotelDAO hotelDAO = new HotelDAO(getMorphia(), getMongoClient());
        final Hotel borg = new Hotel();
        borg.setName("Hotel Borg");
        borg.setStars(4);
        save(borg);
        final Hotel hotelLoaded = hotelDAO.get(borg.getId());
        hotelLoaded.setStars(5);
        save(hotelLoaded);
        final Hotel hotelReloaded = hotelDAO.get(borg.getId());
        Assert.assertEquals(5, hotelReloaded.getStars());
    }
}

