package com.media30.zglosproblem.mobileapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

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

class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    //private final WeakReference imageViewReference;
    private final ImageView imageViewReference;
    private final ProgressBar progressBarReference;
    private final Context contextReference;
    private final Report reportReference;
    private String filepath;

    public ImageDownloaderTask(ImageView imageView, Report report, Context context, ProgressBar progressBar) {
        //imageViewReference = new WeakReference(imageView);
        imageViewReference = imageView;
        progressBarReference = progressBar;
        contextReference = context;
        reportReference = report;
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        StringBuilder tempFilePath = new StringBuilder();
        File storageDir = contextReference.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        tempFilePath.append(storageDir.getAbsolutePath()).append("/");
        if(progressBarReference == null){
            //miniatury
            tempFilePath.append(reportReference.getThumbName());
        }else{
            //duże
            tempFilePath.append(reportReference.getImageName());
        }

        filepath = tempFilePath.toString();
        File f = new File(tempFilePath.toString());
        if(f.exists()) {
            return BitmapFactory.decodeFile(filepath);
        }
        // params comes from the execute() call: params[0] is the url.
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
                if(progressBarReference != null){
                    progressBarReference.setVisibility(View.GONE);
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

                    return BitmapFactory.decodeStream(inputStream);
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