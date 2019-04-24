# Registro de autorizaciones y emisión de certificados

Descripción de la aplicación:
-----------------------------

Esta aplicación, componente principal del SACVeBio es la responsable de gestionar el registro de Autorizaciones para la obtención de productos de biodiversidad y la emisión de Certificados de legítima tenencia de productos, otorgados por la Autoridad local de aplicación.


Ambiente y dependencias:
------------------------

Dado que esta aplicación provée una API REST de servicios a otras aplicaciones del vinculadas al SACVeBio y, a su vez, utiliza los servicios de datos comunes para SACVeBio y SACVeFor, necesita las siguientes dependencias al momento de generar el .war  
* `paqTerritorial v1.1.0` de la aplicación scvf-terr  
* `paqEspecies v1.1.0` de la aplicación scvf-tax  
* `paqRue v1.1.0` de la aplicación scvf-rue  
* `paqScvbCre v1.0.0` de la presente aplicación  
> Remitidos por la coordinación del proyecto


Configuraciones:
----------------

1. En el directorio raiz de la aplicación, crear el archivo `pom.xml` y copiar el contenido de `pom.xml.dist`
2. En el paquete `ar.gob.ambiente.sacvebio.cre.facades`, crear el `AbstractFacade.java` y copiar el contenido de `AbstractFacade.java.dist`  
3. En el directorio `/src/main/webapp/WEB-INF`, crear el archivo `jboss-web.xml` y copiar el contenido de `jboss-web.xml.dist`  
4. En el directorio `/src/main/resources/META-INF` crear el archivo `persistence.xml` y copiar el contenido de `persistence.xml.dist`  
5. En el directorio `/src/main/resources/<default package>` crear los archivos `Config.properties` y `Bundle.properties`, y copiar el contenido de los `.dist`  
6. Crear los directorios para las imágenes de martillos según se indica en el`Config. properties`:  
	* C:\wildfly\resources
	* C:\wildfly\TMP
> En caso de ubicarlos en otro path, actualizar las variables `RutaArchivos`, `SubdirTemp`, `SubdirMartillos`, `UrlMartillos` del `Config.properties`  
7. En el directorio `/src/main/webapp/resources/img` agregar los archivos que remitirá la coordinación del proyecto
	* `escudo.gif`
	* `favicon.ico`
8. En el directorio `/src/main/webapp/resources/reportes` agregar los archivos que remitirá la coordinación del proyecto
	* `guia.jasper`
	* `guia.jrxml`
	* `logo.jpg`  


Datos:
------

Deberá crearse en el servidor de base de datos la base `sacvebio_cre` con los parámetros de creación por defecto. Deberá restaurarse luego con el 
backup remitido por la coordinación del proyecto.

Se deberá crear en el servidor de aplicaciones el recurso `Datasource` con 
los siguientes parámetros (los que no se indiquen quedarán por defecto):  
Nombre: `RegistroEmisionDS`  
JNDI:  `java:/registroEmisionDS`  
Driver: `postgresql`  
Connection URL: `la que corresponda a la configuración local`  
Usuario y password de acceso: `los que correspondan a la configuración local`

> El driver deberá estar previamente registrado en el servidor de aplciaciones.