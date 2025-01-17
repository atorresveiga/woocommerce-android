package com.woocommerce.android.ui.orders.cardreader

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.woocommerce.android.R

sealed class ViewState(
    @StringRes open val hintLabel: Int? = null,
    @StringRes open val headerLabel: Int? = null,
    @StringRes val paymentStateLabel: Int? = null,
    @DimenRes val paymentStateLabelTopMargin: Int = R.dimen.major_275,
    @DrawableRes val illustration: Int? = null,
    open val isProgressVisible: Boolean = false,
    val primaryActionLabel: Int? = null,
    val secondaryActionLabel: Int? = null,
    val tertiaryActionLabel: Int? = null,
) {
    open val onPrimaryActionClicked: (() -> Unit)? = null
    open val onSecondaryActionClicked: (() -> Unit)? = null
    open val onTertiaryActionClicked: (() -> Unit)? = null
    open val amountWithCurrencyLabel: String? = null

    object LoadingDataState : ViewState(
        headerLabel = R.string.card_reader_payment_collect_payment_loading_header,
        hintLabel = R.string.card_reader_payment_collect_payment_loading_hint,
        paymentStateLabel = R.string.card_reader_payment_collect_payment_loading_payment_state,
        isProgressVisible = true
    )

    data class FailedPaymentState(
        private val errorType: PaymentFlowError,
        override val amountWithCurrencyLabel: String?,
        private val primaryLabel: Int? = R.string.try_again,
        override val onPrimaryActionClicked: (() -> Unit)
    ) : ViewState(
        headerLabel = R.string.card_reader_payment_payment_failed_header,
        paymentStateLabel = errorType.message,
        paymentStateLabelTopMargin = R.dimen.major_100,
        primaryActionLabel = primaryLabel,
        illustration = R.drawable.img_products_error
    )

    data class CollectPaymentState(
        override val amountWithCurrencyLabel: String,
        override val hintLabel: Int = R.string.card_reader_payment_collect_payment_hint,
        override val headerLabel: Int = R.string.card_reader_payment_collect_payment_header,
    ) : ViewState(
        paymentStateLabel = R.string.card_reader_payment_collect_payment_state,
        illustration = R.drawable.img_card_reader_available
    )

    data class ProcessingPaymentState(override val amountWithCurrencyLabel: String) :
        ViewState(
            hintLabel = R.string.card_reader_payment_processing_payment_hint,
            headerLabel = R.string.card_reader_payment_processing_payment_header,
            paymentStateLabel = R.string.card_reader_payment_processing_payment_state,
            illustration = R.drawable.img_card_reader_available
        )

    data class CapturingPaymentState(override val amountWithCurrencyLabel: String) :
        ViewState(
            hintLabel = R.string.card_reader_payment_capturing_payment_hint,
            headerLabel = R.string.card_reader_payment_capturing_payment_header,
            paymentStateLabel = R.string.card_reader_payment_capturing_payment_state,
            illustration = R.drawable.img_card_reader_available
        )

    data class PaymentSuccessfulState(
        override val amountWithCurrencyLabel: String,
        override val onPrimaryActionClicked: (() -> Unit),
        override val onSecondaryActionClicked: (() -> Unit),
        override val onTertiaryActionClicked: (() -> Unit)
    ) : ViewState(
        headerLabel = R.string.card_reader_payment_completed_payment_header,
        illustration = R.drawable.img_celebration,
        primaryActionLabel = R.string.card_reader_payment_print_receipt,
        secondaryActionLabel = R.string.card_reader_payment_send_receipt,
        tertiaryActionLabel = R.string.card_reader_payment_save_for_later,
    )

    data class PrintingReceiptState(
        override val amountWithCurrencyLabel: String,
        val receiptUrl: String,
        val documentName: String
    ) : ViewState(
        headerLabel = R.string.card_reader_payment_completed_payment_header,
        illustration = null,
        primaryActionLabel = null,
        secondaryActionLabel = null,
    ) {
        override val isProgressVisible = true
    }

    object ReFetchingOrderState : ViewState(
        headerLabel = R.string.card_reader_payment_fetch_order_loading_header,
        hintLabel = R.string.card_reader_payment_fetch_order_loading_hint,
        paymentStateLabel = R.string.card_reader_payment_fetch_order_loading_payment_state,
        isProgressVisible = true
    )
}

sealed class PaymentFlowError(@StringRes val message: Int) {
    object FetchingOrderFailed : PaymentFlowError(R.string.order_error_fetch_generic)
    object NoNetwork : PaymentFlowError(R.string.card_reader_payment_failed_no_network_state)
    object Server : PaymentFlowError(R.string.card_reader_payment_failed_server_error_state)
    object Generic : PaymentFlowError(R.string.card_reader_payment_failed_unexpected_error_state)
    object AmountTooSmall : Declined(R.string.card_reader_payment_failed_amount_too_small), NonRetryableError
    object Unknown : Declined(R.string.card_reader_payment_failed_unknown)
    sealed class Declined(message: Int) : PaymentFlowError(message) {
        object Temporary : Declined(R.string.card_reader_payment_failed_temporary)
        object Fraud : Declined(R.string.card_reader_payment_failed_fraud), NonRetryableError
        object Generic : Declined(R.string.card_reader_payment_failed_generic)
        object InvalidAccount : Declined(R.string.card_reader_payment_failed_invalid_account), NonRetryableError
        object CardNotSupported : Declined(R.string.card_reader_payment_failed_card_not_supported)
        object CurrencyNotSupported :
            Declined(R.string.card_reader_payment_failed_currency_not_supported), NonRetryableError

        object DuplicateTransaction :
            Declined(R.string.card_reader_payment_failed_duplicate_transaction), NonRetryableError

        object ExpiredCard : Declined(R.string.card_reader_payment_failed_expired_card)
        object IncorrectPostalCode :
            Declined(R.string.card_reader_payment_failed_incorrect_postal_code), NonRetryableError

        object InsufficientFunds : Declined(R.string.card_reader_payment_failed_insufficient_funds)
        object InvalidAmount : Declined(R.string.card_reader_payment_failed_invalid_amount), NonRetryableError
        object PinRequired : Declined(R.string.card_reader_payment_failed_pin_required)
        object TooManyPinTries : Declined(R.string.card_reader_payment_failed_too_many_pin_tries)
        object TestCard : Declined(R.string.card_reader_payment_failed_test_card)
        object TestModeLiveCard : Declined(R.string.card_reader_payment_failed_test_mode_live_card)
    }

    interface NonRetryableError
}
