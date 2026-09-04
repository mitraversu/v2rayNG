package com.v2ray.ang.dto.entities

data class ServerAffiliationInfo(
    var testDelayMillis: Long = 0L,
    // Mitra: last 12 ping results for minimal sparkline — persisted per server
    var pingHistory: MutableList<Long> = mutableListOf()
)
