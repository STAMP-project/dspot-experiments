package water.fvec;


import hex.CreateFrame;
import org.junit.Test;
import water.TestUtil;
import water.api.schemas3.TwoDimTableV3;


public class ChunkSummaryTest extends TestUtil {
    @Test
    public void run() {
        CreateFrame cf = new CreateFrame();
        cf.seed = 1234;
        Frame f = cf.execImpl().get();
        ChunkSummary cs = FrameUtils.chunkSummary(f);
        TwoDimTable chunk_summary_table = cs.toTwoDimTableChunkTypes();
        Log.info(chunk_summary_table);
        TwoDimTableV3 td = new TwoDimTableV3().fillFromImpl(chunk_summary_table);
        String json = td.toJsonString();
        // if (H2O.CLOUD.size() == 1) {
        // Assert.assertEquals("{\"__meta\":{\"schema_version\":3,\"schema_name\":\"TwoDimTableV3\"," +
        // "\"schema_type\":\"TwoDimTable\"},\"name\":\"Chunk compression summary\",\"description\":\"\",\"columns\":[{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"chunk_type\",\"type\":\"string\",\"format\":\"%8s\",\"description\":\"Chunk Type\"},{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"chunk_name\",\"type\":\"string\",\"format\":\"%s\",\"description\":\"Chunk Name\"},{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"count\",\"type\":\"int\",\"format\":\"%10d\",\"description\":\"Count\"},{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"count_percentage\",\"type\":\"float\",\"format\":\"%10.3f %%\",\"description\":\"Count Percentage\"},{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"size\",\"type\":\"string\",\"format\":\"%10s\",\"description\":\"Size\"},{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"size_percentage\",\"type\":\"float\",\"format\":\"%10.3f %%\",\"description\":\"Size Percentage\"}],\"rowcount\":4,\"data\":[[\"CXI\",\"C1\",\"C1S\",\"C8D\"],[\"Sparse Integers\",\"1-Byte Integers\",\"1-Byte Fractions\",\"64-bit Reals\"],[9,18,18,45],[10.0,20.0,20.0,50.0],[\"    1.9 KB\",\"   20.7 KB\",\"   21.0 KB\",\"  393.6 KB\"],[0.42528477,4.7406745,4.8050036,90.02904]]}"
        // ,json);
        // }
        TwoDimTable distribution_summary_table = cs.toTwoDimTableDistribution();
        Log.info(distribution_summary_table);
        json = new TwoDimTableV3().fillFromImpl(distribution_summary_table).toJsonString();
        // if (H2O.CLOUD.size() == 1) {
        // Assert.assertEquals("{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"TwoDimTableV3\"," +
        // "\"schema_type\":\"TwoDimTable\"},\"name\":\"Frame distribution summary\",\"description\":\"\",\"columns\":[{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"\",\"type\":\"string\",\"format\":\"%s\",\"description\":\"\"},{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"size\",\"type\":\"string\",\"format\":\"%s\",\"description\":\"Size\"},{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"number_of_rows\",\"type\":\"float\",\"format\":\"%f\",\"description\":\"Number of Rows\"},{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"number_of_chunks_per_column\",\"type\":\"float\",\"format\":\"%f\",\"description\":\"Number of Chunks per Column\"},{\"__meta\":{\"schema_version\":-1,\"schema_name\":\"ColumnSpecsBase\",\"schema_type\":\"Iced\"},\"name\":\"number_of_chunks\",\"type\":\"float\",\"format\":\"%f\",\"description\":\"Number of Chunks\"}],\"rowcount\":6,\"data\":[[\"172.16.2.81:54321\",\"mean\",\"min\",\"max\",\"stddev\",\"total\"],[\"  436.9 KB\",\"  436.9 KB\",\"  436.9 KB\",\"  436.9 KB\",\"      0  B\",\"  436.9 KB\"],[10000.0,10000.0,10000.0,10000.0,0.0,10000.0],[9.0,9.0,9.0,9.0,0.0,9.0],[90.0,90.0,90.0,90.0,0.0,90.0]]}", json);
        // }
        f.remove();
    }
}

