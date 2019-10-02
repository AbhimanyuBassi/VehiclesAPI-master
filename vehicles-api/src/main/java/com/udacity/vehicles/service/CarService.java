package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository repository;
    private final MapsClient mapsClient;
    private final PriceClient priceClient;

    public CarService(CarRepository repository, MapsClient mapsClient, PriceClient priceClient) {
        this.repository = repository;
        this.mapsClient = mapsClient;
        this.priceClient = priceClient;
    }


    public List<Car> list() {
        return repository.findAll();
    }

    public Car findById(Long id) throws CarNotFoundException {
        Optional<Car> maybeACar = repository.findById(id);

        if (maybeACar.isPresent()) {
            Car car = maybeACar.get();
            car.setPrice(priceClient.getPrice(id));
            car.setLocation(mapsClient.getAddress(car.getLocation()));
            return car;
        } else {
            throw new CarNotFoundException("Car Not Found");
        }
    }

    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setCondition(car.getCondition());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    public void delete(Long id) {
        Optional<Car> car = repository.findById(id);

        if (car.isPresent()) {
            repository.delete(car.get());
        } else {
            throw new CarNotFoundException("Car Not Found");
        }

    }
}
