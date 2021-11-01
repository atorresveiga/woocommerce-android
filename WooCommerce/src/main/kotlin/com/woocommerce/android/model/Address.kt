package com.woocommerce.android.model

import android.os.Parcelable
import com.google.i18n.addressinput.common.AddressData
import com.google.i18n.addressinput.common.FormOptions
import com.google.i18n.addressinput.common.FormatInterpreter
import com.woocommerce.android.extensions.appendWithIfNotEmpty
import com.woocommerce.android.util.WooLog
import com.woocommerce.android.util.WooLog.T
import kotlinx.parcelize.Parcelize
import org.wordpress.android.fluxc.model.shippinglabels.WCShippingLabelModel.ShippingLabelAddress
import java.util.*

@Parcelize
data class Address(
    val company: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val country: String,
    val state: String,
    val address1: String,
    val address2: String,
    val city: String,
    val postcode: String,
    val email: String
) : Parcelable {
    private fun orderAddressToString(): String {
        return StringBuilder()
            .appendWithIfNotEmpty(this.address1)
            .appendWithIfNotEmpty(this.address2, "\n")
            .appendWithIfNotEmpty(this.city, "\n")
            .appendWithIfNotEmpty(this.state)
            .appendWithIfNotEmpty(this.postcode)
            .toString()
    }

    private fun getAddressData(): AddressData {
        return AddressData.builder()
            .setAddressLines(mutableListOf(address1, address2))
            .setLocality(city)
            .setAdminArea(state)
            .setPostalCode(postcode)
            .setCountry(country)
            .setOrganization(company)
            .build()
    }

    fun getEnvelopeAddress(): String {
        return getAddressData().takeIf { it.postalCountry != null }?.let {
            val formatInterpreter = FormatInterpreter(FormOptions().createSnapshot())
            try {
                val separator = System.getProperty("line.separator") ?: ""
                formatInterpreter.getEnvelopeAddress(it).joinToString(separator)
            } catch (e: NullPointerException) {
                // in rare cases getEnvelopeAddress() will throw a NPE due to invalid region data
                // see https://github.com/woocommerce/woocommerce-android/issues/509
                WooLog.e(T.UTILS, e)
                this.orderAddressToString()
            }
        } ?: this.orderAddressToString()
    }

    fun getFullAddress(name: String, address: String, country: String): String {
        var fullAddr = ""
        if (name.isNotBlank()) fullAddr += "$name\n"
        if (address.isNotBlank()) fullAddr += "$address\n"
        if (country.isNotBlank()) fullAddr += country
        return fullAddr
    }

    fun getCountryLabelByCountryCode(): String {
        val locale = Locale(Locale.getDefault().language, country)
        return locale.displayCountry
    }

    fun hasInfo(): Boolean {
        return firstName.isNotEmpty() || lastName.isNotEmpty() ||
            address1.isNotEmpty() || country.isNotEmpty() ||
            phone.isNotEmpty() || email.isNotEmpty() ||
            state.isNotEmpty() || city.isNotEmpty()
    }

    fun isEmpty(): Boolean {
        return company.isEmpty() &&
            firstName.isEmpty() &&
            lastName.isEmpty() &&
            phone.isEmpty() &&
            country.isEmpty() &&
            state.isEmpty() &&
            address1.isEmpty() &&
            address2.isEmpty() &&
            city.isEmpty() &&
            postcode.isEmpty() &&
            email.isEmpty()
    }

    fun toShippingLabelModel(): ShippingLabelAddress {
        return ShippingLabelAddress(
            company = company,
            name = "$firstName $lastName".trim().takeIf { it.isNotBlank() },
            phone = phone,
            address = address1,
            address2 = address2,
            city = city,
            postcode = postcode,
            state = state,
            country = country
        )
    }

    /**
     * Compares this address's physical location to the other one
     */
    fun isSamePhysicalAddress(otherAddress: Address): Boolean {
        return country == otherAddress.country &&
            state == otherAddress.state &&
            address1 == otherAddress.address1 &&
            address2 == otherAddress.address2 &&
            city == otherAddress.city &&
            postcode == otherAddress.postcode
    }

    override fun toString(): String {
        return StringBuilder()
            .appendWithIfNotEmpty(this.company)
            .appendWithIfNotEmpty("$firstName $lastName".trim(), "\n")
            .appendWithIfNotEmpty(this.address1, "\n")
            .appendWithIfNotEmpty(this.address2, "\n")
            .appendWithIfNotEmpty(this.city, "\n")
            .appendWithIfNotEmpty(this.state)
            .appendWithIfNotEmpty(this.postcode, " ")
            .appendWithIfNotEmpty(getCountryLabelByCountryCode(), "\n")
            .toString()
    }
}
