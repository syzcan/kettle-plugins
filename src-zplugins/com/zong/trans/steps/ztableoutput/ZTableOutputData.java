package com.zong.trans.steps.ztableoutput;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class ZTableOutputData extends BaseStepData implements StepDataInterface {
	public Database db;
	public RowMetaInterface insertRowMeta;
}
