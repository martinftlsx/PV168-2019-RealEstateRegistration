package cz.muni.fi.pv168.entities;

import java.util.Objects;

/**
 * @author Martin Podhora
 */
public class RealEstate {
    private Long id;
    private String cadastralArea;
    private String parcelNumber;
    private Double areaInMetersSquared;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCadastralArea() {
        return cadastralArea;
    }

    public void setCadastralArea(String cadastralArea) {
        this.cadastralArea = cadastralArea;
    }

    public String getParcelNumber() {
        return parcelNumber;
    }

    public void setParcelNumber(String parcelNumber) {
        this.parcelNumber = parcelNumber;
    }

    public Double getAreaInMetersSquared() {
        return areaInMetersSquared;
    }

    public void setAreaInMetersSquared(Double areaInMetersSquared) {
        this.areaInMetersSquared = areaInMetersSquared;
    }

    @Override
    public String toString() {
        return "RealEstate{"
                + "id=" + id
                + ", cadastralArea=" + cadastralArea
                + ", parcelNumber=" + parcelNumber
                + ", areaInMetersSquared=" + areaInMetersSquared
                + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RealEstate other = (RealEstate) obj;
        return (obj == this || this.id != null) && Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
