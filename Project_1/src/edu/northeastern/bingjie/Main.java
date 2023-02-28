package edu.northeastern.bingjie;

import java.util.Arrays;
import java.util.Random;

public class Main {
    private static final int NUM_THREADS = 4;

    public static void main(String[] args) {

        question1();
        question2();

    }

    private static void question1(){
        System.out.println("Question 1: ");
        Thread[] threads = new Thread[4];
        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(new MyThread());
            threads[i].start();
        }

        for(int i = 0; i < 4; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            threads[i].stop();
        }
        System.out.println();
    }

    private static void question2(){
        System.out.println("Question 2: ");
        int[] arr = new int[200000000];

        generateValue(arr);
        // System.out.println(Arrays.toString(arr));
        // parallel calculation
        int[][] subarrays = divideArray(arr, NUM_THREADS);

        Thread[] threads = new Thread[NUM_THREADS];
        SumTask[] tasks = new SumTask[NUM_THREADS];

        long startTime = System.currentTimeMillis();

        // start the threads in parallel
        for (int i = 0; i < NUM_THREADS; i++) {
            tasks[i] = new SumTask(subarrays[i]);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        // wait for the threads to finish
        try {
            for (int i = 0; i < NUM_THREADS; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // calculate the total sum of the array
        long totalSum = 0;
        for (int i = 0; i < NUM_THREADS; i++) {
            totalSum += tasks[i].getSum();
        }

        long parallelEndTime = System.currentTimeMillis();

        System.out.println();
        System.out.println("Total sum of array (parallel): " + totalSum);
        System.out.println("Time taken (parallel): " + (parallelEndTime - startTime) + " ms");
        long timeTakenparallel = parallelEndTime - startTime;

        // serial calculation
        long serialSum = 0;

        long serialStartTime = System.currentTimeMillis();

        for (int i = 0; i < arr.length; i++) {
            serialSum += arr[i];
        }

        long serialEndTime = System.currentTimeMillis();

        System.out.println();
        System.out.println("Total sum of array (serial): " + serialSum);
        System.out.println("Time taken (serial): " + (serialEndTime - serialStartTime) + " ms");
        long timeTakenSerial = serialEndTime - serialStartTime;

        System.out.println();
        System.out.println("Time difference is: " + (timeTakenSerial - timeTakenparallel) + " ms");
    }

    private static void generateValue(int[] arr){
        Random rd = new Random();

        for(int i = 0; i < arr.length; i++){
            arr[i] = rd.nextInt(10);
        }
    }

    private static int[][] divideArray(int[] arr, int numParts) {
        int[][] subarrays = new int[numParts][];

        int chunkSize = arr.length / numParts;
        int leftover = arr.length % numParts;

        int offset = 0;
        for (int i = 0; i < numParts; i++) {
            int size = chunkSize + (leftover-- > 0 ? 1 : 0);
            subarrays[i] = Arrays.copyOfRange(arr, offset, offset + size);
            offset += size;
        }

        return subarrays;
    }
}

class MyThread implements Runnable {
    public void run() {
        System.out.println("Thread " + Thread.currentThread().getId() + " is running.");
    }
}


class SumTask implements Runnable {
    private int[] arr;
    private long sum;

    public SumTask(int[] arr) {
        this.arr = arr;
        this.sum = 0;
    }

    public long getSum() {
        return sum;
    }

    @Override
    public void run() {
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        System.out.println(Thread.currentThread().getName() + " calculated sum: " + sum);
    }
}
