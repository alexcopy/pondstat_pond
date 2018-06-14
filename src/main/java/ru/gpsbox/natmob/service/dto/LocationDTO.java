package ru.gpsbox.natmob.service.dto;


import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Location entity.
 */
public class LocationDTO implements Serializable {

    private Long id;

    private String streetAddress;

    private String postalCode;

    private String city;

    private String county;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LocationDTO locationDTO = (LocationDTO) o;
        if(locationDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), locationDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LocationDTO{" +
            "id=" + getId() +
            ", streetAddress='" + getStreetAddress() + "'" +
            ", postalCode='" + getPostalCode() + "'" +
            ", city='" + getCity() + "'" +
            ", county='" + getCounty() + "'" +
            "}";
    }
}
