package com.hz.myclock;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
    	protected static final int TIME = 0;
		private TextView tv_time;
    	private MyClock clock;
    	private String hour;
    	private String min;
    	private String sec;
    	
    	private Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
					case TIME:
						tv_time.setText(msg.obj.toString());
				}
			}
    		
    		
    	};
    	
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            tv_time = (TextView)rootView.findViewById(R.id.tv_time);
            clock = (MyClock)rootView.findViewById(R.id.myclock);
            
            Button bt_newyork = (Button)rootView.findViewById(R.id.bt_newyork);
            bt_newyork.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					clock.setNewYorkTime();
					
				}
			});
            
            new Thread(){
            	public void run() {
            			
            			while(true){
            				hour = clock.getHour();
                            min = clock.getMin();
                            sec = clock.getSec();
            				String time = hour+":"+min+":"+sec;
                            Message msg = Message.obtain();
                            msg.what = TIME;
                            msg.obj = time;
                            try {
								sleep(300);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            handler.sendMessage(msg);
            				
            			}
            			
            		
            	};
            }.start();
            
            return rootView;
        }
    }

}
