package cz.muni.fi.pv168.entities;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author Martin Podhora
 */
public class Ownership {
    private Long id;
    private ZonedDateTime ownershipCreated;
    private ZonedDateTime ownershipRemoved;
    private Integer shareNumerator;
    private Integer shareDenominator;
    private Owner owner;
    private RealEstate realEstate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getOwnershipCreated() {
        return ownershipCreated;
    }

    public void setOwnershipCreated(ZonedDateTime ownershipCreated) {
        this.ownershipCreated = ownershipCreated;
    }

    public ZonedDateTime getOwnershipRemoved() {
        return ownershipRemoved;
    }

    public void setOwnershipRemoved(ZonedDateTime ownershipRemoved) {
        this.ownershipRemoved = ownershipRemoved;
    }

    public Integer getShareNumerator() {
        return shareNumerator;
    }

    public void setShareNumerator(Integer shareNumerator) {
        this.shareNumerator = shareNumerator;
    }

    public Integer getShareDenominator() {
        return shareDenominator;
    }

    public void setShareDenominator(Integer shareDenominator) {
        this.shareDenominator = shareDenominator;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public RealEstate getRealEstate() {
        return realEstate;
    }

    public void setRealEstate(RealEstate realEstate) {
        this.realEstate = realEstate;
    }

    @Override
    public String toString() {
        return "Ownership{"
                + "id=" + id
                + ", ownershipCreated=" + ownershipCreated
                + ", ownershipRemoved=" + ownershipRemoved
                + ", shareNumerator=" + shareNumerator
                + ", shareDenominator=" + shareDenominator
                + ", owner=" + owner.toString()
                + ", realEstate=" + realEstate.toString()
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
        final Ownership other = (Ownership) obj;
        return (obj == this || this.id != null) && Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
