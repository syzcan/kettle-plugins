package com.zong.trans.steps.ztableoutput;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

public class ZTableOutputMeta extends BaseStepMeta implements StepMetaInterface {
	private DatabaseMeta dbMeta;
	private String table;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public DatabaseMeta getDbMeta() {
		return dbMeta;
	}

	public void setDbMeta(DatabaseMeta dbMeta) {
		this.dbMeta = dbMeta;
	}

	@Override
	public void setDefault() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getXML() throws KettleXMLException {
		StringBuffer retval = new StringBuffer();
		retval.append("    " + XMLHandler.addTagValue("db", dbMeta == null ? "" : dbMeta.getName()));
		retval.append("    " + XMLHandler.addTagValue("table", table));
		return retval.toString();
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleXMLException {
		try {
			dbMeta = DatabaseMeta.findDatabase(databases, XMLHandler.getTagValue(stepnode, "db"));
			table = XMLHandler.getTagValue(stepnode, "table");
		} catch (Exception e) {
			throw new KettleXMLException("加载XML步骤失败", e);
		}
	}

	@Override
	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {
		// TODO Auto-generated method stub

	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new ZTableOutput(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new ZTableOutputData();
	}

}
