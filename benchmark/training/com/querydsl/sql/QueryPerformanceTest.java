package com.querydsl.sql;


import JoinType.DEFAULT;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.testutil.Benchmark;
import com.querydsl.core.testutil.H2;
import com.querydsl.core.testutil.Performance;
import com.querydsl.core.testutil.Runner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ H2.class, Performance.class })
public class QueryPerformanceTest {
    private static final String QUERY = "select COMPANIES.NAME\n" + ("from COMPANIES COMPANIES\n" + "where COMPANIES.ID = ?");

    private static final SQLTemplates templates = new H2Templates();

    private static final Configuration conf = new Configuration(QueryPerformanceTest.templates);

    private final Connection conn = Connections.getConnection();

    @Test
    public void jDBC() throws Exception {
        Runner.run("jdbc by id", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    PreparedStatement stmt = conn.prepareStatement(QueryPerformanceTest.QUERY);
                    try {
                        stmt.setLong(1, i);
                        ResultSet rs = stmt.executeQuery();
                        try {
                            while (rs.next()) {
                                rs.getString(1);
                            } 
                        } finally {
                            rs.close();
                        }
                    } finally {
                        stmt.close();
                    }
                }
            }
        });
    }

    @Test
    public void jDBC2() throws Exception {
        Runner.run("jdbc by name", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    PreparedStatement stmt = conn.prepareStatement(QueryPerformanceTest.QUERY);
                    try {
                        stmt.setString(1, String.valueOf(i));
                        ResultSet rs = stmt.executeQuery();
                        try {
                            while (rs.next()) {
                                rs.getString(1);
                            } 
                        } finally {
                            rs.close();
                        }
                    } finally {
                        stmt.close();
                    }
                }
            }
        });
    }

    @Test
    public void querydsl1() throws Exception {
        Runner.run("qdsl by id", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    QCompanies companies = QCompanies.companies;
                    SQLQuery<?> query = new SQLQuery<Void>(conn, QueryPerformanceTest.conf);
                    query.from(companies).where(companies.id.eq(((long) (i)))).select(companies.name).fetch();
                }
            }
        });
    }

    @Test
    public void querydsl12() throws Exception {
        Runner.run("qdsl by id (iterated)", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    QCompanies companies = QCompanies.companies;
                    SQLQuery<?> query = new SQLQuery<Void>(conn, QueryPerformanceTest.conf);
                    CloseableIterator<String> it = query.from(companies).where(companies.id.eq(((long) (i)))).select(companies.name).iterate();
                    try {
                        while (it.hasNext()) {
                            it.next();
                        } 
                    } finally {
                        it.close();
                    }
                }
            }
        });
    }

    @Test
    public void querydsl13() throws Exception {
        Runner.run("qdsl by id (result set access)", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    QCompanies companies = QCompanies.companies;
                    SQLQuery<?> query = new SQLQuery<Void>(conn, QueryPerformanceTest.conf);
                    ResultSet rs = query.select(companies.name).from(companies).where(companies.id.eq(((long) (i)))).getResults();
                    try {
                        while (rs.next()) {
                            rs.getString(1);
                        } 
                    } finally {
                        rs.close();
                    }
                }
            }
        });
    }

    @Test
    public void querydsl14() throws Exception {
        Runner.run("qdsl by id (no validation)", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    QCompanies companies = QCompanies.companies;
                    SQLQuery<?> query = new SQLQuery<Void>(conn, QueryPerformanceTest.conf, new DefaultQueryMetadata());
                    query.from(companies).where(companies.id.eq(((long) (i)))).select(companies.name).fetch();
                }
            }
        });
    }

    @Test
    public void querydsl15() throws Exception {
        Runner.run("qdsl by id (two cols)", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    QCompanies companies = QCompanies.companies;
                    SQLQuery<?> query = new SQLQuery<Void>(conn, QueryPerformanceTest.conf);
                    query.from(companies).where(companies.id.eq(((long) (i)))).select(companies.id, companies.name).fetch();
                }
            }
        });
    }

    @Test
    public void querydsl2() throws Exception {
        Runner.run("qdsl by name", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    QCompanies companies = QCompanies.companies;
                    SQLQuery<?> query = new SQLQuery<Void>(conn, QueryPerformanceTest.conf);
                    query.from(companies).where(companies.name.eq(String.valueOf(i))).select(companies.name).fetch();
                }
            }
        });
    }

    @Test
    public void querydsl22() throws Exception {
        Runner.run("qdsl by name (iterated)", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    QCompanies companies = QCompanies.companies;
                    SQLQuery<?> query = new SQLQuery<Void>(conn, QueryPerformanceTest.conf);
                    CloseableIterator<String> it = query.from(companies).where(companies.name.eq(String.valueOf(i))).select(companies.name).iterate();
                    try {
                        while (it.hasNext()) {
                            it.next();
                        } 
                    } finally {
                        it.close();
                    }
                }
            }
        });
    }

    @Test
    public void querydsl23() throws Exception {
        Runner.run("qdsl by name (no validation)", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    QCompanies companies = QCompanies.companies;
                    SQLQuery<?> query = new SQLQuery<Void>(conn, QueryPerformanceTest.conf, new DefaultQueryMetadata());
                    query.from(companies).where(companies.name.eq(String.valueOf(i))).select(companies.name).fetch();
                }
            }
        });
    }

    @Test
    public void serialization() throws Exception {
        QCompanies companies = QCompanies.companies;
        final QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, companies);
        md.addWhere(companies.id.eq(1L));
        md.setProjection(companies.name);
        Runner.run("ser1", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    SQLSerializer serializer = new SQLSerializer(QueryPerformanceTest.conf);
                    serializer.serialize(md, false);
                    serializer.getConstants();
                    serializer.getConstantPaths();
                    Assert.assertNotNull(serializer.toString());
                }
            }
        });
    }
}

