package com.ddsoft.ucuzubul.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ddsoft.ucuzubul.dao.ProductDao
import com.ddsoft.ucuzubul.entity.ProductEntity

@Database(entities = [ProductEntity::class], version = 1)
abstract class ProductDB : RoomDatabase() {

    abstract fun getProductDao(): ProductDao

    companion object {
        private var INSTANCE: ProductDB? = null

        fun getInstance(context: Context): ProductDB? {
            if (INSTANCE == null) {
                synchronized(ProductDB::class) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            ProductDB::class.java, "product_list_db"
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }
    }
}