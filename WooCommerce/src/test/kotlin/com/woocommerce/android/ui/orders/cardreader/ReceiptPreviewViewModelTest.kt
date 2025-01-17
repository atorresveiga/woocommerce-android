package com.woocommerce.android.ui.orders.cardreader

import androidx.lifecycle.SavedStateHandle
import com.woocommerce.android.analytics.AnalyticsTracker.Stat.*
import com.woocommerce.android.analytics.AnalyticsTrackerWrapper
import com.woocommerce.android.initSavedStateHandle
import com.woocommerce.android.tools.SelectedSite
import com.woocommerce.android.ui.orders.cardreader.ReceiptEvent.PrintReceipt
import com.woocommerce.android.ui.orders.cardreader.ReceiptEvent.SendReceipt
import com.woocommerce.android.ui.orders.cardreader.ReceiptPreviewViewModel.ReceiptPreviewEvent.LoadUrl
import com.woocommerce.android.ui.orders.cardreader.ReceiptPreviewViewModel.ReceiptPreviewViewState.Content
import com.woocommerce.android.ui.orders.cardreader.ReceiptPreviewViewModel.ReceiptPreviewViewState.Loading
import com.woocommerce.android.util.PrintHtmlHelper.PrintJobResult.*
import com.woocommerce.android.viewmodel.BaseUnitTest
import com.woocommerce.android.viewmodel.MultiLiveEvent.Event.ShowSnackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.wordpress.android.fluxc.model.SiteModel

@ExperimentalCoroutinesApi
class ReceiptPreviewViewModelTest : BaseUnitTest() {
    private lateinit var viewModel: ReceiptPreviewViewModel

    private val selectedSite: SelectedSite = mock()
    private val tracker: AnalyticsTrackerWrapper = mock()

    private val savedState: SavedStateHandle = ReceiptPreviewFragmentArgs(
        receiptUrl = "testing url",
        billingEmail = "testing email",
        orderId = 999L
    ).initSavedStateHandle()

    @Before
    fun setUp() {
        viewModel = ReceiptPreviewViewModel(savedState, tracker, selectedSite)
        whenever(selectedSite.get()).thenReturn(SiteModel().apply { name = "testName" })
    }

    @Test
    fun `when screen shown, then loading receipt urls starts`() {
        assertThat(viewModel.event.value).isInstanceOf(LoadUrl::class.java)
    }

    @Test
    fun `when loading receipt url finishes, then content shown`() {
        viewModel.onReceiptLoaded()

        assertThat(viewModel.viewStateData.value).isInstanceOf(Content::class.java)
    }

    @Test
    fun `when progress state shown, then progress is visible and content hidden`() {
        val contentState = viewModel.viewStateData.value as Loading
        assertThat(contentState.isProgressVisible).isTrue
        assertThat(contentState.isContentVisible).isFalse
    }

    @Test
    fun `when content state shown, then progress is hidden and content visible`() {
        viewModel.onReceiptLoaded()

        val contentState = viewModel.viewStateData.value as Content
        assertThat(contentState.isProgressVisible).isFalse
        assertThat(contentState.isContentVisible).isTrue
    }

    @Test
    fun `when user clicks on send email, then send receipt event emitted`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.onSendEmailClicked()

            assertThat(viewModel.event.value).isInstanceOf(SendReceipt::class.java)
        }

    @Test
    fun `when user clicks on send email, then event tracked`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.onSendEmailClicked()

            verify(tracker).track(RECEIPT_EMAIL_TAPPED)
        }

    @Test
    fun `when email application not found, then SnackBar with error shown`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.onEmailActivityNotFound()

            assertThat(viewModel.event.value).isInstanceOf(ShowSnackbar::class.java)
        }

    @Test
    fun `when email application not found, then event tracked`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.onEmailActivityNotFound()

            verify(tracker).track(RECEIPT_EMAIL_FAILED)
        }

    @Test
    fun `when user clicks on print receipt, then print receipt event emitted`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.onPrintClicked()

            assertThat(viewModel.event.value).isInstanceOf(PrintReceipt::class.java)
        }

    @Test
    fun `when user clicks on print receipt, then event tracked`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.onPrintClicked()

            verify(tracker).track(RECEIPT_PRINT_TAPPED)
        }

    @Test
    fun `when printing receipt fails, then event tracked`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.onPrintResult(FAILED)

            verify(tracker).track(RECEIPT_PRINT_FAILED)
        }

    @Test
    fun `when user cancels printing receipt, then event tracked`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.onPrintResult(CANCELLED)

            verify(tracker).track(RECEIPT_PRINT_CANCELED)
        }

    @Test
    fun `when printing receipt succeeds, then event tracked`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.onPrintResult(STARTED)

            verify(tracker).track(RECEIPT_PRINT_SUCCESS)
        }
}
