package io.falu.identity.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
        getSerializable(key, T::class.java)
    }

    else -> {
        @Suppress("DEPRECATION") getSerializable(key) as? T
    }
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
        getSerializableExtra(key, T::class.java)
    }

    else -> {
        @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
        getParcelableExtra(key, T::class.java)
    }

    else -> {
        @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
        getParcelable(key, T::class.java)
    }

    else -> {
        @Suppress("DEPRECATION") getParcelable(key) as? T
    }
}