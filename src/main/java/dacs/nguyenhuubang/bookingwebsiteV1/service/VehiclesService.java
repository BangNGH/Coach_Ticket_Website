package dacs.nguyenhuubang.bookingwebsiteV1.service;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Vehicle;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.UserAlreadyExistsException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VehiclesService {
    private final VehicleRepository vehicleRepository;

    public List<Vehicle> getList() {
        return (List<Vehicle>)vehicleRepository.findAll();
    }

    public Vehicle save(Vehicle vehicle) {
        try{
           vehicleRepository.save(vehicle);
            return vehicle;
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new UserAlreadyExistsException("License Plates " + vehicle.getLicensePlates() + " already exists");
            } else {
                throw e;
            }
        }

    }

    public Vehicle get(Integer id){
        Optional<Vehicle> result = vehicleRepository.findById(id);
        if (result.isPresent()){
            return result.get();
        }
        else
            throw new VehicleNotFoundException("Not found vehicle with ID: "+id+"!");
    }

    public void delete(Integer id) {
        Long count = vehicleRepository.countById(id);
        if (count == null || count == 0) {
            throw new VehicleNotFoundException("Not found vehicle with ID: "+id+"!");
        }
        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> search(String keyword) {
        if (keyword != null) {
            return vehicleRepository.search(keyword);
        }
        return vehicleRepository.findAll();
    }

    public Page<Vehicle> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.vehicleRepository.findAll(pageable);
    }
}
