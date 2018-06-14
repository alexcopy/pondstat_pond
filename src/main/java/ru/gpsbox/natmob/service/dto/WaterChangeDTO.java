package ru.gpsbox.natmob.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the WaterChange entity.
 */
public class WaterChangeDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime changeDate;

    private String description;

    @NotNull
    private Double readingBefore;

    @NotNull
    private Double readingAfter;

    @NotNull
    private Double tempVal;

    private Integer timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(ZonedDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getReadingBefore() {
        return readingBefore;
    }

    public void setReadingBefore(Double readingBefore) {
        this.readingBefore = readingBefore;
    }

    public Double getReadingAfter() {
        return readingAfter;
    }

    public void setReadingAfter(Double readingAfter) {
        this.readingAfter = readingAfter;
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

        WaterChangeDTO waterChangeDTO = (WaterChangeDTO) o;
        if(waterChangeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), waterChangeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "WaterChangeDTO{" +
            "id=" + getId() +
            ", changeDate='" + getChangeDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", readingBefore=" + getReadingBefore() +
            ", readingAfter=" + getReadingAfter() +
            ", tempVal=" + getTempVal() +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
