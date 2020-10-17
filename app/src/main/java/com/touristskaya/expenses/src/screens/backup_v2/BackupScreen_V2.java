package com.touristskaya.expenses.src.screens.backup_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.touristskaya.expenses.R;
import com.touristskaya.expenses.src.screens.backup_v2.controllers.BackupScreenV2Controller;
import com.touristskaya.expenses.src.screens.backup_v2.models.BackupScreenV2Model;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;
import com.touristskaya.expenses.unused.activities.backup.AdapterActivityBackupDataRecyclerView;

/**
 * TODO: Add a class header comment
 */

public class BackupScreen_V2 extends AppCompatActivity {
    private static final String tag = "tag";

    private Button mCreateBackupDataButton;
    private Button mSignInButton;
    private TextView mStatusTextView;
    private ImageView mArrowBackImageView;
    private ImageView mSelectGoogleAccountImageView;
    private RecyclerView mBackupListRecyclerView;
    private AdapterActivityBackupDataRecyclerView mBackupDataRecyclerViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressDialog mProgressDialog;

    private BackupScreenV2Model mModel;
    private BackupScreenV2Controller mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_data);

        mModel = new BackupScreenV2Model(this);
        mController = new BackupScreenV2Controller(mModel);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mBackupListRecyclerView = (RecyclerView) findViewById(R.id.backup_data_recycler_view);

        mCreateBackupDataButton = (Button) findViewById(R.id.backup_data_backup_button);
        mCreateBackupDataButton.setEnabled(false);
        mCreateBackupDataButton.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
        mCreateBackupDataButton.setVisibility(View.GONE);
//        mCreateBackupDataButton.setOnClickListener((v) -> {});

        mSignInButton = (Button) findViewById(R.id.backup_data_sign_in_button);
        mSignInButton.setEnabled(true);
        mSignInButton.setVisibility(View.VISIBLE);
        mSignInButton.setOnClickListener((v) -> {
            mController.requestSignInHandler();
        });

        mArrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);
        mArrowBackImageView.setOnClickListener((v) -> mController.backButtonHandler());

        mSelectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);
//        mSelectGoogleAccountImageView.setOnClickListener((v) -> mController.selectGoogleAccountIconHandler());

        mStatusTextView = (TextView) findViewById(R.id.backup_data_status_textview);

        mProgressDialog = new ProgressDialog(BackupScreen_V2.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mController.hasNetworkConnectionHandler()) {
            Log.d("tag", "HAS_CONNECTION");
        } else {
            Log.d("tag", "NO_CONNECTION");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        mController.activityResultHandler(requestCode, resultCode, resultData);
    }
}
