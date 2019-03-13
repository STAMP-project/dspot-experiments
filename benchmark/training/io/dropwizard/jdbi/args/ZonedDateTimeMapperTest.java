package io.dropwizard.jdbi.args;


import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;


public class ZonedDateTimeMapperTest {
    private final ResultSet resultSet = Mockito.mock(ResultSet.class);

    private final StatementContext ctx = Mockito.mock(StatementContext.class);

    @Test
    public void mapColumnByName() throws Exception {
        Mockito.when(resultSet.getTimestamp("name")).thenReturn(Timestamp.valueOf("2007-12-03 10:15:30.375"));
        ZonedDateTime actual = new ZonedDateTimeMapper().mapColumn(resultSet, "name", ctx);
        assertThat(actual).isEqualTo(ZonedDateTime.of(2007, 12, 3, 10, 15, 30, 375000000, ZoneId.systemDefault()));
    }

    @Test
    public void mapColumnByName_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp("name")).thenReturn(null);
        ZonedDateTime actual = new ZonedDateTimeMapper().mapColumn(resultSet, "name", ctx);
        assertThat(actual).isNull();
    }

    @Test
    public void mapColumnByIndex() throws Exception {
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(Timestamp.valueOf("2007-12-03 10:15:30.375"));
        ZonedDateTime actual = new ZonedDateTimeMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isEqualTo(ZonedDateTime.of(2007, 12, 3, 10, 15, 30, 375000000, ZoneId.systemDefault()));
    }

    @Test
    public void mapColumnByIndex_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(null);
        ZonedDateTime actual = new ZonedDateTimeMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isNull();
    }
}

