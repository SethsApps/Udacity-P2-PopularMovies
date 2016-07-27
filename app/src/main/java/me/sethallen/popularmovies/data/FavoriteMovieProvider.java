package me.sethallen.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class FavoriteMovieProvider extends ContentProvider
{
    private static final String LOG_TAG             = FavoriteMovieProvider.class.getSimpleName();
    private static final String PROVIDER_NAME       = "me.sethallen.popularmovies.favoritemovieprovider";
    private static final int    FAVORITE_MOVIES     = 100;
    private static final int    FAVORITE_MOVIE_ITEM = 101;

    private static final UriMatcher            _uriMatcher = buildUriMatcher();
    private              FavoriteMovieDbHelper _dbHelper;

    private static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Movies Uri
        matcher.addURI(FavoriteMovieContract.CONTENT_AUTHORITY,
                       FavoriteMovieContract.PATH_FAVORITE_MOVIE,
                       FAVORITE_MOVIES);

        // Movie Item Uri
        matcher.addURI(FavoriteMovieContract.CONTENT_AUTHORITY,
                       FavoriteMovieContract.PATH_FAVORITE_MOVIE + "/#",
                       FAVORITE_MOVIE_ITEM);

        return matcher;
    }

    @Override
    public boolean onCreate()
    {
        _dbHelper = new FavoriteMovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri)
    {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = _uriMatcher.match(uri);

        switch (match)
        {
            // Student: Uncomment and fill out these two cases
            case FAVORITE_MOVIES:
                return FavoriteMovieContract.FavoriteMovieEntry.CONTENT_TYPE;
            case FAVORITE_MOVIE_ITEM:
                return FavoriteMovieContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder)
    {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (_uriMatcher.match(uri))
        {
            case FAVORITE_MOVIES:
            {
                retCursor = _dbHelper.getReadableDatabase().query(
                        FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db    = _dbHelper.getWritableDatabase();
        final int            match = _uriMatcher.match(uri);

        Uri returnUri;

        switch (match)
        {
            case FAVORITE_MOVIES:
            {
                long _id = db.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                                     null,
                                     values);
                if ( _id > 0 )
                    returnUri = FavoriteMovieContract.FavoriteMovieEntry.BuildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db    = _dbHelper.getWritableDatabase();
        final int            match = _uriMatcher.match(uri);

        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match)
        {
            case FAVORITE_MOVIES:
                rowsDeleted = db.delete(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                                        selection,
                                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db    = _dbHelper.getWritableDatabase();
        final int            match = _uriMatcher.match(uri);

        int rowsUpdated;

        switch (match)
        {
            case FAVORITE_MOVIES:
                rowsUpdated = db.update(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                                        values,
                                        selection,
                                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        final SQLiteDatabase db    = _dbHelper.getWritableDatabase();
        final int            match = _uriMatcher.match(uri);

        switch (match)
        {
            case FAVORITE_MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try
                {
                    for (ContentValues value : values)
                    {
                        long _id = db.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                                             null,
                                             value);
                        if (_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
