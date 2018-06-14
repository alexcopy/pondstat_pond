package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.WaterChange;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the WaterChange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WaterChangeRepository extends JpaRepository<WaterChange, Long> {

}
