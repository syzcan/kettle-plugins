package com.zong.trans.steps.ztableinput;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepIOMeta;
import org.pentaho.di.trans.step.StepIOMetaInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.Stream;
import org.pentaho.di.trans.step.errorhandling.StreamIcon;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface.StreamType;
import org.w3c.dom.Node;

public class ZTableInputMeta extends BaseStepMeta implements StepMetaInterface {

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
		return new ZTableInput(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new ZTableInputData();
	}

	/**
	 * 告诉下一个步骤输出的数据类型等
	 */
	public void getFields(RowMetaInterface row, String origin, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {
		if (dbMeta == null)
			return;
		boolean param = false;

		Database db = new Database(loggingObject, dbMeta);
		databases = new Database[] { db };
		String sNewSQL = "select * from " + table;
		RowMetaInterface add = null;
		try {
			add = db.getQueryFields(sNewSQL, param);
		} catch (KettleDatabaseException dbe) {
			throw new KettleStepException("Unable to get queryfields for SQL: " + Const.CR + sNewSQL, dbe);
		}

		if (add != null) {
			for (int i = 0; i < add.size(); i++) {
				ValueMetaInterface v = add.getValueMeta(i);
				v.setOrigin(origin);
			}
			row.addRowMeta(add);
		} else {
			try {
				db.connect();

				RowMetaInterface paramRowMeta = null;
				Object[] paramData = null;

				StreamInterface infoStream = getStepIOMeta().getInfoStreams().get(0);
				if (!Const.isEmpty(infoStream.getStepname())) {
					param = true;
					if (info.length >= 0 && info[0] != null) {
						paramRowMeta = info[0];
						paramData = RowDataUtil.allocateRowData(paramRowMeta.size());
					}
				}

				add = db.getQueryFields(sNewSQL, param, paramRowMeta, paramData);

				if (add == null)
					return;
				for (int i = 0; i < add.size(); i++) {
					ValueMetaInterface v = add.getValueMeta(i);
					v.setOrigin(origin);
				}
				row.addRowMeta(add);
			} catch (KettleException ke) {
				throw new KettleStepException("Unable to get queryfields for SQL: " + Const.CR + sNewSQL, ke);
			} finally {
				db.disconnect();
			}
		}
	}

	public StepIOMetaInterface getStepIOMeta() {
		if (ioMeta == null) {

			ioMeta = new StepIOMeta(true, true, false, false, false, false);

			StreamInterface stream = new Stream(StreamType.INFO, null, "这些行作为参数", StreamIcon.INFO, null);
			ioMeta.addStream(stream);
		}

		return ioMeta;
	}

}
