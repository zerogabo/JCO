package com.bigo.sap;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;


public class SAPConnectionManager
{
	
	private static final Logger LOG = Logger.getLogger(SAPConnectionManager.class);
	
    static class MyDestinationDataProvider implements DestinationDataProvider
    {
        private DestinationDataEventListener eL;

        private Properties ABAP_AS_properties; 
        
        public Properties getDestinationProperties(String destinationName)
        {
            if(destinationName.equals("ABAP_AS") && ABAP_AS_properties!=null)
                return ABAP_AS_properties;
            
            return null;
            //alternatively throw runtime exception
            //throw new RuntimeException("Destination " + destinationName + " is not available");
        }

        public void setDestinationDataEventListener(DestinationDataEventListener eventListener)
        {
            this.eL = eventListener;
        }

        public boolean supportsEvents()
        {
            return true;
        }
        
        
        public void releaseFunction(final JCoDestination destination)
    	{
    		try
    		{
    			JCoContext.end(destination);
    		}
    		catch (final JCoException e)
    		{
    			LOG.error(e);
    		}
    	}

        
        
        public JCoFunction executeFunction(final JCoFunction function, final JCoDestination destination) throws Exception
    	{
    		long start = 0;
    		int runs = 0;
    		try
    		{
    			runs = 100 * 1000;
    			start = System.nanoTime();
    			LOG.info(String.format("Start SAP function: %s", function.getName()));

    			function.execute(destination);
    			
    		}
    		finally
    		{
    			releaseFunction(destination);

    			final long time = System.nanoTime() - start;
    			LOG.info(String.format("End SAP function: %s", function.getName()));
    			LOG.info(String.format("Average log time was %,d ns%n", time / runs));
    		}

    		return function;
    	}
        
        
        
    	public JCoFunction createFunction(final String name, final JCoDestination destination) throws Exception
    	{
    		JCoContext.begin(destination);
    		JCoFunction function = null;

    		function = destination.getRepository().getFunction(name);

    		return function;
    	}

        
        void changePropertiesForABAP_AS(Properties properties)
        {
            if(properties==null)
            {
                eL.deleted("ABAP_AS");
                ABAP_AS_properties = null;
            }
            else 
            {
                if(ABAP_AS_properties!=null  && !ABAP_AS_properties.equals(properties))
                    eL.updated("ABAP_AS");
                ABAP_AS_properties = properties;
            }
        }
    }   
}