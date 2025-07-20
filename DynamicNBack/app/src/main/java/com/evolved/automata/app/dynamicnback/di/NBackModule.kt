package com.evolved.automata.app.dynamicnback.di

import android.content.Context
import android.media.RingtoneManager
import com.evolved.automata.app.dynamicnback.HostInterface
import com.evolved.automata.app.dynamicnback.Logger
import com.evolved.automata.app.dynamicnback.NBackHostInterface
import com.evolved.automata.app.dynamicnback.NBackLogger
import com.evolved.automata.app.dynamicnback.repo.NBackProfileRepo
import com.evolved.automata.app.dynamicnback.repo.ProfileRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
val toneType = RingtoneManager.TYPE_NOTIFICATION

@Module @InstallIn(SingletonComponent::class)
class NBackModule {
    @Provides @Singleton
    fun provideHostInterace(@ApplicationContext context:Context, profileRepo: ProfileRepo, logger: Logger, ringtoneManager: RingtoneManager): HostInterface {
        return NBackHostInterface(context, CoroutineScope(Dispatchers.Default), profileRepo, logger, ringtoneManager)
    }


    @Provides @Singleton
    fun provideLogger(): Logger{
        return NBackLogger()
    }


    @Provides @Singleton
    fun provideProfileRepo(@ApplicationContext context:Context): ProfileRepo{
        return NBackProfileRepo(context)
    }


    @Provides @Singleton
    fun provideRingtoneManager(@ApplicationContext context:Context): RingtoneManager {
        val toneType = RingtoneManager.TYPE_NOTIFICATION
        val m = RingtoneManager(context)
        m.setType(toneType)
        return m
    }


}