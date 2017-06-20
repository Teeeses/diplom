package qualificationwork.explead.ru.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.deeplearning4j.datasets.iterator.impl.CifarDataSetIterator;

import qualificationwork.explead.ru.app.App;
import qualificationwork.explead.ru.qualificationwork.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AsyncTask<Void, Void, CifarDataSetIterator>() {

            @Override
            protected CifarDataSetIterator doInBackground(Void... voids) {
                Log.d("TAG", "Start task");
                CifarDataSetIterator cifar = new CifarDataSetIterator(2, 5000, true);
                App.setDataSetIterator(cifar);
                return cifar;
            }

            @Override
            protected void onPostExecute(CifarDataSetIterator result) {
                super.onPostExecute(result);
                Log.d("TAG", "Labels: " + result.getLabels());
                System.out.println(result.getLabels());
            }
        };

    }
}
