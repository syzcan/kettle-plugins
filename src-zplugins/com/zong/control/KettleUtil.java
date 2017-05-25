package com.zong.control;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

public class KettleUtil {

	public static void executeJob(String kjbPath) throws KettleException {
		// 初始化
		KettleEnvironment.init();
		JobMeta jobMeta = new JobMeta(kjbPath, null);
		Job job = new Job(null, jobMeta);
		job.start();
		job.waitUntilFinished();
	}

	public static void executeTrans(String ktrPath) throws KettleException {
		KettleEnvironment.init();
		TransMeta transMeta = new TransMeta(ktrPath);
		Trans trans = new Trans(transMeta);
		trans.prepareExecution(null);
		trans.startThreads();// 执行该方法的时候，将会调用proccessRow()方法对每一行进行操作
		trans.waitUntilFinished();
	}

	public static void main(String[] args) {
		try {
			//KettleUtil.executeJob("d:/work-kettle/zjob.kjb");
			// KettleUtil.executeTrans("d:/work-kettle/ztable.ktr");
			 KettleUtil.executeTrans("d:/work-kettle/zuser.ktr");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
