package pagerank;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCountThreshold extends Configured implements Tool {
	private static final String THRESHOLD = "threshold";
	private static final String RANDOM = "random";
	private static final String SAW = "saw";

	private static final long MB = 1024 * 1024;

	/**
	 * Maps each line and output the filter result with data format needed
	 */
	public static class WordCountMapper extends
			Mapper<Text, Text, Text, IntWritable> {
		private double threshold;
		private double randomValue;
		private int sawFlag;
		private MemoryMXBean memoryMXBean;
		private List<String> wordList;

		@Override
		public void setup(Context context) {
			wordList = new ArrayList<String>();
			threshold = context.getConfiguration().getDouble(THRESHOLD, 512)
					* MB;
			randomValue = context.getConfiguration().getDouble(RANDOM, 1);
			sawFlag = context.getConfiguration().getInt(SAW, 0);
			if (randomValue != 1.0) {
				randomValue = Math.random();
			}
			threshold = threshold * randomValue;
			memoryMXBean = ManagementFactory.getMemoryMXBean();
		}

		@Override
		public void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			MemoryUsage memHeap = memoryMXBean.getHeapMemoryUsage();
			if (memHeap.getUsed() < threshold) {
				String line = value.toString();
				StringTokenizer tokenizer = new StringTokenizer(line);
				String str = "";
				while (tokenizer.hasMoreTokens()) {
					str = tokenizer.nextToken();
					wordList.add(str);
				}
				wordList.addAll(wordList);
			} else if (sawFlag == 1) {
				wordList.clear();
			}
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 3) {
			System.err
					.println("Usage: PrePageRank <in> <threshold> <random> <saw>");
			return 0;
		}
		Configuration conf = new Configuration();
		conf.setDouble(THRESHOLD, Double.parseDouble(args[1]));
		conf.setDouble(RANDOM, Double.parseDouble(args[2]));
		conf.setInt(SAW, Integer.parseInt(args[3]));
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "WordCount");
		job.setJarByClass(WordCountThreshold.class);
		job.setMapperClass(WordCountMapper.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setReducerClass(Reducer.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[5]));
		boolean result = job.waitForCompletion(true);
		return (result ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new Configuration(), new WordCountThreshold(), args);
	}
}
