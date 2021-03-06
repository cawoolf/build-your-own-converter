package com.rayadev.byoc.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rayadev.byoc.R;
import com.rayadev.byoc.model.Converter;
import com.rayadev.byoc.model.Currency;
import com.rayadev.byoc.util.ConverterUtil;
import com.rayadev.byoc.model.ConverterViewModel;
import com.rayadev.byoc.util.CustomConverterUtil;
import com.rayadev.byoc.util.KeyboardUtils;
import com.rayadev.byoc.util.MyTextWatcherUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeSetTabFragment extends Fragment implements HomeSetRecyclerViewAdapter.ConverterClickListener {

    private RecyclerView mRecyclerView;
    private HomeSetRecyclerViewAdapter mAdapter;
    private ConverterViewModel mConverterViewModel;

    private View mConverterUI;
    private HideConverterUIInterface mHideConverterUIInterface;

    //Views for the Converter UI
    private TextView mUnitATitleTextView, mUnitBTitleTextView;
    private EditText mUnitAInputEditText, mUnitBInputEditText;
    private ImageButton mConverterInfoButton, mConverterSwapButton;

    //Globals for Converter
    private ConverterUtil.Unit fromUnit, toUnit;
    private String mUnitAName, mUnitBName;
    private double mUnitAValue, mUnitBValue;
    private String mUnitCategory;
    private int mSwapUnits = 0;

    public HomeSetTabFragment() {
        // Required empty public constructor
    }

    public static HomeSetTabFragment newInstance() {
        HomeSetTabFragment fragment = new HomeSetTabFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_set_tab, container, false);

        setUpHomeSetRecyclerView(view);
        setUpConverterViewModel(mAdapter);
        linkViews(view);
        sendUpConverterUI();


        return view;
    }

    private void linkViews(View view) {
        //Link ConverterBox Views
        mConverterUI = view.findViewById(R.id.converter_cardlayout_include_home_tab); // root View id from include

        mUnitATitleTextView = mConverterUI.findViewById(R.id.cardView_UnitATitle_TextView);
        mUnitBTitleTextView = mConverterUI.findViewById(R.id.cardView_UnitBTitle_TextView);

        mUnitAInputEditText = mConverterUI.findViewById(R.id.cardView_UnitAInput_EditText);
        mUnitBInputEditText = mConverterUI.findViewById(R.id.cardView_UnitBInput_EditText);

        mConverterInfoButton = mConverterUI.findViewById(R.id.cardView_InfoButton);
        mConverterSwapButton = mConverterUI.findViewById(R.id.cardView_SwapButton);

        mConverterSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (mSwapUnits) {

                    case 0:

                        if(mUnitCategory.equals("Currency")) {
                            String currencyPair = mUnitBName + "_" + mUnitAName;
                            setUpTargetCurrency(currencyPair, mUnitBName, mUnitAName);


                        }

                        else if(mUnitCategory.equals("Custom")) {
                            setConverterBoxTitles(mUnitBName, mUnitAName);
                            CustomConverterUtil fromUnit_toUnit = new CustomConverterUtil(mUnitBValue, mUnitAValue);
                            CustomConverterUtil toUnit_fromUnit = new CustomConverterUtil(mUnitBValue, mUnitAValue);
                            setCustomConverterBoxLogic(fromUnit_toUnit, toUnit_fromUnit);


                        }

                        else if(mUnitCategory.equals(getString(R.string.unit_category_temperature))){
                            setConverterBoxTitles(mUnitBName, mUnitAName);
                            setUpTargetTemperature(mUnitBName, mUnitAName);
                            Log.i("TAG_69", "Temp Swap BA");
                        }
                        else {
                                setConverterBoxLogic(toUnit, fromUnit);
                                setConverterBoxTitles(mUnitBName, mUnitAName);
                            }

                        mSwapUnits = 1;
                        break;

                    case 1:

                        if(mUnitCategory.equals("Currency")) {
                            String currencyPair = mUnitAName + "_" + mUnitBName;
                            setUpTargetCurrency(currencyPair, mUnitAName, mUnitBName);

                        }

                        else if(mUnitCategory.equals("Custom")) {
                            setConverterBoxTitles(mUnitAName, mUnitBName);
                            CustomConverterUtil fromUnit_toUnit = new CustomConverterUtil(mUnitAValue, mUnitBValue);
                            CustomConverterUtil toUnit_fromUnit = new CustomConverterUtil(mUnitAValue, mUnitBValue);
                            setCustomConverterBoxLogic(fromUnit_toUnit, toUnit_fromUnit);

                        }

                        else if(mUnitCategory.equals(getString(R.string.unit_category_temperature))){
                            setConverterBoxTitles(mUnitAName, mUnitBName);
                            setUpTargetTemperature(mUnitAName, mUnitBName);
                            Log.i("TAG_69", "Temp Swap AB");
                        }

                        else {
                            setConverterBoxLogic(fromUnit, toUnit);
                            setConverterBoxTitles(mUnitAName, mUnitBName);
                        }

                        mSwapUnits = 0;
                        break;

                }

            }
        });

        mConverterUI.setVisibility(View.GONE);
    }

    private void sendUpConverterUI() {
        //Sends the ConverterUI up to the MainActivity, so that it can be hidden when swiping the keyboard...
        mHideConverterUIInterface.hideConverterUI(mConverterUI);
    }

    private void setUpHomeSetRecyclerView(View view) {

        // Get a handle to the RecyclerView.
        mRecyclerView = view.findViewById(R.id.home_set_tab_recycler_view);

        // Create an adapter and supply the data to be displayed.
        mAdapter = new HomeSetRecyclerViewAdapter(view.getContext(), this);

        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
    }

    private void setUpConverterViewModel(final HomeSetRecyclerViewAdapter adapter) {

        //All the activity's interactions are with the ViewModel only.
        // When the activity is destroyed, the ViewModel still exists. It is not subject to LifeCycle methods.
        mConverterViewModel = new ViewModelProvider(this).get(ConverterViewModel.class); //Call ViewModel constructor directly

        //To display the current contents of the database, you add an observer that observes the LiveData in the ViewModel.
        mConverterViewModel.getConverterFavoritesList().observe(getViewLifecycleOwner(), new Observer<List<Converter>>() {
            @Override
            public void onChanged(List<Converter> converters) {
                adapter.setConverterArrayList((ArrayList<Converter>) converters);

            }
        });

    }

    //Sets the Converter data into the fragment Converter UI.
    @Override
    public void onConverterIconClick(String unitAName, String unitBName, String unitCategory, double unitAValue, double unitBValue) {

        setConverterBoxTitles(unitAName, unitBName);
        mUnitAName = unitAName;
        mUnitBName = unitBName;
        mUnitAValue = unitAValue;
        mUnitBValue = unitBValue;
        mUnitCategory = unitCategory;

        if(unitCategory.equals("Currency")) {
//            LiveData<Currency> currency = mConverterViewModel.getTargetCurrency("USD_NZD");
//            Log.i("CTAG",currency.getCurrencyPair() + "" + currency.currencyValue);

            Log.i("CTAG", "LiveData success");


            //Next step is to use the unit names to parse a currency pair and fetch.
            String currencyPair = unitAName + "_" + unitBName;
            setUpTargetCurrency(currencyPair, unitAName, unitBName);
        }

        else if(unitCategory.equals("Custom")) {
            CustomConverterUtil fromUnit_toUnit = new CustomConverterUtil(mUnitAValue, mUnitBValue);
            CustomConverterUtil toUnit_fromUnit = new CustomConverterUtil(mUnitAValue, mUnitBValue);
            setCustomConverterBoxLogic(fromUnit_toUnit, toUnit_fromUnit);
        }

        else if(unitCategory.equals(getString(R.string.unit_category_temperature))) {
            setUpTargetTemperature(mUnitAName, mUnitBName);
            Log.i("TAG_69", "Temp Converter Set Up");
        }

        else {

            fromUnit = ConverterUtil.Unit.fromString(mUnitAName);
            toUnit = ConverterUtil.Unit.fromString(mUnitBName);

            setConverterBoxLogic(fromUnit, toUnit);

            keyboardManager();

            //Custom back button function.
            OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    mConverterUI.setVisibility(View.GONE);
                    clearUserInput();

                }
            };

            requireActivity().getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
            //Keyboard
        }

    }

    @Override
    public void onConverterLongClick(int position) {

        new AlertDialog.Builder(getContext())
                .setTitle("Delete Converter")
                .setMessage("Are you sure you want to delete this entry?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Converter converter = mAdapter.getConverterAtPosition(position);
                        mConverterViewModel.delete(converter);

                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void setConverterBoxTitles(String unitAText, String unitBText) {

        mUnitATitleTextView.setText(unitAText);
        mUnitBTitleTextView.setText(unitBText);

    }

    private void setConverterBoxLogic(ConverterUtil.Unit fromUnit, ConverterUtil.Unit toUnit) {


        ConverterUtil fromUnit_toUnit = new ConverterUtil(fromUnit, toUnit);
        ConverterUtil toUnit_fromUnit = new ConverterUtil(toUnit, fromUnit);

        final MyTextWatcherUtils[] myTextWatcherUtils = new MyTextWatcherUtils[2];

        myTextWatcherUtils[0] = new MyTextWatcherUtils(1, mUnitAInputEditText, mUnitBInputEditText, fromUnit_toUnit);
        myTextWatcherUtils[1] = new MyTextWatcherUtils(2,  mUnitAInputEditText, mUnitBInputEditText, toUnit_fromUnit);

        mUnitAInputEditText.clearFocus();
        mUnitBInputEditText.clearFocus();

        clearUserInput();

        myTextWatcherUtils[0].setUnitEditTextWatcher(mUnitAInputEditText);
        myTextWatcherUtils[1].setUnitEditTextWatcher(mUnitBInputEditText);

        keyboardManager();

        mUnitBInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearUserInput();
            }
        });

        mUnitBInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearUserInput();
            }
        });

    }

    private void setCustomConverterBoxLogic(CustomConverterUtil fromUnit_toUnit, CustomConverterUtil toUnit_fromUnit) {

        clearUserInput();

        final MyTextWatcherUtils[] myTextWatcherUtils = new MyTextWatcherUtils[2];

        myTextWatcherUtils[0] = new MyTextWatcherUtils(1, mUnitAInputEditText, mUnitBInputEditText, fromUnit_toUnit);
        myTextWatcherUtils[1] = new MyTextWatcherUtils(2, mUnitAInputEditText, mUnitBInputEditText, toUnit_fromUnit);

        myTextWatcherUtils[0].setUnitEditTextWatcher(mUnitAInputEditText);
        myTextWatcherUtils[1].setUnitEditTextWatcher(mUnitBInputEditText);

        keyboardManager();

        mUnitBInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearUserInput();
            }
        });

        mUnitBInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearUserInput();
            }
        });

    }

    private void setUpTargetTemperature(String tempA, String tempB) {
        Log.i("STAG", "setUpTargetTemp() called");

        mUnitAInputEditText.clearFocus();
        mUnitBInputEditText.clearFocus();

        clearUserInput();
        keyboardManager();

        String userInput = tempA + tempB;
        String userInputReversed = tempB + tempA;
        String[] tempCombos = getTempCombos();


        //Special user choice to deal with reversed temperature
        MyTextWatcherUtils utilA = new MyTextWatcherUtils(3, mUnitAInputEditText, mUnitBInputEditText, userInput, tempCombos);
        MyTextWatcherUtils utilB = new MyTextWatcherUtils(4,  mUnitAInputEditText, mUnitBInputEditText, userInputReversed, tempCombos);


        utilA.setUnitEditTextWatcher(mUnitAInputEditText);
        utilB.setUnitEditTextWatcher(mUnitBInputEditText);


        mUnitBInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearUserInput();
            }
        });

        mUnitBInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearUserInput();
            }
        });

    }

    private String[] getTempCombos(){


        String FtoC = getString(R.string.temperature_fahrenheit) + getString(R.string.temperature_celsius);
        String FtoK = getString(R.string.temperature_fahrenheit)+getString(R.string.temperature_kelvin);

        String CtoF = getString(R.string.temperature_celsius)+getString(R.string.temperature_fahrenheit);
        String CtoK = getString(R.string.temperature_celsius) + getString(R.string.temperature_kelvin);

        String KtoF = getString(R.string.temperature_kelvin) + getString(R.string.temperature_fahrenheit);
        String KtoC = getString(R.string.temperature_kelvin) + getString(R.string.temperature_celsius);

        String[] tempCombos = {FtoC,FtoK, CtoF, CtoK, KtoF, KtoC};


        return tempCombos;

    }


    private void setUpTargetCurrency(String currencyPair, String currencyA, String currencyB) {

        //Creating multiple instances of this view model just to access the database seems not good..
        //But that's not really whats happening!.. Right?
//        ConverterViewModel mConverterViewModel = new ViewModelProvider(this).get(ConverterViewModel.class);

        LiveData<Currency> currency = mConverterViewModel.getTargetCurrency(currencyPair);

        //This observer seems to be getting triggered from the add button, and other fragments.
        Observer<Currency> observer = new Observer<Currency>() {
            @Override
            public void onChanged(Currency currency) {
                Log.i("CTAG", "Observer Success" + currency.getCurrencyPair() + ":" + currency.getCurrencyValue());
                keyboardManager();
                setConverterBoxTitles(currencyA, currencyB);
                setCurrencyLogic(currency.getCurrencyValue());


            }

        };

        currency.observe(getViewLifecycleOwner(), observer);

    }

    private void setCurrencyLogic(double currencyValue) {
        MyTextWatcherUtils utilA = new MyTextWatcherUtils(1, mUnitAInputEditText, mUnitBInputEditText, currencyValue);
        MyTextWatcherUtils utilB = new MyTextWatcherUtils(2,  mUnitAInputEditText, mUnitBInputEditText, currencyValue);

        mUnitAInputEditText.clearFocus();
        mUnitBInputEditText.clearFocus();

        clearUserInput();

        utilA.setUnitEditTextWatcher(mUnitAInputEditText);
        utilB.setUnitEditTextWatcher(mUnitBInputEditText);

        keyboardManager();

        mUnitBInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearUserInput();
            }
        });

        mUnitBInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                clearUserInput();
            }
        });

    }



    private void keyboardManager() {
        //Keyboard opens when Converter Icon is clicked
        mUnitAInputEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mUnitAInputEditText, InputMethodManager.SHOW_IMPLICIT);

        //When keyboard is closed, Hides the converter UI.
        KeyboardUtils.addKeyboardToggleListener(getActivity(), new KeyboardUtils.SoftKeyboardToggleListener() {

            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {

                if (isVisible) {
                    if(mUnitAInputEditText.hasFocus() || mUnitBInputEditText.hasFocus()) {
                        mConverterUI.setVisibility(View.VISIBLE);
                    }
                } else {
                    mConverterUI.setVisibility(View.GONE);
                    clearUserInput();
                }
            }
        });

    }


    private void clearUserInput() {

        if (mUnitAInputEditText.getText() != null) {
            mUnitAInputEditText.getText().clear();
        }

        if (mUnitBInputEditText.getText() != null) {
            mUnitBInputEditText.getText().clear();
        }

    }


    //Interface for communicating with the Main Activity to Hide the interface during swiping.
    public interface HideConverterUIInterface {
        void hideConverterUI(View view);
    }

    // lifecycle method in SimpleFragment to capture the host Activity interface implementation:
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HideConverterUIInterface) {
            mHideConverterUIInterface = (HideConverterUIInterface) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }


}
