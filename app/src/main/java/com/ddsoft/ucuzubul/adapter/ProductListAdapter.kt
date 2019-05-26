package com.ddsoft.ucuzubul.adapter


import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddsoft.ucuzubul.entity.ProductEntity

class ProductListAdapter(
    private var productList: List<ProductEntity>,
    private val onClickListener: (contactInfoEntity: ProductEntity) -> Unit
) : RecyclerView.Adapter<ProductListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder =
        ProductListViewHolder(parent)

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {

        holder.bind(productList[position], onClickListener)

    }

    fun setNewItem(productList: List<ProductEntity>) {
        this.productList = productList
        notifyDataSetChanged()
    }
}
