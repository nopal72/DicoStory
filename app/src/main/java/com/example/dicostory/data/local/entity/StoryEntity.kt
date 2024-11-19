package com.example.dicostory.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
data class StoryEntity (
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    val id: String,

    @field:ColumnInfo(name = "name")
    val name: String? = null,

    @field:ColumnInfo(name = "description")
    val description: String? = null,

    @field:ColumnInfo(name = "photoUrl")
    val photoUrl: String? = null,

    @field:ColumnInfo(name = "createdAt")
    val createdAt: String? = null,

    @field:ColumnInfo(name = "lat")
    val lat: Double? = null,

    @field:ColumnInfo(name = "lon")
    val lon: Double? = null

): Parcelable