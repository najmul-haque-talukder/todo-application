package com.example.todoapps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapps.databinding.ItemNoteBinding
import com.example.todoapps.models.Note

class NoteAdapter(
    private val notes: List<Note>,
    private val actions: NoteActions
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.binding.noteTextView.text = note.content

        holder.binding.editButton.setOnClickListener {
            actions.onEditClicked(note)
        }

        holder.binding.deleteButton.setOnClickListener {
            actions.onDeleteClicked(note)
        }
    }

    override fun getItemCount(): Int = notes.size

    interface NoteActions {
        fun onEditClicked(note: Note)
        fun onDeleteClicked(note: Note)
    }
}
