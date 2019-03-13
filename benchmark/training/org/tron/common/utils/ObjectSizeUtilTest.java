/**
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.common.utils;


import com.carrotsearch.sizeof.RamUsageEstimator;
import org.junit.Assert;
import org.junit.Test;


public class ObjectSizeUtilTest {
    class Person {
        int age;

        String name;

        int[] scores;

        public Person() {
        }

        public Person(int age, String name, int[] scores) {
            this.age = age;
            this.name = name;
            this.scores = scores;
        }
    }

    @Test
    public void testGetObjectSize() {
        ObjectSizeUtilTest.Person person = new ObjectSizeUtilTest.Person();
        Assert.assertEquals(48, RamUsageEstimator.sizeOf(person));
        ObjectSizeUtilTest.Person person1 = new ObjectSizeUtilTest.Person(1, "tom", new int[]{  });
        Assert.assertEquals(112, RamUsageEstimator.sizeOf(person1));
        ObjectSizeUtilTest.Person person2 = new ObjectSizeUtilTest.Person(1, "tom", new int[]{ 100 });
        Assert.assertEquals(120, RamUsageEstimator.sizeOf(person2));
        ObjectSizeUtilTest.Person person3 = new ObjectSizeUtilTest.Person(1, "tom", new int[]{ 100, 100 });
        Assert.assertEquals(120, RamUsageEstimator.sizeOf(person3));
        ObjectSizeUtilTest.Person person4 = new ObjectSizeUtilTest.Person(1, "tom", new int[]{ 100, 100, 100 });
        Assert.assertEquals(128, RamUsageEstimator.sizeOf(person4));
        ObjectSizeUtilTest.Person person5 = new ObjectSizeUtilTest.Person(1, "tom", new int[]{ 100, 100, 100, 100 });
        Assert.assertEquals(128, RamUsageEstimator.sizeOf(person5));
        ObjectSizeUtilTest.Person person6 = new ObjectSizeUtilTest.Person(1, "tom", new int[]{ 100, 100, 100, 100, 100 });
        Assert.assertEquals(136, RamUsageEstimator.sizeOf(person6));
    }
}

