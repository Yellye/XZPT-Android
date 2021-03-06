package com.djylrz.xzpt.fragmentCompany;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;
import com.cleveroad.adaptivetablelayout.OnItemLongClickListener;
import com.djylrz.xzpt.R;
import com.djylrz.xzpt.adapter.SampleLinkedTableAdapter;
import com.djylrz.xzpt.datasource.CsvFileDataSourceImpl;
import com.djylrz.xzpt.datasource.UpdateFileCallback;
import com.djylrz.xzpt.ui.dialogs.AddColumnDialog;
import com.djylrz.xzpt.ui.dialogs.AddRowDialog;
import com.djylrz.xzpt.ui.dialogs.DeleteDialog;
import com.djylrz.xzpt.ui.dialogs.EditItemDialog;
import com.djylrz.xzpt.ui.dialogs.SettingsDialog;
import com.djylrz.xzpt.utils.PermissionHelper;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.djylrz.xzpt.datasource.Constants.ADD_COLUMN;
import static com.djylrz.xzpt.datasource.Constants.ADD_ROW;
import static com.djylrz.xzpt.datasource.Constants.DELETE_COLUMN;
import static com.djylrz.xzpt.datasource.Constants.DELETE_ROW;
import static com.djylrz.xzpt.datasource.Constants.EXTRA_BEFORE_OR_AFTER;
import static com.djylrz.xzpt.datasource.Constants.EXTRA_COLUMN_NUMBER;
import static com.djylrz.xzpt.datasource.Constants.EXTRA_ROW_NUMBER;
import static com.djylrz.xzpt.datasource.Constants.EXTRA_VALUE;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_ADD_COLUMN;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_ADD_COLUMN_CONFIRMED;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_ADD_ROW;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_ADD_ROW_CONFIRMED;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_DELETE_COLUMN;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_DELETE_COLUMN_CONFIRMED;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_DELETE_ROW;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_DELETE_ROW_CONFIRMED;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_EDIT_SONG;
import static com.djylrz.xzpt.datasource.Constants.REQUEST_CODE_SETTINGS;

/**
 * @Description: TableLayoutFragment
 * @Author: mingjun
 * @Date: 2019/5/20 下午 2:42
 */
public class TableLayoutFragment
        extends Fragment
        implements OnItemClickListener, OnItemLongClickListener, UpdateFileCallback {
    private static final String TAG = TableLayoutFragment.class.getSimpleName();
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1132;
    private static final String EXTRA_CSV_FILE = "EXTRA_CSV_FILE";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Uri mCsvFile;
    private LinkedAdaptiveTableAdapter mTableAdapter;
    private CsvFileDataSourceImpl mCsvFileDataSource;
    private AdaptiveTableLayout mTableLayout;
    private ProgressBar progressBar;
    private View vHandler;
    private Snackbar mSnackbar;

    public static TableLayoutFragment newInstance(@NonNull String filename) {
        Bundle args = new Bundle();
        args.putString(EXTRA_CSV_FILE, filename);
        TableLayoutFragment fragment = new TableLayoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCsvFile = Uri.parse(Objects.requireNonNull(getArguments()).getString(EXTRA_CSV_FILE));
        mCsvFileDataSource = new CsvFileDataSourceImpl(getContext(), mCsvFile);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab_layout, container, false);

        mTableLayout = view.findViewById(R.id.tableLayout);
        progressBar = view.findViewById(R.id.progressBar);
        vHandler = view.findViewById(R.id.vHandler);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.table_layout);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.actionSave) {

                    //检验数据合法性
                    boolean flag = checkData();
                    if (flag) {
                        //保存文件
                        applyChanges();
                        //上传文件
                        Log.d(TAG, "onMenuItemClick:文件地址为："  + mCsvFile.getPath());
                    } else {

                    }

                } else if (item.getItemId() == R.id.actionSettings) {
                    SettingsDialog.newInstance(
                            mTableLayout.isHeaderFixed(),
                            mTableLayout.isSolidRowHeader(),
                            mTableLayout.isRTL(),
                            mTableLayout.isDragAndDropEnabled())
                            .show(getChildFragmentManager(), SettingsDialog.class.getSimpleName());
                }
                return true;
            }
        });
        //            mTableLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        initAdapter();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSnackbar = Snackbar.make(view, R.string.changes_saved, Snackbar.LENGTH_INDEFINITE);
        TextView tv = mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(3);
        mSnackbar.setAction("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackbar.dismiss();
            }
        });
    }

    private void applyChanges() {
        if (PermissionHelper.checkOrRequest(
                Objects.requireNonNull(getActivity()),
                REQUEST_EXTERNAL_STORAGE,
                PERMISSIONS_STORAGE)) {
            showProgress();
            mCsvFileDataSource.applyChanges(
                    getLoaderManager(),
                    mTableLayout.getLinkedAdapterRowsModifications(),
                    mTableLayout.getLinkedAdapterColumnsModifications(),
                    mTableLayout.isSolidRowHeader(),
                    TableLayoutFragment.this);
        }
    }

    private void applyChanges(int actionChangeData, int position, boolean beforeOrAfter) {
        if (PermissionHelper.checkOrRequest(
                Objects.requireNonNull(getActivity()),
                REQUEST_EXTERNAL_STORAGE,
                PERMISSIONS_STORAGE)) {
            showProgress();
            mCsvFileDataSource.applyChanges(
                    getLoaderManager(),
                    mTableLayout.getLinkedAdapterRowsModifications(),
                    mTableLayout.getLinkedAdapterColumnsModifications(),
                    mTableLayout.isSolidRowHeader(),
                    actionChangeData,
                    position,
                    beforeOrAfter,
                    TableLayoutFragment.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            int columnIndex = data.getIntExtra(EXTRA_COLUMN_NUMBER, 0);
            int rowIndex = data.getIntExtra(EXTRA_ROW_NUMBER, 0);
            boolean beforeOrAfter = data.getBooleanExtra(EXTRA_BEFORE_OR_AFTER, true);
            if (requestCode == REQUEST_CODE_EDIT_SONG) {
                String value = data.getStringExtra(EXTRA_VALUE);
                mCsvFileDataSource.updateItem(rowIndex, columnIndex, value);
                mTableAdapter.notifyItemChanged(rowIndex, columnIndex);
            } else if (requestCode == REQUEST_CODE_SETTINGS) {
                applySettings(data);
            } else if (requestCode == REQUEST_CODE_DELETE_ROW) {
                showDeleteDialog(rowIndex, 0);
            } else if (requestCode == REQUEST_CODE_DELETE_ROW_CONFIRMED) {
                applyChanges(DELETE_ROW, rowIndex, false);
            } else if (requestCode == REQUEST_CODE_ADD_ROW) {
                showAddRowDialog(rowIndex);
            } else if (requestCode == REQUEST_CODE_ADD_ROW_CONFIRMED) {
                applyChanges(ADD_ROW, rowIndex, beforeOrAfter);
            } else if (requestCode == REQUEST_CODE_DELETE_COLUMN) {
                showDeleteDialog(0, columnIndex);
            } else if (requestCode == REQUEST_CODE_DELETE_COLUMN_CONFIRMED) {
                applyChanges(DELETE_COLUMN, columnIndex, false);
            } else if (requestCode == REQUEST_CODE_ADD_COLUMN) {
                showAddColumnDialog(columnIndex);
            } else if (requestCode == REQUEST_CODE_ADD_COLUMN_CONFIRMED) {
                applyChanges(ADD_COLUMN, columnIndex, beforeOrAfter);
            }
        }
    }

    //------------------------------------- adapter callbacks --------------------------------------
    @Override
    public void onItemClick(int row, int column) {
        EditItemDialog.newInstance(
                row,
                column,
                mCsvFileDataSource.getColumnHeaderData(column),
                mCsvFileDataSource.getItemData(row, column))
                .show(getChildFragmentManager(), EditItemDialog.class.getSimpleName());
    }

    @Override
    public void onRowHeaderClick(int row) {
        EditItemDialog.newInstance(
                row,
                0,
                mCsvFileDataSource.getColumnHeaderData(0),
                mCsvFileDataSource.getItemData(row, 0))
                .show(getChildFragmentManager(), EditItemDialog.class.getSimpleName());
    }

    @Override
    public void onColumnHeaderClick(int column) {
        EditItemDialog.newInstance(
                0,
                column,
                mCsvFileDataSource.getColumnHeaderData(column),
                mCsvFileDataSource.getItemData(0, column))
                .show(getChildFragmentManager(), EditItemDialog.class.getSimpleName());
    }

    @Override
    public void onLeftTopHeaderClick() {
        // implement in next version
    }

    @Override
    public void onItemLongClick(int row, int column) {
        // implement in next version
    }


    @Override
    public void onLeftTopHeaderLongClick() {
        // implement in next version
    }

    @Override
    public void onFileUpdated(final String filePath, boolean isSuccess) {
        hideProgress();
        View view = getView();
        if (view == null) {
            return;
        }

        if (isSuccess) { //if data source have been changed

            Log.e("Done", "File path = " + filePath);

            mCsvFile = Uri.parse(filePath);
            mCsvFileDataSource = new CsvFileDataSourceImpl(getContext(), mCsvFile);

            initAdapter();
            if (mSnackbar != null) {
                if (mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
                String text = getString(R.string.changes_saved) + " path = " + filePath;
                mSnackbar.setText(text);
                mSnackbar.show();
            }

        } else {
            Snackbar.make(view, R.string.unexpected_error, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    public void onFileUpdated(final String filePath) {
        hideProgress();
        View view = getView();
        if (view == null) {
            return;
        }
        mCsvFile = Uri.parse(filePath);
        mCsvFileDataSource = new CsvFileDataSourceImpl(getContext(), mCsvFile);
        initAdapter();
        mTableAdapter.notifyDataSetChanged();
    }

    private void initAdapter() {
        mTableAdapter = new SampleLinkedTableAdapter(getContext(), mCsvFileDataSource);
        mTableAdapter.setOnItemClickListener(this);
        mTableAdapter.setOnItemLongClickListener(this);
        mTableLayout.setDragAndDropEnabled(false);
        mTableLayout.setHeaderFixed(true);
        mTableLayout.setAdapter(mTableAdapter);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        vHandler.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        vHandler.setVisibility(View.GONE);
    }

    private void showDeleteDialog(int rowIndex, int columnIndex) {
        DeleteDialog.newInstance(rowIndex, columnIndex)
                .show(getChildFragmentManager(), DeleteDialog.class.getSimpleName());
    }

    private void showAddRowDialog(int rowIndex) {
        AddRowDialog.newInstance(rowIndex)
                .show(getChildFragmentManager(), AddRowDialog.class.getSimpleName());
    }

    private void showAddColumnDialog(int columnIndex) {
        AddColumnDialog.newInstance(columnIndex)
                .show(getChildFragmentManager(), AddRowDialog.class.getSimpleName());
    }

    private void applySettings(Intent data) {
        mTableLayout.setHeaderFixed(data.getBooleanExtra(SettingsDialog.EXTRA_VALUE_HEADER_FIXED, mTableLayout.isHeaderFixed()));
        mTableLayout.setSolidRowHeader(data.getBooleanExtra(SettingsDialog.EXTRA_VALUE_SOLID_HEADER, mTableLayout.isSolidRowHeader()));
        mTableLayout.setLayoutDirection(
                data.getBooleanExtra(SettingsDialog.EXTRA_VALUE_RTL_DIRECTION, mTableLayout.isRTL())
                        ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        mTableLayout.setDragAndDropEnabled(data.getBooleanExtra(
                SettingsDialog.EXTRA_VALUE_DRAG_AND_DROP_ENABLED, mTableLayout.isDragAndDropEnabled()));
        mTableAdapter.setRtl(mTableLayout.isRTL());
        mTableAdapter.notifyDataSetChanged();
    }

    /**
     * @Description: 检验数据合法性
     * @Param: []
     * @Return: void
     * @Author: mingjun
     * @Date: 2019/5/20 下午 3:27
     */
    private boolean checkData() {
        String pattern7 = "^\\d{3}$";//纯数字，非负数
        String pattern8 = "^[0-3]{1}$";//只能是0-3的一位数字
        String pattern9 = "^[0-9]*$";//纯数字
        int rowsCount = mCsvFileDataSource.getRowsCount();
        int columnsCount = mCsvFileDataSource.getColumnsCount();
        for (int i = 0; i < columnsCount; ++i) {
            for (int j = 1; j < rowsCount; ++j) {
                switch (i) {
                    //对工作制内容进行检测
                    case 7:
                        if (!Pattern.matches(pattern7, mCsvFileDataSource.getItemData(j, i))) {
                            //提示弹窗
                            new AlertDialog.Builder(getContext()).setTitle("检测到错误").setMessage(
                                    "[出错位置] : " + "第" + j + "行（" + mCsvFileDataSource.getItemData(j, 0) + "），第" + i + "列(" + mCsvFileDataSource.getItemData(0, i) + ")"
                                            + "\n[错误内容] : " + mCsvFileDataSource.getItemData(j, i)
                                            + "\n[错误类型] : " + "格式错误"
                                            + "\n[修改建议] : " + "\n1.工作制请填写形如'955,965,956,966,996'的3位数字")
                                    .setCancelable(false)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                            return false;
                        }
                        break;
                    //对岗位类型内容进行检测
                    case 8:
                        if (!Pattern.matches(pattern8, mCsvFileDataSource.getItemData(j, i))) {
                            //提示弹窗
                            new AlertDialog.Builder(getContext()).setTitle("检测到错误").setMessage(
                                    "[出错位置] : " + "第" + j + "行（" + mCsvFileDataSource.getItemData(j, 0) + "），第" + i + "列(" + mCsvFileDataSource.getItemData(0, i) + ")"
                                            + "\n[错误内容] : " + mCsvFileDataSource.getItemData(j, i)
                                            + "\n[错误类型] : " + "格式错误"
                                            + "\n[修改建议] : " + "岗位类型只能填写以下三个数字之一" +
                                            "\n数字1：实习" +
                                            "\n数字2：全职" +
                                            "\n数字3：兼职")
                                    .setCancelable(false)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                            return false;
                        }
                        break;
                    //对招聘人数内容进行检测
                    case 9:
                        if (!Pattern.matches(pattern9, mCsvFileDataSource.getItemData(j, i))) {
                            //提示弹窗
                            new AlertDialog.Builder(getContext()).setTitle("检测到错误").setMessage(
                                    "[出错位置] : " + "第" + j + "行（" + mCsvFileDataSource.getItemData(j, 0) + "），第" + i + "列(" + mCsvFileDataSource.getItemData(0, i) + ")"
                                            + "\n[错误内容] : " + mCsvFileDataSource.getItemData(j, i)
                                            + "\n[错误类型] : " + "格式错误"
                                            + "\n[修改建议] : " + "招聘人数请填写非负整数，0表示无限制")
                                    .setCancelable(false)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                            return false;
                        }
                        break;
                }
            }
        }
        //无错误
        return true;
    }
}