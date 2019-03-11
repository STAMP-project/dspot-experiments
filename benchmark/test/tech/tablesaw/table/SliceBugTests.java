package tech.tablesaw.table;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;


public class SliceBugTests {
    private final Integer[] observations = new Integer[]{ 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };

    private final LocalDateTime[] timestamps = new LocalDateTime[]{ LocalDateTime.of(2018, 1, 1, 13, 1, 1), LocalDateTime.of(2018, 1, 1, 13, 1, 2), LocalDateTime.of(2018, 1, 1, 13, 1, 2), LocalDateTime.of(2018, 1, 1, 13, 1, 3), LocalDateTime.of(2018, 1, 1, 13, 1, 3), LocalDateTime.of(2018, 1, 1, 13, 1, 4), LocalDateTime.of(2018, 1, 1, 13, 1, 5), LocalDateTime.of(2018, 1, 1, 13, 1, 6), LocalDateTime.of(2018, 1, 1, 13, 1, 6), LocalDateTime.of(2018, 1, 1, 13, 1, 7) };

    private final String[] categories = new String[]{ "Australia", "Australia", "Australia", "Germany", "USA", "Finland", "Finland", "Japan", "Japan", "Chile" };

    @Test
    public void sliceColumnIsSameWhenRetrievedWithNameOrIndex() {
        Table table = constructTableFromArrays();
        TableSliceGroup countrySplit = table.splitOn("countries");
        for (TableSlice slice : countrySplit) {
            DoubleColumn priceColFromIndex = slice.doubleColumn(2);
            DoubleColumn priceColFromName = slice.doubleColumn("price");
            Assertions.assertTrue(Arrays.equals(priceColFromName.asDoubleArray(), priceColFromIndex.asDoubleArray()), "Columns should have same data");
        }
    }

    @Test
    public void sliceAsTableUsingDatesAfterFilteringDBLoadedTable() throws SQLException {
        Table salesTable = loadTableFromDB();
        Table filteredTable = salesTable.select(salesTable.columnNames().toArray(new String[0])).where(salesTable.dateTimeColumn("sale_timestamp").isAfter(LocalDateTime.of(2018, 1, 1, 13, 1, 3)));
        filteredTable.setName("filteredTable");
        // work around
        TableSliceGroup slices = filteredTable.splitOn("countries");
        slices.forEach(( slice) -> {
            assertFalse(slice.isEmpty());
        });
    }
}

