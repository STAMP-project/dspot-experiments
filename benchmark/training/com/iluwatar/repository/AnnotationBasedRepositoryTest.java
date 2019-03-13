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
package com.iluwatar.repository;


import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


/**
 * Test case to test the functions of {@link PersonRepository}, beside the CRUD functions, the query
 * by {@link org.springframework.data.jpa.domain.Specification} are also test.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class }, loader = AnnotationConfigContextLoader.class)
public class AnnotationBasedRepositoryTest {
    @Resource
    private PersonRepository repository;

    Person peter = new Person("Peter", "Sagan", 17);

    Person nasta = new Person("Nasta", "Kuzminova", 25);

    Person john = new Person("John", "lawrence", 35);

    Person terry = new Person("Terry", "Law", 36);

    List<Person> persons = Arrays.asList(peter, nasta, john, terry);

    @Test
    public void testFindAll() {
        List<Person> actuals = Lists.newArrayList(repository.findAll());
        Assertions.assertTrue(((actuals.containsAll(persons)) && (persons.containsAll(actuals))));
    }

    @Test
    public void testSave() {
        Person terry = repository.findByName("Terry");
        terry.setSurname("Lee");
        terry.setAge(47);
        repository.save(terry);
        terry = repository.findByName("Terry");
        Assertions.assertEquals(terry.getSurname(), "Lee");
        Assertions.assertEquals(47, terry.getAge());
    }

    @Test
    public void testDelete() {
        Person terry = repository.findByName("Terry");
        repository.delete(terry);
        Assertions.assertEquals(3, repository.count());
        Assertions.assertNull(repository.findByName("Terry"));
    }

    @Test
    public void testCount() {
        Assertions.assertEquals(4, repository.count());
    }

    @Test
    public void testFindAllByAgeBetweenSpec() {
        List<Person> persons = repository.findAll(new PersonSpecifications.AgeBetweenSpec(20, 40));
        Assertions.assertEquals(3, persons.size());
        Assertions.assertTrue(persons.stream().allMatch(( item) -> {
            return ((item.getAge()) > 20) && ((item.getAge()) < 40);
        }));
    }

    @Test
    public void testFindOneByNameEqualSpec() {
        Person actual = repository.findOne(new PersonSpecifications.NameEqualSpec("Terry"));
        Assertions.assertEquals(terry, actual);
    }
}

