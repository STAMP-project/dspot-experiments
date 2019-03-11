package com.car.app.web.rest;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import com.car.app.CarappApp;
import com.car.app.domain.Car;
import com.car.app.repository.CarRepository;
import com.car.app.web.rest.errors.ExceptionTranslator;
import java.util.List;
import javax.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test class for the CarResource REST controller.
 *
 * @see CarResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CarappApp.class)
public class CarResourceIntegrationTest {
    private static final String DEFAULT_MAKE = "AAAAAAAAAA";

    private static final String UPDATED_MAKE = "BBBBBBBBBB";

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";

    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 1.0;

    private static final Double UPDATED_PRICE = 2.0;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCarMockMvc;

    private Car car;

    @Test
    @Transactional
    public void createCar() throws Exception {
        int databaseSizeBeforeCreate = carRepository.findAll().size();
        // Create the Car
        restCarMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(car))).andExpect(status().isCreated());
        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize((databaseSizeBeforeCreate + 1));
        Car testCar = carList.get(((carList.size()) - 1));
        assertThat(testCar.getMake()).isEqualTo(CarResourceIntegrationTest.DEFAULT_MAKE);
        assertThat(testCar.getBrand()).isEqualTo(CarResourceIntegrationTest.DEFAULT_BRAND);
        assertThat(testCar.getPrice()).isEqualTo(CarResourceIntegrationTest.DEFAULT_PRICE);
    }

    @Test
    @Transactional
    public void createCarWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = carRepository.findAll().size();
        // Create the Car with an existing ID
        car.setId(1L);
        // An entity with an existing ID cannot be created, so this API call must fail
        restCarMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(car))).andExpect(status().isBadRequest());
        // Validate the Alice in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCars() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);
        // Get all the carList
        restCarMockMvc.perform(get("/api/cars?sort=id,desc")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(car.getId().intValue()))).andExpect(jsonPath("$.[*].make").value(Matchers.hasItem(CarResourceIntegrationTest.DEFAULT_MAKE.toString()))).andExpect(jsonPath("$.[*].brand").value(Matchers.hasItem(CarResourceIntegrationTest.DEFAULT_BRAND.toString()))).andExpect(jsonPath("$.[*].price").value(Matchers.hasItem(CarResourceIntegrationTest.DEFAULT_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    public void getCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);
        // Get the car
        restCarMockMvc.perform(get("/api/cars/{id}", car.getId())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(car.getId().intValue())).andExpect(jsonPath("$.make").value(CarResourceIntegrationTest.DEFAULT_MAKE.toString())).andExpect(jsonPath("$.brand").value(CarResourceIntegrationTest.DEFAULT_BRAND.toString())).andExpect(jsonPath("$.price").value(CarResourceIntegrationTest.DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCar() throws Exception {
        // Get the car
        restCarMockMvc.perform(get("/api/cars/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        // Update the car
        Car updatedCar = carRepository.findOne(car.getId());
        updatedCar.make(CarResourceIntegrationTest.UPDATED_MAKE).brand(CarResourceIntegrationTest.UPDATED_BRAND).price(CarResourceIntegrationTest.UPDATED_PRICE);
        restCarMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(updatedCar))).andExpect(status().isOk());
        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize(databaseSizeBeforeUpdate);
        Car testCar = carList.get(((carList.size()) - 1));
        assertThat(testCar.getMake()).isEqualTo(CarResourceIntegrationTest.UPDATED_MAKE);
        assertThat(testCar.getBrand()).isEqualTo(CarResourceIntegrationTest.UPDATED_BRAND);
        assertThat(testCar.getPrice()).isEqualTo(CarResourceIntegrationTest.UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void updateNonExistingCar() throws Exception {
        int databaseSizeBeforeUpdate = carRepository.findAll().size();
        // Create the Car
        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCarMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(car))).andExpect(status().isCreated());
        // Validate the Car in the database
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize((databaseSizeBeforeUpdate + 1));
    }

    @Test
    @Transactional
    public void deleteCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);
        int databaseSizeBeforeDelete = carRepository.findAll().size();
        // Get the car
        restCarMockMvc.perform(delete("/api/cars/{id}", car.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());
        // Validate the database is empty
        List<Car> carList = carRepository.findAll();
        assertThat(carList).hasSize((databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Car.class);
    }
}

