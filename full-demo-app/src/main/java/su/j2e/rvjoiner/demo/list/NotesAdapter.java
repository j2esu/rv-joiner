package su.j2e.rvjoiner.demo.list;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ru.java2e.android.rvadapterjoinerdemo.R;
import su.j2e.rvjoiner.demo.model.Note;

/**
 * This adapter changes items on click. To get position of clicked element it uses
 * {@link RecyclerView.ViewHolder#getItemId()}, and then find data with the corresponded id in
 * {@link #changeNote(long)}. This is an example how not to use
 * {@link su.j2e.rvjoiner.RvJoiner.RealPositionProvider}. Example of using this class (to avoid
 * manual detecting of position by id) you can find in {@link IssuesAdapter}
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteVh> {

	private static final String TAG = NotesAdapter.class.getName();
	private List<Note> notes = new LinkedList<>();

	public NotesAdapter() {
		setHasStableIds(true);
	}

	public void updateData(List<Note> notes) {
		this.notes = notes;
		notifyDataSetChanged();
	}

	public void changeNote(long noteId) {
		ListIterator<Note> iterator = notes.listIterator();
		Note currentNote;
		while (iterator.hasNext()) {
			currentNote = iterator.next();
			if (currentNote.getId() == noteId) {
				iterator.remove();
				iterator.add(new Note());
				notifyItemChanged(iterator.previousIndex());
				return;
			}
		}
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
					Log.d(TAG, "onClick: id " + getItemId());
					changeNote(getItemId());
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
