package com.optic.cinema.models

import com.optic.cinema.adapters.AppAdapter

open class Provider(
    val name: String,
    val logo: String,
    val language: String,

    val provider: com.optic.cinema.providers.Provider,
) : AppAdapter.Item {


    override lateinit var itemType: AppAdapter.Type
}
