package com.zong.trans.steps.ztableinput;

import java.sql.ResultSet;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;

public class ZTableInputData extends BaseStepData implements StepDataInterface {
	public Object[] nextrow;
	public Object[] thisrow;
	public Database db;
	public ResultSet rs;
	public RowMetaInterface rowMeta;
	public StreamInterface infoStream;
}
