
package ar.gob.ambiente.sacvebio.cre.mb;

import ar.gob.ambiente.sacvebio.cre.util.EntidadServicio;
import ar.gob.ambiente.sacvebio.cre.util.GuiaSigsa;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean de respaldo para la gestión de Guías.
 * En principio solo tendrá la funcionalidad necesaria 
 * para mostrar las guías generadas por el SENSASA
 * @author rincostante
 */
public class MbGuia {
    
    /**
     * Variable privada: objeto principal a gestionar
     */
    private GuiaSigsa guia;
    
    /**
     * Variable privada: listado de las guías existentes
     */
    private List<GuiaSigsa> listado;
    
    /**
     * Variable privada: listado para filtrar la tabla de guías existentes
     */
    private List<GuiaSigsa> listadoFilter;    
    
    /**
     * Variable privada: flag que indica que la guía que se está gestionando no está editable
     */
    private boolean view;    
    
    /**
     * Variable privada: opción seleccionada para realizar la búsqueda
     */
    private EntidadServicio optBusqSelected;

    /**
     * Variable privada: listado de las opciones de búsqueda para obtener el listado de guías.
     * Según la opción seleccionada se abrirá mostrará el campo para el ingreso de la opción y el botón de búsqueda.
     */
    private List<EntidadServicio> lstOptBucarGuias;    
    
    /**
     * Variable privada: flag que indica que la búsqueda no arrojó resultados
     */    
    private boolean busqSinResultados;    

    /**
     * Constructor
     */
    public MbGuia() {
        
    }

    public List<GuiaSigsa> getListado() {
        return listado;
    }

    public void setListado(List<GuiaSigsa> listado) {
        this.listado = listado;
    }

    public List<GuiaSigsa> getListadoFilter() {
        return listadoFilter;
    }

    public void setListadoFilter(List<GuiaSigsa> listadoFilter) {
        this.listadoFilter = listadoFilter;
    }

    public boolean isBusqSinResultados() {
        return busqSinResultados;
    }

    public void setBusqSinResultados(boolean busqSinResultados) {
        this.busqSinResultados = busqSinResultados;
    }

    public GuiaSigsa getGuia() {
        return guia;
    }

    public void setGuia(GuiaSigsa guia) {
        this.guia = guia;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public EntidadServicio getOptBusqSelected() {
        return optBusqSelected;
    }

    public void setOptBusqSelected(EntidadServicio optBusqSelected) {
        this.optBusqSelected = optBusqSelected;
    }

    public List<EntidadServicio> getLstOptBucarGuias() {
        lstOptBucarGuias = new ArrayList<>();
        lstOptBucarGuias.add(new EntidadServicio(Long.valueOf(1), "CUIT del Titular"));
        lstOptBucarGuias.add(new EntidadServicio(Long.valueOf(2), "CUIT del Destinatario"));
        lstOptBucarGuias.add(new EntidadServicio(Long.valueOf(3), "Certificado fuente"));
        return lstOptBucarGuias;
    }

    public void setLstOptBucarGuias(List<EntidadServicio> lstOptBucarGuias) {
        this.lstOptBucarGuias = lstOptBucarGuias;
    }
    
    /**
     * Método para inicializar la vista detalle de la Guía.
     */
    public void prepareViewDetalle(){
        view = true;
    }     
}
