package rapid.decoder;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class BitmapLoadTask extends AsyncTask<Object, Object, Decodable.DecodeResult> {
    private Decodable mDecodable;
    private Decodable.OnBitmapDecodedListener mListener;
    private WeakReference<Object> mWeakKey;
    private Object mStrongKey;

    public BitmapLoadTask(Decodable decoder, Decodable.OnBitmapDecodedListener listener) {
        mDecodable = decoder;
        mListener = listener;
    }

    public void setKey(Object key, boolean isStrong) {
        if (isStrong) {
            mStrongKey = key;
            mWeakKey = null;
        } else {
            mStrongKey = null;
            mWeakKey = new WeakReference<Object>(key);
        }
    }

    @Override
    protected Decodable.DecodeResult doInBackground(Object... params) {
        Decodable.DecodeResult result = new Decodable.DecodeResult();
        mDecodable.decode(result);
        return result;
    }

    @Override
    protected void onPostExecute(Decodable.DecodeResult result) {
        BackgroundTaskRecord record = BitmapDecoder.sTaskManager.remove(getKey());
        if (record != null && !record.isStale) {
            record.isStale = true;
            mListener.onBitmapDecoded(result.bitmap, result.cacheSource);
        }
    }

    Object getKey() {
        return mStrongKey != null ? mStrongKey : mWeakKey.get();
    }

    boolean isKeyStrong() {
        return mStrongKey != null;
    }

    public void cancel() {
        BackgroundTaskRecord record = BitmapDecoder.sTaskManager.remove(getKey());
        if (record != null) {
            record.isStale = true;
        }
        mDecodable.cancel();
    }
}