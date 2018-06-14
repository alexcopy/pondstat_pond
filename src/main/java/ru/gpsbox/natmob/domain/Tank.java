package ru.gpsbox.natmob.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

import ru.gpsbox.natmob.domain.enumeration.TankType;

/**
 * A Tank.
 */
@Entity
@Table(name = "tank")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "tank")
public class Tank implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "tank_name", nullable = false)
    private String tankName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tank_type", nullable = false)
    private TankType tankType;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "jhi_timestamp", nullable = false)
    private Integer timestamp;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTankName() {
        return tankName;
    }

    public Tank tankName(String tankName) {
        this.tankName = tankName;
        return this;
    }

    public void setTankName(String tankName) {
        this.tankName = tankName;
    }

    public TankType getTankType() {
        return tankType;
    }

    public Tank tankType(TankType tankType) {
        this.tankType = tankType;
        return this;
    }

    public void setTankType(TankType tankType) {
        this.tankType = tankType;
    }

    public String getDescription() {
        return description;
    }

    public Tank description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public Tank timestamp(Integer timestamp) {
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
        Tank tank = (Tank) o;
        if (tank.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tank.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Tank{" +
            "id=" + getId() +
            ", tankName='" + getTankName() + "'" +
            ", tankType='" + getTankType() + "'" +
            ", description='" + getDescription() + "'" +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
