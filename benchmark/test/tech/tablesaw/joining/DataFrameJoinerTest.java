package tech.tablesaw.joining;


import com.google.common.base.Joiner;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.selection.Selection;


public class DataFrameJoinerTest {
    private static final Table ONE_YEAR = Table.read().csv(Joiner.on(System.lineSeparator()).join("Date,1 Yr Treasury Rate", "\"Dec 1, 2017\",1.65%", "\"Nov 1, 2017\",1.56%", "\"Oct 1, 2017\",1.40%", "\"Sep 1, 2017\",1.28%", "\"Aug 1, 2017\",1.23%", "\"Jul 1, 2017\",1.22%"), "1 Yr Treasury Rate");

    private static final Table SP500 = Table.read().csv(Joiner.on(System.lineSeparator()).join("Date,S&P 500", "\"Nov 1, 2017\",2579.36", "\"Oct 1, 2017\",2521.20", "\"Sep 1, 2017\",2474.42", "\"Aug 1, 2017\",2477.10", "\"Jul 1, 2017\",2431.39", "\"Jun 1, 2017\",2430.06"), "S&P 500");

    private static final Table ANIMAL_NAMES = Table.read().csv(Joiner.on(System.lineSeparator()).join("Animal,Name", "Pig,Bob", "Pig,James", "Horse,David", "Goat,Samantha", "Tigon,Rudhrani", "Rabbit,Taylor"), "Animal Names");

    private static final Table ANIMAL_FEED = Table.read().csv(Joiner.on(System.lineSeparator()).join("Animal,Feed", "Pig,Mush", "Horse,Hay", "Goat,Anything", "Guanaco,Grass", "Monkey,Banana"), "Animal Feed");

    private static final Table DOUBLE_INDEXED_PEOPLE = Table.read().csv(Joiner.on(System.lineSeparator()).join("ID,Name", "1.1,Bob", "2.1,James", "3.0,David", "4.0,Samantha"), "People");

    private static final Table DOUBLE_INDEXED_DOGS = Table.read().csv(Joiner.on(System.lineSeparator()).join("ID,Dog Name", "1.1,Spot", "3.0,Fido", "4.0,Sasha", "5.0,King"), "Dogs");

    private static final Table DOUBLE_INDEXED_CATS = Table.read().csv(Joiner.on(System.lineSeparator()).join("ID,Cat Name", "1.1,Spot2", "2.1,Fido", "6.0,Sasha", "8.0,King2"), "Cats");

    private static final Table DOUBLE_INDEXED_FISH = Table.read().csv(Joiner.on(System.lineSeparator()).join("ID,Fish Name", "11.1,Spot3", "2.1,Fido", "4.0,Sasha", "6.0,King2"), "Fish");

    private static final Table DOUBLE_INDEXED_MICE = Table.read().csv(Joiner.on(System.lineSeparator()).join("ID,Mice_Name", "2.1,Jerry", "3.0,Fido", "6.0,Sasha", "9.0,Market"), "Mice");

    private static final Table DOUBLE_INDEXED_BIRDS = Table.read().csv(Joiner.on(System.lineSeparator()).join("ID,Bird_Name", "2.1,JerryB", "3.0,FidoB", "6.25,SashaB", "9.0,Market"), "Birds");

    private static final Table DUPLICATE_COL_NAME_DOGS = Table.read().csv(Joiner.on(System.lineSeparator()).join("ID,Dog Name, Good", "1.1,Spot,true", "3.0,Fido,true", "4.0,Sasha,true", "5.0,King,true", "1.1,Spot,false", "3.0,Fido,false", "4.0,Sasha,false", "5.0,King,false"), "Dogs");

    @Test
    public void innerJoinWithDoubleBirdsCatsFishDouble() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_BIRDS.join("ID").inner(DataFrameJoinerTest.DOUBLE_INDEXED_CATS, DataFrameJoinerTest.DOUBLE_INDEXED_FISH);
        Assertions.assertEquals(4, joined.columnCount());
        Assertions.assertEquals(1, joined.rowCount());
    }

    @Test
    public void innerJoinWithDoubleDogsCatsBirdsDouble() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_FISH.join("ID").inner(DataFrameJoinerTest.DOUBLE_INDEXED_CATS, DataFrameJoinerTest.DOUBLE_INDEXED_BIRDS);
        Assertions.assertEquals(4, joined.columnCount());
        Assertions.assertEquals(1, joined.rowCount());
    }

    @Test
    public void innerJoinWithDoubleDogsCatsFishVarargs() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_MICE.join("ID").inner(DataFrameJoinerTest.DOUBLE_INDEXED_CATS, DataFrameJoinerTest.DOUBLE_INDEXED_FISH);
        Assertions.assertEquals(4, joined.columnCount());
        Assertions.assertEquals(2, joined.rowCount());
    }

    @Test
    public void innerJoinWithDoublesSimple() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_PEOPLE.join("ID").inner(DataFrameJoinerTest.DOUBLE_INDEXED_DOGS);
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(3, joined.rowCount());
        Assertions.assertEquals(3, joined.column("ID").size());
    }

    @Test
    public void innerJoinWithDoubles() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_PEOPLE.join("ID").inner(DataFrameJoinerTest.DOUBLE_INDEXED_DOGS, "ID");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(3, joined.rowCount());
    }

    @Test
    public void innerJoinWithDuplicateColumnNames() {
        Table table1 = DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.where(DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
        Table table2 = DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.where(DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());
        Table joined = table1.join("ID").inner(table2, "ID", true);
        Assertions.assertEquals(5, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
    }

    @Test
    public void rightOuterJoinWithDuplicateColumnNames() {
        Table table1 = DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.where(DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
        Table table2 = DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.where(DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());
        Table joined = table1.join("ID").rightOuter(table2, true, "ID");
        Assertions.assertEquals(5, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinWithDuplicateColumnNames() {
        Table table1 = DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.where(DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
        Table table2 = DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.where(DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());
        Table joined = table1.join("ID").leftOuter(table2, true, "ID");
        Assertions.assertEquals(5, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
        Assertions.assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void leftOuterJoinWithDoubles() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_PEOPLE.join("ID").leftOuter(DataFrameJoinerTest.DOUBLE_INDEXED_DOGS, "ID");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
        Assertions.assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void rightOuterJoinWithDoubles() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_PEOPLE.join("ID").rightOuter(DataFrameJoinerTest.DOUBLE_INDEXED_DOGS, "ID");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
        Assertions.assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void rightOuterJoinWithDoubles2() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_PEOPLE.join("ID").rightOuter(DataFrameJoinerTest.DOUBLE_INDEXED_DOGS);
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
        Assertions.assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void rightOuterJoinWithDoubles3() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_PEOPLE.join("ID").rightOuter(DataFrameJoinerTest.DOUBLE_INDEXED_DOGS, DataFrameJoinerTest.DOUBLE_INDEXED_CATS);
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinWithDoubles2() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_DOGS.join("ID").leftOuter(DataFrameJoinerTest.DOUBLE_INDEXED_PEOPLE, "ID");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
        Assertions.assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void leftOuterJoinWithDoubles3() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_DOGS.join("ID").leftOuter(DataFrameJoinerTest.DOUBLE_INDEXED_PEOPLE);
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
        Assertions.assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void leftOuterJoinWithDoubles4() {
        Table joined = DataFrameJoinerTest.DOUBLE_INDEXED_DOGS.join("ID").leftOuter(DataFrameJoinerTest.DOUBLE_INDEXED_PEOPLE, DataFrameJoinerTest.DOUBLE_INDEXED_CATS);
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
        Assertions.assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void innerJoin() {
        Table joined = DataFrameJoinerTest.SP500.join("Date").inner(DataFrameJoinerTest.ONE_YEAR, "Date");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(5, joined.rowCount());
    }

    @Test
    public void innerJoinWithBoolean() {
        Table joined = DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.join("Good").inner(true, DataFrameJoinerTest.DUPLICATE_COL_NAME_DOGS.copy());
        Assertions.assertEquals(5, joined.columnCount());
        Assertions.assertEquals(32, joined.rowCount());
    }

    @Test
    public void leftOuterJoin() {
        Table joined = DataFrameJoinerTest.SP500.join("Date").leftOuter(DataFrameJoinerTest.ONE_YEAR, "Date");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(6, joined.rowCount());
        Assertions.assertEquals(6, joined.column("Date").size());
    }

    @Test
    public void innerJoinDuplicateKeysFirstTable() {
        Table joined = DataFrameJoinerTest.ANIMAL_NAMES.join("Animal").inner(DataFrameJoinerTest.ANIMAL_FEED, "Animal");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinDuplicateKeysFirstTable() {
        Table joined = DataFrameJoinerTest.ANIMAL_NAMES.join("Animal").leftOuter(DataFrameJoinerTest.ANIMAL_FEED, "Animal");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(6, joined.rowCount());
        Assertions.assertEquals(6, joined.column("Animal").size());
    }

    @Test
    public void innerJoinDuplicateKeysSecondTable() {
        Table joined = DataFrameJoinerTest.ANIMAL_FEED.join("Animal").inner(DataFrameJoinerTest.ANIMAL_NAMES, "Animal");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
    }

    @Test
    public void innerJoinDuplicateKeysSecondTableWithTextColumn() {
        Table feed = DataFrameJoinerTest.ANIMAL_FEED.copy();
        Table names = DataFrameJoinerTest.ANIMAL_NAMES.copy();
        feed.replaceColumn("Animal", feed.stringColumn("Animal").asTextColumn());
        TextColumn nameCol = names.stringColumn("Animal").asTextColumn();
        nameCol = nameCol.where(Selection.withRange(0, feed.rowCount()));
        feed.replaceColumn("Animal", nameCol);
        Table joined = DataFrameJoinerTest.ANIMAL_FEED.join("Animal").inner(DataFrameJoinerTest.ANIMAL_NAMES, "Animal");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinDuplicateKeysSecondTable() {
        Table joined = DataFrameJoinerTest.ANIMAL_FEED.join("Animal").leftOuter(DataFrameJoinerTest.ANIMAL_NAMES, "Animal");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(6, joined.rowCount());
        Assertions.assertEquals(6, joined.column("Animal").size());
    }

    @Test
    public void fullOuterJoinJustTable() {
        Table joined = DataFrameJoinerTest.ANIMAL_FEED.join("Animal").fullOuter(DataFrameJoinerTest.ANIMAL_NAMES);
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(8, joined.rowCount());
        Assertions.assertEquals(8, joined.column("Animal").size());
        Assertions.assertEquals(0, joined.column("Animal").countMissing());
        Assertions.assertEquals(8, joined.column("Name").size());
        Assertions.assertEquals(2, joined.column("Name").countMissing());
        Assertions.assertEquals(8, joined.column("Feed").size());
        Assertions.assertEquals(2, joined.column("Feed").countMissing());
    }

    @Test
    public void fullOuterJoin() {
        Table joined = DataFrameJoinerTest.ANIMAL_FEED.join("Animal").fullOuter(DataFrameJoinerTest.ANIMAL_NAMES, "Animal");
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(8, joined.rowCount());
        Assertions.assertEquals(8, joined.column("Animal").size());
        Assertions.assertEquals(0, joined.column("Animal").countMissing());
        Assertions.assertEquals(8, joined.column("Name").size());
        Assertions.assertEquals(2, joined.column("Name").countMissing());
        Assertions.assertEquals(8, joined.column("Feed").size());
        Assertions.assertEquals(2, joined.column("Feed").countMissing());
    }

    @Test
    public void fullOuterJoinNew() {
        Table joined = DataFrameJoinerTest.ANIMAL_FEED.join("Animal").fullOuter(true, DataFrameJoinerTest.ANIMAL_NAMES);
        Assertions.assertEquals(3, joined.columnCount());
        Assertions.assertEquals(8, joined.rowCount());
        Assertions.assertEquals(8, joined.column("Animal").size());
        Assertions.assertEquals(0, joined.column("Animal").countMissing());
        Assertions.assertEquals(8, joined.column("Name").size());
        Assertions.assertEquals(2, joined.column("Name").countMissing());
        Assertions.assertEquals(8, joined.column("Feed").size());
        Assertions.assertEquals(2, joined.column("Feed").countMissing());
    }

    @Test
    public void innerJoinStudentInstructorOnAge() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table joined = table1.join("Age").inner(true, table2);
        assert joined.columnNames().containsAll(Arrays.asList("T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear"));
        Assertions.assertEquals(16, joined.columnCount());
        Assertions.assertEquals(14, joined.rowCount());
    }

    @Test
    public void innerJoinInstructorStudentOnAge() {
        Table table1 = DataFrameJoinerTest.createINSTRUCTOR();
        Table table2 = DataFrameJoinerTest.createSTUDENT();
        Table joined = table1.join("Age").inner(true, table2);
        assert joined.columnNames().containsAll(Arrays.asList("T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear"));
        Assertions.assertEquals(16, joined.columnCount());
        Assertions.assertEquals(14, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorClassOnAge() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table table3 = DataFrameJoinerTest.createCLASS();
        Table joined = table1.join("Age").inner(true, table2, table3);
        assert joined.columnNames().containsAll(Arrays.asList("T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear", "T3.ID"));
        Assertions.assertEquals(24, joined.columnCount());
        Assertions.assertEquals(14, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorClassDeptHeadOnAge() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table table3 = DataFrameJoinerTest.createCLASS();
        Table table4 = DataFrameJoinerTest.createDEPTHEAD();
        Table joined = table1.join("Age").inner(true, table2, table3, table4);
        assert joined.columnNames().containsAll(Arrays.asList("T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear", "T3.ID", "T4.ID", "T4.First", "T4.Last", "T4.City", "T4.State"));
        Assertions.assertEquals(30, joined.columnCount());
        Assertions.assertEquals(14, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorDeptHeadOnStateAge() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table table3 = DataFrameJoinerTest.createDEPTHEAD();
        Table joined = table1.join("State", "Age").inner(true, table2, table3);
        assert joined.columnNames().containsAll(Arrays.asList("T2.ID", "T2.City", "T2.USID", "T2.GradYear", "T3.ID", "T3.First", "T3.Last", "T3.City"));
        Assertions.assertEquals(20, joined.columnCount());
        Assertions.assertEquals(1, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorOnStateAge() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table joined = table1.join("State", "Age").inner(true, table2);
        Assertions.assertEquals(15, joined.columnCount());
        Assertions.assertEquals(3, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorOnStateAgeGradYear() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table joined = table1.join("State", "Age", "GradYear").inner(true, table2);
        Assertions.assertEquals(14, joined.columnCount());
        Assertions.assertEquals(2, joined.rowCount());
    }

    @Test
    public void leftJoinStudentInstructorOnStateAge() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table joined = table1.join("State", "Age").leftOuter(true, table2);
        Assertions.assertEquals(15, joined.columnCount());
        Assertions.assertEquals(10, joined.rowCount());
        Assertions.assertEquals(10, joined.column("State").size());
        Assertions.assertEquals(10, joined.column("Age").size());
    }

    @Test
    public void innerJoinHouseBoatOnBedroomsOwner() {
        Table table1 = DataFrameJoinerTest.createHOUSE();
        Table table2 = DataFrameJoinerTest.createBOAT();
        Table joined = table1.join("Bedrooms", "Owner").inner(table2, new String[]{ "Bedrooms", "Owner" });
        Assertions.assertEquals(6, joined.columnCount());
        Assertions.assertEquals(2, joined.rowCount());
        Assertions.assertEquals(2, joined.column("Bedrooms").size());
    }

    @Test
    public void innerJoinHouseBoatOnStyleTypeBedroomsOwner() {
        Table table1 = DataFrameJoinerTest.createHOUSE();
        Table table2 = DataFrameJoinerTest.createBOAT();
        Table joined = table1.join("Style", "Bedrooms", "Owner").inner(table2, new String[]{ "Type", "Bedrooms", "Owner" });
        Assertions.assertEquals(5, joined.columnCount());
        Assertions.assertEquals(1, joined.rowCount());
    }

    @Test
    public void fullJoinHouseBoatOnBedroomsOwner() {
        Table table1 = DataFrameJoinerTest.createHOUSE();
        Table table2 = DataFrameJoinerTest.createBOAT();
        Table joined = table1.join("Bedrooms", "Owner").fullOuter(true, table2);
        Assertions.assertEquals(6, joined.columnCount());
        Assertions.assertEquals(7, joined.rowCount());
        Assertions.assertEquals(7, joined.column("Style").size());
        Assertions.assertEquals(3, joined.column("Style").countMissing());
        Assertions.assertEquals(7, joined.column("Bedrooms").size());
        Assertions.assertEquals(0, joined.column("Bedrooms").countMissing());
        Assertions.assertEquals(7, joined.column("BuildDate").size());
        Assertions.assertEquals(3, joined.column("BuildDate").countMissing());
        Assertions.assertEquals(7, joined.column("Owner").size());
        Assertions.assertEquals(0, joined.column("Owner").countMissing());
        Assertions.assertEquals(7, joined.column("Type").size());
        Assertions.assertEquals(2, joined.column("Type").countMissing());
        Assertions.assertEquals(7, joined.column("SoldDate").size());
        Assertions.assertEquals(2, joined.column("SoldDate").countMissing());
    }

    @Test
    public void fullJoinHouse10Boat10OnBedroomsOwner() {
        Table table1 = DataFrameJoinerTest.createHOUSE10();
        Table table2 = DataFrameJoinerTest.createBOAT10();
        Table joined = table1.join("Bedrooms", "Owner").fullOuter(true, table2);
        Assertions.assertEquals(6, joined.columnCount());
        Assertions.assertEquals(11, joined.rowCount());
        Assertions.assertEquals(11, joined.column("Bedrooms").size());
        Assertions.assertEquals(0, joined.column("Bedrooms").countMissing());
        Assertions.assertEquals(11, joined.column("Bedrooms").size());
        Assertions.assertEquals(0, joined.column("Bedrooms").countMissing());
        Assertions.assertEquals(11, joined.column("BuildDate").size());
        Assertions.assertEquals(3, joined.column("BuildDate").countMissing());
        Assertions.assertEquals(11, joined.column("Owner").size());
        Assertions.assertEquals(0, joined.column("Owner").countMissing());
        Assertions.assertEquals(11, joined.column("Type").size());
        Assertions.assertEquals(3, joined.column("Type").countMissing());
        Assertions.assertEquals(11, joined.column("SoldDate").size());
        Assertions.assertEquals(3, joined.column("SoldDate").countMissing());
    }

    @Test
    public void fullJoinBnBBoat10OnBedroomsOwner() {
        Table table1 = DataFrameJoinerTest.createBEDANDBREAKFAST();
        Table table2 = DataFrameJoinerTest.createBOAT10();
        Table joined = table1.join("Bedrooms", "SoldDate").fullOuter(true, table2);
        Assertions.assertEquals(6, joined.columnCount());
        Assertions.assertEquals(11, joined.rowCount());
        Assertions.assertEquals(11, joined.column("Design").size());
        Assertions.assertEquals(6, joined.column("Design").countMissing());
        Assertions.assertEquals(11, joined.column("Bedrooms").size());
        Assertions.assertEquals(0, joined.column("Bedrooms").countMissing());
        Assertions.assertEquals(11, joined.column("SoldDate").size());
        Assertions.assertEquals(0, joined.column("SoldDate").countMissing());
        Assertions.assertEquals(11, joined.column("Owner").size());
        Assertions.assertEquals(6, joined.column("Owner").countMissing());
        Assertions.assertEquals(11, joined.column("Type").size());
        Assertions.assertEquals(3, joined.column("Type").countMissing());
        Assertions.assertEquals(11, joined.column("T2.Owner").size());
        Assertions.assertEquals(3, joined.column("T2.Owner").countMissing());
    }

    @Test
    public void leftJoinHouseBoatOnBedroomsOwner() {
        Table table1 = DataFrameJoinerTest.createHOUSE();
        Table table2 = DataFrameJoinerTest.createBOAT();
        Table joined = table1.join("Bedrooms", "Owner").leftOuter(table2, new String[]{ "Bedrooms", "Owner" });
        Assertions.assertEquals(6, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftJoinHouseBoatBnBOnStyleTypeBedroomsOwner() {
        Table table1 = DataFrameJoinerTest.createHOUSE();
        Table table2 = DataFrameJoinerTest.createBOAT();
        Table joined = table1.join("Style", "Bedrooms", "Owner").leftOuter(table2, new String[]{ "Type", "Bedrooms", "Owner" });
        Assertions.assertEquals(5, joined.columnCount());
        Assertions.assertEquals(4, joined.rowCount());
    }

    @Test
    public void rightJoinHouseBoatOnBedroomsOwner() {
        Table table1 = DataFrameJoinerTest.createHOUSE();
        Table table2 = DataFrameJoinerTest.createBOAT();
        Table joined = table1.join("Bedrooms", "Owner").rightOuter(table2, new String[]{ "Bedrooms", "Owner" });
        Assertions.assertEquals(6, joined.columnCount());
        Assertions.assertEquals(5, joined.rowCount());
    }

    @Test
    public void rightJoinHouseBoatOnStyleTypeBedroomsOwner() {
        Table table1 = DataFrameJoinerTest.createHOUSE();
        Table table2 = DataFrameJoinerTest.createBOAT();
        Table joined = table1.join("Style", "Bedrooms", "Owner").rightOuter(table2, new String[]{ "Type", "Bedrooms", "Owner" });
        Assertions.assertEquals(5, joined.columnCount());
        Assertions.assertEquals(5, joined.rowCount());
    }

    @Test
    public void rightJoinStudentInstructorOnStateAge() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table joined = table1.join("State", "Age").rightOuter(true, table2);
        Assertions.assertEquals(15, joined.columnCount());
        Assertions.assertEquals(10, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorOnStateName() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table joined = table1.join("State", "FirstName").inner(table2, true, "State", "First");
        Assertions.assertEquals(15, joined.columnCount());
        Assertions.assertEquals(5, joined.rowCount());
    }

    @Test
    public void leftJoinStudentInstructorOnStateName() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table joined = table1.join("State", "FirstName").leftOuter(table2, true, "State", "First");
        Assertions.assertEquals(15, joined.columnCount());
        Assertions.assertEquals(10, joined.rowCount());
    }

    @Test
    public void rightJoinStudentInstructorOnStateName() {
        Table table1 = DataFrameJoinerTest.createSTUDENT();
        Table table2 = DataFrameJoinerTest.createINSTRUCTOR();
        Table joined = table1.join("State", "FirstName").rightOuter(table2, true, "State", "First");
        Assertions.assertEquals(15, joined.columnCount());
        Assertions.assertEquals(10, joined.rowCount());
    }

    @Test
    public void innerJoinOnAge() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join("Age").inner(table2, "Age", true);
        Assertions.assertEquals(9, joined.columnCount());
        Assertions.assertEquals(18, joined.rowCount());
    }

    @Test
    public void innerJoinAnimalPeopleOnAge() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join("Age").inner(true, table2);
        Assertions.assertEquals(9, joined.columnCount());
        Assertions.assertEquals(18, joined.rowCount());
    }

    @Test
    public void innerJoinAnimalTreeOnAge() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createTREE();
        Table joined = table1.join("Age").inner(true, table2);
        Assertions.assertEquals(7, joined.columnCount());
        Assertions.assertEquals(8, joined.rowCount());
    }

    @Test
    public void innerJoinAnimalPeopleTreeOnAge() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table table3 = DataFrameJoinerTest.createTREE();
        Table table4 = DataFrameJoinerTest.createFLOWER();
        Table joined = table1.join("Age").inner(true, table2, table3, table4);
        assert joined.columnNames().containsAll(Arrays.asList("T2.Name", "T2.HOME", "T2.MoveInDate", "T3.Name", "T3.Home", "T4.Name", "T4.Home", "Color"));
        Assertions.assertEquals(14, joined.columnCount());
        Assertions.assertEquals(18, joined.rowCount());
    }

    @Test
    public void innerJoinAnimalPeopleTreeOnAgeHome() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table table3 = DataFrameJoinerTest.createTREE();
        Table table4 = DataFrameJoinerTest.createFLOWER();
        Table joined = table1.join("Age", "Home").inner(true, table2, table3, table4);
        assert joined.columnNames().containsAll(Arrays.asList("Animal", "Name", "Home", "Age", "MoveInDate", "ID", "T2.Name", "T2.MoveInDate", "T3.Name", "T4.Name", "Color"));
        Assertions.assertEquals(11, joined.columnCount());
        Assertions.assertEquals(2, joined.rowCount());
    }

    @Test
    public void innerJoinOnNameHomeAge() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join("Name", "Home", "Age").inner(true, table2);
        Assertions.assertEquals(7, joined.columnCount());
        Assertions.assertEquals(1, joined.rowCount());
    }

    @Test
    public void innerJoinOnAllMismatchedColNames() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENicknameDwellingYearsMoveInDate();
        Table joined = table1.join("Name", "Home", "Age").inner(table2, true, "Nickname", "Dwelling", "Years");
        Assertions.assertEquals(7, joined.columnCount());
        Assertions.assertEquals(2, joined.rowCount());
    }

    @Test
    public void innerJoinOnPartiallyMismatchedColNames() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate();
        Table joined = table1.join("Name", "Home", "Age").inner(table2, true, "Name", "Dwelling", "Years");
        assert joined.columnNames().containsAll(Arrays.asList("Name", "Home", "Age"));
        Assertions.assertEquals(7, joined.columnCount());
        Assertions.assertEquals(2, joined.rowCount());
    }

    @Test
    public void leftOuterJoinOnPartiallyMismatchedColNames() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate();
        Table joined = table1.join("Name", "Home", "Age").leftOuter(table2, true, "Name", "Dwelling", "Years");
        assert joined.columnNames().containsAll(Arrays.asList("Name", "Home", "Age"));
        Assertions.assertEquals(7, joined.columnCount());
        Assertions.assertEquals(8, joined.rowCount());
    }

    @Test
    public void rightOuterJoinOnPartiallyMismatchedColNames() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate();
        Table joined = table1.join("Name", "Home", "Age").rightOuter(table2, true, "Name", "Dwelling", "Years");
        assert joined.columnNames().containsAll(Arrays.asList("Name", "Dwelling", "Years"));
        Assertions.assertEquals(7, joined.columnCount());
        Assertions.assertEquals(6, joined.rowCount());
    }

    @Test
    public void innerJoinOnAgeMoveInDate() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join("Age", "MoveInDate").inner(true, table2);
        Assertions.assertEquals(8, joined.columnCount());
        Assertions.assertEquals(3, joined.rowCount());
    }

    @Test
    public void leftOuterJoinOnAgeMoveInDate() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join("Age", "MoveInDate").leftOuter(true, table2);
        Assertions.assertEquals(8, joined.columnCount());
        Assertions.assertEquals(9, joined.rowCount());
    }

    @Test
    public void rightOuterJoinOnAgeMoveInDate() {
        Table table1 = DataFrameJoinerTest.createANIMALHOMES();
        Table table2 = DataFrameJoinerTest.createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join("Age", "MoveInDate").rightOuter(true, table2);
        Assertions.assertEquals(8, joined.columnCount());
        Assertions.assertEquals(6, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnPlayDate() {
        Table table1 = DataFrameJoinerTest.createFOOTBALLSCHEDULE();
        Table table2 = DataFrameJoinerTest.createSOCCERSCHEDULE();
        Table joined = table1.join("PlayDate").inner(true, table2);
        Assertions.assertEquals(8, joined.columnCount());
        Assertions.assertEquals(5, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnPlayTime() {
        Table table1 = DataFrameJoinerTest.createFOOTBALLSCHEDULE();
        Table table2 = DataFrameJoinerTest.createSOCCERSCHEDULE();
        Table joined = table1.join("PlayTime").inner(true, table2);
        Assertions.assertEquals(8, joined.columnCount());
        Assertions.assertEquals(3, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnPlayDatePlayTime() {
        Table table1 = DataFrameJoinerTest.createFOOTBALLSCHEDULE();
        Table table2 = DataFrameJoinerTest.createSOCCERSCHEDULE();
        Table joined = table1.join("PlayDate", "PlayTime").inner(true, table2);
        Assertions.assertEquals(7, joined.columnCount());
        Assertions.assertEquals(2, joined.rowCount());
    }

    @Test
    public void fullOuterJoinFootballSoccerOnPlayTime() {
        Table table1 = DataFrameJoinerTest.createFOOTBALLSCHEDULE();
        Table table2 = DataFrameJoinerTest.createSOCCERSCHEDULE();
        Table joined = table1.join("PlayTime").fullOuter(true, table2);
        Assertions.assertEquals(8, joined.columnCount());
        Assertions.assertEquals(5, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnPlayDatePlayDateTime() {
        Table table1 = DataFrameJoinerTest.createFOOTBALLSCHEDULEDateTime();
        Table table2 = DataFrameJoinerTest.createSOCCERSCHEDULEDateTime();
        Table joined = table1.join("PlayDateTime").inner(true, table2);
        Assertions.assertEquals(10, joined.columnCount());
        Assertions.assertEquals(2, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnSeasonRevenue() {
        Table table1 = DataFrameJoinerTest.createFOOTBALLSCHEDULEDateTime();
        Table table2 = DataFrameJoinerTest.createSOCCERSCHEDULEDateTime();
        Table joined = table1.join("SeasonRevenue", "AllTimeRevenue").inner(true, table2);
        Assertions.assertEquals(9, joined.columnCount());
        Assertions.assertEquals(1, joined.rowCount());
    }

    @Test
    public void fullOuterJoinFootballSoccerOnPlayDateTimeSeasonRevenue() {
        Table table1 = DataFrameJoinerTest.createFOOTBALLSCHEDULEDateTime();
        Table table2 = DataFrameJoinerTest.createSOCCERSCHEDULEDateTime();
        Table joined = table1.join("PlayDateTime", "SeasonRevenue").fullOuter(true, table2);
        Assertions.assertEquals(9, joined.columnCount());
        Assertions.assertEquals(6, joined.rowCount());
        Assertions.assertEquals(6, joined.column("TeamName").size());
        Assertions.assertEquals(2, joined.column("TeamName").countMissing());
        Assertions.assertEquals(6, joined.column("PlayDateTime").size());
        Assertions.assertEquals(0, joined.column("PlayDateTime").countMissing());
        Assertions.assertEquals(6, joined.column("Location").size());
        Assertions.assertEquals(2, joined.column("Location").countMissing());
        Assertions.assertEquals(6, joined.column("HomeGame").size());
        Assertions.assertEquals(2, joined.column("HomeGame").countMissing());
        Assertions.assertEquals(6, joined.column("SeasonRevenue").size());
        Assertions.assertEquals(0, joined.column("SeasonRevenue").countMissing());
        Assertions.assertEquals(6, joined.column("Mascot").size());
        Assertions.assertEquals(2, joined.column("Mascot").countMissing());
        Assertions.assertEquals(6, joined.column("Place").size());
        Assertions.assertEquals(2, joined.column("Place").countMissing());
    }

    @Test
    public void fullOuterJoinFootballSoccerOnPlayDateTimeAllTimeRevenue() {
        Table table1 = DataFrameJoinerTest.createFOOTBALLSCHEDULEDateTime();
        Table table2 = DataFrameJoinerTest.createSOCCERSCHEDULEDateTime();
        Table joined = table1.join("AllTimeRevenue").fullOuter(true, table2);
        Assertions.assertEquals(10, joined.columnCount());
        Assertions.assertEquals(5, joined.rowCount());
        Assertions.assertEquals(5, joined.column("TeamName").size());
        Assertions.assertEquals(1, joined.column("TeamName").countMissing());
        Assertions.assertEquals(5, joined.column("PlayDateTime").size());
        Assertions.assertEquals(1, joined.column("PlayDateTime").countMissing());
        Assertions.assertEquals(5, joined.column("Location").size());
        Assertions.assertEquals(1, joined.column("Location").countMissing());
        Assertions.assertEquals(5, joined.column("HomeGame").size());
        Assertions.assertEquals(1, joined.column("HomeGame").countMissing());
        Assertions.assertEquals(5, joined.column("SeasonRevenue").size());
        Assertions.assertEquals(1, joined.column("SeasonRevenue").countMissing());
        Assertions.assertEquals(5, joined.column("AllTimeRevenue").size());
        Assertions.assertEquals(0, joined.column("AllTimeRevenue").countMissing());
        Assertions.assertEquals(5, joined.column("Mascot").size());
        Assertions.assertEquals(1, joined.column("Mascot").countMissing());
        Assertions.assertEquals(5, joined.column("T2.PlayDateTime").size());
        Assertions.assertEquals(1, joined.column("T2.PlayDateTime").countMissing());
        Assertions.assertEquals(5, joined.column("Place").size());
        Assertions.assertEquals(1, joined.column("Place").countMissing());
        Assertions.assertEquals(5, joined.column("T2.SeasonRevenue").size());
        Assertions.assertEquals(1, joined.column("T2.SeasonRevenue").countMissing());
    }

    @Test
    public void fullOuterJoinFootballBaseballBoolean() {
        Table table1 = DataFrameJoinerTest.createFOOTBALLSCHEDULE();
        Table table2 = DataFrameJoinerTest.createBASEBALLSCHEDULEDateTime();
        Table joined = table1.join("HomeGame").fullOuter(true, table2);
        Assertions.assertEquals(10, joined.columnCount());
        Assertions.assertEquals(8, joined.rowCount());
    }
}

