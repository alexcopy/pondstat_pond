package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.Chemicals;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Chemicals entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChemicalsRepository extends JpaRepository<Chemicals, Long> {

}
