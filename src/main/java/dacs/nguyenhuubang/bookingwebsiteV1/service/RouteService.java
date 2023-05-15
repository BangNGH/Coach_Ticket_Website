package dacs.nguyenhuubang.bookingwebsiteV1.service;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Route;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Vehicle;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.RouteNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    public List<Route> getRoutes() {
        return (List<Route>)routeRepository.findAll();
    }

    public Route save(Route route) {
        routeRepository.save(route);
        return route;
    }

    public Route get(Integer id){
        Optional<Route> result = routeRepository.findById(id);
        if (result.isPresent()){
            return result.get();
        }
        else
            throw new RouteNotFoundException("Could not find any route with ID: "+id+"!");
    }

    public void delete(Integer id) {
        Long count = routeRepository.countById(id);
        if (count == null || count == 0) {
            throw new RouteNotFoundException("Could not find any route with ID " + id);
        }
        routeRepository.deleteById(id);
    }
    public List<Route> search(String keyword) {
        if (keyword != null) {
            return routeRepository.search(keyword);
        }
        return routeRepository.findAll();
    }

    public Page<Route> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.routeRepository.findAll(pageable);
    }
}
