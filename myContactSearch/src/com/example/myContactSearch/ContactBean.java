package com.example.myContactSearch;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;

/**
 * 联系人实体类 2015/1/28 11:38
 */
public class ContactBean {

	private int contactId; // id
	private String desplayName; // 名称
	private String phoneNum; // 手机号码
	private String sortKey; // 排序key
	private Long photoId; // 头像id
	private String lookUpKey;
	private int selected = 0;
	private String formattedNumber;
	private String pinyin;

    public SortToken sortToken = new SortToken();
    public String sortLetters; //显示数据拼音的首字母
    public String simpleNumber;

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public String getDesplayName() {
		return desplayName;
	}

	public void setDesplayName(String desplayName) {
		this.desplayName = desplayName;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
        if(phoneNum != null){
            this.simpleNumber = phoneNum.replaceAll("\\-|\\s", "");
        }
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public Long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
	}

	public String getLookUpKey() {
		return lookUpKey;
	}

	public void setLookUpKey(String lookUpKey) {
		this.lookUpKey = lookUpKey;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	public String getFormattedNumber() {
		return formattedNumber;
	}

	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

    /**
     * 获取头像 2015/1/27 11:55
     * @param context
     * @return
     */
    public Bitmap getPhoto(Context context) {
        Bitmap photo;

        String id = null;
        if (!String.valueOf(photoId).equals("0"))
            id = String.valueOf(photoId);

        photo = loadImageFromUrl(context, id);
        return photo;
    }

    public synchronized static Bitmap loadImageFromUrl(Context ct, String photo_id) {
        Bitmap d = null;
        if (photo_id == null || photo_id.equals(""))
            return d;
        try{
            String[] projection = new String[]{ContactsContract.Data.DATA15};
            String selection = "ContactsContract.Data._ID = " + photo_id;
            Cursor cur = ct.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null);
            cur.moveToFirst();
            byte[] contactIcon = null;
            if(cur.getBlob(cur.getColumnIndex(ContactsContract.Data.DATA15)) != null){
                contactIcon = cur.getBlob(cur.getColumnIndex(ContactsContract.Data.DATA15));
                d = BitmapFactory.decodeByteArray(contactIcon, 0, contactIcon.length);
            }
            cur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

}
