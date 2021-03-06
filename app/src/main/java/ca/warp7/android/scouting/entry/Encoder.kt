package ca.warp7.android.scouting.entry

fun String.toBoard() = Board.values().firstOrNull { it.name == this }


/**
 * 24-bit data point encoder, into 4 base64 chars
 * 000000 000000 000000 000000
 * bit 1-6: type
 * bit 7-10: value
 * bit 11-24: time
 */
fun StringBuilder.appendDataPoint(dp: DataPoint) {
    val intTime = (dp.time * 100).toInt()

    val a = dp.type
    val b = (dp.value shl 2) or ((intTime and (0b11 shl 12)) shr 12)
    val c = (intTime and (0b111111 shl 6)) shr 6
    val d = intTime and 0b111111

    append(toBase64(a))
    append(toBase64(b))
    append(toBase64(c))
    append(toBase64(d))
}

fun encodeDataPoint(dp: DataPoint): String {
    val builder = StringBuilder()
    builder.appendDataPoint(dp)
    return builder.toString()
}

fun fromBase64(ch: Int): Int = when (ch) {
    '/'.toInt() -> 63
    '+'.toInt() -> 62
    in 48..57 -> ch + 4
    in 65..90 -> ch - 65
    in 97..122 -> ch - 71
    else -> throw IllegalArgumentException("Invalid Base64 Input")
}

fun toBase64(i: Int): Char {
    require(!(i < 0 || i > 63)) { "Invalid Integer Input" }
    return when {
        i < 26 -> (i + 65).toChar()
        i < 52 -> (i + 71).toChar()
        i < 62 -> (i - 4).toChar()
        i == 62 -> '+'
        else -> '/'
    }
}