package cz.muni.fi.pv168.managers;

import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.entities.Ownership;
import cz.muni.fi.pv168.entities.RealEstate;

import java.time.ZonedDateTime;

/**
 * @author Martin Podhora
 */
public class OwnershipBuilder {
    private Long id;
    private ZonedDateTime ownershipCreated;
    private ZonedDateTime ownershipRemoved;
    private Integer shareNumerator;
    private Integer shareDenominator;
    private Owner owner;
    private RealEstate realEstate;

    public OwnershipBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public OwnershipBuilder ownershipCreated(ZonedDateTime ownershipCreated) {
        this.ownershipCreated = ownershipCreated;
        return this;
    }

    public OwnershipBuilder ownershipRemoved(ZonedDateTime ownershipRemoved) {
        this.ownershipRemoved = ownershipRemoved;
        return this;
    }

    public OwnershipBuilder shareNumerator(Integer shareNumerator) {
        this.shareNumerator = shareNumerator;
        return this;
    }

    public OwnershipBuilder shareDenominator(Integer shareDenominator) {
        this.shareDenominator = shareDenominator;
        return this;
    }

    public OwnershipBuilder owner(Owner owner) {
        this.owner = owner;
        return this;
    }

    public OwnershipBuilder realEstate(RealEstate realEstate) {
        this.realEstate = realEstate;
        return this;
    }

    public Ownership build() {
        Ownership ownership = new Ownership();
        ownership.setId(id);
        ownership.setOwnershipCreated(ownershipCreated);
        ownership.setOwnershipRemoved(ownershipRemoved);
        ownership.setShareNumerator(shareNumerator);
        ownership.setShareDenominator(shareDenominator);
        ownership.setOwner(owner);
        ownership.setRealEstate(realEstate);
        return ownership;
    }
}
