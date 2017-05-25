package com.zong.trans.steps.ztableoutput;

import java.sql.PreparedStatement;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class ZTableOutput extends BaseStep implements StepInterface {

	private ZTableOutputMeta meta;
	private ZTableOutputData data;

	public ZTableOutput(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		// 获取上一个步骤一行数据
		Object[] r = getRow();
		if (r == null) {
			return false;
		}
		if (first) {
			// 获取输入行数据结构
			data.insertRowMeta = getInputRowMeta();
		}
		// 写入目标数据表
		try {
			writeToTable(getInputRowMeta(), r);
			System.out.println(" 写入：" + r);
		} catch (KettleException e) {
			logError("Because of an error, this step can't continue: ", e);
			setErrors(1);
			stopAll();
			setOutputDone();
			return false;
		}
		return true;
	}

	private void writeToTable(RowMetaInterface rowMeta, Object[] r) throws KettleException {
		// 拼接插入sql
		String sql = "insert into " + meta.getTable() + "(";
		for (String field : rowMeta.getFieldNames()) {
			sql += field + ",";
		}
		sql = sql.replaceAll(",$", "") + ") values(";
		for (int i = 0; i < rowMeta.getFieldNames().length; i++) {
			sql += "?,";
		}
		sql = sql.replaceAll(",$", "") + ")";
		System.out.println(sql);
		PreparedStatement insertStatement = data.db.prepareSQL(sql, false);
		data.db.setValues(data.insertRowMeta, r, insertStatement);
		data.db.insertRow(insertStatement, false, false);
		data.db.commit();
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (ZTableOutputMeta) smi;
		data = (ZTableOutputData) sdi;

		if (super.init(smi, sdi)) {
			data.db = new Database(this, meta.getDbMeta());
			data.db.shareVariablesWith(this);
			try {
				if (getTransMeta().isUsingUniqueConnections()) {
					synchronized (getTrans()) {
						data.db.connect(getTrans().getThreadName(), getPartitionID());
					}
				} else {
					data.db.connect(getPartitionID());
				}

				if (meta.getDbMeta().isRequiringTransactionsOnQueries()) {
					data.db.setCommit(100);
				}
				return true;
			} catch (KettleException e) {
				logError("An error occurred, processing will be stopped: " + e.getMessage());
				setErrors(1);
				stopAll();
			}
		}
		return false;
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		if (data.db != null) {
			data.db.disconnect();
		}
		super.dispose(smi, sdi);
	}
}
