package com.ddsoft.ucuzubul

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ddsoft.ucuzubul.db.ProductDB
import com.ddsoft.ucuzubul.entity.ProductEntity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), View.OnClickListener {

    var controller: LayoutAnimationController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setVisibleFalse()

        val database = FirebaseDatabase.getInstance().reference
        val autoComplete = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        database.child("products").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (suggestionSnapshot in dataSnapshot.children) {
                    val suggestion = suggestionSnapshot.child("name").getValue(String::class.java)
                    autoComplete.add(suggestion)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //AutoCompleteTextView
        txtProductNameToSearch.setAdapter(autoComplete)
        txtProductNameToSearch.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            database.child("products").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (suggestionSnapshot in dataSnapshot.children) {
                        if (suggestionSnapshot.child("name").getValue(String::class.java) == selectedItem) {

                            imm.hideSoftInputFromWindow(txtProductNameToSearch.getWindowToken(), 0)
                            productPrices(suggestionSnapshot.child("barcode").value.toString())
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
            txtProductNameToSearch.setText("")
        }

        lytA101.setOnClickListener(this)
        lytBim.setOnClickListener(this)
        lytMigros.setOnClickListener(this)
        lytSok.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val productDb = ProductDB.getInstance(this)
        val productDao = productDb?.getProductDao()

        var productEntity: ProductEntity? = null

        when (view?.id) {
            R.id.lytA101 -> {
                productEntity = ProductEntity(
                    productMarket = "A101",
                    productName = txtProductName.text.toString(),
                    productPrice = txtA101Price.text.toString()
                )
            }
            R.id.lytBim -> {
                productEntity = ProductEntity(
                    productMarket = "BİM",
                    productName = txtProductName.text.toString(),
                    productPrice = txtBimPrice.text.toString()
                )
            }
            R.id.lytMigros -> {
                productEntity = ProductEntity(
                    productMarket = "MİGROS",
                    productName = txtProductName.text.toString(),
                    productPrice = txtMigrosPrice.text.toString()
                )
            }
            R.id.lytSok -> {
                productEntity = ProductEntity(
                    productMarket = "ŞOK",
                    productName = txtProductName.text.toString(),
                    productPrice = txtSokPrice.text.toString()
                )
            }
        }

        if (productEntity != null) {
            thread(start = true) {
                productDao?.addNewItem(productEntity)
            }
        }

        Toast.makeText(this@MainActivity, "Listeye Eklendi", Toast.LENGTH_SHORT).show()
        setVisibleFalse()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.shoppingListMenuItem -> {
                val intent = Intent(this, ShoppingListActivity::class.java)
                startActivity(intent)
            }
            R.id.readBarkodMenuItem -> {
                val scanner = IntentIntegrator(this)
                scanner.setBeepEnabled(false)
                scanner.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
                scanner.initiateScan()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {

                    productPrices(result.contents)

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun productPrices(productBarcode: String) {
        setVisibleFalse()
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("/products/$productBarcode")) {
                    lytProductInfo.isVisible = true
                    val a101: Double =
                        snapshot.child("/products/$productBarcode/markets/a101").value.toString().toDouble()
                    val bim: Double =
                        snapshot.child("/products/$productBarcode/markets/bim").value.toString().toDouble()
                    val sok: Double =
                        snapshot.child("/products/$productBarcode/markets/sok").value.toString().toDouble()
                    val migros: Double =
                        snapshot.child("/products/$productBarcode/markets/migros").value.toString().toDouble()

                    val df = DecimalFormat("0.00")

                    if (a101 != 0.0 || bim != 0.0 || sok != 0.0 || migros != 0.0) {

                        txtProductName.text = snapshot.child("/products/$productBarcode/name").value.toString()
                        if (a101 != 0.0) {
                            lytA101.isVisible = true
                            controller = AnimationUtils.loadLayoutAnimation(this@MainActivity, R.anim.layout_fall_down)
                            lytA101.layoutAnimation = controller
                            lytA101.scheduleLayoutAnimation()
                            txtA101Price.text = "${df.format(a101)} TL"
                        }
                        if (bim != 0.0) {
                            lytBim.isVisible = true
                            controller = AnimationUtils.loadLayoutAnimation(this@MainActivity, R.anim.layout_fall_down)
                            lytBim.layoutAnimation = controller
                            lytBim.scheduleLayoutAnimation()
                            txtBimPrice.text = "${df.format(bim)} TL"
                        }
                        if (sok != 0.0) {
                            lytSok.isVisible = true
                            controller = AnimationUtils.loadLayoutAnimation(this@MainActivity, R.anim.layout_fall_down)
                            lytSok.layoutAnimation = controller
                            lytSok.scheduleLayoutAnimation()
                            txtSokPrice.text = "${df.format(sok)} TL"
                        }
                        if (migros != 0.0) {
                            lytMigros.isVisible = true
                            controller = AnimationUtils.loadLayoutAnimation(this@MainActivity, R.anim.layout_fall_down)
                            lytMigros.layoutAnimation = controller
                            lytMigros.scheduleLayoutAnimation()
                            txtMigrosPrice.text = "${df.format(migros)} TL"
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Bu ürün için fiyat bilgisi girilmemiştir.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(this@MainActivity, "Kayıtlı ürün bulunamadı.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setVisibleFalse() {
        lytMigros.isVisible = false
        lytA101.isVisible = false
        lytBim.isVisible = false
        lytSok.isVisible = false
        lytProductInfo.isVisible = false
    }

}


