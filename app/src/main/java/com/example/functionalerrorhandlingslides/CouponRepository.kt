package com.example.functionalerrorhandlingslides

import arrow.core.Either
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.CallError

class CouponRepository {

    fun getCouponId(productId: String): Either<CallError, String> {
        return "couponId".right()
    }
}
