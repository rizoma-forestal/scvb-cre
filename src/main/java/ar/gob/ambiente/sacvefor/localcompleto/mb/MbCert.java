
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EntidadGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EntidadGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ItemProductivoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.PersonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Bean de respaldo para la gestión de Certificados.
 * Implica la creación, edición y lectura de los datos de los Certificados de tenencia.
 * El Certificado se asimiló a las  guías de extracción, de modo que la entidad gestionada es "Guia"
 * Gestiona las vistas de /cert/gestion/ y /cert/list
 * @author rincostante
 */
public class MbCert {
    
    /**
     * Variable privada: objeto principal a gestionar
     */
    private Guia cert;    
    
    /**
     * Variable privada: Entidad Guía que oficiará de titular de la guía
     */
    private EntidadGuia entOrigen;    
    
    /**
     * Variable privada: listado de los certificados existentes
     */    
    private List<Guia> listado;
    
    /**
     * Variable privada: listado para filtrar la tabla de certificados existentes
     */    
    private List<Guia> listadoFilter;
    
    /**
     * Variable privada: listado de las guías que tomaron productos del certificado.
     * Emitidas mediante el SIGSA del SENASA. Pobladas medienta la consulta al servicio respectivo
     */
    private List<Guia> lstGuiasDTE;
    
    /**
     * Variable privada: cuit del productor a buscar para asignarlo como Entidad Guía titular
     */
    private Long cuitBuscar;

    /**
     * Variable privada: autorización seleccionada para descontar productos
     */
    private Autorizacion autSelected;    
    
    /**
     * Variable privada: certificado seleccionado para su asignación como descuento de productos
     */
    private Guia certSelected;       
    
    /**
     * Variable privada: listado de las opciones de búsqueda para obtener el listado de certificados.
     * Según la opción seleccionada se mostrará el campo para el ingreso de la opción y el botón de búsqueda.
     */
    private List<EntidadServicio> lstOptBucarCert;
    
    /**
     * Variable privada: opción seleccionada para realizar la búsqueda
     */
    private EntidadServicio optBusqSelected;

    /**
     * Variable privada: flag que indica que la búsqueda no arrojó resultados
     */    
    private boolean busqSinResultados;   
    
    /**
     * Variable privada: estado de certificado seleccionado para la búsqueda
     */    
    private EstadoGuia busqEstadoCertSelected;    
    
    /**
     * Variable privada: listado de estados de guías para llenar el combo de selección del estado a buscar las guías respectivas
     */    
    private List<EstadoGuia> lstBusqEstadosCert;    
    
    /**
     * Variable privada: cadena para guardar el código del certificado a buscar
     */    
    private String busqCodCert;
    
    /**
     * Variable privada: cuit del titular cuyos certificados se quiere buscar
     */    
    private Long busqCuitTit;    
    
    /**
     * Variable privada: número de autorización de la cual los certificados a buscar tomaron los productos
     */    
    private String busqNumFuente; 
    
    /**
     * Variable privada: setea la página inicial para mostrar en el frame
     */
    private String page = "general.xhtml";
    
    /**
     * Variable privada: número del certificado para obtenerlo para su gestión
     */
    private String certNumero; 
    
    /**
     * Variable privada: Listado de inmuebles vinculados a la Autorización del predio
     * desde el cual provienen los productos vinculados al Certificado
     * A mostrar en el detalle del Certificado.
     */
    private List<Inmueble> lstInmueblesOrigen;
    
    /**
     * Variable privada: listado de las Autorizaciones disponibles para tomar como fuente de productos para el certificado
     */
    private List<Autorizacion> lstAutorizaciones;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de Autorizaciones disponibles
     */
    private List<Autorizacion> lstAutFilters;    
    
    /**
     * Variable privada: productor a asignar a la Entidad Guía como titular
     */
    private Persona productor;    
    
    /**
     * Variable privada: flag que indica que el certificado que se está gestionando no está editable
     */
    private boolean view;
    
    /**
     * Variable privada: flag que indica si se muestra el detalle de la Autorización seleccionada como Fuente
     * de productos para el certificado
     */
    private boolean viewFuente;    
    
    /**
     * Variable privada: flag que indica que el certificado que se está gestionando es existente
     */
    private boolean edit;    
    
    /**
     * Variable privada: MbSesion para gestionar las variables de sesión del usuario
     */  
    private MbSesion sesion;
    
    /**
     * Variable privada: Usuario de sesión
     */
    private Usuario usLogueado;    
    
    ///////////////
    // Productos //
    /////////////// 
    
    /**
     * Variable privada: Listado de items procedentes de la fuente de productos de la guía.
     */
    private List<ItemProductivo> lstItemsOrigen;
    
    /**
     * Variable privada: Listado de items autorizados para descontar desde la Guía ya registrada
     */
    private List<ItemProductivo> lstItemsAutorizados;
    
    /**
     * Variable privada: Listado de items a descontar productos durante el registro de la guía
     */
    private List<ItemProductivo> lstItemsADescontar;    
    
    ////////////////////////////////////////////////////
    // Accesos a datos mediante inyección de recursos //
    ////////////////////////////////////////////////////
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Guia
     */   
    @EJB
    private GuiaFacade guiaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de EstadoGuia
     */   
    @EJB
    private EstadoGuiaFacade estadoFacade;    
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Autorizacion
     */   
    @EJB
    private AutorizacionFacade autFacade;    
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoParam
     */   
    @EJB
    private TipoParamFacade tipoParamFacade;    
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Parametrica
     */   
    @EJB
    private ParametricaFacade paramFacade;

    /**
     * Variable privada: EJB inyectado para el acceso a datos de Persona
     */   
    @EJB
    private PersonaFacade perFacade;    
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ItemProductivo
     */   
    @EJB
    private ItemProductivoFacade itemFacade; 
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de EntidadGuia
     */   
    @EJB
    private EntidadGuiaFacade entGuiaFacade;    
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoGuia
     */   
    @EJB
    private TipoGuiaFacade tipoGuiaFacade;
    // métodos de acceso a las variables

    public boolean isViewFuente() {
        return viewFuente;
    }

    public void setViewFuente(boolean viewFuente) {
        this.viewFuente = viewFuente;
    }

    public List<ItemProductivo> getLstItemsAutorizados() {
        return lstItemsAutorizados;
    }

    public void setLstItemsAutorizados(List<ItemProductivo> lstItemsAutorizados) {
        this.lstItemsAutorizados = lstItemsAutorizados;
    }

    public List<ItemProductivo> getLstItemsADescontar() {
        return lstItemsADescontar;
    }

    public void setLstItemsADescontar(List<ItemProductivo> lstItemsADescontar) {
        this.lstItemsADescontar = lstItemsADescontar;
    }

    public EntidadGuia getEntOrigen() {
        return entOrigen;
    }

    public void setEntOrigen(EntidadGuia entOrigen) {
        this.entOrigen = entOrigen;
    }

    public List<Autorizacion> getLstAutorizaciones() {
        return lstAutorizaciones;
    }

    public void setLstAutorizaciones(List<Autorizacion> lstAutorizaciones) {
        this.lstAutorizaciones = lstAutorizaciones;
    }

    public List<Autorizacion> getLstAutFilters() {
        return lstAutFilters;
    }

    public void setLstAutFilters(List<Autorizacion> lstAutFilters) {
        this.lstAutFilters = lstAutFilters;
    }

    public Persona getProductor() {
        return productor;
    }

    public void setProductor(Persona productor) {
        this.productor = productor;
    }

    public List<ItemProductivo> getLstItemsOrigen() {
        return lstItemsOrigen;
    }

    public void setLstItemsOrigen(List<ItemProductivo> lstItemsOrigen) {
        this.lstItemsOrigen = lstItemsOrigen;
    }

    public Long getCuitBuscar() {
        return cuitBuscar;
    }

    public void setCuitBuscar(Long cuitBuscar) {
        this.cuitBuscar = cuitBuscar;
    }

    public Autorizacion getAutSelected() {
        return autSelected;
    }

    public void setAutSelected(Autorizacion autSelected) {
        this.autSelected = autSelected;
    }

    public Guia getCertSelected() {
        return certSelected;
    }

    public void setCertSelected(Guia certSelected) {
        this.certSelected = certSelected;
    }

    public AutorizacionFacade getAutFacade() {
        return autFacade;
    }

    public void setAutFacade(AutorizacionFacade autFacade) {
        this.autFacade = autFacade;
    }

    public List<Inmueble> getLstInmueblesOrigen() {
        return lstInmueblesOrigen;
    }

    public void setLstInmueblesOrigen(List<Inmueble> lstInmueblesOrigen) {
        this.lstInmueblesOrigen = lstInmueblesOrigen;
    }

    public String getCertNumero() {
        return certNumero;
    }

    public void setCertNumero(String certNumero) {
        this.certNumero = certNumero;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Guia getCert() {
        return cert;
    }

    public void setCert(Guia cert) {
        this.cert = cert;
    }

    public List<EstadoGuia> getLstBusqEstadosCert() {
        lstBusqEstadosCert = estadoFacade.getHabilitados();
        return lstBusqEstadosCert;
    }

    public void setLstBusqEstadosCert(List<EstadoGuia> lstBusqEstadosCert) {
        this.lstBusqEstadosCert = lstBusqEstadosCert;
    }

    public EstadoGuia getBusqEstadoCertSelected() {
        return busqEstadoCertSelected;
    }

    public void setBusqEstadoCertSelected(EstadoGuia busqEstadoCertSelected) {
        this.busqEstadoCertSelected = busqEstadoCertSelected;
    }

    public String getBusqCodCert() {
        return busqCodCert;
    }

    public void setBusqCodCert(String busqCodCert) {
        this.busqCodCert = busqCodCert;
    }

    public Long getBusqCuitTit() {
        return busqCuitTit;
    }

    public void setBusqCuitTit(Long busqCuitTit) {
        this.busqCuitTit = busqCuitTit;
    }

    public String getBusqNumFuente() {
        return busqNumFuente;
    }

    public void setBusqNumFuente(String busqNumFuente) {
        this.busqNumFuente = busqNumFuente;
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

    public boolean isBusqSinResultados() {
        return busqSinResultados;
    }

    public void setBusqSinResultados(boolean busqSinResultados) {
        this.busqSinResultados = busqSinResultados;
    }

    public List<EntidadServicio> getLstOptBucarCert() {
        lstOptBucarCert = new ArrayList<>();;
        lstOptBucarCert.add(new EntidadServicio(Long.valueOf(1), "Número"));
        lstOptBucarCert.add(new EntidadServicio(Long.valueOf(2), "CUIT del Titular"));
        lstOptBucarCert.add(new EntidadServicio(Long.valueOf(3), "Autorización fuente"));
        lstOptBucarCert.add(new EntidadServicio(Long.valueOf(4), "Estado actual"));
        return lstOptBucarCert;
    }

    public void setLstOptBucarCert(List<EntidadServicio> lstOptBucarCert) {
        this.lstOptBucarCert = lstOptBucarCert;
    }
    
    public List<Guia> getListado() {
        return listado;
    }

    public void setListado(List<Guia> listado) {
        this.listado = listado;
    }

    public List<Guia> getListadoFilter() {
        return listadoFilter;
    }

    public void setListadoFilter(List<Guia> listadoFilter) {
        this.listadoFilter = listadoFilter;
    }

    public List<Guia> getLstGuiasDTE() {
        return lstGuiasDTE;
    }

    public void setLstGuiasDTE(List<Guia> lstGuiasDTE) {
        this.lstGuiasDTE = lstGuiasDTE;
    }    

    /**
     * Constructor
     */
    public MbCert() {
    }
    
    /******************************
     * Métodos de inicialización **
     ******************************/

    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa el bean de sesión y el usuario
     */    
    @PostConstruct
    public void init(){
    	// obtento el usuario
	ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        sesion = (MbSesion)ctx.getSessionMap().get("mbSesion");
        usLogueado = sesion.getUsuario();
        optBusqSelected = null;
        busqSinResultados = false;
        limpiarFormBusqCert();
    }
    
    /**
     * Método para cargar la vista solicitada desde el menú lateral
     * Por defecto será "general.xhtml"
     * @param strPage String vista a cargar recibida como parámetro
     */
    public void cargarFrame(String strPage){
        // renuevo el objeto guía
        Guia certAct = guiaFacade.find(cert.getId());

        page = strPage;
    }
    
    /**************************************
     * Métodos para el listado y detalle **
     **************************************/    
    
    /**
     * Método para listar los certificados según los parámetros de búsqueda
     * Si no hubo resultados setea el flag correspondiente para mostrar un mensaje al usuario.
     * Al final limpia el formulario de búsqueda.
     */
    public void poblarListado(){
        // reseteo el resultado
        busqSinResultados = false;
        // busco y pueblo el listado luego de validar
        switch (optBusqSelected.getId().intValue()) {
            case 1:
                // Si busca por número de certificado
                listado = new ArrayList<>();
                listado.add(guiaFacade.getExistente(busqCodCert));
                break;
            case 2:
                // Si busca por el cuit del titular
                listado = guiaFacade.getByOrigen(busqCuitTit);
                break;
            case 3:
                // Si busca por el número de la Autorización fuente
                listado = guiaFacade.getByNumFuente(busqNumFuente);
                break;
            case 4:
                // Si busca por estado del certificado
                listado = guiaFacade.getByEstado(busqEstadoCertSelected);
                break;
        }
        // verifico los resultados
        if(listado.isEmpty()) busqSinResultados = true;
        // limpio el formulario
        limpiarFormBusqCert();  
    }
    
    /**
     * Método para inicializar la vista detalle del Certificado.
     * Instancia las posibles guías que toman productos del presente certificado, emitidas mediante el SIGSA del SENASA.
     */
    public void prepareViewDetalle(){
        //lstGuiasDTE = [consulta API SENASA]
        view = true;
    }
    
    /****************************
     * Métodos para la gestión **
     ****************************/
    
    /**
     * Método que busca un Certificado según el número ingresado en el formulario de búsqueda.
     * certNumero será el parámetro.
     */
    public void buscar(){
        try{
            // busco el Certificado
            cert = guiaFacade.getExistente(certNumero.toUpperCase());
            if(cert == null){
                JsfUtil.addErrorMessage("No se encontró ningún Certificado con el Número ingresado.");
            }else{
                // redirecciono a la vista
                setearInmueblesOrigen();
                page = "generalView.xhtml";
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error buscando el Certificado. " + ex.getMessage());
        }
    }     
    
    /**
     * Método para redireccionar a la vista detalle del Certificado.
     * Actualizo el Certificado para deshacer los cambios que hubiera podido hacer
     */
    public void prepareView(){
        cert = guiaFacade.find(cert.getId());
        limpiarForm();
        setearInmueblesOrigen();
        page = "generalView.xhtml";
    }        
    
    /**
     * Método para limpiar el campo de CUIT a buscar
     */
    public void limpiarCuit(){
        viewFuente = false;
        autSelected = null;
        lstItemsADescontar = null;
        cuitBuscar = null;
        productor = null;
        lstAutorizaciones = null;
        entOrigen = null;
        cert = new Guia();
    }  
    
    /**
     * Método para limpiar el formulario de datos generales luego de la edición de la Guía
     */
    public void limpiarForm() {
        edit = false;
        viewFuente = false;
        cuitBuscar = null;
        productor = null;
        entOrigen = null;
        lstAutorizaciones = null;
        autSelected = null;
        lstItemsOrigen = null;
    }    
    
    /**
     * Método para mostrar el detalle de la Autorización seleccionada como fuente de productos.
     * Solo guardo los ítems si la guía descuenta de una Autorización
     */
    public void verDetalleFuente(){
        lstItemsOrigen = itemFacade.getByAutorizacion(autSelected);
        viewFuente = true;
    }
    
    /**
     * Método que instancia un Certificado nuevo, los listados de los objetos a seleccinar y campos.
     * Renderiza el fomulario de creación/edición.
     */
    public void prepareNew(){
        cert = new Guia();
    }    
    
    /**
     * Método para buscar la/s fuente/s de productos para descontar.
     * Si estoy editando un certificado limpio todo lo establecido previamente como fuente de produtos.
     * También setea el Prductor (Persona) según el CUIT obtenido.
     */
    public void buscarFuentesProductos(){
        // obtengo el rol de Proponente
        TipoParam tipoParamRol = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("RolPersonas"));
        Parametrica rolProp = paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Proponente"), tipoParamRol);
        // obtengo el Producor
        productor = perFacade.findByCuitRol(cuitBuscar, rolProp);
        if(productor != null){
            // busco Autorizaciones que tengan al productor como Proponente
            lstAutorizaciones = autFacade.getFuenteByProponente(productor);
        }else{
            JsfUtil.addErrorMessage("No se encontró un Productor registrado con el CUIT ingresado.");
        }
    }    

    /**
     * Método para resetear la Fuente de productos.
     * Si hay guías seleccionadas para descontar, las quito
     * y elimino la que pudiera estar para asignar.
     */
    public void deleteFuenteGuardada(){
        // reseteo el flag de asignación de la guía
        for (Autorizacion aut : lstAutorizaciones){
            if(Objects.equals(aut.getId(), autSelected.getId())){
                aut.setAsignadaDesc(false);
            }
        }    
        autSelected = null;
        viewFuente = false;
        cert.setNumFuente(null);
        if(!cert.getGuiasfuentes().isEmpty()){
            cert.getGuiasfuentes().clear();
        }
        lstItemsOrigen = null;
        lstItemsADescontar = null;
        cert.getRodales().clear();
    }    

    /**
     * Método para guardar la Fuente de productos para el Certificado.
     * Se define como fuente de productos al instrumento que autoriza su extracción
     * de uno o más inmuebles especificados.
     */
    public void guardarFuente(){
        boolean valida = true;
        // seteo la validación según gestione o no rodales
        if(ResourceBundle.getBundle("/Config").getString("GestionaRodales").equals("si") && cert.getTipo().isDescuentaAutoriz()){
            if(!autSelected.getRodales().isEmpty() && cert.getRodales().isEmpty()){
                valida = false;
            }
        }
        // si valida seteo el número de fuente de los productos
        if(valida){
            cert.setNumFuente(autSelected.getNumero());
            // seteo el flag de seleccionada a la autorización correspondiente
            for (Autorizacion aut : lstAutorizaciones){
                if(Objects.equals(aut.getId(), autSelected.getId())){
                    aut.setAsignadaDesc(true);
                }
            }  
            // seteo el tipo de paramétrica para obtener el tipo de fuente de productos (origen del descuento)
            TipoParam tipoParamFuente = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoFuente"));
            // seteo la autorización como el tipo de origen de productos
            cert.setTipoFuente(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Autorizacion"), tipoParamFuente));
            JsfUtil.addSuccessMessage("Se ha registrado la Fuente de Productos, puede guardar los Datos Generales de la Guía");
        }else{
            JsfUtil.addErrorMessage("La Autorización seleccionada tiene rodales asignados, debe vincular a la Guía al menos uno.");
        }
    }    
    
    /**
     * Método para cancelar la Fuente seleccionada (Autorización) como origen de productos
     * Resteteo el flag de asignación y los listados y objetos seleccionados.
     */
    public void cancelarFuenteSelected(){
        // reseteo el flag de asignación de la guía
        for (Autorizacion aut : lstAutorizaciones){
            if(Objects.equals(aut.getId(), autSelected.getId())){
                aut.setAsignadaDesc(false);
            }
        }        
        autSelected = null;
        lstItemsADescontar = null;
        viewFuente = false;
        cert.getRodales().clear();
    }    
    
    /**
     * Método para persistir un Certificado con los datos de origen.
     * Crea o selecciona la entidad Origen y asigna el inmueble de origen
     * Sea inserción o edición
     */
    public void save(){
        Date fechaAlta = new Date(System.currentTimeMillis());
        // obtengo la EntidadGuia (Origen)
        entOrigen = obtenerEntidadOrigen(productor, cert.getTipoFuente());
        if(!edit){
            // seteo la fecha de alta
            cert.setFechaAlta(fechaAlta);
        }
        try{
            cert.setUsuario(usLogueado);
            // si no existe la EntidadGuia origen, seteo los datos de registro y la creo
            if(entOrigen.getId() == null){
                entOrigen.setUsuario(usLogueado);
                entOrigen.setFechaAlta(fechaAlta);
                entOrigen.setHabilitado(true);
                entGuiaFacade.create(entOrigen);
            }
            // seteo o actualizo la EntidadGuia origen
            if(entOrigen != null){
                cert.setOrigen(entOrigen);
            }
            // edito o inserto, según sea el caso
            if(!edit){
                // Obtengo el tipo de Guia correspondiente al certificado (COLTE). Debe estar configurado en el Config.properties
                TipoGuia tipo = tipoGuiaFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoGuiaCert"));
                // Estado (carga inicial)
                EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaInicial"));
                if(estado != null && tipo != null){
                    // solo continúo si hay un estado y un tipo configurados
                    cert.setEstado(estado);
                    cert.setTipo(tipo);
                    // Seteo el código. En principio será un autonumérico
                    cert.setCodigo(setCodigoCert());
                    // Creo la Guía
                    guiaFacade.create(cert);
                    JsfUtil.addSuccessMessage("El Certificado se creo correctamente, puede continuar con los pasos siguientes para su emisión.");
                }else{
                    JsfUtil.addErrorMessage("No se pudo encontrar un Parámetro para el Estado de Certificado: 'GuiaInicial' y/o el tipo de Guía para el Certificado.");
                }
            }else{
                guiaFacade.edit(cert);
                JsfUtil.addSuccessMessage("El Certificado se actualizó correctamente, puede continuar con los pasos siguientes para su emisión.");
            }
            // limpio los objetos temporales del bean
            limpiarForm();
            // redirecciono a la vista
            setearInmueblesOrigen();
            page = "generalView.xhtml";
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error gestionando el Certificado. " + ex.getMessage());
        }
    }    
    
    /*********************
     * Métodos privados **
     *********************/
    
    /**
     * Método para limpiar el formulario de búsqueda de certificados.
     * Resetea todas las variables y listados.
     */
    private void limpiarFormBusqCert() {
        optBusqSelected = null;
        lstBusqEstadosCert = new ArrayList<>();
        busqEstadoCertSelected = null;
        busqCodCert = null;
        busqCuitTit = null;
    }
    
    /**
     * Método para listar los inmuebles vinculados a la Autorización de la cual provienen los productos del Certificado.
     * Busca la Autorización a partir del número de fuente, 
     * Utilizado en buscar(), prepareView(), save() y prepareViewDetalle()
     */
    private void setearInmueblesOrigen() {
        lstInmueblesOrigen = new ArrayList<>();
        // obtengo la Autorización de la que descontó productos
        Autorizacion aut;
        aut = autFacade.getExistente(cert.getNumFuente());

        for(Inmueble inm : aut.getInmuebles()){
            lstInmueblesOrigen.add(inm);
        }
    }
    
    /**
     * Método para obtener la EntidadGuia de origen, si no existe setea sus datos y la retorna.
     * Si se está editando el Certificado valida que tenga la autorización fuente.
     * Utilizado en save()
     * @return EntidadGuia entidad guía generada
     */
    private EntidadGuia obtenerEntidadOrigen(Persona per, Parametrica tipoFuente) {
        EntidadGuia ent;
        if(autSelected == null){
            autSelected = autFacade.getExistente(cert.getNumFuente());
        }
        Inmueble inm = autSelected.getInmuebles().get(0);
        // obtengo la entidad origen y verifico si la entidad de origen no está registrada
        ent = entGuiaFacade.getOrigenExistente(per.getCuit(), inm.getNombre());
       
        if(ent != null){
            // si existe la retorno
            return ent;
        }else{
            // si no existe la creo
            ent = new EntidadGuia();
            // seteo la persona según el origen de los productos sea o no una guía de movimiento interno
            ent.setCuit(per.getCuit());
            ent.setIdRue(per.getIdRue());
            ent.setNombreCompleto(per.getNombreCompleto());
            ent.setTipoPersona(per.getTipo());
            ent.setEmail(per.getEmail());

            // tipo entidad
            TipoParam tipoParamEntGuia = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoEntidadGuia"));
            ent.setTipoEntidadGuia(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TegFuente"), tipoParamEntGuia));
            // inmueble
            if(inm.getIdCatastral() != null){
                ent.setInmCatastro(inm.getIdCatastral());
            }
            ent.setInmNombre(inm.getNombre());
            ent.setDepartamento(inm.getDepartamento());
            ent.setIdLocGT(inm.getIdLocGt());
            ent.setLocalidad(inm.getLocalidad());
            ent.setProvincia(inm.getProvincia());
            // autorización
            ent.setNumAutorizacion(autSelected.getNumero());
            return ent;
        }
    }    
        
    /**
     * Método que crea el código del Certificado
     * De acuerdo a la configuración del Config.properties.
     * Utilizado en save()
     * @return String código generado
     */
    private String setCodigoCert() {
        String codigo;
        Calendar fecha = Calendar.getInstance();
        String sAnio = String.valueOf(fecha.get(Calendar.YEAR));
        int id = guiaFacade.getUltimoIdByTipo(cert.getTipo().getId());
        
        // seteo el código (COLTE)
        codigo = "COLTE";
        
        if(id == 0){
            // si inicia
            codigo = codigo + "-" + ResourceBundle.getBundle("/Config").getString("IdProvinciaGt") + "-00001";
        }else{
            String sId = String.valueOf(id + 1);
            int ceros = 5 - sId.length();
            // agrego los ceros a la izquierda
            int j = ceros;
            codigo = codigo + "-" + ResourceBundle.getBundle("/Config").getString("IdProvinciaGt") + "-";
            while(j > 0){
                codigo = codigo + "0";
                j -= 1;
            }
            codigo = codigo + sId;
        }
        return codigo + "-" + sAnio;
    }
    
}
