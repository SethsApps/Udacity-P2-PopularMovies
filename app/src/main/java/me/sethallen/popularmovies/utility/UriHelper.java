package me.sethallen.popularmovies.utility;

import android.net.Uri;

import me.sethallen.popularmovies.R;

public class UriHelper {

    public static Uri getImageUriOrDefaultDrawable(Uri uri)
    {
        return getImageUriOrDefaultDrawable(uri, R.drawable.ic_cloud_off);
    }

    public static Uri getImageUriOrDefaultDrawable(Uri uri, int defaultDrawable)
    {
        if (uri == null || uri == Uri.EMPTY)
        {
            String path = "res:/" + defaultDrawable;
            return Uri.parse(path);
        }
        else
        {
            return uri;
        }
    }

    public static Uri getYouTubeUri(String youTubeId)
    {
        return Uri.parse("http://www.youtube.com/watch?v=" + youTubeId);
    }
}
