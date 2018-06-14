package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.TempMeter;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the TempMeter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TempMeterRepository extends JpaRepository<TempMeter, Long> {

}
