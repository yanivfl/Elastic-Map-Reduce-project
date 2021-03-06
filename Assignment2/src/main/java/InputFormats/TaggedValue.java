package InputFormats;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;


public class TaggedValue implements Writable {

    // An implementation of value with tag, as a writable object

    private Text tag;
    private Text value;
    private Text initialKey;

    TaggedValue() {
        tag = new Text();
        value = new Text();
        initialKey = new Text();
    }

    public TaggedValue(Text tag) {
        this.tag = new Text(tag);
        this.initialKey = new Text();
        this.value = new Text();
    }

    @Override
    public void readFields(DataInput data) throws IOException {
        tag.readFields(data);
        initialKey.readFields(data);
        value.readFields(data);
    }

    @Override
    public void write(DataOutput data) throws IOException {
        tag.write(data);
        initialKey.write(data);
        value.write(data);
    }

    @Override
    public String toString() {
        return "TaggedValue{" +
                "tag=" + tag +
                ", value=" + value +
                ", initialKey=" + initialKey +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        TaggedValue other = (TaggedValue)o;
        return tag.equals(other.tag) && value.equals(other.value);
    }


    public Text getTag() {
        return tag;
    }

    public Text getValue() {
        return value;
    }

    public Text getInitialKey() {
        return initialKey;
    }

    public void setValue(Text value) {
        this.value = new Text(value);
    }

    public void setInitialKey(Text initialKey) {
        this.initialKey = new Text(initialKey);
    }

    public void setTag(Text tag) {
        this.tag = tag;
    }
}