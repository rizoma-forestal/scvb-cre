
package ar.gob.ambiente.sacvebio.cre.mb;

import ar.gob.ambiente.sacvebio.cre.entities.Autorizacion;
import ar.gob.ambiente.sacvebio.cre.entities.CopiaGuia;
import ar.gob.ambiente.sacvebio.cre.entities.EntidadGuia;
import ar.gob.ambiente.sacvebio.cre.entities.EstadoGuia;
import ar.gob.ambiente.sacvebio.cre.entities.Guia;
import ar.gob.ambiente.sacvebio.cre.entities.Inmueble;
import ar.gob.ambiente.sacvebio.cre.entities.ItemProductivo;
import ar.gob.ambiente.sacvebio.cre.entities.Parametrica;
import ar.gob.ambiente.sacvebio.cre.entities.Persona;
import ar.gob.ambiente.sacvebio.cre.entities.Producto;
import ar.gob.ambiente.sacvebio.cre.entities.ProductoTasa;
import ar.gob.ambiente.sacvebio.cre.entities.TipoGuia;
import ar.gob.ambiente.sacvebio.cre.entities.TipoGuiaTasa;
import ar.gob.ambiente.sacvebio.cre.entities.TipoParam;
import ar.gob.ambiente.sacvebio.cre.entities.Usuario;
import ar.gob.ambiente.sacvebio.cre.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvebio.cre.facades.EntidadGuiaFacade;
import ar.gob.ambiente.sacvebio.cre.facades.EstadoGuiaFacade;
import ar.gob.ambiente.sacvebio.cre.facades.GuiaFacade;
import ar.gob.ambiente.sacvebio.cre.facades.ItemProductivoFacade;
import ar.gob.ambiente.sacvebio.cre.facades.ParametricaFacade;
import ar.gob.ambiente.sacvebio.cre.facades.PersonaFacade;
import ar.gob.ambiente.sacvebio.cre.facades.ProductoFacade;
import ar.gob.ambiente.sacvebio.cre.facades.TipoGuiaFacade;
import ar.gob.ambiente.sacvebio.cre.facades.TipoParamFacade;
import ar.gob.ambiente.sacvebio.cre.util.CriptPass;
import ar.gob.ambiente.sacvebio.cre.util.DetalleTasas;
import ar.gob.ambiente.sacvebio.cre.util.EntidadServicio;
import ar.gob.ambiente.sacvebio.cre.util.GuiaSinPago;
import ar.gob.ambiente.sacvebio.cre.util.JsfUtil;
import ar.gob.ambiente.sacvebio.cre.util.LiqTotalTasas;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
    
    /**
     * Variable privada: Listado de Items asignados a un certificado
     */
    private List<ItemProductivo> lstItemsAsignados; 
    
    /**
     * Variable privada: Item productivo asignado al Certificado
     */
    private ItemProductivo itemAsignado;    
    
    /**
     * Variable privada: flag que indica si se está editando un ítem ya asignado al Certificado
     */
    private boolean editandoItem;    
    
    /**
     * Variable privada: flag que indica si se está descontando cupo de un producto
     */
    private boolean descontandoProd;    
    
    /**
     * Variable privada: Item productivo seleccionado para obtener productos
     */
    private ItemProductivo itemFuente;
    
    ///////////////
    //// Tasas ////
    ///////////////
    
    /**
     * Variable privada: listado de guías que adeudan el pago de tasas.
     * Para mostrar en caso en que esté configurada la opción de emitir
     * certificados con pagos adeudados.
     * En principio no se usará
     */
    private List<GuiaSinPago> guiasSinPago;
    
    /**
     * Variable privada: Listado con los nombres de las tasas a liquidar
     */
    private List<String> lstNombresTasas;    
    
    /**
     * Variable privada: listado de las tasas para un item productivo
     */
    private List<DetalleTasas> lstDetallesTasas;    
    
    /**
     * Variable privada: liquidación total de tasas para un ítem productivo
     */
    private LiqTotalTasas liquidacion;
    
    /**
     * Variable privada: listado de las liquidaciones totales correspondientes a los ítems del Certificado
     */
    private List<LiqTotalTasas> liquidaciones;
    
    /**
     * Variable privada: objeto para gestionar el reporte para la emisión del Certificado
     */
    JasperPrint jasperPrint;
    
    /**
     * Variable estática privada y final: indica la ruta de los reportes 
     */
    private static final String RUTA_VOLANTE = "/resources/reportes/";
    
    /**
     * Variable privada: falg que indica si el Certificado se esta emitiendo sin acreditar el pago de tasas
     */
    private boolean emiteSinPago;    
    
    /**
     * Variable privada: mensaje devuelto al usuario en caso de éxito de la emisión de la Guía
     */
    private String msgExitoEmision;
    
    /**
     * Variable privada: mensaje devuelto al usuario en caso de error de la emisión de la Guía
     */
    private String msgErrorEmision;   
    
    //////////////////////
    /// Notificaciones ///
    //////////////////////
    
    /**
     * Variable privada: sesión de mail del servidor
     */
    @Resource(mappedName ="java:/mail/ambientePrueba") 
    private Session mailSesion;
    
    /**
     * Variable privada: String mensaje a enviar por correo electrónico
     */ 
    private Message mensaje;    
    
    /////////////////
    // Cancelación //
    /////////////////
    
    /**
     * Variable privada: mensaje que indicará que el certificado no se puede cancelar
     */    
    private String msgCancelVencido;

    /**
     * Variable privada: mensaje que indicará que el certificado podrá cancelarse
     */       
    private String msgCancelVigente;
    
    /**
     * Variable privada: mensaje que indica el error al cancelar el certificado
     */
    private String msgErrorCancelCert;    
    
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
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Producto
     */   
    @EJB
    private ProductoFacade prodFacade;    
    // métodos de acceso a las variables

    public String getMsgCancelVencido() {
        return msgCancelVencido;
    }

    public void setMsgCancelVencido(String msgCancelVencido) {
        this.msgCancelVencido = msgCancelVencido;
    }

    public String getMsgCancelVigente() {
        return msgCancelVigente;
    }

    public void setMsgCancelVigente(String msgCancelVigente) {
        this.msgCancelVigente = msgCancelVigente;
    }

    public String getMsgErrorCancelCert() {
        return msgErrorCancelCert;
    }

    public void setMsgErrorCancelCert(String msgErrorCancelCert) {
        this.msgErrorCancelCert = msgErrorCancelCert;
    }

    public String getMsgExitoEmision() {
        return msgExitoEmision;
    }

    public void setMsgExitoEmision(String msgExitoEmision) {
        this.msgExitoEmision = msgExitoEmision;
    }

    public String getMsgErrorEmision() {
        return msgErrorEmision;
    }

    public void setMsgErrorEmision(String msgErrorEmision) {
        this.msgErrorEmision = msgErrorEmision;
    }

    public List<DetalleTasas> getLstDetallesTasas() {
        return lstDetallesTasas;
    }

    public void setLstDetallesTasas(List<DetalleTasas> lstDetallesTasas) {
        this.lstDetallesTasas = lstDetallesTasas;
    }

    public LiqTotalTasas getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(LiqTotalTasas liquidacion) {
        this.liquidacion = liquidacion;
    }

    public List<LiqTotalTasas> getLiquidaciones() {
        return liquidaciones;
    }

    public void setLiquidaciones(List<LiqTotalTasas> liquidaciones) {
        this.liquidaciones = liquidaciones;
    }

    public boolean isEmiteSinPago() {
        return emiteSinPago;
    }

    public void setEmiteSinPago(boolean emiteSinPago) {
        this.emiteSinPago = emiteSinPago;
    }

    public List<String> getLstNombresTasas() {
        return lstNombresTasas;
    }

    public void setLstNombresTasas(List<String> lstNombresTasas) {
        this.lstNombresTasas = lstNombresTasas;
    }

    public List<GuiaSinPago> getGuiasSinPago() {
        if(guiasSinPago == null){
            verGuiasAdeudadas();
        }
        return guiasSinPago;
    }

    public void setGuiasSinPago(List<GuiaSinPago> guiasSinPago) {
        this.guiasSinPago = guiasSinPago;
    }

    public ItemProductivo getItemFuente() {
        return itemFuente;
    }

    public void setItemFuente(ItemProductivo itemFuente) {
        this.itemFuente = itemFuente;
    }

    public boolean isDescontandoProd() {
        return descontandoProd;
    }

    public void setDescontandoProd(boolean descontandoProd) {
        this.descontandoProd = descontandoProd;
    }

    public boolean isEditandoItem() {
        return editandoItem;
    }

    public void setEditandoItem(boolean editandoItem) {
        this.editandoItem = editandoItem;
    }

    public ItemProductivo getItemAsignado() {
        return itemAsignado;
    }

    public void setItemAsignado(ItemProductivo itemAsignado) {
        this.itemAsignado = itemAsignado;
    }

    public List<ItemProductivo> getLstItemsAsignados() {
        lstItemsAsignados = itemFacade.getByGuia(cert);
        return lstItemsAsignados;
    }

    public void setLstItemsAsignados(List<ItemProductivo> lstItemsAsignados) {
        this.lstItemsAsignados = lstItemsAsignados;
    }

    public boolean isViewFuente() {
        return viewFuente;
    }

    public void setViewFuente(boolean viewFuente) {
        this.viewFuente = viewFuente;
    }

    /**
     * Método para completar el listado con los ítems autorizados del certificado.
     * Se puebla el listado y se setea el flag "asignado" a cada item
     * @return 
     */
    public List<ItemProductivo> getLstItemsAutorizados() {
        // instancio el listado de items de la autorización
        lstItemsAutorizados = itemFacade.getByAutorizacion(autFacade.getExistente(cert.getNumFuente()));

        // actualizo el flag 'descontado' de cada uno
        for(ItemProductivo ipOrigen : lstItemsAutorizados){
            for(ItemProductivo ipAsignado : getLstItemsAsignados()){
                if(Objects.equals(ipOrigen.getId(), ipAsignado.getItemOrigen())){
                    ipOrigen.setDescontado(true);
                }
            }
        }        
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
        switch (strPage) {
            case "emision.xhtml":
                cert = certAct;
                // si emite sin acreditar pagos seteo un código de recibo temporal
                if(emiteSinPago){
                    cert.setCodRecibo("TEMP");
                    emiteSinPago = false;
                }
                break;
            case "cancelar.xhtml":
                cert = certAct;
                msgCancelVencido = null;
                msgCancelVigente = null;
                validarVigencia(1);
                break;
        }
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
        // seteo el listado de inmuebles
        setearInmueblesOrigen();
        view = true;
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
     * Método para inicializar la edición de los datos generales del Certificado.
     * Obtiene el cuit del productor y con el el listado de las autorizaciones disponibles
     * Finalmente, redirecciono al formlario de edición
     */
    public void prepareEdit(){
        edit = true;
        // seteo el cuit a buscar
        cuitBuscar = cert.getOrigen().getCuit();
        // seteo la fuente de productos
        buscarFuentesProductos();
        // en cualquier caso, seteo el flag de la autorización seleccionada
        for (Autorizacion aut : lstAutorizaciones){
            if(aut.getNumero().equals(cert.getNumFuente())){
                aut.setAsignadaDesc(true);
            }
        }   
        // cargo el listado de autorizaciones disponibles, seteo la seleccionada y seteo su detalle
        autSelected = autFacade.getExistente(cert.getNumFuente());
        verDetalleFuente();
        page = "general.xhtml";
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
    
    ///////////////
    // Productos //
    ///////////////
    
    /**
     * Método para generar un ItemAsignado con el consecuente descuento del itemFuente correspondiente
     */
    public void descontarProducto(){
        // seteo el saldo temporal para la operatoria
        itemFuente.setSaldoTemp(itemFuente.getSaldo());
        // instancio el itemAsignado y seteo los datos correspondientes
        itemAsignado = new ItemProductivo();
        itemAsignado.setIdProd(itemFuente.getIdProd());
        itemAsignado.setItemOrigen(itemFuente.getId());
        itemAsignado.setClase(itemFuente.getClase());
        itemAsignado.setUnidad(itemFuente.getUnidad());
        itemAsignado.setNombreCientifico(itemFuente.getNombreCientifico());
        itemAsignado.setNombreVulgar(itemFuente.getNombreVulgar());
        itemAsignado.setIdEspecieTax(itemFuente.getIdEspecieTax());
        itemAsignado.setKilosXUnidad(itemFuente.getKilosXUnidad());
        itemAsignado.setCodigoProducto(itemFuente.getCodigoProducto());
        
        TipoParam tipoParamTipoItemActual = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoItem"));
        itemAsignado.setTipoActual(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Extraidos"), tipoParamTipoItemActual));
        
        TipoParam tipoParamTipoItemOrigen = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoItem"));
        itemAsignado.setTipoOrigen(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Autorizados"), tipoParamTipoItemOrigen));
        
        itemAsignado.setTotalOrigen(itemFuente.getTotal());
        itemAsignado.setSaldoOrigen(itemFuente.getSaldo());
        
        descontandoProd = true;
    }    
    
    /**
     * Método para agregar Productos provenientes de un itemFuente a un itemAsignado, 
     * incluye la actualización del saldo del itemFuente
     */
    public void addProducto(){
        EstadoGuia estado = cert.getEstado();
        boolean validaCantidad = true;
        // obtengo el tipoActual (item Autorizado) (tipoOrigen e itemOrigen van nulos
        Parametrica tipoActual = obtenerParametro(ResourceBundle.getBundle("/Config").getString("TipoItem"), ResourceBundle.getBundle("/Config").getString("Extraidos"));
        // valido la cantidad con el saldo
        if(itemAsignado.getTotal() > itemAsignado.getSaldoOrigen() || itemAsignado.getTotal() == 0){
            if(!editandoItem) validaCantidad = false;
            else if(itemAsignado.getTotal() > itemAsignado.getSaldoTemp()) validaCantidad = false;
        }

        // solo continúo si los parámetros están configurados
        if(tipoActual != null && estado != null && validaCantidad){
            
            // actualizo el saldo del itemAsignado
            itemAsignado.setSaldo(itemAsignado.getTotal());  

            // obtengo el Estado a setear según abone tasa o no
            if(cert.getTipo().isAbonaTasa()){
                // si abona tasas, lo habilito a pasar a la liquidación
                estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProductos"));
            }else{
                // si no abona tasas, lo habilito para la emisión
                estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProdAEmitir"));
            }
            
            try{
                // actualizo el saldo del itemOrigen
                for(ItemProductivo ip : lstItemsAutorizados){
                    if(Objects.equals(ip.getId(), itemAsignado.getItemOrigen())){
                        if(editandoItem){
                            ip.setSaldo(itemAsignado.getSaldoTemp() - itemAsignado.getTotal());
                        }else{
                            ip.setSaldo(ip.getSaldo() - itemAsignado.getTotal());
                        }
                        ip.setSaldoKg(ip.getSaldo() * ip.getKilosXUnidad());
                        ip.setDescontado(true);
                        // actualizo el item fuente
                        itemFacade.edit(ip);
                    }
                }
                if(editandoItem){
                    // actualizo el equivalente total en Kg.
                    itemAsignado.setTotalKg(itemAsignado.getTotal() * itemAsignado.getKilosXUnidad());
                    // seteo el equivalente en Kg. del saldo
                    itemAsignado.setSaldoKg(itemAsignado.getTotalKg());
                    // actualizo el item asignado a la guía
                    itemFacade.edit(itemAsignado);
                    editandoItem = false;
                }else{
                    // creo el item asignado a la guía
                    itemAsignado.setTipoActual(tipoActual);
                    // seteo los datos de inserción
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    itemAsignado.setFechaAlta(fechaAlta);
                    // seteo el usuario
                    itemAsignado.setUsuario(usLogueado);
                    // se habilitará cuando se habilite la Guía (NO SE)
                    itemAsignado.setHabilitado(true);
                    // seteo el saldo igual al total
                    itemAsignado.setSaldo(itemAsignado.getTotal());
                    // seteo el equivalente total en Kg.
                    itemAsignado.setTotalKg(itemAsignado.getTotal() * itemAsignado.getKilosXUnidad());
                    // seteo el equivalente en Kg. del saldo
                    itemAsignado.setSaldoKg(itemAsignado.getTotalKg());
                    // seteo la guía
                    itemAsignado.setGuia(cert);
                    itemFacade.create(itemAsignado);
                    // asigno el estado
                    if(estado != cert.getEstado()){
                        cert.setEstado(estado);
                    }
                    guiaFacade.edit(cert);
                    cert.getItems().add(itemAsignado);
                    // actualizo el flag
                    descontandoProd = false;
                }
                itemAsignado = null;
            }catch(Exception ex){
                JsfUtil.addErrorMessage("Hubo un error agregadon el Producto al Certificado: " + ex.getMessage());
            }
        }else if(tipoActual == null){
            JsfUtil.addErrorMessage("No se pudo encontrar un Parámetro para el Tipo de Item: 'Extraidos'.");
        }
        if(!validaCantidad){
            JsfUtil.addErrorMessage("La cantidad a descontar debe ser menor o igual al saldo disponible y mayor a 0.");
        }
        if(estado == null){
            JsfUtil.addErrorMessage("No se pudo encontrar un Estado de Certificado para: 'GuiaConProductos'.");
        }
    }
    
    /**
     * Método para eliminar un item productivo asociado a una Guía
     */
    public void deleteProducto(){
        try{
            // actualizo el itemOrigen, agregando al saldo lo descontado para el item a eliminar
            ItemProductivo itemOrigen = itemFacade.find(itemAsignado.getItemOrigen());
            if(itemOrigen != null){
                // actualizo el saldo del item origen
                float saldo = itemOrigen.getSaldo();
                float saldoActualizado = saldo + itemAsignado.getTotal();
                itemOrigen.setSaldo(saldoActualizado);
                // actualizo el saldo en Kg
                itemOrigen.setSaldoKg(itemOrigen.getSaldo() * itemOrigen.getKilosXUnidad());
                // actualizo el itemOrigen
                itemFacade.edit(itemOrigen);
                // elimino el itemAsignado de la Guía
                itemFacade.remove(itemAsignado);
                // si estoy eliminando el único producto de la Guía, actualizo su estado
                getLstItemsAsignados();
                if(lstItemsAsignados.isEmpty()){
                    // asumo que si el producto fue creado, había configurado un Estado incicial para la Guía
                    EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaInicial"));
                    cert.setEstado(estado);
                    List<ItemProductivo> items = new ArrayList<>();
                    cert.setItems(items);
                    guiaFacade.edit(cert);
                }
                // muestro mensaje
                JsfUtil.addSuccessMessage("El Producto ha sido eliminado de la Guía correctamente.");
                itemAsignado = null;
            }
            editandoItem = false;
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error eliminando el Producto. " + ex.getMessage());
        }
    }
    
    /**
     * Método para limpiar el formulario para el descuento y volver al listado
     */
    public void limpiarDescuento(){
        descontandoProd = false;
    }
    
    /**
     * Método para habilitar el formulario para la edición del Item asignado
     */
    public void prepareEditItem(){
        // obtengo el total autorizado del Item origen y seteo cantProdDescuento y saldo con el total y el saldo del Item asignado
        for(ItemProductivo ip : lstItemsAutorizados){
            if(Objects.equals(ip.getId(), itemAsignado.getItemOrigen())){
                itemAsignado.setSaldoTemp(ip.getSaldo() + itemAsignado.getTotal());
                itemAsignado.setSaldoOrigen(ip.getSaldo());
                itemAsignado.setTotalOrigen(ip.getTotal());
            }
        }
        editandoItem = true;
    }
    
    /**
     * Método para limpiar el formulario de edición del producto descontado
     */
    public void limpiarEdicion(){
        // vuelvo a los datos originales
        editandoItem = false;
        itemAsignado = null;
    }

    /**
     * Método para generar un volante de pago de un certificado ya emitido.
     * Invoca al método imprimirVolante()
     */
    public void emitirVolanteAdeudado(){
        imprimirVolante();
    }   
    
    /**
     * Método para generar un código de recibo temporal.
     * Setea el flag para que en el método cargarFrame()
     * se genere un código temporal para el recibo.
     */
    public void generarCodReciboTemp(){
        emiteSinPago = true;
        cargarFrame("emision.xhtml");
    }

    /**
     * Método para registrar un Código de recibo para la Guía.
     * Una vez efectuado el pago de las tasas
     */
    public void registrarCodRecibo(){
        try{
            // actualizo
            guiaFacade.edit(cert);
            cert = guiaFacade.find(cert.getId());
            JsfUtil.addSuccessMessage("El código del recibo fue agregado al Certificado. Ya puede emitirlo.");
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error registrando el código del recibo. " + ex.getMessage());
        }
    }    
    
    /**
     * Método para emitir un certificado.
     * Se setean los datos finales del certificado y se llama a los métodos
     * generarCopias() para generar las copias a incluir en el pdf, e
     * imprimirCert() para generar el pdf con las copias incluidas.
     */
    public void emitir() {
        List<Guia> certs = new ArrayList<>();
        // encripto el código para generar el qr en el reporte y lo asigno al certificado
        Date fechaEmision = new Date(System.currentTimeMillis());
        String codQr = "guía:" + cert.getCodigo() + "|fuente:" + cert.getNumFuente();
        cert.setCodQr(codQr);
        // asigno la fecha de emisión
        cert.setFechaEmisionGuia(fechaEmision);
        // asigno la fecha de vencimiento, si el tipo de Guía tiene vigencia asignada.
        if(cert.getTipo().getVigencia() > 0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(cert.getFechaEmisionGuia());
            calendar.add(Calendar.DAY_OF_YEAR, cert.getTipo().getVigencia());
            cert.setFechaVencimiento(calendar.getTime());
        }
        // cambio el estado del certificado
        EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaEmitida"));
        cert.setEstado(estado);        
        // seteo "formEmitidos" en 0
        cert.setFormEmitidos(0);
       
        // obtengo la autorización vinculada al certificado
        Autorizacion aut = autFacade.getExistente(cert.getNumFuente());

        // genero las copias
        generarCopias(aut, cert.getItems(), certs);
        // imprimo las copias en pdf
        imprimirCert(certs, aut);
        // actualizo el certificado. Si tiene un código de recibo temporal lo pongo nulo
        if(cert.getCodRecibo() != null){
            if(cert.getCodRecibo().equals("TEMP")){
                cert.setCodRecibo(null);
            }
        }
        try{
            guiaFacade.edit(cert);
            if(enviarCorreoEmision()){
                msgExitoEmision = "El certificado se emitió correctamente y se notificó a su titular.";
            }else{
                msgErrorEmision = "El certificado se emititió correctamente pero hubo un error enviando la notificación a su titular.";
            }
        }catch(Exception ex){
            msgErrorEmision = "Hubo un error emitiendo el certificado " + ex.getMessage();
        }
    }    
    
    /////////////////////////////////////////////////
    // Métodos para la cancelación de Certificados //
    /////////////////////////////////////////////////

    /**
     * Método para limpiar el formulario para la cancelación de cerrtificados
     * Resetea el campo "obs" del certificado.
     */
    public void limpiarFormCancel(){
        cert.setObs("");
    } 
    
    /**
     * Método para cancelar un certificado vigente.
     * Se actualiza y reintegran los productos a su orgien y se notifica la novedad al titular del certificado
     */
    public void cancelarCert(){
        boolean actualizadaLocal = false, actualizadosItems = false, titularNotificado = false;
        msgErrorCancelCert = "";
        
        try{
            ///////////////
            // actualizo //
            ///////////////
            EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaCancelada"));
            cert.setEstado(estado); 
            guiaFacade.edit(cert);
            actualizadaLocal = true;
            
            /////////////////////////////////////////////////////////////////////
            // reintegro los productos y deshabilito los ítems del certificado //
            /////////////////////////////////////////////////////////////////////
            // obtengo los items
            List<ItemProductivo> lstItems = cert.getItems();
            // recorro el listado y por cada uno retorno los productos a su origen original y deshabilito
            for (ItemProductivo item : lstItems){
                // obtengo el id del item origen
                Long idItemOrigen = item.getItemOrigen();
                // obtengo el item origen
                ItemProductivo itemOrigen = itemFacade.find(idItemOrigen);
                // seteo los saldos actuales
                float saldoActual = itemOrigen.getSaldo();
                float saldoKgActual = itemOrigen.getSaldoKg();
                // actualizo los saldos
                itemOrigen.setSaldo(saldoActual + item.getTotal());
                itemOrigen.setSaldoKg(saldoKgActual + item.getTotalKg());
                // actualizo el origen
                itemFacade.edit(itemOrigen);
                // deshabilito el item actual
                item.setHabilitado(false);
                itemFacade.edit(item);
            }
            actualizadosItems = true;
            
            /////////////////////////
            // notifico al titular //
            /////////////////////////       
            if(notificarCancelacion()){
                titularNotificado = true; 
            }else{
                titularNotificado = false;
            }
            // armo el mensaje
            if(!actualizadaLocal || !actualizadosItems || !titularNotificado){
                msgErrorCancelCert = "Hubo un error cancelando el certificado. ";
                if(!actualizadaLocal ) msgErrorCancelCert = msgErrorCancelCert + "No se actualizó el certificado localmente. ";
                if(!actualizadosItems ) msgErrorCancelCert = msgErrorCancelCert + "No se actualizaron los items. ";
                if(!titularNotificado ) msgErrorCancelCert = msgErrorCancelCert + "No se pudo notificar al titular.";
                JsfUtil.addErrorMessage(msgErrorCancelCert);
            }else{
                JsfUtil.addSuccessMessage("La Guía se canceló exitosamente, "
                        + "se reintegraron sus productos al origen, "
                        + "se actualizó el estado en el componente de Control y se notificó al titular del certificado");
            }
            
        }catch(Exception ex){
            msgErrorCancelCert = "Hubo un error cancelando el certificado. ";
            if(!actualizadaLocal ) msgErrorCancelCert = msgErrorCancelCert + "No se actualizó el certificado localmente. ";
            if(!actualizadosItems ) msgErrorCancelCert = msgErrorCancelCert + "No se actualizaron los items. ";
            if(!titularNotificado ) msgErrorCancelCert = msgErrorCancelCert + "No se pudo notificar al titular.";
            JsfUtil.addErrorMessage(msgErrorCancelCert + ex.getMessage());
        }
    }   
    
    ///////////////////////////////////////////
    // Métodos para la extensión de vigencia //
    ///////////////////////////////////////////      
    
    /**
     * Método para extender la vigencia de un Certificado.
     * Extiende su fecha de vencimiento, guarda los motivos del cambio y
     * genera un nueo código qr
     */
    public void extenderVenc(){
        boolean error = false;
        String msg = "";
        // valido que la fecha de vencimiento sea mayor a la original
        Guia g = guiaFacade.find(cert.getId());
        if(cert.getFechaVencimiento().after(g.getFechaVencimiento())){
            // encripto el código para generar el qr en el reporte y lo asigno al certificado
            Date fechaExtend = new Date(System.currentTimeMillis());
            String codLiso = cert.getCodigo() + "_" + fechaExtend.toString();
            cert.setCodQr(CriptPass.encriptar(codLiso));        
            // actualiza
            guiaFacade.edit(cert);
            
            // notifico al titular que se extendió el período de vigencia
            if(cert.getOrigen().getEmail() != null){
                if(notificarExtensionVig(cert.getOrigen().getEmail())){
                    msg += " Se notificó al titular del certificado la extensión de la vigencia del mismo.";
                }else{
                    error = true;
                    msg += " No se pudo notificar al titular del certificado la extensión de la vigencia del mismo.";
                }
            }else{
                error = true;
                msg += " No se pudo notificar al titular del certificado la extensión de la vigencia del mismo dado que no tiene registrada una dirección de correo.";
            }
            
        }else{
            error = true;
            msg = "La nueva fecha de vencimiento debe ser mayor a la existente.";
        }
        
        if(error){
            JsfUtil.addErrorMessage(msg + " Por favor, contacte al administrador.");
        }else{
            JsfUtil.addSuccessMessage(msg);
        }
    }    
    
    /**
     * Método para limpiar los datos del formulario de extensión de vencimiento.
     * Vacía el campo observaciones del Certificado.
     */
    public void limpiarFormExtend(){
        cert = guiaFacade.find(cert.getId());
        cert.setObs("");
    }    
    
    /**
     * Método para emitir el pdf con los datos actualizados del Certificado.
     * Obtengo la autorización de la cual surgen los productos.
     * Mando a generar el listado de copias mediante generarCopias()
     * y luego imprimo el pdf con las copias generadas mediante imprimirCert().
     * Y si todo salió bien mando a imprimir las copias
     */
    public void emitirExtend(){
        List<Guia> copias = new ArrayList<>(); 
        // obtengo la autorización vinculada al Certificado
        Autorizacion aut = autFacade.getExistente(cert.getNumFuente()); 
        // agrupo los items
        List<ItemProductivo> itemsAgr;
        itemsAgr = cert.getItems();
      
        // genero las copias
        generarCopias(aut, itemsAgr, copias);
        // imprimo el pdf
        imprimirCert(copias, aut);
    }    
    
    /*********************
     * Métodos privados **
     *********************/
    
    /**
     * Método para limpiar el formulario de búsqueda de certificados.
     * Resetea todas las variables y listados.
     * Utilizado por init() y poblarListado()
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
    
    /**
     * Método para obtener una Paramétrica según su nombre y nombre del Tipo.
     * Utilizado en addProducto()
     * @param nomTipo String nombre del Tipo de Paramétrica
     * @param nomParam String nombre de la Paramétrica
     * @return Parametrica paramétrica obtenida
     */
    private Parametrica obtenerParametro(String nomTipo, String nomParam) {
        TipoParam tipo = tipoParamFacade.getExistente(nomTipo);
        return paramFacade.getExistente(nomParam, tipo);
    }    
    
    /**
     * Método privado para listar los certificados que se emitieron sin registrar el comprobante de pago de tasas.
     * Invocado por getGuiasSinPago(), solo se ejecuta si la aplicación está configurada para emitir certificados
     * sin registrar los pagos de tasas.
     */
    private void verGuiasAdeudadas(){
        guiasSinPago = new ArrayList<>();
        lstNombresTasas = new ArrayList<>();
        List<Guia> adeudadas = guiaFacade.getSinPago(cert.getOrigen().getCuit());
        for (Guia g : adeudadas){
            // limpio el listado de liquidaciones
            liquidaciones = new ArrayList<>();
            // obtengo el total adeudado
            liquidarTasas(g);
            GuiaSinPago gsp = new GuiaSinPago();
            gsp.setId(g.getId());
            gsp.setCodGuia(g.getCodigo());
            gsp.setFechaEmision(g.getFechaEmisionGuia());
            gsp.setFechaVenc(g.getFechaVencimiento());
            gsp.setTotalAdeudado(liquidaciones.get(0).getTotalALiquidar());
            guiasSinPago.add(gsp);
        }
    }      
    
    /**
     * EN PRINCIPIO NO SE USARA
     * Método para liquidar todas las tasas de los productos de una guía para luego generar el volante de pago.
     * Primero recorre las tasas configuradas para la guía. Por cada una verifica si discrimina valores según la configuración.
     * Si discrimina, verifica por cada tasa con discriminación registrada que, además de estar en la base, deberán estar también en el properties.
     * Al momento son 5:
     *  1. Según el origen del predio: CondAforoFiscal=FISCAL CON AFORO. El valor de la variable configurada deberá ser el del origen del predio a validar, de las fuentes de productos.
     *  2. Según el destino de los productos: CondDerInspExt=DESTINO EXTERNO y CondDerInspInt=DESTINO INTERNO
     *  3. Según el tipo de intervención autorizado para la fuente de productos: IntervPCUS=CAMBIO USO DEL SUELO y IntervPM=PLAN MANEJO SOSTENIBLE.
     *  El valor de cada variable configurada deberá ser el tipo de intervención que corresponda para cada caso.
     * Según corresponda en cada caso se agregará o no al listado de tasas a liquidar.
     * Si no discrimina, se agrega directamente.
     * Luego, se recorren los ítems productivos y por cada uno se calcula el detalle correpondiente a cada tasa que integra el listado, 
     * siempre que el ítem la tenga configurada.
     * Finalmente se genera el listado de las liquidaciones que se mostrarán en pantalla y será luego mandado como datasource para el reporte.
     * Invocado por cargarFrame() al llamar a la vista tasas.xhtml
     * @param guiaAct Guia certificado del cual se liquidarán las tasas.
     */
    private void liquidarTasas(Guia guiaAct) {
        for(TipoGuiaTasa tgt : guiaAct.getTipo().getTasas()){
            // por cada tasa que tenga configurada la guía a liquidar, verifico si tiene discriminaciones
            if(tgt.getTipo().isLeeConf()){
                // si las tiene, respondo en cada caso
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("CondAforoFiscal"))){
                    // si discrimina por origen del predio, verifico si el predio de la fuente de productos tiene un origen que corresponda a la discriminación
                    Autorizacion aut = autFacade.getExistente(cert.getNumFuente());
                    if(aut.getInmuebles().get(0).getOrigen().getNombre().equals(ResourceBundle.getBundle("/Config").getString("CondAforoFiscal"))){
                        // si el origen del predio amerita el pago, guardo la tasa en el listado
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("CondDerInspExt"))){
                    // si discrimina por el destino externo de los productos se la agrega si el destino de la guía es externo
                    if(guiaAct.isDestinoExterno()){
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("CondDerInspInt"))){
                    // si discrimina por el destino interno de los productos se la agrega si el destino de la guía es interno
                    if(!guiaAct.isDestinoExterno()){
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("IntervPCUS"))){
                    // si discrimina por intervención PCUS de la fuente de productos, se la agrega si ese es el tipo de intervención
                    Autorizacion aut = autFacade.getExistente(cert.getNumFuente());
                    if(aut.getIntervencion().getNombre().equals(ResourceBundle.getBundle("/Config").getString("IntervPCUS"))){
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("IntervPM"))){
                    // si discrimina por intervención PM de la fuente de productos, se la agrega si ese es el tipo de intervención
                    Autorizacion aut = autFacade.getExistente(cert.getNumFuente());
                    if(aut.getIntervencion().getNombre().equals(ResourceBundle.getBundle("/Config").getString("IntervPM"))){
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
            }else{
                // si no las tiene, directamente agrego la tasa al listado
                lstNombresTasas.add(tgt.getTipo().getNombre());
            }
        }

        // cargo los detalles por item
        lstDetallesTasas = new ArrayList<>();
        for(ItemProductivo item : guiaAct.getItemsAgrupados()){
            // obtengo el producto para sacar las tasas
            Producto prod = prodFacade.find(item.getIdProd());
            // instancio el DetalleTasa
            DetalleTasas detTasa = new DetalleTasas();
            // instancio el listado de TasasModel
            List<DetalleTasas.TasaModel> lstTasaModel = new ArrayList<>();
            detTasa.setNombreProd(prod.getEspecieLocal().getNombreVulgar());
            detTasa.setClase(prod.getClase().getNombre());
            detTasa.setCantidad(item.getTotal());
            detTasa.setUnidad(item.getUnidad());
            // obtengo las tasas del producto. En caso de no tener tasas asignadas irán todas en 0
            if(prod.getTasas().isEmpty()){
                for(String nomTasa : lstNombresTasas){
                    lstTasaModel.add(new DetalleTasas.TasaModel(nomTasa, 0, 0));
                }
            }else{
                for(String nomTasa : lstNombresTasas){
                    // por cada tasa asigno su valor, en los casos que la tengan configurada
                    float subTotal;
                    for(ProductoTasa prodTasa : prod.getTasas()){
                        if(nomTasa.equals(prodTasa.getTipo().getNombre())){
                            lstTasaModel.add(new DetalleTasas.TasaModel(nomTasa, prodTasa.getMonto(), prodTasa.getMonto() * detTasa.getCantidad()));
                            subTotal = prodTasa.getMonto() * detTasa.getCantidad();
                            detTasa.setTotal(detTasa.getTotal() + subTotal);
                        }
                    }
                }
                // si hay tasas no configuradas para el item, las seteo en 0
                for(String nomTasa : lstNombresTasas){
                    boolean existe = false;
                    for(DetalleTasas.TasaModel tasa : lstTasaModel){
                        if(nomTasa.equals(tasa.getHeader())){
                            existe = true;
                        }
                    }
                    if(!existe){
                        lstTasaModel.add(new DetalleTasas.TasaModel(nomTasa, 0, 0));
                    }
                }
            }

            // asigno el listado de tasas
            detTasa.setTasas(lstTasaModel);
            // agrego el DetalleTasa al listado
            lstDetallesTasas.add(detTasa);
        }   
        // instancio la Liquidación y el array para el reporte
        liquidacion = new LiqTotalTasas();
        liquidaciones = new ArrayList<>();
        // seteo los datos para mostrar en el reporte
        liquidacion.setTipoGuia(guiaAct.getTipo().getNombre());
        liquidacion.setCodGuia(guiaAct.getCodigo());
        liquidacion.setDetalles(lstDetallesTasas);
        liquidaciones.add(liquidacion);
    }    
    
    /**
     * EN PRINCIPIO NO SE USARA
     * Método que genera el pdf con el volante a partir de un listado de LiqTotalTasas con las tasas de un certificado
     * Llamado por generarVolante()
     */
    private void imprimirVolante() {
        try{
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(liquidaciones);
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "volante.jasper");
            jasperPrint =  JasperFillManager.fillReport(reportPath, new HashMap(), beanCollectionDataSource);
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=volante_pago_" + liquidaciones.get(0).getCodGuia() + ".pdf");
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();

            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete();
        }catch(JRException | IOException ex){
            JsfUtil.addErrorMessage("Hubo un error al imprimir el volante de pago. " + ex.getMessage());
        }
    }    
    
    /**
     * Método para generar un listado con las copias del certificado.
     * Utilizado por emitir() y emitirExtend()
     * @param aut Autorización desde la cual se toman los productos
     * @param itemsAgr Listado de Items agrupados
     * @param certs Listado de los certificados a poblar como paso previo a la generación del pdf
     */
    private void generarCopias(Autorizacion aut, List<ItemProductivo> itemsAgr, List<Guia> certs){
        Guia c;
        // obtengo la ruta al martillo (del primer inmueble asociado)
        String rutaMartillo = aut.getInmuebles().get(0).getRutaArchivo() + aut.getInmuebles().get(0).getNombreArchivo();

        try{
            for(CopiaGuia copia : cert.getTipo().getCopias()){
                // creo el inputStream con el martillo
                FileInputStream fisMartillo = new FileInputStream(rutaMartillo);
                
                // seteo la Guía en el listado
                c = new Guia();
                c.setCodigo(cert.getCodigo());
                c.setDestinoCopia(copia.getDestino());
                c.setFechaAlta(cert.getFechaAlta());
                c.setFechaEmisionGuia(cert.getFechaEmisionGuia());
                c.setFechaVencimiento(cert.getFechaVencimiento());
                c.setItems(itemsAgr);
                c.setOrigen(cert.getOrigen());
                c.setTipo(cert.getTipo());
                c.setProvincia(cert.getProvincia());
                c.setCodQr(cert.getCodQr());
                c.setMartillo(fisMartillo);

                // agrego el certificado al listado
                certs.add(c);
            }  
        }catch(FileNotFoundException ex){
            JsfUtil.addErrorMessage("Hubo un error generando el martillo para el certificado. " + ex.getMessage());
        } 
    }

    /**
     * Método para generar el pdf con las copias del certificado según su configuración
     * A partir del listado de las copias a imprimir, se validan los parámetros a ingresar,
     * y se genera el pdf con las copias del certificado.
     * Utilizado por emitir() y emitirExtend()
     * @param certs List<Guia> listado con las copias de los certificados a imprimir
     * @param aut Autorizacion autorización fuente de los productos del certificado para tomar de allí el martillo del inmueble
     */
    public void imprimirCert(List<Guia> certs, Autorizacion aut){
        try{
            final Map<String,Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(certs);
            String reportPath;
            reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "guia.jasper");
            
            // guardo el expediente como parámetro
            parameters.put("expediente", aut.getNumExp());
            // si discrimina tasa según el origen del predio guardo dicho origen como parámetro
            if(ResourceBundle.getBundle("/Config").getString("DiscTasaOrigen").equals("si")){
                parameters.put("origenPredio", aut.getInmuebles().get(0).getOrigen().getNombre());
            }
            // si muestra la autorización en el Certificado, agrego su número como parámetro
            if(ResourceBundle.getBundle("/Config").getString("MuestraAutGuias").equals("si")){
                parameters.put("numAut", aut.getNumero());
            }
            // si muestra los datos catastrales del predio, lo agrego como parámetro
            if(ResourceBundle.getBundle("/Config").getString("MuestraPredioGuias").equals("si")){
                parameters.put("inmCatastro", aut.getInmuebles().get(0).getIdCatastral());
            }
 
            jasperPrint =  JasperFillManager.fillReport(reportPath, parameters, beanCollectionDataSource);
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=cert_" + certs.get(0).getCodigo() + ".pdf");

            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();

            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete(); 
        }catch(JRException | IOException ex){
            msgErrorEmision = "Hubo un error generando el pdf del Certificado. " + ex.getMessage();
        }
    }    
    
    /**
     * Método para enviar un correo electrónico al titular del certificado
     * Utilizado en emitir()
     * @return boolean verdadero o falso según el correo se haya enviado o no
     */
    private boolean enviarCorreoEmision(){  
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>Se acaba de emitir el Certificado " + cert.getCodigo() + " a su nombre.</p>"
                + "<p>Dispone hasta el " + formateador.format(cert.getFechaVencimiento()) + " para utilizarlo.</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "<br/> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";
        
        result = enviarCorreo(cert.getOrigen().getEmail(), bodyMessage, "Aviso de emisión de Certificado");
        return result;   
    }   
    
    /**
     * Método para notificar a titular de un Certificado que el mismo fue cancelado.
     * Utilizado en cancelarCert()
     * @return boolean verdadero o falso según el correo se haya enviado o no
     */
    private boolean notificarCancelacion(){
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>El Certificado " + cert.getCodigo() + " del cual es titular, acaba de ser cancelado. Todo lo documentado en el mismo queda sin efecto. "
                + "Los productos descontados han sido retornados a sus fuentes de origen. Por cualquier consulta podrá dirigirse a la Autoridad local.</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "<br/> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";      
        
        result = enviarCorreo(cert.getOrigen().getEmail(), bodyMessage, "Notificación de cancelación de Certificado");
        return result;       
    }   
    
    /**
     * Método para enviar un correo electrónico al titular de un Certificado
     * para notificarlo de la extensión del período de vigencia.
     * Utilizado por extenderVenc()
     * @param mail String dirección de correo electrónico del titular
     * @return boolean verdadero o false según el resultado del envío.
     */
    private boolean notificarExtensionVig(String mail) {
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date hoy = new Date(System.currentTimeMillis());
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        
        bodyMessage = "<p>Estimado/a</p> "
                + "Hoy: " + formateador.format(hoy) + " se ha extendido la vigencia del "
                + "Certificado " + cert.getCodigo() + " emitido el " + formateador.format(cert.getFechaEmisionGuia()) + ", "
                + "que ahora vencerá el " + formateador.format(cert.getFechaVencimiento()) + ".</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "<br/> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";

        result = enviarCorreo(mail, bodyMessage, "Aviso de extensión del período de vigencia del Certificado");
        return result;               
    }     
    
    /**
     * Método privado para el envío de correo.
     * Utilizado por enviarCorreoEmision(), notificarCancelacion() y notificarExtensionVig()
     * @param mail String dirección de correo
     * @param bodyMessage String mensaje a enviar
     * @param subject String motivo del mensaje
     * @return boolean verdadero o false según el resultado del envío.
     */
    private boolean enviarCorreo(String mail, String bodyMessage, String subject){
        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(mail));
            mensaje.setSubject(ResourceBundle.getBundle("/Bundle").getString("Aplicacion") + ": " + subject);
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            return true;
            
        }catch(MessagingException ex){
            System.out.println("Hubo un error enviando el correo al usuario" + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Método para validar la vigencia del certificado en el caso que se quiera cancelar.
     * Según esté vigente se setea el mensaje correspondiente para mostrar en la vista /guia/gestion/cancel.xhtml
     */
    private void validarVigencia(Integer tipo) {
        Date hoy = new Date(System.currentTimeMillis());
        if(tipo == 1){
            if(cert.getFechaVencimiento().after(hoy)){
                msgCancelVigente = ResourceBundle.getBundle("/Config").getString("MsgCancelVigente");
            }else{
                msgCancelVencido = ResourceBundle.getBundle("/Config").getString("MsgCancelVencida");
            }
        }
    }    
}
