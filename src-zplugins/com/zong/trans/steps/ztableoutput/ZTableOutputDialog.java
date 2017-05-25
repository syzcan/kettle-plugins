package com.zong.trans.steps.ztableoutput;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.database.dialog.DatabaseExplorerDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class ZTableOutputDialog extends BaseStepDialog implements StepDialogInterface {

	private CCombo dbCombo;
	private Text tableTx;
	private ZTableOutputMeta input;

	public ZTableOutputDialog(Shell parent, Object in, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) in, transMeta, stepname);
		input = (ZTableOutputMeta) in;
	}

	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, input);
		shell.setText("步骤");
		shell.setLayout(null);

		wlStepname = new Label(shell, SWT.NONE);
		wlStepname.setBounds(10, 10, 80, 25);
		wlStepname.setText("步骤名称");
		// 这个设置背景，不加则默认为灰色
		props.setLook(wlStepname);

		wStepname = new Text(shell, SWT.BORDER);
		wStepname.setBounds(90, 10, 200, 25);
		wStepname.setText(stepname);

		// 数据库下拉框标签、按钮定义
		Label dbLb = new Label(shell, SWT.NONE);
		dbLb.setBounds(10, 40, 80, 25);
		dbLb.setText("数据库连接");
		Button dbBte = new Button(shell, SWT.NONE);
		dbBte.setBounds(290, 40, 50, 25);
		dbBte.setText("编辑");
		Button dbBta = new Button(shell, SWT.NONE);
		dbBta.setBounds(340, 40, 50, 25);
		dbBta.setText("新建");
		// 基类方法创建数据库下拉框
		dbCombo = addConnectionLine(shell, wStepname, props.getMiddlePct(), Const.MARGIN, dbLb, dbBta, dbBte);
		dbCombo.setBounds(90, 40, 200, 25);

		Label sqlLb = new Label(shell, SWT.NONE);
		sqlLb.setBounds(10, 70, 80, 25);
		sqlLb.setText("输出表");
		props.setLook(sqlLb);
		tableTx = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		tableTx.setBounds(90, 70, 200, 25);

		Button sqlBt = new Button(shell, SWT.None);
		sqlBt.setBounds(290, 70, 50, 25);
		sqlBt.setText("选择");
		sqlBt.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (dbCombo.getText().equals("")) {
					MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
					mb.setText("提示");
					mb.setMessage("请选择数据库");
					mb.open();
					return;
				}
				DatabaseMeta inf = transMeta.findDatabase(dbCombo.getText());
				DatabaseExplorerDialog std = new DatabaseExplorerDialog(shell, SWT.NONE, inf, transMeta.getDatabases());
				if (std.open()) {
					tableTx.setText(std.getTableName());
				}
			}
		});

		wOK = new Button(shell, SWT.NONE);
		wOK.setBounds(100, 150, 50, 25);
		wOK.setText("确定");
		wOK.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				ok();
			}
		});

		wCancel = new Button(shell, SWT.NONE);
		wCancel.setBounds(160, 150, 50, 25);
		wCancel.setText("取消");
		wCancel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				cancel();
			}
		});

		// 读取配置
		getData();
		setSize();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return stepname;
	}

	private void ok() {
		if (Const.isEmpty(wStepname.getText()))
			return;
		stepname = wStepname.getText();
		// 保存设置
		getInfo();
		// 关闭
		dispose();
	}

	private void cancel() {
		// 关闭
		dispose();
	}

	/**
	 * 加载meta配置到界面
	 */
	private void getData() {
		if (input.getDbMeta() != null) {
			dbCombo.setText(input.getDbMeta().getName());
		}
		tableTx.setText(input.getTable() == null ? "" : input.getTable());
	}

	/**
	 * 界面数据写入meta配置
	 */
	private void getInfo() {
		input.setDbMeta(transMeta.findDatabase(dbCombo.getText()));
		input.setTable(tableTx.getText());
	}

}
