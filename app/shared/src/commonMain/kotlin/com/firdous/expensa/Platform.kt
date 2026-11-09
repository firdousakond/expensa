package com.firdous.expensa

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform