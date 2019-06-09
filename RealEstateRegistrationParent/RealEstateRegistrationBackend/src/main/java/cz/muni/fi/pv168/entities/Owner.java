package cz.muni.fi.pv168.entities;

import java.util.Objects;

/**
 * @author Martin Podhora
 */
public class Owner {
    private Long id;
    private String name;
    private String idCardOrCorpNumber;
    private Boolean isCorp;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCardOrCorpNumber() {
        return idCardOrCorpNumber;
    }

    public void setIdCardOrCorpNumber(String idCardOrCorpNumber) {
        this.idCardOrCorpNumber = idCardOrCorpNumber;
    }

    public Boolean getCorp() {
        return isCorp;
    }

    public void setCorp(Boolean corp) {
        isCorp = corp;
    }

    @Override
    public String toString() {
        return "Owner{"
                + "id=" + id
                + ", name=" + name
                + ", idCardOrCorpNumber=" + idCardOrCorpNumber
                + ", isCorp=" + isCorp
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
        final Owner other = (Owner) obj;
        return (obj == this || this.id != null) && Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
