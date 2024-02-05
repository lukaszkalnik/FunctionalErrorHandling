package com.example.functionalerrorhandlingslides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.either
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val api = ApiService.instance()
    private val couponRepository = CouponRepository()

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

                val cartWithCoupon = api.redeemCoupon(
                    ProductCouponToRedeem(
                        cartId = cart.id,
                        productId = product.id,
                        couponId = couponId,
                    )
                ).bind()

                api.checkout(cartId = cartWithCoupon.id).bind()
            }
        }
    }
}
