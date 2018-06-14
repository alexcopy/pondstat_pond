package ru.gpsbox.natmob.service.mapper;

import ru.gpsbox.natmob.domain.*;
import ru.gpsbox.natmob.service.dto.LiveStockDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity LiveStock and its DTO LiveStockDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LiveStockMapper extends EntityMapper<LiveStockDTO, LiveStock> {



    default LiveStock fromId(Long id) {
        if (id == null) {
            return null;
        }
        LiveStock liveStock = new LiveStock();
        liveStock.setId(id);
        return liveStock;
    }
}
