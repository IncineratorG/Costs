package com.touristskaya.expenses.activities.backup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;
import com.touristskaya.expenses.ActivityMainWithFragments;
import com.touristskaya.expenses.Constants;
import com.touristskaya.expenses.DB_Costs;
import com.touristskaya.expenses.R;
import com.touristskaya.expenses.common.types.reactive.realisation.Subscription;
import com.touristskaya.expenses.services.realisation.backup.tasks.TaskRunner;
import com.touristskaya.expenses.stores.abstraction.Action;
import com.touristskaya.expenses.stores.abstraction.Store;
import com.touristskaya.expenses.stores.common.Payload;
import com.touristskaya.expenses.stores.realisation.Stores;
import com.touristskaya.expenses.stores.realisation.backup.BackupActionsFactory;
import com.touristskaya.expenses.stores.realisation.backup.BackupState;
import com.touristskaya.expenses.stores.realisation.backup.types.BackupData;
import com.touristskaya.expenses.stores.realisation.backup.types.CreateDeviceBackupStatus;
import com.touristskaya.expenses.stores.realisation.backup.types.DeleteDeviceBackupStatus;
import com.touristskaya.expenses.stores.realisation.backup.types.DriveServiceBundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class ActivityBackupData extends AppCompatActivity {
    private static final String TAG = "tag";
    private static final String CLASS_NAME = "ActivityBackupData";

    private ImageView arrowBackImageView;

    private Button createBackupDataButton;

    private List<DataUnitBackupFolder> existingDeviceBackupFolders = new ArrayList<>();

    private Calendar calendar;

    private RecyclerView backupListRecyclerView;
    private AdapterActivityBackupDataRecyclerView backupDataRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private TextView statusTextView;

    private ImageView selectGoogleAccountImageView;

    private static final int REQUEST_CODE_SIGN_IN = 1;

    private Store mBackupStore;
    private BackupState mBackupState;

    private Subscription mHasInternetConnectionSubscription;
    private Subscription mGoogleDriveServiceBundleSubscription;
    private Subscription mBackupDataSubscription;
    private Subscription mRestoreStatusSubscription;
    private Subscription mCreateDeviceBackupSubscription;
    private Subscription mDeleteDeviceBackupSubscription;

    private ProgressDialog mProgressDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_data);

        calendar = new GregorianCalendar();

        linearLayoutManager = new LinearLayoutManager(this);
        backupListRecyclerView = (RecyclerView) findViewById(R.id.backup_data_recycler_view);

        // Кнопка создания резервной копии
        createBackupDataButton = (Button) findViewById(R.id.backup_data_backup_button);
        createBackupDataButton.setEnabled(false);
        createBackupDataButton.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
        createBackupDataButton.setOnClickListener((v) -> createDeviceBackup());

        // При нажатии стрелки назад - возвращаемся к предыдущему экрану
        arrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);
        arrowBackImageView.setOnClickListener((v) -> returnToPreviousActivity());

        // Открываем диалоговое окно, в котором можно выбрать аккаунт Google
        selectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);
        selectGoogleAccountImageView.setOnClickListener((v) -> signOut());

        statusTextView = (TextView) findViewById(R.id.backup_data_status_textview);
        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));

        mBackupStore = Stores.getInstance().getStore(Stores.BackupStore);
        mBackupState = (BackupState) mBackupStore.getState();

        // Очищаем всю информацию в хранилище.
        Action clearStoreAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.ClearStore);
        mBackupStore.dispatch(clearStoreAction);

        // Подписываемся на необходимые параметры хранилища.
        setSubscriptions();

        // Все кнопки, кроме кнопки "Назад" делаем неактивными до момента получения данных обфайлах резервных копий.
        disableBackground();

        mProgressDialog = new ProgressDialog(ActivityBackupData.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Cancel_string), (d, w) -> {
            mBackupStore.dispatch(mBackupStore.getActionFactory().getAction(BackupActionsFactory.StopCurrentAsyncTask));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Проверяем соединение с интернетом.
        Payload payload = new Payload();
        payload.set("context", this);

        Action checkInternetConnectionAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.CheckInternetConnection);
        checkInternetConnectionAction.setPayload(payload);

        mBackupStore.dispatch(checkInternetConnectionAction);

        if (mBackupState.hasInternetConnection.get()) {
            if (!mBackupState.signedIn.get()) {
                requestSignIn();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unsubscribeAll();
    }

    @Override
    public void onBackPressed() {
        returnToPreviousActivity();
    }

    // Возвращаемся к предыдущему экрану
    private void returnToPreviousActivity() {
        Intent mainActivityWithFragmentsIntent = new Intent(ActivityBackupData.this, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityWithFragmentsIntent);
    }

    private void requestSignIn() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE))
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        Action setGoogleSignInClient = mBackupStore.getActionFactory().getAction(BackupActionsFactory.SetGoogleSignInClient);

        Payload payload = new Payload();
        payload.set("googleSignInClient", client);

        setGoogleSignInClient.setPayload(payload);

        mBackupStore.dispatch(setGoogleSignInClient);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private void signOut() {
        disableBackground();

        mBackupState.googleSignInClient.get().signOut().addOnCompleteListener((task) -> {
            requestSignIn();
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void handleSignInResult(Intent result) {
        // Устанавливаем признак того, что пользователь залогинился.
        Action signInAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.SetSignIn);

        mBackupStore.dispatch(signInAction);

        // Создаём Drive сервис.
        Payload payload = new Payload();
        payload.set("result_intent", result);
        payload.set("context", this);
        payload.set("appLabel", getAppLabel(this));

        Action buildGoogleDriveServiceAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.BuildGoogleDriveService);
        buildGoogleDriveServiceAction.setPayload(payload);

        mBackupStore.dispatch(buildGoogleDriveServiceAction);
    }

    private void setSubscriptions() {
        final String METHOD_NAME = ".setSubscriptions()";

        mHasInternetConnectionSubscription = mBackupState.hasInternetConnection.subscribe(() -> {
            if (!mBackupState.hasInternetConnection.get()) {
                statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));
            } else {
                statusTextView.setText(getResources().getString(R.string.abd_statusTextView_connectionAcquired_string));
            }
        });

        mGoogleDriveServiceBundleSubscription = mBackupState.driveServiceBundle.subscribe(() -> {
            switch (mBackupState.driveServiceBundle.get().getDriveServiceStatus()) {
                case DriveServiceBundle.Set: {
                    mProgressDialog.dismiss();

                    // Получаем данные резервной копии.
                    Payload payload = new Payload();
                    payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());

                    Action getBackupData = mBackupStore.getActionFactory().getAction(BackupActionsFactory.GetBackupData);
                    getBackupData.setPayload(payload);

                    mBackupStore.dispatch(getBackupData);

                    break;
                }

                case DriveServiceBundle.Setting: {
                    mProgressDialog.setTitle(null);
                    mProgressDialog.setMessage("Подключение к серверам Google");
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }

                case DriveServiceBundle.NotSet: {
                    mProgressDialog.dismiss();
                    break;
                }
            }
        });

        mBackupDataSubscription = mBackupState.backupData.subscribe(() -> {
            switch (mBackupState.backupData.get().getBackupDataStatus()) {
                case BackupData.Set: {
                    mProgressDialog.dismiss();

                    // Отображаем полученный список резервных копий
                    existingDeviceBackupFolders = mBackupState.backupData.get().getDeviceBackupFolders();
                    backupListRecyclerView.setLayoutManager(linearLayoutManager);
                    backupDataRecyclerViewAdapter = new AdapterActivityBackupDataRecyclerView(ActivityBackupData.this, existingDeviceBackupFolders);
                    backupListRecyclerView.setAdapter(backupDataRecyclerViewAdapter);

                    enableBackground();

                    break;
                }

                case BackupData.Setting: {
                    mProgressDialog.setTitle("");
                    mProgressDialog.setMessage("Получение резервных копий");
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }

                case BackupData.NotSet: {
                    mProgressDialog.dismiss();
                    break;
                }
            }
        });

        mRestoreStatusSubscription = mBackupState.restoreStatus.subscribe(() -> {
            final String restoreStatus = mBackupState.restoreStatus.get().getStatus();

            switch (restoreStatus) {
                case TaskRunner.TaskStartedStatus: {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    mProgressDialog.setMessage(getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Title_string));
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }

                case TaskRunner.TaskCompletedStatus:
                case TaskRunner.TaskInterruptedStatus:
                case TaskRunner.TaskErrorOccurredStatus: {
                    mProgressDialog.dismiss();

                    Toast dataRestoredToast;
                    if (restoreStatus.equals(TaskRunner.TaskCompletedStatus)) {
                        statusTextView.setText(getResources().getString(R.string.abd_dataRestoredSuccessful_string));
                        dataRestoredToast = Toast.makeText(this, getResources().getString(R.string.abd_dataRestoredSuccessful_string), Toast.LENGTH_SHORT);
                    } else {
                        statusTextView.setText(getResources().getString(R.string.abd_dataNotRestored_string));
                        dataRestoredToast = Toast.makeText(this, getResources().getString(R.string.abd_dataNotRestored_string), Toast.LENGTH_SHORT);
                    }
                    dataRestoredToast.show();

                    enableBackground();

                    break;
                }

                default: {
                    mProgressDialog.setMessage(restoreStatus);
                }
            }
        });

        mCreateDeviceBackupSubscription = mBackupState.createDeviceBackupStatus.subscribe(() -> {
            final String createDeviceBackupStatus = mBackupState.createDeviceBackupStatus.get().getStatus();

            switch (createDeviceBackupStatus) {
                case CreateDeviceBackupStatus.Complete:
                case CreateDeviceBackupStatus.NotComplete: {
                    mProgressDialog.dismiss();

                    // Получаем список резервных копий.
                    Payload payload = new Payload();
                    payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());

                    Action getBackupData = mBackupStore.getActionFactory().getAction(BackupActionsFactory.GetBackupData);
                    getBackupData.setPayload(payload);

                    mBackupStore.dispatch(getBackupData);

                    break;
                }

                case CreateDeviceBackupStatus.InProgress: {
                    mProgressDialog.setTitle("");
                    mProgressDialog.setMessage("Создание резервной копии");
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }
            }
        });

        mDeleteDeviceBackupSubscription = mBackupState.deleteDeviceBackupStatus.subscribe(() -> {
            final String deleteDeviceBackupStatus = mBackupState.deleteDeviceBackupStatus.get().getStatus();

            switch (deleteDeviceBackupStatus) {
                case DeleteDeviceBackupStatus.Complete:
                case DeleteDeviceBackupStatus.NotComplete: {
                    mProgressDialog.dismiss();

                    // Получаем данные резервной копии.
                    Payload payload = new Payload();
                    payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());

                    Action getBackupData = mBackupStore.getActionFactory().getAction(BackupActionsFactory.GetBackupData);
                    getBackupData.setPayload(payload);

                    mBackupStore.dispatch(getBackupData);

                    break;
                }

                case DeleteDeviceBackupStatus.InProgress: {
                    mProgressDialog.setTitle("");
                    mProgressDialog.setMessage("Удаление резервной копии");
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }
            }
        });
    }

    private void unsubscribeAll() {
        mHasInternetConnectionSubscription.unsubscribe();
        mGoogleDriveServiceBundleSubscription.unsubscribe();
        mBackupDataSubscription.unsubscribe();
        mRestoreStatusSubscription.unsubscribe();
        mCreateDeviceBackupSubscription.unsubscribe();
        mDeleteDeviceBackupSubscription.unsubscribe();
    }

    // При нажатии на элемент списка резервных копий - отображаем диалоговое окно,
    // предлагающее восстановить данные из резервной копии или удалить выбранную резервную копию
    private void onBackupItemClick(final int position) {
        DataUnitBackupFolder selectedBackupItem = existingDeviceBackupFolders.get(position);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(selectedBackupItem.getMilliseconds());

        AlertDialog.Builder chosenBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
        LayoutInflater inflater = LayoutInflater.from(ActivityBackupData.this);
        View dialogView = inflater.inflate(R.layout.edit_cost_value_dialog, null);
        chosenBackupItemDialogBuilder.setView(dialogView);

        // Устанавливаем дату выбранной резервной копии
        TextView dateTextView = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costDate);
        dateTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                selectedBackupItem.getDay() + " " +
                Constants.DECLENSION_MONTH_NAMES[selectedBackupItem.getMonth()] + " " +
                selectedBackupItem.getYear() + ", " +
                calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                calendar.get(Calendar.MINUTE));

        // Устанавливаем название устройства, на котром была создана выбранная резервная копия
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costName);
        descriptionTextView.setText(selectedBackupItem.getDeviceManufacturer() + " " +
                selectedBackupItem.getDeviceModel());

        // Скрываем ненужные элементы
        TextView invisTextView_1 = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costValue);
        invisTextView_1.setVisibility(View.GONE);
        TextView invisTextView_2 = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costNote);
        invisTextView_2.setVisibility(View.GONE);

        // Запускаем диалоговое окно
        final AlertDialog chosenBackupItemDialog = chosenBackupItemDialogBuilder.create();
        chosenBackupItemDialog.show();

        // Устанавливаем слушатели на кнопки созданного диалогового окна
        // При нажатии кнопки "Восстановить" появляется диалоговое окно, запрашивающее подтверждение восстановления
        Button restoreButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_editButton);
        restoreButton.setText(getResources().getString(R.string.abd_restoreButton_string));
        restoreButton.setOnClickListener((v) -> {
            chosenBackupItemDialog.dismiss();

            AlertDialog.Builder restoreFromChosenBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
            restoreFromChosenBackupItemDialogBuilder.setTitle(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_Title_string));
            restoreFromChosenBackupItemDialogBuilder.setMessage(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_Message_string));
            restoreFromChosenBackupItemDialogBuilder.setPositiveButton(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_continue_button_string), (d, w) -> {
                disableBackground();
                restoreDataFromBackup(position);
            });
            restoreFromChosenBackupItemDialogBuilder.setNegativeButton(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_cancel_button_string), null);

            AlertDialog restoreFromChosenBackupItemDialog = restoreFromChosenBackupItemDialogBuilder.create();
            restoreFromChosenBackupItemDialog.show();
        });

        // При нажатии кнопки "Удалить" появляется диалоговое окно, запрашивающее подтверждение удаления
        Button deleteButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_deleteButton);
        deleteButton.setText(getResources().getString(R.string.abd_deleteButton_string));
        deleteButton.setOnClickListener((v) -> {
            chosenBackupItemDialog.dismiss();

            AlertDialog.Builder deleteBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
            deleteBackupItemDialogBuilder.setTitle(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_Title_string));
            deleteBackupItemDialogBuilder.setMessage(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_Message_string));
            deleteBackupItemDialogBuilder.setPositiveButton(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_delete_button_string), (d, w) -> {
                deleteBackupItem(position);
            });
            deleteBackupItemDialogBuilder.setNegativeButton(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_cancel_button_string), null);

            AlertDialog deleteBackupItemDialog = deleteBackupItemDialogBuilder.create();
            deleteBackupItemDialog.show();
        });

        Button cancelButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_cancelButton);
        cancelButton.setText(getResources().getString(R.string.abd_cancelButton_string));
        cancelButton.setOnClickListener((v) -> chosenBackupItemDialog.dismiss());
    }

    private void restoreDataFromBackup(int position) {
        if (existingDeviceBackupFolders.size() == 0) {
            Log.i(TAG, "NO BACKUP FILES FOUND");
            return;
        }

        String backupFolderId = existingDeviceBackupFolders.get(position).getDriveId();

        Payload payload = new Payload();
        payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());
        payload.set("backupFolderId", backupFolderId);
        payload.set("costsDb", DB_Costs.getInstance(this));

        Action restoreFromBackup = mBackupStore.getActionFactory().getAction(BackupActionsFactory.RestoreFromBackup);
        restoreFromBackup.setPayload(payload);

        mBackupStore.dispatch(restoreFromBackup);
    }

    private void createDeviceBackup() {
        Payload payload = new Payload();
        payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());
        payload.set("rootFolderId", mBackupState.backupData.get().getRootFolderId());
        payload.set("costsDb", DB_Costs.getInstance(this));

        Action createDeviceBackup = mBackupStore.getActionFactory().getAction(BackupActionsFactory.CreateDeviceBackup);
        createDeviceBackup.setPayload(payload);

        mBackupStore.dispatch(createDeviceBackup);
    }

    private void deleteBackupItem(int position) {
        if (existingDeviceBackupFolders.size() == 0) {
            Log.i(TAG, "NO BACKUP FILES FOUND");
            return;
        }

        String backupFolderId = existingDeviceBackupFolders.get(position).getDriveId();

        Payload payload = new Payload();
        payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());
        payload.set("backupFolderId", backupFolderId);

        Action deleteDeviceBackup = mBackupStore.getActionFactory().getAction(BackupActionsFactory.DeleteDeviceBackup);
        deleteDeviceBackup.setPayload(payload);

        mBackupStore.dispatch(deleteDeviceBackup);
    }

    private void enableBackground() {
        if (backupDataRecyclerViewAdapter != null) {
            backupDataRecyclerViewAdapter.setClickListener((v, p) -> onBackupItemClick(p));
        }

        createBackupDataButton.setEnabled(true);
        createBackupDataButton.setBackgroundResource(R.drawable.keyboard_buttons_custom);
        createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));

        selectGoogleAccountImageView.setEnabled(true);
    }

    private void disableBackground() {
        if (backupDataRecyclerViewAdapter != null) {
            backupDataRecyclerViewAdapter.setClickListener(null);
        }

        createBackupDataButton.setEnabled(false);
        createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));

        selectGoogleAccountImageView.setEnabled(false);
    }

    private String getAppLabel(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            Log.d(TAG, CLASS_NAME + "->getAppLabel->NameNotFoundException");
        }

        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }







//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_backup_data);
//
//
//        calendar = new GregorianCalendar();
//        cdb = DB_Costs.getInstance(this);
//
//
//        linearLayoutManager = new LinearLayoutManager(this);
//        backupListRecyclerView = (RecyclerView) findViewById(R.id.backup_data_recycler_view);
//
//        // Кнопка создания резервной копии
//        createBackupDataButton = (Button) findViewById(R.id.backup_data_backup_button);
//        createBackupDataButton.setEnabled(false);
//        createBackupDataButton.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
//        createBackupDataButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createBackupData_V2();
//                createBackupDataButton.setEnabled(false);
//                createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));
//                if (backupDataRecyclerViewAdapter != null)
//                    backupDataRecyclerViewAdapter.setClickListener(null);
//            }
//        });
//
//        // При нажатии стрелки назад - возвращаемся к предыдущему экрану
//        arrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);
//        arrowBackImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                returnToPreviousActivity();
//            }
//        });
//
//        // Открываем диалоговое окно, в котором можно выбрать аккаунт Google
//        selectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);
//        selectGoogleAccountImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectGoogleAccount();
//            }
//        });
//
//        // Создаём googleApiClient
//        if (googleApiClient == null) {
//            googleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(Drive.API)
//                    .addScope(Drive.SCOPE_APPFOLDER)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//        }
//
////        currentDefaultString = "Нет соединения с сетью";
//        statusTextView = (TextView) findViewById(R.id.backup_data_status_textview);
//        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (!hasNetworkConnection()) {
////            currentDefaultString = "Нет соединения с сетью";
//            statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));
//        } else
//            connectToGoogleDrive();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (asyncTaskRestoreData != null)
//            asyncTaskRestoreData.cancel(true);
//        disconnectFromGoogleDrive();
//    }
//
//// ===========================================================================
//
//
//    // ============================ MY FUNCTIONS =================================
//    // Выбираем аккаунт Google
//    private void selectGoogleAccount() {
//        connectedToGoogleDrive = false;
//
//        // Очищаем список доступных резервных копий
//        if (existingDeviceBackupFolders != null)
//            existingDeviceBackupFolders.clear();
//        if (backupDataRecyclerViewAdapter != null)
//            backupDataRecyclerViewAdapter.notifyDataSetChanged();
//
//        // Отключаем возможность создания резервной копии
//        createBackupDataButton.setEnabled(false);
//        createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));
//
//        // Показываем диалоговое окно с возможностью выбора аккаунта Google. Если пользователь ничего
//        // не выберет, то повторно вызвать данное диалоговое окно можно либо нажав на значок выбора
//        // аккаунта, либо заново зайдя в данную активность
//        if (googleApiClient != null) {
//            if (!chooseAccountDialogShown) {
//                if (googleApiClient.isConnected())
//                    googleApiClient.clearDefaultAccountAndReconnect();
//            } else {
//                chooseAccountDialogShown = false;
//                connectToGoogleDrive();
//            }
//        }
//
//    }
//
//    // Проверяем есть ли соединение с интернетом
//    private boolean hasNetworkConnection() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(ActivityBackupData.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//
//        return networkInfo != null;
//    }
//
//    // Соединяемся с Google Drive
//    private void connectToGoogleDrive() {
//        if (googleApiClient != null) {
//            statusTextView.setText(getResources().getString(R.string.abd_statusTextView_acquiringConnection_string));
//            googleApiClient.connect();
//        }
//    }
//
//    // Разрываем соединение с Google Drive
//    private void disconnectFromGoogleDrive() {
//        if (googleApiClient != null) {
//            googleApiClient.disconnect();
//        }
//    }
//
//    // Восстанавливаем данные из резервной копии
//    private void restoreDataFromBackup(int selectedBackupListPosition) {
//        if (existingDeviceBackupFolders.size() == 0) {
//            Log.i(TAG, "NO BACKUP FILES FOUND");
//            return;
//        }
//
//        // Находим файлы резервной копии
//        DriveFolder selectedBackupFolder = existingDeviceBackupFolders.get(selectedBackupListPosition).getDriveId().asDriveFolder();
//        selectedBackupFolder.listChildren(googleApiClient).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
//            @Override
//            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
//                if (!metadataBufferResult.getStatus().isSuccess()) {
//                    Log.i(TAG, "!!ERROR RETRIEVING BACKUP FILES!!");
//                    return;
//                }
//
//                // Получаем файлы резервной копии
//                DriveFile tableCostNamesBackupFile = null;
//                DriveFile tableCostValuesBackupFile = null;
//                MetadataBuffer backupFilesMetadata = metadataBufferResult.getMetadataBuffer();
//                for (int i = 0; i < backupFilesMetadata.getCount(); ++i) {
//                    if (TABLE_COST_NAMES_FILE_NAME.equals(backupFilesMetadata.get(i).getTitle()))
//                        tableCostNamesBackupFile = backupFilesMetadata.get(i).getDriveId().asDriveFile();
//                    else if (TABLE_COST_VALUES_FILE_NAME.equals(backupFilesMetadata.get(i).getTitle()))
//                        tableCostValuesBackupFile = backupFilesMetadata.get(i).getDriveId().asDriveFile();
//                }
//
//                // Восстанавливаем данные из резервной копии в отдельном потоке
//                asyncTaskRestoreData = new AsyncTaskRestoreData(googleApiClient,
//                                        ActivityBackupData.this,
//                                        tableCostNamesBackupFile,
//                                        tableCostValuesBackupFile,
//                                        statusTextView)
//                        .execute();
//            }
//        });
//    }
//
//    // Создаём резервную копию данных
//    private void createBackupData_V2() {
//        // Формируем название новой папки резервной копии
//        DEVICE_BACKUP_FOLDER_NAME = Build.MANUFACTURER + Constants.BACKUP_FOLDER_NAME_DELIMITER
//                + Build.MODEL + Constants.BACKUP_FOLDER_NAME_DELIMITER
//                + Build.ID + Constants.BACKUP_FOLDER_NAME_DELIMITER
//                + calendar.get(Calendar.DAY_OF_MONTH) + "."
//                + calendar.get(Calendar.MONTH) + "."
//                + calendar.get(Calendar.YEAR) + Constants.BACKUP_FOLDER_NAME_DELIMITER
//                + calendar.getTimeInMillis() + Constants.BACKUP_FOLDER_NAME_DELIMITER
//                + BACKUP_USER_COMMENT;
//
//        // Если корневая папка создана - сохраняем папку резервной копии в ней.
//        // Иначе - создаём корневую папку, в ней создаём папку резервной копии,
//        // в этой папке создаём файлы резервной копии
//        if (ROOT_BACKUP_FOLDER_FOLDER != null) {
//            createDeviceBackup();
//            return;
//        }
//
//        // Создаём корневую папку резервной копии
//        Log.i(TAG, "CREATING ROOT BACKUP FOLDER");
//        MetadataChangeSet rootBackupFolderMetadata = new MetadataChangeSet.Builder()
//                .setTitle(ROOT_BACKUP_FOLDER_NAME)
//                .setPinned(false)
//                .build();
//
//        Drive.DriveApi.getAppFolder(googleApiClient)
//                .createFolder(googleApiClient, rootBackupFolderMetadata)
//                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
//                    @Override
//                    public void onResult(@NonNull DriveFolder.DriveFolderResult driveFolderResult) {
//                        if (!driveFolderResult.getStatus().isSuccess()) {
//                            Log.i(TAG, "ERROR CREATING ROOT BACKUP FOLDER");
//                            dataSaved(false);
//                            return;
//                        }
//                        Log.i(TAG, "ROOT BACKUP FOLDER CREATED");
//
//                        ROOT_BACKUP_FOLDER_ID = driveFolderResult.getDriveFolder().getDriveId();
//                        ROOT_BACKUP_FOLDER_FOLDER = driveFolderResult.getDriveFolder();
//
//                        if (ROOT_BACKUP_FOLDER_FOLDER == null)
//                            return;
//
//                        createDeviceBackup();
//                    }
//                });
//
//        Log.i(TAG, "CREATE_BACKUP_DATA FUNCTION END");
//    }
//
//    // Создаём папку с резервной копией данных. Расположена внутри корневой папки
//    private void createDeviceBackup() {
//        // Создаём папку резервной копии для текущего устройства
//        Log.i(TAG, "CREATING DEVICE BACKUP FOLDER");
//        MetadataChangeSet deviceBackupFolderMetadata = new MetadataChangeSet.Builder()
//                .setTitle(DEVICE_BACKUP_FOLDER_NAME)
//                .setPinned(false)
//                .build();
//        ROOT_BACKUP_FOLDER_FOLDER.createFolder(googleApiClient, deviceBackupFolderMetadata)
//                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
//                    @Override
//                    public void onResult(@NonNull DriveFolder.DriveFolderResult driveFolderResult) {
//                        if (!driveFolderResult.getStatus().isSuccess()) {
//                            Log.i(TAG, "ERROR CREATING DEVICE BACKUP FOLDER");
//                            dataSaved(false);
//                            return;
//                        }
//
//                        DEVICE_BACKUP_FOLDER_ID = driveFolderResult.getDriveFolder().getDriveId();
//                        DEVICE_BACKUP_FOLDER_FOLDER = DEVICE_BACKUP_FOLDER_ID.asDriveFolder();
//                        if (DEVICE_BACKUP_FOLDER_ID != null && DEVICE_BACKUP_FOLDER_FOLDER != null)
//                            Log.i(TAG, "DEVICE BACKUP FOLDER CREATED");
//                        else {
//                            Log.i(TAG, "DEVICE_BACKUP_FOLDER_ID OR DEVICE_BACKUP_FOLDER_FOLDER IS NULL");
//                            return;
//                        }
//
//                        // Создаём файлы резервной копии в отдельном потоке
//                        Log.i(TAG, "ASYNC START");
//                        new AsyncTaskSaveDeviceData(googleApiClient,
//                                                    DEVICE_BACKUP_FOLDER_FOLDER,
//                                                    ActivityBackupData.this,
//                                                    TABLE_COST_NAMES_FILE_NAME,
//                                                    REFERENCE_FILE_NAME,
//                                                    TABLE_COST_VALUES_FILE_NAME,
//                                                    statusTextView)
//                                .execute();
//                    }
//                });
//
//        Log.i(TAG, "CREATE_CURRENT_DEVICE_BACKUP FUNCTION END");
//    }
//
//    // Удаляем выбранный элемент списка резервных копий
//    private void deleteBackupItem(int selectedBackupListPosition) {
//        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_deleting_string));
//
//        DriveFolder folderToDelete = existingDeviceBackupFolders.get(selectedBackupListPosition).getDriveId().asDriveFolder();
//        folderToDelete.delete(googleApiClient).setResultCallback(new ResultCallback<Status>() {
//            @Override
//            public void onResult(@NonNull Status status) {
//                if (!status.isSuccess()) {
//                    Toast errorDeletingBackupFolderToast = Toast.makeText(ActivityBackupData.this,
//                            getResources().getString(R.string.abd_errorDeletingBackupFolderToast_string), Toast.LENGTH_LONG);
//                    errorDeletingBackupFolderToast.show();
//                } else {
//                    Toast backupFolderDeletedToast = Toast.makeText(ActivityBackupData.this,
//                            getResources().getString(R.string.abd_backupFolderDeletedToast_string), Toast.LENGTH_SHORT);
//                    backupFolderDeletedToast.show();
//                }
//
//                searchForBackupData();
//            }
//        });
//    }
//
//    // Ищем данные резервной копии
//    private void searchForBackupData() {
//        if (existingDeviceBackupFolders != null)
//            existingDeviceBackupFolders.clear();
//        if (backupDataRecyclerViewAdapter != null)
//            backupDataRecyclerViewAdapter.notifyDataSetChanged();
//
//        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_searchBackupData_string));
//
//        // Ищем папку с сохранёнными данными
//        Query searchRootBackupFolder = new Query.Builder()
//                .addFilter(Filters.eq(SearchableField.TITLE, ROOT_BACKUP_FOLDER_NAME))
//                .build();
//        Drive.DriveApi.query(googleApiClient, searchRootBackupFolder).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
//            @Override
//            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
//                if (!metadataBufferResult.getStatus().isSuccess()) {
//                    Log.i(TAG, "ERROR TRYING TO FIND ROOT BACKUP FOLDER");
//                    statusTextView.setText("ERROR TRYING TO FIND ROOT BACKUP FOLDER");
//                    return;
//                }
//
//                MetadataBuffer metadataBuffer = metadataBufferResult.getMetadataBuffer();
//                ROOT_BACKUP_FOLDER_ID = null;
//
//                // Если папка не найдена - прекращаем обработку
//                if (metadataBuffer.getCount() <= 0) {
//                    Log.i(TAG, "NO BACKUP DATA FOUND");
//                    statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noBackupDataFound_string));
//                    createBackupDataButton.setEnabled(true);
//                    createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));
//                    return;
//                }
//
//                // Считаем первую найденную не удалённую папку папкой с резервной копией данных
//                for (int i = 0; i < metadataBuffer.getCount(); ++i) {
//                    Metadata metadata = metadataBuffer.get(i);
//                    if (!metadata.isTrashed()) {
//                        ROOT_BACKUP_FOLDER_ID = metadata.getDriveId();
//                        break;
//                    }
//                }
//                if (ROOT_BACKUP_FOLDER_ID == null)
//                    return;
//
//                // Если папка с резервной копией найдена - ищем подпапки
//                // резервной копии внутри этой папки
//                Log.i(TAG, "ROOT BACKUP FOLDER FOUND");
//                ROOT_BACKUP_FOLDER_FOLDER = ROOT_BACKUP_FOLDER_ID.asDriveFolder();
//                ROOT_BACKUP_FOLDER_FOLDER.listChildren(googleApiClient).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
//                    @Override
//                    public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
//                        if (!metadataBufferResult.getStatus().isSuccess()) {
//                            Log.i(TAG, "ERROR LISTING ROOT BACKUP FOLDER CHILDREN");
//                            statusTextView.setText("ERROR LISTING ROOT BACKUP FOLDER CHILDREN");
//                            return;
//                        }
//
//                        existingDeviceBackupFolders = new ArrayList<>();
//                        // Получаем названия и ID подпапок с резервными копиями
//                        MetadataBuffer devicesBackupFoldersMetadata = metadataBufferResult.getMetadataBuffer();
//                        for (int i = 0; i < devicesBackupFoldersMetadata.getCount(); ++i) {
//                            Log.i(TAG, "FOLDER: " + devicesBackupFoldersMetadata.get(i).getTitle());
//
//                            DataUnitBackupFolder backupTitle = new DataUnitBackupFolder();
//                            backupTitle.setTitle(devicesBackupFoldersMetadata.get(i).getTitle());
//                            backupTitle.setDriveId(devicesBackupFoldersMetadata.get(i).getDriveId());
//
//                            existingDeviceBackupFolders.add(backupTitle);
//                        }
//
//                        // Отображаем полученный список резервных копий
//                        backupListRecyclerView.setLayoutManager(linearLayoutManager);
//                        backupDataRecyclerViewAdapter = new AdapterActivityBackupDataRecyclerView(ActivityBackupData.this, existingDeviceBackupFolders);
//                        backupDataRecyclerViewAdapter.setClickListener(new AdapterActivityBackupDataRecyclerView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(View itemView, int position) {
//                                onBackupItemClick(position);
//                            }
//                        });
//                        backupListRecyclerView.setAdapter(backupDataRecyclerViewAdapter);
//
//                        createBackupDataButton.setEnabled(true);
//                        createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));
//
//                        if (existingDeviceBackupFolders.size() > 0) {
//                            statusTextView.setText(getResources().getString(R.string.abd_statusTextView_backupDataFound_string));
//                        } else {
//                            statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noBackupDataFound_string));
//                        }
//                    }
//                });
//
//            }
//        });
//    }
//
//    // При нажатии на элемент списка резервных копий - отображаем диалоговое окно,
//    // предлагающее восстановить данные из резервной копии или удалить выбранную резервную копию
//    private void onBackupItemClick(final int position) {
//        DataUnitBackupFolder selectedBackupItem = existingDeviceBackupFolders.get(position);
//
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTimeInMillis(selectedBackupItem.getMilliseconds());
//
//        AlertDialog.Builder chosenBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
//        LayoutInflater inflater = LayoutInflater.from(ActivityBackupData.this);
//        View dialogView = inflater.inflate(R.layout.edit_cost_value_dialog, null);
//        chosenBackupItemDialogBuilder.setView(dialogView);
//
//        // Устанавливаем дату выбранной резервной копии
//        TextView dateTextView = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costDate);
//        dateTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
//                selectedBackupItem.getDay() + " " +
//                Constants.DECLENSION_MONTH_NAMES[selectedBackupItem.getMonth()] + " " +
//                selectedBackupItem.getYear() + ", " +
//                calendar.get(Calendar.HOUR_OF_DAY) + ":" +
//                calendar.get(Calendar.MINUTE));
//
//        // Устанавливаем название устройства, на котром была создана выбранная резервная копия
//        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costName);
//        descriptionTextView.setText(selectedBackupItem.getDeviceManufacturer() + " " +
//                selectedBackupItem.getDeviceModel());
//
//        // Скрываем ненужные элементы
//        TextView invisTextView_1 = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costValue);
//        invisTextView_1.setVisibility(View.GONE);
//        TextView invisTextView_2 = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costNote);
//        invisTextView_2.setVisibility(View.GONE);
//
//        // Запускаем диалоговое окно
//        final AlertDialog chosenBackupItemDialog = chosenBackupItemDialogBuilder.create();
//        chosenBackupItemDialog.show();
//
//        // Устанавливаем слушатели на кнопки созданного диалогового окна
//        // При нажатии кнопки "Восстановить" появляется диалоговое окно, запрашивающее подтверждение восстановления
//        Button restoreButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_editButton);
//        restoreButton.setText(getResources().getString(R.string.abd_restoreButton_string));
//        restoreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chosenBackupItemDialog.dismiss();
//
//                AlertDialog.Builder restoreFromChosenBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
//                restoreFromChosenBackupItemDialogBuilder.setTitle(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_Title_string));
//                restoreFromChosenBackupItemDialogBuilder.setMessage(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_Message_string));
//                restoreFromChosenBackupItemDialogBuilder.setPositiveButton(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_continue_button_string), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        backupDataRecyclerViewAdapter.setClickListener(null);
//
//                        createBackupDataButton.setEnabled(false);
//                        createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));
//
//                        arrowBackImageView.setEnabled(false);
//
//                        restoreDataFromBackup(position);
//                    }
//                });
//                restoreFromChosenBackupItemDialogBuilder.setNegativeButton(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_cancel_button_string), null);
//
//                AlertDialog restoreFromChosenBackupItemDialog = restoreFromChosenBackupItemDialogBuilder.create();
//                restoreFromChosenBackupItemDialog.show();
//            }
//        });
//
//        // При нажатии кнопки "Удалить" появляется диалоговое окно, запрашивающее подтверждение удаления
//        Button deleteButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_deleteButton);
//        deleteButton.setText(getResources().getString(R.string.abd_deleteButton_string));
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chosenBackupItemDialog.dismiss();
//
//                AlertDialog.Builder deleteBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
//                deleteBackupItemDialogBuilder.setTitle(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_Title_string));
//                deleteBackupItemDialogBuilder.setMessage(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_Message_string));
//                deleteBackupItemDialogBuilder.setPositiveButton(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_delete_button_string), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteBackupItem(position);
//                    }
//                });
//                deleteBackupItemDialogBuilder.setNegativeButton(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_cancel_button_string), null);
//
//                AlertDialog deleteBackupItemDialog = deleteBackupItemDialogBuilder.create();
//                deleteBackupItemDialog.show();
//            }
//        });
//
//        Button cancelButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_cancelButton);
//        cancelButton.setText(getResources().getString(R.string.abd_cancelButton_string));
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chosenBackupItemDialog.dismiss();
//            }
//        });
//    }
//
//    // Возвращаемся к предыдущему экрану
//    private void returnToPreviousActivity() {
//        if (asyncTaskRestoreData != null)
//            asyncTaskRestoreData.cancel(true);
//        Intent mainActivityWithFragmentsIntent = new Intent(ActivityBackupData.this, ActivityMainWithFragments.class);
//        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(mainActivityWithFragmentsIntent);
//    }
//    // ===========================================================================
//
//
//
//    // ======================== ON_CONNECTION CALLBACKS ==============================
//    // При соединении проверяем, есть ли на Гугл Диске данные резервной копии.
//    // Если есть - инициализируем ID папки и файлов с резервной копией
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        chooseAccountDialogShown = false;
//        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_connectionAcquired_string));
////        createBackupDataButton.setEnabled(true);
////        createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));
//        Log.i(TAG, "CONNECTED");
//        Log.i(TAG, "TRYING TO FIND ROOT BACKUP FOLDER");
//
//        // Ищем папку с сохранёнными данными
//        searchForBackupData();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_connectionSuspended_string));
//        Log.i(TAG, "CONNECTION_SUSPENDED");
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.i(TAG, "CONNECTION_FAILED: " + connectionResult.toString());
//        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_chooseGoogleAccount_string));
//
//        // Окно с выбором аккаунта Google показывается только один раз.
//        if (chooseAccountDialogShown)
//            return;
//
//        if (!connectionResult.hasResolution()) {
//            // show the localized error dialog.
//            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
//            return;
//        }
//
//        try {
//            chooseAccountDialogShown = true;
//            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
//        } catch (IntentSender.SendIntentException e) {
//            Log.e(TAG, "EXCEPTION WHILE STARING RESOLUTION ACTIVITY", e);
//        }
//    }
//    // ===============================================================================
//
//
//
//    // =================== SELECT GOOGLE ACCOUNT CALLBACK ============================
//    @Override
//    protected void onActivityResult(final int requestCode,
//                                    final int resultCode, final Intent data) {
//        switch (requestCode) {
//
//            case REQUEST_CODE_RESOLUTION:
//
//                System.out.println(resultCode);
//
//                break;
//
//            default:
//                super.onActivityResult(requestCode, resultCode, data);
//                break;
//        }
//    }
//    // ===============================================================================
//
//
//
//    // ============ AdapterActivityBackupDataRecyclerView, AsyncTaskRestoreData CALLBACK ===========
//    @Override
//    public void dataSaved(boolean dateSavedSuccessful) {
//        Toast dataSavedToast;
//        if (dateSavedSuccessful) {
//            statusTextView.setText(getResources().getString(R.string.abd_dataSavedSuccessful_string));
//            dataSavedToast = Toast.makeText(this, getResources().getString(R.string.abd_dataSavedSuccessful_string), Toast.LENGTH_SHORT);
//        } else {
//            statusTextView.setText(getResources().getString(R.string.abd_dataNotSaved_string));
//            dataSavedToast = Toast.makeText(this, getResources().getString(R.string.abd_dataNotSaved_string), Toast.LENGTH_SHORT);
//        }
//        dataSavedToast.show();
//
//        createBackupDataButton.setEnabled(true);
//        createBackupDataButton.setBackgroundResource(R.drawable.keyboard_buttons_custom);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color, getApplicationContext().getTheme()));
//        } else
//            createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));
//
//        searchForBackupData();
//    }
//
//    @Override
//    public void dataRestored(boolean b) {
//        Log.i(TAG, "dateRestored(): " + String.valueOf(b));
//        Toast dataRestoredToast;
//        if (b) {
//            statusTextView.setText(getResources().getString(R.string.abd_dataRestoredSuccessful_string));
//            dataRestoredToast = Toast.makeText(this, getResources().getString(R.string.abd_dataRestoredSuccessful_string), Toast.LENGTH_SHORT);
//        } else {
//            statusTextView.setText(getResources().getString(R.string.abd_dataNotRestored_string));
//            dataRestoredToast = Toast.makeText(this, getResources().getString(R.string.abd_dataNotRestored_string), Toast.LENGTH_SHORT);
//        }
//        dataRestoredToast.show();
//
//        createBackupDataButton.setEnabled(true);
//        createBackupDataButton.setBackgroundResource(R.drawable.keyboard_buttons_custom);
//        createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));
//
//        backupDataRecyclerViewAdapter.setClickListener(new AdapterActivityBackupDataRecyclerView.OnItemClickListener() {
//            @Override
//            public void onItemClick(View itemView, int position) {
//                onBackupItemClick(position);
//            }
//        });
//
//        arrowBackImageView.setEnabled(true);
//    }
//    // ===============================================================================
//
//
//    @Override
//    public void onBackPressed() {
//        returnToPreviousActivity();
//    }
}