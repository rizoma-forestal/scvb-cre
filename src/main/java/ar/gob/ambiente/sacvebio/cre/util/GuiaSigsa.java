
package ar.gob.ambiente.sacvebio.cre.util;

import java.util.Date;
import java.util.List;

/**
 * Clase para gestionar una Guía emitida por SENASA
 * Temporal, hasta la definición de SENASA
 * @author rincostante
 */
public class GuiaSigsa {
    
    /**
     * Variable privada: Identificador único
     */
    private Long id;    
    
    /**
     * Variable privada: código identificatorio de la guía
     */
    private String codigo;
    
    /**
     * Variable privada: listado de los items que contiene la guía
     */    
    private List<ItemGuia> items; 
    
    /**
     * Variable privada: número cuit del titular de la guía
     */    
    private Long cuitTitular;
    
    /**
     * Variable privada: nombre completo del productor
     */    
    private String nombreTitular;
    
    /**
     * Variable privada: código identificatorio del certificado del cual se tomaron los productos
     */    
    private String origen;
    
    /**
     * Variable privada: marca y modelo del vehículo de transporte
     */    
    private String nomVehiculo;
    
    /**
     * Variable privada: matrícula del vehículo de transporte
     */    
    private String matVehiculo;
    
    /**
     * Variable privada: número del cuit de la entidad destinataria de la guía
     */    
    private Long cuitDestino;
    
    /**
     * Variable privada: nombre completo de la entidad destinataria de la guía
     */    
    private String nombreDestino;
    
    /**
     * Variable privada: nombre del Departamento al cual se remitió la guía
     */    
    private String deptoDestino;
    
    /**
     * Variable privada: nombre de la Provincia a la cual se remitió la guía
     */    
    private String provDestino;
    
    /**
     * Variable privada: fecha de emisión de la guía
     */    
    private Date fechaEmision;   

    /**
     * Variable privada: fecha de cierre de la guía. Si su estado es cerrado
     */    
    private Date fechaCierre;
    
    /**
     * Variable privada: estado en el que se encuentra la guía (EMITIDA - CERRADA - VENCIDA)
     */    
    private String estado;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<ItemGuia> getItems() {
        return items;
    }

    public void setItems(List<ItemGuia> items) {
        this.items = items;
    }

    public Long getCuitTitular() {
        return cuitTitular;
    }

    public void setCuitTitular(Long cuitTitular) {
        this.cuitTitular = cuitTitular;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getNomVehiculo() {
        return nomVehiculo;
    }

    public void setNomVehiculo(String nomVehiculo) {
        this.nomVehiculo = nomVehiculo;
    }

    public String getMatVehiculo() {
        return matVehiculo;
    }

    public void setMatVehiculo(String matVehiculo) {
        this.matVehiculo = matVehiculo;
    }

    public Long getCuitDestino() {
        return cuitDestino;
    }

    public void setCuitDestino(Long cuitDestino) {
        this.cuitDestino = cuitDestino;
    }

    public String getNombreDestino() {
        return nombreDestino;
    }

    public void setNombreDestino(String nombreDestino) {
        this.nombreDestino = nombreDestino;
    }

    public String getDeptoDestino() {
        return deptoDestino;
    }

    public void setDeptoDestino(String deptoDestino) {
        this.deptoDestino = deptoDestino;
    }

    public String getProvDestino() {
        return provDestino;
    }

    public void setProvDestino(String provDestino) {
        this.provDestino = provDestino;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    //////////////////////////
    // métodos sobrescritos //
    //////////////////////////

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.util.Guia[ id=" + id + " ]";
    }

    @Override
    public boolean equals(Object obj) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(obj instanceof GuiaSigsa)) {
            return false;
        }
        GuiaSigsa other = (GuiaSigsa) obj;
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
