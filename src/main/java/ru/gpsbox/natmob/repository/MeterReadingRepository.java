package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.MeterReading;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the MeterReading entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

}
