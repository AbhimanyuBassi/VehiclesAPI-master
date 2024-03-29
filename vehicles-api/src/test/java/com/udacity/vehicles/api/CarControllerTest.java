package com.udacity.vehicles.api;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }


    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }


    @Test
    public void listCars() throws Exception {
        mvc.perform(get("/cars").header("Content-Type",MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*.carList", hasSize(1)))
                .andExpect(jsonPath("$.*.carList[0].id").value(1))
                .andExpect(jsonPath("$.*.carList[0].details.model").value("Impala"));
    }


    @Test
    public void findCar() throws Exception {
        Car car = getCar();
        car.setId(1L);
        long id = car.getId();

        mvc.perform(get("/cars/" + id).header("Content-Type",MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.details.model").value("Impala"));
    }

    @Test
    public void updateCar() throws Exception {
        Car car = getCar();
        car.setId(1L);
        long id = car.getId();

        mvc.perform(get("/cars/" + id).header("Content-Type",MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.condition").value("NEW"));

        car.setCondition(Condition.USED);

        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);

        mvc.perform(put("/cars/" + id).header("Content-Type",MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(json.write(car).getJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.condition").value("USED"));
    }


    @Test
    public void deleteCar() throws Exception {
        Car car = getCar();
        car.setId(1L);
        long id = car.getId();

        mvc.perform(delete("/cars/" + id).header("Content-Type",MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNoContent());

    }


    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.NEW);
        return car;
    }
}