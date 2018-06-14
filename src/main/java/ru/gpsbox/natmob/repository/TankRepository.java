package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.Tank;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Tank entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TankRepository extends JpaRepository<Tank, Long> {

}
