package com.bigo.sap;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;

/**
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.bigo.sap.SAPConnectionManager.MyDestinationDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

/**
 * @author Ramiro
 *
 */
public class DefaultTecnoliteSAPService {

	private final static String VKORG = "1200";

	public static void main(String[] args) throws Exception {
		Properties connectProperties = new Properties();
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "137.0.164.111");
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "300");
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, "BAPIPOR");
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "BAPIPOR");
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "en");
		
		final File destCfg = new File("produccion.jcoDestination");

		final FileOutputStream fos = new FileOutputStream(destCfg, false);
		connectProperties.store(fos, "produccion");
		fos.close();

		MyDestinationDataProvider myProvider = new MyDestinationDataProvider();

		//com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(myProvider);
		//myProvider.changePropertiesForABAP_AS(connectProperties);

		JCoDestination sapDest = JCoDestinationManager.getDestination("produccion");
		final JCoFunction sapFunc;

		sapFunc = myProvider.createFunction("ZBAPI_CTE_MAT_EXI", sapDest);

		// input header final String salesOrganization =
		// neorisFacade.getCurrentBaseStore().getUid();
		sapFunc.getImportParameterList().setValue("DISTRIBUIDOR", "10786");
		sapFunc.getImportParameterList().setValue("DESTINATARIO", "10837");
		sapFunc.getImportParameterList().setValue("MATERIAL", "50651021");
		sapFunc.getImportParameterList().setValue("CANTIDAD", 1);

		myProvider.executeFunction(sapFunc, sapDest);

		//final List<SAPClientsResponse> clientsResponseList = new ArrayList<SAPClientsResponse>();
		//final JCoTable masterTable = sapFunc.getTableParameterList().getTable(1);
		
		// get the structure final JCoStructure exportStructure =
		final JCoTable codes = sapFunc.getTableParameterList().getTable("EXISTENCIA");
		
		if (codes.getNumRows() > 0)
		{
			
			BigDecimal precio =codes.getBigDecimal("PRECIO");

			BigDecimal precioMaster =codes.getBigDecimal("PRECIO_MASTER");

			BigDecimal precioLista = codes.getBigDecimal("PRECIOLISTA");

			
			//return sapStockAndPricesResponse;
		}
		

//		while (masterTable.nextRow()) {
//			final SAPClientsResponse clientsResponse = new SAPClientsResponse();
//
//			clientsResponse.setOrganizacionVentas(masterTable.getString("VKORG"));
//			clientsResponse.setNumeroCliente(masterTable.getString("KUNNR"));
//			clientsResponse.setNombre(masterTable.getString("NAME"));
//			clientsResponse.setCalle(masterTable.getString("STREET"));
//			// clientsResponse.setNumero(masterTable.getString("HOUSE_NUM"));
//			clientsResponse.setNumero_2(masterTable.getString("HOUSE_NUM2"));
//			clientsResponse.setColonia(masterTable.getString("STR_SUPPL3"));
//			clientsResponse.setCodigoPostal(masterTable.getString("POST_CODE1"));
//			clientsResponse.setPoblacion(masterTable.getString("ORT01"));
//			clientsResponse.setEstado(masterTable.getString("REGIO_TEXT"));
//			clientsResponse.setPais(masterTable.getString("COUNTRY_TEXT"));
//			clientsResponse.setTelefono(masterTable.getString("TELF1"));
//
//			clientsResponseList.add(clientsResponse);
//
//			System.out.println("el estado es " + clientsResponse.getEstado());
//		}
		// return clientsResponseList;

		System.out.println("ABAP_AS destination is ok");

	}

}
