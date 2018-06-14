package ru.gpsbox.natmob.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import ru.gpsbox.natmob.domain.enumeration.TankType;

/**
 * A DTO for the Tank entity.
 */
public class TankDTO implements Serializable {

    private Long id;

    @NotNull
    private String tankName;

    @NotNull
    private TankType tankType;

    private String description;

    @NotNull
    private Integer timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTankName() {
        return tankName;
    }

    public void setTankName(String tankName) {
        this.tankName = tankName;
    }

    public TankType getTankType() {
        return tankType;
    }

    public void setTankType(TankType tankType) {
        this.tankType = tankType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TankDTO tankDTO = (TankDTO) o;
        if(tankDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tankDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TankDTO{" +
            "id=" + getId() +
            ", tankName='" + getTankName() + "'" +
            ", tankType='" + getTankType() + "'" +
            ", description='" + getDescription() + "'" +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
