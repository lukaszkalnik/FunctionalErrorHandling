package com.example.functionalerrorhandlingslides

import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("scan/{ean}")
    suspend fun scanBarcode(@Path("ean") ean: String): Product

    @POST("cart/add")
    suspend fun addToCart(@Body cartItem: CartItem): Cart

    @POST("coupon")
    suspend fun redeemCoupon(@Body productCouponToRedeem: ProductCouponToRedeem): Cart

    @POST("checkout/{cartId}")
    suspend fun checkout(@Path("cartId") cartId: String): Unit

    companion object {

        fun instance(): ApiService = Retrofit.Builder()
            .baseUrl("https://msco.rewe.com/")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(EitherCallAdapterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

data class Product(
    val id: String,
    val name: String,
    val price: Float,
    val discountedPrice: Float?,
)

data class CartItem(
    val product: Product,
    val quantity: Int,
)

data class Cart(
    val id: String,
    val items: List<CartItem>,
    val totalPrice: Float,
    val totalDiscountedPrice: Float?,
)

data class ProductCouponToRedeem(
    val cartId: String,
    val productId: String,
    val couponId: String,
)
