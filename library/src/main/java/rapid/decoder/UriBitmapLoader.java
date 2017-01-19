package rapid.decoder;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UriBitmapLoader extends BitmapLoader {

    private ContentResolver resolver;

    public UriBitmapLoader(ContentResolver resolver, Uri uri) {
        if (uri == null) {
            throw new NullPointerException();
        }
        this.resolver = resolver;
        mId = uri;
    }

    protected UriBitmapLoader(UriBitmapLoader other) {
        super(other);
    }

    @Override
    protected Bitmap decode(BitmapFactory.Options opts) {
        return BitmapFactory.decodeStream(openInputStream(), null, opts);
    }

    @Override
    protected InputStream openInputStream() {
        try {
            return resolver.openInputStream((Uri) mId);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected BitmapRegionDecoder createBitmapRegionDecoder() {
        try {
            InputStream is = openInputStream();
            if (is == null) {
                return null;
            }
            return BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            return null;
        }
    }

    @NonNull
    @Override
    public BitmapLoader fork() {
        return new UriBitmapLoader(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof UriBitmapLoader && super.equals(o);
    }
}
