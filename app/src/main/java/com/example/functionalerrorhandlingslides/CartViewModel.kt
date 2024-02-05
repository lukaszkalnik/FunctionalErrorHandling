package com.example.functionalerrorhandlingslides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.either
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val api = ApiService.instance()
    private val couponRepository = CouponRepository()

    val uiState: MutableStateFlow<CartState> = MutableStateFlow(Loading)

    fun onBarcodeScanAndCheckout(ean: String) {
        viewModelScope.launch {
            val result = either {
                val product = api.scanBarcode(ean).bind()

                val cart = api.addToCart(
                    CartItem(
                        product = product,
                        quantity = 1,
                    )
                ).bind()

                val couponId = couponRepository.getCouponId(product.id).bind()

                val discountedCart = api.redeemCoupon(
                    ProductCouponToRedeem(
                        cartId = cart.id,
                        productId = product.id,
                        couponId = couponId,
                    )
                ).bind()

                api.checkout(cartId = discountedCart.id).bind()
                discountedCart.totalDiscountedPrice
            }

            result.fold(
                ifLeft = { callError ->
                    val message = when (callError) {
                        is HttpError -> callError.message
                        is IOError -> "Check your internet connection"
                        is UnexpectedCallError -> "Unknown error"
                    }
                    uiState.value = Error(message = message)
                },
                ifRight = { totalDiscountedCartPrice ->
                    uiState.value = Success(totalDiscountedCartPrice = totalDiscountedCartPrice)
                }
            )
        }
    }
}

sealed class CartState
data object Loading : CartState()
data class Error(val message: String) : CartState()
data class Success(val totalDiscountedCartPrice: Float) : CartState()
