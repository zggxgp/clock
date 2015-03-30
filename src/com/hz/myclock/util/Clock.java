package com.hz.myclock.util;

public class Clock {
	
	private int hour;
	private int min;
	private int sec;
	private CountTime countTime;
	private static Clock instanceClock = null;
	
	protected Clock(int hour, int min, int sec) {
		super();
		this.hour = hour;
		this.min = min;
		this.sec = sec;
		countTime = new CountTime();
		countTime.start();
	}
	
	public static Clock getInstance(){
		if(instanceClock==null){
			instanceClock = new Clock(0, 0, 0);
		}
		
		return instanceClock;
	}
	
	
	private class CountTime extends Thread{
		
		private boolean flag = true;
		
		@Override
		public void run() {
			super.run();
			while(flag){
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sec+=1;
				if(sec>=60){
					min+=1;
					sec=0;
				}
				
				if(min>=60){
					hour+=1;
					min=0;
				}
				if(hour>=12){
					hour = 0;
				}
			}
			
		}
		
		public void stopTime(){
			flag = false;
		}
		
		public void runTime(){
			flag = true;
		}
		
	}
	
	public void set(int hour, int min, int sec){
		this.hour = hour;
		this.min = min;
		this.sec = sec;
	}
	
	public void stopTime(){
		countTime.stopTime();
	}
	
	public void runTime(){
		countTime.runTime();
	}
	
	
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getSec() {
		return sec;
	}
	public void setSec(int sec) {
		this.sec = sec;
	}
	
	
}
