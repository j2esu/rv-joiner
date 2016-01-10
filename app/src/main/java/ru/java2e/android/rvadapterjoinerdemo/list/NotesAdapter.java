package ru.java2e.android.rvadapterjoinerdemo.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import ru.java2e.android.rvadapterjoinerdemo.R;
import ru.java2e.android.rvadapterjoinerdemo.model.Note;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteVH> {

	private List<Note> notes = new LinkedList<>();

	/**
	 * Used to update adapter data
	 */
	public void updateData(List<Note> notes) {
		this.notes = notes;
		notifyDataSetChanged();
	}

	@Override
	public NoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NoteVH(parent);
	}

	@Override
	public void onBindViewHolder(NoteVH holder, int position) {
		holder.bind(notes.get(position));
	}

	@Override
	public int getItemCount() {
		return notes.size();
	}

	protected class NoteVH extends RecyclerView.ViewHolder {

		private final TextView textTv;

		private NoteVH(ViewGroup parent) {
			super(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.note_item, parent, false));
			textTv = (TextView) itemView.findViewById(R.id.note_item_text);
		}

		private void bind(Note note) {
			textTv.setText(note.getText());
			//bind note color as layout background
			itemView.setBackgroundColor(note.getColor());
		}

	}

}
