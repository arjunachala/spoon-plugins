/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpccsystems.pentaho.job.eclexecute;

import java.util.ArrayList;
import java.util.List;
//import org.hpccsystems.javaecl.Output;
import org.hpccsystems.javaecl.EclDirect;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.compatibility.Value;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.w3c.dom.Node;
import org.hpccsystems.javaecl.Column;
import java.io.*;
import org.hpccsystems.javaecl.ECLSoap;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.core.*;
import org.pentaho.di.core.gui.SpoonFactory;

import org.pentaho.di.plugins.perspectives.eclresults.*;

import org.hpccsystems.eclguifeatures.*;
import org.pentaho.di.job.JobMeta;
import org.hpccsystems.ecljobentrybase.*;
/**
 *
 * @author ChambersJ
 */
public class ECLExecute extends ECLJobEntry{//extends JobEntryBase implements Cloneable, JobEntryInterface {
    
    private String attributeName = "";
    private String fileName = "";
    private String serverAddress = "";
    private String serverPort = "";
    private String debugLevel = "";
    
    public static boolean isReady = false;
    boolean isValid = true;


    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public static boolean isIsReady() {
        return isReady;
    }

    public static void setIsReady(boolean isReady) {
        ECLExecute.isReady = isReady;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }    

    public String getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(String debugLevel) {
		this.debugLevel = debugLevel;
	}
	 
	 
	@Override
    public Result execute(Result prevResult, int k) throws KettleException {
		String error = "";
        
      //Result result = null;
        
        Result result = prevResult;
        if(result.isStopped()){
            logBasic("{Output Job is Stopped}");

           if(!ECLExecute.isReady){
             //  result.setResult(false);
           } 
        }else{
        
	
	        JobMeta jobMeta = super.parentJob.getJobMeta();
	                
	        String serverHost = "";
	        String serverPort = "";
	        String cluster = "";
	        String jobName = "";
	        String jobNameNoSpace ="";
	        String maxReturn = "";
	        String eclccInstallDir = "";
	        String mlPath = "";
	        String includeML = "";
	        String user = "";
	        String pass = "";
	        
	        AutoPopulate ap = new AutoPopulate();
	        try{
	        //Object[] jec = this.jobMeta.getJobCopies().toArray();
	
	            serverHost = ap.getGlobalVariable(jobMeta.getJobCopies(),"server_ip");
	            serverPort = ap.getGlobalVariable(jobMeta.getJobCopies(),"server_port");
	
	            cluster = ap.getGlobalVariable(jobMeta.getJobCopies(),"cluster");
	            jobName = ap.getGlobalVariable(jobMeta.getJobCopies(),"jobName");
	            jobNameNoSpace = jobName.replace(" ", "_"); 
	            maxReturn = ap.getGlobalVariable(jobMeta.getJobCopies(),"maxReturn");
	            eclccInstallDir = ap.getGlobalVariable(jobMeta.getJobCopies(),"eclccInstallDir");
	            mlPath = ap.getGlobalVariable(jobMeta.getJobCopies(),"mlPath");
	            includeML = ap.getGlobalVariable(jobMeta.getJobCopies(),"includeML");
	            user = ap.getGlobalVariable(jobMeta.getJobCopies(),"user_name");
                pass = ap.getGlobalVariableEncrypted(jobMeta.getJobCopies(),"password");
                
                //System.out.println("autopop:: User:" + user + " Pass: " + pass);
	
	        }catch (Exception e){
	            System.out.println("Error Parsing existing Global Variables ");
	            System.out.println(e.toString());
	            e.printStackTrace();
	
	        }
	        
	        //System.out.println("Output -- Finished setting up Global Variables");
	        this.setServerAddress(serverHost);
	        this.setServerPort(serverPort);
        
        
        
            ECLExecute.isReady=true;
            //logBasic("not waiting: " + ECLExecute.isReady);
            result.setResult(true);


            List<RowMetaAndData> list = result.getRows();
            String eclCode = "";
            if (list == null) {
                list = new ArrayList<RowMetaAndData>();
            } else {

                for (int i = 0; i < list.size(); i++) {
                    RowMetaAndData rowData = (RowMetaAndData) list.get(i);
                    String code = rowData.getString("ecl", null);
                    if (code != null) {
                        eclCode += code;
                    }
                }
                logBasic("{Execute Job} Execute Code =" + eclCode);
            }
          
            
            
            EclDirect eclDirect = new EclDirect(this.serverAddress, cluster, this.serverPort);
            eclDirect.setEclccInstallDir(eclccInstallDir);
            eclDirect.setIncludeML(includeML);
            eclDirect.setJobName(jobName);
            eclDirect.setMaxReturn(maxReturn);
            eclDirect.setMlPath(mlPath);
            eclDirect.setOutputName(this.getName());
            eclDirect.setUserName(user);
            eclDirect.setPassword(pass);
            //ArrayList dsList = null;
          
            //String outStr = "";
            //System.out.println("Output -- Finished setting up ECLDirect");
            try{
                String includes = "";
                includes += "IMPORT Std;\n";
                if(includeML.equalsIgnoreCase("true")){
                    includes += "IMPORT * FROM ML;\r\n\r\n";
                    includes += "IMPORT * FROM ML.Cluster;\r\n\r\n";
                    includes += "IMPORT * FROM ML.Types;\r\n\r\n";
                }
               // System.out.println("Execute -- Finished Imports");
                eclCode = includes + eclCode;
                
                boolean isValid = false;
                System.out.println("---------------- submitToCluster");
                isValid = eclDirect.execute(eclCode, this.debugLevel);
                System.out.println("---------------- finished submitToCluster");
                if(isValid){
                	//System.out.println("---------------- writing file");
                	isValid = eclDirect.writeResultsToFile(this.fileName);
                }
                if(!isValid){
                	result.setResult(false);
                    result.setLogText(eclDirect.getError());
                	logError(eclDirect.getError());
                	System.out.println(eclDirect.getError());
                	error += eclDirect.getError();
                }
                    
             }catch (Exception e){
                logError("Failed to execute code on Cluster." + e);
                result.setResult(false);
                error += "Exception occured please verify all settings.";
                e.printStackTrace();
            }
            
            result.clear();
            RowMetaAndData data = new RowMetaAndData();
            data.addValue("eclErrorCode", Value.VALUE_TYPE_STRING,eclCode+"\r\n");
            //list.add(data);
            
            //data = new RowMetaAndData();
            data.addValue("eclError", Value.VALUE_TYPE_STRING, error + "\r\n");
            list.add(data);
            result.setRows(list);
            //write the executed ecl code to file
            try {              
                System.getProperties().setProperty("eclCodeFile",fileName);
                
                BufferedWriter out = new BufferedWriter(new FileWriter(this.fileName + "\\eclcode.txt"));
                out.write(eclCode);
                out.close();
            } catch (IOException e) {
                 e.printStackTrace();
            }   
             
        
       }
        
        
       return result;
    }
	
    
    public void createOutputFile_old(ArrayList dsList,String fileName, int count){
         String outStr = "";
         String header = "";
         if(dsList != null){
         String newline = System.getProperty("line.separator");
         
                        for (int iList = 0; iList < dsList.size(); iList++) {
                            //logBasic("----------Outer-------------");
                            ArrayList rowList = (ArrayList) dsList.get(iList);

                            for (int jRow = 0; jRow < rowList.size(); jRow++) {
                                //logBasic("----------Row-------------");
                                ArrayList columnList = (ArrayList) rowList.get(jRow);
                                for (int lCol = 0; lCol < columnList.size(); lCol++) {
                                 //   logBasic("----------Column-------------");
                                    Column column = (Column) columnList.get(lCol);
                                    logBasic(column.getName() + "=" + column.getValue() + "|");
                                    outStr += column.getValue();
                                    if(lCol< (columnList.size()-1)){
                                        outStr += ",";
                                    }
                                    if(jRow == 0){
                                        header += column.getName();
                                        if(lCol< (columnList.size()-1)){
                                            header += ",";
                                        }else{
                                            header += newline;
                                        }
                                    }
                                }
                               // logBasic("newline");
                                outStr += newline;
                            }
                        }
             try {
                
                BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
                System.getProperties().getProperty("fileName");
                System.setProperty("fileName"+count, fileName);
                
                out.write(header+outStr);
                out.close();
           
            } catch (IOException e) {
               logError("Failed to write file: " + fileName);
               //error += "Failed to write ecl code file";
               //result.setResult(false);
                e.printStackTrace();
            }  
         }
    }

    @Override
    public void loadXML(Node node, List<DatabaseMeta> list, List<SlaveServer> list1, Repository rpstr) throws KettleXMLException {
        try {
             //System.out.println(" ------------ loadXML ------------- ");
            super.loadXML(node, list, list1);
            if(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "file_name")) != null)
                setFileName(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "file_name")));
            if(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "debugLevel")) != null)
                setDebugLevel(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "debugLevel")));

        } catch (Exception e) {
            e.printStackTrace();
            throw new KettleXMLException("ECL Output Job Plugin Unable to read step info from XML node", e);
        }

    }

    public String getXML() {
        String retval = "";
        // System.out.println(" ------------ getXML ------------- ");
        retval += super.getXML();
        retval += "		<file_name><![CDATA[" + fileName + "]]></file_name>" + Const.CR;
        retval += "		<debugLevel><![CDATA[" + this.debugLevel + "]]></debugLevel>" + Const.CR;
        return retval;

    }

    public void loadRep(Repository rep, ObjectId id_jobentry, List<DatabaseMeta> databases, List<SlaveServer> slaveServers)
            throws KettleException {
        //System.out.println(" ------------ loadRep " + id_jobentry + "------------- ");
        try {
           if(rep.getStepAttributeString(id_jobentry, "fileName") != null)
                fileName = rep.getStepAttributeString(id_jobentry, "fileName"); //$NON-NLS-1$
            if(rep.getStepAttributeString(id_jobentry, "debugLevel") != null)
                debugLevel = rep.getStepAttributeString(id_jobentry, "debugLevel"); //$NON-NLS-1$
        
        } catch (Exception e) {
            throw new KettleException("Unexpected Exception", e);
            
        }
    }

    public void saveRep(Repository rep, ObjectId id_job) throws KettleException {
       // System.out.println(" ------------ saveRep " + id_job + " ------------- ");
        try {
             
            ObjectId[] allIDs = rep.getPartitionSchemaIDs(true);
            for(int i = 0; i<allIDs.length; i++){
                logBasic("ObjectID["+i+"] = " + allIDs[i]);
            }
            rep.saveStepAttribute(id_job, getObjectId(), "fileName", fileName); //$NON-NLS-1$
            rep.saveStepAttribute(id_job, getObjectId(), "debugLevel", this.debugLevel); //$NON-NLS-1$
           
        } catch (Exception e) {
            throw new KettleException("Unable to save info into repository" + id_job, e);
        }
    }

    public boolean evaluates() {
    	return isValid;
    }

    public boolean isUnconditional() {
        return false;
    }
    
    
    
    
    
   
}
