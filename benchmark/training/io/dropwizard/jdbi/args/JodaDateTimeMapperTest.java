package io.dropwizard.jdbi.args;


import java.sql.ResultSet;
import java.sql.Timestamp;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;


public class JodaDateTimeMapperTest {
    private final ResultSet resultSet = Mockito.mock(ResultSet.class);

    private final StatementContext ctx = Mockito.mock(StatementContext.class);

    @Test
    public void mapColumnByName() throws Exception {
        Mockito.when(resultSet.getTimestamp("name")).thenReturn(Timestamp.valueOf("2007-12-03 10:15:30.375"));
        DateTime actual = new JodaDateTimeMapper().mapColumn(resultSet, "name", ctx);
        assertThat(actual).isEqualTo(DateTime.parse("2007-12-03T10:15:30.375"));
    }

    @Test
    public void mapColumnByName_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp("name")).thenReturn(null);
        DateTime actual = new JodaDateTimeMapper().mapColumn(resultSet, "name", ctx);
        assertThat(actual).isNull();
    }

    @Test
    public void mapColumnByIndex() throws Exception {
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(Timestamp.valueOf("2007-12-03 10:15:30.375"));
        DateTime actual = new JodaDateTimeMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isEqualTo(DateTime.parse("2007-12-03T10:15:30.375"));
    }

    @Test
    public void mapColumnByIndex_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(null);
        DateTime actual = new JodaDateTimeMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isNull();
    }
}

