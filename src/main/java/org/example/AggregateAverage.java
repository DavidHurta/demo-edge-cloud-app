//
//  Author: David Hurta (xhurta04)
//  Project: DIP
//

package org.example;

import java.util.HashMap;
import java.util.Map;

public class AggregateAverage {
    public Map<String, FieldAggregate> fieldAggregates;

    public Map<String, FieldAggregate> getFieldAggregates() {
        return fieldAggregates;
    }

    public void setFieldAggregates(Map<String, FieldAggregate> fieldAggregates) {
        this.fieldAggregates = fieldAggregates;
    }

    public AggregateAverage() {
        this.fieldAggregates = new HashMap<>();
    }

    public void add(String fieldName, double value) {
        FieldAggregate aggregate = fieldAggregates.getOrDefault(fieldName, new FieldAggregate());
        aggregate.sum += value;
        aggregate.count++;
        fieldAggregates.put(fieldName, aggregate);
    }

    public double getAverage(String fieldName) {
        FieldAggregate aggregate = fieldAggregates.get(fieldName);
        if (aggregate == null || aggregate.count == 0) {
            return 0;
        }
        return aggregate.sum / aggregate.count;
    }
}

