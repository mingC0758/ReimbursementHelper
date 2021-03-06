package com.reimbursementhelper.base;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.reimbursementhelper.data.ProjectDataHelper;
import com.reimbursementhelper.data.StaffDataHelper;

import java.io.File;

/**
 * 项目配置信息类，使用方法：BaseConfig.instance.xx()
 * @author mingC
 * @date 2018/2/13
 */
public class BaseConfig {
	//BaseConfig的实例
	public static BaseConfig instance;

	private Context mContext;
	//第一次打开
	private boolean firstOpen = false;
	//默认项目序号
	public int defaultProjectId = 1;
	//默认人员序号
	public int defaultStaffId = 1;
	//报销审批单输出目录
	public File itemOutputDir;
	//报账申请表输出目录
	public File budgetOutputDir;

	/**
	 * 构造方法
	 * @param context
	 */
	public BaseConfig(Context context) {
		mContext = context;
		itemOutputDir = new File(Environment.getExternalStorageDirectory(), "/报销助手/费用报销单输出");
		budgetOutputDir = new File(Environment.getExternalStorageDirectory(), "/报销助手/报账申请表输出");
		if (! itemOutputDir.exists() && itemOutputDir.mkdirs()) {
			Log.d("BaseConfig", itemOutputDir.getPath() + "文件夹创建成功");
		}
		if (! budgetOutputDir.exists() && budgetOutputDir.mkdirs()) {
			Log.d("BaseConfig", budgetOutputDir.getPath() + "文件夹创建成功");
		}
		defaultProjectId = context.getSharedPreferences("config", Context.MODE_PRIVATE).getInt("defaultProjectId",
				ProjectDataHelper.getMinProjectId());
		defaultStaffId = context.getSharedPreferences("config", Context.MODE_PRIVATE).getInt("defaultStaffId",
				StaffDataHelper.getMinStaffId());
	}

	/** 初始化配置,应在application中进行*/
	public static void initConfig(Context context) {
		instance = new BaseConfig(context);
	}
}
