package edu.praktismp.gamesessiontracker.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import edu.praktismp.gamesessiontracker.R
import edu.praktismp.gamesessiontracker.model.Session

class SessionAdapter(
    private val context: Context,
    private val sessions: List<Session>,
    private val displayText: List<String>,
    private val onEdit: (Session) -> Unit,
    private val onDelete: (Session) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = sessions.size

    override fun getItem(position: Int): Any = sessions[position]

    override fun getItemId(position: Int): Long = sessions[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_session, parent, false)

        val tvInfo = row.findViewById<TextView>(R.id.tvSessionInfo)
        val btnEdit = row.findViewById<ImageButton>(R.id.btnEdit)
        val btnDelete = row.findViewById<ImageButton>(R.id.btnDelete)

        tvInfo.text = displayText[position]

        btnEdit.setOnClickListener { onEdit(sessions[position]) }
        btnDelete.setOnClickListener { onDelete(sessions[position]) }

        return row
    }
}
