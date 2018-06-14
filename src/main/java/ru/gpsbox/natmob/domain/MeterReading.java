package ru.gpsbox.natmob.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A MeterReading.
 */
@Entity
@Table(name = "meter_reading")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "meterreading")
public class MeterReading implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "reading_date", nullable = false)
    private ZonedDateTime readingDate;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "reading", nullable = false)
    private Double reading;

    @NotNull
    @Column(name = "temp_val", nullable = false)
    private Double tempVal;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getReadingDate() {
        return readingDate;
    }

    public MeterReading readingDate(ZonedDateTime readingDate) {
        this.readingDate = readingDate;
        return this;
    }

    public void setReadingDate(ZonedDateTime readingDate) {
        this.readingDate = readingDate;
    }

    public String getDescription() {
        return description;
    }

    public MeterReading description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getReading() {
        return reading;
    }

    public MeterReading reading(Double reading) {
        this.reading = reading;
        return this;
    }

    public void setReading(Double reading) {
        this.reading = reading;
    }

    public Double getTempVal() {
        return tempVal;
    }

    public MeterReading tempVal(Double tempVal) {
        this.tempVal = tempVal;
        return this;
    }

    public void setTempVal(Double tempVal) {
        this.tempVal = tempVal;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeterReading meterReading = (MeterReading) o;
        if (meterReading.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), meterReading.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MeterReading{" +
            "id=" + getId() +
            ", readingDate='" + getReadingDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", reading=" + getReading() +
            ", tempVal=" + getTempVal() +
            "}";
    }
}
