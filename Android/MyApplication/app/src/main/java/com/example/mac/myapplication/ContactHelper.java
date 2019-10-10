package com.example.mac.myapplication;

/**
 * Created by mac on 24/9/2019.
 */

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName:welasda
 * Author:Gnoxu
 * Date:2018/6/13
 * Time:10:18
 * Description:获取联系人工具类
 */
public class ContactHelper {
    private static final String TAG = "ContactHelper";



    private static final String[] PROJECTION = new String[]{
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private List<PhoneBookEntity> contacts = new ArrayList<>();

    private ContactHelper() {
        // Check the SDK version and whether the permission is already granted or not.
    }

    public static ContactHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final ContactHelper INSTANCE = new ContactHelper();
    }

    /**
     * 获取所有联系人
     *
     * @param context 上下文
     * @return 联系人集合
     */
    public List<PhoneBookEntity> getContacts(Context context) {
        long currentTimeMillis = System.currentTimeMillis();

        contacts.clear();
        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, "sort_key");
            if (cursor != null) {
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo, displayName;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    displayName = cursor.getString(displayNameIndex);
                    contacts.add(new PhoneBookEntity(displayName, mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                Log.d(TAG, "获取所有联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis) + "，共计：" + cursor.getCount());
                cursor.close();
            }
        }
        return contacts;
    }

    /**
     * 通过姓名获取联系人
     *
     * @param context     上下文
     * @param contactName 联系人姓名
     * @return 联系人集合
     */
    public List<PhoneBookEntity> getContactByName(Context context, String contactName) {
        long currentTimeMillis = System.currentTimeMillis();
        contacts.clear();

        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like ? ";
        String[] selectionArgs = new String[]{"%" + contactName + "%"};
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, selection, selectionArgs, "sort_key");
            if (cursor != null) {
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo, displayName;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    displayName = cursor.getString(displayNameIndex);
                    contacts.add(new PhoneBookEntity(displayName, mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            Log.d(TAG, "通过姓名获取联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis));
        }
        return contacts;
    }

    /**
     * 通过手机号获取联系人
     *
     * @param context 上下文
     * @param number  手机号码
     * @return 联系人集合
     */
    public List<PhoneBookEntity> getContactByNumber(Context context, String number) {
        long currentTimeMillis = System.currentTimeMillis();
        contacts.clear();

        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? ";
        String[] selectionArgs = new String[]{"%" + number + "%"};
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, selection, selectionArgs, "sort_key");
            if (cursor != null) {
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo, displayName;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    displayName = cursor.getString(displayNameIndex);
                    contacts.add(new PhoneBookEntity(displayName, mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            Log.d(TAG, "通过手机号获取联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis));
        }
        return contacts;
    }

    /**
     * 分页查询联系人
     *
     * @param context  上下文
     * @param page     页数
     * @param pageSize 每页数据量
     * @return 联系人集合
     */
    public List<PhoneBookEntity> getContactsByPage(Context context, int page, int pageSize) {
        long currentTimeMillis = System.currentTimeMillis();
        contacts.clear();

        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            String sortOrder = "_id  limit " + page + "," + pageSize;
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, sortOrder);
            if (cursor != null) {
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo, displayName;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    displayName = cursor.getString(displayNameIndex);
                    contacts.add(new PhoneBookEntity(displayName, mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            Log.d(TAG, "分页查询联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis));
        }
        return contacts;
    }

    /**
     * 获取联系人总数
     *
     * @param context 上下文
     * @return 数量
     */
    public int getContactCount(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, "sort_key");
            if (cursor != null) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            Log.d(TAG, "获取联系人总数耗时: " + (System.currentTimeMillis() - currentTimeMillis));
        }
        return 0;
    }
}