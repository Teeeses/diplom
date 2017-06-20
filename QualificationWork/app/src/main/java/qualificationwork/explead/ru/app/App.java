package qualificationwork.explead.ru.app;

import android.app.Application;

import org.deeplearning4j.datasets.iterator.impl.CifarDataSetIterator;

/**
 * Created by develop on 20.06.2017.
 */

public class App extends Application {

    private static CifarDataSetIterator dataSetIterator;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static CifarDataSetIterator getDataSetIterator() {
        return dataSetIterator;
    }

    public static void setDataSetIterator(CifarDataSetIterator dataSetIterator) {
        App.dataSetIterator = dataSetIterator;
    }
}
