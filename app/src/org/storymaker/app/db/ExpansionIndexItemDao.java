package org.storymaker.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hannesdorfmann.sqlbrite.dao.Dao;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by mnbogner on 8/20/15.
 */
public class ExpansionIndexItemDao extends Dao {

    Random r = new Random();

    @Override
    public void createTable(SQLiteDatabase sqLiteDatabase) {

        CREATE_TABLE(ExpansionIndexItem.TABLE_NAME,
                ExpansionIndexItem.COLUMN_ID + " INTEGER", // change to auto-increment?
                ExpansionIndexItem.COLUMN_TITLE + " TEXT",
                ExpansionIndexItem.COLUMN_DESCRIPTION + " TEXT",
                ExpansionIndexItem.COLUMN_THUMBNAILPATH + " TEXT",
                ExpansionIndexItem.COLUMN_PACKAGENAME + " TEXT",
                ExpansionIndexItem.COLUMN_EXPANSIONID + " TEXT",
                ExpansionIndexItem.COLUMN_PATCHORDER + " TEXT PRIMARY KEY NOT NULL",
                ExpansionIndexItem.COLUMN_CONTENTTYPE + " TEXT",
                ExpansionIndexItem.COLUMN_EXPANSIONFILEURL + " TEXT",
                ExpansionIndexItem.COLUMN_EXPANSIONFILEPATH + " TEXT",
                ExpansionIndexItem.COLUMN_EXPANSIONFILEVERSION + " TEXT",
                ExpansionIndexItem.COLUMN_EXPANSIONFILESIZE + " INTEGER",
                ExpansionIndexItem.COLUMN_EXPANSIONFILECHECKSUM + " TEXT",
                ExpansionIndexItem.COLUMN_PATCHFILEVERSION + " TEXT",
                ExpansionIndexItem.COLUMN_PATCHFILESIZE + " INTEGER",
                ExpansionIndexItem.COLUMN_PATCHFILECHECKSUM + " TEXT",
                ExpansionIndexItem.COLUMN_AUTHOR + " TEXT",
                ExpansionIndexItem.COLUMN_WEBSITE + " TEXT",
                ExpansionIndexItem.COLUMN_DATEUPDATED + " TEXT",
                ExpansionIndexItem.COLUMN_LANGUAGES + " TEXT",
                ExpansionIndexItem.COLUMN_TAGS + " TEXT",
                ExpansionIndexItem.COLUMN_DOWNLOADFLAG + " INTEGER")
                .execute(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // foo

    }

    public Observable<List<ExpansionIndexItem>> getExpansionIndexItems() {

        // select all rows

        return query(SELECT(ExpansionIndexItem.COLUMN_ID,
                ExpansionIndexItem.COLUMN_TITLE,
                ExpansionIndexItem.COLUMN_DESCRIPTION,
                ExpansionIndexItem.COLUMN_THUMBNAILPATH,
                ExpansionIndexItem.COLUMN_PACKAGENAME,
                ExpansionIndexItem.COLUMN_EXPANSIONID,
                ExpansionIndexItem.COLUMN_PATCHORDER,
                ExpansionIndexItem.COLUMN_CONTENTTYPE,
                ExpansionIndexItem.COLUMN_EXPANSIONFILEURL,
                ExpansionIndexItem.COLUMN_EXPANSIONFILEPATH,
                ExpansionIndexItem.COLUMN_EXPANSIONFILEVERSION,
                ExpansionIndexItem.COLUMN_EXPANSIONFILESIZE,
                ExpansionIndexItem.COLUMN_EXPANSIONFILECHECKSUM,
                ExpansionIndexItem.COLUMN_PATCHFILEVERSION,
                ExpansionIndexItem.COLUMN_PATCHFILESIZE,
                ExpansionIndexItem.COLUMN_PATCHFILECHECKSUM,
                ExpansionIndexItem.COLUMN_AUTHOR,
                ExpansionIndexItem.COLUMN_WEBSITE,
                ExpansionIndexItem.COLUMN_DATEUPDATED,
                ExpansionIndexItem.COLUMN_LANGUAGES,
                ExpansionIndexItem.COLUMN_TAGS,
                ExpansionIndexItem.COLUMN_DOWNLOADFLAG)
                .FROM(ExpansionIndexItem.TABLE_NAME))
                .map(new Func1<SqlBrite.Query, List<ExpansionIndexItem>>() {

                    @Override
                    public List<ExpansionIndexItem> call(SqlBrite.Query query) {
                        Cursor cursor = query.run();
                        return ExpansionIndexItemMapper.list(cursor);
                    }
                });
    }

    public Observable<List<ExpansionIndexItem>> getExpansionIndexItemsByType(String contentType) {

        // select all rows with matching content type

        return query(SELECT(ExpansionIndexItem.COLUMN_ID,
                ExpansionIndexItem.COLUMN_TITLE,
                ExpansionIndexItem.COLUMN_DESCRIPTION,
                ExpansionIndexItem.COLUMN_THUMBNAILPATH,
                ExpansionIndexItem.COLUMN_PACKAGENAME,
                ExpansionIndexItem.COLUMN_EXPANSIONID,
                ExpansionIndexItem.COLUMN_PATCHORDER,
                ExpansionIndexItem.COLUMN_CONTENTTYPE,
                ExpansionIndexItem.COLUMN_EXPANSIONFILEURL,
                ExpansionIndexItem.COLUMN_EXPANSIONFILEPATH,
                ExpansionIndexItem.COLUMN_EXPANSIONFILEVERSION,
                ExpansionIndexItem.COLUMN_EXPANSIONFILESIZE,
                ExpansionIndexItem.COLUMN_EXPANSIONFILECHECKSUM,
                ExpansionIndexItem.COLUMN_PATCHFILEVERSION,
                ExpansionIndexItem.COLUMN_PATCHFILESIZE,
                ExpansionIndexItem.COLUMN_PATCHFILECHECKSUM,
                ExpansionIndexItem.COLUMN_AUTHOR,
                ExpansionIndexItem.COLUMN_WEBSITE,
                ExpansionIndexItem.COLUMN_DATEUPDATED,
                ExpansionIndexItem.COLUMN_LANGUAGES,
                ExpansionIndexItem.COLUMN_TAGS,
                ExpansionIndexItem.COLUMN_DOWNLOADFLAG)
                .FROM(ExpansionIndexItem.TABLE_NAME)
                .WHERE(ExpansionIndexItem.COLUMN_CONTENTTYPE + " = ? "), contentType)
                .map(new Func1<SqlBrite.Query, List<ExpansionIndexItem>>() {

                    @Override
                    public List<ExpansionIndexItem> call(SqlBrite.Query query) {
                        Cursor cursor = query.run();
                        return ExpansionIndexItemMapper.list(cursor);
                    }
                });
    }

    public Observable<Long> addExpansionIndexItem(long id, String title, String description, String thumbnailPath, String packageName, String expansionId, String patchOrder, String contentType, String expansionFileUrl, String expansionFilePath, String expansionFileVersion, long expansionFileSize, String expansionFileChecksum, String patchFileVersion, long patchFileSize, String patchFileChecksum, String author, String website, String dateUpdated, String languages, String tags, int downloadFlag) {

        Observable<Long> rowId = null;

        ContentValues values = ExpansionIndexItemMapper.contentValues()
                .id(r.nextLong())
                .title(title)
                .description(description)
                .thumbnailPath(thumbnailPath)
                .packageName(packageName)
                .expansionId(expansionId)
                .patchOrder(patchOrder)
                .contentType(contentType)
                .expansionFileUrl(expansionFileUrl)
                .expansionFilePath(expansionFilePath)
                .expansionFileVersion(expansionFileVersion)
                .expansionFileSize(expansionFileSize)
                .expansionFileChecksum(expansionFileChecksum)
                .patchFileVersion(patchFileVersion)
                .patchFileSize(patchFileSize)
                .patchFileChecksum(patchFileChecksum)
                .author(author)
                .website(website)
                .dateUpdated(dateUpdated)
                .languages(languages)
                .tags(tags)
                .downloadFlag(downloadFlag)
                .build();

        try {
            rowId = insert(ExpansionIndexItem.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLiteConstraintException sce) {
            Log.d("RX_DB", "INSERT FAILED: " + sce.getMessage());
        }

        return rowId;
    }

    public Observable<Long> addExpansionIndexItem(scal.io.liger.model.ExpansionIndexItem item) {

        String langguageString = null;
        String tagString = null;

        if (item.getLanguages() != null) {
            langguageString = item.getLanguages().toString();
            Log.d("RX_DB", "WHAT DOES THIS LOOK LIKE? " + langguageString);
        }
        if (item.getTags() != null) {
            tagString = item.getTags().toString();
            Log.d("RX_DB", "WHAT DOES THIS LOOK LIKE? " + tagString);
        }

        return addExpansionIndexItem(r.nextLong(),
                item.getTitle(),
                item.getDescription(),
                item.getThumbnailPath(),
                item.getPackageName(),
                item.getExpansionId(),
                item.getPatchOrder(),
                item.getContentType(),
                item.getExpansionFileUrl(),
                item.getExpansionFilePath(),
                item.getExpansionFileVersion(),
                item.getExpansionFileSize(),
                item.getExpansionFileChecksum(),
                item.getPatchFileVersion(),
                item.getPatchFileSize(),
                item.getPatchFileChecksum(),
                item.getAuthor(),
                item.getWebsite(),
                item.getDateUpdated(),
                langguageString,
                tagString,
                0); // default to false, no need to update liger ExpansionIndexItem class
    }
}
