package ru.gpsbox.natmob.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the FilterPumpCleaning entity.
 */
public class FilterPumpCleaningDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime cleaningDate;

    private String description;

    @NotNull
    private Double tempVal;

    private Integer timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCleaningDate() {
        return cleaningDate;
    }

    public void setCleaningDate(ZonedDateTime cleaningDate) {
        this.cleaningDate = cleaningDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTempVal() {
        return tempVal;
    }

    public void setTempVal(Double tempVal) {
        this.tempVal = tempVal;
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

        FilterPumpCleaningDTO filterPumpCleaningDTO = (FilterPumpCleaningDTO) o;
        if(filterPumpCleaningDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), filterPumpCleaningDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "FilterPumpCleaningDTO{" +
            "id=" + getId() +
            ", cleaningDate='" + getCleaningDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", tempVal=" + getTempVal() +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
