package com.brigade.rockit.data;

import android.content.Context;
import android.util.Log;

import com.brigade.rockit.R;

import java.util.Locale;

public class CaseManager {

    private Context context;

    public CaseManager(Context context) {
        this.context = context;
    }

    public String getAuditionsCase(long auditions) {
        if (auditions == 1)
            return context.getString(R.string.audition);
        if (Locale.getDefault().getLanguage().equals("ru")) {
            String string = String.valueOf(auditions);
            int mod = Integer.valueOf(string.substring(string.length() - 1));
            if ((mod > 10) && (mod < 20))
                return context.getString(R.string.auditions_genitive);
            switch (mod) {
                case 1:
                    return context.getString(R.string.audition);
                case 2:
                case 3:
                case 4:
                    return context.getString(R.string.auditions);
                default:
                    return context.getString(R.string.auditions_genitive);

            }
        }
        return context.getString(R.string.auditions);
    }

    public String getSongsCase(int songs) {
        if (songs == 1)
            return context.getString(R.string.song).toLowerCase(Locale.ROOT);
        if (Locale.getDefault().getLanguage().equals("ru")) {
            if ((songs > 10) && songs < 20)
                return context.getString(R.string.songs_genitive);
            switch (songs % 10) {
                case 1:
                    return context.getString(R.string.song);
                case 2:
                case 3:
                case 4:
                    return context.getString(R.string.song_genitive);
                default:
                    return context.getString(R.string.songs_genitive);

            }
        }
        return context.getString(R.string.songs).toLowerCase(Locale.ROOT);
    }
}
