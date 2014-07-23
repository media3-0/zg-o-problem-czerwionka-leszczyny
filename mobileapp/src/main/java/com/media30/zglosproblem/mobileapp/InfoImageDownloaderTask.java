package com.media30.zglosproblem.mobileapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

class InfoImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    //private final WeakReference imageViewReference;
    private final ImageView imageViewReference;
    private final ProgressBar progressBarReference;
    private final Context contextReference;
    private final Information infoReference;
    private final PhotoViewAttacher mAttacherReference;
    private String filepath;

    public InfoImageDownloaderTask(ImageView imageView, Information info, Context context, ProgressBar progressBar, PhotoViewAttacher mAttacher) {
        //imageViewReference = new WeakReference(imageView);
        imageViewReference = imageView;
        progressBarReference = progressBar;
        contextReference = context;
        infoReference = info;
        mAttacherReference = mAttacher;
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the url.
        StringBuilder tempFilePath = new StringBuilder();
        File storageDir = contextReference.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        tempFilePath.append(storageDir.getAbsolutePath()).append("/");
        String extension = "";

        int i = params[0].lastIndexOf('.');
        if (i > 0) {
            extension = params[0].substring(i+1);
        }
        tempFilePath.append("info").append(infoReference.getId()).append(extension);
        filepath = tempFilePath.toString();
        File f = new File(tempFilePath.toString());
        if(f.exists()) {
            return BitmapFactory.decodeFile(filepath);
        }
        return downloadBitmap(params[0]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            //ImageView imageView = (ImageView)imageViewReference.get();

            if (bitmap != null) {
                imageViewReference.setImageBitmap(bitmap);
                if(mAttacherReference != null)
                    mAttacherReference.update();
                if(progressBarReference != null){
                    progressBarReference.setVisibility(View.GONE);
                    imageViewReference.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private Bitmap downloadBitmap(String url) {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode
                        + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                OutputStream os = null;
                try {
                    inputStream = entity.getContent();
                    os = new FileOutputStream(filepath);
                    //zapisywanie do pliku
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = inputStream.read(bytes)) != -1) {
                        os.write(bytes, 0, read);
                    }
                    os.flush();

                    return BitmapFactory.decodeFile(filepath);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or
            // IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
        } finally {
            client.close();
        }
        return null;
    }

}