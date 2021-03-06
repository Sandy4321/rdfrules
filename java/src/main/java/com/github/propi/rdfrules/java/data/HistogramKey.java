package com.github.propi.rdfrules.java.data;

import com.github.propi.rdfrules.data.Histogram;
import com.github.propi.rdfrules.java.TripleItemConverters;
import scala.Option;

import java.util.Objects;

/**
 * Created by Vaclav Zeman on 2. 5. 2018.
 */
public class HistogramKey {

    private final Histogram.Key key;

    public HistogramKey(Histogram.Key key) {
        this.key = key;
    }

    public HistogramKey(TripleItem.Uri subject, TripleItem.Uri predicate, TripleItem object) {
        this(new Histogram.Key(
                subject == null ? Option.empty() : Option.apply(subject.asScala()),
                predicate == null ? Option.empty() : Option.apply(predicate.asScala()),
                object == null ? Option.empty() : Option.apply(object.asScala())
        ));
    }

    public TripleItem.Uri getSubject() {
        return key.s().isEmpty() ? null : TripleItemConverters.toJavaUri(key.s().get());
    }

    public TripleItem.Uri getPredicate() {
        return key.p().isEmpty() ? null : TripleItemConverters.toJavaUri(key.p().get());
    }

    public TripleItem getObject() {
        return key.o().isEmpty() ? null : TripleItemConverters.toJavaTripleItem(key.o().get());
    }

    public Histogram.Key asScala() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistogramKey that = (HistogramKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return key.toString();
    }

}