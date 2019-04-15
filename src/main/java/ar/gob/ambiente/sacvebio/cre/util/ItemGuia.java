
package ar.gob.ambiente.sacvebio.cre.util;

import java.io.Serializable;

/**
 * Clase para gestionar el detalle de productos de una Guía
 * Temporal, hasta la definición de SENASA
 * @author rincostante
 */
public class ItemGuia implements Serializable{
    
    /**
     * Variable privada: Identificador único
     */
    private Long id; 
    
    /**
     * Variable privada: nombre científico de la especie
     */
    private String nombreCientifico;
    
    /**
     * Variable privada: nombre vulgar del producto 
     */
    private String nombreVulgar;
    
    /**
     * Variable privada: modalidad en la que se comercializa el producto
     */
    private String modalidad;
    
    /**
     * Variable privada: unidad de medida del producto
     */
    private String unidad;
    
    /**
     * Variable privada: total incluido 
     */
    private float total;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getNombreVulgar() {
        return nombreVulgar;
    }

    public void setNombreVulgar(String nombreVulgar) {
        this.nombreVulgar = nombreVulgar;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }    
    
    //////////////////////////
    // métodos sobrescritos //
    //////////////////////////

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.util.ItemGuia[ id=" + id + " ]";
    }

    @Override
    public boolean equals(Object obj) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(obj instanceof ItemGuia)) {
            return false;
        }
        ItemGuia other = (ItemGuia) obj;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }        
}
