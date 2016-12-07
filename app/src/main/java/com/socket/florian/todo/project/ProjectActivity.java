package com.socket.florian.todo.project;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.socket.florian.todo.R;
import com.socket.florian.todo.storage.DataManager;
import com.socket.florian.todo.storage.Project;
import com.socket.florian.todo.storage.Task;

import java.util.ArrayList;
import java.util.List;


public class ProjectActivity extends AppCompatActivity {

    private Project _project;
    private TaskAdapter _adapter;
    private DataManager _dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        _dataManager = DataManager.getInstance();
        int id = getIntent().getIntExtra("PROJECT_ID", 0);
        if(id == 0){
            finish();
        }
        _project = _dataManager.getProject(id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(_project.getName());

        ListView list = (ListView) findViewById(R.id.list);
        _adapter = new TaskAdapter(this);
        list.setAdapter(_adapter);
        _adapter.updateAll(_project.getTasks());
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private List<Boolean> state;

            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
                state.set(i, b);
                ListView view = (ListView) findViewById(R.id.list);
                view.getChildAt(i).setActivated(true);
            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.menu_delete, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                state = new ArrayList<>();
                for(int i = 0; i<ProjectActivity.this._adapter.getCount(); i++){
                    state.add(false);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_delete){
                    for(int i=state.size()-1;i>=0;i--){
                        if(state.get(i)){
                            _dataManager.removeTask((Task) _adapter.getItem(i));
                        }
                    }
                    updateProject();
                    actionMode.finish();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {}

        });
        FloatingActionButton floating = (FloatingActionButton) findViewById(R.id.fab);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //_dataManager.createNewTask(_project, Task.create("nouveau"));
                //updateProject();
                Intent intent = new Intent(ProjectActivity.this, NewTaskActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == 0){
            updateProject();
        }
    }

    private void updateProject(){
        _project = _dataManager.getProject(_project.getId());
        _adapter.updateAll(_project.getTasks());
    }
}
