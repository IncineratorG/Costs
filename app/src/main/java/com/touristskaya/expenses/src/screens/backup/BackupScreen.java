package com.touristskaya.expenses.src.screens.backup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.touristskaya.expenses.R;
import com.touristskaya.expenses.activities.backup.AdapterActivityBackupDataRecyclerView;
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

    private BackupScreenModel mModel = new BackupScreenModel();
    private BackupScreenController mController = new BackupScreenController(mModel);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_data);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mBackupListRecyclerView = (RecyclerView) findViewById(R.id.backup_data_recycler_view);

        mCreateBackupDataButton = (Button) findViewById(R.id.backup_data_backup_button);

        mSignInButton = (Button) findViewById(R.id.backup_data_sign_in_button);

        mArrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);

        mSelectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);

        mStatusTextView = (TextView) findViewById(R.id.backup_data_status_textview);

        mProgressDialog = new ProgressDialog(BackupScreen.this);
    }
}
