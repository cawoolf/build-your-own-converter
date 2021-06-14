package com.rayadev.byoc.room;


import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

//Layer of abstraction. Passes data from either the RoomDatabase, or a network source.
public class ConverterRepository {

    private ConverterDAO mConverterDao;
    private LiveData<List<Converter>> mAllConverters;

    //Add a constructor that gets a handle to the database and initializes the member variables.
    public ConverterRepository (Application application) {
        ConverterRoomDataBase db = ConverterRoomDataBase.getDatabase(application);
        mConverterDao = db.mConverterDAO();
        mAllConverters = mConverterDao.getAllConverters();
    }



    //These are the methods that get called by the ViewModel.
    /*
    Add a wrapper method called getAllConverter() that returns the cached Converters as LiveData.
    Room executes all queries on a separate thread.
    Observed LiveData notifies the observer when the data changes.

    Add a wrapper for all methods in the ConverterDAO
    */

    //getter doesn't need AsyncWrapper just returning a List
    public LiveData<List<Converter>> getAllConverters() {
        return mAllConverters;
    }

    public void insertConverter (Converter converter) {
        new insertAsyncTask(mConverterDao).execute(converter);
    }

    //This delete function needs to use the converterID
    public void deleteConverter(Converter converter)  {
        new deleteConverterAsyncTask(mConverterDao).execute(converter);
    }

    public LiveData<List<Converter>> getTargetConverter(String converterName) {
        //AsyncTask occurs at Fragment level. Seems to be good. This needs to be LiveData.
        return mConverterDao.getTargetConverter(converterName);
    }

    public Converter getConverterByID(int converterID) {
        return mConverterDao.getConverterByID(converterID);
    }

    //Every method from the ConverterDAO needs an Async innerclass to execute.

    private static class insertAsyncTask extends AsyncTask<Converter, Void, Void> {

        private ConverterDAO mAsyncTaskDao;

        insertAsyncTask(ConverterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Converter... params) {
            mAsyncTaskDao.insertConverter(params[0]);
            return null;
        }
    }

    private static class deleteConverterAsyncTask extends AsyncTask<Converter , Void, Void> {

        private ConverterDAO mAsyncTaskDao;

        public deleteConverterAsyncTask(ConverterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Converter... params) {
            mAsyncTaskDao.deleteConverter(params[0]);
            return null;
        }
    }

    private static class getTargetConverterAsyncTask extends AsyncTask<String, Void, Void> {

        private ConverterViewModel mConverterViewModel;

        public getTargetConverterAsyncTask(ConverterViewModel converterViewModel){
            this.mConverterViewModel = converterViewModel;
        }
        @Override
        protected Void doInBackground(String... strings) {
            LiveData<List<Converter>> converters = mConverterViewModel.getTargetConverter(strings[0]);
            return null;
        }

    }

}
