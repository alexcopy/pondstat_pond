package ru.gpsbox.natmob.service.mapper;

import ru.gpsbox.natmob.domain.*;
import ru.gpsbox.natmob.service.dto.FilterPumpCleaningDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity FilterPumpCleaning and its DTO FilterPumpCleaningDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FilterPumpCleaningMapper extends EntityMapper<FilterPumpCleaningDTO, FilterPumpCleaning> {



    default FilterPumpCleaning fromId(Long id) {
        if (id == null) {
            return null;
        }
        FilterPumpCleaning filterPumpCleaning = new FilterPumpCleaning();
        filterPumpCleaning.setId(id);
        return filterPumpCleaning;
    }
}
