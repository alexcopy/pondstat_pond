package ru.gpsbox.natmob.service.mapper;

import ru.gpsbox.natmob.domain.*;
import ru.gpsbox.natmob.service.dto.MeterReadingDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity MeterReading and its DTO MeterReadingDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MeterReadingMapper extends EntityMapper<MeterReadingDTO, MeterReading> {



    default MeterReading fromId(Long id) {
        if (id == null) {
            return null;
        }
        MeterReading meterReading = new MeterReading();
        meterReading.setId(id);
        return meterReading;
    }
}
