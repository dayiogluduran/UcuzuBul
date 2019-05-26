package com.ddsoft.ucuzubul.dao

import androidx.room.*
import com.ddsoft.ucuzubul.entity.ProductEntity

@Dao
interface ProductDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewItem(productEntity: ProductEntity)

    @Delete
    fun removeItem(productEntity: ProductEntity)

    @Query("SELECT * FROM product_list WHERE id LIKE :id")
    fun findSingleItem(id: Int): ProductEntity

    @Query("SELECT * FROM product_list")
    fun getAllList(): List<ProductEntity>

    @Query("DELETE FROM product_list")
    fun deleteAllTable()
}