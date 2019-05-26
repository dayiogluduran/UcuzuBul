package com.ddsoft.ucuzubul.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_list")
data class ProductEntity(

        @PrimaryKey(autoGenerate = true) @NonNull
        val id: Int = 0,

        @ColumnInfo(name = "productName")
        val productName: String,

        @ColumnInfo(name = "productPrice")
        val productPrice: String,

        @ColumnInfo(name = "productMarket")
        val productMarket: String
)