package com.komig.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform