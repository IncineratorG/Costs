package com.touristskaya.expenses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class ActivityBackupData extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, AsyncTaskSaveDeviceData.DataSavedCallback,
        AsyncTaskRestoreData.DataRestoredCallback {


    private static final String TAG = "tag";
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_CODE_RESOLUTION = 123;
    private DB_Costs cdb;
    private ImageView arrowBackImageView;
    
    private Button createBackupDataButton;

    private String TABLE_COST_NAMES_FILE_NAME = "cost_names_data.xml";
    private String TABLE_COST_VALUES_FILE_NAME = "cost_values_data.xml";
    private String DEVICE_BACKUP_FOLDER_NAME;
    private DriveId DEVICE_BACKUP_FOLDER_ID;
    private String REFERENCE_FILE_NAME = "reference_file";
    private DriveFolder DEVICE_BACKUP_FOLDER_FOLDER;

    private String ROOT_BACKUP_FOLDER_NAME = "EXPENSES_BACKUP";
    private DriveId ROOT_BACKUP_FOLDER_ID;
    private DriveFolder ROOT_BACKUP_FOLDER_FOLDER;
    private List<DataUnitBackupFolder> existingDeviceBackupFolders = new ArrayList<>();

    private Calendar calendar;
    private String BACKUP_USER_COMMENT = "";

    private RecyclerView backupListRecyclerView;
    private AdapterActivityBackupDataRecyclerView backupDataRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private TextView statusTextView;
//    private String currentDefaultString = "";

    private AsyncTask asyncTaskRestoreData;
    private ImageView selectGoogleAccountImageView;

    private boolean chooseAccountDialogShown = false;
    private boolean connectedToGoogleDrive = false;



    // ====================== ACTIVITY LIFECYCLE FUNCTIONS ======================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_data);


        calendar = new GregorianCalendar();
        cdb = DB_Costs.getInstance(this);


        linearLayoutManager = new LinearLayoutManager(this);
        backupListRecyclerView = (RecyclerView) findViewById(R.id.backup_data_recycler_view);

        // Кнопка создания резервной копии
        createBackupDataButton = (Button) findViewById(R.id.backup_data_backup_button);
        createBackupDataButton.setEnabled(false);
        createBackupDataButton.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
        createBackupDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBackupData_V2();
                createBackupDataButton.setEnabled(false);
                createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));
                if (backupDataRecyclerViewAdapter != null)
                    backupDataRecyclerViewAdapter.setClickListener(null);
            }
        });

        // При нажатии стрелки назад - возвращаемся к предыдущему экрану
        arrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);
        arrowBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToPreviousActivity();
            }
        });

        // Открываем диалоговое окно, в котором можно выбрать аккаунт Google
        selectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);
        selectGoogleAccountImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGoogleAccount();
            }
        });

        // Создаём googleApiClient
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

//        currentDefaultString = "Нет соединения с сетью";
        statusTextView = (TextView) findViewById(R.id.backup_data_status_textview);
        statusTextView.setText("Нет соединения с сетью");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!hasNetworkConnection()) {
//            currentDefaultString = "Нет соединения с сетью";
            statusTextView.setText("Нет соединения с сетью");
        } else
            connectToGoogleDrive();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (asyncTaskRestoreData != null)
            asyncTaskRestoreData.cancel(true);
        disconnectFromGoogleDrive();
    }

// ===========================================================================


    // ============================ MY FUNCTIONS =================================
    // Выбираем аккаунт Google
    private void selectGoogleAccount() {
        connectedToGoogleDrive = false;

        // Очищаем список доступных резервных копий
        if (existingDeviceBackupFolders != null)
            existingDeviceBackupFolders.clear();
        if (backupDataRecyclerViewAdapter != null)
            backupDataRecyclerViewAdapter.notifyDataSetChanged();

        // Отключаем возможность создания резервной копии
        createBackupDataButton.setEnabled(false);
        createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));

        // Показываем диалоговое окно с возможностью выбора аккаунта Google. Если пользователь ничего
        // не выберет, то повторно вызвать данное диалоговое окно можно либо нажав на значок выбора
        // аккаунта, либо заново зайдя в данную активность
        if (googleApiClient != null) {
            if (!chooseAccountDialogShown) {
                if (googleApiClient.isConnected())
                    googleApiClient.clearDefaultAccountAndReconnect();
            } else {
                chooseAccountDialogShown = false;
                connectToGoogleDrive();
            }
        }

    }

    // Проверяем есть ли соединение с интернетом
    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(ActivityBackupData.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null;
    }

    // Соединяемся с Google Drive
    private void connectToGoogleDrive() {
        if (googleApiClient != null) {
            statusTextView.setText("Устанавливаю соединение с сетью...");
            googleApiClient.connect();
        }
    }

    // Разрываем соединение с Google Drive
    private void disconnectFromGoogleDrive() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    // Восстанавливаем данные из резервной копии
    private void restoreDataFromBackup(int selectedBackupListPosition) {
        if (existingDeviceBackupFolders.size() == 0) {
            Log.i(TAG, "NO BACKUP FILES FOUND");
            return;
        }

        // Находим файлы резервной копии
        DriveFolder selectedBackupFolder = existingDeviceBackupFolders.get(selectedBackupListPosition).getDriveId().asDriveFolder();
        selectedBackupFolder.listChildren(googleApiClient).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                if (!metadataBufferResult.getStatus().isSuccess()) {
                    Log.i(TAG, "!!ERROR RETRIEVING BACKUP FILES!!");
                    return;
                }

                // Получаем файлы резервной копии
                DriveFile tableCostNamesBackupFile = null;
                DriveFile tableCostValuesBackupFile = null;
                MetadataBuffer backupFilesMetadata = metadataBufferResult.getMetadataBuffer();
                for (int i = 0; i < backupFilesMetadata.getCount(); ++i) {
                    if (TABLE_COST_NAMES_FILE_NAME.equals(backupFilesMetadata.get(i).getTitle()))
                        tableCostNamesBackupFile = backupFilesMetadata.get(i).getDriveId().asDriveFile();
                    else if (TABLE_COST_VALUES_FILE_NAME.equals(backupFilesMetadata.get(i).getTitle()))
                        tableCostValuesBackupFile = backupFilesMetadata.get(i).getDriveId().asDriveFile();
                }

                // Восстанавливаем данные из резервной копии в отдельном потоке
                asyncTaskRestoreData = new AsyncTaskRestoreData(googleApiClient,
                                        ActivityBackupData.this,
                                        tableCostNamesBackupFile,
                                        tableCostValuesBackupFile,
                                        statusTextView)
                        .execute();
            }
        });
    }

    // Создаём резервную копию данных
    private void createBackupData_V2() {
        // Формируем название новой папки резервной копии
        DEVICE_BACKUP_FOLDER_NAME = Build.MANUFACTURER + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + Build.MODEL + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + Build.ID + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + calendar.get(Calendar.DAY_OF_MONTH) + "."
                + calendar.get(Calendar.MONTH) + "."
                + calendar.get(Calendar.YEAR) + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + calendar.getTimeInMillis() + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + BACKUP_USER_COMMENT;

        // Если корневая папка создана - сохраняем папку резервной копии в ней.
        // Иначе - создаём корневую папку, в ней создаём папку резервной копии,
        // в этой папке создаём файлы резервной копии
        if (ROOT_BACKUP_FOLDER_FOLDER != null) {
            createDeviceBackup();
            return;
        }

        // Создаём корневую папку резервной копии
        Log.i(TAG, "CREATING ROOT BACKUP FOLDER");
        MetadataChangeSet rootBackupFolderMetadata = new MetadataChangeSet.Builder()
                .setTitle(ROOT_BACKUP_FOLDER_NAME)
                .setPinned(false)
                .build();

        Drive.DriveApi.getAppFolder(googleApiClient)
                .createFolder(googleApiClient, rootBackupFolderMetadata)
                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                    @Override
                    public void onResult(@NonNull DriveFolder.DriveFolderResult driveFolderResult) {
                        if (!driveFolderResult.getStatus().isSuccess()) {
                            Log.i(TAG, "ERROR CREATING ROOT BACKUP FOLDER");
                            dataSaved(false);
                            return;
                        }
                        Log.i(TAG, "ROOT BACKUP FOLDER CREATED");

                        ROOT_BACKUP_FOLDER_ID = driveFolderResult.getDriveFolder().getDriveId();
                        ROOT_BACKUP_FOLDER_FOLDER = driveFolderResult.getDriveFolder();

                        if (ROOT_BACKUP_FOLDER_FOLDER == null)
                            return;

                        createDeviceBackup();
                    }
                });

        Log.i(TAG, "CREATE_BACKUP_DATA FUNCTION END");
    }

    // Создаём папку с резервной копией данных. Расположена внутри корневой папки
    private void createDeviceBackup() {
        // Создаём папку резервной копии для текущего устройства
        Log.i(TAG, "CREATING DEVICE BACKUP FOLDER");
        MetadataChangeSet deviceBackupFolderMetadata = new MetadataChangeSet.Builder()
                .setTitle(DEVICE_BACKUP_FOLDER_NAME)
                .setPinned(false)
                .build();
        ROOT_BACKUP_FOLDER_FOLDER.createFolder(googleApiClient, deviceBackupFolderMetadata)
                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                    @Override
                    public void onResult(@NonNull DriveFolder.DriveFolderResult driveFolderResult) {
                        if (!driveFolderResult.getStatus().isSuccess()) {
                            Log.i(TAG, "ERROR CREATING DEVICE BACKUP FOLDER");
                            dataSaved(false);
                            return;
                        }

                        DEVICE_BACKUP_FOLDER_ID = driveFolderResult.getDriveFolder().getDriveId();
                        DEVICE_BACKUP_FOLDER_FOLDER = DEVICE_BACKUP_FOLDER_ID.asDriveFolder();
                        if (DEVICE_BACKUP_FOLDER_ID != null && DEVICE_BACKUP_FOLDER_FOLDER != null)
                            Log.i(TAG, "DEVICE BACKUP FOLDER CREATED");
                        else {
                            Log.i(TAG, "DEVICE_BACKUP_FOLDER_ID OR DEVICE_BACKUP_FOLDER_FOLDER IS NULL");
                            return;
                        }

                        // Создаём файлы резервной копии в отдельном потоке
                        Log.i(TAG, "ASYNC START");
                        new AsyncTaskSaveDeviceData(googleApiClient,
                                                    DEVICE_BACKUP_FOLDER_FOLDER,
                                                    ActivityBackupData.this,
                                                    TABLE_COST_NAMES_FILE_NAME,
                                                    REFERENCE_FILE_NAME,
                                                    TABLE_COST_VALUES_FILE_NAME,
                                                    statusTextView)
                                .execute();
                    }
                });

        Log.i(TAG, "CREATE_CURRENT_DEVICE_BACKUP FUNCTION END");
    }

    // Удаляем выбранный элемент списка резервных копий
    private void deleteBackupItem(int selectedBackupListPosition) {
        statusTextView.setText("Удаление...");

        DriveFolder folderToDelete = existingDeviceBackupFolders.get(selectedBackupListPosition).getDriveId().asDriveFolder();
        folderToDelete.delete(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (!status.isSuccess()) {
                    Toast errorDeletingBackupFolderToast = Toast.makeText(ActivityBackupData.this,
                            "При удалении возникла проблема", Toast.LENGTH_LONG);
//                    errorDeletingBackupFolderToast.setGravity(Gravity.CENTER, 0, 0);
                    errorDeletingBackupFolderToast.show();
                } else {
                    Toast backupFolderDeletedToast = Toast.makeText(ActivityBackupData.this,
                            "Резервная копия удалена", Toast.LENGTH_SHORT);
//                    backupFolderDeletedToast.setGravity(Gravity.CENTER, 0, 0);
                    backupFolderDeletedToast.show();
                }

//                statusTextView.setText(currentDefaultString);
                searchForBackupData();
            }
        });
    }

    // Ищем данные резервной копии
    private void searchForBackupData() {
        if (existingDeviceBackupFolders != null)
            existingDeviceBackupFolders.clear();
        if (backupDataRecyclerViewAdapter != null)
            backupDataRecyclerViewAdapter.notifyDataSetChanged();

        statusTextView.setText("Поиск резервных копий...");

        // Ищем папку с сохранёнными данными
        Query searchRootBackupFolder = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, ROOT_BACKUP_FOLDER_NAME))
                .build();
        Drive.DriveApi.query(googleApiClient, searchRootBackupFolder).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                if (!metadataBufferResult.getStatus().isSuccess()) {
                    Log.i(TAG, "ERROR TRYING TO FIND ROOT BACKUP FOLDER");
                    statusTextView.setText("ERROR TRYING TO FIND ROOT BACKUP FOLDER");
                    return;
                }

                MetadataBuffer metadataBuffer = metadataBufferResult.getMetadataBuffer();
                ROOT_BACKUP_FOLDER_ID = null;

                // Если папка не найдена - прекращаем обработку
                if (metadataBuffer.getCount() <= 0) {
                    Log.i(TAG, "NO BACKUP DATA FOUND");
//                    currentDefaultString = "Данные резервной копии не найдены";
                    statusTextView.setText("Резервные копии не найдены");
                    createBackupDataButton.setEnabled(true);
                    createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));
                    return;
                }

                // Считаем первую найденную не удалённую папку папкой с резервной копией данных
                for (int i = 0; i < metadataBuffer.getCount(); ++i) {
                    Metadata metadata = metadataBuffer.get(i);
                    if (!metadata.isTrashed()) {
                        ROOT_BACKUP_FOLDER_ID = metadata.getDriveId();
                        break;
                    }
                }
                if (ROOT_BACKUP_FOLDER_ID == null)
                    return;

                // Если папка с резервной копией найдена - ищем подпапки
                // резервной копии внутри этой папки
                Log.i(TAG, "ROOT BACKUP FOLDER FOUND");
//                currentDefaultString = "Папка резервной копии найдена";
//                statusTextView.setText("Данные резервной копии найдены");
                ROOT_BACKUP_FOLDER_FOLDER = ROOT_BACKUP_FOLDER_ID.asDriveFolder();
                ROOT_BACKUP_FOLDER_FOLDER.listChildren(googleApiClient).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                        if (!metadataBufferResult.getStatus().isSuccess()) {
                            Log.i(TAG, "ERROR LISTING ROOT BACKUP FOLDER CHILDREN");
                            statusTextView.setText("Ошибка поиска файлов резервной копии");
                            return;
                        }

                        existingDeviceBackupFolders = new ArrayList<>();
                        // Получаем названия и ID подпапок с резервными копиями
                        MetadataBuffer devicesBackupFoldersMetadata = metadataBufferResult.getMetadataBuffer();
                        for (int i = 0; i < devicesBackupFoldersMetadata.getCount(); ++i) {
                            Log.i(TAG, "FOLDER: " + devicesBackupFoldersMetadata.get(i).getTitle());

                            DataUnitBackupFolder backupTitle = new DataUnitBackupFolder();
                            backupTitle.setTitle(devicesBackupFoldersMetadata.get(i).getTitle());
                            backupTitle.setDriveId(devicesBackupFoldersMetadata.get(i).getDriveId());

                            existingDeviceBackupFolders.add(backupTitle);
                        }

                        // Отображаем полученный список резервных копий
                        backupListRecyclerView.setLayoutManager(linearLayoutManager);
                        backupDataRecyclerViewAdapter = new AdapterActivityBackupDataRecyclerView(ActivityBackupData.this, existingDeviceBackupFolders);
                        backupDataRecyclerViewAdapter.setClickListener(new AdapterActivityBackupDataRecyclerView.OnItemClickListener() {
                            @Override
                            public void onItemClick(View itemView, int position) {
                                onBackupItemClick(position);
                            }
                        });
                        backupListRecyclerView.setAdapter(backupDataRecyclerViewAdapter);

                        createBackupDataButton.setEnabled(true);
                        createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));

                        if (existingDeviceBackupFolders.size() > 0) {
                            statusTextView.setText("Резервные копии найдены");
                        } else {
                            statusTextView.setText("Резервные копии не найдены");
                        }
                    }
                });

            }
        });
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
        restoreButton.setText("Восстановить");
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenBackupItemDialog.dismiss();

                AlertDialog.Builder restoreFromChosenBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
                restoreFromChosenBackupItemDialogBuilder.setTitle("Внимние!");
                restoreFromChosenBackupItemDialogBuilder.setMessage("Все записи рвсходов будут заменены записями из выбранной резервной копии.");
                restoreFromChosenBackupItemDialogBuilder.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        backupDataRecyclerViewAdapter.setClickListener(null);

                        createBackupDataButton.setEnabled(false);
                        createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));

                        arrowBackImageView.setEnabled(false);

                        restoreDataFromBackup(position);
                    }
                });
                restoreFromChosenBackupItemDialogBuilder.setNegativeButton("Отмена", null);

                AlertDialog restoreFromChosenBackupItemDialog = restoreFromChosenBackupItemDialogBuilder.create();
                restoreFromChosenBackupItemDialog.show();
            }
        });

        // При нажатии кнопки "Удалить" появляется диалоговое окно, запрашивающее подтверждение удаления
        Button deleteButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_deleteButton);
        deleteButton.setText("Удалить");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenBackupItemDialog.dismiss();

                AlertDialog.Builder deleteBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
                deleteBackupItemDialogBuilder.setTitle("Внимние!");
                deleteBackupItemDialogBuilder.setMessage("Удалить выбранную резервную копию?");
                deleteBackupItemDialogBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBackupItem(position);
                    }
                });
                deleteBackupItemDialogBuilder.setNegativeButton("Отмена", null);

                AlertDialog deleteBackupItemDialog = deleteBackupItemDialogBuilder.create();
                deleteBackupItemDialog.show();
            }
        });

        Button cancelButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_cancelButton);
        cancelButton.setText("Отмена");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenBackupItemDialog.dismiss();
            }
        });
    }

    // Возвращаемся к предыдущему экрану
    private void returnToPreviousActivity() {
        if (asyncTaskRestoreData != null)
            asyncTaskRestoreData.cancel(true);
        Intent mainActivityWithFragmentsIntent = new Intent(ActivityBackupData.this, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityWithFragmentsIntent);
    }
    // ===========================================================================



    // ======================== ON_CONNECTION CALLBACKS ==============================
    // При соединении проверяем, есть ли на Гугл Диске данные резервной копии.
    // Если есть - инициализируем ID папки и файлов с резервной копией
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        chooseAccountDialogShown = false;
        statusTextView.setText("Соединение установлено");
//        createBackupDataButton.setEnabled(true);
//        createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));
        Log.i(TAG, "CONNECTED");
        Log.i(TAG, "TRYING TO FIND ROOT BACKUP FOLDER");

        // Ищем папку с сохранёнными данными
        searchForBackupData();
    }

    @Override
    public void onConnectionSuspended(int i) {
        statusTextView.setText("Соединение приостановлено");
        Log.i(TAG, "CONNECTION_SUSPENDED");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "CONNECTION_FAILED: " + connectionResult.toString());
        statusTextView.setText("Выберите учётную запись Google");

        // Окно с выбором аккаунта Google показывается только один раз.
        if (chooseAccountDialogShown)
            return;

        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }

        try {
            chooseAccountDialogShown = true;
            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "EXCEPTION WHILE STARING RESOLUTION ACTIVITY", e);
        }
    }
    // ===============================================================================



    // =================== SELECT GOOGLE ACCOUNT CALLBACK ============================
    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_RESOLUTION:

                System.out.println(resultCode);

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    // ===============================================================================



    // ============ AdapterActivityBackupDataRecyclerView, AsyncTaskRestoreData CALLBACK ===========
    @Override
    public void dataSaved(boolean dateSavedSuccessful) {
        Toast dataSavedToast;
        if (dateSavedSuccessful) {
            statusTextView.setText("Сохранение завершено");
            dataSavedToast = Toast.makeText(this, "Сохранение завершено", Toast.LENGTH_SHORT);
        } else {
            statusTextView.setText("Данные не сохранены!");
            dataSavedToast = Toast.makeText(this, "Данные не сохранены!", Toast.LENGTH_SHORT);
        }
        dataSavedToast.show();

        createBackupDataButton.setEnabled(true);
        createBackupDataButton.setBackgroundResource(R.drawable.keyboard_buttons_custom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color, getApplicationContext().getTheme()));
        } else
            createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));

        searchForBackupData();
    }

    @Override
    public void dataRestored(boolean b) {
        Log.i(TAG, "dateRestored(): " + String.valueOf(b));
        Toast dataRestoredToast;
        if (b) {
            statusTextView.setText("Данные восстановлены");
            dataRestoredToast = Toast.makeText(this, "Данные восстановлены", Toast.LENGTH_SHORT);
        } else {
            statusTextView.setText("Данные не восстановлены!");
            dataRestoredToast = Toast.makeText(this, "Данные не восстановлены!", Toast.LENGTH_SHORT);
        }
        dataRestoredToast.show();

        createBackupDataButton.setEnabled(true);
        createBackupDataButton.setBackgroundResource(R.drawable.keyboard_buttons_custom);
        createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));

        backupDataRecyclerViewAdapter.setClickListener(new AdapterActivityBackupDataRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                onBackupItemClick(position);
            }
        });

        arrowBackImageView.setEnabled(true);
    }
    // ===============================================================================


    @Override
    public void onBackPressed() {
        returnToPreviousActivity();
    }
}