package ru.gpsbox.natmob.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the MeterReading entity.
 */
public class MeterReadingDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime readingDate;

    private String description;

    @NotNull
    private Double reading;

    @NotNull
    private Double tempVal;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getReading() {
        return reading;
    }

    public void setReading(Double reading) {
        this.reading = reading;
    }

    public Double getTempVal() {
        return tempVal;
    }

    public void setTempVal(Double tempVal) {
        this.tempVal = tempVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MeterReadingDTO meterReadingDTO = (MeterReadingDTO) o;
        if(meterReadingDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), meterReadingDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MeterReadingDTO{" +
            "id=" + getId() +
            ", readingDate='" + getReadingDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", reading=" + getReading() +
            ", tempVal=" + getTempVal() +
            "}";
    }
}
