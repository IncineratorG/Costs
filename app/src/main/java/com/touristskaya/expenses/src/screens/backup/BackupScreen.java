package com.touristskaya.expenses.src.screens.backup;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.touristskaya.expenses.R;
import com.touristskaya.expenses.src.libs.selector.Selector;
import com.touristskaya.expenses.src.screens.backup.store.BackupScreenState;
import com.touristskaya.expenses.src.stores.AppStore;
import com.touristskaya.expenses.src.utils.common.system_events.SystemEventsHandler;
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
        mCreateBackupDataButton.setOnClickListener((v) -> mController.createBackupButtonHandler());

        mSignInButton = (Button) findViewById(R.id.backup_data_sign_in_button);

        mArrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);
        mArrowBackImageView.setOnClickListener((v) -> mController.backButtonHandler(this));

        mSelectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);
        mSelectGoogleAccountImageView.setOnClickListener((v) -> mController.selectGoogleAccountIconHandler());

        mStatusTextView = (TextView) findViewById(R.id.backup_data_status_textview);

        mProgressDialog = new ProgressDialog(BackupScreen.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mState.select(new Selector((selector) -> {
            Boolean prevHasNetworkConnection = (Boolean) selector.getPrevValue();
            boolean currHasNetworkConnection = mState.hasNetworkConnection;

            if (prevHasNetworkConnection == null || currHasNetworkConnection != prevHasNetworkConnection) {
                SystemEventsHandler.onInfo("HAS_CONNECTION_FROM_SCREEN: " + currHasNetworkConnection);
            }

            selector.setPrevValue(currHasNetworkConnection);
        }));

//        // Проверяем соединение с интернетом.
//        Payload payload = new Payload();
//        payload.set("context", this);
//
//        Action checkInternetConnectionAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.CheckInternetConnection);
//        checkInternetConnectionAction.setPayload(payload);
//
//        mBackupStore.dispatch(checkInternetConnectionAction);
//
//        if (mBackupState.hasInternetConnection.get() && needRequestSignIn) {
//            if (!mBackupState.signedIn.get()) {
//                statusTextView.setText("Вход в аккаунт Google");
//                requestSignIn();
//            }
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unsubscribeAll();
    }

    @Override
    public void onBackPressed() {
        mController.backButtonHandler(this);
    }
}
