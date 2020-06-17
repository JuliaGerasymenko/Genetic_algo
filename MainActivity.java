package ua.ip74.genetic;

import android.os.Bundle;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.widget.Toast;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity {
    EditText a, b, c, d, y;
    int aValue, bValue, cValue, dValue, yValue, counter = 0, times = 0;
    double[] rateArr = new double[]{0.1,0.2,0.3,0.4,0.5, 0.6, 0.7, 0.8, 0.9};
    long[] timeExeqution = new long[9];
    int[][] answers = new int [9][6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);


    }
    public void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Genetic(View view) {

        a = findViewById(R.id.a);
        b = findViewById(R.id.b);
        c = findViewById(R.id.b);
        d = findViewById(R.id.d);
        y = findViewById(R.id.y);
        aValue = Integer.parseInt(a.getText().toString());
        bValue = Integer.parseInt(b.getText().toString());
        cValue = Integer.parseInt(c.getText().toString());
        dValue = Integer.parseInt(d.getText().toString());
        yValue = Integer.parseInt(y.getText().toString());
//        showToast("Hello "+ Arrays.toString(new int[]{aValue, bValue, cValue, dValue, yValue}));
        hideKeyboard();

 
            int chromosomeNum, uniqueParents;
             int generationNum, generations;
             double totalFitness;
             double mutationRate, newK;
             double crossoverRate;
             int[][] chromosomes, newChromo, parent1, parent2, allocParent;
             int[] calc, crossover, parentInd1, allocIndex;
             double[] fitness, probability, comulativeProb, R;

        mutationRate = rateArr[counter];
        chromosomeNum = 6;
        generationNum = 4;
        crossoverRate = 0.25;
        generations = 200;
        newK = 1.2;
        calc = new int[chromosomeNum];
        allocIndex = new int[chromosomeNum];
        fitness = new double[chromosomeNum];
        probability = new double[chromosomeNum];
        comulativeProb = new double[chromosomeNum];
        R = new double[chromosomeNum];
        int[] coef = new int[]{aValue,bValue,cValue,dValue,yValue};
        chromosomes = new int[chromosomeNum][generationNum];
        newChromo = new int[chromosomeNum][generationNum];
        allocParent = new int[chromosomeNum][generationNum];
        long startTime = System.nanoTime();
        for(int t = 0; t < generations; t++)

            {
                for (int i = 0; i < chromosomeNum; i++) {
                    calc[i] = 0;
                    crossoverRate = crossoverRate*newK;
                    for (int j = 0; j < generationNum; j++) {
                        chromosomes[i][j] = (int) (Math.random() * coef[coef.length - 1]);

                        calc[i] += chromosomes[i][j] * coef[j];
                    }
                    calc[i] = Math.abs(calc[i] - coef[4]);

                    if (calc[i] == 0) {

                        long endTime = System.nanoTime();
                        timeExeqution[counter] = endTime-startTime;
                        answers[counter] = chromosomes[i];
                        showToast("The number of iterations  is: " + t);
                        counter++;
                        while(counter < 8) {
                            Genetic(view);
                        }
                        if (times > 7) {
                            Map<Long, Double> map = new HashMap<>();
                            for (int counter = 0; counter < 9; counter++) {
                                showToast("Answer is: " + Arrays.toString(answers[counter]));
                                map.put(timeExeqution[counter], rateArr[counter]);

//                                showToast("Elapsed task " + counter + " " + timeExeqution[counter]);
                            }
                            Long min = Collections.min(map.keySet());
                            showToast("Time elapsed  " + min+ " with mutation ratio "+ map.get(min)+"%");
                        }
                        times++;
                        return;
                    }
                    fitness[i] = 1 / (1 + calc[i]);
                }

                totalFitness = 0.0;

                for (int i = 0; i < chromosomeNum; i++) {
                    totalFitness += fitness[i];
                }

                for (int i = 0; i < chromosomeNum; i++) {
                    probability[i] = fitness[i] / totalFitness;
                }

                // roulette wheel
                for (int i = 0; i < chromosomeNum; i++) {
                    if (i != 0) {
                        comulativeProb[i] = comulativeProb[i - 1] + probability[i];
                    } else {
                        comulativeProb[i] = probability[i];
                    }
                }

                // generate random number
                for (int i = 0; i < chromosomeNum; i++) {
                    R[i] = Math.random();
                }

                double[] copyR = new double[R.length];
                for (int i = 0; i < R.length; i++) {
                    copyR[i] = R[i];
                }
                Arrays.sort(R);

                for (int i = 0; i < chromosomeNum; i++) {
                    int index = findIndex(copyR, R[i]);
                    newChromo[i] = chromosomes[index];
                }


                int k = 0, pIndex = 0;

                while (k < chromosomeNum) {
                    R[k] = Math.random();
                    if (R[k] < crossoverRate) {
                        allocParent[pIndex] = newChromo[k];
                        allocIndex[pIndex] = k;
                        pIndex++;
                    }
                    k++;
                }

                parent1 = new int[pIndex][generationNum];
                parent2 = new int[pIndex][generationNum];
                parentInd1 = new int[pIndex];

                for (int i = 0; i < pIndex; i++) {
                    parent1[i] = allocParent[i];
                    parentInd1[i] = allocIndex[i];
                }

                //chromosome selection
                uniqueParents = parent1.length;

                for (int i = 0; i < uniqueParents; i++) {
                    if (i == uniqueParents - 1) {
                        parent2[0] = parent2[i];
                    } else {
                        parent2[i + 1] = parent1[i];
                    }
                }

                crossover = new int[uniqueParents];
                //crossover
                for (int i = 0; i < uniqueParents; i++) {
                    crossover[i] = (int) (Math.random() * (generationNum - 1) + 1);
                }

                for (int i = 0; i < uniqueParents; i++) {
                    for (int j = 0; j < generationNum; j++) {
                        if (i < crossover[i]) {
                            newChromo[parentInd1[i]][j] = parent1[i][j];
                        } else {
                            newChromo[parentInd1[i]][j] = parent2[i][j];
                        }
                    }
                }

                //mutation
                int totalGen = generationNum * chromosomeNum;
                int numMutation = (int) mutationRate * totalGen;
                int randGen;

                for (int i = 0; i < numMutation; i++) {
                    randGen = (int) Math.random() * (totalGen - 1);
                    if (randGen % generationNum == 0)
                        chromosomes[randGen / generationNum - 1][generationNum - 1] = (int) (Math.random() * coef[coef.length - 1] - 1) + 1;
                    else {
                        chromosomes[randGen / generationNum][randGen % generationNum - 1] = (int) (Math.random() * coef[coef.length - 1] - 1) + 1;
                    }
                }


//		System.out.println(
            }
        
//        showToast("x1: " + +" x2:"+ ) showToast("Answer is: " + answer[counter]);
    }



    private int findIndex(double[] arr, double v) {

        if (arr == null) {
            return -1;
        }

        int len = arr.length;
        int i = 0;

        // traverse in the array
        while (i < len) {

            if (arr[i] == v) {
                return i;
            } else {
                i = i + 1;
            }
        }
        return -1;
    }

}
