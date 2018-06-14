package ru.gpsbox.natmob.service.mapper;

import ru.gpsbox.natmob.domain.*;
import ru.gpsbox.natmob.service.dto.TankDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Tank and its DTO TankDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TankMapper extends EntityMapper<TankDTO, Tank> {



    default Tank fromId(Long id) {
        if (id == null) {
            return null;
        }
        Tank tank = new Tank();
        tank.setId(id);
        return tank;
    }
}
