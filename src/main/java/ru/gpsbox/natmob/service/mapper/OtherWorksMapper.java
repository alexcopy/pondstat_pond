package ru.gpsbox.natmob.service.mapper;

import ru.gpsbox.natmob.domain.*;
import ru.gpsbox.natmob.service.dto.OtherWorksDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity OtherWorks and its DTO OtherWorksDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface OtherWorksMapper extends EntityMapper<OtherWorksDTO, OtherWorks> {



    default OtherWorks fromId(Long id) {
        if (id == null) {
            return null;
        }
        OtherWorks otherWorks = new OtherWorks();
        otherWorks.setId(id);
        return otherWorks;
    }
}
