package ru.gpsbox.natmob.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the TempMeter entity.
 */
public class TempMeterDTO implements Serializable {

    private Long id;

    private ZonedDateTime readingDate;

    @NotNull
    private Double tempVal;

    @NotNull
    private Integer timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getReadingDate() {
        return readingDate;
    }

    public void setReadingDate(ZonedDateTime readingDate) {
        this.readingDate = readingDate;
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

        TempMeterDTO tempMeterDTO = (TempMeterDTO) o;
        if(tempMeterDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tempMeterDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TempMeterDTO{" +
            "id=" + getId() +
            ", readingDate='" + getReadingDate() + "'" +
            ", tempVal=" + getTempVal() +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
