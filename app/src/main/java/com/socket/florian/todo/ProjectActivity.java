package com.socket.florian.todo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.socket.florian.todo.storage.Project;

public class ProjectActivity extends AppCompatActivity {

    Project _project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        _project = getIntent().getParcelableExtra("PROJECT");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(_project.getName());
        TextView text = (TextView) findViewById(R.id.text);
        String str = _project.getName() + " : " + _project.getStartDate().toVerboseString();
        text.setText(str);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }
}
