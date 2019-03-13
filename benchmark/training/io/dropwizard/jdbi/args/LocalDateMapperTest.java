package io.dropwizard.jdbi.args;


import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;


public class LocalDateMapperTest {
    private final ResultSet resultSet = Mockito.mock(ResultSet.class);

    private final StatementContext ctx = Mockito.mock(StatementContext.class);

    @Test
    public void mapColumnByName() throws Exception {
        Mockito.when(resultSet.getTimestamp("name")).thenReturn(Timestamp.valueOf("2007-12-03 00:00:00.000"));
        LocalDate actual = new LocalDateMapper().mapColumn(resultSet, "name", ctx);
        assertThat(actual).isEqualTo(LocalDate.parse("2007-12-03"));
    }

    @Test
    public void mapColumnByName_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp("name")).thenReturn(null);
        LocalDate actual = new LocalDateMapper().mapColumn(resultSet, "name", ctx);
        assertThat(actual).isNull();
    }

    @Test
    public void mapColumnByIndex() throws Exception {
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(Timestamp.valueOf("2007-12-03 00:00:00.000"));
        LocalDate actual = new LocalDateMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isEqualTo(LocalDate.parse("2007-12-03"));
    }

    @Test
    public void mapColumnByIndex_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(null);
        LocalDate actual = new LocalDateMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isNull();
    }
}

