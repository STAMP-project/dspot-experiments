/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.column_prefix;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class ColumnPrefixTest {
    protected SqlSessionFactory sqlSessionFactory;

    @Test
    public void testSelectPetAndRoom() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            List<Pet> pets = getPetAndRoom(sqlSession);
            Assert.assertEquals(3, pets.size());
            Assert.assertEquals("Ume", pets.get(0).getRoom().getRoomName());
            Assert.assertNull(pets.get(1).getRoom());
            Assert.assertEquals("Sakura", pets.get(2).getRoom().getRoomName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testComplexPerson() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            List<Person> list = getPersons(sqlSession);
            Person person1 = list.get(0);
            Assert.assertEquals(Integer.valueOf(1), person1.getId());
            Assert.assertEquals(Address.class, person1.getBillingAddress().getClass());
            Assert.assertEquals(Integer.valueOf(10), person1.getBillingAddress().getId());
            Assert.assertEquals("IL", person1.getBillingAddress().getState());
            Assert.assertEquals("Chicago", person1.getBillingAddress().getCity());
            Assert.assertEquals("Cardinal", person1.getBillingAddress().getStateBird());
            Assert.assertEquals("IL", person1.getBillingAddress().getZip().getState());
            Assert.assertEquals("Chicago", person1.getBillingAddress().getZip().getCity());
            Assert.assertEquals(81, person1.getBillingAddress().getZip().getZipCode());
            Assert.assertEquals("0123", person1.getBillingAddress().getPhone1().getPhone());
            Assert.assertEquals("4567", person1.getBillingAddress().getPhone2().getPhone());
            Assert.assertEquals(AddressWithCaution.class, person1.getShippingAddress().getClass());
            Assert.assertEquals("Has a big dog.", ((AddressWithCaution) (person1.getShippingAddress())).getCaution());
            Assert.assertEquals(Integer.valueOf(11), person1.getShippingAddress().getId());
            Assert.assertEquals("CA", person1.getShippingAddress().getState());
            Assert.assertEquals("San Francisco", person1.getShippingAddress().getCity());
            Assert.assertEquals("California Valley Quail", person1.getShippingAddress().getStateBird());
            Assert.assertEquals("CA", person1.getShippingAddress().getZip().getState());
            Assert.assertEquals(82, person1.getShippingAddress().getZip().getZipCode());
            Assert.assertEquals("8888", person1.getShippingAddress().getPhone1().getPhone());
            Assert.assertNull(person1.getShippingAddress().getPhone2());
            Assert.assertEquals("Tsubaki", person1.getRoom().getRoomName());
            Assert.assertEquals(2, person1.getPets().size());
            Assert.assertEquals("Kotetsu", person1.getPets().get(0).getName());
            Assert.assertEquals("Ume", person1.getPets().get(0).getRoom().getRoomName());
            Assert.assertNull(person1.getPets().get(1).getRoom());
            Assert.assertEquals("Chien", person1.getPets().get(1).getName());
            Person person2 = list.get(1);
            Assert.assertEquals(Integer.valueOf(2), person2.getId());
            Assert.assertEquals(AddressWithCaution.class, person2.getBillingAddress().getClass());
            Assert.assertEquals(Integer.valueOf(12), person2.getBillingAddress().getId());
            Assert.assertEquals("No door bell.", ((AddressWithCaution) (person2.getBillingAddress())).getCaution());
            Assert.assertEquals("Los Angeles", person2.getBillingAddress().getCity());
            Assert.assertEquals("California Valley Quail", person2.getBillingAddress().getStateBird());
            Assert.assertEquals("Los Angeles", person2.getBillingAddress().getZip().getCity());
            Assert.assertEquals(83, person2.getBillingAddress().getZip().getZipCode());
            Assert.assertNull(person2.getBillingAddress().getPhone1());
            Assert.assertNull(person2.getBillingAddress().getPhone2());
            Assert.assertNull(person2.getShippingAddress());
            Assert.assertEquals(0, person2.getPets().size());
            Person person3 = list.get(2);
            Assert.assertEquals(Integer.valueOf(3), person3.getId());
            Assert.assertNull(person3.getBillingAddress());
            Assert.assertEquals(Address.class, person3.getShippingAddress().getClass());
            Assert.assertEquals(Integer.valueOf(13), person3.getShippingAddress().getId());
            Assert.assertEquals("Dallas", person3.getShippingAddress().getCity());
            Assert.assertEquals("Mockingbird", person3.getShippingAddress().getStateBird());
            Assert.assertEquals("Dallas", person3.getShippingAddress().getZip().getCity());
            Assert.assertEquals("9999", person3.getShippingAddress().getPhone1().getPhone());
            Assert.assertEquals("4567", person3.getShippingAddress().getPhone2().getPhone());
            Assert.assertEquals(1, person3.getPets().size());
            Assert.assertEquals("Dodo", person3.getPets().get(0).getName());
            Assert.assertEquals("Sakura", person3.getPets().get(0).getRoom().getRoomName());
        } finally {
            sqlSession.close();
        }
    }
}

