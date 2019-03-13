/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.operations.down;


import java.util.Date;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.database.DatabaseVersionHeader;
import org.syncany.database.VectorClock;


public class DatabaseVersionHeaderComparatorTest {
    private DatabaseVersionHeader dbvh1;

    private DatabaseVersionHeader dbvh2;

    private VectorClock vc1;

    private VectorClock vc2;

    @Test
    public void testCompareDatabaseVersionHeaderEqual() {
        DatabaseVersionHeaderComparator databaseVersionHeaderComparator = new DatabaseVersionHeaderComparator(true);
        Assert.assertThat(databaseVersionHeaderComparator.compare(dbvh1, dbvh2), Is.is(0));
    }

    @Test
    public void testCompareDatabaseVersionHeaderEqualIgnoreTime() {
        DatabaseVersionHeaderComparator databaseVersionHeaderComparator = new DatabaseVersionHeaderComparator(false);
        dbvh2.setDate(new Date(-21053762));
        Assert.assertThat(databaseVersionHeaderComparator.compare(dbvh1, dbvh2), Is.is(0));
    }

    @Test
    public void testCompareDatabaseVersionHeaderSimultaneous() {
        DatabaseVersionHeaderComparator databaseVersionHeaderComparator = new DatabaseVersionHeaderComparator(true);
        vc1.setClock("A", 3);
        vc1.setClock("B", 7);
        vc2.setClock("A", 5);
        vc2.setClock("B", 4);
        dbvh2.setDate(new Date(-21053762));
        Assert.assertThat(databaseVersionHeaderComparator.compare(dbvh1, dbvh2), Is.is((-1)));
    }

    @Test
    public void testCompareDatabaseVersionHeaderSimultaneousIgnoreTime() {
        DatabaseVersionHeaderComparator databaseVersionHeaderComparator = new DatabaseVersionHeaderComparator(false);
        vc1.setClock("A", 3);
        vc1.setClock("B", 7);
        vc2.setClock("A", 5);
        vc2.setClock("B", 4);
        dbvh2.setDate(new Date(-21053762));
        Assert.assertThat(databaseVersionHeaderComparator.compare(dbvh1, dbvh2), Is.is(0));
    }

    @Test
    public void testCompareDatabaseVersionHeaderLarger() {
        DatabaseVersionHeaderComparator databaseVersionHeaderComparator = new DatabaseVersionHeaderComparator(false);
        vc1.setClock("A", 3);
        vc1.setClock("B", 7);
        vc2.setClock("A", 1);
        vc2.setClock("B", 7);
        Assert.assertThat(databaseVersionHeaderComparator.compare(dbvh1, dbvh2), Is.is(1));
    }

    @Test
    public void testCompareDatabaseVersionHeaderSmaller() {
        DatabaseVersionHeaderComparator databaseVersionHeaderComparator = new DatabaseVersionHeaderComparator(false);
        vc1.setClock("A", 3);
        vc1.setClock("B", 7);
        vc2.setClock("A", 5);
        vc2.setClock("B", 7);
        dbvh2.setDate(new Date(-21053762));
        Assert.assertThat(databaseVersionHeaderComparator.compare(dbvh1, dbvh2), Is.is((-1)));
    }

    @Test
    public void testCompareDatabaseVersionHeaderDifferentClients() {
        DatabaseVersionHeaderComparator databaseVersionHeaderComparator = new DatabaseVersionHeaderComparator(true);
        dbvh2.setClient("B");
        Assert.assertThat(databaseVersionHeaderComparator.compare(dbvh1, dbvh2), Is.is((-1)));
    }
}

