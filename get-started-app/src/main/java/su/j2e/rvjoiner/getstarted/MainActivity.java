package su.j2e.rvjoiner.getstarted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//init your RecyclerView as usual
		RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
		rv.setLayoutManager(new LinearLayoutManager(this));

		//construct a joiner
		RvJoiner rvJoiner = new RvJoiner(true, false);
		rvJoiner.add(new JoinableLayout(R.layout.header));
		rvJoiner.add(new JoinableAdapter(new MyAdapter()));
		rvJoiner.add(new JoinableLayout(R.layout.devider));
		rvJoiner.add(new JoinableAdapter(new MyAdapter()));
		rvJoiner.add(new JoinableLayout(R.layout.footer));

		//set join adapter to your RecyclerView
		rv.setAdapter(rvJoiner.getAdapter());

	}
}
