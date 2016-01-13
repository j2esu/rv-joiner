package su.j2e.rvjoinerdemo.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import ru.java2e.android.rvadapterjoinerdemo.R;
import su.j2e.rvjoinerdemo.model.Note;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteVh> {

	private List<Note> notes = new LinkedList<>();

	public NotesAdapter() {
		setHasStableIds(true);
	}

	public void updateData(List<Note> notes) {
		this.notes = notes;
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return notes.get(position).getId();
	}

	@Override
	public NoteVh onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NoteVh(parent);
	}

	@Override
	public void onBindViewHolder(NoteVh holder, int position) {
		holder.bind(notes.get(position));
	}

	@Override
	public int getItemCount() {
		return notes.size();
	}

	protected class NoteVh extends RecyclerView.ViewHolder {

		private final TextView textTv;

		private NoteVh(ViewGroup parent) {
			super(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.note_item, parent, false));
			textTv = (TextView) itemView.findViewById(R.id.note_item_text);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(), "Id: " + getItemId(), Toast.LENGTH_SHORT).show();
				}
			});
		}

		private void bind(Note note) {
			textTv.setText(note.getText());
			//bind note color as layout background
			itemView.setBackgroundColor(note.getColor());
		}

	}

}
