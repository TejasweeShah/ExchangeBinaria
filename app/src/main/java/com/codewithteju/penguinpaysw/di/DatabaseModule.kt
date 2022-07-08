package com.codewithteju.penguinpaysw.di

import android.content.Context
import androidx.room.Room
import com.codewithteju.penguinpaysw.db.PenguinPayDB
import com.codewithteju.penguinpaysw.db.ReceivingCountryDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule{

    @Singleton
    @Provides
    fun providePenguinPayDB(@ApplicationContext context: Context) : PenguinPayDB{
        return Room.databaseBuilder(context.applicationContext,
            PenguinPayDB::class.java,
            "PenguinPayDB")
            .createFromAsset("default.db")
            .build()
    }

    @Singleton
    @Provides
    fun provideReceivingCountryDAO(penguinPayDB: PenguinPayDB): ReceivingCountryDAO {
        return penguinPayDB.receivingCountryDao()
    }
}