package ru.gpsbox.natmob.service.mapper;

import ru.gpsbox.natmob.domain.*;
import ru.gpsbox.natmob.service.dto.TempMeterDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity TempMeter and its DTO TempMeterDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TempMeterMapper extends EntityMapper<TempMeterDTO, TempMeter> {



    default TempMeter fromId(Long id) {
        if (id == null) {
            return null;
        }
        TempMeter tempMeter = new TempMeter();
        tempMeter.setId(id);
        return tempMeter;
    }
}
