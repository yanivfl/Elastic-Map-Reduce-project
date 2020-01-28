package InputFormats;


import Jobs.Constants;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class InputFormat_C2 extends FileInputFormat<Text, TaggedValue> {


    @Override
    public RecordReader<Text, TaggedValue>
    createRecordReader(InputSplit split,
                       TaskAttemptContext context) {
        return new OccTaggedValueRecordReader(true, true, false, Constants.TAG_2_OCC);
    }

    @Override
    protected boolean isSplitable(JobContext context, Path file) {
        CompressionCodec codec =
                new CompressionCodecFactory(context.getConfiguration()).getCodec(file);
        return codec == null;
    }

}