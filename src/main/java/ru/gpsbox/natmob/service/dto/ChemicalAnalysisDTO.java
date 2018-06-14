package ru.gpsbox.natmob.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the ChemicalAnalysis entity.
 */
public class ChemicalAnalysisDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime date;

    private String nO2;

    private String nO3;

    private String nH4;

    private String ph;

    @NotNull
    private Double tempVal;

    private Integer timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getnO2() {
        return nO2;
    }

    public void setnO2(String nO2) {
        this.nO2 = nO2;
    }

    public String getnO3() {
        return nO3;
    }

    public void setnO3(String nO3) {
        this.nO3 = nO3;
    }

    public String getnH4() {
        return nH4;
    }

    public void setnH4(String nH4) {
        this.nH4 = nH4;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
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

        ChemicalAnalysisDTO chemicalAnalysisDTO = (ChemicalAnalysisDTO) o;
        if(chemicalAnalysisDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), chemicalAnalysisDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ChemicalAnalysisDTO{" +
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
