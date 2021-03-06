package Jobs;

import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.*;



public class Main {

    public static void main(String[] args) throws Exception {

        AmazonElasticMapReduce mapReduce = AmazonElasticMapReduceClientBuilder.standard()
                .withRegion("us-east-1")
                .build();

        Constants.printDebug("hadoop jar step");
        HadoopJarStepConfig hadoopJarStep = new HadoopJarStepConfig()
                .withJar(Constants.getS3Path(Constants.INPUT_BUCKET_NAME, Constants.MY_JAR_NAME))
//                    .withMainClass("some.pack.MainClass")
                .withArgs(
                        Constants.getS3NgramLink(1),
                        Constants.getS3NgramLink(2),
                        Constants.getS3NgramLink(3),
                        Constants.getS3OutputPath(Constants.OUTPUT_FILE_NAME)
                );

        Constants.printDebug("stepConfig jar step");

        StepConfig stepConfig = new StepConfig()
                .withName("stepname")
                .withHadoopJarStep(hadoopJarStep)
                .withActionOnFailure("TERMINATE_JOB_FLOW");

        Constants.printDebug("jobFlowConfig jar step");

        JobFlowInstancesConfig instances = new JobFlowInstancesConfig()
                .withInstanceCount(6)
                .withMasterInstanceType(InstanceType.M1Large.toString())
                .withSlaveInstanceType(InstanceType.M1Large.toString())
                .withHadoopVersion("2.6.0").withEc2KeyName(Constants.MY_KEY)
                .withKeepJobFlowAliveWhenNoSteps(false)
                .withPlacement(new PlacementType("us-east-1a"));

        Constants.printDebug("RunJobFlow jar step");

        RunJobFlowRequest runFlowRequest = new RunJobFlowRequest()
                .withName("jobname")
                .withInstances(instances)
                .withReleaseLabel("emr-5.1.0")
                .withSteps(stepConfig)
                .withJobFlowRole("EMR_EC2_DefaultRole")
                .withServiceRole("EMR_DefaultRole")
                .withLogUri(Constants.getS3OutputPath("logs/"));

        RunJobFlowResult runJobFlowResult = mapReduce.runJobFlow(runFlowRequest);
        String jobFlowId = runJobFlowResult.getJobFlowId();
        System.out.println("Ran job flow with id: " + jobFlowId);

    }
}
