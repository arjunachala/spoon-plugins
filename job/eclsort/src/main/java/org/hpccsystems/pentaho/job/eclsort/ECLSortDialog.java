/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpccsystems.pentaho.job.eclsort;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import org.hpccsystems.eclguifeatures.*;
import org.hpccsystems.ecljobentrybase.*;

/**
 *
 * @author ChambersJ
 */
public class ECLSortDialog extends ECLJobEntryDialog{//extends JobEntryDialog implements JobEntryDialogInterface {

    private ECLSort jobEntry;
    private Text jobEntryName;
    
   
    private Text recordsetName;
    private Combo datasetName;
    private Text fields;//Comma separated list of fieldNames. a "-" prefix to the field name will indicate descending order
    
    
    
    private Button wOK, wCancel;
    private boolean backupChanged;
    private SelectionAdapter lsDef;

    public ECLSortDialog(Shell parent, JobEntryInterface jobEntryInt, Repository rep, JobMeta jobMeta) {
        super(parent, jobEntryInt, rep, jobMeta);
        jobEntry = (ECLSort) jobEntryInt;
        if (this.jobEntry.getName() == null) {
            this.jobEntry.setName("Sort");
        }
    }

    public JobEntryInterface open() {
        Shell parentShell = getParent();
        Display display = parentShell.getDisplay();

        shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);

        props.setLook(shell);
        JobDialog.setShellImage(shell, jobEntry);
        
        String datasets[] = null;
        AutoPopulate ap = new AutoPopulate();
        try{
            //Object[] jec = this.jobMeta.getJobCopies().toArray();
            
            datasets = ap.parseDatasetsRecordsets(this.jobMeta.getJobCopies());
        }catch (Exception e){
            System.out.println("Error Parsing existing Datasets");
            System.out.println(e.toString());
            datasets = new String[]{""};
        }

        ModifyListener lsMod = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                jobEntry.setChanged();
            }
        };

        backupChanged = jobEntry.hasChanged();

        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;


        shell.setLayout(formLayout);
        shell.setText("Sort");

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        shell.setLayout(formLayout);
        shell.setText("Define an ECL Sort");

        FormLayout groupLayout = new FormLayout();
        groupLayout.marginWidth = 10;
        groupLayout.marginHeight = 10;

        // Stepname line
        Group generalGroup = new Group(shell, SWT.SHADOW_NONE);
        props.setLook(generalGroup);
        generalGroup.setText("General Details");
        generalGroup.setLayout(groupLayout);
        FormData generalGroupFormat = new FormData();
        generalGroupFormat.top = new FormAttachment(0, margin);
        generalGroupFormat.width = 400;
        generalGroupFormat.height = 65;
        generalGroupFormat.left = new FormAttachment(middle, 0);
        generalGroup.setLayoutData(generalGroupFormat);
        
        jobEntryName = buildText("Job Entry Name", null, lsMod, middle, margin, generalGroup);

        //All other contols
        //Sort Declaration
        Group datasetGroup = new Group(shell, SWT.SHADOW_NONE);
        props.setLook(datasetGroup);
        datasetGroup.setText("Sort Details");
        datasetGroup.setLayout(groupLayout);
        FormData datasetGroupFormat = new FormData();
        datasetGroupFormat.top = new FormAttachment(generalGroup, margin);
        datasetGroupFormat.width = 400;
        datasetGroupFormat.height = 130;
        datasetGroupFormat.left = new FormAttachment(middle, 0);
        datasetGroup.setLayoutData(datasetGroupFormat);
        
        
        recordsetName = buildText("Resulting Recordset Name", null, lsMod, middle, margin, datasetGroup);
        
        datasetName = buildCombo("Dataset to be Sorted", recordsetName, lsMod, middle, margin, datasetGroup, datasets);
        fields = buildText("Fields", datasetName, lsMod, middle, margin, datasetGroup);
        Label lb = buildLabel("comma separated, prefix field with - for descending order", fields, lsMod, middle, margin, datasetGroup);

        
       
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText("OK");
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText("Cancel");

        BaseStepDialog.positionBottomButtons(shell, new Button[]{wOK, wCancel}, margin, datasetGroup);

        // Add listeners
        Listener cancelListener = new Listener() {

            public void handleEvent(Event e) {
                cancel();
            }
        };
        Listener okListener = new Listener() {

            public void handleEvent(Event e) {
                ok();
            }
        };

        wCancel.addListener(SWT.Selection, cancelListener);
        wOK.addListener(SWT.Selection, okListener);

        lsDef = new SelectionAdapter() {

            public void widgetDefaultSelected(SelectionEvent e) {
                ok();
            }
        };


        // Detect X or ALT-F4 or something that kills this window...

        shell.addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent e) {
                cancel();
            }
        });

        if (jobEntry.getName() != null) {
            jobEntryName.setText(jobEntry.getName());
        }
        if (jobEntry.getFields() != null) {
            fields.setText(jobEntry.getFields());
        }
        if (jobEntry.getDatasetName() != null) {
            datasetName.setText(jobEntry.getDatasetName());
        }
        if (jobEntry.getRecordsetName() != null) {
            recordsetName.setText(jobEntry.getRecordsetName());
        }


        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        return jobEntry;

    }
    
    
    private boolean validate(){
    	boolean isValid = true;
    	String errors = "";
    	
    	//only need to require a entry name
    	if(this.jobEntryName.getText().equals("")){
    		//one is required.
    		isValid = false;
    		errors += "You must provide a \"Job Entry Name\"!\r\n";
    	}
    	
    	if(this.recordsetName.getText().equals("")){
    		//one is required.
    		isValid = false;
    		errors += "You must provide a \"Resulting Recordset Name\"!\r\n";
    	}
    	
    	if(this.datasetName.getText().equals("")){
    		//one is required.
    		isValid = false;
    		errors += "You must provide a \"Dataset to be Sorted\"!\r\n";
    	}
    	
    	if(this.fields.getText().equals("")){
    		//one is required.
    		isValid = false;
    		errors += "You must provide a \"Fields\"!\r\n";
    	}


		if(!isValid){
			ErrorNotices en = new ErrorNotices();
			errors += "\r\n";
			errors += "If you continue to save with errors you may encounter compile errors if you try to execute the job.\r\n\r\n";
			isValid = en.openValidateDialog(getParent(),errors);
		}
		return isValid;
		
	}
    
    private void ok() {
    	if(!validate()){
    		return;
    	}
        jobEntry.setName(jobEntryName.getText());
        jobEntry.setFields(fields.getText());
        jobEntry.setDatasetName(datasetName.getText());
        jobEntry.setRecordsetName(recordsetName.getText());
        
        dispose();
    }

    private void cancel() {
        jobEntry.setChanged(backupChanged);
        jobEntry = null;
        dispose();
    }

   
}
