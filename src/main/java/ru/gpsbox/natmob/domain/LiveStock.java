package ru.gpsbox.natmob.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import ru.gpsbox.natmob.domain.enumeration.StockCase;

/**
 * A LiveStock.
 */
@Entity
@Table(name = "live_stock")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "livestock")
public class LiveStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "jhi_date", nullable = false)
    private ZonedDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private StockCase reason;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "qty", nullable = false)
    private Integer qty;

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

    public ZonedDateTime getDate() {
        return date;
    }

    public LiveStock date(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public StockCase getReason() {
        return reason;
    }

    public LiveStock reason(StockCase reason) {
        this.reason = reason;
        return this;
    }

    public void setReason(StockCase reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public LiveStock description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQty() {
        return qty;
    }

    public LiveStock qty(Integer qty) {
        this.qty = qty;
        return this;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getTempVal() {
        return tempVal;
    }

    public LiveStock tempVal(Double tempVal) {
        this.tempVal = tempVal;
        return this;
    }

    public void setTempVal(Double tempVal) {
        this.tempVal = tempVal;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public LiveStock timestamp(Integer timestamp) {
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
        LiveStock liveStock = (LiveStock) o;
        if (liveStock.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), liveStock.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LiveStock{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", reason='" + getReason() + "'" +
            ", description='" + getDescription() + "'" +
            ", qty=" + getQty() +
            ", tempVal=" + getTempVal() +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
