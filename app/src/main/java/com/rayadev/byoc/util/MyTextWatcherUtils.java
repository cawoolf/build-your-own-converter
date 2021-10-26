package com.rayadev.byoc.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.rayadev.byoc.util.ConverterUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MyTextWatcherUtils {

    private EditText viewA, viewB;
    private ConverterUtil converter;
    private int userSelection;
    private double baseCurrency;
    private double currencyValue;

    private TextWatcher mTextWatcher;


    public MyTextWatcherUtils(int userSelection, EditText viewA, EditText viewB, ConverterUtil converter) {

        this.userSelection = userSelection;
        this.converter = converter;

        this.viewA = viewA;
        this.viewB = viewB;

    }


    public MyTextWatcherUtils(int userSelection, EditText viewA, EditText viewB, double baseCurrency, double currencyValue) {

        this.userSelection = userSelection;
        this.viewA = viewA;
        this.viewB = viewB;
        this.baseCurrency = baseCurrency;
        this.currencyValue = currencyValue;

    }

    public void setUnitEditTextWatcher(EditText userInputEditText) {


        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                switch(userSelection) {
                    case 1:
                        if(viewA.isFocused()) {
                            runConversionAB(converter);
                        }

                        break;
                    case 2:
                        if(viewB.isFocused()) {
                            runConversionBA(converter);
                        }
                        break;
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        userInputEditText.addTextChangedListener(mTextWatcher);
    }

    public void removeTextWatcher(EditText userInputEditText) {
        userInputEditText.removeTextChangedListener(mTextWatcher);
    }

    private void runConversionAB(ConverterUtil converter) {

        String editTextAInputString = String.valueOf(viewA.getText());

        //Currency provides a null converter
        if(converter == null) {

            if (!editTextAInputString.equals("") && !editTextAInputString.equals(".")) {
                double input = Double.parseDouble(viewA.getText().toString());

                double result = input * currencyValue;
//                String text_Result = String.valueOf(result);

                String text_Result = new DecimalFormat("########.###").format(result);
                viewB.setText(text_Result);

            } else {
                viewB.setText("");

            }

        }

        else {


            if (!editTextAInputString.equals("") && !editTextAInputString.equals(".")) {
                double input = Double.parseDouble(viewA.getText().toString());
                double result;

                if(converter instanceof CustomConverterUtil) {

                    double unitAValue = ((CustomConverterUtil) converter).getUnitAValue();
                    double unitBValue = ((CustomConverterUtil) converter).getUnitBValue();

                    result = ((CustomConverterUtil) converter).convertABCustomUnits(unitAValue, unitBValue, input);
                    Log.i("CTAG", "AB Ran");

                }
                else {
                    result = converter.convert(input);

                }

                String result_String = String.valueOf(result);

                //Handles the text output for small results.
                if(result < 1) {
                    if(result_String.length() < 9)
                    {
                        result_String = new DecimalFormat("0.#######").format(result);
                    }
                    else {
                        result_String = new DecimalFormat("0.#######E00").format(result);
                    }

                }
                else {

                    result_String = new DecimalFormat("#########.###").format(result);
                }

                //Handles text output for large results
                if(result_String.length() > 12){
                    result_String = new DecimalFormat("#######.###E00").format(result);
                }
                viewB.setText(result_String);
            }


            else {
                viewB.setText("");

            }
        }

    }

    private void runConversionBA(ConverterUtil converter) {

        String editTextBInputString = String.valueOf(viewB.getText());

        //Currency provides a null converter
        if(converter == null) {
            if (!editTextBInputString.equals("") && !editTextBInputString.equals(".")) {
                double input = Double.parseDouble(viewB.getText().toString());

                double result = input * (1/currencyValue);

                String text_Result = new DecimalFormat("########.###").format(result);
                viewA.setText(text_Result);

            } else {
                viewA.setText("");

            }

        }

        else {
            if (!editTextBInputString.equals("") && !editTextBInputString.equals(".")) {
                double input = Double.parseDouble(viewB.getText().toString());
                double result;

                if(converter instanceof CustomConverterUtil) {

                    double unitAValue = ((CustomConverterUtil) converter).getUnitAValue(); //Should usually be 1.
                    double unitBValue = ((CustomConverterUtil) converter).getUnitBValue();

                    result = ((CustomConverterUtil) converter).convertBACustomUnits(unitAValue, unitBValue, input);
                    Log.i("CTAG", "BA Ran");

                }
                else {
                    result = converter.convert(input);

                }

                String result_String = String.valueOf(result);

                //Handles the text output for small results.
                if(result < 1) {
                    if(result_String.length() < 9)
                    {
                        result_String = new DecimalFormat("0.#######").format(result);
                    }
                    else {
                        result_String = new DecimalFormat("0.#######E00").format(result);
                    }

                }
                else {

                    result_String = new DecimalFormat("#########.###").format(result);
                }

                if(result_String.length() > 12){
                    result_String = new DecimalFormat("#######.###E00").format(result);
                }
                viewA.setText(result_String);


            } else {
                viewA.setText("");


            }
        }

    }


}
