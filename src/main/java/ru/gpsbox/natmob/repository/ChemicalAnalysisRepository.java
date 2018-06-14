package ru.gpsbox.natmob.repository;

import ru.gpsbox.natmob.domain.ChemicalAnalysis;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ChemicalAnalysis entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChemicalAnalysisRepository extends JpaRepository<ChemicalAnalysis, Long> {

}
