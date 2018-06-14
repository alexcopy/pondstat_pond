package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.Device;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Device entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

}
