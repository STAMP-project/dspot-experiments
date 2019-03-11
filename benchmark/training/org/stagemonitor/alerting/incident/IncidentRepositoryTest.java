package org.stagemonitor.alerting.incident;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.stagemonitor.AbstractElasticsearchTest;


@RunWith(Parameterized.class)
public class IncidentRepositoryTest<T extends IncidentRepository> extends AbstractElasticsearchTest {
    private final T incidentRepository;

    public IncidentRepositoryTest(T incidentRepository, Class<T> clazz) {
        this.incidentRepository = incidentRepository;
        if (incidentRepository instanceof ElasticsearchIncidentRepository) {
            final ElasticsearchIncidentRepository elasticsearchIncidentRepository = ((ElasticsearchIncidentRepository) (incidentRepository));
            elasticsearchIncidentRepository.setElasticsearchClient(elasticsearchClient);
        }
    }

    @Test
    public void testSaveAndGet() throws Exception {
        Incident incident = IncidentRepositoryTest.createIncidentWithVersion("id1", 1);
        Assert.assertTrue(incidentRepository.createIncident(incident));
        IncidentRepositoryTest.refresh();
        assertIncidentEquals(incidentRepository.getIncidentByCheckId(incident.getCheckId()), incident);
        assertIncidentEquals(getAllIncidents().iterator().next(), incident);
    }

    @Test
    public void testGetNotPresent() throws Exception {
        Assert.assertNull(getIncidentByCheckId("testGetNotPresent"));
        Assert.assertTrue(getAllIncidents().isEmpty());
    }

    @Test
    public void testAlreadyCreated() {
        Assert.assertTrue(incidentRepository.createIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 1)));
        Assert.assertFalse(incidentRepository.createIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 1)));
    }

    @Test
    public void testWrongVersion() {
        Assert.assertTrue(incidentRepository.createIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 1)));
        Assert.assertFalse(incidentRepository.updateIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 1)));
        Assert.assertTrue(incidentRepository.updateIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 2)));
    }

    @Test
    public void testDelete() throws Exception {
        Assert.assertTrue(incidentRepository.createIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 1)));
        Assert.assertTrue(incidentRepository.deleteIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 2)));
        Assert.assertNull(getIncidentByCheckId("id1"));
        Assert.assertTrue(getAllIncidents().isEmpty());
    }

    @Test
    public void testDeleteWrongVersion() throws Exception {
        Assert.assertTrue(incidentRepository.createIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 1)));
        Assert.assertFalse(incidentRepository.deleteIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 1)));
        Assert.assertFalse(incidentRepository.deleteIncident(IncidentRepositoryTest.createIncidentWithVersion("id1", 0)));
    }
}

