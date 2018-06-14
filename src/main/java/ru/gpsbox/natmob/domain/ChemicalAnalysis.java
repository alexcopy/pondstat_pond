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
 * A ChemicalAnalysis.
 */
@Entity
@Table(name = "chemical_analysis")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "chemicalanalysis")
public class ChemicalAnalysis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "jhi_date", nullable = false)
    private ZonedDateTime date;

    @Column(name = "n_o_2")
    private String nO2;

    @Column(name = "n_o_3")
    private String nO3;

    @Column(name = "n_h_4")
    private String nH4;

    @Column(name = "ph")
    private String ph;

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

    public ChemicalAnalysis date(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getnO2() {
        return nO2;
    }

    public ChemicalAnalysis nO2(String nO2) {
        this.nO2 = nO2;
        return this;
    }

    public void setnO2(String nO2) {
        this.nO2 = nO2;
    }

    public String getnO3() {
        return nO3;
    }

    public ChemicalAnalysis nO3(String nO3) {
        this.nO3 = nO3;
        return this;
    }

    public void setnO3(String nO3) {
        this.nO3 = nO3;
    }

    public String getnH4() {
        return nH4;
    }

    public ChemicalAnalysis nH4(String nH4) {
        this.nH4 = nH4;
        return this;
    }

    public void setnH4(String nH4) {
        this.nH4 = nH4;
    }

    public String getPh() {
        return ph;
    }

    public ChemicalAnalysis ph(String ph) {
        this.ph = ph;
        return this;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public Double getTempVal() {
        return tempVal;
    }

    public ChemicalAnalysis tempVal(Double tempVal) {
        this.tempVal = tempVal;
        return this;
    }

    public void setTempVal(Double tempVal) {
        this.tempVal = tempVal;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public ChemicalAnalysis timestamp(Integer timestamp) {
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
        ChemicalAnalysis chemicalAnalysis = (ChemicalAnalysis) o;
        if (chemicalAnalysis.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), chemicalAnalysis.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ChemicalAnalysis{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", nO2='" + getnO2() + "'" +
            ", nO3='" + getnO3() + "'" +
            ", nH4='" + getnH4() + "'" +
            ", ph='" + getPh() + "'" +
            ", tempVal=" + getTempVal() +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
