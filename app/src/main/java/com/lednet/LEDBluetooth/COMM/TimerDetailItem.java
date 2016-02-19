package com.lednet.LEDBluetooth.COMM;

import java.util.Calendar;


public class TimerDetailItem
{
	public boolean mEnableTimer = true;
	public int mYearFrom2000 = 0;
	public int mMonth = 0;
	public int mDay = 0;
	public int mHour= 0;
	public int mMinute= 0;
	public int mSec= 0;
	public byte mWeek= 0;
	public byte mModeValue = 0;
	public byte mValue1 = 0;
	public byte mValue2 = 0;
	public byte mValue3 = 0;
	public byte mValue4 = 0;
	public boolean mPower;

	public static TimerDetailItem createTimerItemForAfterMinute(int afterMinute)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, afterMinute);

		int year = cal.get(Calendar.YEAR)-2000;
		int month = cal.get(Calendar.MONTH)+1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour  = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);

		//一次性，多少分钟后
		TimerDetailItem itm= new TimerDetailItem();
		itm.mYearFrom2000 = year;
		itm.mMonth = month;
		itm.mDay = day;
		itm.mHour = hour;
		itm.mMinute = minute;
		itm.mSec = sec;
		itm.mWeek = 0;
		itm.mModeValue = 0;
		itm.mPower = false;
		return itm;
	}

}
