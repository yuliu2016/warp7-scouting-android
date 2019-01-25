package ca.warp7.android.scouting.v5.ui

/**
 * Base interface for all custom controls
 * @since v0.2.0
 */

interface BaseFieldWidget {
    fun updateControlState()
    val fieldData: FieldData?
}
