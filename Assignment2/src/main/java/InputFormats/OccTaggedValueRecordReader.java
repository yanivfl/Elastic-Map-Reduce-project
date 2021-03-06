package InputFormats;

import java.io.IOException;

import Jobs.Constants;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;


public class OccTaggedValueRecordReader extends RecordReader<Text,TaggedValue> {

    private LineRecordReader reader;
    private Text tag;

    private boolean w1;
    private boolean w2;
    private boolean w3;


    OccTaggedValueRecordReader(boolean w1, boolean w2, boolean w3, Text tag) {
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
        reader = new LineRecordReader();
        this.tag = new Text(tag);
    }


    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException {
        reader.initialize(split, context);
    }


    @Override
    public void close() throws IOException {
        reader.close();
    }


    @Override
    public boolean nextKeyValue() throws IOException {
        return reader.nextKeyValue();
    }


    @Override
    public Text getCurrentKey() {
        // reader key is the line number, we need to extract the key we want from the reader.value
        return parseKey(reader.getCurrentValue().toString());
    }


    @Override
    public TaggedValue getCurrentValue() throws IOException {
        return parseValue(reader.getCurrentValue().toString());
    }


    @Override
    public float getProgress() throws IOException {
        return reader.getProgress();
    }


    protected Text parseKey(String str) {

        // line structure: ngram \t .....
        String[] ngram = str
                .substring(0, str.indexOf('\t'))
                .split(" ");

        if ((w1 && ngram.length < 1) || (w2 && ngram.length < 2) || (w3 && ngram.length < 3)) {
            Constants.printDebug("wrong InputFileFormat, missing booleans for w1 or w2 or w3");
        }
        String key = "";
        if (w1 && ngram.length > 0)
            key += ngram[0];
        if (w2 && ngram.length > 1)
            key += ngram[1];
        if (w3 && ngram.length > 2)
            key += ngram[2];

        return new Text(key);
    }


    protected TaggedValue parseValue(String str) throws IOException {

        String initial_key = str.substring( 0, str.indexOf('\t'));
        String value = str.substring(str.indexOf('\t')+1);

        TaggedValue tagged_value = new TaggedValue();

        // line structure: ngram \t v1 v2 v3...

        if (tag.equals(Constants.TAG_3_OCC)){
            tagged_value.setInitialKey(new Text(initial_key));
        }

        tagged_value.setValue(new Text(value));
        tagged_value.setTag(new Text(this.tag));

        return tagged_value;
    }

}




