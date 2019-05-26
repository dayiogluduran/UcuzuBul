package com.ddsoft.ucuzubul

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddsoft.ucuzubul.adapter.ProductListAdapter
import com.ddsoft.ucuzubul.db.ProductDB
import com.ddsoft.ucuzubul.entity.ProductEntity
import kotlinx.android.synthetic.main.activity_shopping_list.*
import kotlin.concurrent.thread

class ShoppingListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        this.title = "Alışveriş Listesi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val productDb = ProductDB.getInstance(this)
        val productDao = productDb?.getProductDao()

        var allProductList: List<ProductEntity>? = null
        thread(start = true) {
            allProductList = productDao?.getAllList()
            recyclerProductList.adapter = ProductListAdapter(allProductList!!) { productEntity ->
                var newProduct: List<ProductEntity>? = null
                thread(start = true) {
                    productDao?.removeItem(productEntity)
                    newProduct = productDao?.getAllList()!!
                    runOnUiThread {
                        (recyclerProductList.adapter as ProductListAdapter).setNewItem(newProduct!!)
                    }
                }
            }
            recyclerProductList.layoutManager = LinearLayoutManager(this)
        }
    }
}
