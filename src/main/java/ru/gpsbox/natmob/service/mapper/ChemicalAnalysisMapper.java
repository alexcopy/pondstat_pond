package ru.gpsbox.natmob.service.mapper;

import ru.gpsbox.natmob.domain.*;
import ru.gpsbox.natmob.service.dto.ChemicalAnalysisDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ChemicalAnalysis and its DTO ChemicalAnalysisDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ChemicalAnalysisMapper extends EntityMapper<ChemicalAnalysisDTO, ChemicalAnalysis> {



    default ChemicalAnalysis fromId(Long id) {
        if (id == null) {
            return null;
        }
        ChemicalAnalysis chemicalAnalysis = new ChemicalAnalysis();
        chemicalAnalysis.setId(id);
        return chemicalAnalysis;
    }
}
