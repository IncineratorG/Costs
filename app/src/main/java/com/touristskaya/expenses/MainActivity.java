package com.touristskaya.expenses;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

/*================================== Переменные ==================================================*/
    private static final String tag = "MainActivityTag";

    private CostsDB cdb;

    private int currentYear;
    private int currentMonth;                            // Начинается с нуля
    private int currentDay;
    private int todayMonth;
    private int todayYear;
    private int todayDay;
    private int chosenCostNameId;
    private double chosenCostTypeValue;
    private String currentOverallCosts;
    private String note;
    private String[] nonActiveCostNames;

    TextView currentDateTextViewMainActivity;
    TextView currentDayNumberTextView;
    TextView currentOverallCostsTextViewMainActivity;
    TextView currentDialogCostSumTextView;
    ListAdapter costsListViewMainActivityAdapter;
    ListView costsListViewMainActivity;
    Dialog currentDialog;
    TextView inputDataPopupNoteTextView;






/*================================================================================================*/





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Constants.HEADER_SYSTEM_COLOR);

//        format = NumberFormat.getInstance(Locale.UK);
//        format.setGroupingUsed(false);

        // Получаем доступ к базе данных
        if (cdb == null)
            cdb = CostsDB.getInstance(this);

        // Инициализируем поле, содержащее выбранные день, месяц и год
        if (currentDateTextViewMainActivity == null)
            currentDateTextViewMainActivity = (TextView) findViewById(R.id.currentDateMainActivity);
        if (currentDayNumberTextView == null)
            currentDayNumberTextView = (TextView) findViewById(R.id.dayNumberMainActivity);

        // Инициализируем поле, отображающее текущие расходы
        if (currentOverallCostsTextViewMainActivity == null)
            currentOverallCostsTextViewMainActivity = (TextView) findViewById(R.id.currentOverallCostsMainActivity);

        // Устанавливаем текущую дату
        setCurrentDate();

        // Устанавливаем суммарное значение расходов за текущий месяц,
        // формируем данные для costsListViewMainActivity - названия статей расходов и
        // значения по этим статьям за текущий месяц; устанавливаем слушатели на
        // нажатие элемента списка статей расходов (costsListViewMainActivity)
        setCurrentOverallCosts_V2();

        // Формируем данные для costsListViewMainActivity - названия статей расходов и
        // значения по этим статьям за текущий месяц; устанавливаем слушатели на
        // нажатие элемента списка статей расходов (costsListViewMainActivity)
//        createListViewContent();
    }





/*================================== Слушатели ===================================================*/

    // Обработчик нажатий кнопок в input_data_popup (диалог ввода значений расходов)
    public void onInputDataPopupClickListener(final View view) {
        Button pressedButton = (Button) view;
        String buttonLabel = (String)pressedButton.getText();

        final EditText inputDataEditText = (EditText) currentDialog.findViewById(R.id.inputTextFieldEditTextInInputDataPopup);
        inputDataEditText.setFilters(new DecimalDigitsInputFilter[] {new DecimalDigitsInputFilter()});
        inputDataEditText.setCursorVisible(false);

        switch (view.getId()){
            case R.id.zero:
            case R.id.one:
            case R.id.two:
            case R.id.three:
            case R.id.four:
            case R.id.five:
            case R.id.six:
            case R.id.seven:
            case R.id.eight:
            case R.id.nine:
                inputDataEditText.append(buttonLabel);
                break;

            case R.id.dot: {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (!inputText.contains(".")) {
                    inputDataEditText.append(".");
                }
                break;
            }

            case R.id.del: {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (inputText != null && inputText.length() != 0) {
                    inputText = inputText.substring(0, inputText.length() - 1);
                    inputDataEditText.setText(inputText);
                }
                break;
            }

            case R.id.ok: {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (inputText != null && inputText.length() != 0 && !".".equals(inputText)) {
                    Double enteredCostValue = Double.parseDouble(inputText);
                    chosenCostTypeValue = chosenCostTypeValue + enteredCostValue;

                    cdb.addCosts(enteredCostValue, chosenCostNameId, note);

                    // Обновляем главный экран приложения (MainActivity)
                    setCurrentOverallCosts_V2();

                    currentDialogCostSumTextView.setText(Constants.formatDigit(chosenCostTypeValue) + " руб.");
                    inputDataEditText.setText("");
                    inputDataPopupNoteTextView.setText("");
                    note = null;
                }
                break;
            }

            case R.id.inputDataPopup_cancelButton:
                note = null;
                currentDialog.cancel();
                break;

            case R.id.inputDataPopup_add_text_btn:
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_text_popup);

                final EditText inputTextField = (EditText) dialog.findViewById(R.id.addTextPopup_edit_text);
                inputTextField.requestFocus();
                if (note != null) {
                    inputTextField.setText(note);
                    inputTextField.setSelection(inputTextField.getText().length());
                }
                inputTextField.setCursorVisible(true);

                // Отображаем клавиатуру
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                Button addTextButton = (Button) dialog.findViewById(R.id.addTextPopup_add_text_btn);
                Button cancelButton = (Button) dialog.findViewById(R.id.addTextPopup_cancel_btn);

                addTextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String noteRaw = inputTextField.getText().toString();
                        if (noteRaw != null) {
                            if ("".equals(noteRaw)) {
                                if (inputDataPopupNoteTextView != null)
                                    inputDataPopupNoteTextView.setText("");
                                note = null;
                            } else {
                                note = noteRaw;
                                if (inputDataPopupNoteTextView != null) {
                                    inputDataPopupNoteTextView.setText(note);
                                }
                            }
                        }
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        dialog.cancel();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        dialog.cancel();
                    }
                });

                dialog.show();
                break;

        }
    }


    // При нажатии на пункт списка статей расходов появляется всплывающее окно,
    // в котором можно ввести значение расходов по выбранной статье. При нажатии
    // на пункт "Добавить новую категорию" появляется всплывающее окно, в котором
    // можно добавить название новой категории расходов
    public void onCostsListViewItemClick_V2(AdapterView<?> parent, View view, int position, long id) {
        String textLine = String.valueOf(parent.getItemAtPosition(position));

        // Нажатие на пункт "Добавить новую категорию"
        if ("+".equals(textLine.substring(textLine.lastIndexOf(Constants.SEPARATOR_ID) + 1))) {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.add_new_cost_type_popup);

            // Инициализируем поле ввода названия новой статьи расходов
            final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.costTypeTextViewInAddNewCostTypePopup);
            inputTextField.setCursorVisible(false);
            inputTextField.requestFocus();

            // Отображаем клавиатуру
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            ArrayAdapter<String> autoCompleteTextViewAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, nonActiveCostNames);
            inputTextField.setAdapter(autoCompleteTextViewAdapter);

            // Инициализируем кнопки всплывающего окна
            Button addNewCostTypeButton = (Button) dialog.findViewById(R.id.addNewCostTypeButton);
            Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

            // Устанавливаем слушатели на кнопки
            addNewCostTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newCostTypeName = inputTextField.getText().toString();

                    if (newCostTypeName.length() > 0) {
                        boolean costNameNotExist = cdb.addCostName(newCostTypeName);
                        if (costNameNotExist) {
                            Toast newCostTypeAddedToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' создана.", Toast.LENGTH_LONG);

                            // Обновляем главный экран приложения (MainActivity)
                            setCurrentOverallCosts_V2();

                            dialog.cancel();
                            newCostTypeAddedToast.show();
                        } else {
                            Toast newCostTypeAddedToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' уже создана.", Toast.LENGTH_LONG);
                            newCostTypeAddedToast.show();
                        }
                    }
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }
        // Нажатие на название категроии расходов
        else {
            // Получаем название и значение расходов по выбранной статье расходов
            String costName = textLine.substring(0, textLine.lastIndexOf(Constants.SEPARATOR_VALUE));
            String chosenCostTypeValueString = textLine.substring(
                    textLine.lastIndexOf(Constants.SEPARATOR_VALUE) + 1,
                    textLine.lastIndexOf(Constants.SEPARATOR_ID)
            );
            chosenCostTypeValueString = chosenCostTypeValueString.replaceAll(",", ".");
            chosenCostTypeValue = Double.parseDouble(chosenCostTypeValueString);

            // Устанавливаем ID выбранной статьи расходов
            chosenCostNameId = Integer.parseInt(textLine.substring(
                    textLine.lastIndexOf(Constants.SEPARATOR_ID) + 1
            ));

            // Инициализируем диалог ввода значения расходов
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.input_data_popup);
            currentDialog = dialog;

//            inputDataDialogNoteLayout = (LinearLayout) dialog.findViewById(R.id.inputDataPopup_note_layout);
//            inputDataDialogNoteLayout.setVisibility(View.GONE);
            inputDataPopupNoteTextView = (TextView) dialog.findViewById(R.id.inputDataPopup_note_text_view);
            note = null;
//            inputDataPopupNoteTextView.setVisibility(View.GONE);

            // Инициализируем поле с названием выбранной статьи расходов
            TextView chosenCostTypeNameTextView = (TextView) dialog.findViewById(R.id.costTypeTextViewInInputDataPopup);
            currentDialogCostSumTextView = (TextView) dialog.findViewById(R.id.inputDataPopup_costSum);
            currentDialogCostSumTextView.setText(Constants.formatDigit(chosenCostTypeValue) + " руб.");
            chosenCostTypeNameTextView.setText(costName);

            dialog.show();
        }
    }


    // При длительном нажатии на пункт списка статей расходов появляется всплывающее окно,
    // позволяющее редактировать название выбранной статьи расходов (удалить, переименовать)
    public boolean onCostsListViewItemLongClick_V2(AdapterView<?> parent, View view, int position, long id) {
        String textLine = String.valueOf(parent.getItemAtPosition(position));

        if (!"+".equals(textLine.substring(textLine.lastIndexOf(Constants.SEPARATOR_ID) + 1))) {
            final String chosenCostTypeName = textLine.substring(0, textLine.lastIndexOf(Constants.SEPARATOR_VALUE));
            final int chosenCostTypeId = Integer.parseInt(textLine.substring(
                    textLine.lastIndexOf(Constants.SEPARATOR_ID) + 1
            ));
            String chosenCostTypeValueString = textLine.substring(
                    textLine.lastIndexOf(Constants.SEPARATOR_VALUE) + 1,
                    textLine.lastIndexOf(Constants.SEPARATOR_ID)
            );
            chosenCostTypeValueString = chosenCostTypeValueString.replaceAll(",", ".");
            final Double chosenCostTypeValue = Double.parseDouble(chosenCostTypeValueString);

            final Dialog mainEditDialog = new Dialog(MainActivity.this);
            mainEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mainEditDialog.setContentView(R.layout.edit_cost_type_popup);

            // Устанавливаем поле с названием выбранной статьи расходов
            final EditText chosenCostTypeNameEditText = (EditText) mainEditDialog.findViewById(R.id.costTypeNameEditTextInEditCostTypePopup);
            chosenCostTypeNameEditText.setCursorVisible(false);
            chosenCostTypeNameEditText.setText(chosenCostTypeName);
            chosenCostTypeNameEditText.setEnabled(false);

            // Инициализируем кнопки всплывающего окна
            Button renameButton = (Button) mainEditDialog.findViewById(R.id.renameButton);
            Button deleteButton = (Button) mainEditDialog.findViewById(R.id.deleteButton);
            Button cancelButton = (Button) mainEditDialog.findViewById(R.id.cancelButton);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainEditDialog.cancel();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chosenCostTypeValue < 0.01) {
                        // Диалоговое окно, запрашивающее подтверждение на удаление
                        // выбранного элемента контекстного меню. При нажатии на кнопку "Удалить"
                        // происходит удаление выбранного элемента из базы данных и обновление
                        // текущей суммы расходов по данной категории
                        AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
                        adBuilder.setNegativeButton("Отмена", null);
                        adBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean result = cdb.deleteCostName(chosenCostTypeId);
                                if (result) {

                                    Toast costTypeDeletedToast = Toast.makeText(MainActivity.this,
                                            "Категория '" + chosenCostTypeName + "' удалена.",
                                            Toast.LENGTH_LONG);
                                    costTypeDeletedToast.show();

                                    // Обновляем главный экран приложения (MainActivity)
                                    setCurrentOverallCosts_V2();

                                    // Закрываем окно редактирования
                                    mainEditDialog.cancel();
                                } else {
                                    Toast errorDeletingCostTypeToast = Toast.makeText(MainActivity.this,
                                            "Не удалось удалить категорию " + chosenCostTypeName + ".",
                                            Toast.LENGTH_LONG);
                                    errorDeletingCostTypeToast.show();
                                }
                            }
                        });
                        adBuilder.setMessage("Удалить категорию \"" + chosenCostTypeName + "\" ?");

                        AlertDialog dialog = adBuilder.create();
                        dialog.show();

                        TextView dialogText = (TextView) dialog.findViewById(android.R.id.message);
                        dialogText.setGravity(Gravity.CENTER);
                    } else {
                        Toast hasValuesInCostTypeToast = Toast.makeText(MainActivity.this,
                                "Нельзя удалить категорию, по которой присутствуют записи в текущем месяце",
                                Toast.LENGTH_LONG);
                        hasValuesInCostTypeToast.show();
                    }
                }
            });

            renameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.add_new_cost_type_popup);

                    // Инициализируем поле ввода названия новой статьи расходов
                    final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.costTypeTextViewInAddNewCostTypePopup);
                    inputTextField.setCursorVisible(true);
                    inputTextField.setText(chosenCostTypeName);
                    inputTextField.setSelection(inputTextField.getText().length());

                    // Инициализируем кнопки всплывающего окна
                    Button renameButton = (Button) dialog.findViewById(R.id.addNewCostTypeButton);
                    renameButton.setText("Переименовать");
                    Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                    // Отображаем клавиатуру
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    // Устанавливаем слушатели на кнопки
                    renameButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newCostTypeName = inputTextField.getText().toString();

                            if (newCostTypeName.length() > 0) {
                                int renameResult = cdb.renameCostName(chosenCostTypeId, newCostTypeName);
                                if (renameResult == 0) {
                                    Toast renameToast = Toast.makeText(MainActivity.this,
                                            "Категория '" + newCostTypeName + "' переименована.",
                                            Toast.LENGTH_LONG);

                                    // Обновляем главный экран приложения (MainActivity)
                                    setCurrentOverallCosts_V2();

                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                    dialog.cancel();
                                    renameToast.show();
                                    mainEditDialog.cancel();
                                } else if (renameResult == 1){
                                    Toast newCostTypeErrorToast = Toast.makeText(MainActivity.this,
                                            "Категорию '" + newCostTypeName + "' не " +
                                            "возможно создать, так как в программе присутствуют записи по категории с таким названием.",
                                            Toast.LENGTH_LONG);
                                    newCostTypeErrorToast.show();
                                } else if (renameResult == 2) {
                                    Toast newCostTypeErrorToast = Toast.makeText(MainActivity.this,
                                            "Категория '" + newCostTypeName + "' уже создана.",
                                            Toast.LENGTH_LONG);
                                    newCostTypeErrorToast.show();
                                }
                            }
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
            });

            mainEditDialog.show();
        }

        return true;
    }


    // При нажатии на суммарное значение расходов за текущий месяц появляется список,
    // содержащий последние тридцать введённых значений. При нажатии на элемент этого
    // списка появляется всплывающее окно, предлагающее удалить или изменить выбранную запись
    public void onOverallCostsValueClick_V2(View view) {
        int numberOfLastEntriesToShow = 30;

        String[] lastEnteredValuesRaw = cdb.getLastEntries_V2(numberOfLastEntriesToShow);
        final String[] lastEnteredValuesFinal = new String[lastEnteredValuesRaw.length / 6];
        StringBuilder sb = new StringBuilder();

        PopupMenu lastEntriesPopupMenu = new PopupMenu(this, currentOverallCostsTextViewMainActivity);
        int indexCounter = 0;
        for (int i = 0; i < lastEnteredValuesRaw.length; i = i + 6) {
            sb.append(lastEnteredValuesRaw[i]);         // день
            sb.append(".");
            sb.append(lastEnteredValuesRaw[i + 1]);     // месяц
            sb.append(" ");
            sb.append(lastEnteredValuesRaw[i + 2]);     // название
            sb.append(": ");
            sb.append(lastEnteredValuesRaw[i + 3]);     // сумма
            sb.append(" руб.");
            sb.append(Constants.SEPARATOR_NOTE);
            sb.append(lastEnteredValuesRaw[i + 5]);
            sb.append(Constants.SEPARATOR_MILLISECONDS);
            sb.append(lastEnteredValuesRaw[i + 4]);
            lastEnteredValuesFinal[indexCounter++] = sb.toString();

            lastEntriesPopupMenu.getMenu().add(1, indexCounter, indexCounter,
                    lastEnteredValuesFinal[indexCounter - 1].substring(
                    0, lastEnteredValuesFinal[indexCounter - 1].lastIndexOf(Constants.SEPARATOR_NOTE)
            ));
            sb.setLength(0);
        }

        lastEntriesPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                final int itemPositionInLastEnteredValuesArray = item.getItemId() - 1;

                // Диалоговое окно, позволяющее удалить или изменить выбранную запись
                CustomDialogClass customDialog = new CustomDialogClass(MainActivity.this, lastEnteredValuesFinal[itemPositionInLastEnteredValuesArray]);
                customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Обновляем главный экран приложения (MainActivity)
                        setCurrentOverallCosts_V2();
                    }
                });
                customDialog.show();

                return true;
            }
        });


        lastEntriesPopupMenu.show();
    }

/*================================================================================================*/





/*==================================== Функции ===================================================*/

    // Устанавливает текущие дату и время
    public void setCurrentDate() {
        Calendar c = Calendar.getInstance();
        currentYear = todayYear = c.get(Calendar.YEAR);
        currentMonth = todayMonth = c.get(Calendar.MONTH);
        currentDay = todayDay = c.get(Calendar.DAY_OF_MONTH);

        currentDateTextViewMainActivity.setText(Constants.MONTH_NAMES[currentMonth] + " " + currentYear);
        currentDayNumberTextView.setText("День " + Constants.DAY_NAMES_ORDINAL[currentDay].toLowerCase());
    }


    // Устанавливает суммарное значение расходов за текущий месяц,
    // формирует данные для costsListViewMainActivity - названия статей расходов и
    // значения по этим статьям за текущий месяц; устанавливает слушатели на
    // нажатие элемента списка статей расходов (costsListViewMainActivity)
    public void setCurrentOverallCosts_V2() {
        String[] tableCostNamesContent = cdb.getActiveCostNames_V2();
        String[] costsArray_V2 = new String[tableCostNamesContent.length / 2 + 1];

        // Получаем массив не активных (удалённых) названий статей расходов
        nonActiveCostNames = cdb.getNonActiveCostNames();

        StringBuilder sb = new StringBuilder(5);
        Double totalCostsValue = 0.0;
        int costsArrayCounter = 0;
        for (int i = 0; i < tableCostNamesContent.length; i = i + 2) {
            int id_n = Integer.parseInt(tableCostNamesContent[i + 1]);
            Double costValue = cdb.getCostValue(-1, currentMonth, currentYear, id_n);
            sb.append(tableCostNamesContent[i]);
            sb.append(Constants.SEPARATOR_VALUE);
            sb.append(Constants.formatDigit(costValue));
            sb.append(Constants.SEPARATOR_ID);
            sb.append(id_n);

            costsArray_V2[costsArrayCounter++] = sb.toString();
            sb.setLength(0);

            totalCostsValue = totalCostsValue + costValue;
        }
        costsArray_V2[costsArrayCounter] = "Добавить новую категорию" +
                Constants.SEPARATOR_VALUE + Integer.MIN_VALUE +
                Constants.SEPARATOR_ID + "+";

        costsListViewMainActivityAdapter = new MainAdapter(this, costsArray_V2);

        costsListViewMainActivity = (ListView) findViewById(R.id.costsListViewMainActivity);
        costsListViewMainActivity.setAdapter(costsListViewMainActivityAdapter);
        costsListViewMainActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onCostsListViewItemClick_V2(parent, view, position, id);
            }
        });
        costsListViewMainActivity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return onCostsListViewItemLongClick_V2(parent, view, position, id);
            }
        });

        // Устанавливаем суммарное значение расходов за текущий месяц
        currentOverallCosts = Constants.formatDigit(totalCostsValue);
        currentOverallCostsTextViewMainActivity.setText(currentOverallCosts + " руб.");
    }

/*================================================================================================*/





/*============================== Переопределённые слушатели ======================================*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            nearestEventShown = null;
//        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(1, 1, 1, "Просмотр статистики");
        menu.add(1, 2, 2, "Последние введённые значения");
//        menu.add(1, 3, 3, "Редактировать название статей");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Переход на экран просмотра статистики расходов
        if (id == 1) {
            Intent statisticMainScreenIntent = new Intent(MainActivity.this, StatisticMainScreenActivity.class);
            startActivity(statisticMainScreenIntent);
        }

        // Просмотр последних введённых значений
        if (id == 2) {
            onOverallCostsValueClick_V2(currentOverallCostsTextViewMainActivity);
        }

//        // Редактирование названия статей расходов
//        if (id == 3) {
//            Toast notImplementedToast = Toast.makeText(MainActivity.this, "Не реализовано.", Toast.LENGTH_LONG);
//            notImplementedToast.show();
//        }

        return super.onOptionsItemSelected(item);
    }

/*================================================================================================*/





































/*================================================================================================*/
/*======================================= OLD ====================================================*/
//    public void onCostsListViewItemClick(AdapterView<?> parent, View view, int position, long id) {
//        String textLine = String.valueOf(parent.getItemAtPosition(position));
//
//        // Нажатие на пункт "Добавить новую категорию"
//        if ("+".equals(String.valueOf(textLine.charAt(textLine.length() - 1)))) {
//            final Dialog dialog = new Dialog(MainActivity.this);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.add_new_cost_type_popup);
//
//            // Инициализируем поле ввода названия новой статьи расходов
//            final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.costTypeTextViewInAddNewCostTypePopup);
//            inputTextField.setCursorVisible(false);
//
//            ArrayAdapter<String> autoCompleteTextViewAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, nonActiveCostNames);
//            inputTextField.setAdapter(autoCompleteTextViewAdapter);
//
//            // Инициализируем кнопки всплывающего окна
//            Button addNewCostTypeButton = (Button) dialog.findViewById(R.id.addNewCostTypeButton);
//            Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
//
//            // Устанавливаем слушатели на кнопки
//            addNewCostTypeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String newCostTypeName = inputTextField.getText().toString();
//
//                    if (newCostTypeName.length() > 0) {
//                        boolean costNameNotExist = cdb.addCostName(newCostTypeName);
//                        if (costNameNotExist) {
//                            Toast newCostTypeAddedToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' создана.", Toast.LENGTH_LONG);
//
//                            // Обновляем главный экран приложения (MainActivity)
//                            setCurrentOverallCosts();
//                            createListViewContent();
//
//                            dialog.cancel();
//                            newCostTypeAddedToast.show();
//                        } else {
//                            Toast newCostTypeAddedToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' уже создана.", Toast.LENGTH_LONG);
//                            newCostTypeAddedToast.show();
//                        }
//                    }
//                }
//            });
//
//            cancelButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.cancel();
//                }
//            });
//
//            dialog.show();
//        }
//        // Нажатие на название категроии расходов
//        else {
//            // Получаем название и значение расходов по выбранной статье расходов
//            String[] textLineData = textLine.split("\\$");
//            String costName = textLineData[1];
//            chosenCostTypeValue = Double.parseDouble(textLineData[2]);
//
//            // Устанавливаем ID выбранной статьи расходов
//            chosenCostNameId = Integer.parseInt(textLineData[0]);
//
//            // Инициализируем диалог ввода значения расходов
//            final Dialog dialog = new Dialog(MainActivity.this);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.input_data_popup);
//            currentDialog = dialog;
//
//            // Инициализируем поле с названием выбранной статьи расходов
//            TextView chosenCostTypeNameTextView = (TextView) dialog.findViewById(R.id.costTypeTextViewInInputDataPopup);
//            currentDialogCostSumTextView = (TextView) dialog.findViewById(R.id.inputDataPopup_costSum);
//            currentDialogCostSumTextView.setText(textLineData[2] + " руб.");
//            chosenCostTypeNameTextView.setText(costName);
//
//            dialog.show();
//        }
//    }

//    public boolean onCostsListViewItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        String textLine = String.valueOf(parent.getItemAtPosition(position));
//        final String[] textLineData = textLine.split("\\$");
//
//        if (!"+".equals(String.valueOf(textLine.charAt(textLine.length() - 1)))) {
//            final String chosenCostTypeName = textLineData[1];
//
//            final Dialog mainEditDialog = new Dialog(MainActivity.this);
//            mainEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            mainEditDialog.setContentView(R.layout.edit_cost_type_popup);
//
//            // Устанавливаем поле с названием выбранной статьи расходов
//            final EditText chosenCostTypeNameEditText = (EditText) mainEditDialog.findViewById(R.id.costTypeNameEditTextInEditCostTypePopup);
//            chosenCostTypeNameEditText.setCursorVisible(false);
//            chosenCostTypeNameEditText.setText(chosenCostTypeName);
//            chosenCostTypeNameEditText.setEnabled(false);
//
//            // Инициализируем кнопки всплывающего окна
//            Button renameButton = (Button) mainEditDialog.findViewById(R.id.renameButton);
//            Button deleteButton = (Button) mainEditDialog.findViewById(R.id.deleteButton);
//            Button cancelButton = (Button) mainEditDialog.findViewById(R.id.cancelButton);
//
//            cancelButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mainEditDialog.cancel();
//                }
//            });
//
//            deleteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Диалоговое окно, запрашивающее подтверждение на удаление
//                    // выбранного элемента контекстного меню. При нажатии на кнопку "Удалить"
//                    // происходит удаление выбранного элемента из базы данных и обновление
//                    // текущей суммы расходов по данной категории
//                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
//                    adBuilder.setNegativeButton("Отмена", null);
//                    adBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            boolean result = cdb.deleteCostName(Integer.parseInt(textLineData[0]));
//                            if (result) {
//
//                                Toast costTypeDeletedToast = Toast.makeText(MainActivity.this, "Категория '" + chosenCostTypeName + "' удалена.", Toast.LENGTH_LONG);
//                                costTypeDeletedToast.show();
//
//                                // Обновляем главный экран приложения (MainActivity)
//                                setCurrentOverallCosts();
//                                createListViewContent();
//
//                                // Закрываем окно редактирования
//                                mainEditDialog.cancel();
//                            } else {
//                                Toast errorDeletingCostTypeToast = Toast.makeText(MainActivity.this, "Не удалось удалить категорию " + chosenCostTypeName + ".", Toast.LENGTH_LONG);
//                                errorDeletingCostTypeToast.show();
//                            }
//                        }
//                    });
//                    adBuilder.setMessage("Удалить категорию \"" + textLineData[1] + "\" ?");
//
//                    AlertDialog dialog = adBuilder.create();
//                    dialog.show();
//
//                    TextView dialogText = (TextView) dialog.findViewById(android.R.id.message);
//                    dialogText.setGravity(Gravity.CENTER);
//                }
//            });
//
//            renameButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    final Dialog dialog = new Dialog(MainActivity.this);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.add_new_cost_type_popup);
//
//                    // Инициализируем поле ввода названия новой статьи расходов
//                    final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.costTypeTextViewInAddNewCostTypePopup);
//                    inputTextField.setCursorVisible(true);
//                    inputTextField.setText(textLineData[1]);
//
//                    // Инициализируем кнопки всплывающего окна
//                    Button renameButton = (Button) dialog.findViewById(R.id.addNewCostTypeButton);
//                    renameButton.setText("Переименовать");
//                    Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
//
//                    // Устанавливаем слушатели на кнопки
//                    renameButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String newCostTypeName = inputTextField.getText().toString();
//
//                            if (newCostTypeName.length() > 0) {
//                                int renameResult = cdb.renameCostName(Integer.parseInt(textLineData[0]), newCostTypeName);
//                                if (renameResult == 0) {
//                                    Toast renameToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' переименована.", Toast.LENGTH_LONG);
//
//                                    // Обновляем главный экран приложения (MainActivity)
//                                    setCurrentOverallCosts();
//                                    createListViewContent();
//
//                                    dialog.cancel();
//                                    renameToast.show();
//                                    mainEditDialog.cancel();
//                                } else if (renameResult == 1){
//                                    Toast newCostTypeErrorToast = Toast.makeText(MainActivity.this, "Категорию '" + newCostTypeName + "' не " +
//                                            "возможно создать, так как в программе присутствуют записи по категории с таким названием.", Toast.LENGTH_LONG);
//                                    newCostTypeErrorToast.show();
//                                } else if (renameResult == 2) {
//                                    Toast newCostTypeErrorToast = Toast.makeText(MainActivity.this, "Категория '" + newCostTypeName + "' уже создана.", Toast.LENGTH_LONG);
//                                    newCostTypeErrorToast.show();
//                                }
//                            }
//                        }
//                    });
//
//                    cancelButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.cancel();
//                        }
//                    });
//
//                    dialog.show();
//                }
//            });
//
//            mainEditDialog.show();
//        }
//
//        return true;
//    }

//    public void onOverallCostsValueClick(View view) {
//        int numberOfLastEntriesToShow = 30;
//
//        final String[] lastEnteredValues = cdb.getLastEntries(numberOfLastEntriesToShow);
//
//        PopupMenu lastEntriesPopupMenu = new PopupMenu(this, currentOverallCostsTextViewMainActivity);
//        for (int i = 0; i < lastEnteredValues.length; ++i)
//            lastEntriesPopupMenu.getMenu().add(1, i + 1, i + 1, lastEnteredValues[i].substring(0, lastEnteredValues[i].indexOf("%")));
//
//        lastEntriesPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                final int itemPositionInLastEnteredValuesArray = item.getItemId() - 1;
//
//                // Диалоговое окно, позволяющее удалить или изменить выбранную запись
//                CustomDialogClass customDialog = new CustomDialogClass(MainActivity.this, lastEnteredValues[itemPositionInLastEnteredValuesArray]);
//                customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        // Обновляем главный экран приложения (MainActivity)
//                        setCurrentOverallCosts();
//                        createListViewContent();
//                    }
//                });
//                customDialog.show();
//
//                return true;
//            }
//        });
//
//
//        lastEntriesPopupMenu.show();
//    }

//    public void setCurrentOverallCosts() {
//        List<String> tableCostNamesContent = cdb.getActiveCostNames();
//        costsMap = new HashMap<>();
//
//        // Получаем массив не активных (удалённых) названий статей расходов
//        nonActiveCostNames = cdb.getNonActiveCostNames();
//
//        Double totalCostsValue = 0.0;
//        for (String costNameRaw : tableCostNamesContent) {
//            int id_n = Integer.parseInt(costNameRaw.substring(0, costNameRaw.indexOf("$")));
//            Double costValue = cdb.getCostValue(-1, currentMonth, currentYear, id_n);
//            totalCostsValue = totalCostsValue + costValue;
//            costsMap.put(costNameRaw, costValue);
//        }
//
//        // Устанавливаем суммарное значение расходов за текущий месяц
//        currentOverallCosts = format.format(totalCostsValue);
//        currentOverallCostsTextViewMainActivity.setText(currentOverallCosts + " руб.");
//    }

    // Инициализирует costsListViewMainActivity и заполняет его
    // данными (названия статей расходов и значения по этим статьям
    // за текущий месяц); устанавливает слушатель на
    // нажатие элемента списка статей расходов (costsListViewMainActivity)
//    public void createListViewContent() {
//        costsArray = createCostsArray();
//
//        costsListViewMainActivityAdapter = new NewCostsListViewAdapter(this, costsArray);
//
//        costsListViewMainActivity = (ListView) findViewById(R.id.costsListViewMainActivity);
//        costsListViewMainActivity.setAdapter(costsListViewMainActivityAdapter);
//        costsListViewMainActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                onCostsListViewItemClick(parent, view, position, id);
//            }
//        });
//        costsListViewMainActivity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                return onCostsListViewItemLongClick(parent, view, position, id);
//            }
//        });
//    }

    // Создаёт массив, состоящий из строк с названиями статей расходов и значениями по этим статьям
    // за текущий месяц, разделённые символом "$". Последним элементом созданного массива всегда
    // идёт строка "Добавить новую категорию$+".
//    public String[] createCostsArray() {
//        List<String> listOfCostNames = new ArrayList<>();
//
//        for (Map.Entry<String, Double> entry : costsMap.entrySet())
//            listOfCostNames.add(entry.getKey() + "$" + format.format(entry.getValue()));
//
//
//        String[] costsArray = new String[listOfCostNames.size() + 1];
//        listOfCostNames.toArray(costsArray);
//
//        costsArray[costsArray.length - 1] = Integer.MAX_VALUE + "$Добавить новую категорию$+";
//
//        // Сортируем массив в соответсвие с частотой использования каждого элемента массива
////        OrderCostsArray(costsArray);
//
//        return costsArray;
//    }
}