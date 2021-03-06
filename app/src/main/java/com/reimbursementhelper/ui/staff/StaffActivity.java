package com.reimbursementhelper.ui.staff;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.reimbursementhelper.R;
import com.reimbursementhelper.base.BaseActivity;
import com.reimbursementhelper.base.BaseConfig;
import com.reimbursementhelper.bean.Staff;
import com.reimbursementhelper.data.StaffDataHelper;

import java.util.List;

import butterknife.BindView;

/**
 * 人员活动
 */
public class StaffActivity extends BaseActivity {

	@BindView(R.id.lv_staff)
	ListView lvStaff;
	@BindView(R.id.btn_staff_add)
	Button btnStaffAdd;
	@BindView(R.id.layout_staff)
	LinearLayout layoutStaff;

	StaffEditFragment staffEditFragment;

	List<Staff> staffList;
	MyStaffAdapter adapter;

	boolean isEditFragmentShowing = false;
	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		staffEditFragment = new StaffEditFragment();
		staffList = StaffDataHelper.getStaffList();
		for (Staff staff : staffList) {
			Log.d("StaffActivity", "staff:" + staff);
		}
		adapter = new MyStaffAdapter(this, staffList);
		lvStaff.setAdapter(adapter);
		lvStaff.setDividerHeight(0);
		adapter.setOnEditClickListener(new MyStaffAdapter.OnClickListener() {
			@Override
			public void onClick(Staff staff) {
				//设置编辑人员
				staffEditFragment.setEditStaff(staff);
				//如果正在显示则更新
				if (isEditFragmentShowing) {
					staffEditFragment.refreshEditTexts();
				}
				//显示编辑Fragment
				addEditFragment();
				//隐藏按钮
				btnStaffAdd.setVisibility(View.GONE);
			}
		});
		adapter.setOnDelClickListener(new MyStaffAdapter.OnClickListener() {
			@Override
			public void onClick(Staff staff) {
				//判断是否最后一个人员，如果是则不能删除！
				if (StaffDataHelper.getStaffsCount() == 1) {
					Toast.makeText(StaffActivity.this, "至少需要有一个人员处于默认状态，删除失败！",
							Toast.LENGTH_SHORT).show();
				} else {
					staff.delete();
					Toast.makeText(StaffActivity.this, staff.getName() + " 删除成功！",
							Toast.LENGTH_SHORT).show();
					//如果删除的人员处于默认状态则传递给序号最小的未删除人员
					if (BaseConfig.instance.defaultStaffId == staff.getId()) {
						int minId = StaffDataHelper.getMinStaffId();
						BaseConfig.instance.defaultStaffId = minId;
						//修改当前人员
						getGlobal().curStaff = StaffDataHelper.getStaffById(minId);
					}
				}
				refresh();
			}
		});
		lvStaff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("StaffActivity", "点击staffList.get(position):" + staffList.get(position));
			}
		});
		Toast.makeText(this, "长按人员可设为默认状态", Toast.LENGTH_SHORT).show();
		lvStaff.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
			                               long id) {
				new AlertDialog.Builder(StaffActivity.this).setMessage(
						"是否设为默认人员？").setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int id = staffList.get(position).getId();
						//修改默认序号
						BaseConfig.instance.defaultStaffId = id;
						//修改当前人员
						getGlobal().curStaff = StaffDataHelper.getStaffById(id);
						//保存到sp中
						SharedPreferences.Editor editor = getSharedPreferences("config",
								Context.MODE_PRIVATE).edit();
						editor.putInt("defaultStaffId", id);
						editor.apply();
						adapter.notifyDataSetInvalidated();
					}
				}).setNeutralButton("取消", null).show();
				return false;
			}
		});
		btnStaffAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				staffEditFragment.setEditStaff(null);
				//如果正在显示则更新
				if (isEditFragmentShowing) {
					staffEditFragment.refreshEditTexts();
				}
				//点击添加时显示fragment
				addEditFragment();
				Log.d("momingqi", "onClick: 执行原活动剩余代码");
				//隐藏按钮
				btnStaffAdd.setVisibility(View.GONE);
			}
		});

	}

	/**
	 * 显示正在编辑的Fragment，若正在显示则不做处理
	 */
	private void addEditFragment() {
		if (!isEditFragmentShowing) {
			//点击编辑时显示fragment
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.layout_staff, staffEditFragment);
			transaction.commit();
			isEditFragmentShowing = true;
		}
	}

	/**
	 * 隐藏编辑Fragment
	 */
	public void removeEditFragment() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.remove(staffEditFragment);
		transaction.commit();
		isEditFragmentShowing = false;
	}

	@Override
	public int getContentView() {
		return R.layout.activity_staff;
	}

	@Override
	public void initView() {

	}

	@Override
	public void initListener() {

	}

	@Override
	public void initData() {

	}

	public void refresh() {
		//重新抓取人员数据
		staffList.clear();
		List<Staff> staffListNew = StaffDataHelper.getStaffList();
		Log.d("StaffActivity", "staffListNew:" + staffListNew);
		staffList.addAll(staffListNew);
		adapter.notifyDataSetChanged();
		//重置按钮状态
		btnStaffAdd.setVisibility(View.VISIBLE);
		//隐藏编辑fragment
		removeEditFragment();
	}

	@Override
	public void initToolbar() {
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.onBackPressed();
		}
		return true;
	}
}
