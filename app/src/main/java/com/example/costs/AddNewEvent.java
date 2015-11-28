package com.example.costs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNewEvent extends AppCompatActivity {

    TextView eventDescriptionTextView;
    TextView eventDayTextView;
    TextView eventMonthTextView;
    TextView eventYearTextView;

    CostsDB db;

    String dateOfEventString;
    String eventDateError = "Некорректная дата";

    boolean isInputIncorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        // Инициализируем поля ввода
        eventDescriptionTextView = (TextView) findViewById(R.id.eventDescription);
        eventDayTextView = (TextView) findViewById(R.id.eventDay);
        eventMonthTextView = (TextView) findViewById(R.id.eventMonth);
        eventYearTextView = (TextView) findViewById(R.id.eventYear);

        // Получаем доступ к базе данных
        db = new CostsDB(this, null, null, 1);
    }

    public void addEventButtonOnClick(View view) {
        Calendar calendar = Calendar.getInstance();

        // Получаем и проверяем входные данные
        int eventDay = 3;
        int eventMonth = 5;
        int eventYear = 1989;
        try {
            isInputIncorrect = false;
            eventDay = Integer.parseInt(eventDayTextView.getText().toString());
            eventMonth = Integer.parseInt(eventMonthTextView.getText().toString());
            eventYear = Integer.parseInt(eventYearTextView.getText().toString());

            if ((eventDay < 1 && eventDay > 31) || (eventMonth < 1 && eventMonth > 12) || (eventYear < calendar.get(Calendar.YEAR))) {
                isInputIncorrect = true;
                Toast errorDayToast = Toast.makeText(this, eventDateError, Toast.LENGTH_LONG);
                errorDayToast.show();
            }
        } catch (Exception e) {
            isInputIncorrect = true;
            Toast errorDayToast = Toast.makeText(this, eventDateError, Toast.LENGTH_LONG);
            errorDayToast.show();
        }

        // Если всё в порядке - записываем данные о предстоящем событии в базу данных
        if (!isInputIncorrect) {

            dateOfEventString = String.valueOf(eventDay) + "." + String.valueOf(eventMonth) + "." + String.valueOf(eventYear);
            long dateOfEventInMilliseconds = 0;
            boolean allDataIsFine = true;

            try {
                dateOfEventInMilliseconds = new SimpleDateFormat("dd.MM.yyyy").parse(dateOfEventString).getTime();
            } catch (Exception e) {
                Toast errorInDateParsingToast = Toast.makeText(this, "ERROR PARSING DATE", Toast.LENGTH_LONG);
                errorInDateParsingToast.show();
                allDataIsFine = false;
            }

            if (allDataIsFine) {
                //db.addNewEvent(eventDescriptionTextView.getText().toString(), dateOfEventString, dateOfEventInMilliseconds);
                eventDescriptionTextView.setText(String.valueOf(dateOfEventInMilliseconds));
                System.out.println(calendar.getTimeInMillis());
            }
        }

    }
}