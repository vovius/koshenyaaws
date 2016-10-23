package com.crossover.trial.weather.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A collected point, including some information about the range of collected values
 *
 * @author code test administrator
 */
public class DataPoint {

    private double mean;

    private int first;

    private int last;

    private int median;

    private int count;

    /** private constructor, use the builder to create this object */
    private DataPoint() { }

    private DataPoint(Builder builder) {
        this.first = builder.first;
        this.last = builder.last;
        this.median = builder.median;
        this.mean = builder.mean;
        this.count = builder.count;
    }

    /** the mean of the observations */
    public double getMean() {
        return mean;
    }

    public void setMean(double mean) { this.mean = mean; }

    /** 1st quartile -- useful as a lower bound */
    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    /** 2nd quartile -- median value */
    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    /** 3rd quartile value -- less noisy upper value */
    public int getMedian() {
        return median;
    }

    public void setMedian(int median) {
        this.median = median;
    }

    /** the total number of measurements */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public boolean equals(Object that) {
        return this.toString().equals(that.toString());
    }

    static public class Builder {
        private int mean;
        private int first;
        private int median;
        private int last;
        private int count;

        public Builder() { }

        public Builder mean(int mean) {
            this.mean = mean;
            return this;
        }


        public Builder first(int first) {
            this.first= first;
            return this;
        }

        public Builder median(int median) {
            this.median = median;
            return this;
        }

        public Builder last(int last) {
            this.last = last;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }


        public DataPoint build() {
            return new DataPoint(this);
        }
    }
}
