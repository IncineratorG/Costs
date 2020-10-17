package com.touristskaya.expenses.src.screens.backup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.drive.Drive;
import com.touristskaya.expenses.R;
import com.touristskaya.expenses.src.libs.selector.Selector;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenState;
import com.touristskaya.expenses.unused.activities.backup.AdapterActivityBackupDataRecyclerView;
import com.touristskaya.expenses.src.screens.backup.controllers.BackupScreenController;
import com.touristskaya.expenses.src.screens.backup.models.BackupScreenModel;

/**
 * TODO: Add a class header comment
 */

public class BackupScreen extends AppCompatActivity {
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

    private BackupScreenModel mModel;
    private BackupScreenController mController;
    private BackupScreenState mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_data);

        mModel = new BackupScreenModel(this);
        mController = new BackupScreenController(mModel);
        mState = (BackupScreenState) mModel.getState();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mBackupListRecyclerView = (RecyclerView) findViewById(R.id.backup_data_recycler_view);

        mCreateBackupDataButton = (Button) findViewById(R.id.backup_data_backup_button);
        mCreateBackupDataButton.setEnabled(false);
        mCreateBackupDataButton.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
        mCreateBackupDataButton.setVisibility(View.GONE);
        mCreateBackupDataButton.setOnClickListener((v) -> mController.createBackupButtonHandler());

        mSignInButton = (Button) findViewById(R.id.backup_data_sign_in_button);
        mSignInButton.setEnabled(true);
        mSignInButton.setVisibility(View.VISIBLE);
        mSignInButton.setOnClickListener((v) -> mController.signInButtonHandler());
        // ===
//        mModel.testProp.onChange((val) -> {
//            mSignInButton.setText(val);
//        });
        // ===

        mArrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);
        mArrowBackImageView.setOnClickListener((v) -> mController.backButtonHandler());

        mSelectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);
        mSelectGoogleAccountImageView.setOnClickListener((v) -> mController.selectGoogleAccountIconHandler());

        mStatusTextView = (TextView) findViewById(R.id.backup_data_status_textview);

        mProgressDialog = new ProgressDialog(BackupScreen.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mState.select(new Selector((selector) -> {
            Boolean prevHasNetworkConnection = (Boolean) selector.getPrevValue("hasNetworkConnection");
            boolean currHasNetworkConnection = mState.hasNetworkConnection;

            if (prevHasNetworkConnection == null || currHasNetworkConnection != prevHasNetworkConnection) {
                if (currHasNetworkConnection) {
                    mStatusTextView.setText("Вход в аккаунт Google");
                    mSignInButton.setEnabled(true);
                    mSignInButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));
                } else {
                    mStatusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));
                    mSignInButton.setEnabled(false);
                    mSignInButton.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
                }
            }

            selector.setPrevValue("hasNetworkConnection", currHasNetworkConnection);
        }));

        mState.select(new Selector(selector -> {
            Drive prevDriveService = (Drive) selector.getPrevValue("driveService");
            Boolean prevDriveServiceBuilding = (Boolean) selector.getPrevValue("driveServiceBuilding");
            Boolean prevDriveServiceBuildingHasError = (Boolean) selector.getPrevValue("driveServiceBuildingHasError");
            String prevDriveServiceBuildingErrorDescription = (String) selector.getPrevValue("driveServiceBuildingErrorDescription");

            Drive currDriveService = mState.driveService;
            boolean currDriveServiceBuilding = mState.driveServiceBuilding;
            boolean currDriveServiceBuildingHasError = mState.driveServiceBuildingHasError;
            String currDriveServiceBuildingErrorDescription = mState.driveServiceBuildingErrorDescription;

            if (prevDriveServiceBuilding == null) {

            }

            if (!currDriveServiceBuilding && currDriveService != null) {
//                SystemEventsHandler.onInfo("NEED_FETCH_DATA");
            }

            if (prevDriveService == null || !prevDriveService.equals(currDriveService)) {
//                SystemEventsHandler.onInfo("DriveService->" + (currDriveService != null));
//                mState.driveService = currDriveService;
            }
//            if (prevDriveServiceBuilding == null || prevDriveServiceBuilding != currDriveServiceBuilding) {
//                if (currDriveServiceBuilding) {
//                    SystemEventsHandler.onInfo("HERE_HERE");
//
//                    mProgressDialog.setTitle(null);
//                    mProgressDialog.setMessage("Подключение к серверам Google");
//                    if (!isFinishing()) {
//                        mProgressDialog.show();
//                    }
//                } else {
//                    mProgressDialog.dismiss();
//                }
//            }

//            if (prevDriveServiceBuilding != null && prevDriveServiceBuilding != currDriveServiceBuilding) {
//                SystemEventsHandler.onInfo("H_HERE");
//            }

            selector.setPrevValue("driveService", currDriveService);
            selector.setPrevValue("driveServiceBuilding", currDriveServiceBuilding);
            selector.setPrevValue("driveServiceBuildingHasError", currDriveServiceBuildingHasError);
            selector.setPrevValue("driveServiceBuildingErrorDescription", currDriveServiceBuildingErrorDescription);
        }));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        mController.backButtonHandler();
        mModel.unsubscribe();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        mController.activityResultHandler(requestCode, resultCode, resultData);
        super.onActivityResult(requestCode, resultCode, resultData);
    }
}
