package com.example.functionalerrorhandlingslides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val api = ApiService.instance()
    private val couponRepository = CouponRepository()

    fun onBarcodeScanAndCheckout(ean: String) {
        viewModelScope.launch {
            val product = api.scanBarcode(ean)

            val cart = api.addToCart(
                CartItem(
                    product = product,
                    quantity = 1,
                )
            )

            val couponId = couponRepository.getCouponId(product.id)

            val cartWithCoupon = api.redeemCoupon(
                ProductCouponToRedeem(
                    cartId = cart.id,
                    productId = product.id,
                    couponId = couponId,
                )
            )

            api.checkout(cartId = cartWithCoupon.id)
        }
    }
}
