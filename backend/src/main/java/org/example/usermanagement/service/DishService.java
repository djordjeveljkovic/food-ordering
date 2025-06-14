package org.example.usermanagement.service;

import org.example.usermanagement.entity.Dish;
import org.example.usermanagement.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    @Autowired
    private DishRepository dishRepository;

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public Optional<Dish> getDishById(Long id) {
        return dishRepository.findById(id);
    }

    public Dish createDish(Dish dish) {
        return dishRepository.save(dish);
    }

    public Dish updateDish(Long id, Dish dish) {
        Optional<Dish> existingDish = dishRepository.findById(id);
        if (existingDish.isPresent()) {
            Dish updatedDish = existingDish.get();
            updatedDish.setName(dish.getName());
            updatedDish.setDescription(dish.getDescription());
            updatedDish.setPrice(dish.getPrice());
            return dishRepository.save(updatedDish);
        } else {
            throw new IllegalArgumentException("Dish not found with id: " + id);
        }
    }

    public void deleteDish(Long id) {
        if (dishRepository.existsById(id)) {
            dishRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Dish not found with id: " + id);
        }
    }
}