package com.shakeexpense.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.shakeexpense.app.BuildConfig
import com.shakeexpense.app.data.local.ExpenseDatabase
import com.shakeexpense.app.data.remote.ExpenseApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> = ctx.dataStore

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): ExpenseDatabase =
        Room.databaseBuilder(ctx, ExpenseDatabase::class.java, "expense_db")
            .fallbackToDestructiveMigration().build()

    @Provides @Singleton
    fun provideDao(db: ExpenseDatabase) = db.dao()

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }).build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(if (BuildConfig.APPS_SCRIPT_URL.isNotBlank()) BuildConfig.APPS_SCRIPT_URL else "https://script.google.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideApiService(retrofit: Retrofit): ExpenseApiService =
        retrofit.create(ExpenseApiService::class.java)
}
