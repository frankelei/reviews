package org.cocos2dx.cpp;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;

public class FilePathUtil {
 
	public static int getAndroidSDKVersion() { 
		int version = 18; //4.3 
		try { 
			version = Integer.valueOf(android.os.Build.VERSION.SDK); 
		} catch (NumberFormatException e) { 
			//Log.e(e.toString());
		} 
		return version; 
	}
	
	public static String getPathBefore44(final Context context, final Uri uri)
	{
		String filename = null;
    	if (uri.getScheme().toString().compareTo("content") == 0) {
    		Cursor cursor = context.getContentResolver().query(uri,
                    new String[] {Audio.Media.DATA}, null, null, null);
            if (cursor.moveToFirst()) {
                filename = cursor.getString(0);
            }
        }else if (uri.getScheme().toString().compareTo("file") == 0)         //file:///开头的uri
        {
            filename = uri.toString();
            filename = uri.toString().replace("file://", "");
            //替换file://
            if(!filename.startsWith("/mnt")){
                //加上"/mnt"头
                filename += "/mnt"; 
            }
        }
    	return filename;
	}
	
	@SuppressLint("NewApi")
    public static String getPathAfter44(final Context context, final Uri uri) {
 
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
 
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
 
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
 
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
 
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
 
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
 
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
 
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };
 
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
 
        return null;
    }
 
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     * 
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {
 
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
 
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
 
    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
 
    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
 
    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    
	public static String fromUrlData(final Context context, final Uri uriwww)
	{
		if (getAndroidSDKVersion() > 18){
			return getPathAfter44(context, uriwww);
		}else{
			return getPathBefore44(context, uriwww);
		}
	}
}


