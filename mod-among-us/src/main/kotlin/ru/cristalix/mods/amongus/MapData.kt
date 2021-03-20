package ru.cristalix.mods.amongus

data class MapData(
    val maxX: Double,
    val maxZ: Double,
    val playerTexturePath: String,
    val mapTexturePath: String,
    val textureSize: Double,
    val markers: List<Marker>
)
