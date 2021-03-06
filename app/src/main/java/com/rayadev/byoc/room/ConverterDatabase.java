package com.rayadev.byoc.room;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rayadev.byoc.model.Converter;
import com.rayadev.byoc.model.Currency;

@Database(entities = {Converter.class, Currency.class}, version = 1, exportSchema = false)
public abstract class ConverterDatabase extends RoomDatabase {

    public abstract ConverterDAO getConverterDAO();

    private static ConverterDatabase INSTANCE;

    public static ConverterDatabase getDatabase(final Context context) {

        //If there is no database, create one
        //Does that mean that everytime the ConverterViewModel is initialized, the code runs all the way to here?
        //Create a Test class for that?
        if (INSTANCE == null) {
            synchronized (ConverterDatabase.class) {

                //Redundant if statement?
                if (INSTANCE == null) {

                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ConverterDatabase.class, "converter_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
