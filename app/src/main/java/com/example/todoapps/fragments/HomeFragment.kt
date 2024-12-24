package com.example.todoapps.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapps.R
import com.example.todoapps.databinding.FragmentHomeBinding
import com.example.todoapps.databinding.NoteDialogBinding
import com.example.todoapps.models.Note
import com.example.todoapps.adapters.NoteAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment(), NoteAdapter.NoteActions {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private lateinit var noteAdapter: NoteAdapter
    private val noteList = mutableListOf<Note>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Notes")

        // Setup RecyclerView with the Adapter
        noteAdapter = NoteAdapter(noteList, this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }

        // Fetch notes from Firebase
        fetchNotes()

        // Add button functionality
        binding.addButton.setOnClickListener {
            showNoteDialog(null)
        }
    }

    private fun fetchNotes() {
        // Fetching notes from Firebase and updating the RecyclerView
        database.get().addOnSuccessListener { snapshot ->
            noteList.clear()  // Clear the list before adding new data
            snapshot.children.forEach {
                val note = it.getValue(Note::class.java)
                note?.let { noteList.add(it) }
            }
            noteAdapter.notifyDataSetChanged()  // Notify adapter that data has changed
        }
    }

    private fun showNoteDialog(note: Note?) {
        // Inflating the Note Dialog for adding or editing notes
        val dialogBinding = NoteDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        // Pre-filling the content if it's an existing note
        dialogBinding.noteEditText.setText(note?.content ?: "")

        // Save button functionality
        dialogBinding.saveButton.setOnClickListener {
            val content = dialogBinding.noteEditText.text.toString().trim()
            if (content.isNotEmpty()) {
                saveOrUpdateNote(content, note?.id)  // Save or update the note
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Note content cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancel button functionality
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveOrUpdateNote(content: String, id: String?) {
        // Save or Update the note in Firebase
        val noteId = id ?: database.push().key ?: return
        val note = Note(noteId, content)
        database.child(noteId).setValue(note).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Note saved!", Toast.LENGTH_SHORT).show()
                fetchNotes()  // Refresh notes after saving
            } else {
                Toast.makeText(requireContext(), "Failed to save note!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditClicked(note: Note) {
        // Edit the note when clicked
        showNoteDialog(note)
    }

    override fun onDeleteClicked(note: Note) {
        // Show confirmation dialog before deleting the note
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                database.child(note.id).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Note deleted!", Toast.LENGTH_SHORT).show()
                        fetchNotes()  // Refresh notes after deletion
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete note!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
