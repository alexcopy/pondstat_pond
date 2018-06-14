package ru.gpsbox.natmob.service.mapper;

import ru.gpsbox.natmob.domain.*;
import ru.gpsbox.natmob.service.dto.ChemicalsDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Chemicals and its DTO ChemicalsDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ChemicalsMapper extends EntityMapper<ChemicalsDTO, Chemicals> {



    default Chemicals fromId(Long id) {
        if (id == null) {
            return null;
        }
        Chemicals chemicals = new Chemicals();
        chemicals.setId(id);
        return chemicals;
    }
}
