package com.woocommerce.android.ui.products.addons

import com.woocommerce.android.model.Order
import com.woocommerce.android.model.Order.Item.Attribute
import com.woocommerce.android.model.ProductAddon
import com.woocommerce.android.model.ProductAddonOption
import com.woocommerce.android.model.toAppModel
import com.woocommerce.android.util.UnitTestUtils.jsonFileAs
import com.woocommerce.android.util.UnitTestUtils.jsonFileToString
import org.wordpress.android.fluxc.model.WCOrderModel.LineItem
import org.wordpress.android.fluxc.model.WCProductModel
import org.wordpress.android.fluxc.model.addons.WCProductAddonModel.AddOnPriceType.FlatFee

object AddonTestFixtures {
    val defaultWCOrderItemList: List<LineItem> by lazy {
        "mocks/order_items.json"
            .jsonFileAs(Array<LineItem>::class.java)
            ?.toList()
            ?: emptyList()
    }

    val defaultOrderAttributes by lazy {
        listOf(
            Attribute("Topping ($3,00)", "Peperoni"),
            Attribute("Topping ($4,00)", "Extra cheese"),
            Attribute("Topping ($3,00)", "Salami"),
            Attribute("Soda ($8,00)", "4"),
            Attribute("Delivery ($5,00)", "Yes")
        )
    }

    val defaultWCProductModel by lazy {
        WCProductModel()
            .apply {
                attributes = "[]"
                status = "draft"
                metadata = "mocks/product_addon_metadata.json".jsonFileToString() ?: ""
            }
    }

    val defaultProductAddonList by lazy {
        defaultWCProductModel
            .toAppModel()
            .addons
    }

    val defaultOrderedAddonList by lazy {
        listOf(
            emptyProductAddon.copy(
                name = "Topping",
                description = "Pizza topping",
                priceType = FlatFee,
                rawOptions = listOf(
                    ProductAddonOption(
                        priceType = FlatFee,
                        label = "Peperoni",
                        price = "3",
                        image = ""
                    )
                )
            ),
            emptyProductAddon.copy(
                name = "Topping",
                description = "Pizza topping",
                priceType = FlatFee,
                rawOptions = listOf(
                    ProductAddonOption(
                        priceType = FlatFee,
                        label = "Extra cheese",
                        price = "4",
                        image = ""
                    )
                )
            ),
            emptyProductAddon.copy(
                name = "Topping",
                description = "Pizza topping",
                priceType = FlatFee,
                rawOptions = listOf(
                    ProductAddonOption(
                        priceType = FlatFee,
                        label = "Salami",
                        price = "3",
                        image = ""
                    )
                )
            ),
            emptyProductAddon.copy(
                name = "Soda",
                priceType = FlatFee,
                rawPrice = "2",
                rawOptions = listOf(
                    ProductAddonOption(
                        priceType = FlatFee,
                        label = "4",
                        price = "$8,00",
                        image = ""
                    )
                )
            ),
            emptyProductAddon.copy(
                name = "Delivery",
                priceType = FlatFee,
                rawOptions = listOf(
                    ProductAddonOption(
                        priceType = FlatFee,
                        label = "Yes",
                        price = "5",
                        image = ""
                    )
                )
            )
        )
    }

    val repositoryResponseWithSingleValidOption by lazy {
        Pair(
            listOf(
                emptyProductAddon.copy(
                    name = "test-name",
                    priceType = FlatFee,
                    rawOptions = listOf(
                        ProductAddonOption(
                            null,
                            "invalid",
                            "invalid",
                            "invalid"
                        ),
                        ProductAddonOption(
                            FlatFee,
                            "test-label",
                            "test-price",
                            "test-image"
                        ),
                    )
                )
            ),
            listOf(
                Order.Item.Attribute(
                    "test-name (test-price)",
                    "test-label"
                )
            )
        )
    }

    val emptyProductAddon by lazy {
        ProductAddon(
            name = "",
            required = false,
            description = "",
            descriptionEnabled = false,
            max = "",
            min = "",
            position = "",
            adjustPrice = "",
            restrictions = "",
            titleFormat = null,
            restrictionsType = null,
            priceType = null,
            type = null,
            display = null,
            rawPrice = "",
            rawOptions = listOf()
        )
    }
}
