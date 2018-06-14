package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.OtherWorks;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the OtherWorks entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OtherWorksRepository extends JpaRepository<OtherWorks, Long> {

}
