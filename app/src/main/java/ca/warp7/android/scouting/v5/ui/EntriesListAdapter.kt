package ca.warp7.android.scouting.v5.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.entry.Board.*
import ca.warp7.android.scouting.v5.entry.EntryItem
import ca.warp7.android.scouting.v5.entry.EntryItemState.*

class EntriesListAdapter(
    context: Context,
    scheduleItems: List<EntryItem>
) : ArrayAdapter<EntryItem>(context, 0, scheduleItems) {

    private val mInflater = LayoutInflater.from(context)
    private val red = ContextCompat.getColor(context, R.color.colorRed)
    private val blue = ContextCompat.getColor(context, R.color.colorBlue)
    private val gray = ContextCompat.getColor(context, R.color.colorGray)
    private val yellow = ContextCompat.getColor(context, R.color.colorAlmostYellow)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = if (convertView is LinearLayout)
            convertView else mInflater.inflate(R.layout.list_item_v5_match, parent, false)
        getItem(position)?.apply {
            when (state) {
                Waiting -> itemView.setBackgroundColor(0)
                Completed -> itemView.setBackgroundColor(0x6080ff88)
                Added -> itemView.setBackgroundColor(0x60ffe088)
            }

            itemView.findViewById<TextView>(R.id.match_number).text = match.let {
                val split = it.split("_")
                if (split.size == 2) split[1] else it
            }

            val red1 = itemView.findViewById<TextView>(R.id.red_1)
            val red2 = itemView.findViewById<TextView>(R.id.red_2)
            val red3 = itemView.findViewById<TextView>(R.id.red_3)
            val blue1 = itemView.findViewById<TextView>(R.id.blue_1)
            val blue2 = itemView.findViewById<TextView>(R.id.blue_2)
            val blue3 = itemView.findViewById<TextView>(R.id.blue_3)
            val teamsArray = arrayOf(red1, red2, red3, blue1, blue2, blue3)
            teamsArray.forEach { it.setTextColor(gray) }
            when (board) {
                R1 -> red1.setTextColor(red)
                R2 -> red2.setTextColor(red)
                R3 -> red3.setTextColor(red)
                B1 -> blue1.setTextColor(blue)
                B2 -> blue2.setTextColor(blue)
                B3 -> blue3.setTextColor(blue)
                RX -> arrayOf(red1, red2, red3).forEach { it.setTextColor(red) }
                BX -> arrayOf(blue1, blue2, blue3).forEach { it.setTextColor(blue) }
            }
            if (teams.size > 5) {
                for (i in 0 until 6) {
                    val number = teams[i]
                    teamsArray[i].text = if (number > 0) number.toString() else "----"
                }
            }
        }
        return itemView
    }
}