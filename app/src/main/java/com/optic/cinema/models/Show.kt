package com.optic.cinema.models

import com.optic.cinema.adapters.AppAdapter

sealed interface Show : AppAdapter.Item {
    var isFavorite: Boolean
}

