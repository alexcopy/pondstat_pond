package ru.gpsbox.natmob.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the OtherWorks entity.
 */
public class OtherWorksDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime date;

    private String reason;

    private Integer qty;

    private String descripton;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getDescripton() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
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

        OtherWorksDTO otherWorksDTO = (OtherWorksDTO) o;
        if(otherWorksDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), otherWorksDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OtherWorksDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", reason='" + getReason() + "'" +
            ", qty=" + getQty() +
            ", descripton='" + getDescripton() + "'" +
            ", tempVal=" + getTempVal() +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
