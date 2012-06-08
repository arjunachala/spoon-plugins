package org.hpccsystems.pentaho.steps.ecloutput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hpccsystems.ecldirect.Dataset;
import org.hpccsystems.ecldirect.Output;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

public class ECLOutputStep extends BaseStep implements StepInterface {

    private ECLOutputStepData data;
    private ECLOutputStepMeta meta;

    public ECLOutputStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
        super(s, stepDataInterface, c, t, dis);
    }

    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
    	meta = (ECLOutputStepMeta) smi;
        data = (ECLOutputStepData) sdi;
<<<<<<< HEAD
        System.out.println("-------------------------------------");
    	System.out.println("Started ecloutputstep processrow");
        logBasic("Rendered Output ECL Code processRow: ");
        /*
        Object[] r = this.getRow(); 
        String input = "";
        if (r == null) 
        {
        	logBasic("r is null");
=======
        
        
        Object[] r = getRow(); 
        String input = "";
        if (r == null) 
        {
>>>>>>> e3817dab9afa0cee261ee0d81604e8c6f414dd6a
        } else {
            logBasic("Found Row = " + r[r.length-1]);
            input = r[r.length-1].toString() + "\r\n";
        }
<<<<<<< HEAD
         */
=======
        
>>>>>>> e3817dab9afa0cee261ee0d81604e8c6f414dd6a
        Object[] newRow = new Object[1]; 
        
        Output op = new Output();
        op.setAttributeName(meta.getAttributeName());
        op.setIsDef(meta.getIsDef());
        op.setOutputType(meta.getOutputType());
        op.setIncludeFormat(meta.getIncludeFormat());
        op.setInputType(meta.getInputType());
        op.setOutputFormat(meta.getOutputFormat());
        op.setExpression(meta.getExpression());
        op.setFile(meta.getFile());
        op.setTypeOptions(meta.getTypeOptions());
        op.setFileOptions(meta.getFileOptions());
        op.setNamed(meta.getNamed());
        op.setExtend(meta.getExtend());
        op.setReturnAll(meta.getReturnAll());
        op.setThor(meta.getThor());
        op.setCluster(meta.getCluster());
        op.setEncrypt(meta.getEncrypt());
        op.setCompressed(meta.getCompressed());
        op.setOverwrite(meta.getOverwrite());
        op.setExpire(meta.getExpire());
        op.setRepeat(meta.getRepeat());
        op.setPipeType(meta.getPipeType());
        op.setName(meta.getName());
<<<<<<< HEAD
        logBasic("done with direct");
		newRow[0] = op.ecl();
		//meta.setOutputField(op.ecl()); 
		putRow(data.outputRowMeta, newRow);
        data.output += op.ecl();
		logBasic("{Dataset Step} Output = ");
		//should change this to catch errors
		System.out.println("done processRow Outputstep");
		return false;
=======
        newRow[0] = input + op.ecl();
        
        putRow(data.outputRowMeta, newRow);
        
        logBasic("{Dataset Step} Output = " + newRow[0]);
        
        return false;
>>>>>>> e3817dab9afa0cee261ee0d81604e8c6f414dd6a
    }

    
    
<<<<<<< HEAD
    
    
    
    
    
    
    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        meta = (ECLOutputStepMeta) smi;
        data = (ECLOutputStepData) sdi;

=======
    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        meta = (ECLOutputStepMeta) smi;
        data = (ECLOutputStepData) sdi;
        super.setStepname(meta.getStepName());
        
>>>>>>> e3817dab9afa0cee261ee0d81604e8c6f414dd6a
        return super.init(smi, sdi);
    }

    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
        meta = (ECLOutputStepMeta) smi;
        data = (ECLOutputStepData) sdi;

        super.dispose(smi, sdi);
    }

<<<<<<< HEAD
    //
    // Run is were the action happens!
    /*
    public void run() {
    	System.out.println("-------------------------------------");
    	System.out.println("Started ecloutputstep");
        logBasic("Starting to run...");
        try {
            while (processRow(meta, data) && !isStopped()){
            	System.out.println("loop");
            }
            //boolean isValid = processRow(meta,data);
            logBasic("Rendered Output ECL Code: ");// + meta.getOutputField());
            //if(!isValid){
            //	setErrors(1);
            //	stopAll();
            //}
        } catch (Exception e) {
            logError("Unexpected error : " + e.toString());
            logError(Const.getStackTracker(e));
            setErrors(1);
            stopAll();
        } finally {
            dispose(meta, data);
            logBasic("Finished, processing " + getLinesRead() + " rows");
            markStop();
        }
    }
    */
=======
   
>>>>>>> e3817dab9afa0cee261ee0d81604e8c6f414dd6a
}
