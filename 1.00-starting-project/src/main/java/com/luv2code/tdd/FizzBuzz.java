package com.luv2code.tdd;

public class FizzBuzz {

    //If number  is divisible by 3, print Fizz

    //If number is divisible by 5, print Buzz

    //If number is divisible by 3 and 5, print FizzBuzz

    //If number is NOT divisible by 3 or 5, then print the number

    public static String compute(int i) {
        if( i%3 == 0 && i%5==0){
            return "FizzBuzz";
        }
        if (i % 3 == 0) {
            return "Fizz";
        }
        if (i % 5 == 0) {
            return "Buzz";
        }

        return String.valueOf(i);
    }
}
