package de.aletutto.animal.picture.app.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AnimalRepository extends CrudRepository<Animal, Long> {
    Optional<Animal> findFirstByOrderByCreationDateDesc();
}
