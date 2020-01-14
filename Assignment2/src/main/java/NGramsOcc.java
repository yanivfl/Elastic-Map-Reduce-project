import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.File;
import java.io.IOException;

public class NGramsOcc {

    public static class MapClass extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] splitted = value.toString().split("\t");
            String ngram = splitted[0];
//            String year = splitted[1];
            String occurrences = splitted[2];
            context.write(new Text(ngram), new IntWritable(Integer.parseInt(occurrences)));
        }
    }

    public static class ReduceClass extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value : values) {
                sum += value.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }

    public static class PartitionerClass extends Partitioner<Text,IntWritable> {

        @Override
        public int getPartition(Text key, IntWritable value, int numPartitions) {
            return key.hashCode() % numPartitions;
        }

    }


    /* Create a job instance for JOB_1_GRAM, N2, N3, C1, C2 */
    private static Job CreateCounterJob(String jobName, String outputDirName, String inputPath) throws IOException {

        Constants.clearOutput(outputDirName);

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(NGramsOcc.class);
        job.setMapperClass(MapClass.class);
        job.setPartitionerClass(PartitionerClass.class);
        job.setCombinerClass(ReduceClass.class);
        job.setReducerClass(ReduceClass.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputDirName));

        return job;
    }


    public static Job[] createOccTables() {
        Job job_1gram=null, job_2gram=null, job_3gram=null;

        try {
            job_1gram = CreateCounterJob(Constants.JOB_1_GRAM, Constants.OCC_1_GRAMS, Constants.CORPUS_1_GRAMS);
            job_2gram = CreateCounterJob(Constants.JOB_2_GRAM, Constants.OCC_2_GRAMS, Constants.CORPUS_2_GRAMS);
            job_3gram = CreateCounterJob(Constants.JOB_3_GRAM, Constants.OCC_3_GRAMS, Constants.CORPUS_3_GRAMS);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Job [] jobs = {job_1gram, job_2gram, job_3gram};
        return jobs;



//        System.exit(
//                job_1gram.waitForCompletion(true) &&
//                        job_2gram.waitForCompletion(true) &&
//                        job_3gram.waitForCompletion(true)? 0 : 1);
    }
}