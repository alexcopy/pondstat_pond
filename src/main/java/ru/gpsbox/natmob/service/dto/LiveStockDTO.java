package ru.gpsbox.natmob.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import ru.gpsbox.natmob.domain.enumeration.StockCase;

/**
 * A DTO for the LiveStock entity.
 */
public class LiveStockDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime date;

    private StockCase reason;

    private String description;

    @NotNull
    private Integer qty;

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

    public StockCase getReason() {
        return reason;
    }

    public void setReason(StockCase reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
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

        LiveStockDTO liveStockDTO = (LiveStockDTO) o;
        if(liveStockDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), liveStockDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LiveStockDTO{" +
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
