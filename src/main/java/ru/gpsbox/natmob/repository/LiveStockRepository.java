package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.LiveStock;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the LiveStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LiveStockRepository extends JpaRepository<LiveStock, Long> {

}
