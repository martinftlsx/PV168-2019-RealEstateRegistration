package cz.muni.fi.pv168.managers;

import cz.muni.fi.pv168.entities.Owner;

/**
 * @author Martin Podhora
 */
public class OwnerBuilder {
    private Long id;
    private String name;
    private String idCardOrCorpNumber;
    private Boolean isCorp;

    public OwnerBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public OwnerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public OwnerBuilder idCardOrCorpNumber(String idCardOrCorpNumber) {
        this.idCardOrCorpNumber = idCardOrCorpNumber;
        return this;
    }

    public OwnerBuilder isCorp(Boolean isCorp) {
        this.isCorp = isCorp;
        return this;
    }

    public Owner build() {
        Owner owner = new Owner();
        owner.setId(this.id);
        owner.setName(this.name);
        owner.setIdCardOrCorpNumber(this.idCardOrCorpNumber);
        owner.setCorp(this.isCorp);
        return owner;
    }
}
