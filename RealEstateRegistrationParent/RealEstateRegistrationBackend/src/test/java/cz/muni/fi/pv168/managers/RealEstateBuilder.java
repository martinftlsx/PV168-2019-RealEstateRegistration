package cz.muni.fi.pv168.managers;

import cz.muni.fi.pv168.entities.RealEstate;

/**
 * @author Martin Podhora
 */
public class RealEstateBuilder {
    private Long id;
    private String cadastralArea;
    private String parcelNumber;
    private Double areaInMetersSquared;

    public RealEstateBuilder id(Long id) {
        this.id = id;
        return this;
    }


    public RealEstateBuilder cadastralArea(String cadastralArea) {
        this.cadastralArea = cadastralArea;
        return this;
    }

    public RealEstateBuilder parcelNumber(String parcelNumber) {
        this.parcelNumber = parcelNumber;
        return this;
    }

    public RealEstateBuilder areaInMetersSquared(Double areaInMetersSquared) {
        this.areaInMetersSquared = areaInMetersSquared;
        return this;
    }

    public RealEstate build() {
        RealEstate realEstate = new RealEstate();
        realEstate.setId(id);
        realEstate.setCadastralArea(cadastralArea);
        realEstate.setParcelNumber(parcelNumber);
        realEstate.setAreaInMetersSquared(areaInMetersSquared);
        return realEstate;
    }
}
