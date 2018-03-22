package com.philippefouquet.stationmeteo.Interface;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by philippefouquet on 11/03/2018.
 */

public class PikerDate {
    EditText myEditText;
    Context myContext;

    Calendar myCalendar = Calendar.getInstance();

    public PikerDate(EditText editText, Context context){
        this.myEditText = editText;
        this.myContext = context;
        DatePickerDialog.OnDateSetListener datepiker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                update();
            }

        };

        editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(myContext, datepiker, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void update(){
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRENCH);

        myEditText.setText(sdf.format(myCalendar.getTime()));
    }

    public void setDate(Date date){
        myCalendar.setTime(date);
        rounddate();
        update();
    }

    private void rounddate(){
        myCalendar.set(Calendar.HOUR_OF_DAY, 0);
        myCalendar.set(Calendar.MINUTE,0);
        myCalendar.set(Calendar.SECOND,0);
        myCalendar.set(Calendar.MILLISECOND,0);
    }

    public Date getDate(){
        rounddate();
        return myCalendar.getTime();
    }

    public Date getNextDay(){
        rounddate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(myCalendar.getTime());

        calendar.set(Calendar.DAY_OF_YEAR,
                calendar.get(Calendar.DAY_OF_YEAR)+1);
        return calendar.getTime();
    }
}
