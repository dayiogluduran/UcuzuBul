package com.ddsoft.ucuzubul.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ddsoft.ucuzubul.R
import com.ddsoft.ucuzubul.entity.ProductEntity

class ProductListViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_item_product_list, parent, false)) {
    private val txtMarketName: TextView by lazy { itemView.findViewById<TextView>(R.id.txtMarketName) }
    private val txtProductName: TextView by lazy { itemView.findViewById<TextView>(R.id.txtProductName) }
    private val txtProductPrice: TextView by lazy { itemView.findViewById<TextView>(R.id.txtProductFiyat) }

    fun bind(productEntity: ProductEntity, onClickListener: (productEntity: ProductEntity) -> Unit) {
        txtMarketName.text = productEntity.productMarket
        txtProductName.text = productEntity.productName
        txtProductPrice.text = productEntity.productPrice

        itemView.setOnClickListener {
            onClickListener(productEntity)
        }
    }
}