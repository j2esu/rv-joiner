package su.j2e.rvjoiner.getstarted;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyVh> {

	private String[] data = {"First", "Second", "Third"};

	@Override
	public MyVh onCreateViewHolder(ViewGroup parent, int viewType) {
		return new MyVh(parent);
	}

	@Override
	public void onBindViewHolder(MyVh holder, int position) {
		holder.bind(data[position]);
	}

	@Override
	public int getItemCount() {
		return data.length;
	}

	protected class MyVh extends RecyclerView.ViewHolder {

		private final TextView text;

		private MyVh(ViewGroup parent) {
			super(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_item, parent, false));
			text = (TextView) itemView.findViewById(R.id.my_item_text);
		}

		private void bind(String s) {
			text.setText(s);
		}

	}

}
