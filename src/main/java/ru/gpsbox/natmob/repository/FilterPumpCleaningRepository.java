package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.FilterPumpCleaning;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the FilterPumpCleaning entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FilterPumpCleaningRepository extends JpaRepository<FilterPumpCleaning, Long> {

}
