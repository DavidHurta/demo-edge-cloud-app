//
//  Author: David Hurta (xhurta04)
//  Project: DIP
//

package org.example;

// Inner class to hold sum and count for a field
public class FieldAggregate {
    double sum = 0;
    long count = 0;

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
