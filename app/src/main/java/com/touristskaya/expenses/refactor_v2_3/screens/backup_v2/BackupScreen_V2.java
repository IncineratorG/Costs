package com.touristskaya.expenses.refactor_v2_3.screens.backup_v2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.touristskaya.expenses.R;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.controllers.BackupScreenV2Controller;
import com.touristskaya.expenses.refactor_v2_3.screens.backup_v2.models.BackupScreenV2Model;
import com.touristskaya.expenses.refactor_v2_3.utils.common.system_events.SystemEventsHandler;
import com.touristskaya.expenses.refactor_v1_active.activities.backup.AdapterActivityBackupDataRecyclerView;
import com.touristskaya.expenses.refactor_v1_active.activities.backup.DataUnitBackupFolder;

import java.util.ArrayList;
import java.util.List;

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
        mSignInButton.setVisibility(View.GONE);
        mSignInButton.setOnClickListener((v) -> {
            mController.signInHandler();
        });

        mArrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);
        mArrowBackImageView.setOnClickListener((v) -> mController.backButtonHandler());

        mSelectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);
        mSelectGoogleAccountImageView.setOnClickListener((v) -> mController.signOutHandler());

        mStatusTextView = (TextView) findViewById(R.id.backup_data_status_textview);

        mProgressDialog = new ProgressDialog(BackupScreen_V2.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Cancel_string),
                (d, w) -> SystemEventsHandler.onInfo("PROGRESS_DIALOG->CANCEL_BUTTON"));

        // ===
        mModel.signedIn.onChange((signedIn) -> {
           if (signedIn) {
               SystemEventsHandler.onInfo("SIGNED_IN");
               mController.getBackupDataHandler();
           } else {
               SystemEventsHandler.onInfo("NO_SIGNED_IN");
               mController.signInHandler();
           }
        });

        mModel.state.gettingBackupData.onChange((gettingBackupData) -> {
            if (gettingBackupData) {
                mProgressDialog.setTitle("");
                mProgressDialog.setMessage("Получение резервных копий");
                if (!isFinishing()) {
                    mProgressDialog.show();
                }
            } else {
                mProgressDialog.dismiss();
            }
        });

        mModel.state.backupFilesList.onChange((backupFilesList) -> {
            List<DataUnitBackupFolder> backupFiles = backupFilesList;
            if (backupFiles == null) {
                backupFiles = new ArrayList<>();
            }

            mBackupListRecyclerView.setLayoutManager(mLinearLayoutManager);
            mBackupDataRecyclerViewAdapter = new AdapterActivityBackupDataRecyclerView(BackupScreen_V2.this, backupFiles);
            mBackupListRecyclerView.setAdapter(mBackupDataRecyclerViewAdapter);
        });

//        mModel.state.select(new PropsSelector(
//                Arrays.asList(mModel.state.acquiringDriveService, mModel.state.googleDriveService),
//                () -> {
//                    boolean acquiringDriveService = mModel.state.acquiringDriveService.value();
//                    Drive driveService = mModel.state.googleDriveService.value();
//
//                    if (acquiringDriveService) {
//                        SystemEventsHandler.onInfo("ACQUIRING_DRIVE_SERVICE");
//                    } else if (driveService != null) {
//                        SystemEventsHandler.onInfo("DRIVE_SERVICE_ACQUIRED");
//                    } else {
//                        if (mController.hasNetworkConnectionHandler()) {
//                            mController.signInHandler();
//                        } else {
//                            SystemEventsHandler.onInfo("NO_CONNECTION");
//                        }
//                    }
//                }
//        ));
        // ===
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mController.hasNetworkConnectionHandler()) {
//            Log.d("tag", "HAS_CONNECTION");


        } else {
//            Log.d("tag", "NO_CONNECTION");
        }

//        if (mController.hasNetworkConnectionHandler()) {
//            Log.d("tag", "HAS_CONNECTION");
////            mController.requestSignInHandler();
//        } else {
//            Log.d("tag", "NO_CONNECTION");
//        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        mController.activityResultHandler(requestCode, resultCode, resultData);
    }
}
