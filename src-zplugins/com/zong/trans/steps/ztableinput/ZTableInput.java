package com.zong.trans.steps.ztableinput;

import java.sql.ResultSet;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class ZTableInput extends BaseStep implements StepInterface {

	private ZTableInputMeta meta;
	private ZTableInputData data;

	public ZTableInput(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		if (first) {
			first = false;
			boolean success = doQuery();
			if (!success) {
				return false;
			}
		} else {
			if (data.thisrow != null) {
				data.nextrow = data.db.getRow(data.rs, false);
				if (data.nextrow != null)
					incrementLinesInput();
			}
		}

		if (data.thisrow == null) {
			setOutputDone();
			return false;
		} else {
			putRow(data.rowMeta, data.thisrow);
			data.thisrow = data.nextrow;
			if (checkFeedback(getLinesInput())) {
				if (log.isBasic())
					logBasic("linenr " + getLinesInput());
			}
		}
		return true;
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (ZTableInputMeta) smi;
		data = (ZTableInputData) sdi;
		if (super.init(smi, sdi)) {
			data.infoStream = meta.getStepIOMeta().getInfoStreams().get(0);
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

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		try {
			if (data.db != null) {
				data.db.closeQuery(data.rs);
			}
		} catch (KettleException e) {
			logError("Unexpected error closing query : " + e.toString());
			setErrors(1);
			stopAll();
		} finally {
			if (data.db != null) {
				data.db.disconnect();
			}
		}

		super.dispose(smi, sdi);
	}

	private boolean doQuery() throws KettleDatabaseException {
		boolean success = true;
		String sql = "select * from "+meta.getTable();
		data.rs = data.db.openQuery(sql, null, null, ResultSet.FETCH_FORWARD, false);
		if (data.rs == null) {
			logError("Couldn't open Query [" + sql + "]");
			setErrors(1);
			stopAll();
			success = false;
		} else {
			data.rowMeta = data.db.getReturnRowMeta();
			if (data.rowMeta != null) {
				for (ValueMetaInterface valueMeta : data.rowMeta.getValueMetaList()) {
					valueMeta.setOrigin(getStepname());
				}
			}
			data.thisrow = data.db.getRow(data.rs);
			if (data.thisrow != null) {
				incrementLinesInput();
				data.nextrow = data.db.getRow(data.rs);
				if (data.nextrow != null)
					incrementLinesInput();
			}
		}
		return success;
	}
}
