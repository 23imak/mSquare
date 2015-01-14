package com.asif.notes;

import java.util.List;

import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private static final int EDITOR_ACTIVITY_REQUEST = 1001;
	private static final int MENU_DELETE_ID = 1002;
	private int currentNoteId;
	private NotesDataSource datasource;
	List<NoteItem> notesList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		registerForContextMenu(getListView());  // hold the list, prompts a option
		
		datasource = new  NotesDataSource(this);
		
		refreshDisplay();

	
	}


    public interface OnRefreshListListener {
        public void onRefresh();
    }



	//For Testing
/*		note = datasource.findAll();
		NoteItem note = notes.get(0); 
		note.setText("Updated!");
		datasource.update(note);
		
		notes = datasource.findAll();
		note = notes.get(0);
		
		Log.i("NOTES", note.getKey() + ": " + note.getText());
	
*/
	private void refreshDisplay() {
		// TODO Auto-generated method stub
		notesList = datasource.findAll();
		ArrayAdapter<NoteItem> 	adapter = new ArrayAdapter<NoteItem>(this, R.layout.list_item_layout,notesList);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		if (item.getItemId() == R.id.action_create) {
			createNote();
		}
        if (item.getItemId() == R.id.notification){
            Intent intent = new Intent(this,Notification.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.profile){
            Intent intent = new Intent(this,Profile.class);
            startActivity(intent);
        }
		
		return super.onOptionsItemSelected(item);
	}
	private void createNote() {
		NoteItem note = NoteItem.getnew();
		Intent intent = new Intent(this,NoteEditorActivity.class);
		intent.putExtra("key", note.getKey());
		intent.putExtra("text",note.getText());
		startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
		
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		NoteItem note = notesList.get(position);
		Intent intent = new Intent(this,NoteEditorActivity.class);
		intent.putExtra("key", note.getKey());
		intent.putExtra("text",note.getText());
		startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
			NoteItem note = new NoteItem();
			note.setKey(data.getStringExtra("key"));
			note.setText(data.getStringExtra("text"));
			datasource.update(note);
			refreshDisplay();
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		currentNoteId = (int)info.id;
		menu.add(0,MENU_DELETE_ID, 0, "Delete");
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if (item.getItemId() == MENU_DELETE_ID) {
			NoteItem note = notesList.get(currentNoteId);
			datasource.remove(note);
			refreshDisplay();
		}
		
		
		return super.onContextItemSelected(item);
	}
}
