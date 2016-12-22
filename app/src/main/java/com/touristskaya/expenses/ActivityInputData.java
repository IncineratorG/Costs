package com.touristskaya.expenses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;


public class ActivityInputData extends AppCompatActivity implements MyDatePicker.MyDatePickerCallback {

    private TextView signTextView;
    private EditText inputValueEditText, inputNoteEditText;
    private Button yesterdayButton, todayButton, chooseDateButton;
    private String pickedDate;
    private String lastEnteredCharacter, currentString, previousValueString;
    private LinearLayout inputValueEditTextCursor;
    private boolean plusPressed;
    private int costID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        // Получаем данные о выбранной статье расходов
        Bundle expenseDataBundle = getIntent().getExtras();
        if (expenseDataBundle == null)
            return;

        String[] expenseDataArray = expenseDataBundle.getStringArray(Constants.DATA_ARRAY_LABEL);
        if (expenseDataArray == null || expenseDataArray.length != Constants.DATA_ARRAY_SIZE)
            return;

        String costNameString = expenseDataArray[Constants.COST_NAME_INDEX];
        String costIdString = expenseDataArray[Constants.COST_ID_INDEX];
        costID = Integer.parseInt(costIdString);
        String costValueString = expenseDataArray[Constants.COST_VALUE_INDEX];
        String costNoteString = expenseDataArray[Constants.COST_NOTE_INDEX];

        // Инициализируем элементы интерфейса
        signTextView = (TextView) findViewById(R.id.activity_input_data_sign_textview);
        signTextView.setText("");

        inputValueEditTextCursor = (LinearLayout) findViewById(R.id.activity_input_data_edit_text_cursor);
        inputValueEditTextCursor.setAnimation(startBlinking());

        inputValueEditText = (EditText) findViewById(R.id.activity_input_data_input_value_edittext);
        inputValueEditText.setFilters(new DecimalDigitsInputFilter[] {new DecimalDigitsInputFilter()});
        inputNoteEditText = (EditText) findViewById(R.id.activity_input_data_input_note_edittext);
        if (costNoteString != null && costNoteString.length() > 0) {
            inputNoteEditText.setText(costNoteString);
            inputNoteEditText.setSelection(inputNoteEditText.getText().length());
        }

        yesterdayButton = (Button) findViewById(R.id.activity_input_data_date_yesterday_button);
        todayButton = (Button) findViewById(R.id.activity_input_data_date_today_button);
        chooseDateButton = (Button) findViewById(R.id.activity_input_data_choose_date_button);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(costNameString + ": " + costValueString + " руб.");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Constants.HEADER_SYSTEM_COLOR));
//        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_input_data_toolbar);
//        setSupportActionBar(toolbar);

        currentString = previousValueString = "";
        plusPressed = false;
    }

    // Обработчик нажатий кнопок выбора даты внесения расходов
    public void onDateButtonsClick(View view) {
        switch (view.getId()) {
            case R.id.activity_input_data_date_yesterday_button:
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                final long milliseconds = calendar.getTimeInMillis();

                if ("".equals(previousValueString))
                    saveData(milliseconds);
                else {
                    String currentValueString = inputValueEditText.getText().toString();
                    double previousValue = 0.0;
                    double currentValue = 0.0;
                    if (!".".equals(previousValueString))
                        previousValue = Double.parseDouble(previousValueString);
                    if (!".".equals(currentValueString) && !"".equals(currentValueString))
                        currentValue = Double.parseDouble(currentValueString);
                    currentValue = currentValue + previousValue;

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    String message = "Сохранить " +
                            previousValueString + " + " + currentValueString +
                            " = " + currentValue + " .руб ?";

                    dialogBuilder.setMessage(message);
                    dialogBuilder.setCancelable(true);
                    dialogBuilder.setNegativeButton("Отмена", null);
                    final double finalCurrentValue = currentValue;
                    final double finalCurrentValue1 = currentValue;
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveData(finalCurrentValue, milliseconds);

                            Intent intent = new Intent(ActivityInputData.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("result", true);
                            intent.putExtra("value", Constants.formatDigit(finalCurrentValue1));
                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }
                break;
            case R.id.activity_input_data_date_today_button:
                if ("".equals(previousValueString))
                    saveData();
                else {
                    String currentValueString = inputValueEditText.getText().toString();
                    double previousValue = 0.0;
                    double currentValue = 0.0;
                    if (!".".equals(previousValueString))
                        previousValue = Double.parseDouble(previousValueString);
                    if (!".".equals(currentValueString) && !"".equals(currentValueString))
                        currentValue = Double.parseDouble(currentValueString);
                    currentValue = currentValue + previousValue;

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    String message = "Сохранить " +
                            previousValueString + " + " + currentValueString +
                            " = " + currentValue + " .руб ?";

                    dialogBuilder.setMessage(message);
                    dialogBuilder.setCancelable(true);
                    dialogBuilder.setNegativeButton("Отмена", null);
                    final double finalCurrentValue = currentValue;
                    final double finalCurrentValue1 = currentValue;
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveData(finalCurrentValue);

                            Intent intent = new Intent(ActivityInputData.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("result", true);
                            intent.putExtra("value", Constants.formatDigit(finalCurrentValue1));
                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }
                break;
            case R.id.activity_input_data_choose_date_button:
                MyDatePicker datePicker = new MyDatePicker(ActivityInputData.this);
                datePicker.show();
                break;
        }
    }

    // Обработчик нажатий кнопок цифровой клавиатуры
    public void onKeyboardClick(View view) {
        if (inputValueEditText == null)
            return;

        boolean previousWasPlus = false;
        if (plusPressed) {
            inputValueEditText.setText("");
            plusPressed = false;
            previousWasPlus = true;
        }

        Button pressedButton = (Button) view;
        String buttonLabel = String.valueOf(pressedButton.getText());
        String inputTextString = "";

        switch (view.getId()) {
            case R.id.activity_input_data_zero:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if (!"0".equals(inputTextString)) {
                    inputValueEditText.append(buttonLabel);
                    lastEnteredCharacter = buttonLabel;
                }

                break;
            case R.id.activity_input_data_one:
            case R.id.activity_input_data_two:
            case R.id.activity_input_data_three:
            case R.id.activity_input_data_four:
            case R.id.activity_input_data_five:
            case R.id.activity_input_data_six:
            case R.id.activity_input_data_seven:
            case R.id.activity_input_data_eight:
            case R.id.activity_input_data_nine:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if ("0".equals(inputTextString)) {
                    inputValueEditText.setText(buttonLabel);
                    lastEnteredCharacter = buttonLabel;
                }
                else {
                    inputValueEditText.append(buttonLabel);
                    lastEnteredCharacter = buttonLabel;
                }
                break;
            case R.id.activity_input_data_dot:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if (!inputTextString.contains(".")) {
                    inputValueEditText.append(".");
                    lastEnteredCharacter = ".";
                }
                break;
            case R.id.activity_input_data_del:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if (inputTextString != null && inputTextString.length() != 0) {
                    inputTextString = inputTextString.substring(0, inputTextString.length() - 1);
                    inputValueEditText.setText(inputTextString);
                    inputValueEditText.setSelection(inputValueEditText.getText().length());
                }
                break;
            case R.id.activity_input_data_add:
                signTextView.setText("+");
                if (!"".equals(previousValueString) && !".".equals(previousValueString)) {
                    String currentValueString = inputValueEditText.getText().toString();
                    double previousValue = Double.parseDouble(previousValueString);
                    double currentValue = 0.0;
                    if (!"".equals(currentValueString) && !".".equals(currentValueString))
                        currentValue = Double.parseDouble(currentValueString);
                    currentValue = currentValue + previousValue;
                    inputValueEditText.setText(Constants.formatDigit(currentValue));
                    inputValueEditText.setSelection(inputValueEditText.getText().length());
                }

                previousValueString = inputValueEditText.getText().toString();
                plusPressed = true;
                break;
            case R.id.activity_input_data_equal:
                signTextView.setText("");
                if (!"".equals(previousValueString) && !".".equals(previousValueString)) {
                    String currentValueString = inputValueEditText.getText().toString();
                    double previousValue = Double.parseDouble(previousValueString);
                    double currentValue = 0.0;
                    if (previousWasPlus)
                        currentValue = previousValue;
                    if (!"".equals(currentValueString) && !".".equals(currentValueString))
                        currentValue = Double.parseDouble(currentValueString);
                    currentValue = currentValue + previousValue;
                    inputValueEditText.setText(Constants.formatDigit(currentValue));
                    inputValueEditText.setSelection(inputValueEditText.getText().length());
                }

                previousValueString = "";
                break;
            case R.id.activity_input_data_ok:
                if ("".equals(previousValueString))
                    saveData();
                else {
                    String currentValueString = inputValueEditText.getText().toString();
                    double previousValue = 0.0;
                    double currentValue = 0.0;
                    if (!".".equals(previousValueString))
                        previousValue = Double.parseDouble(previousValueString);
                    if (!".".equals(currentValueString) && !"".equals(currentValueString))
                        currentValue = Double.parseDouble(currentValueString);
                    currentValue = currentValue + previousValue;

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    String message = "Сохранить " +
                            previousValueString + " + " + currentValueString +
                            " = " + currentValue + " .руб ?";

                    dialogBuilder.setMessage(message);
                    dialogBuilder.setCancelable(true);
                    dialogBuilder.setNegativeButton("Отмена", null);
                    final double finalCurrentValue = currentValue;
                    final double finalCurrentValue1 = currentValue;
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveData(finalCurrentValue);

                            Intent intent = new Intent(ActivityInputData.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("result", true);
                            intent.putExtra("value", Constants.formatDigit(finalCurrentValue1));
                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }
                break;
        }
    }

    // Показ окна при некорректном вводе данных
    private void showAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Ввдеите сумму");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("Ok", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    // ============================= Сохранение данных в базе ======================================
    private boolean saveData() {
        String inputValueString = inputValueEditText.getText().toString();
        String inputNoteString = inputNoteEditText.getText().toString();

        // Проверяем введённое значение
        if ("".equals(inputValueString) || ".".equals(inputValueString)) {
            showAlertDialog();
            return false;
        }

        // Сохраняем введённое значение в базу
        CostsDB cdb = CostsDB.getInstance(this);
        double inputValue = Double.parseDouble(inputValueString);
        cdb.addCosts(inputValue, costID, inputNoteString);

        // Возвращаемся на главный экран приложения
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("result", true);
        intent.putExtra("value", inputValueString);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        return true;
    }

    private boolean saveData(double value) {
        String inputNoteString = inputNoteEditText.getText().toString();

        // Сохраняем введённое значение в базу
        CostsDB cdb = CostsDB.getInstance(this);
        cdb.addCosts(value, costID, inputNoteString);

        return true;
    }

    private boolean saveData(long milliseconds) {
        String inputValueString = inputValueEditText.getText().toString();
        String inputNoteString = inputNoteEditText.getText().toString();
        if ("".equals(inputValueString) || ".".equals(inputValueString)) {
            showAlertDialog();
            return false;
        }

        // Сохраняем введённое значение в базу
        CostsDB cdb = CostsDB.getInstance(this);
        cdb.addCostInMilliseconds(costID, inputValueString, milliseconds, inputNoteString);

        // Возвращаемся на главный экран приложения
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("result", true);
        intent.putExtra("value", inputValueString);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        return true;
    }

    private boolean saveData(double value, long milliseconds) {
        String inputNoteString = inputNoteEditText.getText().toString();

        // Сохраняем введённое значение в базу
        CostsDB cdb = CostsDB.getInstance(this);
        cdb.addCostInMilliseconds(costID, Constants.formatDigit(value), milliseconds, inputNoteString);

        return true;
    }
    // =============================================================================================






    // Анимация курсора ввода значения расходов
    private Animation startBlinking(){
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.setDuration(1000);
        fadeIn.setRepeatCount(-1);

        return fadeIn;
    }


    @Override
    public void getPickedDate(String pickedDate) {
        this.pickedDate = pickedDate;
        String[] pickedDateArray = pickedDate.split("\\.");

        int pickedDay = Integer.valueOf(pickedDateArray[0]);
        int pickedMonth = Integer.valueOf(pickedDateArray[1]);
        int pickedYear = Integer.valueOf(pickedDateArray[2]);

        Calendar calendar = Calendar.getInstance();
        long currentTimeInMilliseconds = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, pickedDay);
        calendar.set(Calendar.MONTH, pickedMonth - 1);
        calendar.set(Calendar.YEAR, pickedYear);
        final long pickedTimeInMilliseconds = calendar.getTimeInMillis();

        if (pickedTimeInMilliseconds > currentTimeInMilliseconds) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            String message = "Выбранная дата ещё не наступила";

            dialogBuilder.setMessage(message);
            dialogBuilder.setCancelable(true);
            dialogBuilder.setNegativeButton("Ok", null);

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        } else {
            if ("".equals(previousValueString))
                saveData(pickedTimeInMilliseconds);
            else {
                String currentValueString = inputValueEditText.getText().toString();
                double previousValue = 0.0;
                double currentValue = 0.0;
                if (!".".equals(previousValueString))
                    previousValue = Double.parseDouble(previousValueString);
                if (!".".equals(currentValueString) && !"".equals(currentValueString))
                    currentValue = Double.parseDouble(currentValueString);
                currentValue = currentValue + previousValue;

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                String message = "Сохранить " +
                        previousValueString + " + " + currentValueString +
                        " = " + currentValue + " .руб ?";

                dialogBuilder.setMessage(message);
                dialogBuilder.setCancelable(true);
                dialogBuilder.setNegativeButton("Отмена", null);
                final double finalCurrentValue = currentValue;
                final double finalCurrentValue1 = currentValue;
                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveData(finalCurrentValue, pickedTimeInMilliseconds);

                        Intent intent = new Intent(ActivityInputData.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("result", true);
                        intent.putExtra("value", Constants.formatDigit(finalCurrentValue1));
                        startActivity(intent);
                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
