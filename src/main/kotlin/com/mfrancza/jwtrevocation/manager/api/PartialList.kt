package com.mfrancza.jwtrevocation.manager.api

import com.mfrancza.jwtrevocation.rules.Rule
import kotlinx.serialization.Serializable

@Serializable
data class PartialList(
    val rules: List<Rule>,
    val cursor: String?
)