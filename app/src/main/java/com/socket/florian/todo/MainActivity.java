package com.socket.florian.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.socket.florian.todo.storage.DataManager;
import com.socket.florian.todo.storage.Date;
import com.socket.florian.todo.storage.Project;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity{

    ProjectAdapter _adapter;
    DataManager _dataManager;
    SearchView _searchView;
    AlertDialog _dialog;
    Date _date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createDialog();
        _dataManager = DataManager.getInstance();
        _dataManager.open(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Projets");
        setSupportActionBar(toolbar);
        ListView view = (ListView) findViewById(R.id.list);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //MainActivity.this.createNewProject();
                if(_dialog.getWindow() != null){
                    _dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }
                MainActivity.this._dialog.show();
                setDatePickerButton((Button) _dialog.findViewById(R.id.startDate));
            }
        });
        _adapter = new ProjectAdapter(this);
        view.setAdapter(_adapter);
        view.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

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
                for(int i = 0; i<MainActivity.this._adapter.getCount(); i++){
                    state.add(false);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_delete){
                    for(int i=state.size()-1;i>=0;i--){
                        if(state.get(i)){
                            _dataManager.removeProject((Project) _adapter.getItem(i));
                        }
                    }
                    MainActivity.this.updateProjects();
                    actionMode.finish();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {}

        });
        updateProjects();
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                intent.putExtra("PROJECT",(Project)_adapter.getItem(i));
                MainActivity.this.startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        _searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        _searchView.setMaxWidth(Integer.MAX_VALUE);
        _searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        _searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                _adapter.getFilter().filter(s);
                return true;
            }
        });
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        closeSearchView();
        updateProjects();
    }

    private void updateProjects(){
        _adapter.updateAll(_dataManager.getProjects());
    }

    @Override
    public void finish(){
        _dataManager.close();
        super.finish();
    }

    @Override
    public void onBackPressed(){
        if(!closeSearchView()) {
            super.onBackPressed();
        }
    }

    private boolean closeSearchView(){
        if (_searchView != null && !_searchView.isIconified()) {
            _searchView.setIconified(true);
            return true;
        }
        return false;
    }

    private void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialogTheme);
        builder.setView(R.layout.new_project_dialog);
        builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText text = (EditText) _dialog.findViewById(R.id.editName);
                String name;
                if(text == null || text.getText().toString().equals("")){
                    name = "nouveau";
                }else{
                    name = text.getText().toString();
                }
                _dataManager.createNewProject(name, _date);
                closeSearchView();
                updateProjects();
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                closeSearchView();
            }
        });
        _dialog = builder.create();
    }

    //TODO : Modify the rocket icon when we change the starting date
    private void setDatePickerButton(final Button v){
        _date = Date.currentDate();
        setProjectStartDateOn(_date, v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new DatePickerDialog(MainActivity.this,
                        R.style.DatePickerDialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                _date = new Date(year, month+1, dayOfMonth);
                                Log.d("D", _date.toString('-'));
                                setProjectStartDateOn(_date, v);
                            }
                        },
                        _date.year(),
                        _date.month()-1,
                        _date.dayOfMonth());
                dialog.show();
            }
        });
    }

    private void setProjectStartDateOn(Date date, Button v){
        String strDate = date.toVerboseString();
        v.setText(strDate);
    }
}
