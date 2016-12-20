package com.touristskaya.expenses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditCostsActivity extends AppCompatActivity implements MyDatePicker.MyDatePickerCallback {

    private String date;
    private String categoryName;
    private String costSum;
    private String note;
    private String dateInMilliseconds;
    private String[] availableCostNamesArrayWithID;
    private CostsDB db;
    private String[] bundleDataArray;
    private String dataForPreviousActivity;
    private double previousCostValue;

    Spinner availableCostNamesSpinner;
    Dialog currentDialog;
    EditText inputDataEditText, dateEditText,
             costSumEditText, noteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_costs);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Редактирование");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF9800")));

//        date = "3.05.1989";
//        categoryName = "First";
//        costSum = "125800.25 руб.";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.mm.yyyy");
//        try {
//            Date d = simpleDateFormat.parse(date);
//            dateInMilliseconds = String.valueOf(d.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        String dataString = "none";
        Bundle bundleData = getIntent().getExtras();
        if (bundleData != null)
            dataString = bundleData.getString("data");

        bundleDataArray = bundleData.getStringArray(Constants.DATA_ARRAY_LABEL);
        dataForPreviousActivity = bundleData.getString("dataForPreviousActivity");
        previousCostValue = 0.0;
        if (bundleDataArray != null)
            previousCostValue = Double.parseDouble(bundleDataArray[Constants.COST_VALUE_INDEX]);

        db = CostsDB.getInstance(this);
        long milliseconds = Long.parseLong(dataString.substring(dataString.lastIndexOf(Constants.SEPARATOR_MILLISECONDS) + 1));
        String[] dataArr = db.getCostByDateInMillis_V2(milliseconds);

        categoryName = dataArr[0];
        costSum = dataArr[1] + " руб.";
        date = dataArr[2] + "." + dataArr[3] + "." + dataArr[4];
        dateInMilliseconds = dataArr[5];
        note = dataArr[6];

        // Получаем список всех используемых категорий расходов и их id_n
        availableCostNamesArrayWithID = db.getActiveCostNames_V2();

        // Формируем массив, состоящий только из названий используемых
        // категорий расходов
        int availableCostNamesArrayCounter = 0;
        String[] availableCostNamesArray = new String[availableCostNamesArrayWithID.length / 2];
        for (int i = 0; i < availableCostNamesArrayWithID.length; i = i + 2)
            availableCostNamesArray[availableCostNamesArrayCounter++] = availableCostNamesArrayWithID[i];

        // Помещаем выбранную категрию расходов на первое место
        // в масисве названий используемых категорий расходов
        if (!availableCostNamesArray[0].equals(categoryName)) {
            for (int i = 0; i < availableCostNamesArray.length; ++i) {
                if (availableCostNamesArray[i].equals(categoryName)) {
                    availableCostNamesArray[i] = availableCostNamesArray[0];
                    break;
                }
            }
            availableCostNamesArray[0] = categoryName;
        }

        // Заполняем выпадающий список назвниями используемых статей расходов
        availableCostNamesSpinner = (Spinner) findViewById(R.id.editCosts_AvailableCostNames);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, availableCostNamesArray);
        availableCostNamesSpinner.setAdapter(adapter);

        // При нажатии на дату внесения значения по статье расходов,
        // появляется возможность изменения даты внесения значения расходов
        dateEditText = (EditText) findViewById(R.id.editCosts_costDate);
        dateEditText.setText(date);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatePicker datePicker = new MyDatePicker(EditCostsActivity.this, Long.valueOf(dateInMilliseconds));
                datePicker.show();
            }
        });

        // При нажатии на сумму расходов, появляется диалоговое окно,
        // в котором можно изменить сумму
        costSumEditText = (EditText) findViewById(R.id.editCosts_costSum);
        costSumEditText.setText(costSum);
        costSumEditText.setCursorVisible(false);
        costSumEditText.setFocusable(false);
        costSumEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(EditCostsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_cost_sum_popup);
                currentDialog = dialog;

                inputDataEditText = (EditText) dialog.findViewById(R.id.editCostsPopup_inputTextField);
                inputDataEditText.setFilters(new DecimalDigitsInputFilter[] {new DecimalDigitsInputFilter()});
                inputDataEditText.setCursorVisible(false);
                inputDataEditText.setText(costSumEditText.getText().toString().substring(0, costSumEditText.getText().toString().indexOf(" ")));

                dialog.show();
            }
        });

        // При нажатии на заметку, появляется окно, в котором можно изменить заметку
        noteEditText = (EditText) findViewById(R.id.editCosts_cost_note);
        noteEditText.setCursorVisible(false);
        noteEditText.setFocusable(false);
        if (note != null && !note.equals("null"))
            noteEditText.setText(note);
        noteEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EditCostsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_text_popup);

                final EditText inputTextField = (EditText) dialog.findViewById(R.id.addTextPopup_edit_text);
                inputTextField.requestFocus();
                inputTextField.setText(noteEditText.getText());
                inputTextField.setSelection(inputTextField.getText().length());
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
                        note = noteRaw;
                        noteEditText.setText(note);
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
            }
        });
    }


/*================================== Слушатели ==============================================*/

    // Обработчик нажатий кнопок в edit_cost_sum_popup (диалог редактирования значений расходов)
    public void onEditCostSumPopupClickListener(View view) {
        Button pressedButton = (Button) view;
        String buttonLabel = (String)pressedButton.getText();

        if (inputDataEditText == null) {
            inputDataEditText = (EditText) currentDialog.findViewById(R.id.editCostsPopup_inputTextField);
            inputDataEditText.setFilters(new DecimalDigitsInputFilter[]{new DecimalDigitsInputFilter()});
            inputDataEditText.setCursorVisible(false);
        }

        switch (view.getId()){
            case R.id.editCostsPopup_zero:
            case R.id.editCostsPopup_one:
            case R.id.editCostsPopup_two:
            case R.id.editCostsPopup_three:
            case R.id.editCostsPopup_four:
            case R.id.editCostsPopup_five:
            case R.id.editCostsPopup_six:
            case R.id.editCostsPopup_seven:
            case R.id.editCostsPopup_eight:
            case R.id.editCostsPopup_nine:
                inputDataEditText.append(buttonLabel);
                break;

            case R.id.editCostsPopup_dot: {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (!inputText.contains(".")) {
                    inputDataEditText.append(".");
                }
                break;
            }

            case R.id.editCostsPopup_del: {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (inputText != null && inputText.length() != 0) {
                    inputText = inputText.substring(0, inputText.length() - 1);
                    inputDataEditText.setText(inputText);
                }
                break;
            }

            case R.id.editCostsPopup_ok: {
                String inputText = String.valueOf(inputDataEditText.getText());
                if (inputText != null && inputText.length() != 0 && !".".equals(inputText)) {
                    Double enteredCostValue = Double.parseDouble(inputText);
                    costSumEditText.setText(Constants.formatDigit(enteredCostValue) + " руб.");
                    currentDialog.cancel();
                }
                break;
            }

            case R.id.editCostsPopup_cancelButton:
                currentDialog.cancel();
                break;

        }
    }

    // Обработчик нажатий кнопок сохранения введённых значений и отмены редактирования
    public void onSaveCancelButtonsClick(View view) {
        switch (view.getId()) {
            case R.id.editCosts_saveButton:
                boolean badData = false;

                String chosenCategoryName = availableCostNamesSpinner.getSelectedItem().toString();

                // Получаем id_n по названию выбранной категории расходов
                int id_n = -1;
                for (int i = 0; i < availableCostNamesArrayWithID.length; ++i) {
                    if (chosenCategoryName.equals(availableCostNamesArrayWithID[i]))
                        id_n = Integer.parseInt(availableCostNamesArrayWithID[i + 1]);
                }
                if (id_n == -1) {
                    Toast idErrorToast = Toast.makeText(this,
                            "ERROR RETRIEVING ID_N",
                            Toast.LENGTH_LONG);
                    idErrorToast.show();
                    return;
                }

                // Получаем введённую заметку
                String enteredNote = noteEditText.getText().toString();

                // Получаем значение введённой суммы расходов
                String enteredCostSum = costSumEditText.getText().toString().substring(0, costSumEditText.getText().toString().indexOf(" "));

                // Получаем выбранную дату
                String enteredDate = dateEditText.getText().toString();
                long enteredDateInMilliseconds = Long.valueOf(dateInMilliseconds);

                if (!enteredDate.equals(date)) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                    try {
                        Date d = simpleDateFormat.parse(enteredDate);
                        enteredDateInMilliseconds = d.getTime();
                    } catch (ParseException e) {
                        Toast invalidDataErrorToast = Toast.makeText(this,
                                "Неправильная дата",
                                Toast.LENGTH_LONG);
                        invalidDataErrorToast.show();
                        return;
                    }

                    if (enteredDateInMilliseconds > calendar.getTimeInMillis()) {
                        Toast wrongDateToast = Toast.makeText(this,
                                "Введённая дата ещё не наступила",
                                Toast.LENGTH_LONG);
                        wrongDateToast.show();
                        badData = true;
                    }
                }

                if (!badData) {
                    db.removeCostValue(Long.valueOf(dateInMilliseconds));
                    db.addCostInMilliseconds(id_n, enteredCostSum, enteredDateInMilliseconds, enteredNote);

                    Toast recordEditedSuccessfulToast = Toast.makeText(this,
                            "Запись изменена",
                            Toast.LENGTH_LONG);
                    recordEditedSuccessfulToast.show();
                }

                break;

            case R.id.editCosts_cancelButton:
                Intent intent = null;
                if (bundleDataArray != null) {
                    intent = new Intent(this, StatisticCostTypeDetailedActivity.class);
                    intent.putExtra(Constants.DATA_ARRAY_LABEL, bundleDataArray);
                    intent.putExtra("dataForPreviousActivity", dataForPreviousActivity);
                } else
                    intent = new Intent(this, MainActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

/*================================================================================================*/



    @Override
    public void getPickedDate(String pickedDate) {
        dateEditText.setText(pickedDate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = null;
                if (bundleDataArray != null) {
                    intent = new Intent(this, StatisticCostTypeDetailedActivity.class);
                    intent.putExtra(Constants.DATA_ARRAY_LABEL, bundleDataArray);
                    intent.putExtra("dataForPreviousActivity", dataForPreviousActivity);
                } else
                    intent = new Intent(this, MainActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}