package my.todolist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ToDoList extends Activity {

	private static final int ADD_NEW_TODO = Menu.FIRST;
	private static final int REMOVE_TODO = ADD_NEW_TODO + 1;
	
	private static final int CONFIRM_REMOVE_DIALOG = 0;

	private EditText myEditText;
	private ListView myListView;
	private List<String> todoItems;
	private ArrayAdapter<String> todoItemsArrayAdapter;
	private boolean addingNew = false;
	private int selectedItem = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		myEditText = (EditText) findViewById(R.id.myEditText);
		myListView = (ListView) findViewById(R.id.myListView);

		todoItems = new ArrayList<String>();
		todoItemsArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.todolist_item, todoItems);

		myListView.setAdapter(todoItemsArrayAdapter);

		myEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
						todoItems.add(0, myEditText.getText().toString());
						todoItemsArrayAdapter.notifyDataSetChanged();
						myEditText.setText("");
						cancelAdd();
						return true;
					}
				}
				return false;
			}
		});

		registerForContextMenu(myListView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem addMenuItem = menu.add(0, ADD_NEW_TODO, Menu.NONE,
				R.string.add_new_item);
		MenuItem removeMenuItem = menu.add(0, REMOVE_TODO, Menu.NONE,
				R.string.remove_item);

		addMenuItem.setIcon(R.drawable.add);
		removeMenuItem.setIcon(R.drawable.delete);

		addMenuItem.setShortcut('1','a');
		removeMenuItem.setShortcut('2', 'r');

		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(R.string.list_view_item_context_menu_title);
		menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove_item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		int idx = myListView.getSelectedItemPosition();

		String removeTitle = getString(addingNew ? R.string.cancel
				: R.string.remove_item);
		MenuItem removeMenuItem = menu.findItem(REMOVE_TODO);
		
		removeMenuItem.setTitle(removeTitle);
		removeMenuItem.setVisible(addingNew || idx > -1);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		selectedItem = myListView.getSelectedItemPosition();
		
		switch (item.getItemId()) {
			case (REMOVE_TODO): {
				if (addingNew) {
					cancelAdd();
				} else {
					showDialog(CONFIRM_REMOVE_DIALOG);
				}
				return true;
			}
			case (ADD_NEW_TODO): {
				addNewItem();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		
		switch (item.getItemId()) {
			case (REMOVE_TODO): {
				AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
				selectedItem = menuInfo.position;
				showDialog(CONFIRM_REMOVE_DIALOG);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
	    switch(id) {
	    case CONFIRM_REMOVE_DIALOG:
	    	return createConfirmRemoveDialog();
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
	}
	
	private Dialog createConfirmRemoveDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.remove_confirm_message)
		       .setCancelable(false)
		       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                removeItem(selectedItem);
		           }
		       })
		       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		return alert;
	}
	
	private void cancelAdd() {
		addingNew = false;
		myEditText.setVisibility(View.GONE);
	}
	
	private void addNewItem() {
		addingNew = true;
		myEditText.setVisibility(View.VISIBLE);
		myEditText.requestFocus();
	}
	
	private void removeItem(int index) {
		if(index > -1) {
			todoItems.remove(index);
			todoItemsArrayAdapter.notifyDataSetChanged();
		}
	}
	
}