package com.ardabank.aradapay.di

import com.ardabank.aradapay.data.repository.AuthRepositoryImpl
import com.ardabank.aradapay.data.repository.ExpenseRepositoryImpl
import com.ardabank.aradapay.data.repository.FriendRepositoryImpl
import com.ardabank.aradapay.data.repository.GroupRepositoryImpl
import com.ardabank.aradapay.data.repository.SettlementRepositoryImpl
import com.ardabank.aradapay.domain.repository.AuthRepository
import com.ardabank.aradapay.domain.repository.ExpenseRepository
import com.ardabank.aradapay.domain.repository.FriendRepository
import com.ardabank.aradapay.domain.repository.GroupRepository
import com.ardabank.aradapay.domain.repository.SettlementRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        groupRepositoryImpl: GroupRepositoryImpl
    ): GroupRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindFriendRepository(
        friendRepositoryImpl: FriendRepositoryImpl
    ): FriendRepository

    @Binds
    @Singleton
    abstract fun bindSettlementRepository(
        settlementRepositoryImpl: SettlementRepositoryImpl
    ): SettlementRepository
}
