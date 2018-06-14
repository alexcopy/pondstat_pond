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
 * A FilterPumpCleaning.
 */
@Entity
@Table(name = "filter_pump_cleaning")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "filterpumpcleaning")
public class FilterPumpCleaning implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "cleaning_date", nullable = false)
    private ZonedDateTime cleaningDate;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "temp_val", nullable = false)
    private Double tempVal;

    @Column(name = "jhi_timestamp")
    private Integer timestamp;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCleaningDate() {
        return cleaningDate;
    }

    public FilterPumpCleaning cleaningDate(ZonedDateTime cleaningDate) {
        this.cleaningDate = cleaningDate;
        return this;
    }

    public void setCleaningDate(ZonedDateTime cleaningDate) {
        this.cleaningDate = cleaningDate;
    }

    public String getDescription() {
        return description;
    }

    public FilterPumpCleaning description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTempVal() {
        return tempVal;
    }

    public FilterPumpCleaning tempVal(Double tempVal) {
        this.tempVal = tempVal;
        return this;
    }

    public void setTempVal(Double tempVal) {
        this.tempVal = tempVal;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public FilterPumpCleaning timestamp(Integer timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
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
        FilterPumpCleaning filterPumpCleaning = (FilterPumpCleaning) o;
        if (filterPumpCleaning.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), filterPumpCleaning.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "FilterPumpCleaning{" +
            "id=" + getId() +
            ", cleaningDate='" + getCleaningDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", tempVal=" + getTempVal() +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
