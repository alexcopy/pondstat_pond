package ru.gpsbox.natmob.service.mapper;

import ru.gpsbox.natmob.domain.*;
import ru.gpsbox.natmob.service.dto.WaterChangeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity WaterChange and its DTO WaterChangeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface WaterChangeMapper extends EntityMapper<WaterChangeDTO, WaterChange> {



    default WaterChange fromId(Long id) {
        if (id == null) {
            return null;
        }
        WaterChange waterChange = new WaterChange();
        waterChange.setId(id);
        return waterChange;
    }
}
