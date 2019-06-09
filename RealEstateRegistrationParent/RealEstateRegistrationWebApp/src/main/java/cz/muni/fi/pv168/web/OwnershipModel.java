package cz.muni.fi.pv168.web;

public class OwnershipModel {
    private Long id;
    private String ownershipCreated;
    private String ownershipRemoved;
    private String ownerName;
    private String ownerIdCardOrCorpNumber;
    private String cadastralArea;
    private String parcelNumber;
    private String share;

    public String getOwnershipCreated() {
        return ownershipCreated;
    }

    public void setOwnershipCreated(String ownershipCreated) {
        this.ownershipCreated = ownershipCreated;
    }

    public String getOwnershipRemoved() {
        return ownershipRemoved;
    }

    public void setOwnershipRemoved(String ownershipRemoved) {
        this.ownershipRemoved = ownershipRemoved;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerIdCardOrCorpNumber() {
        return ownerIdCardOrCorpNumber;
    }

    public void setOwnerIdCardOrCorpNumber(String ownerIdCardOrCorpNumber) {
        this.ownerIdCardOrCorpNumber = ownerIdCardOrCorpNumber;
    }

    public String getCadastralArea() {
        return cadastralArea;
    }

    public void setCadastralArea(String cadastralArea) {
        this.cadastralArea = cadastralArea;
    }

    public String getPareclNumber() {
        return parcelNumber;
    }

    public void setPareclNumber(String pareclNumber) {
        this.parcelNumber = pareclNumber;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
