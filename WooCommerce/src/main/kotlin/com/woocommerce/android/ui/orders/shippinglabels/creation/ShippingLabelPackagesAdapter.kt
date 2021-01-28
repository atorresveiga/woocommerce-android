package com.woocommerce.android.ui.orders.shippinglabels.creation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.woocommerce.android.R
import com.woocommerce.android.databinding.PackageProductListItemBinding
import com.woocommerce.android.databinding.ShippingLabelPackageDetailsListItemBinding
import com.woocommerce.android.model.Order
import com.woocommerce.android.model.ShippingLabelPackage
import com.woocommerce.android.ui.orders.shippinglabels.creation.PackageProductsAdapter.PackageProductViewHolder
import com.woocommerce.android.ui.orders.shippinglabels.creation.ShippingLabelPackagesAdapter.ShippingLabelPackageViewHolder

class ShippingLabelPackagesAdapter() : RecyclerView.Adapter<ShippingLabelPackageViewHolder>() {
    var packages: List<ShippingLabelPackage> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingLabelPackageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ShippingLabelPackageViewHolder(
            ShippingLabelPackageDetailsListItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun getItemCount() = packages.count()

    override fun onBindViewHolder(holder: ShippingLabelPackageViewHolder, position: Int) {
        holder.bind(packages[position], position)
    }

    class ShippingLabelPackageViewHolder(val binding: ShippingLabelPackageDetailsListItemBinding) : ViewHolder(binding.root) {
        init {
            with(binding.itemsList) {
                layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                adapter = PackageProductsAdapter()
            }
        }

        @SuppressLint("SetTextI18n") fun bind(packageDetails: ShippingLabelPackage, position: Int) {
            val context = binding.root.context
            binding.packageName.text = context.getString(
                R.string.shipping_label_package_details_title_template,
                position + 1
            )
            binding.packageItemsCount.text = " - ${context.getString(
                R.string.shipping_label_package_details_items_count,
                packageDetails.items.count()
            )}"
            (binding.itemsList.adapter as PackageProductsAdapter).items = packageDetails.items
            binding.selectedPackageSpinner.setText(packageDetails.selectedPackage.title)
            if (packageDetails.weight != -1) {
                binding.weightEditText.setText(packageDetails.weight.toString())
            }
        }
    }
}

class PackageProductsAdapter : RecyclerView.Adapter<PackageProductViewHolder>() {
    var items: List<Order.Item> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PackageProductViewHolder(
            PackageProductListItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun getItemCount() = items.count()

    override fun onBindViewHolder(holder: PackageProductViewHolder, position: Int) = holder.bind(items[position])

    class PackageProductViewHolder(val binding: PackageProductListItemBinding) : ViewHolder(binding.root) {
        fun bind(item: Order.Item) {
            binding.productName.text = item.name
            // TODO fetch and add weight
            binding.productDetails.text = item.attributesList
        }
    }
}
